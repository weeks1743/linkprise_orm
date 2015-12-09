package com.linkprise.dao.common.nativejdbc;

import com.mchange.v2.c3p0.C3P0ProxyConnection;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;

public class C3P0NativeJdbcExtractor implements INativeJdbcExtractor {
	private final Method getRawConnectionMethod;

	public static Connection getRawConnection(Connection con) {
		return con;
	}

	public C3P0NativeJdbcExtractor() {
		try {
			this.getRawConnectionMethod = getClass().getMethod(
					"getRawConnection", new Class[] { Connection.class });
		} catch (NoSuchMethodException ex) {
			throw new IllegalStateException(
					"Internal error in C3P0NativeJdbcExtractor: "
							+ ex.getMessage());
		}
	}

	public Connection doGetNativeConnection(Connection con) throws SQLException {
		if ((con instanceof C3P0ProxyConnection)) {
			C3P0ProxyConnection cpCon = (C3P0ProxyConnection) con;
			try {
				return (Connection) cpCon.rawConnectionOperation(
						this.getRawConnectionMethod, null,
						new Object[] { C3P0ProxyConnection.RAW_CONNECTION });
			} catch (SQLException ex) {
				throw ex;
			} catch (Exception ex) {
				throw new SQLException(ex);
			}
		}
		return con;
	}
}