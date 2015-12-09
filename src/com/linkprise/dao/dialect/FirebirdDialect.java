package com.linkprise.dao.dialect;

import com.linkprise.dao.id.SequenceGenerator;

public class FirebirdDialect implements Dialect {
	public boolean supportsOffset() {
		return true;
	}

	public String getLimitString(String query, int offset, int limit) {
		return new StringBuffer(query.length() + 20).append(query)
				.insert(6, " first " + limit + " skip " + offset).toString();
	}

	public String getSelectGUIDString() {
		throw new UnsupportedOperationException(getClass().getName()
				+ " does not support GUIDs");
	}

	public String getSequenceNextValString(String tablename) {
		return "select "
				+ getSelectSequenceNextValString(SequenceGenerator
						.getDefaultSequenceName(tablename))
				+ " from RDB$DATABASE";
	}

	public String getSelectSequenceNextValString(String sequenceName) {
		return "gen_id( " + sequenceName + ", 1 )";
	}
}