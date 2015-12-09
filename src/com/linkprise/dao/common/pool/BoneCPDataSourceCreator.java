package com.linkprise.dao.common.pool;

import com.jolbox.bonecp.BoneCPDataSource;
import com.linkprise.dao.common.DatasourceConfig;
import com.linkprise.utils.Utils;

import java.sql.SQLException;
import java.util.Map;
import javax.sql.DataSource;

public class BoneCPDataSourceCreator implements IDataSourceCreator {
	public DataSource createDatasource(DatasourceConfig datasourcecfg)
			throws SQLException {
		BoneCPDataSource dataSource = new BoneCPDataSource();
		dataSource.setDriverClass(datasourcecfg.getDriverClass());
		dataSource.setJdbcUrl(datasourcecfg.getJdbcUrl());
		dataSource.setUsername(datasourcecfg.getUsername());
		dataSource.setPassword(datasourcecfg.getPassword());
		Map props = datasourcecfg.getPoolPerperties();
		int idleConTestPeriod = Utils.strToInt((String) props
				.get("idleConnectionTestPeriod"));
		if (idleConTestPeriod <= 0) {
			idleConTestPeriod = 1;
		}
		dataSource.setIdleConnectionTestPeriod(idleConTestPeriod);
		int idleMaxAge = Utils.strToInt((String) props.get("idleMaxAge"));
		if (idleMaxAge <= 0) {
			idleMaxAge = 40;
		}
		dataSource.setIdleMaxAge(idleMaxAge);
		int maxConnectionsPerPartition = Utils.strToInt((String) props
				.get("maxConnectionsPerPartition"));
		if (maxConnectionsPerPartition <= 0) {
			maxConnectionsPerPartition = 20;
		}
		dataSource.setMaxConnectionsPerPartition(maxConnectionsPerPartition);
		int minConnectionsPerPartition = Utils.strToInt((String) props
				.get("minConnectionsPerPartition"));
		if (minConnectionsPerPartition <= 0) {
			minConnectionsPerPartition = 5;
		}
		dataSource.setMinConnectionsPerPartition(minConnectionsPerPartition);
		int partitionCount = Utils.strToInt((String) props
				.get("partitionCount"));
		if (partitionCount <= 0) {
			partitionCount = 3;
		}
		dataSource.setPartitionCount(partitionCount);
		int acquireIncrement = Utils.strToInt((String) props
				.get("acquireIncrement"));
		if (acquireIncrement <= 0) {
			acquireIncrement = 2;
		}
		dataSource.setAcquireIncrement(acquireIncrement);
		int acquireRetryAttempts = Utils.strToInt((String) props
				.get("acquireRetryAttempts"));
		if (acquireRetryAttempts <= 0) {
			acquireRetryAttempts = 10;
		}
		dataSource.setAcquireRetryAttempts(acquireRetryAttempts);
		int acquireRetryDelay = Utils.strToInt((String) props
				.get("acquireRetryDelay"));
		if (acquireRetryDelay <= 0) {
			acquireRetryDelay = 500;
		}
		dataSource.setAcquireRetryDelay(acquireRetryDelay);
		int connectionTimeout = Utils.strToInt((String) props
				.get("connectionTimeout"));
		if (connectionTimeout <= 0) {
			connectionTimeout = 5000;
		}
		dataSource.setConnectionTimeout(connectionTimeout);
		int statementsCacheSize = Utils.strToInt((String) props
				.get("statementsCacheSize"));
		if (statementsCacheSize <= 0) {
			statementsCacheSize = 50;
		}
		dataSource.setStatementsCacheSize(statementsCacheSize);
		int releaseHelperThreads = Utils.strToInt((String) props
				.get("releaseHelperThreads"));
		if (releaseHelperThreads <= 0) {
			releaseHelperThreads = 3;
		}
		dataSource.setReleaseHelperThreads(releaseHelperThreads);
		return dataSource;
	}
}