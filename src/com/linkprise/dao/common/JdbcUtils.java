package com.linkprise.dao.common;

import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class JdbcUtils {
	private static final Log logger = LogFactory.getLog(JdbcUtils.class);

	public static void closeConnection(Connection con) {
		if (con != null) {
			try {
				con.close();
			} catch (SQLException ex) {
				logger.debug("Could not close JDBC Connection", ex);
			} catch (Throwable ex) {
				logger.debug("Unexpected exception on closing JDBC Connection",
						ex);
			}
			con = null;
		}
	}

	public static void closeStatement(Statement stmt) {
		if (stmt != null) {
			try {
				stmt.close();
			} catch (SQLException ex) {
				logger.trace("Could not close JDBC Statement", ex);
			} catch (Throwable ex) {
				logger.trace("Unexpected exception on closing JDBC Statement",
						ex);
			}
			stmt = null;
		}
	}

	public static void closeClob(Clob clob) {
		if (clob != null) {
			try {
				clob.free();
			} catch (SQLException e) {
				logger.trace("Could not close Clob", e);
			} catch (Throwable ex) {
				logger.trace("Unexpected exception on closing Clob", ex);
			}
			clob = null;
		}
	}

	public static void closeBlob(Blob blob) {
		if (blob != null) {
			try {
				blob.free();
			} catch (SQLException e) {
				logger.trace("Could not close Blob", e);
			} catch (Throwable ex) {
				logger.trace("Unexpected exception on closing Blob", ex);
			}
			blob = null;
		}
	}

	public static void closeResultSet(ResultSet rs) {
		if (rs != null) {
			try {
				rs.close();
			} catch (SQLException ex) {
				logger.trace("Could not close JDBC ResultSet", ex);
			} catch (Throwable ex) {
				logger.trace("Unexpected exception on closing JDBC ResultSet",
						ex);
			}
			rs = null;
		}
	}
}