package com.eps.template;

import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;

import com.eps.dao.EbExecutiveDashboardDao;
import com.eps.mapper.EbExecutiveDashboardMapper;
import com.eps.mapper.EbMessagesMapper;
import com.eps.model.EbExecutiveDashboard;
import com.eps.model.EbMessages;
import com.eps.model.EbUserData;

public class EbExecutiveDashboardTemplate implements EbExecutiveDashboardDao{

	private DataSource ds;
	private JdbcTemplate jdbcTemplateObject;
	
	@Override
	public void setDataSource(DataSource ds) {
		// TODO Auto-generated method stub
		this.ds = ds;
		this.jdbcTemplateObject = new JdbcTemplate(this.ds);
	}

	@Override
	public List<EbExecutiveDashboard> mappingDashboard(EbUserData EbUD) {
		// TODO Auto-generated method stub
		String nmUserId = String.valueOf(EbUD.getNmUserId());
		String SQL = "select p.*, d.stMoneySymbol,"
				+ "SUM(IF(s.SchStatus='Done',s.nmExpenditureToDate,0)) CPIExpended,"
				+ "SUM(IF(s.SchStatus='Done',s.SchCost,0)) CPIEstimated,"
				+ "SUM(IF(s.SchStatus='Done',s.SchEfforttoDate,0)) SPIExpended,"
				+ "SUM(IF(s.SchStatus='Done',s.SchEstimatedEffort,0)) SPIEstimated,"
				+ "SUM(s.SchEffortToDate) SchExpended,"
				+ "SUM(s.SchEstimatedEffort) SchEstimated,"
				+ "IFNULL(SUM(IF(s.SchStatus='Done',s.SchEstimatedEffort,0))/SUM(IF(s.SchStatus='Done',s.SchEfforttoDate,0)), 0) SPI,"
				+ "IFNULL(SUM(IF(s.SchStatus='Done',s.SchCost,0))/SUM(IF(s.SchStatus='Done',s.nmExpenditureToDate,0)), 0) CPI"
				+ " from Projects p"
				+ " join teb_division d on p.nmDivision=d.nmDivision"
				+ " join Schedule s on s.nmProjectId=p.RecId and s.nmBaseline=p.CurrentBaseline and s.lowlvl=1"
				+ " where p.ProjectStatus=1"
				+ " group by p.RecId";
		
		List<EbExecutiveDashboard> EbDashboard = jdbcTemplateObject.query(SQL, 
				 new EbExecutiveDashboardMapper());

		
		return EbDashboard;
	}

}
