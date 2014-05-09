package com.eps.dao;

import java.util.List;

import javax.sql.DataSource;

import org.springframework.context.ApplicationContext;

import com.eps.model.EbUserData;
import com.eps.model.EbWorkFlow;

public interface EbWorkFlowDao {

public void setDataSource(DataSource ds);
	
public List<EbWorkFlow> mappingWorkFlow(EbUserData EbUD,
		ApplicationContext DbEpsContext);

	
}
