package com.eps.template;

import java.util.List;

import javax.sql.DataSource;

import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;

import com.eps.dao.EbWorkFlowOfPTMDao;
import com.eps.mapper.EbWorkFlowMapper;
import com.eps.mapper.EbWorkFlowOfPTMMapper;
import com.eps.model.EbUserData;
import com.eps.model.EbWorkFlow;
import com.eps.model.EbWorkFlowOfPTM;

public class EbWorkFlowOfPTMTemplate implements EbWorkFlowOfPTMDao{

	private DataSource ds;
	private JdbcTemplate jdbcTemplateObject;
	
	@Override
	public void setDataSource(DataSource ds) {
		// TODO Auto-generated method stub
		this.ds = ds;
		jdbcTemplateObject = new JdbcTemplate(this.ds);
	}

	@Override
	public List<EbWorkFlowOfPTM> mappingWorkFlowOfPTM(EbUserData EbUD,ApplicationContext DbEpsContext) {
		// TODO Auto-generated method stub
		int nmUserId = EbUD.getNmUserId();
/*
		stSql = "select p.ProjectName, p.RecId AS ProjectId,"
				+ " s.RecId, s.SchId, s.SchDescription, s.nmBaseline, s.SchStartDate, s.SchComments,"
				+ " wf.nmAllocated, wf.nmApproved, wf.SchStatus,"
				+ " CONCAT(u.FirstName, ' ', u.LastName) AS UserName, wf.nmUserId,"
				+ " SUM( IF(ta.dtDatePrj<=curdate(),ta.nmActual,0) ) nmActualBefore,"
				+ " wf.nmActual nmActualToday"
				+ " FROM teb_workflow wf"
				+ " LEFT JOIN Projects p ON p.CurrentBaseline=wf.nmBaseline AND p.RecId=wf.nmProjectId"
				+ " LEFT JOIN Schedule s ON wf.nmProjectId=s.nmProjectId AND wf.nmBaseline=s.nmBaseline AND wf.RecId=s.RecId"
				+ " LEFT JOIN Users u ON wf.nmUserId=u.nmUserId"
				+ " LEFT JOIN teb_allocateprj ta ON p.RecId=ta.nmPrjId AND s.RecId=ta.nmTaskId AND u.nmUserId=ta.nmUserId"
				+ " WHERE p.ProjectStatus=1 AND p.isTemplate=0 AND wf.iHandleFlags=0"
				+ " AND (p.ProjectManagerAssignment="
				+ String.valueOf(nmUserId)
				+ " OR p.ProjectPortfolioManagerAssignment="
				+ String.valueOf(nmUserId)
				+ ")";
	*/	
		
		String SQL = "select p.ProjectName, p.RecId AS ProjectId,"
				+ " s.RecId, s.SchTitle, s.SchDescription, s.nmBaseline, s.SchStartDate, s.SchComments,"
				+ " wf.nmAllocated, wf.nmApproved, wf.SchStatus,"
				+ " CONCAT(u.FirstName, ' ', u.LastName) AS UserName, wf.nmUserId,"
				+ " SUM( IF(ta.dtDatePrj<curdate(),ta.nmActual,0) ) nmActualBefore,"
				+ " wf.nmActual nmActualToday"
				+ " FROM teb_workflow wf"
				+ " LEFT JOIN Projects p ON p.CurrentBaseline=wf.nmBaseline AND p.RecId=wf.nmProjectId"
				+ " LEFT JOIN Schedule s ON wf.nmProjectId=s.nmProjectId AND wf.nmBaseline=s.nmBaseline AND wf.RecId=s.RecId"
				+ " LEFT JOIN Users u ON wf.nmUserId=u.nmUserId"
				+ " LEFT JOIN teb_allocateprj ta ON p.RecId=ta.nmPrjId AND s.RecId=ta.nmTaskId AND u.nmUserId=ta.nmUserId"
				+ " WHERE p.ProjectStatus=1 AND wf.iHandleFlags=0"
				+ " AND (p.ProjectManagerAssignment="+String.valueOf(nmUserId)
				+ " OR p.ProjectPortfolioManagerAssignment="+String.valueOf(nmUserId)+")";
		
		
		System.out.println("Template1");
		List<EbWorkFlowOfPTM> EbWFofPTM = jdbcTemplateObject.query(SQL, 
				 new EbWorkFlowOfPTMMapper());
		if ((EbWFofPTM != null)){
			System.out.println("not null!");
	
			EbProgressTemplate EbPGTemplate = (EbProgressTemplate)DbEpsContext.getBean("EbProgressTemplate");

			
			//mapping Progress to each work flow entry.
			for(int i=0; i<EbWFofPTM.size(); i++){
				EbWFofPTM.get(i).setEbPg(EbPGTemplate.mappingProgress(EbWFofPTM.get(i), EbUD));
				EbWFofPTM.get(i).setPgDescription(EbWFofPTM.get(i).getEbPg().getDescription());
				EbWFofPTM.get(i).setPgPlannedQuantity(EbWFofPTM.get(i).getEbPg().getPlannedQuantity());
				EbWFofPTM.get(i).setPgIncrementalProgress(EbWFofPTM.get(i).getEbPg().getIncrementalProgress());
				EbWFofPTM.get(i).setPgAccomplishedToDate(EbWFofPTM.get(i).getEbPg().getAccomplishedToDate());
			}
		
			return EbWFofPTM;
			
		}
		System.out.println("Template2");
		
		
		
		return null;
	}

}
