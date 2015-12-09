package com.linkprise.dao.common;

import java.sql.SQLException;
import java.sql.Statement;

public abstract interface StatementCallback<T> {
	public abstract T doInStatement(Statement paramStatement)
			throws SQLException;
}