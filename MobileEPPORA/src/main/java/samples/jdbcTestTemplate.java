package samples;

import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;


public class jdbcTestTemplate implements jdbcTestDAO{

	private DataSource dataSource;
	private JdbcTemplate jdbcTemplateObject;
	@Override
	@Autowired
	public void setDataSource(DataSource dataSource) {
		// TODO Auto-generated method stub
		this.dataSource = dataSource;
		this.jdbcTemplateObject = new JdbcTemplate(dataSource);
	}

	@Override
	public void create(String name, String short_info, String description) {
		// TODO Auto-generated method stub
		String SQL = "INSERT INTO my_new_table (name, short_info, description) VALUES (?, ?, ?)";
		
		jdbcTemplateObject.update(SQL, name, short_info, description);
		System.out.println("Created Record Name = "+ name);
		return ;
	}

	@Override
	public jdbcTest getRow(Integer id) {
		// TODO Auto-generated method stub
		 String SQL = "select * from my_new_table where id = ?";
	      jdbcTest test = jdbcTemplateObject.queryForObject(SQL, 
	                        new Object[]{id}, new jdbcTestingMapper());
	      return test;
	}

	@Override
	public List<jdbcTest> listRows() {
		// TODO Auto-generated method stub
		String SQL = "select * from my_new_table";
	      List <jdbcTest> test = jdbcTemplateObject.query(SQL, 
	                                new jdbcTestingMapper());
	      return test;
	}

	@Override
	public void delete(Integer id) {
		// TODO Auto-generated method stub
		String SQL = "DELETE FROM my_new_table where id = ?";
		jdbcTemplateObject.update(SQL, id);
		System.out.println("Deleted id ! "+ id);
		return ;
	}

	@Override
	public void update(Integer id, String description) {
		// TODO Auto-generated method stub
		String SQL = "UPDATE my_new_table SET description = ? where id = ?";
		jdbcTemplateObject.update(SQL, description, id);
		System.out.println("Updated !");
		return ;
	}

}
