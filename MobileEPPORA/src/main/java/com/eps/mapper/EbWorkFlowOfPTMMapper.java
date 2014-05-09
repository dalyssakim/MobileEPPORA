package com.eps.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.eps.model.EbWorkFlowOfPTM;

public class EbWorkFlowOfPTMMapper implements RowMapper<EbWorkFlowOfPTM> {

	@Override
	public EbWorkFlowOfPTM mapRow(ResultSet rs, int rowNum) throws SQLException {
		// TODO Auto-generated method stub
		
		if(rs == null)
		{
			return null;
		}
		
		
		System.out.println("Mapper");
		EbWorkFlowOfPTM EbWFofPTM = new EbWorkFlowOfPTM();
		EbWFofPTM.setPrjId(rs.getString("ProjectId"));
		EbWFofPTM.setBaseline(rs.getString("nmBaseline"));
		//EbWFofPTM.setSchId(rs.getString("SchId"));
		EbWFofPTM.setProjectName(rs.getString("ProjectName"));
		EbWFofPTM.setTaskId(rs.getInt("RecId"));
		EbWFofPTM.setProjectTeamMember(rs.getString("UserName"));
		EbWFofPTM.setStartDate(formatDate(String.valueOf(rs.getDate("SchStartDate"))));
		EbWFofPTM.setExpendedHoursToDate(String.valueOf(rs.getDouble("nmActualBefore")));
		EbWFofPTM.setEstimatedHours(String.valueOf(rs.getDouble("nmAllocated")));
		EbWFofPTM.setExpendedHoursToday(String.valueOf(rs.getDouble("nmActualToday")));
		EbWFofPTM.setMessage(rs.getString("SchComments"));
		EbWFofPTM.setStatus(rs.getString("SchStatus"));
		EbWFofPTM.setSchTitle(rs.getString("SchTitle"));
		EbWFofPTM.setDescription(rs.getString("SchDescription"));
		
		return EbWFofPTM;
	}

	public String formatDate(String date){
		
		if(date.length()>=10){
			date = date.substring(0, 10);
		}
		
		return date;
	}
}
