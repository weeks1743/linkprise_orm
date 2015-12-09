package com.linkprise.dao.common;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.linkprise.orm.mapping.IObjectReader;

public class RowsResultSetExtractor<T> implements ResultSetExtractor<List<T>> {
	private IObjectReader objectReader;
	private Class<T> cls = null;
	private int offset = 0;
	private int max = 2147483647;

	public RowsResultSetExtractor(IObjectReader objectReader, Class<T> cls) {
		this.objectReader = objectReader;
		this.cls = cls;
	}

	public List<T> extractData(ResultSet rs) throws SQLException {
		List results = new ArrayList();
		ResultSetMetaData rsmd = rs.getMetaData();
		int pos = 0;
		while (rs.next()) {
			if (pos >= this.offset) {
				try {
					results.add(this.objectReader.read(this.cls, rs, rsmd));
				} catch (Exception e) {
					throw new SQLException(e);
				}
			}
			if (results.size() >= this.max) {
				break;
			}
			pos++;
		}
		return results;
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