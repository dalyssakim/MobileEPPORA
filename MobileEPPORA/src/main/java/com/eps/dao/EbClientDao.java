package com.eps.dao;

import java.util.List;

import javax.sql.DataSource;

import com.eps.model.EbClient;

public interface EbClientDao {

	public void setDataSource(DataSource ds);
	public List<EbClient> clientMapping(String stEmail, String stPwd);
	
}
