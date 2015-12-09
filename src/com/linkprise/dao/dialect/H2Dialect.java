package com.linkprise.dao.dialect;

import com.linkprise.dao.id.SequenceGenerator;

public class H2Dialect implements Dialect {
	public boolean supportsOffset() {
		return true;
	}

	public String getLimitString(String query, int offset, int limit) {
		StringBuffer sb = new StringBuffer(query.length() + 20);
		sb.append(query);
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
		return "call next value for "
				+ SequenceGenerator.getDefaultSequenceName(tablename);
	}
}