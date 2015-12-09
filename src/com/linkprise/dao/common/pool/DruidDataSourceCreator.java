package com.linkprise.dao.common.pool;

import com.alibaba.druid.pool.DruidDataSource;
import com.linkprise.dao.common.DatasourceConfig;
import com.linkprise.utils.Utils;

import java.sql.SQLException;
import java.util.Map;
import javax.sql.DataSource;

public class DruidDataSourceCreator implements IDataSourceCreator {
	public DataSource createDatasource(DatasourceConfig datasourcecfg)
			throws SQLException {
		DruidDataSource dataSource = new DruidDataSource();
		dataSource.setDriverClassName(datasourcecfg.getDriverClass());
		dataSource.setUrl(datasourcecfg.getJdbcUrl());
		dataSource.setUsername(datasourcecfg.getUsername());
		dataSource.setPassword(datasourcecfg.getPassword());
		Map props = datasourcecfg.getPoolPerperties();
		int initialSize = Utils.strToInt((String) props.get("initialSize"));
		if (initialSize > 0)
			dataSource.setInitialSize(initialSize);
		else {
			dataSource.setInitialSize(1);
		}
		int minIdle = Utils.strToInt((String) props.get("minIdle"));
		if (minIdle > 0)
			dataSource.setMinIdle(minIdle);
		else {
			dataSource.setMinIdle(1);
		}
		int maxActive = Utils.strToInt((String) props.get("maxActive"));
		if (maxActive > 0)
			dataSource.setMaxActive(maxActive);
		else {
			dataSource.setMaxActive(20);
		}

		int maxWait = Utils.strToInt((String) props.get("maxWait"));
		if (maxWait > 0)
			dataSource.setMaxWait(maxWait);
		else {
			dataSource.setMaxWait(60000L);
		}

		long timeBetweenEvictionRunsMillis = Utils.str2long((String) props
				.get("timeBetweenEvictionRunsMillis"));
		if (timeBetweenEvictionRunsMillis > 0L)
			dataSource
					.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis);
		else {
			dataSource.setTimeBetweenEvictionRunsMillis(60000L);
		}

		long minEvictableIdleTimeMillis = Utils.str2long((String) props
				.get("minEvictableIdleTimeMillis"));
		if (minEvictableIdleTimeMillis > 0L) {
			dataSource
					.setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis);
		}
		String validationQuery = (String) props.get("validationQuery");
		if ((validationQuery != null) && (validationQuery.trim().length() > 0))
			dataSource.setValidationQuery(validationQuery);
		else {
			dataSource.setValidationQuery("SELECT 'x'");
		}
		Boolean testWhileIdle = Utils.str2Boolean((String) props
				.get("testWhileIdle"));
		if (testWhileIdle != null) {
			dataSource.setTestWhileIdle(testWhileIdle.booleanValue());
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

		Boolean poolPreparedStatements = Utils.str2Boolean((String) props
				.get("poolPreparedStatements"));
		if (poolPreparedStatements != null) {
			dataSource.setPoolPreparedStatements(poolPreparedStatements
					.booleanValue());
		}
		int maxPoolPreparedStatementPerConnectionSize = Utils
				.strToInt((String) props
						.get("maxPoolPreparedStatementPerConnectionSize"));
		if (maxPoolPreparedStatementPerConnectionSize > 0) {
			dataSource
					.setMaxPoolPreparedStatementPerConnectionSize(maxPoolPreparedStatementPerConnectionSize);
		}

		String filters = (String) props.get("filters");
		if ((filters != null) && (filters.trim().length() > 0)) {
			dataSource.setFilters(filters);
		}
		return dataSource;
	}
}