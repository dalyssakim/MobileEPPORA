package com.eps.dao;

import java.util.List;

import javax.sql.DataSource;

import com.eps.model.EbClient;
import com.eps.model.EbUserData;

public interface EbUserDataDao {

	public void setDataSource(DataSource ds);
	
	public List<EbUserData> mappingUserData(EbClient EbC);
}
