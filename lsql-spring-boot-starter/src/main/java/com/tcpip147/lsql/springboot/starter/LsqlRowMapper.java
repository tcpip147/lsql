package com.tcpip147.lsql.springboot.starter;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

public class LsqlRowMapper implements RowMapper<RowMap> {

	private boolean isLowerCase;

	public LsqlRowMapper(LsqlProperties properties) {
		isLowerCase = "lowercase".equals(properties.getMapCaseSensitive());
	}

	@Override
	public RowMap mapRow(ResultSet rs, int rowNum) throws SQLException {
		RowMap map = new RowMap(isLowerCase);
		ResultSetMetaData metaData = rs.getMetaData();
		int columnCount = metaData.getColumnCount();
		for (int i = 1; i <= columnCount; i++) {
			String columnName = metaData.getColumnLabel(i);
			Object value = rs.getObject(i);
			if (isLowerCase) {
				map.put(columnName.toLowerCase(), value);
			} else {
				map.put(columnName.toUpperCase(), value);
			}
		}
		return map;
	}

}
