package com.linkprise.dao.dialect;

import com.linkprise.dao.id.SequenceGenerator;

public class Oracle8iDialect implements Dialect {
	public boolean supportsOffset() {
		return true;
	}

	public String getLimitString(String sql, int offset, int limit) {
		sql = sql.trim();
		boolean isForUpdate = false;
		if (sql.toLowerCase().endsWith(" for update")) {
			sql = sql.substring(0, sql.length() - 11);
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
			pagingSelect.append(" ) row_ ) where rownum_ <= ")
					.append(limit + offset).append(" and rownum_ > ")
					.append(offset);
		} else {
			pagingSelect.append(" ) where rownum <= ").append(limit);
		}

		if (isForUpdate) {
			pagingSelect.append(" for update");
		}

		return pagingSelect.toString();
	}

	public String getSelectGUIDString() {
		return "select rawtohex(sys_guid()) from dual";
	}

	public String getSequenceNextValString(String tablename) {
		return "select "
				+ getSelectSequenceNextValString(fixOracleSeqName(tablename))
				+ " from dual";
	}

	public String getSelectSequenceNextValString(String sequenceName) {
		return sequenceName + ".nextval";
	}

	private String fixOracleSeqName(String tablename) {
		String seqname = SequenceGenerator.getDefaultSequenceName(tablename);
		if (seqname.length() > 30) {
			seqname = seqname.substring(seqname.length() - 30);
		}
		return seqname;
	}
}