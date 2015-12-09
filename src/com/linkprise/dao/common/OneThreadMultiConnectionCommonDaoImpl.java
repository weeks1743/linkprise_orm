package com.linkprise.dao.common;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.linkprise.dao.transation.OneThreadMultiConnectionTransactionManager;
import com.linkprise.orm.annotation.FieldMapping;
import com.linkprise.orm.convert.GenericConverterFactory;
import com.linkprise.orm.convert.ITypeConverter;
import com.linkprise.orm.mapping.IObjectReader;
import com.linkprise.utils.Assert;

public class OneThreadMultiConnectionCommonDaoImpl extends CommonDaoImpl {
	private static final Log logger = LogFactory
			.getLog(OneThreadMultiConnectionCommonDaoImpl.class);

	/**
	 * 
	 */
	public OneThreadMultiConnectionCommonDaoImpl() {
		super();
	}

	/**
	 * @param connectionFactory
	 * @param objectReader
	 */
	public OneThreadMultiConnectionCommonDaoImpl(
			ConnectionFactory connectionFactory, IObjectReader objectReader) {
		super(connectionFactory, objectReader);
	}

	/**
	 * @param connectionFactory
	 */
	public OneThreadMultiConnectionCommonDaoImpl(
			ConnectionFactory connectionFactory) {
		super(connectionFactory);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.uorm.dao.common.CommonDaoImpl#beginTransation()
	 */
	@Override
	public void beginTransation() throws SQLException {
		OneThreadMultiConnectionTransactionManager
				.startManagedConnection(getConnectionFactory());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.uorm.dao.common.CommonDaoImpl#commitTransation()
	 */
	@Override
	public void commitTransation() throws SQLException {
		try {
			OneThreadMultiConnectionTransactionManager
					.commit(getConnectionFactory());
		} finally {
			OneThreadMultiConnectionTransactionManager
					.closeManagedConnection(getConnectionFactory());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.uorm.dao.common.CommonDaoImpl#rollbackTransation()
	 */
	@Override
	public void rollbackTransation() throws SQLException {
		try {
			OneThreadMultiConnectionTransactionManager
					.rollback(getConnectionFactory());
		} finally {
			OneThreadMultiConnectionTransactionManager
					.closeManagedConnection(getConnectionFactory());
		}
	}

	public <T> T doExecute(ConnectionCallback<T> action) throws SQLException {
		Assert.notNull(action, "Callback object must not be null");
		Connection connection = OneThreadMultiConnectionTransactionManager
				.getConnection(getConnectionFactory());
		try {
			T result = action.doInConnection(connection);
			return result;
		} catch (SQLException ex) {
			throw ex;
		} catch (RuntimeException ex) {
			throw ex;
		} finally {
			OneThreadMultiConnectionTransactionManager.closeConnection(
					connection, getConnectionFactory());
		}
	}

	protected <T> T doExecuteInTransation(ConnectionCallback<T> action)
			throws SQLException {
		Assert.notNull(action, "Callback object must not be null");
		OneThreadMultiConnectionTransactionManager
				.startManagedConnection(getConnectionFactory());
		Connection connection = OneThreadMultiConnectionTransactionManager
				.getConnection(getConnectionFactory());
		try {
			T result = action.doInConnection(connection);
			OneThreadMultiConnectionTransactionManager
					.commit(getConnectionFactory());
			return result;
		} catch (SQLException ex) {
			OneThreadMultiConnectionTransactionManager
					.rollback(getConnectionFactory());
			throw ex;
		} catch (RuntimeException ex) {
			OneThreadMultiConnectionTransactionManager
					.rollback(getConnectionFactory());
			throw ex;
		} finally {
			OneThreadMultiConnectionTransactionManager
					.closeManagedConnection(getConnectionFactory());
		}
	}

	public <T> T doExecute(StatementCallback<T> action) throws SQLException {
		Assert.notNull(action, "Callback object must not be null");
		Connection connection = OneThreadMultiConnectionTransactionManager
				.getConnection(getConnectionFactory());
		Statement stmt = null;
		try {
			stmt = connection.createStatement();
			T result = action.doInStatement(stmt);
			return result;
		} catch (SQLException ex) {
			throw ex;
		} catch (RuntimeException ex) {
			throw ex;
		} finally {
			JdbcUtils.closeStatement(stmt);
			stmt = null;
			OneThreadMultiConnectionTransactionManager.closeConnection(
					connection, getConnectionFactory());
		}
	}

	protected <T> T doExecute(StatementCallback<T> action, String sql,
			final Class<?> paramClass, SqlParameter... params)
			throws SQLException {
		Assert.notNull(action, "Callback object must not be null");
		Assert.notNull(sql, "sql must not be null");
		Connection connection = OneThreadMultiConnectionTransactionManager
				.getConnection(getConnectionFactory());
		PreparedStatement stmt = null;
		try {
			if (logger.isDebugEnabled()) {
				if (params != null && params.length > 0) {
					logger.debug("Executing SQL statement [" + sql
							+ "] values: " + Arrays.asList(params));
				} else {
					logger.debug("Executing SQL statement [" + sql + "]");
				}
			}
			stmt = connection.prepareStatement(sql);
			int idx = 1;
			for (SqlParameter param : params) {
				int sqltype = 12;
				Map<String, FieldMapping> fieldMappings = null;
				if (param.getOrmClass() != null) {
					fieldMappings = getObjectReader().getObjectFieldMap(
							param.getOrmClass());
				} else if (paramClass != null) {
					fieldMappings = getObjectReader().getObjectFieldMap(
							paramClass);
				}
				if (fieldMappings != null) {
					FieldMapping field = fieldMappings.get(param.getName());
					if (field == null) {
						// find by table column's name
						for (FieldMapping fieldl : fieldMappings.values()) {
							if (fieldl.columnName().equalsIgnoreCase(
									param.getName())) {
								sqltype = fieldl.columnType();
								break;
							}
						}
					} else {
						sqltype = field.columnType();
					}

					Object val = param.getValue();
					if (val != null) {
						Class<?> targetsqlcls = getObjectReader()
								.getTargetSqlClass(sqltype);
						if (targetsqlcls != null) {
							Class<?> srcCls = val.getClass();
							if (GenericConverterFactory.getInstance()
									.needConvert(srcCls, targetsqlcls)) {
								ITypeConverter converter = GenericConverterFactory
										.getInstance().getSqlConverter();// .getConverter(srcCls,
																			// targetsqlcls);
								if (converter != null) {
									val = converter.convert(val, targetsqlcls);
								}
							}
						}
					}
					// stmt.setObject(idx, val, sqltype);
					// fix java.sql.SQLException: Unknown Types value
					stmt.setObject(idx, val);
				} else {
					stmt.setObject(idx, param.getValue());
				}
				idx++;
			}
			T result = action.doInStatement(stmt);
			return result;
		} catch (SQLException ex) {
			throw ex;
		} catch (Exception e) {
			throw new SQLException(e);
		} finally {
			JdbcUtils.closeStatement(stmt);
			stmt = null;
			OneThreadMultiConnectionTransactionManager.closeConnection(
					connection, getConnectionFactory());
		}
	}

	protected <T> T doExecuteInTransation(StatementCallback<T> action)
			throws SQLException {
		Assert.notNull(action, "Callback object must not be null");
		OneThreadMultiConnectionTransactionManager
				.startManagedConnection(getConnectionFactory());
		Connection connection = OneThreadMultiConnectionTransactionManager
				.getConnection(getConnectionFactory());
		Statement stmt = null;
		try {
			stmt = connection.createStatement();
			T result = action.doInStatement(stmt);
			OneThreadMultiConnectionTransactionManager
					.commit(getConnectionFactory());
			return result;
		} catch (SQLException ex) {
			OneThreadMultiConnectionTransactionManager
					.rollback(getConnectionFactory());
			throw ex;
		} catch (RuntimeException ex) {
			OneThreadMultiConnectionTransactionManager
					.rollback(getConnectionFactory());
			throw ex;
		} finally {
			JdbcUtils.closeStatement(stmt);
			stmt = null;
			OneThreadMultiConnectionTransactionManager
					.closeManagedConnection(getConnectionFactory());
		}
	}

	protected <T> T doExecuteInTransation(StatementCallback<T> action,
			String sql, final Class<T> paramClass, SqlParameter... params)
			throws SQLException {
		Assert.notNull(action, "Callback object must not be null");
		Assert.notNull(sql, "sql must not be null");
		OneThreadMultiConnectionTransactionManager
				.startManagedConnection(getConnectionFactory());
		Connection connection = OneThreadMultiConnectionTransactionManager
				.getConnection(getConnectionFactory());
		PreparedStatement stmt = null;
		try {
			if (logger.isDebugEnabled()) {
				if (params != null && params.length > 0) {
					logger.debug("Executing SQL statement [" + sql
							+ "] values: " + Arrays.asList(params));
				} else {
					logger.debug("Executing SQL statement [" + sql + "]");
				}
			}
			stmt = connection.prepareStatement(sql);
			int idx = 1;
			for (SqlParameter param : params) {
				int sqltype = 12;
				Map<String, FieldMapping> fieldMappings = null;
				if (param.getOrmClass() != null) {
					fieldMappings = getObjectReader().getObjectFieldMap(
							param.getOrmClass());
				} else if (paramClass != null) {
					fieldMappings = getObjectReader().getObjectFieldMap(
							paramClass);
				}
				if (fieldMappings != null) {
					FieldMapping field = fieldMappings.get(param.getName());
					if (field == null) {
						// find by table column's name
						for (FieldMapping fieldl : fieldMappings.values()) {
							if (fieldl.columnName().equalsIgnoreCase(
									param.getName())) {
								sqltype = fieldl.columnType();
								break;
							}
						}
					} else {
						sqltype = field.columnType();
					}

					Object val = param.getValue();
					if (val != null) {
						Class<?> targetsqlcls = getObjectReader()
								.getTargetSqlClass(sqltype);
						if (targetsqlcls != null) {
							Class<?> srcCls = val.getClass();
							if (GenericConverterFactory.getInstance()
									.needConvert(srcCls, targetsqlcls)) {
								ITypeConverter converter = GenericConverterFactory
										.getInstance().getSqlConverter();// .getConverter(srcCls,
																			// targetsqlcls);
								if (converter != null) {
									val = converter.convert(val, targetsqlcls);
								}
							}
						}
					}
					// stmt.setObject(idx, val, sqltype);
					// fix java.sql.SQLException: Unknown Types value
					stmt.setObject(idx, val);
				} else {
					stmt.setObject(idx, param.getValue());
				}
				idx++;
			}
			T result = action.doInStatement(stmt);
			OneThreadMultiConnectionTransactionManager
					.commit(getConnectionFactory());
			return result;
		} catch (SQLException ex) {
			OneThreadMultiConnectionTransactionManager
					.rollback(getConnectionFactory());
			throw ex;
		} catch (Exception ex) {
			OneThreadMultiConnectionTransactionManager
					.rollback(getConnectionFactory());
			throw new SQLException(ex);
		} finally {
			JdbcUtils.closeStatement(stmt);
			stmt = null;
			OneThreadMultiConnectionTransactionManager
					.closeManagedConnection(getConnectionFactory());
		}
	}

}