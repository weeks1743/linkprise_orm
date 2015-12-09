package com.linkprise.dao.common.pool;

import java.sql.SQLException;
import javax.sql.DataSource;

import com.linkprise.dao.common.DatasourceConfig;

public abstract interface IDataSourceCreator {
	public abstract DataSource createDatasource(
			DatasourceConfig paramDatasourceConfig) throws SQLException;
}