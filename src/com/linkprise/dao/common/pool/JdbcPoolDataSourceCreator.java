package com.linkprise.dao.common.pool;

import java.sql.SQLException;
import java.util.Map;
import org.apache.tomcat.jdbc.pool.XADataSource;

import com.linkprise.dao.common.DatasourceConfig;
import com.linkprise.utils.Utils;

public class JdbcPoolDataSourceCreator implements IDataSourceCreator {
	public javax.sql.DataSource createDatasource(DatasourceConfig datasourcecfg)
			throws SQLException {
		javax.sql.DataSource datasource = null;
		Boolean XA = Utils.str2Boolean((String) datasourcecfg
				.getPoolPerperties().get("isXADataSource"));
		if (XA != null)
			datasource = createJdbcPoolDataSource(datasourcecfg,
					XA.booleanValue());
		else {
			datasource = createJdbcPoolDataSource(datasourcecfg, false);
		}
		return datasource;
	}

	private static javax.sql.DataSource createJdbcPoolDataSource(
			DatasourceConfig datasourcecfg, boolean XA) {
		org.apache.tomcat.jdbc.pool.DataSource dataSource = XA ? new XADataSource()
				: new org.apache.tomcat.jdbc.pool.DataSource();
		dataSource.setDriverClassName(datasourcecfg.getDriverClass());
		dataSource.setUrl(datasourcecfg.getJdbcUrl());
		dataSource.setUsername(datasourcecfg.getUsername());
		dataSource.setPassword(datasourcecfg.getPassword());
		Map props = datasourcecfg.getPoolPerperties();
		Boolean defaultAutoCommit = Utils.str2Boolean((String) props
				.get("defaultAutoCommit"));
		if (defaultAutoCommit != null) {
			dataSource.setDefaultAutoCommit(defaultAutoCommit);
		}
		Boolean defaultReadOnly = Utils.str2Boolean((String) props
				.get("defaultReadOnly"));
		if (defaultReadOnly != null) {
			dataSource.setDefaultReadOnly(defaultReadOnly);
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
		int initialSize = Utils.strToInt((String) props.get("initialSize"));
		if (initialSize > 0) {
			dataSource.setInitialSize(initialSize);
		}
		int maxWait = Utils.strToInt((String) props.get("maxWait"));
		if (maxWait > 0) {
			dataSource.setMaxWait(maxWait);
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

		String validationQuery = (String) props.get("validationQuery");
		if ((validationQuery != null) && (validationQuery.trim().length() > 0)) {
			dataSource.setValidationQuery(validationQuery);
		}
		String validatorClassName = (String) props.get("validatorClassName");
		if ((validatorClassName != null)
				&& (validatorClassName.trim().length() > 0)) {
			dataSource.setValidatorClassName(validatorClassName);
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

		Boolean accessToUnderlyingConnectionAllowed = Utils
				.str2Boolean((String) props
						.get("accessToUnderlyingConnectionAllowed"));
		if (accessToUnderlyingConnectionAllowed != null) {
			dataSource
					.setAccessToUnderlyingConnectionAllowed(accessToUnderlyingConnectionAllowed
							.booleanValue());
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

		String connectionProperties = (String) props
				.get("connectionProperties");
		if ((connectionProperties != null)
				&& (connectionProperties.trim().length() > 0)) {
			dataSource.setConnectionProperties(connectionProperties);
		}
		String jdbcInterceptors = (String) props.get("jdbcInterceptors");
		if ((jdbcInterceptors != null)
				&& (jdbcInterceptors.trim().length() > 0)) {
			dataSource.setJdbcInterceptors(jdbcInterceptors);
		}
		Integer validationInterval = Utils.strToInteger((String) props
				.get("validationInterval"));
		if (validationInterval != null) {
			dataSource.setValidationInterval(validationInterval.intValue());
		}
		Boolean jmxEnabled = Utils
				.str2Boolean((String) props.get("jmxEnabled"));
		if (jmxEnabled != null) {
			dataSource.setJmxEnabled(jmxEnabled.booleanValue());
		}
		Boolean fairQueue = Utils.str2Boolean((String) props.get("fairQueue"));
		if (fairQueue != null) {
			dataSource.setFairQueue(fairQueue.booleanValue());
		}
		Integer abandonWhenPercentageFull = Utils.strToInteger((String) props
				.get("abandonWhenPercentageFull"));
		if (abandonWhenPercentageFull != null) {
			dataSource.setAbandonWhenPercentageFull(abandonWhenPercentageFull
					.intValue());
		}
		Integer maxAge = Utils.strToInteger((String) props.get("maxAge"));
		if (maxAge != null) {
			dataSource.setMaxAge(maxAge.intValue());
		}
		Boolean useEquals = Utils.str2Boolean((String) props.get("useEquals"));
		if (useEquals != null) {
			dataSource.setUseEquals(useEquals.booleanValue());
		}
		Integer suspectTimeout = Utils.strToInteger((String) props
				.get("suspectTimeout"));
		if (suspectTimeout != null) {
			dataSource.setSuspectTimeout(suspectTimeout.intValue());
		}
		Boolean alternateUsernameAllowed = Utils.str2Boolean((String) props
				.get("alternateUsernameAllowed"));
		if (alternateUsernameAllowed != null) {
			dataSource.setAlternateUsernameAllowed(alternateUsernameAllowed
					.booleanValue());
		}
		return dataSource;
	}
}