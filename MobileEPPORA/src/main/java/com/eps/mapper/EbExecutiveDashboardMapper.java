package com.eps.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;
import com.eps.model.EbExecutiveDashboard;

public class EbExecutiveDashboardMapper implements RowMapper<EbExecutiveDashboard> {

	@Override
	public EbExecutiveDashboard mapRow(ResultSet rs, int rowNum)
			throws SQLException {
		// TODO Auto-generated method stub

		EbExecutiveDashboard EbDashboard = new EbExecutiveDashboard();
		EbDashboard.setProjectName(rs.getString("ProjectName"));
		EbDashboard.setStartDate(rs.getString("ProjectStartDate"));
		EbDashboard.setEndDate(rs.getString("EstimatedCompletionDate"));
		EbDashboard.setEstimatedHours(rs.getString("SchEstimated"));
		EbDashboard.setExpendedHours(rs.getString("SchExpended"));
		EbDashboard.setEstimatedCost(rs.getString("SPIEstimated"));
		EbDashboard.setExpendedToDate(rs.getString("SPIExpended"));
		EbDashboard.setCpi(renderPI(String.valueOf(rs.getDouble("CPI"))));
		EbDashboard.setSpi(renderPI(String.valueOf(rs.getDouble("SPI"))));
		return EbDashboard;
	}

	public String renderPI(String pis){
		if(pis.length()>=4){
			return pis.substring(0, 4);
		}
		
		return pis;
		
	}
	
}
