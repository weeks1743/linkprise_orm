package com.linkprise.dao.transation;

import java.sql.Connection;
import java.sql.SQLException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.linkprise.dao.common.ConnectionFactory;

public class TransactionManager {
	private static final Log logger = LogFactory
			.getLog(TransactionManager.class);

	private static ThreadLocal<Connection> localConnection = new ThreadLocal();

	public static Connection getConnection(ConnectionFactory connectionFactory)
			throws SQLException {
		Connection connection = (Connection) localConnection.get();
		if (connection == null) {
			if (logger.isDebugEnabled()) {
				logger.debug("open connection.");
			}
			connection = connectionFactory.openConnection();
		}
		return connection;
	}

	public static void closeConnection(Connection connection) {
		if (localConnection.get() == null) {
			if (logger.isDebugEnabled())
				logger.debug("close connection.");
			try {
				connection.close();
			} catch (SQLException e) {
				logger.error(e);
			}
			connection = null;
		}
	}

	public static void startManagedConnection(
			ConnectionFactory connectionFactory) throws SQLException {
		if (localConnection.get() == null) {
			if (logger.isDebugEnabled()) {
				logger.debug("beginTransation.");
			}
			Connection connection = getConnection(connectionFactory);
			connection.setAutoCommit(false);
			localConnection.set(connection);
		}
	}

	public static boolean isManagedConnectionStarted() {
		return localConnection.get() != null;
	}

	public static void commit() throws SQLException {
		Connection connection = (Connection) localConnection.get();
		if (connection != null) {
			if (logger.isDebugEnabled()) {
				logger.debug("commitTransation.");
			}
			connection.commit();
		}
	}

	public static void rollback() throws SQLException {
		Connection connection = (Connection) localConnection.get();
		if (connection != null) {
			if (logger.isDebugEnabled()) {
				logger.debug("rollbackTransation.");
			}
			connection.rollback();
		}
	}

	public static void closeManagedConnection() {
		Connection connection = (Connection) localConnection.get();
		if (connection != null) {
			if (logger.isDebugEnabled())
				logger.debug("close connection.");
			try {
				connection.close();
			} catch (Exception e) {
				logger.error(e);
			} finally {
				localConnection.set(null);
			}
		}
	}

	protected void finalize() throws Throwable {
		closeManagedConnection();
		super.finalize();
	}
}