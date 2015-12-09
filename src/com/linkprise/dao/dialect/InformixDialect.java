package com.linkprise.dao.dialect;

import com.linkprise.dao.id.SequenceGenerator;

public class InformixDialect implements Dialect {
	public boolean supportsOffset() {
		return true;
	}

	public String getLimitString(String query, int offset, int limit) {
		if (offset > 0) {
			throw new UnsupportedOperationException(
					"query result offset is not supported");
		}
		return new StringBuffer(query.length() + 8)
				.append(query)
				.insert(query.toLowerCase().indexOf("select") + 6,
						" first " + limit).toString();
	}

	public String getSelectGUIDString() {
		throw new UnsupportedOperationException(getClass().getName()
				+ " does not support GUIDs");
	}

	public String getSequenceNextValString(String tablename) {
		return "select "
				+ getSelectSequenceNextValString(SequenceGenerator
						.getDefaultSequenceName(tablename))
				+ " from informix.systables where tabid=1";
	}

	public String getSelectSequenceNextValString(String sequenceName) {
		return sequenceName + ".nextval";
	}
}