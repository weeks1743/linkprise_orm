package com.linkprise.dao.dialect;

import com.linkprise.dao.id.SequenceGenerator;

public class TimesTenDialect implements Dialect {
	public boolean supportsOffset() {
		return false;
	}

	public String getLimitString(String query, int offset, int limit) {
		if (offset > 0) {
			throw new UnsupportedOperationException(
					"query result offset is not supported");
		}
		return new StringBuffer(query.length() + 8).append(query)
				.insert(6, " first " + limit).toString();
	}

	public String getSelectGUIDString() {
		throw new UnsupportedOperationException(getClass().getName()
				+ " does not support GUIDs");
	}

	public String getSequenceNextValString(String tablename) {
		return "select first 1 "
				+ SequenceGenerator.getDefaultSequenceName(tablename)
				+ ".nextval from sys.tables";
	}
}