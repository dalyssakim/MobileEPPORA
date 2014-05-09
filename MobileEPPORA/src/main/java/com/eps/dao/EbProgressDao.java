package com.eps.dao;

import java.util.List;

import javax.sql.DataSource;

import com.eps.model.*;

public interface EbProgressDao {


	public void setDataSource(DataSource ds);
	
	public EbProgress mappingProgress(EbWorkFlow EbWF, EbUserData EbUD);
	public EbProgress mappingProgress(EbWorkFlowOfPTM EbWFofPTM, EbUserData EbUD);
	
}
