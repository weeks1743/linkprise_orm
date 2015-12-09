package com.linkprise.dao.dialect;

public class MySQLDialect implements Dialect {
	public boolean supportsOffset() {
		return true;
	}

	public String getLimitString(String query, int offset, int limit) {
		StringBuffer sb = new StringBuffer(query.length() + 20);
		sb.append(query);
		if (offset > 0)
			sb.append(" limit ").append(offset).append(',').append(limit);
		else {
			sb.append(" limit ").append(limit);
		}
		return sb.toString();
	}

	public String getSelectGUIDString() {
		return "select uuid()";
	}

	public String getSequenceNextValString(String sequenceName) {
		throw new UnsupportedOperationException(getClass().getName()
				+ " does not support Sequence");
	}
}