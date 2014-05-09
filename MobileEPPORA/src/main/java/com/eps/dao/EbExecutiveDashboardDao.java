package com.eps.dao;

import java.util.List;

import javax.sql.DataSource;

import com.eps.model.EbExecutiveDashboard;
import com.eps.model.EbUserData;

public interface EbExecutiveDashboardDao {
	public void setDataSource(DataSource ds);
	public List<EbExecutiveDashboard> mappingDashboard(EbUserData EbUD);
}
