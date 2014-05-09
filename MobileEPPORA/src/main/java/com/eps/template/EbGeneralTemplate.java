package com.eps.template;

import java.sql.ResultSet;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;

public class EbGeneralTemplate {

	private DataSource ds;
	private JdbcTemplate jdbcTemplateObject;

	
	public void setDataSource(DataSource ds) {
		// TODO Auto-generated method stub
		this.ds = ds;
		this.jdbcTemplateObject = new JdbcTemplate(this.ds);
	}
	
	public boolean sqlUpdate(String SQL){
		try{
			
			jdbcTemplateObject.execute(SQL);
			
		}catch(Exception e){
			return false;
		}
		
		return true;
	}
	

}
