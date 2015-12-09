package com.linkprise.dao.common;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import com.linkprise.orm.mapping.IJsonReader;

public class RowsResultSetJsonExtractor implements ResultSetExtractor<String> {
	private int offset = 0;
	private int max = 2147483647;

	private IJsonReader jsonReader = null;

	public RowsResultSetJsonExtractor(IJsonReader jsonReader) {
		this.jsonReader = jsonReader;
	}

	public String extractData(ResultSet rs) throws SQLException {
		StringBuilder json = new StringBuilder();
		json.append('[');
		ResultSetMetaData rsmd = rs.getMetaData();
		int pos = 0;
		int len = 0;
		while (rs.next()) {
			if (pos >= this.offset) {
				try {
					if (len == 0)
						json.append(this.jsonReader.read(rs, rsmd));
					else {
						json.append(',').append(this.jsonReader.read(rs, rsmd));
					}
					len++;
				} catch (Exception e) {
					throw new SQLException(e);
				}
			}
			if (len >= this.max) {
				break;
			}
			pos++;
		}
		json.append(']');
		return json.toString();
	}

	public int getOffset() {
		return this.offset;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

	public int getMax() {
		return this.max;
	}

	public void setMax(int max) {
		this.max = max;
	}
}