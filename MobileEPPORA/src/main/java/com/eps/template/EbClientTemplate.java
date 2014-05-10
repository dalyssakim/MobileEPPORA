package com.eps.template;

import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;

import com.eps.dao.EbClientDao;
import com.eps.mapper.EbClientMapper;
import com.eps.model.EbClient;

public class EbClientTemplate implements EbClientDao{

	private DataSource ds;
	private JdbcTemplate jdbcTemplateObject;
	@Override
	public void setDataSource(DataSource ds) {
		// TODO Auto-generated method stub
		this.ds = ds;
		this.jdbcTemplateObject = new JdbcTemplate(this.ds);
	}
//	
//	stPassword=password('"
//			+ this.aLogin[3]
//			+ "') or stPassword=old_password('"
//			+ this.aLogin[3] + "') )");

	@Override
	public List<EbClient> clientMapping(String stEmail, String stPwd) {
		// TODO Auto-generated method stub
		String SQL = "SELECT * FROM X25User WHERE stEmail = ? AND (stPassword=password( ? ) OR stPassword=old_password( ? ))";
		List<EbClient> EbC = jdbcTemplateObject.query(SQL, 
										new Object[]{stEmail,stPwd,stPwd}, new EbClientMapper());
		if ((EbC != null)){
			return EbC;
		}

		return null;
	}


}
