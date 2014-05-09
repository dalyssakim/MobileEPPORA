package com.eps.template;

import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;

import com.eps.dao.EbUserDataDao;
import com.eps.mapper.EbUserDataMapper;
import com.eps.model.EbClient;
import com.eps.model.EbUserData;

public class EbUserDataTemplate implements EbUserDataDao{

	private DataSource ds;
	private JdbcTemplate jdbcTemplateObject;

	@Override
	public void setDataSource(DataSource ds) {
		// TODO Auto-generated method stub
		this.ds = ds;
		this.jdbcTemplateObject = new JdbcTemplate(this.ds);
	}

	@Override
	public List<EbUserData> mappingUserData(EbClient EbC) {
		// TODO Auto-generated method stub
		if(EbC != null){
			String SQL = "SELECT * FROM users WHERE nmUserId = ?";
			List<EbUserData> EbUD = jdbcTemplateObject.query(SQL, 
					new Object[]{EbC.getRecId()}, new EbUserDataMapper());
			if ((EbUD != null)){
				EbUD.get(0).setNmPriviledge(EbC.getNmPriviledge());
				return EbUD;
			}

		}
		return null;
	}

	
}
