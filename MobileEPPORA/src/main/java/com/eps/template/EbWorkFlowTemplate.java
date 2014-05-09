package com.eps.template;

import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;

import com.eps.dao.EbWorkFlowDao;
import com.eps.mapper.EbWorkFlowMapper;
import com.eps.model.EbUserData;
import com.eps.model.EbWorkFlow;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class EbWorkFlowTemplate implements EbWorkFlowDao{

	private DataSource ds;
	private JdbcTemplate jdbcTemplateObject;

	@Override
	public void setDataSource(DataSource ds) {
		// TODO Auto-generated method stub
		this.ds = ds;
		this.jdbcTemplateObject = new JdbcTemplate(this.ds);
	}

	@Override
	public List<EbWorkFlow> mappingWorkFlow(EbUserData EbUD, ApplicationContext DbEpsContext) {
		// TODO Auto-generated method stub
		int nmUserId = EbUD.getNmUserId();
		
		/*
	stSql = "SELECT * FROM ("
				+ " SELECT p.ProjectName, s.* ,"
				+ " SUM( ta.nmAllocated ) nmAllocated,"
				+ " SUM( IF(ta.dtDatePrj<=curdate(),ta.nmActual,0) ) nmActualBefore,"
				+ " SUM( IFNULL(tw.nmActual,0) ) nmActualToday,"
				+ " SUM( ta.nmActualApproved ) nmActualApproved,"
				+ " tw.nmActual nmDoActual, tw.SchStatus SchDoStatus"
				+ " FROM teb_allocateprj ta INNER JOIN projects p ON ta.nmPrjId=p.RecId AND ta.nmBaseline=p.CurrentBaseline AND p.ProjectStatus=1 AND p.isTemplate=0"
				+ " INNER JOIN schedule s ON ta.nmPrjId=s.nmProjectId AND ta.nmBaseline=s.nmBaseline AND ta.nmTaskId=s.RecId AND (s.SchStatus='Not Started' OR s.SchStatus='In Progress')"
				+ " LEFT JOIN teb_workflow tw ON tw.nmProjectId=s.nmProjectId AND tw.nmBaseline=s.nmBaseline AND tw.RecId=s.RecId"
				+ " WHERE (tw.SchStatus IS NULL OR tw.SchStatus!='Done') AND ta.nmUserId="
				+ this.ebEnt.ebUd.getLoginId()
				+ " GROUP BY ta.nmPrjId, ta.nmTaskId"
				+ " ORDER BY s.SchStartDate, p.TotalRankingScore desc, p.ProjectName, s.SchTitle) ma"
				+ " LIMIT " + iFrom + "," + iDisplay;
		rs = this.ebEnt.dbDyn.ExecuteSql(stSql);
		

			String SQL = "SELECT * FROM ("
					+ " SELECT p.projectName, s.* ,"
					+ " SUM( ta.nmAllocated ) nmAllocated,"
					+ " SUM( IF(ta.dtDatePrj<curdate(),ta.nmActual,0) ) nmActualBefore,"
					+ " SUM( IF(ta.dtDatePrj=curdate(),ta.nmActual,0) ) nmActualToday,"
					+ " SUM( ta.nmActualApproved ) nmActualApproved,"
					+ " tw.nmActual nmDoActual, tw.SchStatus SchDoStatus"
					+ " FROM teb_allocateprj ta INNER JOIN Projects p ON ta.nmPrjId=p.RecId AND ta.nmBaseline=p.CurrentBaseline AND p.ProjectStatus=1"
					+ " INNER JOIN schedule s ON ta.nmPrjId=s.nmProjectId AND ta.nmBaseline=s.nmBaseline AND ta.nmTaskId=s.RecId AND (s.SchStatus='Not Started' OR s.SchStatus='In Progress')"
					+ " LEFT JOIN teb_workflow tw ON tw.nmProjectId=s.nmProjectId AND tw.nmBaseline=s.nmBaseline AND tw.RecId=s.RecId"
					+ " WHERE (tw.SchStatus IS NULL OR tw.SchStatus!='Done') AND ta.nmUserId="
					+ " ? "
					+ " GROUP BY ta.nmPrjId, ta.nmTaskId"
					+ " ORDER BY s.SchStartDate, p.TotalRankingScore desc, p.ProjectName, s.SchTitle) ma";
			*/
		
		String SQL = "SELECT * FROM ("
				+ " SELECT p.ProjectName, s.* ,"
				+ " SUM( ta.nmAllocated ) nmAllocated,"
				+ " SUM( IF(ta.dtDatePrj<=curdate(),ta.nmActual,0) ) nmActualBefore,"
				+ " SUM( IFNULL(tw.nmActual,0) ) nmActualToday,"
				+ " SUM( ta.nmActualApproved ) nmActualApproved,"
				+ " tw.nmActual nmDoActual, tw.SchStatus SchDoStatus"
				+ " FROM teb_allocateprj ta INNER JOIN projects p ON ta.nmPrjId=p.RecId AND ta.nmBaseline=p.CurrentBaseline AND p.ProjectStatus=1 AND p.isTemplate=0"
				+ " INNER JOIN schedule s ON ta.nmPrjId=s.nmProjectId AND ta.nmBaseline=s.nmBaseline AND ta.nmTaskId=s.RecId AND (s.SchStatus='Not Started' OR s.SchStatus='In Progress')"
				+ " LEFT JOIN teb_workflow tw ON tw.nmProjectId=s.nmProjectId AND tw.nmBaseline=s.nmBaseline AND tw.RecId=s.RecId"
				+ " WHERE (tw.SchStatus IS NULL OR tw.SchStatus!='Done') AND ta.nmUserId="
				+ " ? "
				+ " GROUP BY ta.nmPrjId, ta.nmTaskId"
				+ " ORDER BY s.SchStartDate, p.TotalRankingScore desc, p.ProjectName, s.SchTitle) ma";
	
		
			List<EbWorkFlow> EbWF = jdbcTemplateObject.query(SQL, 
					new Object[]{String.valueOf(nmUserId)}, new EbWorkFlowMapper());


			if ((EbWF != null)){
				System.out.println("not null!");
				

				EbProgressTemplate EbPGTemplate = (EbProgressTemplate)DbEpsContext.getBean("EbProgressTemplate");

		
				//mapping Progress to each work flow entry.
				for(int i=0; i<EbWF.size(); i++){
					EbWF.get(i).setEbPg(EbPGTemplate.mappingProgress(EbWF.get(i), EbUD));
					EbWF.get(i).setPgDescription(EbWF.get(i).getEbPg().getDescription());
					EbWF.get(i).setPgPlannedQuantity(EbWF.get(i).getEbPg().getPlannedQuantity());
					EbWF.get(i).setPgIncrementalProgress(EbWF.get(i).getEbPg().getIncrementalProgress());
					EbWF.get(i).setPgAccomplishedToDate(EbWF.get(i).getEbPg().getAccomplishedToDate());
				}

				return EbWF;
				
			}
		
		
		return null;
	}
	
	public boolean editWorkFlow(EbWorkFlow EbWF, EbUserData EbUD){
		
		String SQLProgress = "REPLACE INTO schedule_progress_allocate (nmProgressId, nmUserId, dtUpdate, nmActual) VALUES"
				+ "(" + EbWF.getEbPg().getProgressId() + "," + String.valueOf(EbUD.getNmUserId()) + ", "+ "curdate() ," + EbWF.getPgIncrementalProgress() +")";
		try {
			jdbcTemplateObject.update(SQLProgress);
		}catch(Exception e){
			return false;
		}
		
		String SQL = "REPLACE INTO teb_workflow VALUE ("
				+ EbWF.getPrjId() + ","
				+ EbWF.getBaseline() + ","
				+ EbWF.getSchId() + ","
				+ EbWF.getEstimatedHours() + ","
				+ EbWF.getExpendedHoursToday() + ","
				+ "0 ,"
				+ String.valueOf(EbUD.getNmUserId()) + ","
				+ EbWF.getStatus() + ","
				+ "curdate(), 0 )";
		try{
		jdbcTemplateObject.update(SQL);
			return true;
		}catch(Exception e)
		{		return false;
		}
		
	}

	/*
	 * "SELECT * FROM ("
					+ " SELECT p.projectName, s.* ,"
					+ " SUM( ta.nmAllocated ) nmAllocated,"
					+ " SUM( IF(ta.dtDatePrj<curdate(),ta.nmActual,0) ) nmActualBefore,"
					+ " SUM( IF(ta.dtDatePrj=curdate(),ta.nmActual,0) ) nmActualToday,"
					+ " SUM( ta.nmActualApproved ) nmActualApproved,"
					+ " tw.nmActual nmDoActual, tw.SchStatus SchDoStatus"
					+ " FROM teb_allocateprj ta INNER JOIN Projects p ON ta.nmPrjId=p.RecId AND ta.nmBaseline=p.CurrentBaseline AND p.ProjectStatus=1"
					+ " INNER JOIN schedule s ON ta.nmPrjId=s.nmProjectId AND ta.nmBaseline=s.nmBaseline AND ta.nmTaskId=s.RecId AND (s.SchStatus='Not Started' OR s.SchStatus='In Progress')"
					+ " LEFT JOIN teb_workflow tw ON tw.nmProjectId=s.nmProjectId AND tw.nmBaseline=s.nmBaseline AND tw.RecId=s.RecId"
					+ " WHERE (tw.SchStatus IS NULL OR tw.SchStatus!='Done') AND ta.nmUserId="
					+ "22"
					+ " GROUP BY ta.nmPrjId, ta.nmTaskId"
					+ " ORDER BY s.SchStartDate, p.TotalRankingScore desc, p.ProjectName, s.SchTitle) ma"
	 */
	
}
