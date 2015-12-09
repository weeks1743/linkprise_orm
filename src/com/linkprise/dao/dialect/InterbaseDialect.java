package com.linkprise.dao.dialect;

import com.linkprise.dao.id.SequenceGenerator;

public class InterbaseDialect implements Dialect {
	public boolean supportsOffset() {
		return true;
	}

	public String getLimitString(String query, int offset, int limit) {
		return query.length() + 15 + query + " rows " + limit + " to " + offset;
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