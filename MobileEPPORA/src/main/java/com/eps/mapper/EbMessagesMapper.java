package com.eps.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.eps.model.EbMessages;

public class EbMessagesMapper implements RowMapper<EbMessages>{

	@Override
	public EbMessages mapRow(ResultSet rs, int rowNum) throws SQLException {
		// TODO Auto-generated method stub
		if(rs == null)
		{
			return null;
		}
		
		EbMessages EbMsg = new EbMessages();
		String []stMsgParts = rs.getString("stDescription").split(" - ");
		
		if(stMsgParts.length >= 2){
			EbMsg.setProjectName(stMsgParts[0]);
		}else{
			EbMsg.setProjectName("");
		}
		EbMsg.setMessage(getRidOfHtml(rs.getString("stTitle")));
		EbMsg.setDescription(getRidOfHtml(rs.getString("stDescription")));
		EbMsg.setRecId(rs.getString("RecId"));
		
		return EbMsg;
	}

	public String getRidOfHtml(String msg){
		String temp = msg;
		temp.replace("<span class=des_bigger>", " ");
		temp.replace("</span>", " ");
		return temp;
	}
	
}
