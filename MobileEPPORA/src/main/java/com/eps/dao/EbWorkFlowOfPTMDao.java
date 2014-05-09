package com.eps.dao;

import java.util.List;

import javax.sql.DataSource;

import org.springframework.context.ApplicationContext;

import com.eps.model.EbUserData;
import com.eps.model.EbWorkFlowOfPTM;

public interface EbWorkFlowOfPTMDao {


public void setDataSource(DataSource ds);
	
public List<EbWorkFlowOfPTM> mappingWorkFlowOfPTM(EbUserData EbUD,ApplicationContext DbEpsContext);
}
