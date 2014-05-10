package com.eps.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.eps.model.EbUserData;

public class EbUserDataMapper implements RowMapper<EbUserData>{

	@Override
	public EbUserData mapRow(ResultSet rs, int rowNum) throws SQLException {
		// TODO Auto-generated method stub
		
		if(rs == null)
		{
			return null;
		}
		
		EbUserData EbUD = new EbUserData();

		EbUD.setNmUserId(rs.getInt("nmUserId"));
		EbUD.setFirstName(rs.getString("FirstName"));
		EbUD.setLastName(rs.getString("LastName"));
		
		return EbUD;

	}

	
}
