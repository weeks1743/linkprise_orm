package com.linkprise.dao.dialect;

public class Ingres9Dialect extends IngresDialect {
	public boolean supportsOffset() {
		return true;
	}

	public String getLimitString(String querySelect, int offset, int limit) {
		StringBuffer soff = new StringBuffer(" offset " + offset);
		StringBuffer slim = new StringBuffer(" fetch first " + limit
				+ " rows only");
		StringBuffer sb = new StringBuffer(querySelect.length() + soff.length()
				+ slim.length()).append(querySelect);
		if (offset > 0) {
			sb.append(soff);
		}
		if (limit > 0) {
			sb.append(slim);
		}
		return sb.toString();
	}
}