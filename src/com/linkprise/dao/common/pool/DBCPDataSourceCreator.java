package com.linkprise.dao.common.pool;

import java.sql.SQLException;
import java.util.Map;
import javax.sql.DataSource;
import org.apache.commons.dbcp.BasicDataSource;

import com.linkprise.dao.common.DatasourceConfig;
import com.linkprise.utils.Utils;

public class DBCPDataSourceCreator implements IDataSourceCreator {
	public DataSource createDatasource(DatasourceConfig datasourcecfg)
			throws SQLException {
		BasicDataSource dataSource = new BasicDataSource();
		dataSource.setDriverClassName(datasourcecfg.getDriverClass());
		dataSource.setUrl(datasourcecfg.getJdbcUrl());
		dataSource.setUsername(datasourcecfg.getUsername());
		dataSource.setPassword(datasourcecfg.getPassword());
		Map props = datasourcecfg.getPoolPerperties();

		String connectionProperties = (String) props
				.get("connectionProperties");
		if ((connectionProperties != null)
				&& (connectionProperties.trim().length() > 0)) {
			dataSource.setConnectionProperties(connectionProperties);
		}
		Boolean defaultAutoCommit = Utils.str2Boolean((String) props
				.get("defaultAutoCommit"));
		if (defaultAutoCommit != null) {
			dataSource.setDefaultAutoCommit(defaultAutoCommit.booleanValue());
		}
		Boolean defaultReadOnly = Utils.str2Boolean((String) props
				.get("defaultReadOnly"));
		if (defaultReadOnly != null) {
			dataSource.setDefaultReadOnly(defaultReadOnly.booleanValue());
		}
		Integer defaultTransactionIsolation = Utils.strToInteger((String) props
				.get("defaultTransactionIsolation"));
		if (defaultTransactionIsolation != null) {
			dataSource
					.setDefaultTransactionIsolation(defaultTransactionIsolation
							.intValue());
		}
		String defaultCatalog = (String) props.get("defaultCatalog");
		if ((defaultCatalog != null) && (defaultCatalog.trim().length() > 0)) {
			dataSource.setDefaultCatalog(defaultCatalog);
		}

		int initialSize = Utils.strToInt((String) props.get("initialSize"));
		if (initialSize > 0) {
			dataSource.setInitialSize(initialSize);
		}
		int maxActive = Utils.strToInt((String) props.get("maxActive"));
		if (maxActive > 0) {
			dataSource.setMaxActive(maxActive);
		}
		int maxIdle = Utils.strToInt((String) props.get("maxIdle"));
		if (maxIdle > 0) {
			dataSource.setMaxIdle(maxIdle);
		}
		int minIdle = Utils.strToInt((String) props.get("minIdle"));
		if (minIdle > 0) {
			dataSource.setMinIdle(minIdle);
		}
		int maxWait = Utils.strToInt((String) props.get("maxWait"));
		if (maxWait > 0) {
			dataSource.setMaxWait(maxWait);
		}

		String validationQuery = (String) props.get("validationQuery");
		if ((validationQuery != null) && (validationQuery.trim().length() > 0)) {
			dataSource.setValidationQuery(validationQuery);
		}
		Integer validationQueryTimeout = Utils.strToInteger((String) props
				.get("validationQueryTimeout"));
		if (validationQueryTimeout != null) {
			dataSource.setValidationQueryTimeout(validationQueryTimeout
					.intValue());
		}
		Boolean testOnBorrow = Utils.str2Boolean((String) props
				.get("testOnBorrow"));
		if (testOnBorrow != null) {
			dataSource.setTestOnBorrow(testOnBorrow.booleanValue());
		}
		Boolean testOnReturn = Utils.str2Boolean((String) props
				.get("testOnReturn"));
		if (testOnReturn != null) {
			dataSource.setTestOnReturn(testOnReturn.booleanValue());
		}
		Boolean testWhileIdle = Utils.str2Boolean((String) props
				.get("testWhileIdle"));
		if (testWhileIdle != null) {
			dataSource.setTestWhileIdle(testWhileIdle.booleanValue());
		}
		Integer timeBetweenEvictionRunsMillis = Utils
				.strToInteger((String) props
						.get("timeBetweenEvictionRunsMillis"));
		if (timeBetweenEvictionRunsMillis != null) {
			dataSource
					.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis
							.intValue());
		}
		Integer numTestsPerEvictionRun = Utils.strToInteger((String) props
				.get("numTestsPerEvictionRun"));
		if (numTestsPerEvictionRun != null) {
			dataSource.setNumTestsPerEvictionRun(numTestsPerEvictionRun
					.intValue());
		}
		int minEvictableIdleTimeMillis = Utils.strToInt((String) props
				.get("minEvictableIdleTimeMillis"));
		if (minEvictableIdleTimeMillis > 0) {
			dataSource
					.setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis);
		}

		Boolean removeAbandoned = Utils.str2Boolean((String) props
				.get("removeAbandoned"));
		if (removeAbandoned != null) {
			dataSource.setRemoveAbandoned(removeAbandoned.booleanValue());
		}
		int removeAbandonedTimeout = Utils.strToInt((String) props
				.get("removeAbandonedTimeout"));
		if (removeAbandonedTimeout > 0) {
			dataSource.setRemoveAbandonedTimeout(removeAbandonedTimeout);
		}
		Boolean logAbandoned = Utils.str2Boolean((String) props
				.get("logAbandoned"));
		if (logAbandoned != null) {
			dataSource.setLogAbandoned(logAbandoned.booleanValue());
		}
		return dataSource;
	}
}