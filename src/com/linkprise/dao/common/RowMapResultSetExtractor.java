package com.linkprise.dao.common;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.linkprise.orm.mapping.IObjectReader;

public class RowMapResultSetExtractor implements
		ResultSetExtractor<List<Map<String, Object>>> {
	private IObjectReader objectReader;
	private int max = 2147483647;

	public RowMapResultSetExtractor(IObjectReader objectReader) {
		this.objectReader = objectReader;
	}

	public RowMapResultSetExtractor(IObjectReader objectReader, int max) {
		this.objectReader = objectReader;
		this.max = max;
	}

	public List<Map<String, Object>> extractData(ResultSet rs)
			throws SQLException {
		List results = new ArrayList();
		ResultSetMetaData rsmd = rs.getMetaData();
		while (rs.next()) {
			try {
				results.add(this.objectReader.readToMap(rs, rsmd));
			} catch (Exception e) {
				throw new SQLException(e);
			}
			if (results.size() >= this.max) {
				break;
			}
		}
		return results;
	}

	public int getMax() {
		return this.max;
	}

	public void setMax(int max) {
		this.max = max;
	}
}