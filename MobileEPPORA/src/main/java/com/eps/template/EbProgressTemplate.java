package com.eps.template;

import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;

import com.eps.dao.EbProgressDao;
import com.eps.mapper.EbProgressMapper;
import com.eps.mapper.EbUserDataMapper;
import com.eps.model.EbClient;
import com.eps.model.EbProgress;
import com.eps.model.EbUserData;
import com.eps.model.EbWorkFlow;
import com.eps.model.EbWorkFlowOfPTM;

public class EbProgressTemplate implements EbProgressDao{

	private DataSource ds;
	private JdbcTemplate jdbcTemplateObject;

	
	@Override
	public void setDataSource(DataSource ds) {
		// TODO Auto-generated method stub
		this.ds = ds;
		this.jdbcTemplateObject = new JdbcTemplate(this.ds);
	}
	

	/*
	"SELECT *,"
	+ " SUM(IF(spa.dtUpdate=curdate() AND spa.nmUserId="
	+ this.ebEnt.ebUd.getLoginId()
	+ ",spa.nmActual,0)) nmToday"
	+ " FROM schedule_progress sp"
	+ " LEFT JOIN schedule_progress_allocate spa ON sp.RecId=spa.nmProgressId"
	+ " WHERE sp.nmProjectId=" + stPrjId
	+ " AND sp.nmBaseline=" + stBaseLine
	+ " AND sp.nmSchRecId=" + stRecId
	+ " GROUP BY sp.RecId"
	+ " ORDER BY sp.stDescription"
*/
	@Override
	public EbProgress mappingProgress(EbWorkFlow EbWF, EbUserData EbUD) {
		// TODO Auto-generated method stub
		
		String SQL = "SELECT *, SUM(IF(spa.dtUpdate=curdate() AND spa.nmUserId="+String.valueOf(EbUD.getNmUserId())
			+ ",spa.nmActual,0)) nmToday"
			+ " FROM schedule_progress sp"
			+ " LEFT JOIN schedule_progress_allocate spa ON sp.RecId=spa.nmProgressId"
			+ " WHERE sp.nmProjectId=" + EbWF.getPrjId()
			+ " AND sp.nmBaseline=" + EbWF.getBaseline()
			+ " AND sp.nmSchRecId=" + String.valueOf(EbWF.getTaskId())
			+ " GROUP BY sp.RecId"
			+ " ORDER BY sp.stDescription";
		
		List<EbProgress> EbProgress = jdbcTemplateObject.query(SQL, 
				 new EbProgressMapper());
		if(EbProgress.size() != 0){
			return EbProgress.get(0);
		}else{
			EbProgress temp = new EbProgress();
			temp.setDescription("no progress");
			EbProgress.add(temp);
			
		}
		
		return EbProgress.get(0);
	}


	@Override
	public EbProgress mappingProgress(EbWorkFlowOfPTM EbWFofPTM, EbUserData EbUD) {
		// TODO Auto-generated method stub
		
		String SQL = "SELECT *,"
				+ " SUM(IF(spa.dtUpdate=curdate() AND spa.nmUserId="
				+ String.valueOf(EbUD.getNmUserId())
				+ ",spa.nmActual,0)) nmToday"
				+ " FROM schedule_progress sp"
				+ " LEFT JOIN schedule_progress_allocate spa ON sp.RecId=spa.nmProgressId"
				+ " WHERE sp.nmProjectId=" + EbWFofPTM.getPrjId()
				+ " AND sp.nmBaseline=" + EbWFofPTM.getBaseline()
				+ " AND sp.nmSchRecId=" + String.valueOf(EbWFofPTM.getTaskId())
				+ " GROUP BY sp.RecId"
				+ " ORDER BY sp.stDescription";
		
		List<EbProgress> EbProgress = jdbcTemplateObject.query(SQL, 
				 new EbProgressMapper());
		if(EbProgress.size() != 0){
			return EbProgress.get(0);
		}else{
			EbProgress temp = new EbProgress();
			temp.setDescription("no progress");
			EbProgress.add(temp);
			
		}
		
		return EbProgress.get(0);
	}

}
