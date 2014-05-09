package com.eps.controller;

import javax.mail.MessagingException;
import javax.servlet.http.*;

import org.springframework.stereotype.Controller;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.web.bind.annotation.ModelAttribute;
//import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;

import com.ederbase.model.*;
import com.eps.model.*;
import com.eps.template.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
@Controller
@SessionAttributes({"client"})
public class EpsController {
	
	ApplicationContext EbEpsContext = new ClassPathXmlApplicationContext("ebeps.xml");
	ApplicationContext DbEpsContext = new ClassPathXmlApplicationContext("dbeps.xml");
	private EbClient EbCl;
	private EbUserData EbUD;
	private List<EbWorkFlow> EbWF;
	private List<EbWorkFlowOfPTM> EbWFofPTM;
	private List<EbExecutiveDashboard> EbDashboard;
	private List<EbMessages> Ebmsg;
	private EbWorkFlow editWF;
	private EbWorkFlowOfPTM editPTM;
	private HttpSession session;
	private EbDatabase Ebdb;
	private EbDatabase dbDyn;
	private int lastRecord = 0;
	EpsController(){
		EbCl = null;
		EbUD = null;
		EbWF = null;
		EbWFofPTM = null;
		EbDashboard = null;
		editWF = null;
		editPTM = null;
		Ebmsg = null;
		Ebdb = new EbDatabase("dbeps");
		dbDyn = new EbDatabase("ebeps"); //check the database name
	}
	
	@RequestMapping(value="/welcome", method = RequestMethod.GET)
	public ModelAndView welcome(HttpSession session){


		if(EbUD != null){
			
			return refreshLogin(session);
		}else{
			
			ModelAndView mav = new ModelAndView("welcome");
			mav.addObject("question","null");
			return mav;
		}
	}
	
	
	@RequestMapping( params="forgotpassword", value="/login", method=RequestMethod.POST)
	public ModelAndView forgotPassword(@RequestParam(value="stEmail", required=false, defaultValue="")String stEmail) throws SQLException{
		/*
		 * Users click on Forgot Password come this routine.
		 */
		String popupMessage="";
		EbMail ebM = new EbMail(this.EbUD);
		ModelAndView mav = new ModelAndView("welcome");
		mav.addObject("question","null");
		
		if (stEmail == null || stEmail.isEmpty())
		{
			popupMessage = "No Email entered";
			mav.addObject("popupMessage", popupMessage);
			return mav;
		}
			ResultSet rsUser = this.Ebdb
				.ExecuteSql("select * from Users u, "
						+ this.dbDyn.getDbName()
						+ ".X25User xu where "
						+ "u.nmUserId=xu.RecId and stEmail = '"
						+ stEmail + "'");
		if (rsUser == null || !rsUser.next()) {
			popupMessage = "No such Email found";
			mav.addObject("popupMessage", popupMessage);
			return mav;
		}

		String stPattern = "!@#$%^&*+_.";
		String stPass = "EPPORA";
		stPass += stPattern.charAt(Math.round((float) Math.random()
				* (stPattern.length() - 1)));
		stPass += stPattern.charAt(Math.round((float) Math.random()
				* (stPattern.length() - 1)));
		stPass += stPattern.charAt(Math.round((float) Math.random()
				* (stPattern.length() - 1)));
		stPass += System.currentTimeMillis();

		this.dbDyn
				.ExecuteUpdate("update x25user set stPassword=password('"
						+ stPass
						+ "') where RecId="
						+ rsUser.getString("RecId"));

		String stContent = "Your password has been changed to \""
				+ stPass
				+ "\" temporarily. "
				+ "It is recommended that when you log back in you change your password to something that will be easy "
				+ "for you to remember and contains at least one non-alphanumeric character.";


		ebM.setProduction(1);
		ebM.setEbEmail("smtp.gmail.com", "true",
				"EPPORA Do Not Reply",
				"donotreply.eppora@gmail.com",
				"donotreply.eppora@gmail.com", "epsdonotreply");
		int iRecId=0;
		try {
			iRecId = ebM.sendMail(
					stEmail,
					rsUser.getString("FirstName") + " "
							+ rsUser.getString("LastName"),
					"Password Recovery", stContent, 1);
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			mav.addObject("popupMessage", e);
		}
		if (iRecId > 0) {
			
			popupMessage = "Password is sent to your email address.";
			mav.addObject("popupMessage", popupMessage);
		//	ebEnt.ebUd
		//			.setPopupMessage("Password is sent to your email address.");
		}
		
		return mav;
		
	}
	
	@RequestMapping(value="/login", method=RequestMethod.GET)
	public ModelAndView refreshLogin( HttpSession session){
		this.session = session;
		if(EbUD == null){
			ModelAndView mav = new ModelAndView("welcome");
			mav.addObject("question","null");
			return mav;
		}
		
		ModelAndView mav = new ModelAndView("home");
		mav.addObject("client", this.EbUD);
		mav.addObject("list", this.EbUD.getHomeList());
		return mav;
	}

	@RequestMapping( params = "login", value="/login", method=RequestMethod.POST)
	@ModelAttribute("client")
	public ModelAndView LoginProcess(@RequestParam(value="stEmail", required=false, defaultValue="")String stEmail,
			@RequestParam(value="stPwd", required=false, defaultValue="") String stPwd, HttpSession session){
		/*
		 * users click on "Login" and come to this controller.
		 */
		
		EbClientTemplate EbcTemplate = (EbClientTemplate)EbEpsContext.getBean("EbClientTemplate");
		List<EbClient> EbC = EbcTemplate.clientMapping(stEmail, stPwd);
		
		if(EbC.isEmpty()){
			ModelAndView mav = new ModelAndView("welcome");
			mav.addObject("question", "What is your favorite color?");
			return mav;
		}
		else{
			/*
			 * get the previlage.
			 */
			EbUserDataTemplate EbUDTemplate = (EbUserDataTemplate)DbEpsContext.getBean("EbUserDataTemplate");
			List<EbUserData> EbUD = EbUDTemplate.mappingUserData(EbC.get(0));
			EbUserData client = EbUD.get(0);
			this.EbUD = client;
			session.setAttribute("client", client);
			ArrayList <String> homeList = new ArrayList<String>();
			homeList.add("Work Flow");
			homeList.add("Messages");
			System.out.println(client.getStPriviledge());
			if(client.getStPriviledge().contains("Pm") || client.getStPriviledge().contains("Ppm")){
				homeList.add("Work Flow of Project Team Members");
			}
			if(client.getStPriviledge().contains("Ex")){
				homeList.add("Executive Project Portfolio Dashboard");
			}
			System.out.println(this.EbUD.getNmUserId());
			client.setHomeList(homeList);
			ModelAndView mav = new ModelAndView("home");
			mav.addObject("client", client);
			mav.addObject("list", homeList);
			mav.addObject("question", "null");
			return mav;
		}
		
	}
	
	@RequestMapping(value="/logout", method=RequestMethod.GET)
	public ModelAndView logoutProcess(HttpSession session){
		
		session.setAttribute("client", null);
		if(EbUD != null){
			EbUD = null;
		}
		if(session.getAttribute("client")!=null)
		{session.removeAttribute("client");
		/*
		 * SQL Queries to get users logout from EPS database session
		 */
		}
		
		ModelAndView mav = new ModelAndView("welcome");
		mav.addObject("question", "null");
		return mav;
	}
	
//	@RequestMapping(value="/logout", method=RequestMethod.GET)
//	public ModelAndView refreshLogout(HttpSession session){
//	
//		return new ModelAndView("welcome");
//	}
//	
	
	@RequestMapping(value="/Work Flow", method=RequestMethod.GET)
	public ModelAndView getWorkFlow(HttpSession session ){
		if(EbUD == null){
			return refreshLogin(session);
		}
		EbWorkFlowTemplate EbWFTemplate = (EbWorkFlowTemplate)DbEpsContext.getBean("EbWorkFlowTemplate");
		 EbWF = EbWFTemplate.mappingWorkFlow(EbUD, DbEpsContext);
		List <EbWorkFlow> tempEbWf =  EbWFTemplate.mappingWorkFlow(EbUD, DbEpsContext);
		System.out.println("Debug 1");
	//	session.setAttribute("client", EbUD);
		ModelAndView mav = new ModelAndView("login");
		mav.addObject("client", EbUD);
		List<String> columns = new ArrayList<String>();
		columns.add("Act");
		columns.add("PN");
		columns.add("Task");
		columns.add("Status");
		
		mav.addObject("columns", columns);
		mav.addObject("pageName","Work Flow");
		if(!EbWF.isEmpty()){
			System.out.println("Debug2");
		mav.addObject("wflist", tempEbWf);
		lastRecord = this.EbWF.size();
		System.out.println("Debug3");
		System.out.println(EbUD.getFirstName()+":"+EbUD.getNmUserId());
		System.out.println(EbWF.get(0).getProjectName());
	
		EbWF = tempEbWf;
		}else{
			System.out.println("Null!!!!!!!!!!!!!!!!!");
		
		
		}
		

		mav.addObject("lastrecord", lastRecord);
		return mav;
		
		
	}
	@RequestMapping(value="/Messages", method=RequestMethod.GET)
	public ModelAndView getMessage(HttpSession session){

		if(EbUD == null){
			return refreshLogin(session);
		}
		
		EbMessagesTemplate EbMsgTemplate = (EbMessagesTemplate)EbEpsContext.getBean("EbMessagesTemplate");
		List<EbMessages> EbMsg = EbMsgTemplate.mappingMessages(EbUD);
		this.Ebmsg = EbMsg;
		ModelAndView mav = new ModelAndView("login");
		mav.addObject("client",EbUD);
		List<String> columns = new ArrayList<String>();
		columns.add("Act");
		columns.add("PN");
		columns.add("Message");
		columns.add("Desc");
		mav.addObject("columns",columns);

		mav.addObject("pageName","Messages");
		if(!EbMsg.isEmpty()){
			System.out.println(EbMsg.get(0));
			mav.addObject("list", EbMsg);
			lastRecord = EbMsg.size();
			mav.addObject("lastrecord", lastRecord);
		}else{
			
			System.out.println("message empty");
		}

		mav.addObject("lastrecord", lastRecord);
		return mav;
	}
	
	@RequestMapping(value="/Work Flow of Project Team Members", method=RequestMethod.GET)
	public ModelAndView getWFofPTM(){
		
		if(EbUD == null){
			return refreshLogin(session);
		}
		
		EbWorkFlowOfPTMTemplate EbWFofPTMTemplate = (EbWorkFlowOfPTMTemplate)DbEpsContext.getBean("EbWorkFlowOfPTMTemplate");

		 EbWFofPTM = EbWFofPTMTemplate.mappingWorkFlowOfPTM(EbUD, DbEpsContext);

		System.out.println("PTM 3");
		ModelAndView mav = new ModelAndView("login");
		mav.addObject("pageName","Work Flow of Project Team Members");
		mav.addObject("client",EbUD);
		List<String> columns = new ArrayList<String>();
		//add columns here
		columns.add("Act");
		columns.add("PN");
		columns.add("Task");
		columns.add("Status");
		columns.add("Info");
		mav.addObject("columns",columns);
		if(!EbWFofPTM.isEmpty() && EbWFofPTM.get(0).getTaskId()!=0){
			mav.addObject("list", EbWFofPTM);
			lastRecord = EbWFofPTM.size();
		}

		mav.addObject("lastrecord", lastRecord);
		return mav;
	}
	
	
	@RequestMapping(value="/Executive Project Portfolio Dashboard", method=RequestMethod.GET)
	public ModelAndView getDashboard(){
		
		if(EbUD == null){
			return refreshLogin(session);
		}
		
		EbExecutiveDashboardTemplate EbDashboardTemplate = (EbExecutiveDashboardTemplate)DbEpsContext.getBean("EbExecutiveDashboardTemplate");
		
		EbDashboard = EbDashboardTemplate.mappingDashboard(EbUD);
		ModelAndView mav = new ModelAndView("login");
		mav.addObject("pageName","Executive Project Portfolio Dashboard");
		mav.addObject("client",EbUD);
		List <String>columns = new ArrayList<String>();
		//columns.add("Action");
		columns.add("Project Name");
		columns.add("SPI");
		columns.add("CPI");
		columns.add("Info");
		mav.addObject("columns",columns);
		
		if(!EbDashboard.isEmpty()){
			mav.addObject("list", EbDashboard);
			lastRecord = EbDashboard.size();
		}

		mav.addObject("lastrecord", lastRecord);
		return mav;
	}
	
	private String []command ;
	
	@RequestMapping(value="/edit", method=RequestMethod.GET)
	public String editEntry( @RequestParam(value="command", required=true)String command){
		
		
		
		this.command = command.split("-");
		

		return "redirect:/edit2.do";
		
	}
	

//	@RequestMapping(value="/workflowOfPTMedit.do", method=RequestMethod.POST)
//	public ModelAndView workFlowOfPTMEdit( @RequestParam("expendedHoursToday") String stEHT, @RequestParam(value="incrementalProgress",required=false, defaultValue="") String stProg, @RequestParam("status") String status, @RequestParam(value="message",required=false, defaultValue="") String message) throws SQLException{
//		{
//			double dExpendedHoursToday;
//			double dIncrementalProgress=0;
//			EbWorkFlowOfPTM wfPtm ;
//			
//		}
	
	@RequestMapping(params="Reject",value="/workflowofPtmUpdate", method=RequestMethod.POST)
	public ModelAndView ptmReject( @RequestParam(value="expendedHoursToday", required=false, defaultValue="") String stEHT, @RequestParam(value="incrementalProgress", required=false, defaultValue="") String stProg, @RequestParam(value="status", required=false, defaultValue="") String status, @RequestParam(value="message", required=false, defaultValue="") String message){
		
		if(EbUD == null){
			return refreshLogin(session);
		}
		
		double dExpendedHoursToday = 0;
		double dIncrementalProgress=0;
		EbWorkFlowOfPTM ptmTemp = this.editPTM;
		
		try{
			dExpendedHoursToday = Double.parseDouble(stEHT);
			if(!stProg.contains("")){
				dIncrementalProgress = Double.parseDouble(stProg);
			}
		}catch(Exception e){
				System.out.println("??");
		
			}
		
		ptmTemp.setExpendedHoursToday(stEHT);
		ptmTemp.setPgIncrementalProgress(stProg);
		ptmTemp.getEbPg().setPlannedQuantity(stProg);
		ptmTemp.setStatus(status);
		ptmTemp.setMessage(message);
		

		SimpleDateFormat fmt = new SimpleDateFormat("MM/dd/yyyy");
		Calendar c = Calendar.getInstance();
		c.add(Calendar.DATE, 10); // number of days to add
		String dateEnd = fmt.format(c.getTime());
		
		String stWhere = " WHERE nmProjectId=" + ptmTemp.getPrjId()
				+ " AND nmBaseline=" + ptmTemp.getBaseline() + " AND RecId=" + ptmTemp.getTaskId();
		String stId = this.Ebdb
				.ExecuteSql1("SELECT SchId From Schedule" + stWhere);
		
		ResultSet rsP = this.Ebdb
				.ExecuteSql("SELECT * FROM Projects WHERE RecId=" + ptmTemp.getPrjId());
		try {
			rsP.next();
		
		int iCount = 0;
		
		double nmTodaySpent = this.Ebdb
				.ExecuteSql1nm("SELECT SUM(nmActualApproved)"
						+ " FROM teb_allocateprj WHERE nmUserId="
						+ this.Ebdb.fmtDbString(String.valueOf(EbUD.getNmUserId()))
						+ " AND dtDatePrj=curdate()");
		double nmTodayExpended = this.Ebdb
				.ExecuteSql1nm("SELECT SUM(nmActual)"
						+ " FROM teb_workflow WHERE nmUserId="
						+ this.Ebdb.fmtDbString(String.valueOf(EbUD.getNmUserId()))
						+ " AND dtAllocate=curdate()");
		int iMaxHoursPerDay =  10; //this.rsMyDiv.getInt("MaxWorkHoursPerDay");
		if ((nmTodaySpent + nmTodayExpended + dExpendedHoursToday) > iMaxHoursPerDay) {
				//error
		}
		
		String msg = "Task " + stId + ": Estimated \""
				+ptmTemp.getEstimatedHours() + "\", Expended \""
				+ ptmTemp.getExpendedHoursToday() + "\", Status \"" + this.Ebdb.fmtDbString(ptmTemp.getStatus()) + "\"";
		makeMessage(rsP.getString("ProjectName"), String.valueOf(this.EbUD.getNmUserId()),
				"A Workflow Task update is rejected", this.Ebdb.fmtDbString(msg), dateEnd,
				"./?stAction=projects&t=12&do=xls&pk=" + this.Ebdb.fmtDbString(ptmTemp.getPrjId())
						+ "&parent=&child=21&a=editfull&r=" + stId);
		
		
		this.Ebdb.ExecuteUpdate("DELETE FROM teb_workflow"
				+ stWhere);

		// update progress allocate
		String stProgressIds = this.editPTM.getEbPg().getProgressId();
		
		if (stProgressIds != null && !stProgressIds.equalsIgnoreCase("")) {
				String stPId = stProgressIds;
				this.Ebdb
						.ExecuteUpdate("DELETE FROM schedule_progress_allocate WHERE nmProgressId="
								+ stPId + " AND nmUserId=" + String.valueOf(this.EbUD.getNmUserId()));
			
		}
		
		String stPrjId = ptmTemp.getPrjId();
		String stBaseline = ptmTemp.getBaseline();
		String stSchId = String.valueOf(ptmTemp.getTaskId());
		String stUserId = String.valueOf(this.EbUD.getNmUserId());
		
		this.Ebdb.ExecuteUpdate("DELETE FROM teb_workflow"
				+ stWhere);
		this.Ebdb.ExecuteUpdate("UPDATE Schedule set SchStatus="
				+ this.Ebdb.fmtDbString(status)
				+ ",dtLastUpdated=now(),nmLastUpdatedUser=" + stUserId
				+ stWhere);
		EpsXlsProject epsXlsProject = new EpsXlsProject();
		epsXlsProject.recalcSchedule(stSchId, stPrjId, Integer.parseInt(stBaseline));
		
		epsXlsProject.setEpsXlsProject(this.EbUD);
		epsXlsProject.nmBaseline = Integer.parseInt(stBaseline);
		epsXlsProject.stPk = stPrjId;
		epsXlsProject.processScheduleRequirementCost(Integer
				.parseInt(stSchId));
		epsXlsProject
				.updateParentEfforts(
						stPrjId,
						epsXlsProject.nmBaseline,
						this.Ebdb
								.ExecuteSql1n("SELECT SchParentRecId FROM Schedule "
										+ stWhere), true);
		epsXlsProject.processCriticalPath();
		epsXlsProject.updateProjectInfo();
		
		
		makeTask(15, ptmTemp.getSchTitle());
		// TODO: dunghm Milestone messages
		sendMilestoneMessage(stPrjId, Integer.parseInt(stBaseline),
				stSchId);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return getWFofPTM();
	}
	
	@RequestMapping(params="Cancel", value="/workflowedit", method=RequestMethod.POST)
	public ModelAndView cancel(){
		
		if(EbUD == null){
			return refreshLogin(session);
		}
		
		return getWorkFlow(session);
	}
	
	@RequestMapping(params="Cancel", value="/workflowofPtmUpdate", method=RequestMethod.POST)
	public ModelAndView cancelPtm(){
		
		if(EbUD == null){
			return refreshLogin(session);
		}
		
		return getWFofPTM();
	}
	

	@RequestMapping(params="Update",value="/workflowofPtmUpdate", method=RequestMethod.POST)
	public ModelAndView ptmUpdate( @RequestParam(value="expendedHoursToday", required=false, defaultValue="") String stEHT, @RequestParam(value="incrementalProgress", required=false, defaultValue="") String stProg, @RequestParam("status") String status, @RequestParam(value="message", required=false, defaultValue="") String message){
	
		if(EbUD == null){
			return refreshLogin(session);
		}
		
		double dExpendedHours = 0;
		double dIncrementalProgress=0;
		double addedUp = 0;
		EbWorkFlowOfPTM ptmTemp = this.editPTM;
		
		try{
			dExpendedHours = Double.parseDouble(stEHT);
			if(ptmTemp.getPgAccomplishedToDate()!= null && !ptmTemp.getPgAccomplishedToDate().equalsIgnoreCase("") )
			addedUp = Double.parseDouble(ptmTemp.getPgAccomplishedToDate());
			if(!stProg.contains("")){
				dIncrementalProgress = Double.parseDouble(stProg);
				addedUp += Double.parseDouble(stProg);
			}
		}catch(Exception e){
				System.out.println("??");
		
			}
		
		String stPrjId = ptmTemp.getPrjId();
		String stBaseline = ptmTemp.getBaseline();
		String stSchId = String.valueOf(ptmTemp.getTaskId());
		String stUserId = String.valueOf(this.EbUD.getNmUserId());
		
		ptmTemp.setExpendedHoursToday(stEHT);
		ptmTemp.setPgIncrementalProgress(String.valueOf(dIncrementalProgress));
		ptmTemp.getEbPg().setPlannedQuantity(String.valueOf(dIncrementalProgress));
		ptmTemp.setStatus(status);
		ptmTemp.setMessage(message);
		

		SimpleDateFormat fmt = new SimpleDateFormat("MM/dd/yyyy");
		Calendar c = Calendar.getInstance();
		c.add(Calendar.DATE, 10); // number of days to add
		String dateEnd = fmt.format(c.getTime());
		String stAllocateWhere = " WHERE nmPrjId=" + stPrjId
				+ " AND nmBaseline=" + stBaseline + " AND nmTaskID="
				+ stSchId;
		String stWhere = " WHERE nmProjectId=" + ptmTemp.getPrjId()
				+ " AND nmBaseline=" + ptmTemp.getBaseline() + " AND RecId=" + ptmTemp.getTaskId();
		String stId = this.Ebdb
				.ExecuteSql1("SELECT SchId From Schedule" + stWhere);
		
		ResultSet rsP = this.Ebdb
				.ExecuteSql("SELECT * FROM Projects WHERE RecId=" + ptmTemp.getPrjId());
		try {
			rsP.next();
		
		int iCount = 0;
		
		double nmTodaySpent = this.Ebdb
				.ExecuteSql1nm("SELECT SUM(nmActualApproved)"
						+ " FROM teb_allocateprj WHERE nmUserId="
						+ this.Ebdb.fmtDbString(String.valueOf(EbUD.getNmUserId()))
						+ " AND dtDatePrj=curdate()");
		double nmTodayExpended = this.Ebdb
				.ExecuteSql1nm("SELECT SUM(nmActual)"
						+ " FROM teb_workflow WHERE nmUserId="
						+ this.Ebdb.fmtDbString(String.valueOf(EbUD.getNmUserId()))
						+ " AND dtAllocate=curdate()");
		int iMaxHoursPerDay =  10; //this.rsMyDiv.getInt("MaxWorkHoursPerDay");
		if ((nmTodaySpent + nmTodayExpended + dExpendedHours) > iMaxHoursPerDay) {
				//error
		}
		
		String stProgressIds = this.editPTM.getEbPg().getProgressId();
		
		if (stProgressIds != null && !stProgressIds.equalsIgnoreCase("")) {
				this.Ebdb
						.ExecuteUpdate("DELETE FROM schedule_progress_allocate WHERE nmProgressId="
								+ ptmTemp.getEbPg().getProgressId() + " AND nmUserId=" + String.valueOf(this.EbUD.getNmUserId()));

				if (addedUp > 0)
					this.Ebdb
							.ExecuteUpdate("UPDATE schedule_progress SET nmAccomplished=nmAccomplished+"
									+ this.Ebdb.fmtDbString(String.valueOf(addedUp))
									+ " WHERE RecId="
									+ ptmTemp.getEbPg().getProgressId());
			}
		

		ResultSet rs = this.Ebdb
				.ExecuteSql("SELECT * FROM teb_allocateprj"
						+ stAllocateWhere + " AND nmUserId=" + stUserId);
		if (rs.next()) {
			if (this.Ebdb
					.ExecuteSql1n("SELECT COUNT(*) FROM teb_allocateprj"
							+ stAllocateWhere
							+ " AND dtDatePrj=curdate() AND nmUserId="
							+ stUserId) > 0) {
				this.Ebdb
						.ExecuteUpdate("UPDATE teb_allocateprj set nmActual="
								+ String.valueOf(dExpendedHours)
								+ ", nmActualApproved="
								+ String.valueOf(dExpendedHours)
								+ stAllocateWhere
								+ " and dtDatePrj=curdate() AND nmUserId="
								+ stUserId);
			} else {
				this.Ebdb
						.ExecuteUpdate("REPLACE INTO teb_allocateprj VALUE (curdate(), "
								+ stUserId
								+ ","
								+ rs.getString("nmLC")
								+ ","
								+ rs.getString("nmPrjId")
								+ ","
								+ rs.getString("nmBaseline")
								+ ","
								+ rs.getString("nmTaskId")
								+ ",0,"
								+ dExpendedHours
								+ ","
								+ dExpendedHours
								+ ")");
			}
		}
		
		
		this.Ebdb.ExecuteUpdate("DELETE FROM teb_workflow"
				+ stWhere);
		this.Ebdb.ExecuteUpdate("UPDATE Schedule set SchStatus="
				+ this.Ebdb.fmtDbString(status)
				+ ",dtLastUpdated=now(),nmLastUpdatedUser=" + stUserId
				+ stWhere);
		EpsXlsProject epsXlsProject = new EpsXlsProject();
		epsXlsProject.recalcSchedule(stSchId, stPrjId, Integer.parseInt(stBaseline));
		
		epsXlsProject.setEpsXlsProject(this.EbUD);
		epsXlsProject.nmBaseline = Integer.parseInt(stBaseline);
		epsXlsProject.stPk = stPrjId;
		epsXlsProject.processScheduleRequirementCost(Integer
				.parseInt(stSchId));
		epsXlsProject
				.updateParentEfforts(
						stPrjId,
						epsXlsProject.nmBaseline,
						this.Ebdb
								.ExecuteSql1n("SELECT SchParentRecId FROM Schedule "
										+ stWhere), true);
		epsXlsProject.processCriticalPath();
		epsXlsProject.updateProjectInfo();
		
		
		makeTask(15, ptmTemp.getSchTitle());
		// TODO: dunghm Milestone messages
		sendMilestoneMessage(stPrjId, Integer.parseInt(stBaseline),
				stSchId);
		
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return getWFofPTM();
	
	}
	
	@RequestMapping(params="send", value="/workflowedit.do", method=RequestMethod.POST)
	public ModelAndView workFlowEdit( @RequestParam("expendedHoursToday") String stEHT, @RequestParam("incrementalProgress") String stProg, @RequestParam("status") String status) throws SQLException{
		
		if(EbUD == null){
			return refreshLogin(session);
		}
		
		double dExpendedHoursToday;
		double dIncrementalProgress=0;
		EbWorkFlow wfTemp = this.editWF; //This is the entry the user just clicked
		try{
		dExpendedHoursToday = Double.parseDouble(stEHT);
		if(!stProg.contentEquals(""))
		{dIncrementalProgress = Double.parseDouble(stProg);
		//It gets ingremental Progress if there is a progress available
			}
		}catch(Exception e){
			System.out.println("expended hours must be a number");
			return editEntry2();
		}
		
		
		wfTemp.setExpendedHoursToday(stEHT);
		wfTemp.setPgIncrementalProgress(stProg);
		wfTemp.getEbPg().setPlannedQuantity(stProg);
		wfTemp.setStatus(status);

//		try{
		SimpleDateFormat fmt = new SimpleDateFormat("MM/dd/yyyy");
		Calendar c = Calendar.getInstance();
		c.add(Calendar.DATE, 10); // number of days to add
		String dateEnd = fmt.format(c.getTime());
		
		/*
		 *  Work Flow class has following fields to contain proper database information in it.
		 *  String : prjId, baseline, schId, projectName, startDate, expectedHoursToDate; estimatedHours, expendedHoursToday
		 *  ,status, description, message, pgDescription(progress), pgPlannedQuantity(progress), pgIncrementalProgress(progress), PgAccomplishedToDate(progress),
		 *  Int taskId
		 *  those fields are filled when the user first click on "work flow"menu on their home page,
		 *    via Spring template.
		 */
		
		/*
		 * EbUD is EbUserData class which contains e-mails, recId, username, etc.
		 * Things that are related to users.
		 * Except for the parameters, code is just same as eps project.
		 */
		String stWhere = " WHERE nmProjectId=" + this.Ebdb.fmtDbString(wfTemp.getPrjId())
				+ " AND nmBaseline=" + this.Ebdb.fmtDbString(wfTemp.getBaseline()) + " AND RecId=" + this.Ebdb.fmtDbString(wfTemp.getSchId());
		String stId = this.Ebdb
				.ExecuteSql1("SELECT SchId From Schedule" + stWhere);
		
		ResultSet rsP = this.Ebdb
				.ExecuteSql("SELECT * FROM Projects WHERE RecId=" + this.Ebdb.fmtDbString(wfTemp.getPrjId()));
		rsP.next();
		int iCount = 0;

			String stSql = "";
			

			String stPrjId = wfTemp.getPrjId();
			String stBaseline = wfTemp.getBaseline();
			String stSchId = String.valueOf(wfTemp.getTaskId());
			String stUserId = String.valueOf(this.EbUD.getNmUserId());
			try{
		
			// update progress allocate
//	String stProgressId = wfTemp.getEbPg().getProgressId();
//			if (stProgressId != null) {
//				
//				stSql = "REPLACE INTO schedule_progress_allocate (nmProgressId,nmUserId,dtUpdate,nmActual) VALUES ";
//
//					String stPId = stProgressId;
//					double dUpdateQty = 0;
//					try {
//						dUpdateQty = Double
//								.parseDouble(wfTemp.getPgIncrementalProgress());
//					} catch (Exception e) {
//				
//					}
//					if (dUpdateQty < 0)
//					
//					if (iCount > 0)
//						stSql += ",";
//					stSql += "(" + stPId + ","
//							+ String.valueOf(EbUD.getNmUserId()) + ",curdate(),"
//							+ dUpdateQty + ")";
//					iCount++;
//				}
//		
//				if (iCount > 0) {
//					this.Ebdb.ExecuteUpdate(stSql);
//				}
			

			double nmTodaySpent = this.Ebdb
					.ExecuteSql1nm("SELECT SUM(nmActualApproved)"
							+ " FROM teb_allocateprj WHERE nmUserId="
							+ String.valueOf(EbUD.getNmUserId())
							+ " AND dtDatePrj=curdate()");
			double nmTodayExpended = this.Ebdb
					.ExecuteSql1nm("SELECT SUM(nmActual)"
							+ " FROM teb_workflow WHERE nmUserId="
							+ String.valueOf(EbUD.getNmUserId())
							+ " AND dtAllocate=curdate()");
			int iMaxHoursPerDay =  10; //this.rsMyDiv.getInt("MaxWorkHoursPerDay");
			if ((nmTodaySpent + nmTodayExpended + dExpendedHoursToday) > iMaxHoursPerDay) {
					//error
			}
			
			this.Ebdb
					.ExecuteUpdate("REPLACE INTO teb_workflow VALUE("
							+  this.Ebdb.fmtDbString(wfTemp.getPrjId()) + "," +  this.Ebdb.fmtDbString(wfTemp.getBaseline()) + "," +  this.Ebdb.fmtDbString(wfTemp.getSchId())
							+ "," +  this.Ebdb.fmtDbString(wfTemp.getEstimatedHours()) + ","
							+  this.Ebdb.fmtDbString(wfTemp.getExpendedHoursToday()) + "," + 0 + ","
							+  this.Ebdb.fmtDbString(String.valueOf(EbUD.getNmUserId())) + ","
							+ this.Ebdb.fmtDbString(wfTemp.getStatus())
							+ ",curdate(),0)");
			String msg = "Task " + stId + ": Estimated \""
					+ wfTemp.getEstimatedHours() + "\", Expended \""
					+ wfTemp.getExpendedHoursToday() + "\", Status \"" + this.Ebdb.fmtDbString(wfTemp.getStatus()) + "\"";
			makeMessage(
					rsP.getString("ProjectName"),
					rsP.getString("ProjectManagerAssignment")
							+ ","
							+ rsP.getString("ProjectPortfolioManagerAssignment"),
					"A Workflow Task is updated", msg, dateEnd,
					"./?stAction=projects&t=12&do=xls&pk=" + wfTemp.getSchId()
							+ "&parent=&child=21&a=editfull&r=" + stId);
			
			}catch(Exception e){
				
				ModelAndView mav = new ModelAndView("home");
				mav.addObject("client", this.EbUD);
				mav.addObject("list", this.EbUD.getHomeList());
				return mav;
				
			}
			
		//	this.Ebdb.getConnect().getEbConn().commit();
			return getWorkFlow(session);
//		}catch(Exception e){
//			
//		}
		// end nmWfStatus == 0
		
		

//		ModelAndView mav = new ModelAndView("workFlowInfo");
//		mav.addObject("client",EbUD);
//		mav.addObject("wf",wfTemp);
//		this.editWF = null;
//		//update database :)
//		
//		return mav;
			
			
	}
	
	
	
	@RequestMapping(value="/edit2.do", method=RequestMethod.GET)
	public ModelAndView editEntry2(){
		
		if(EbUD == null){
			return refreshLogin(session);
		}
		
		ModelAndView mav = new ModelAndView("home");
		
		if(command[0].equalsIgnoreCase("Work Flow")){
			// add work flow object here
			// find taskId = command[1]
			// get row
			// return
			
			mav = new ModelAndView("workFlowEdit");
			mav.addObject("pageName", "Work Flow Update");
			for(int i=0; i<EbWF.size(); i++){
				if(Integer.parseInt(command[1]) == EbWF.get(i).getTaskId()){
					this.editWF = EbWF.get(i);
					mav.addObject("wf", EbWF.get(i));
				
				}
			}
			
			return mav;
			
		}else if(command[0].equalsIgnoreCase("Work Flow of Project Team Members")){
			// add work flow of project team members object here

			mav = new ModelAndView("workflowOfPTMEdit");
			mav.addObject("pageName", "Work Flow of Project Team Member Update");
			for(int i=0; i<EbWFofPTM.size(); i++){
				if(Integer.parseInt(command[1]) == EbWFofPTM.get(i).getTaskId()){
					this.editPTM = EbWFofPTM.get(i);
					mav.addObject("wfptm", EbWFofPTM.get(i));
				
				}
			}
			
			return mav;
		}else if(command[0].equalsIgnoreCase("Messages")){
			
			
			

			dbDyn.ExecuteUpdate("DELETE FROM x25task where RecId="+command[1]);
			return getMessage(session);
			
			
		}
		
		return refreshLogin(session);
	}
	
	@RequestMapping(value="/info.do", method=RequestMethod.GET)
	public String infoEntry( @RequestParam(value="command", required=true)String command){
		
		
		
		this.command = command.split("-");
		
		ModelAndView mav = new ModelAndView("info");
		mav.addObject("redirectUrl", "info2.do");
		return "redirect:/info2.do";
		
	}
	
	@RequestMapping(value="/info2.do", method=RequestMethod.GET)
	public ModelAndView infoEntry2(){
		
		if(EbUD == null){
			return new ModelAndView("welcome");
		}

		ModelAndView mav = null;
		
		if(command[0].equalsIgnoreCase("Work Flow")){
			// add work flow object here
			// find taskId = command[1]
			// get row
			// return
			mav = new ModelAndView("workFlowInfo");
			
	
			for(int i=0; i<EbWF.size(); i++){
				if(Integer.parseInt(command[1]) == EbWF.get(i).getTaskId()){
					mav.addObject("wf", EbWF.get(i));
				
				}
			}

			mav.addObject("client", EbUD);
			return mav;
			
		}else if(command[0].equalsIgnoreCase("Work Flow of Project Team Members")){
			// add work flow of project team members object here
			mav = new ModelAndView("workFlowOfPTMInfo");
			

			for(int i=0; i<EbWFofPTM.size(); i++){
				if(Integer.parseInt(command[1]) == EbWFofPTM.get(i).getTaskId()){
					mav.addObject("wfptm", EbWFofPTM.get(i));
				
				}
			}

			mav.addObject("client", EbUD);
			return mav;
		}else if(command[0].equalsIgnoreCase("Executive Project Portfolio Dashboard")){
			mav = new ModelAndView("dashboard");
			
			for(int i=0; i<EbDashboard.size(); i++){
				if(command[1].equalsIgnoreCase(EbDashboard.get(i).getProjectName())){
					mav.addObject("exdashboard", EbDashboard.get(i));
				
				}
			}
			

			mav.addObject("client", EbUD);
			return mav;
		}

		//if mav != null

		return mav;
	}
	
	public String makeMessage(String projectName, String stUsers,
			String stTask, String stTaskDetail, String dtEnd, String stLink) {
		String stReturn = "<br>Workflow Task: [" + stTask + "] for: ";
		String stSql = "";
		String[] users = stUsers.trim().split(",");
		Set<String> us = new HashSet<String>();
		if (stLink == null)
			stLink = "";
		if ("All".equals(projectName))
			projectName = "";
		for (String u : users) {
			if (u == null || u.trim().equals("") || u.trim().equals("null"))
				continue;
			us.add(u);
		}
		if (us.size() == 0)
			return null;

		String[] aV = us.toArray(new String[us.size()]);

		int iU = 0;
		try {
			int iSchId = this.Ebdb
					.ExecuteSql1n("select max(RecId) from X25Task where stTitle="
							+ this.Ebdb.fmtDbString(stTask)
							+ " and stDescription="
							+ this.Ebdb.fmtDbString(projectName
									+ " - " + stTaskDetail));
			if (iSchId <= 0) {
				iSchId = this.Ebdb
						.ExecuteSql1n("select max(RecId) from X25Task");
				iSchId++;
				stSql = "INSERT INTO X25Task "
						+ "(RecId,nmTaskType,nmTaskFlag,dtStart,dtTargetEnd,stTitle,stDescription,stURL)VALUES("
						+ iSchId
						+ ",1,2,now(),"
						+ this.Ebdb.fmtDbString(fmtDateToDb(dtEnd))
						+ ","
						+ this.Ebdb.fmtDbString(stTask)
						+ ","
						+ this.Ebdb.fmtDbString(projectName
								+ " - " + stTaskDetail) + ","
						+ this.Ebdb.fmtDbString(stLink) + ")";
				this.Ebdb.ExecuteUpdate(stSql);
			} else {
				this.Ebdb
						.ExecuteUpdate("update X25Task set nmTaskFlag=2, dtStart=now(),dtTargetEnd="
								+ Ebdb
										.fmtDbString(fmtDateToDb(dtEnd))
								+ ",stURL="
								+ this.Ebdb.fmtDbString(stLink)
								+ " where RecId=" + iSchId);
			}

			this.Ebdb
					.ExecuteUpdate("delete from X25RefTask where nmTaskId="
							+ iSchId + " and nmRefType=42 ");
			for (iU = 0; iU < aV.length; iU++) {
				if (aV[iU].trim().length() > 0) {
					stSql = "INSERT IGNORE INTO X25RefTask (nmTaskId,nmRefType,nmRefId,nmTaskFlag,dtAssignStart) VALUES("
							+ iSchId + ",42," + aV[iU] + ",2,now())";
					this.Ebdb.ExecuteUpdate(stSql);
				}
			}
		} catch (Exception e) {
	
		}
		stReturn += iU + " users";
		return stReturn;
	}

	public String fmtDateToDb(String stIn) {
		String stDate = stIn;
		if ((stIn != null) && (stIn.trim().length() > 0)) {
			String[] aV = stIn.trim().split("/");
			if ((aV != null) && (aV.length >= 3))
				stDate = aV[2] + "-" + aV[0] + "-" + aV[1];
		} else {
			stDate = "1900-01-01";
		}
		return stDate;
	}

	public String fmtDateToDb(Date dtIn) {
		SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
		String stDate;
		if (dtIn != null) {
			stDate = fmt.format(dtIn);
		} else {
			stDate = "1900-01-01";
		}
		return stDate;
	}

	public String fmtDateFromDb(String stIn) {
		String stDate = "";
		if ((stIn != null) && (stIn.trim().length() > 0)) {
			if (!stIn.equals("1900-01-01")) {
				String[] aV1 = stIn.trim().split(" ");
				String[] aV = aV1[0].trim().split("-");
				if ((aV != null) && (aV.length >= 3))
					stDate = aV[1] + "/" + aV[2] + "/" + aV[0];
			}
		}
		return stDate;
	}

	public String fmtDateTimeFromDb(String stIn) {
		String stDate = "";
		if ((stIn != null) && (stIn.trim().length() > 0)) {
			if (!stIn.equals("1900-01-01")) {
				String[] aV1 = stIn.trim().split(" ");
				String[] aV = aV1[0].trim().split("-");
				if ((aV != null) && (aV.length >= 3))
					stDate = aV[1] + "/" + aV[2] + "/" + aV[0];
				if (aV1.length > 1) {
					String[] aV2 = aV1[1].trim().split(":");
					stDate = stDate + " " + aV2[0] + ":" + aV2[1] + ":"
							+ aV2[2].split("\\.")[0];
				}
			}
		}
		return stDate;
	}
/* Controller for testing
	public ModelAndView LogInProcess(
			@RequestParam(value="stEmail", required=false, defaultValue="") String stEmail,@RequestParam(value="stPwd", required=false, defaultValue="") String stPwd, HttpSession session) {
		
		
		System.out.println("get To Login!!");
	
		if(stEmail.equalsIgnoreCase("") || stPwd.equalsIgnoreCase("")){
			
		
			return new ModelAndView("index2");
		}
		
		@SuppressWarnings("resource")
		ApplicationContext EbEpsContext = new ClassPathXmlApplicationContext("ebeps.xml");
		EbClientTemplate EbcTemplate = (EbClientTemplate)EbEpsContext.getBean("EbClientTemplate");
		List<EbClient> EbC = EbcTemplate.clientMapping(stEmail, stPwd);

		System.out.println(stEmail+","+stPwd);
		ModelAndView mav = null;
		
		if(!EbC.isEmpty()){
			// login
			@SuppressWarnings("resource")
			ApplicationContext DbEpsContext = new ClassPathXmlApplicationContext("dbeps.xml");
			EbUserDataTemplate EbUDTemplate = (EbUserDataTemplate)DbEpsContext.getBean("EbUserDataTemplate");
			List<EbUserData> EbUD = EbUDTemplate.mappingUserData(EbC.get(0));
			if(!EbUD.isEmpty()){
				EbUserData EbUD1 = EbUD.get(0);
			System.out.println("Debug 0");
			EbWorkFlowTemplate EbWFTemplate = (EbWorkFlowTemplate)DbEpsContext.getBean("EbWorkFlowTemplate");
			List<EbWorkFlow> EbWF = EbWFTemplate.mappingWorkFlow(EbUD.get(0));
			System.out.println("Debug 1");
			session.setAttribute("client", EbUD1);
			mav = new ModelAndView("login");
			mav.addObject("client", EbUD1);
			return mav;		
			if(!EbWF.isEmpty()){
				System.out.println("Debug2");
			mav.addObject("client", (Object)EbWF);
			System.out.println("Debug3");
			System.out.println(EbWF.get(0).getProjectName());
			
			}else{
				System.out.println("Null!!!!!!!!!!!!!!!!!");
			}
			}
		}else{
			// login with user question
			System.out.println("Illegal Login");
			mav = new ModelAndView("greetme");
			
		}
		return mav;
		// return mav;
	}
*/	

	public String makeTask(int iTriggerId, String stTaskDetail) {
		String stTitle = "";
		String stReturn = "";
		String stSql = "";
		int iU = 0;
		try {
			ResultSet rsTrigger = this.Ebdb
					.ExecuteSql("select * from Triggers" + " where RecId="
							+ iTriggerId);
			rsTrigger.absolute(1);
			stTitle = rsTrigger.getString("TriggerName");
			stReturn = "<h1>" + stTitle + "</h1>";
			if (rsTrigger.getString("TriggerEvent").equals("Enabled")
					&& rsTrigger.getString("ContactList").length() > 0) {
				int iTaskId = this.dbDyn
						.ExecuteSql1n("select max(RecId) from X25Task"
								+ " where stTitle="
								+ this.dbDyn.fmtDbString(stTitle)
								+ " and stDescription = "
								+ this.dbDyn
										.fmtDbString(stTaskDetail)
								+ " and nmTaskFlag="
								+ rsTrigger.getString("nmTasktype"));
				if (iTaskId <= 0) {
					iTaskId = this.dbDyn
							.ExecuteSql1n("select max(RecId) from X25Task");
					iTaskId++;
					stSql = "INSERT INTO X25Task "
							+ "(RecId,nmMasterTaskId,nmTaskType,nmTaskFlag,dtStart,dtTargetEnd,stTitle,stDescription,nmUserCreated)VALUES("
							+ iTaskId + "," + iTriggerId + ",1,"
							+ rsTrigger.getString("nmTasktype")
							+ ",now(),DATE_ADD(now(),INTERVAL 10 DAY),"
							+ this.dbDyn.fmtDbString(stTitle)
							+ ","
							+ this.dbDyn.fmtDbString(stTaskDetail)
							+ "," + String.valueOf(this.EbUD.getNmUserId()) + ")";
					this.dbDyn.ExecuteUpdate(stSql);
				} else {
					this.dbDyn
							.ExecuteUpdate("update X25Task set dtStart=now(),"
									+ "stDescription="
									+ this.dbDyn
											.fmtDbString(stTaskDetail)
									+ " where RecId=" + iTaskId);
				}
				if (rsTrigger.getString("Communication").equals("Yes")) {
					// Handle EMAIL here
					EbMail ebM = new EbMail(this.EbUD);
					ebM.setEbEmail("smtp.myinfo.com", "false",
							"EPPORA Do Not Reply", "donotreply@eppora.com",
							"donotreply@eppora.com", "eppora123");
					ebM.setProduction(1);
					stSql = "select * from X25User where RecId in ("
							+ rsTrigger.getString("ContactList") + ")";
					ResultSet rsU = this.dbDyn.ExecuteSql(stSql);
					rsU.last();
					int iMaxUser = rsU.getRow();
					for (int iUser = 1; iUser <= iMaxUser; iUser++) {
						rsU.absolute(iUser);
						int nmCommId = ebM.sendMail(rsU, "-200", stTitle,
								stTaskDetail);
					}
					rsU.close();
				}
				String[] aV = rsTrigger.getString("ContactList").trim()
						.split(",");
				this.dbDyn
						.ExecuteUpdate("delete from X25RefTask where nmTaskId="
								+ iTaskId + " and nmRefType=42 ");
				for (iU = 0; iU < aV.length; iU++) {
					if (aV[iU].trim().length() > 0) {
						stSql = "INSERT IGNORE INTO X25RefTask (nmTaskId,nmRefType,nmRefId,nmTaskFlag,dtAssignStart) VALUES("
								+ iTaskId + ",42," + aV[iU] + ",1,now())";
						this.dbDyn.ExecuteUpdate(stSql);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		stReturn += stTaskDetail;
		return stReturn;
	}

	public void sendMilestoneMessage(String stPrjId, int nmBaseline,
			String stSchId) throws SQLException {
		ResultSet rs = Ebdb
				.ExecuteSql("SELECT SchFlags,SchStatus FROM Schedule WHERE nmProjectId="
						+ stPrjId
						+ " and nmBaseline="
						+ nmBaseline
						+ " and RecId=" + stSchId);
		rs.next();

		int iFlag = rs.getInt("SchFlags");
		String stStatus = rs.getString("SchStatus");
		rs.close();

		if ("Done".equals(stStatus) && (iFlag & 0x4000) != 0) {
			rs = this.Ebdb
					.ExecuteSql("SELECT * FROM `projects` WHERE `RecId`="
							+ stPrjId);
			rs.next();

			String stProjName = rs.getString("ProjectName");
			SimpleDateFormat fmt = new SimpleDateFormat("MM/dd/yyyy");
			Calendar c = Calendar.getInstance();
			c.add(Calendar.DATE, 10); // number of days to add
			String dateEnd = fmt.format(c.getTime());
			this.makeMessage(
					stProjName,
					rs.getString("ProjectManagerAssignment") + ","
							+ rs.getString("ProjectPortfolioManagerAssignment")
							+ "," + rs.getString("BusinessAnalystAssignment")
							+ "," + rs.getString("Sponsor"), "Task Completion",
					"Task ID <span class='des_bigger'>" + stSchId
							+ "</span> milestone has been completed", dateEnd,
					null);

			rs.close();
		}
	}
	
}
	