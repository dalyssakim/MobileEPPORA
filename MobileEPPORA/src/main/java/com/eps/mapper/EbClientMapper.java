package com.eps.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.eps.model.EbClient;

public class EbClientMapper implements RowMapper<EbClient> {

	@Override
	public EbClient mapRow(ResultSet rs, int rowNum) throws SQLException {
		// TODO Auto-generated method stub
		
		if(rs == null)
		{
			return null;
		}
		
		EbClient EbC = new EbClient();
		EbC.setRecId(rs.getInt("RecId"));
		EbC.setStEmail(rs.getString("stEmail"));
		EbC.setNmApproveUserID(rs.getInt("nmApproveUserID"));
		EbC.setNmPriviledge(rs.getInt("nmPriviledge"));
		EbC.setStAuth(rs.getString("stAuth"));
		EbC.setStProjects(rs.getString("stProjects"));
		EbC.setNmLastLoginTime(rs.getLong("nmLastLoginTime"));
		EbC.setNmSuccessLoginTime(rs.getLong("SuccessLoginTime"));
		return EbC;
	}

}
