package samples;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

public class jdbcTestingMapper implements RowMapper<jdbcTest> {

	@Override
	public jdbcTest mapRow(ResultSet rs, int rowNum) throws SQLException {
		// TODO Auto-generated method stub
		jdbcTest test = new jdbcTest();
		test.setId(rs.getInt("id"));
		test.setName(rs.getString("name"));
		test.setShortInfo(rs.getString("short_info"));
		test.setDescription(rs.getString("description"));
		test.setModified(rs.getString("modified"));
		return test;
	}

}
