package com.eps.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.eps.model.EbWorkFlow;

public class EbWorkFlowMapper implements RowMapper<EbWorkFlow>{

	@Override
	public EbWorkFlow mapRow(ResultSet rs, int rowNum) throws SQLException {
		// TODO Auto-generated method stub
		if(rs == null)
		{
			return null;
		}
		
		EbWorkFlow EbWF = new EbWorkFlow();
		EbWF.setPrjId(rs.getString("nmProjectId"));
		EbWF.setBaseline(rs.getString("nmBaseline"));
		EbWF.setSchId(rs.getString("SchId"));
		EbWF.setProjectName(rs.getString("ProjectName"));
		EbWF.setTaskId(rs.getInt("RecID"));
		EbWF.setDescription(rs.getString("SchDescription"));
		EbWF.setStartDate(formatDate(String.valueOf(rs.getString("SchStartDate"))));
		EbWF.setExpectedHoursToDate(rs.getString("nmActualBefore"));
		EbWF.setExpendedHoursToday(rs.getString("nmDoActual"));
		EbWF.setEstimatedHours(rs.getString("nmAllocated"));
		EbWF.setMessage(rs.getString("SchWFMessage"));
		EbWF.setSchTitle(rs.getString("SchTitle"));
		String stStatus = rs.getString("SchDoStatus") == null ? rs
				.getString("SchStatus") : rs.getString("SchDoStatus");

		
		EbWF.setStatus(stStatus);
		
		return EbWF;

	}

	public String formatDate(String date){
		
		if(date.length()>=10){
			date = date.substring(0, 10);
		}
		
		return date;
	}
	
}
