package com.eps.dao;

import javax.sql.DataSource;

import com.eps.model.EbProgress;
import com.eps.model.EbUserData;
import com.eps.model.EbWorkFlow;
import com.eps.model.EbWorkFlowOfPTM;

public interface EbProgressDao {


	public void setDataSource(DataSource ds);
	
	public EbProgress mappingProgress(EbWorkFlow EbWF, EbUserData EbUD);
	public EbProgress mappingProgress(EbWorkFlowOfPTM EbWFofPTM, EbUserData EbUD);
	
}
