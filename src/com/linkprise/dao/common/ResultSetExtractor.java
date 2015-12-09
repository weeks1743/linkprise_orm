package com.linkprise.dao.common;

import java.sql.ResultSet;
import java.sql.SQLException;

public abstract interface ResultSetExtractor<T> {
	public abstract T extractData(ResultSet paramResultSet) throws SQLException;
}