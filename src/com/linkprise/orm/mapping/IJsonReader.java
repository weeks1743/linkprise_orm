package com.linkprise.orm.mapping;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;

public abstract interface IJsonReader {
	public abstract String read(ResultSet paramResultSet,
			ResultSetMetaData paramResultSetMetaData) throws Exception;
}