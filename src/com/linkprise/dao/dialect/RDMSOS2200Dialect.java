package com.linkprise.dao.dialect;

public class RDMSOS2200Dialect implements Dialect {
	public boolean supportsOffset() {
		return false;
	}

	public String getLimitString(String query, int offset, int limit) {
		if (offset > 0) {
			throw new UnsupportedOperationException(
					"query result offset is not supported");
		}
		return query.length() + 40 + query + " fetch first " + limit
				+ " rows only ";
	}

	public String getSelectGUIDString() {
		throw new UnsupportedOperationException(getClass().getName()
				+ " does not support GUIDs");
	}

	public String getSequenceNextValString(String tablename) {
		return "select permuted_id('NEXT',31) from rdms.rdms_dummy where key_col = 1 ";
	}
}