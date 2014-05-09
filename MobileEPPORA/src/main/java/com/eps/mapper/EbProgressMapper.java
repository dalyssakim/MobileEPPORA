package com.eps.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.eps.model.*;

public class EbProgressMapper implements RowMapper<EbProgress>{

	@Override
	public EbProgress mapRow(ResultSet rs, int rowNum) throws SQLException {
		// TODO Auto-generated method stub
		
		EbProgress EbPG = new EbProgress();
		EbPG.setDescription(rs.getString("stDescription"));
		EbPG.setPlannedQuantity(String.valueOf(rs.getInt("nmQuantity")));
		EbPG.setIncrementalProgress(String.valueOf(rs.getInt("nmToday")));
		EbPG.setAccomplishedToDate(String.valueOf(rs.getInt("nmAccomplished")));
		EbPG.setProgressId(String.valueOf(rs.getInt("nmProgressId")));
		return EbPG;
	}

}
