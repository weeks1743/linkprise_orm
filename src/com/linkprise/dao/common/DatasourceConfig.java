package com.linkprise.dao.common;

import java.util.Map;

import com.linkprise.DataBaseType;

public class DatasourceConfig {
	public static final String _POOL_TYPE = "___POOL_TYPE_";
	private DataBaseType databasetype = null;
	private String dialectClass = null;
	private String driverClass;
	private String jdbcUrl;
	private String username;
	private String password;
	private Map<String, String> poolPerperties = null;

	public DatasourceConfig() {
	}

	public DatasourceConfig(String driverClass, String jdbcUrl,
			String username, String password) {
		this.driverClass = driverClass;
		this.jdbcUrl = jdbcUrl;
		this.username = username;
		this.password = password;
	}

	public DatasourceConfig(DataBaseType databasetype, String driverClass,
			String jdbcUrl, String username, String password) {
		this.databasetype = databasetype;
		this.driverClass = driverClass;
		this.jdbcUrl = jdbcUrl;
		this.username = username;
		this.password = password;
	}

	public String getPoolType() {
		if (this.poolPerperties != null) {
			return (String) this.poolPerperties.get("___POOL_TYPE_");
		}
		return null;
	}

	public DataBaseType getDatabasetype() {
		if (this.databasetype == null) {
			this.databasetype = guessDataBaseType(this);
		}
		return this.databasetype;
	}

	public void setDatabasetype(DataBaseType databasetype) {
		this.databasetype = databasetype;
	}

	public String getDriverClass() {
		return this.driverClass;
	}

	public void setDriverClass(String driverClass) {
		this.driverClass = driverClass;
	}

	public String getJdbcUrl() {
		return this.jdbcUrl;
	}

	public void setJdbcUrl(String jdbcUrl) {
		this.jdbcUrl = jdbcUrl;
	}

	public String getUsername() {
		return this.username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getDialectClass() {
		return this.dialectClass;
	}

	public void setDialectClass(String dialectClass) {
		this.dialectClass = dialectClass;
	}

	public Map<String, String> getPoolPerperties() {
		return this.poolPerperties;
	}

	public void setPoolPerperties(Map<String, String> poolPerperties) {
		this.poolPerperties = poolPerperties;
	}

	public static DataBaseType guessDataBaseType(DatasourceConfig datasourcecfg) {
		String dbDriver = datasourcecfg.getDriverClass() == null ? ""
				: datasourcecfg.getDriverClass().toLowerCase();
		if (dbDriver.indexOf("oracledriver") >= 0)
			return DataBaseType.ORACLE;
		if (dbDriver.indexOf("db2driver") >= 0)
			return DataBaseType.DB2;
		if (dbDriver.indexOf("postgresql") >= 0)
			return DataBaseType.PSQL;
		if (dbDriver.indexOf("sqlserverdriver") >= 0)
			return DataBaseType.SQLSERVER;
		if (dbDriver.indexOf("mysql") >= 0)
			return DataBaseType.MYSQL;
		if (dbDriver.indexOf("h2") >= 0) {
			return DataBaseType.H2;
		}

		if (dbDriver.indexOf("hsqldb") >= 0)
			return DataBaseType.HSQL;
		if (dbDriver.indexOf("derby") >= 0)
			return DataBaseType.DERBY;
		if (dbDriver.indexOf("firebirdsql") >= 0)
			return DataBaseType.FIREBIRD;
		if (dbDriver.indexOf("interbase") >= 0)
			return DataBaseType.INTERBASE;
		if (dbDriver.indexOf("informix") >= 0)
			return DataBaseType.INFORMIX;
		if (dbDriver.indexOf("ingres") >= 0)
			return DataBaseType.INGRES10;
		if (dbDriver.indexOf("rdms2200") >= 0)
			return DataBaseType.RDMS2200;
		if (dbDriver.indexOf("timesten") >= 0) {
			return DataBaseType.TIMESTEN;
		}
		return DataBaseType.OTHER;
	}
}