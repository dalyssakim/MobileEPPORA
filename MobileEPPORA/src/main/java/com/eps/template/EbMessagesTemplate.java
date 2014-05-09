package com.eps.template;

import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;

import com.eps.dao.EbMessagesDao;
import com.eps.mapper.EbMessagesMapper;
import com.eps.model.EbMessages;
import com.eps.model.EbUserData;

public class EbMessagesTemplate implements EbMessagesDao{

	private DataSource ds;
	private JdbcTemplate jdbcTemplateObject;

	public void setDataSource(DataSource ds) {
		// TODO Auto-generated method stub
		this.ds = ds;
		this.jdbcTemplateObject = new JdbcTemplate(this.ds);
	}

	public List<EbMessages> mappingMessages(EbUserData EbUD) {
		// TODO Auto-generated method stub
		int nmUserId = EbUD.getNmUserId();

		String SQL ="SELECT t.* FROM X25RefTask rt, X25Task t WHERE t.RecId=rt.nmTaskId AND rt.nmRefType=42 and (t.nmTaskFlag=1 OR (t.nmTaskFlag=2  and dtStart >= DATE_ADD(curdate(),INTERVAL -10 DAY))) AND rt.nmRefId=?";

		//+" and (t.nmTaskFlag=1 OR (t.nmTaskFlag=2  and dtStart >= DATE_ADD(curdate(),INTERVAL -10 DAY)))"
		
		System.out.println(nmUserId);
		List<EbMessages> EbMsg = jdbcTemplateObject.query(SQL, 
				new Object[]{String.valueOf(nmUserId)}, new EbMessagesMapper());

		if(EbMsg.isEmpty()){
			System.out.println("Emptylist");
		}
		return EbMsg;
	}
}
