package com.linkprise.dao.dialect;

import com.linkprise.dao.id.SequenceGenerator;

public class DerbyDialect implements Dialect {
	public boolean supportsOffset() {
		return true;
	}

	public String getLimitString(String query, int offset, int limit) {
		StringBuffer sb = new StringBuffer(query.length() + 50);

		String normalizedSelect = query.toLowerCase().trim();
		int forUpdateIndex = normalizedSelect.lastIndexOf("for update");

		if (hasForUpdateClause(forUpdateIndex)) {
			sb.append(query.substring(0, forUpdateIndex - 1));
		} else if (hasWithClause(normalizedSelect)) {
			sb.append(query.substring(0, getWithIndex(query) - 1));
		} else {
			sb.append(query);
		}

		if (offset == 0) {
			sb.append(" fetch first ");
		} else {
			sb.append(" offset ").append(offset).append(" rows fetch next ");
		}

		sb.append(limit).append(" rows only");

		if (hasForUpdateClause(forUpdateIndex)) {
			sb.append(' ');
			sb.append(query.substring(forUpdateIndex));
		} else if (hasWithClause(normalizedSelect)) {
			sb.append(' ').append(query.substring(getWithIndex(query)));
		}
		return sb.toString();
	}

	private boolean hasForUpdateClause(int forUpdateIndex) {
		return forUpdateIndex >= 0;
	}

	private boolean hasWithClause(String normalizedSelect) {
		return normalizedSelect.startsWith("with ",
				normalizedSelect.length() - 7);
	}

	private int getWithIndex(String querySelect) {
		int i = querySelect.lastIndexOf("with ");
		if (i < 0) {
			i = querySelect.lastIndexOf("WITH ");
		}
		return i;
	}

	public String getSelectGUIDString() {
		throw new UnsupportedOperationException(getClass().getName()
				+ " does not support GUIDs");
	}

	public String getSequenceNextValString(String tablename) {
		return "values next value for "
				+ SequenceGenerator.getDefaultSequenceName(tablename);
	}
}