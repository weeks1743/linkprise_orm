package com.linkprise.dao.common;

import java.io.InputStreamReader;
import java.io.Serializable;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.linkprise.dao.dialect.Dialect;
import com.linkprise.dao.id.IdentifierGenerator;
import com.linkprise.dao.id.IdentifierGeneratorFactory;
import com.linkprise.dao.transation.TransactionManager;
import com.linkprise.orm.annotation.ClassMapping;
import com.linkprise.orm.annotation.FieldMapping;
import com.linkprise.orm.annotation.KeyGenertator;
import com.linkprise.orm.convert.GenericConverterFactory;
import com.linkprise.orm.convert.ITypeConverter;
import com.linkprise.orm.mapping.AsciiStream;
import com.linkprise.orm.mapping.IObjectReader;
import com.linkprise.utils.Assert;

public class CommonDaoImpl extends JdbcTemplate implements ICommonDao {
	private static final Log logger = LogFactory.getLog(CommonDaoImpl.class);
	public static final int _BATCH_SIZE = 50;
	private int batchSize = 50;
	
	// list的子级
	private static List list4Val = Arrays.asList(new String[]{"java.util.AbstractList", "java.util.AbstractSequentialList", "java.util.ArrayList", "java.util.AttributeList", "java.util.CopyOnWriteArrayList", "java.util.LinkedList", "java.util.RoleList", "java.util.RoleUnresolvedList", "java.util.Stack", "java.util.Vector" }); 

	
	public CommonDaoImpl() {
	}

	public CommonDaoImpl(ConnectionFactory connectionFactory) {
		super(connectionFactory);
	}

	public CommonDaoImpl(ConnectionFactory connectionFactory,
			IObjectReader objectReader) {
		super(connectionFactory, objectReader);
	}

	public void beginTransation() throws SQLException {
		TransactionManager.startManagedConnection(getConnectionFactory());
	}

	public void commitTransation() throws SQLException {
		try {
			TransactionManager.commit();
		} finally {
			TransactionManager.closeManagedConnection();
		}
	}

	public void rollbackTransation() throws SQLException {
		try {
			TransactionManager.rollback();
		} finally {
			TransactionManager.closeManagedConnection();
		}
	}

	public <T> T queryBusinessObjByPk(Class<T> cls, Serializable[] pkvals)
			throws SQLException {
		Assert.notEmpty(pkvals);
		ClassMapping clsmapping = getObjectReader().getClassMapping(cls);
		if ((pkvals.length > 1) && (clsmapping == null)) {
			throw new IllegalArgumentException(
					"too many primary key values, can not find the key order.");
		}
		StringBuffer sql = new StringBuffer("SELECT * FROM ");
		String tableName = null;
		if (clsmapping != null) {
			tableName = clsmapping.tableName();
		}
		if (tableName == null) {
			tableName = cls.getSimpleName().toUpperCase();
		}
		sql.append(tableName).append(" WHERE ");
		FieldMapping[] pkfields = getObjectReader().getClassPrimaryKeys(cls,
				clsmapping.keyOrder());
		if (pkfields == null) {
			throw new IllegalArgumentException(
					"can not find the primary key infomation.");
		}
		int idx = 0;
		SqlParameter[] params = new SqlParameter[pkfields.length];
		for (FieldMapping field : pkfields) {
			params[idx] = new SqlParameter(field.columnName(), pkvals[idx]);
			if (idx == 0)
				sql.append(field.columnName()).append(" = ?");
			else {
				sql.append(" and ").append(field.columnName()).append(" = ?");
			}
			idx++;
		}
		return queryForObject(cls, sql.toString(), params);
	}

	public <T> List<T> queryBusinessObjs(Class<T> cls, String query,
			SqlParameter[] params) throws SQLException {
		return queryForList(cls, query, params);
	}

	private <T> List<T> queryBusinessObjs(Class<T> cls, String query,
			int offset, SqlParameter[] params) throws SQLException {
		if (offset > 0) {
			RowsResultSetExtractor rse = new RowsResultSetExtractor(
					getObjectReader(), cls);
			rse.setOffset(offset);
			return (List) query(query, rse, params);
		}
		return queryForList(cls, query, params);
	}

	public <T> List<T> queryAllBusinessObjs(Class<T> cls) throws SQLException {
		ClassMapping clsmapping = getObjectReader().getClassMapping(cls);
		StringBuffer sql = new StringBuffer("SELECT * FROM ");
		String tableName = null;
		if (clsmapping != null) {
			tableName = clsmapping.tableName();
		}
		if (tableName == null) {
			tableName = cls.getSimpleName().toUpperCase();
		}
		sql.append(tableName);
		return queryForList(cls, sql.toString(), new SqlParameter[0]);
	}

	public List<Map<String, Object>> queryForListMap(String sql,
			SqlParameter[] params) throws SQLException {
		return queryForList(sql, params);
	}

	public <T> T querySingleObject(Class<T> cls, String sql,
			SqlParameter[] params) throws SQLException {
		return queryForObject(cls, sql, params);
	}

	public <T> List<T> queryBusinessObjs(Class<T> cls, String sql,
			int startRecord, int maxRecord, SqlParameter[] params)
			throws SQLException {
		Assert.notNull(sql, "sql can not be null");
		if (getDialect() == null) {
			throw new SQLException("can not find SQL Dialect.");
		}
		List items = null;
		String sqlforLimit = null;
		if (getDialect().supportsOffset()) {
			sqlforLimit = getDialect().getLimitString(sql, startRecord,
					maxRecord);
			items = queryBusinessObjs(cls, sqlforLimit, params);
		} else {
			sqlforLimit = getDialect().getLimitString(sql, 0,
					startRecord + maxRecord);
			items = queryBusinessObjs(cls, sqlforLimit, startRecord, params);
		}
		return items;
	}

	public <T> PaginationSupport<T> queryByPagedQuery(Class<T> cls, String sql,
			int startPage, int pageSize, SqlParameter[] params)
			throws SQLException {
		Assert.notNull(sql, "sql can not be null");

		String coutsql = "SELECT COUNT(0) " + removeSelect(sql);
		Long totalCount = (Long) queryForObject(Long.class, coutsql, params);
		if (totalCount != null) {
			int pagecount = (int) Math.ceil(totalCount.longValue() / pageSize);
			int curpage = startPage;
			if (curpage >= pagecount)
				curpage = pagecount - 1;
			int startRecord = curpage >= 0 ? curpage * pageSize : 0;
			List items = null;
			if (0L == totalCount.longValue()) {
				items = new ArrayList(0);
			} else {
				if (getDialect() == null) {
					throw new SQLException("can not find SQL Dialect.");
				}
				String sqlforLimit = null;
				if (getDialect().supportsOffset()) {
					sqlforLimit = getDialect().getLimitString(sql, startRecord,
							pageSize);
					items = queryBusinessObjs(cls, sqlforLimit, params);
				} else {
					int endRecord = (curpage + 1) * pageSize;
					if (endRecord > totalCount.intValue()) {
						endRecord = totalCount.intValue();
					}
					sqlforLimit = getDialect()
							.getLimitString(sql, 0, endRecord);
					items = queryBusinessObjs(cls, sqlforLimit, startRecord,
							params);
				}
			}
			PaginationSupport ps = new PaginationSupport(pageSize,
					totalCount.longValue(), curpage, items);
			return ps;
		}
		return null;
	}

	protected static String removeSelect(String sql) {
		int beginPos = sql.toLowerCase().indexOf("from");
		return sql.substring(beginPos);
	}

	public int deleteBusiness(Class<?> cls, Serializable[] pkvals)
			throws SQLException {
		Assert.notEmpty(pkvals, "pkvals must not null or empty!");
		ClassMapping clsmapping = getObjectReader().getClassMapping(cls);
		if ((pkvals.length > 1) && (clsmapping == null)) {
			throw new IllegalArgumentException(
					"too many primary key values, can not find the key order.");
		}
		StringBuffer sql = new StringBuffer("DELETE FROM ");
		String tableName = null;
		if (clsmapping != null) {
			tableName = clsmapping.tableName();
		}
		if (tableName == null) {
			tableName = cls.getSimpleName().toUpperCase();
		}
		sql.append(tableName).append(" WHERE ");
		FieldMapping[] pkfields = getObjectReader().getClassPrimaryKeys(cls,
				clsmapping.keyOrder());
		if (pkfields == null) {
			throw new IllegalArgumentException(
					"can not find the primary key infomation.");
		}
		int idx = 0;
		SqlParameter[] params = new SqlParameter[pkfields.length];
		for (FieldMapping field : pkfields) {
			params[idx] = new SqlParameter(field.columnName(), pkvals[idx]);
			if (idx == 0)
				sql.append(field.columnName()).append(" = ?");
			else {
				sql.append(" and ").append(field.columnName()).append(" = ?");
			}
			idx++;
		}
		return update(sql.toString(), params);
	}

	public int deleteBusiness(Object[] pojos) throws SQLException {
		Assert.notEmpty(pojos, "pojos can not be null.");
		if (pojos.length == 1) {
			Assert.notNull(pojos[0]);
			UpdateSqlInfo deleteSqlinfo = generateDeleteSql(pojos[0].getClass());
			List<FieldMapping> parameterMappings = deleteSqlinfo
					.getParameterMappings();
			SqlParameter[] params = new SqlParameter[parameterMappings.size()];
			Map<String, Object> objvalMap = null;
			try {
				objvalMap = this.getObjectReader().readValue2Map(pojos[0]);
			} catch (Exception e) {
				throw new SQLException(e);
			}
			for (int i = 0; i < parameterMappings.size(); i++) {
				FieldMapping field = parameterMappings.get(i);
				params[i] = new SqlParameter(field.columnName(),
						(Serializable) objvalMap.get(field.columnName()));
			}
			return update(deleteSqlinfo.getSql(), params);
		} else {
			Set<Class<?>> clsSet = new HashSet<Class<?>>();
			for (Object pojo : pojos) {
				clsSet.add(pojo.getClass());
			}
			Map<Class<?>, UpdateSqlInfo> sqlMap = new HashMap<Class<?>, UpdateSqlInfo>();
			for (Class<?> cls : clsSet) {
				sqlMap.put(cls, generateDeleteSql(cls));
			}
			int rtnval = 0;
			for (Object pojo : pojos) {
				UpdateSqlInfo deleteSqlinfo = sqlMap.get(pojo.getClass());
				List<FieldMapping> parameterMappings = deleteSqlinfo
						.getParameterMappings();
				SqlParameter[] params = new SqlParameter[parameterMappings
						.size()];
				Map<String, Object> objvalMap = null;
				try {
					objvalMap = this.getObjectReader().readValue2Map(pojo);
				} catch (Exception e) {
					throw new SQLException(e);
				}
				for (int i = 0; i < parameterMappings.size(); i++) {
					FieldMapping field = parameterMappings.get(i);
					params[i] = new SqlParameter(field.columnName(),
							(Serializable) objvalMap.get(field.columnName()));
				}
				rtnval += update(deleteSqlinfo.getSql(), params);
			}
			return rtnval;
		}
	}

	public int deleteBusinessCol(Collection<? extends Serializable> pojos)
			throws SQLException {
		if ((pojos != null) && (!pojos.isEmpty())) {
			return deleteBusiness(pojos.toArray());
		}
		return 0;
	}

	public int saveBusinessObjs(Object... pojos) throws SQLException {
		Assert.notEmpty(pojos, "pojos can not be null.");
		
		boolean isList = false;
		
		// 增加List判断，pojos可以直接传递实体对象
		if(list4Val.contains(pojos.getClass())){
			isList = true;
		}
		
		if(isList){
			List listInner = (List)pojos[0];
		
			//logger.debug(pojos);
			//logger.debug(((List)pojos[0]).get(0));
			//logger.debug(((List)pojos[0]).get(0).getClass());
			
			// List  = pojos[0] 第一个为整个集合对象
			// pojos[1,2,3,4] = [Book@f1、Book@f2]
			
			if (listInner.size() == 1) {
				//String sql = generateInsertSql(pojos[0].getClass());
				String sql = generateInsertSql(listInner.get(0).getClass());
				if (autoManagerTransaction)
					beginTransation();
				try {
					int rtn = saveBusinessObjs(listInner.get(0), sql);
					if (autoManagerTransaction)
						commitTransation();
					return rtn;
				} catch (SQLException e) {
					if (autoManagerTransaction)
						rollbackTransation();
					throw e;
				} catch (Exception e) {
					if (autoManagerTransaction)
						rollbackTransation();
					throw new SQLException(e);
				}
			}
			int rtnval = 0;
			if (autoManagerTransaction)
				beginTransation();
			try {
				String sql = generateInsertSql(listInner.get(0).getClass());
				rtnval = batchSaveBusinessObjs(sql, listInner).intValue();
				if (autoManagerTransaction)
					commitTransation();
				return rtnval;
			} catch (SQLException e) {
				if (autoManagerTransaction)
					rollbackTransation();
				throw e;
			} catch (Exception e) {
				if (autoManagerTransaction)
					rollbackTransation();
				throw new SQLException(e);
			}
		}else{
			String sql = generateInsertSql(pojos.getClass());
			if (autoManagerTransaction)
				beginTransation();
			try {
				int rtn = saveBusinessObjs(pojos, sql);
				if (autoManagerTransaction)
					commitTransation();
				return rtn;
			} catch (SQLException e) {
				if (autoManagerTransaction)
					rollbackTransation();
				throw e;
			} catch (Exception e) {
				if (autoManagerTransaction)
					rollbackTransation();
				throw new SQLException(e);
			}
		}
	}

	private Integer batchSaveBusinessObjs(final String insertsql,
			final Object... pojos) throws Exception {
		
		final List listInner = (List)pojos[0];
		
		Class<?> cls = listInner.get(0).getClass();
		final Map<String, FieldMapping> fieldMappings = getObjectReader()
				.getObjectFieldMap(cls);
		final ClassMapping classMapping = getObjectReader()
				.getClassMapping(cls);
		final ICommonDao dao = this;
		String keyGenerator = null;
		if (classMapping != null) {
			keyGenerator = classMapping.keyGenerator();
		} else {
			keyGenerator = KeyGenertator.ASSIGNED;
		}
		final IdentifierGenerator idgenerator = getIdentifierGeneratorFactory()
				.createIdentifierGenerator(keyGenerator);
		if (keyGenerator.equals(KeyGenertator.SELECT)) {
			// if select deal primary key val first, becuase need execute update
			// sql
			for (FieldMapping field : fieldMappings.values()) {
				if (field.primary()) {
					Map<String, Object> objValMap = getObjectReader()
							.readValue2Map(listInner.get(0));
					Object val = objValMap.get(field.columnName());
					if (val == null) {
						idgenerator.generate(getDialect(), dao, listInner, field,
								true);
					}
					break;
				}
			}
		}
		Integer vals = doExecute(new ConnectionCallback<Integer>() {

			@Override
			public Integer doInConnection(Connection connection)
					throws SQLException {
				PreparedStatement stmt = null;
				List<Clob> clobs = new ArrayList<Clob>();
				List<Blob> blobs = new ArrayList<Blob>();
				int rtnval = 0;
				try {
					// if(logger.isDebugEnabled()){
					// logger.debug("Batch Executing SQL [" + insertsql + "]");
					// }
					int batchsize = 0;
					stmt = connection.prepareStatement(insertsql);
					Serializable[] pkvals = null;
					int ik = 0;
					for (Object pojo : listInner) {
						Map<String, Object> objValMap = getObjectReader()
								.readValue2Map(pojo);
						int idx = 1;
						for (FieldMapping field : fieldMappings.values()) {
							if (field.includeInWrites()) {
								if ((field.primary())
										&& (classMapping != null)
										&& (KeyGenertator.NATIVE
												.equals(classMapping
														.keyGenerator()))) {
									// if native
									continue;
								}
								Object val = objValMap.get(field.columnName());
								if (val == null && field.primary()) {
									// if KeyGenertator not native
									if (pkvals == null) {
										pkvals = idgenerator.generate(
												getDialect(), dao, listInner.toArray(),
												field, true);
										if (pkvals == null) {
											pkvals = new Serializable[listInner.size()];
										}
									}
									val = pkvals[ik];
								}
								int columntype = field.columnType();
								if (columntype == Types.BLOB
										|| columntype == Types.CLOB
										|| columntype == Types.NCLOB) {
									// deal CLOB, BLOB etc.
									if (val != null) {
										if (columntype == Types.CLOB
												|| columntype == Types.NCLOB) {
											if (val instanceof byte[]) {
												Clob lob = getOracleNatveJdbcConnection(
														connection)
														.createClob();
												lob.setString(1, new String(
														(byte[]) val));
												stmt.setClob(idx, lob);
												clobs.add(lob);
											} else if (val instanceof String) {
												Clob lob = getOracleNatveJdbcConnection(
														connection)
														.createClob();
												lob.setString(1, (String) val);
												stmt.setClob(idx, lob);
												clobs.add(lob);
											} else if (val instanceof AsciiStream) {
												stmt.setClob(
														idx,
														new InputStreamReader(
																((AsciiStream) val)
																		.getInputStream()),
														((AsciiStream) val)
																.getLength());
											}
										} else {
											Blob lob = getOracleNatveJdbcConnection(
													connection).createBlob();
											lob.setBytes(1, (byte[]) val);
											stmt.setBlob(idx, lob);
											blobs.add(lob);
										}
									} else {
										stmt.setNull(idx, columntype);
									}
								} else {
									if (val != null) {
										Class<?> targetsqlcls = getObjectReader()
												.getTargetSqlClass(columntype);
										if (targetsqlcls != null) {
											Class<?> srcCls = val.getClass();
											if (GenericConverterFactory
													.getInstance().needConvert(
															srcCls,
															targetsqlcls)) {
												ITypeConverter converter = GenericConverterFactory
														.getInstance()
														.getSqlConverter();// .getConverter(srcCls,
																			// targetsqlcls);
												if (converter != null) {
													val = converter.convert(
															val, targetsqlcls);
												}
											}
										}
									}
									// stmt.setObject(idx, val, columntype);
									// fix java.sql.SQLException: Unknown Types
									// value
									stmt.setObject(idx, val);
								}
								idx++;
							}
						}
						stmt.addBatch();
						batchsize++;
						if (batchsize >= batchSize) {
							int[] bvals = stmt.executeBatch();
							for (int val : bvals) {
								rtnval += val;
							}
							stmt.clearBatch();
							if (logger.isDebugEnabled()) {
								logger.debug("Batch Executing SQL [size:"
										+ batchsize + "] [" + insertsql + "]");
							}
							batchsize = 0;
						}
						ik++;
					}
					if (batchsize > 0) {
						int[] bvals = stmt.executeBatch();
						for (int val : bvals) {
							rtnval += val;
						}
						if (logger.isDebugEnabled()) {
							logger.debug("Batch Executing SQL [size:"
									+ batchsize + "] [" + insertsql + "]");
						}
					}
					return rtnval;
				} catch (SQLException e) {
					throw e;
				} catch (Exception e) {
					throw new SQLException(e);
				} finally {
					if (!clobs.isEmpty()) {
						for (Clob clob : clobs) {
							JdbcUtils.closeClob(clob);
						}
						clobs.clear();
						clobs = null;
					}
					if (!blobs.isEmpty()) {
						for (Blob blob : blobs) {
							JdbcUtils.closeBlob(blob);
						}
						blobs.clear();
						blobs = null;
					}
					JdbcUtils.closeStatement(stmt);
					stmt = null;
				}
			}

		});
		return vals;
	}

	private int saveBusinessObjs(Object pojo, final String insertsql)
			throws Exception {
		Assert.notNull(pojo);
		Class<?> cls = pojo.getClass();
		final Map<String, FieldMapping> fieldMappings = getObjectReader()
				.getObjectFieldMap(cls);
		final Map<String, Object> objValMap = getObjectReader().readValue2Map(
				pojo);
		final ClassMapping classMapping = getObjectReader()
				.getClassMapping(cls);
		String keyGenerator = null;
		if (classMapping != null) {
			keyGenerator = classMapping.keyGenerator();
		} else {
			keyGenerator = KeyGenertator.ASSIGNED;
		}
		// deal primary key
		for (FieldMapping field : fieldMappings.values()) {
			if (field.primary()) {
				Object val = objValMap.get(field.columnName());
				if (val == null) {
					IdentifierGenerator idgenerator = getIdentifierGeneratorFactory()
							.createIdentifierGenerator(keyGenerator);
					if (idgenerator != null) {
						Serializable id = idgenerator.generate(getDialect(),
								this, pojo, field, true);
						objValMap.put(field.columnName(), id);
					} else {
						throw new SQLException(
								"column "
										+ field.columnName()
										+ " is null and can not find the proper IdentifierGenerator");
					}
				}
			}
		}

		Integer val = doExecute(new ConnectionCallback<Integer>() {
			@Override
			public Integer doInConnection(Connection connection)
					throws SQLException {
				PreparedStatement stmt = null;
				List<Clob> clobs = new ArrayList<Clob>();
				List<Blob> blobs = new ArrayList<Blob>();
				try {
					if (logger.isDebugEnabled()) {
						logger.debug("Executing SQL [" + insertsql
								+ "] values:" + objValMap);
					}
					stmt = connection.prepareStatement(insertsql);
					int idx = 1;
					for (FieldMapping field : fieldMappings.values()) {
						if (field.includeInWrites()) {
							if ((field.primary())
									&& (classMapping != null)
									&& (KeyGenertator.NATIVE
											.equals(classMapping.keyGenerator()))) {
								// if native
								continue;
							}
							Object val = objValMap.get(field.columnName());
							int columntype = field.columnType();
							if (columntype == Types.BLOB
									|| columntype == Types.CLOB
									|| columntype == Types.NCLOB) {
								// deal CLOB, BLOB etc.
								if (val != null) {
									if (columntype == Types.CLOB
											|| columntype == Types.NCLOB) {
										if (val instanceof byte[]) {
											Clob lob = getOracleNatveJdbcConnection(
													connection).createClob();
											lob.setString(1, new String(
													(byte[]) val));
											stmt.setClob(idx, lob);
											clobs.add(lob);
										} else if (val instanceof String) {
											Clob lob = getOracleNatveJdbcConnection(
													connection).createClob();
											lob.setString(1, (String) val);
											stmt.setClob(idx, lob);
											clobs.add(lob);
										} else if (val instanceof AsciiStream) {
											stmt.setClob(
													idx,
													new InputStreamReader(
															((AsciiStream) val)
																	.getInputStream()),
													((AsciiStream) val)
															.getLength());
										}
									} else {
										Blob lob = getOracleNatveJdbcConnection(
												connection).createBlob();
										lob.setBytes(1, (byte[]) val);
										stmt.setBlob(idx, lob);
										blobs.add(lob);
									}
								} else {
									stmt.setNull(idx, columntype);
									// if(columntype == Types.CLOB){
									// stmt.setClob(idx, (Clob)null);
									// }else{
									// stmt.setBlob(idx, (Blob)null);
									// }
								}
							} else {
								if (val != null) {
									Class<?> targetsqlcls = getObjectReader()
											.getTargetSqlClass(columntype);
									if (targetsqlcls != null) {
										Class<?> srcCls = val.getClass();
										if (GenericConverterFactory
												.getInstance().needConvert(
														srcCls, targetsqlcls)) {
											ITypeConverter converter = GenericConverterFactory
													.getInstance()
													.getSqlConverter();// .getConverter(srcCls,
																		// targetsqlcls);
											if (converter != null) {
												val = converter.convert(val,
														targetsqlcls);
											}
										}
									}
								}
								// stmt.setObject(idx, val, columntype);
								// fix java.sql.SQLException: Unknown Types
								// value
								stmt.setObject(idx, val);
							}
							idx++;
						}
					}
					return stmt.executeUpdate();
				} catch (SQLException e) {
					throw e;
				} catch (Exception e) {
					throw new SQLException(e);
				} finally {
					if (!clobs.isEmpty()) {
						for (Clob clob : clobs) {
							JdbcUtils.closeClob(clob);
						}
						clobs.clear();
						clobs = null;
					}
					if (!blobs.isEmpty()) {
						for (Blob blob : blobs) {
							JdbcUtils.closeBlob(blob);
						}
						blobs.clear();
						blobs = null;
					}
					JdbcUtils.closeStatement(stmt);
					stmt = null;
				}
			}
		});
		return val == null ? 0 : val.intValue();
	}

	public int saveBusinessObjsCol(Collection<? extends Serializable> pojos)
			throws SQLException {
		if ((pojos != null) && (!pojos.isEmpty())) {
			return saveBusinessObjs(pojos.toArray());
		}
		return 0;
	}

	public int updateBusinessObjs(boolean isFilterNull, Object[] pojos)
			throws SQLException {
		Assert.notEmpty(pojos, "pojos can not be null.");
		if (pojos.length == 1) {
			UpdateSqlInfo updateSqlInfo = null;
			if (isFilterNull) {// filter null
				updateSqlInfo = generateUpdateSql(pojos[0].getClass(), pojos[0]);
			} else {
				updateSqlInfo = generateUpdateSql(pojos[0].getClass(), null);
			}
			if (autoManagerTransaction) {
				beginTransation();
			}
			try {
				int rtn = updateBusinessObjs(pojos[0], updateSqlInfo);
				if (autoManagerTransaction) {
					commitTransation();
				}
				return rtn;
			} catch (SQLException e) {
				if (autoManagerTransaction) {
					rollbackTransation();
				}
				throw e;
			} catch (Exception e) {
				if (autoManagerTransaction) {
					rollbackTransation();
				}
				throw new SQLException(e);
			}
		} else {
			int rtnval = 0;
			if (autoManagerTransaction) {
				beginTransation();
			}
			try {
				if (isFilterNull) {// filter null
					// one by one, sql can be not same
					for (Object pojo : pojos) {
						Class<?> cls = pojo.getClass();
						final UpdateSqlInfo updateSqlInfo = generateUpdateSql(
								cls, pojo);
						rtnval += updateBusinessObjs(pojo, updateSqlInfo);
					}
				} else {// not filter null, same class ,sql is same
					Set<Class<?>> clsSet = new HashSet<Class<?>>();
					for (Object pojo : pojos) {
						clsSet.add(pojo.getClass());
					}
					if (clsSet.size() == 1) {
						UpdateSqlInfo updateSqlInfo = null;
						for (Class<?> cls : clsSet) {
							updateSqlInfo = generateUpdateSql(cls, null);
							break;
						}
						rtnval = batchUpdateBusinessObjs(updateSqlInfo, pojos);
					} else {
						Map<Class<?>, UpdateSqlInfo> sqlMap = new HashMap<Class<?>, UpdateSqlInfo>();
						for (Class<?> cls : clsSet) {
							sqlMap.put(cls, generateUpdateSql(cls, null));
						}
						for (Object pojo : pojos) {
							final UpdateSqlInfo updateSqlInfo = sqlMap.get(pojo
									.getClass());
							rtnval += updateBusinessObjs(pojo, updateSqlInfo);
						}
					}
				}
				if (autoManagerTransaction) {
					commitTransation();
				}
				return rtnval;
			} catch (SQLException e) {
				if (autoManagerTransaction) {
					rollbackTransation();
				}
				throw e;
			} catch (Exception e) {
				if (autoManagerTransaction) {
					rollbackTransation();
				}
				throw new SQLException(e);
			}
		}
	}

	private int batchUpdateBusinessObjs(final UpdateSqlInfo updateSqlInfo,
			final Object[] pojos) throws SQLException {
		Integer val = doExecute(new ConnectionCallback<Integer>() {

			@Override
			public Integer doInConnection(Connection connection)
					throws SQLException {
				PreparedStatement stmt = null;
				int rtnval = 0;
				List<Clob> clobs = new ArrayList<Clob>();
				List<Blob> blobs = new ArrayList<Blob>();
				try {
					// if(logger.isDebugEnabled()){
					// logger.debug("Executing SQL [" + updateSqlInfo.getSql() +
					// "]");
					// }
					int batchsize = 0;
					stmt = connection.prepareStatement(updateSqlInfo.getSql());
					List<FieldMapping> parameterMappings = updateSqlInfo
							.getParameterMappings();
					for (Object pojo : pojos) {
						int idx = 1;
						Map<String, Object> objValMap = getObjectReader()
								.readValue2Map(pojo);
						for (FieldMapping field : parameterMappings) {
							if (field.includeInWrites()) {
								int columntype = field.columnType();
								if (columntype == Types.BLOB
										|| columntype == Types.CLOB
										|| columntype == Types.NCLOB) {
									// deal CLOB, BLOB.
									Object val = objValMap.get(field
											.columnName());
									if (val != null) {
										if (columntype == Types.CLOB
												|| columntype == Types.NCLOB) {
											if (val instanceof byte[]) {
												Clob lob = getOracleNatveJdbcConnection(
														connection)
														.createClob();
												lob.setString(1, new String(
														(byte[]) val));
												stmt.setClob(idx, lob);
												clobs.add(lob);
											} else if (val instanceof String) {
												Clob lob = getOracleNatveJdbcConnection(
														connection)
														.createClob();
												lob.setString(1, (String) val);
												stmt.setClob(idx, lob);
												clobs.add(lob);
											} else if (val instanceof AsciiStream) {
												stmt.setClob(
														idx,
														new InputStreamReader(
																((AsciiStream) val)
																		.getInputStream()),
														((AsciiStream) val)
																.getLength());
											}
										} else {
											Blob lob = getOracleNatveJdbcConnection(
													connection).createBlob();
											lob.setBytes(1, (byte[]) val);
											stmt.setBlob(idx, lob);
											blobs.add(lob);
										}
									} else {
										stmt.setNull(idx, columntype);
									}
								} else {
									Object val = objValMap.get(field
											.columnName());
									if (val != null) {
										Class<?> targetsqlcls = getObjectReader()
												.getTargetSqlClass(columntype);
										if (targetsqlcls != null) {
											Class<?> srcCls = val.getClass();
											if (GenericConverterFactory
													.getInstance().needConvert(
															srcCls,
															targetsqlcls)) {
												ITypeConverter converter = GenericConverterFactory
														.getInstance()
														.getSqlConverter();// .getConverter(srcCls,
																			// targetsqlcls);
												if (converter != null) {
													val = converter.convert(
															val, targetsqlcls);
												}
											}
										}
									}
									// stmt.setObject(idx, val,
									// field.columnType());
									// fix java.sql.SQLException: Unknown Types
									// value
									stmt.setObject(idx, val);
								}
								idx++;
							}
						}// end for
						stmt.addBatch();
						batchsize++;
						if (batchsize >= batchSize) {
							int[] bvals = stmt.executeBatch();
							for (int val : bvals) {
								rtnval += val;
							}
							stmt.clearBatch();
							if (logger.isDebugEnabled()) {
								logger.debug("Batch Executing SQL [size:"
										+ batchsize + "] ["
										+ updateSqlInfo.getSql() + "]");
							}
							batchsize = 0;
						}
					}// end for pojos
					if (batchsize > 0) {
						int[] bvals = stmt.executeBatch();
						for (int val : bvals) {
							rtnval += val;
						}
						if (logger.isDebugEnabled()) {
							logger.debug("Batch Executing SQL [size:"
									+ batchsize + "] ["
									+ updateSqlInfo.getSql() + "]");
						}
					}
					return rtnval;
				} catch (SQLException e) {
					throw e;
				} catch (Exception e) {
					throw new SQLException(e);
				} finally {
					if (!clobs.isEmpty()) {
						for (Clob clob : clobs) {
							JdbcUtils.closeClob(clob);
						}
						clobs.clear();
						clobs = null;
					}
					if (!blobs.isEmpty()) {
						for (Blob blob : blobs) {
							JdbcUtils.closeBlob(blob);
						}
						blobs.clear();
						blobs = null;
					}
					JdbcUtils.closeStatement(stmt);
					stmt = null;
				}
			}

		});
		return val == null ? 0 : val.intValue();
	}

	private int updateBusinessObjs(Object pojo,
			final UpdateSqlInfo updateSqlInfo) throws Exception {
		final Map objValMap = getObjectReader().readValue2Map(pojo);

		Integer val = doExecute(new ConnectionCallback<Integer>() {
			public Integer doInConnection(Connection connection)
					throws SQLException {
				PreparedStatement stmt = null;
				List<Clob> clobs = new ArrayList();
				List<Blob> blobs = new ArrayList();
				try {
					stmt = connection.prepareStatement(updateSqlInfo.getSql());
					List<FieldMapping> parameterMappings = updateSqlInfo
							.getParameterMappings();
					int idx = 1;
					for (FieldMapping field : parameterMappings) {
						if (field.includeInWrites()) {
							int columntype = field.columnType();
							if ((columntype == 2004) || (columntype == 2005)
									|| (columntype == 2011)) {
								Object val = objValMap.get(field.columnName());
								if (val != null) {
									if ((columntype == 2005)
											|| (columntype == 2011)) {
										if ((val instanceof byte[])) {
											Clob lob = CommonDaoImpl.this
													.getOracleNatveJdbcConnection(
															connection)
													.createClob();
											lob.setString(1L, new String(
													(byte[]) val));
											stmt.setClob(idx, lob);
											clobs.add(lob);
										} else if ((val instanceof String)) {
											Clob lob = CommonDaoImpl.this
													.getOracleNatveJdbcConnection(
															connection)
													.createClob();
											lob.setString(1L, (String) val);
											stmt.setClob(idx, lob);
											clobs.add(lob);
										} else if ((val instanceof AsciiStream)) {
											stmt.setClob(
													idx,
													new InputStreamReader(
															((AsciiStream) val)
																	.getInputStream()),
													((AsciiStream) val)
															.getLength());
										}
									} else {
										Blob lob = CommonDaoImpl.this
												.getOracleNatveJdbcConnection(
														connection)
												.createBlob();
										lob.setBytes(1L, (byte[]) val);
										stmt.setBlob(idx, lob);
										blobs.add(lob);
									}
								} else
									stmt.setNull(idx, columntype);

							} else {
								Object val = objValMap.get(field.columnName());
								if (val != null) {
									Class targetsqlcls = CommonDaoImpl.this
											.getObjectReader()
											.getTargetSqlClass(columntype);
									if (targetsqlcls != null) {
										Class srcCls = val.getClass();
										if (GenericConverterFactory
												.getInstance().needConvert(
														srcCls, targetsqlcls)) {
											ITypeConverter converter = GenericConverterFactory
													.getInstance()
													.getSqlConverter();
											if (converter != null) {
												val = converter.convert(val,
														targetsqlcls);
											}
										}
									}
								}
								stmt.setObject(idx, val, field.columnType());
							}
							idx++;
						}
					}
					Integer localInteger = Integer
							.valueOf(stmt.executeUpdate());
					Clob clob;
					Blob blob;
					return localInteger;
				} catch (SQLException e) {
					throw e;
				} catch (Exception e) {
					throw new SQLException(e);
				} finally {
					Iterator localIterator2;
					if (!clobs.isEmpty()) {
						for (Clob clob : clobs) {
							JdbcUtils.closeClob(clob);
						}
						clobs.clear();
						clobs = null;
					}
					if (!blobs.isEmpty()) {
						for (Blob blob : blobs) {
							JdbcUtils.closeBlob(blob);
						}
						blobs.clear();
						blobs = null;
					}
					JdbcUtils.closeStatement(stmt);
					stmt = null;
				}
			}
		});
		return val == null ? 0 : val.intValue();
	}

	public int updateBusinessObjsCol(boolean isFilterNull,
			Collection<? extends Serializable> pojos) throws SQLException {
		if ((pojos != null) && (!pojos.isEmpty())) {
			return updateBusinessObjs(isFilterNull, pojos.toArray());
		}
		return 0;
	}

	public int getBatchSize() {
		return this.batchSize;
	}

	public void setBatchSize(int batchSize) {
		this.batchSize = batchSize;
	}
}