package com.eps.dao;

import java.util.List;

import javax.sql.DataSource;

import com.eps.model.EbMessages;
import com.eps.model.EbUserData;

public interface EbMessagesDao {
	
	public void setDataSource(DataSource ds);
	public List<EbMessages> mappingMessages(EbUserData EbUD);
}
