package com.linkprise.dao.transation;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.linkprise.dao.common.ConnectionFactory;
import com.linkprise.dao.common.DatasourceConfig;

public class OneThreadMultiConnectionTransactionManager {
	private static final Log logger = LogFactory
			.getLog(OneThreadMultiConnectionTransactionManager.class);

	private static Map<String, ThreadLocal<Connection>> localConnectionMap = new ConcurrentHashMap();

	public static Connection getConnection(ConnectionFactory connectionFactory)
			throws SQLException {
		Connection connection = null;
		ThreadLocal localConnection = (ThreadLocal) localConnectionMap
				.get(connectionFactory.toString());
		if (localConnection == null) {
			if (logger.isDebugEnabled()) {
				logger.debug(connectionFactory.getConfiguration().getJdbcUrl()
						+ ", open connection.");
			}
			connection = connectionFactory.openConnection();
		} else {
			connection = (Connection) localConnection.get();
			if (connection == null) {
				if (logger.isDebugEnabled()) {
					logger.debug(connectionFactory.getConfiguration()
							.getJdbcUrl() + ", open connection.");
				}
				connection = connectionFactory.openConnection();
			}
		}
		return connection;
	}

	public static void closeConnection(Connection connection,
			ConnectionFactory connectionFactory) {
		ThreadLocal localConnection = (ThreadLocal) localConnectionMap
				.get(connectionFactory.toString());
		if ((localConnection == null) || (localConnection.get() == null)) {
			if (logger.isDebugEnabled())
				logger.debug(connectionFactory.getConfiguration().getJdbcUrl()
						+ ", close connection.");
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
		ThreadLocal localConnection = (ThreadLocal) localConnectionMap
				.get(connectionFactory.toString());
		if ((localConnection == null) || (localConnection.get() == null)) {
			if (logger.isDebugEnabled()) {
				logger.debug(connectionFactory.getConfiguration().getJdbcUrl()
						+ ", beginTransation.");
			}
			Connection connection = getConnection(connectionFactory);
			connection.setAutoCommit(false);
			if (localConnection == null) {
				localConnection = new ThreadLocal();
				localConnection.set(connection);
				localConnectionMap.put(connectionFactory.toString(),
						localConnection);
			} else {
				localConnection.set(connection);
			}
		}
	}

	public static boolean isManagedConnectionStarted(
			ConnectionFactory connectionFactory) {
		ThreadLocal localConnection = (ThreadLocal) localConnectionMap
				.get(connectionFactory.toString());
		if (localConnection == null) {
			return false;
		}
		return localConnection.get() != null;
	}

	public static void commit(ConnectionFactory connectionFactory)
			throws SQLException {
		ThreadLocal localConnection = (ThreadLocal) localConnectionMap
				.get(connectionFactory.toString());
		if (localConnection != null) {
			Connection connection = (Connection) localConnection.get();
			if (connection != null) {
				if (logger.isDebugEnabled()) {
					logger.debug(connectionFactory.getConfiguration()
							.getJdbcUrl() + ", commitTransation.");
				}
				connection.commit();
			}
		}
	}

	public static void rollback(ConnectionFactory connectionFactory)
			throws SQLException {
		ThreadLocal localConnection = (ThreadLocal) localConnectionMap
				.get(connectionFactory.toString());
		if (localConnection != null) {
			Connection connection = (Connection) localConnection.get();
			if (connection != null) {
				if (logger.isDebugEnabled()) {
					logger.debug(connectionFactory.getConfiguration()
							.getJdbcUrl() + ", rollbackTransation.");
				}
				connection.rollback();
			}
		}
	}

	public static void closeManagedConnection(
			ConnectionFactory connectionFactory) {
		ThreadLocal localConnection = (ThreadLocal) localConnectionMap
				.get(connectionFactory.toString());
		if (localConnection != null) {
			Connection connection = (Connection) localConnection.get();
			if (connection != null) {
				if (logger.isDebugEnabled())
					logger.debug(connectionFactory.getConfiguration()
							.getJdbcUrl() + ", close connection.");
				try {
					connection.close();
				} catch (Exception e) {
					logger.error(e);
				} finally {
					localConnection.set(null);
				}
			}
		}
	}

	private void closeManagedConnection() {
		for (ThreadLocal localConnection : localConnectionMap.values()) {
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
		localConnectionMap.clear();
		localConnectionMap = null;
	}

	protected void finalize() throws Throwable {
		closeManagedConnection();
		super.finalize();
	}
}