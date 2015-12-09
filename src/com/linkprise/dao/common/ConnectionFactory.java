package com.linkprise.dao.common;

import java.sql.Connection;
import java.sql.SQLException;

public abstract interface ConnectionFactory {
	public abstract Connection openConnection() throws SQLException;

	public abstract DatasourceConfig getConfiguration();
}