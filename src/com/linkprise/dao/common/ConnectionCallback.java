package com.linkprise.dao.common;

import java.sql.Connection;
import java.sql.SQLException;

public abstract interface ConnectionCallback<T> {
	public abstract T doInConnection(Connection paramConnection)
			throws SQLException;
}