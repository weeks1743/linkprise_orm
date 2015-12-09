package com.linkprise.dao.common;

import com.alibaba.druid.pool.DruidDataSource;
import com.jolbox.bonecp.BoneCPDataSource;
import com.linkprise.dao.common.pool.BoneCPDataSourceCreator;
import com.linkprise.dao.common.pool.C3p0DataSourceCreator;
import com.linkprise.dao.common.pool.DBCPDataSourceCreator;
import com.linkprise.dao.common.pool.DruidDataSourceCreator;
import com.linkprise.dao.common.pool.IDataSourceCreator;
import com.linkprise.dao.common.pool.JdbcPoolDataSourceCreator;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;
import org.apache.commons.dbcp.BasicDataSource;

public class DefaultConnectionFactory implements ConnectionFactory {
	public static final String _POOL_TYPE_C3P0 = "c3p0";
	public static final String _POOL_TYPE_BONECP = "BoneCP";
	public static final String _POOL_TYPE_DBCP = "DBCP";
	public static final String _POOL_TYPE_JDBC_POOL = "jdbc-pool";
	public static final String _POOL_TYPE_DRUID = "druid";
	private DatasourceConfig config = null;
	private javax.sql.DataSource dataSource = null;

	public DefaultConnectionFactory() {
	}

	public DefaultConnectionFactory(javax.sql.DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public DefaultConnectionFactory(DatasourceConfig config) {
		this.config = config;
	}

	public DefaultConnectionFactory(DatasourceConfig config,
			javax.sql.DataSource dataSource) {
		this.config = config;
		this.dataSource = dataSource;
	}

	public Connection openConnection() throws SQLException {
		Connection con = null;
		Map poolcfg = this.config.getPoolPerperties();
		if (poolcfg != null) {
			String pooltype = (String) poolcfg.get("___POOL_TYPE_");
			if (pooltype != null) {
				if (this.dataSource != null) {
					con = this.dataSource.getConnection();
				} else {
					if (this.config == null) {
						throw new SQLException(
								"can not connection config infomation.");
					}
					IDataSourceCreator creator = getDataSourceCreator(pooltype);
					if (creator == null) {
						throw new SQLException("un support connection pool "
								+ pooltype);
					}
					this.dataSource = creator.createDatasource(this.config);

					con = this.dataSource.getConnection();
				}
			}
		}
		if (con == null) {
			try {
				Class.forName(this.config.getDriverClass());
			} catch (ClassNotFoundException e) {
				throw new SQLException("ClassNotFoundException", e);
			}
			con = DriverManager.getConnection(this.config.getJdbcUrl(),
					this.config.getUsername(), this.config.getPassword());
		}
		return con;
	}

	private IDataSourceCreator getDataSourceCreator(String pooltype) {
		IDataSourceCreator creator = null;
		if ("c3p0".equalsIgnoreCase(pooltype))
			creator = new C3p0DataSourceCreator();
		else if ("BoneCP".equalsIgnoreCase(pooltype))
			creator = new BoneCPDataSourceCreator();
		else if ("DBCP".equalsIgnoreCase(pooltype))
			creator = new DBCPDataSourceCreator();
		else if ("jdbc-pool".equalsIgnoreCase(pooltype))
			creator = new JdbcPoolDataSourceCreator();
		else if ("druid".equalsIgnoreCase(pooltype)) {
			creator = new DruidDataSourceCreator();
		}
		return creator;
	}

	public DatasourceConfig getConfiguration() {
		return this.config;
	}

	public javax.sql.DataSource getDataSource() {
		return this.dataSource;
	}

	public void setDataSource(javax.sql.DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public void setConfig(DatasourceConfig config) {
		this.config = config;
	}

	public String toString() {
		if (this.config != null) {
			return String.format(
					"[%s , %s]",
					new Object[] { this.config.getDriverClass(),
							this.config.getJdbcUrl() });
		}
		return super.toString();
	}

	protected void finalize() throws Throwable {
		if (this.dataSource != null) {
			try {
				String clsName = this.dataSource.getClass().getName();
				if ("com.mchange.v2.c3p0.ComboPooledDataSource".equals(clsName))
					((ComboPooledDataSource) this.dataSource).close(true);
				else if ("com.jolbox.bonecp.BoneCPDataSource".equals(clsName))
					((BoneCPDataSource) this.dataSource).close();
				else if ("org.apache.commons.dbcp.BasicDataSource"
						.equals(clsName))
					((BasicDataSource) this.dataSource).close();
				else if ("org.apache.tomcat.jdbc.pool.DataSource"
						.equals(clsName))
					((org.apache.tomcat.jdbc.pool.DataSource) this.dataSource)
							.close();
				else if ("com.alibaba.druid.pool.DruidDataSource"
						.equals(clsName))
					((DruidDataSource) this.dataSource).close();
			} catch (Exception localException) {
			}
			this.dataSource = null;
		}
		super.finalize();
	}
}