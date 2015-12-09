package com.linkprise.dao.common;

public class ValueEscaper {
	private static final String[] escape = { ",", "and ", "exec ", "insert ",
			"select ", "delete ", "update ", "union ", "count", "*", "%",
			"chr", "mid", "master", "truncate", "char", "declare", ";", "or ",
			"+", "--", "'" };

	public static String escapeSqlParameter(String parameter) {
		for (int i = 0; i < escape.length; i++) {
			parameter = parameter.replace(escape[i], "");
		}
		return parameter;
	}
}