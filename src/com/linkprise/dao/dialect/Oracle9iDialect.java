package com.linkprise.dao.dialect;

public class Oracle9iDialect extends Oracle8iDialect {
	public String getLimitString(String sql, int offset, int limit) {
		sql = sql.trim();
		String forUpdateClause = null;
		boolean isForUpdate = false;
		int forUpdateIndex = sql.toLowerCase().lastIndexOf("for update");
		if (forUpdateIndex > -1) {
			forUpdateClause = sql.substring(forUpdateIndex);
			sql = sql.substring(0, forUpdateIndex - 1);
			isForUpdate = true;
		}

		StringBuffer pagingSelect = new StringBuffer(sql.length() + 100);
		if (offset > 0) {
			pagingSelect
					.append("select * from ( select row_.*, rownum rownum_ from ( ");
		} else {
			pagingSelect.append("select * from ( ");
		}
		pagingSelect.append(sql);
		if (offset > 0) {
			pagingSelect.append(" ) row_ where rownum <= ")
					.append(limit + offset).append(") where rownum_ > ")
					.append(offset);
		} else {
			pagingSelect.append(" ) where rownum <= ").append(limit);
		}

		if (isForUpdate) {
			pagingSelect.append(' ');
			pagingSelect.append(forUpdateClause);
		}

		return pagingSelect.toString();
	}
}