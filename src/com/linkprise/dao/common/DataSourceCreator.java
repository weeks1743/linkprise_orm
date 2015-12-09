package com.linkprise.dao.common;

import com.jolbox.bonecp.BoneCPDataSource;
import com.linkprise.utils.Utils;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import java.beans.PropertyVetoException;
import java.sql.SQLException;
import java.util.Map;
import org.apache.commons.dbcp.BasicDataSource;
import org.apache.tomcat.jdbc.pool.XADataSource;

public class DataSourceCreator {
	public static javax.sql.DataSource createDatasource(String pooltype,
			DatasourceConfig datasourcecfg) throws SQLException {
		return genPoolDataSource(pooltype, datasourcecfg);
	}

	private static javax.sql.DataSource genPoolDataSource(String pooltype,
			DatasourceConfig datasourcecfg) throws SQLException {
		javax.sql.DataSource datasource = null;
		if ("c3p0".equalsIgnoreCase(pooltype)) {
			datasource = createC3p0DataSource(datasourcecfg);
		} else if ("BoneCP".equalsIgnoreCase(pooltype)) {
			datasource = createBoneCPDataSource(datasourcecfg);
		} else if ("DBCP".equalsIgnoreCase(pooltype)) {
			datasource = createDBCPDataSource(datasourcecfg);
		} else if ("jdbc-pool".equalsIgnoreCase(pooltype)) {
			Boolean XA = Utils.str2Boolean((String) datasourcecfg
					.getPoolPerperties().get("isXADataSource"));
			if (XA != null)
				datasource = createJdbcPoolDataSource(datasourcecfg,
						XA.booleanValue());
			else {
				datasource = createJdbcPoolDataSource(datasourcecfg, false);
			}
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

	private static javax.sql.DataSource createDBCPDataSource(
			DatasourceConfig datasourcecfg) {
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

	private static javax.sql.DataSource createBoneCPDataSource(
			DatasourceConfig datasourcecfg) {
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

	private static javax.sql.DataSource createC3p0DataSource(
			DatasourceConfig datasourcecfg) throws SQLException {
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