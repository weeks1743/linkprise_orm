package com.linkprise.dao.dialect;

import com.linkprise.dao.id.SequenceGenerator;

public class DB2Dialect implements Dialect {
	public boolean supportsOffset() {
		return true;
	}

	public String getLimitString(String sql, int offset, int limit) {
		int startOfSelect = sql.toLowerCase().indexOf("select");

		StringBuffer pagingSelect = new StringBuffer(sql.length() + 100)
				.append(sql.substring(0, startOfSelect))
				.append("select * from ( select ").append(getRowNumber(sql));

		if (hasDistinct(sql)) {
			pagingSelect.append(" row_.* from ( ")
					.append(sql.substring(startOfSelect)).append(" ) as row_");
		} else {
			pagingSelect.append(sql.substring(startOfSelect + 6));
		}

		pagingSelect.append(" ) as temp_ where rownumber_ ");

		if (offset > 0) {
			pagingSelect.append("between ").append(offset).append("+1 and ")
					.append(limit + offset);
		} else {
			pagingSelect.append("<= ").append(limit);
		}

		return pagingSelect.toString();
	}

	private String getRowNumber(String sql) {
		StringBuffer rownumber = new StringBuffer(50)
				.append("rownumber() over(");

		int orderByIndex = sql.toLowerCase().indexOf("order by");

		if ((orderByIndex > 0) && (!hasDistinct(sql))) {
			rownumber.append(sql.substring(orderByIndex));
		}

		rownumber.append(") as rownumber_,");

		return rownumber.toString();
	}

	private static boolean hasDistinct(String sql) {
		return sql.toLowerCase().indexOf("select distinct") >= 0;
	}

	public String getSelectGUIDString() {
		throw new UnsupportedOperationException(getClass().getName()
				+ " does not support GUIDs");
	}

	public String getSequenceNextValString(String tablename) {
		return "values nextval for "
				+ SequenceGenerator.getDefaultSequenceName(tablename);
	}
}