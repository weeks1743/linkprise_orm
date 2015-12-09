package com.linkprise.dao.dialect;

import com.linkprise.dao.id.SequenceGenerator;

public class IngresDialect implements Dialect {
	public boolean supportsOffset() {
		return false;
	}

	public String getLimitString(String query, int offset, int limit) {
		if (offset > 0) {
			throw new UnsupportedOperationException(
					"query result offset is not supported");
		}
		return new StringBuffer(query.length() + 16).append(query)
				.insert(6, " first " + limit).toString();
	}

	public String getSelectGUIDString() {
		return "select uuid_to_char(uuid_create())";
	}

	public String getSequenceNextValString(String tablename) {
		return "select nextval for "
				+ SequenceGenerator.getDefaultSequenceName(tablename);
	}
}