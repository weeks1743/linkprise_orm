package com.linkprise.dao.common.pool;

import com.linkprise.dao.common.DatasourceConfig;
import com.linkprise.utils.Utils;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import java.beans.PropertyVetoException;
import java.sql.SQLException;
import java.util.Map;
import javax.sql.DataSource;

public class C3p0DataSourceCreator implements IDataSourceCreator {
	public DataSource createDatasource(DatasourceConfig datasourcecfg)
			throws SQLException {
		ComboPooledDataSource dataSource = new ComboPooledDataSource();
		try {
			dataSource.setDriverClass(datasourcecfg.getDriverClass());
		} catch (PropertyVetoException e) {
			throw new SQLException(e);
		}
		dataSource.setJdbcUrl(datasourcecfg.getJdbcUrl());
		dataSource.setUser(datasourcecfg.getUsername());
		dataSource.setPassword(datasourcecfg.getPassword());
		Map props = datasourcecfg.getPoolPerperties();
		int checkoutTimeout = Utils.strToInt((String) props
				.get("checkoutTimeout"));
		if (checkoutTimeout <= 0) {
			checkoutTimeout = 5000;
		}
		dataSource.setCheckoutTimeout(checkoutTimeout);
		int initPoolSize = Utils
				.strToInt((String) props.get("initialPoolSize"));
		if (initPoolSize > 0) {
			dataSource.setInitialPoolSize(initPoolSize);
		}
		int minpoolsize = Utils.strToInt((String) props.get("minPoolSize"));
		if (minpoolsize <= 0) {
			minpoolsize = 5;
		}
		dataSource.setMinPoolSize(minpoolsize);
		int maxpoolsize = Utils.strToInt((String) props.get("maxPoolSize"));
		if (maxpoolsize <= 0) {
			maxpoolsize = 100;
		}
		dataSource.setMaxPoolSize(maxpoolsize);
		int acqInc = Utils.strToInt((String) props.get("acquireIncrement"));
		if (acqInc <= 0) {
			acqInc = 5;
		}
		dataSource.setAcquireIncrement(acqInc);
		int maxIdleTime = Utils.strToInt((String) props.get("maxIdleTime"));
		if (maxIdleTime > 0) {
			dataSource.setMaxIdleTime(maxIdleTime);
		}
		int idleConTestPeriod = Utils.strToInt((String) props
				.get("idleConnectionTestPeriod"));
		if (idleConTestPeriod > 0) {
			dataSource.setIdleConnectionTestPeriod(idleConTestPeriod);
		}
		int acqRetryAttempts = Utils.strToInt((String) props
				.get("acquireRetryAttempts"));
		if (acqRetryAttempts <= 0) {
			acqRetryAttempts = 10;
		}
		dataSource.setAcquireRetryAttempts(acqRetryAttempts);
		int acqRetryDelay = Utils.strToInt((String) props
				.get("acquireRetryDelay"));
		if (acqRetryDelay <= 0) {
			acqRetryDelay = 500;
		}
		dataSource.setAcquireRetryDelay(acqRetryDelay);
		return dataSource;
	}
}