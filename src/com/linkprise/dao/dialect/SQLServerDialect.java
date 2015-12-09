package com.linkprise.dao.dialect;

public class SQLServerDialect implements Dialect {
	public boolean supportsOffset() {
		return false;
	}

	public String getLimitString(String query, int offset, int limit) {
		if (offset > 0) {
			throw new UnsupportedOperationException(
					"query result offset is not supported");
		}
		return new StringBuffer(query.length() + 8).append(query)
				.insert(getAfterSelectInsertPoint(query), " top " + limit)
				.toString();
	}

	static int getAfterSelectInsertPoint(String sql) {
		int selectIndex = sql.toLowerCase().indexOf("select");
		int selectDistinctIndex = sql.toLowerCase().indexOf("select distinct");
		return selectIndex + (selectDistinctIndex == selectIndex ? 15 : 6);
	}

	public String getSelectGUIDString() {
		return "select newid()";
	}

	public String getSequenceNextValString(String sequenceName) {
		throw new UnsupportedOperationException(getClass().getName()
				+ " does not support Sequence");
	}
}