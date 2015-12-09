package com.linkprise.dao.dialect;

import com.linkprise.dao.id.SequenceGenerator;

public class PostgreSQLDialect implements Dialect {
	public boolean supportsOffset() {
		return true;
	}

	public String getLimitString(String sql, int offset, int limit) {
		StringBuffer sb = new StringBuffer(sql.length() + 20);
		sb.append(sql);
		if (offset > 0)
			sb.append(" limit ").append(limit).append(" offset ")
					.append(offset);
		else {
			sb.append(" limit ").append(limit);
		}
		return sb.toString();
	}

	public String getSelectGUIDString() {
		throw new UnsupportedOperationException(getClass().getName()
				+ " does not support GUIDs");
	}

	public String getSequenceNextValString(String tablename) {
		return "select "
				+ getSelectSequenceNextValString(SequenceGenerator
						.getDefaultSequenceName(tablename));
	}

	public String getSelectSequenceNextValString(String sequenceName) {
		return "nextval ('" + sequenceName + "')";
	}
}