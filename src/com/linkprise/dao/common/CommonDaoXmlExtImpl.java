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
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.linkprise.dao.dialect.Dialect;
import com.linkprise.dao.id.IdentifierGenerator;
import com.linkprise.dao.id.IdentifierGeneratorFactory;
import com.linkprise.orm.annotation.ClassMapping;
import com.linkprise.orm.annotation.FieldMapping;
import com.linkprise.orm.annotation.KeyGenertator;
import com.linkprise.orm.convert.GenericConverterFactory;
import com.linkprise.orm.convert.ITypeConverter;
import com.linkprise.orm.mapping.AsciiStream;
import com.linkprise.orm.mapping.IJsonReader;
import com.linkprise.orm.mapping.IObjectReader;
import com.linkprise.orm.mapping.IXmlReader;
import com.linkprise.orm.mapping.JsonReader;
import com.linkprise.orm.mapping.XmlReader;
import com.linkprise.utils.Assert;

public class CommonDaoXmlExtImpl extends CommonDaoImpl implements
		ICommonDaoXmlExt {
	private static final Log logger = LogFactory
			.getLog(CommonDaoXmlExtImpl.class);
	public static final String _DEFAULT_ROOT_NAME = "RecordSet";
	public static final String _DEFAULT_ITEM_NAME = "Record";
	private IXmlReader xmlReader = null;
	private IJsonReader jsonReader = null;

	private String rootName = "RecordSet";
	private String itemName = "Record";

	public CommonDaoXmlExtImpl() {
		this.xmlReader = new XmlReader();
		this.jsonReader = new JsonReader();
	}

	public CommonDaoXmlExtImpl(ConnectionFactory connectionFactory) {
		super(connectionFactory);
		this.xmlReader = new XmlReader();
		this.jsonReader = new JsonReader();
	}

	public CommonDaoXmlExtImpl(ConnectionFactory connectionFactory,
			IObjectReader objectReader) {
		super(connectionFactory, objectReader);
		this.xmlReader = new XmlReader();
		this.jsonReader = new JsonReader();
	}

	public CommonDaoXmlExtImpl(ConnectionFactory connectionFactory,
			IXmlReader xmlReader) {
		super(connectionFactory);
		this.xmlReader = xmlReader;
		this.jsonReader = new JsonReader();
	}

	public Document fill(String sql, SqlParameter[] params) throws SQLException {
		return (Document) query(sql, new RowsResultSetXmlExtractor(
				this.xmlReader, this.rootName, this.itemName), params);
	}

	public Document fill(String sql, String rootName, String itemName,
			SqlParameter[] params) throws SQLException {
		return (Document) query(sql, new RowsResultSetXmlExtractor(
				this.xmlReader, rootName, itemName), params);
	}

	public Document fill(String sql, int startRecord, int maxRecord,
			SqlParameter[] params) throws SQLException {
		if (getDialect() == null) {
			throw new SQLException("can not find SQL Dialect.");
		}
		Document items = null;
		String sqlforLimit = null;
		RowsResultSetXmlExtractor rse = new RowsResultSetXmlExtractor(
				this.xmlReader, this.rootName, this.itemName);
		if (getDialect().supportsOffset()) {
			sqlforLimit = getDialect().getLimitString(sql, startRecord,
					maxRecord);
			items = (Document) query(sqlforLimit, rse, params);
		} else {
			sqlforLimit = getDialect().getLimitString(sql, 0,
					startRecord + maxRecord);
			rse.setOffset(startRecord);
			items = (Document) query(sqlforLimit, rse, params);
		}
		return items;
	}

	public Document fill(String sql, String rootName, String itemName,
			int startRecord, int maxRecord, SqlParameter[] params)
			throws SQLException {
		if (getDialect() == null) {
			throw new SQLException("can not find SQL Dialect.");
		}
		Document items = null;
		String sqlforLimit = null;
		RowsResultSetXmlExtractor rse = new RowsResultSetXmlExtractor(
				this.xmlReader, rootName, itemName);
		if (getDialect().supportsOffset()) {
			sqlforLimit = getDialect().getLimitString(sql, startRecord,
					maxRecord);
			items = (Document) query(sqlforLimit, rse, params);
		} else {
			sqlforLimit = getDialect().getLimitString(sql, 0,
					startRecord + maxRecord);
			rse.setOffset(startRecord);
			items = (Document) query(sqlforLimit, rse, params);
		}
		return items;
	}

	public Document fillByPagedQuery(String sql, int startPage, int pageSize,
			SqlParameter[] params) throws SQLException {
		String coutsql = "SELECT COUNT(0) " + removeSelect(sql);
		Long totalCount = (Long) queryForObject(Long.class, coutsql, params);
		if (totalCount != null) {
			int pagecount = (int) Math.ceil(totalCount.longValue() / pageSize);
			int curpage = startPage;
			if (curpage >= pagecount)
				curpage = pagecount - 1;
			int startRecord = curpage >= 0 ? curpage * pageSize : 0;
			Document items = null;
			if (0L == totalCount.longValue()) {
				items = DocumentHelper.createDocument();
				Element rootElement = items.addElement(this.rootName);
				Element totalitem = rootElement.addElement(this.itemName + "1");
				totalitem.addElement("TotalCount").addText("0");
			} else {
				if (getDialect() == null) {
					throw new SQLException("can not find SQL Dialect.");
				}
				RowsResultSetXmlExtractor rse = new RowsResultSetXmlExtractor(
						this.xmlReader, this.rootName, this.itemName);
				String sqlforLimit = null;
				if (getDialect().supportsOffset()) {
					sqlforLimit = getDialect().getLimitString(sql, startRecord,
							pageSize);
					items = (Document) query(sqlforLimit, rse, params);
				} else {
					int endRecord = (curpage + 1) * pageSize;
					if (endRecord > totalCount.intValue()) {
						endRecord = totalCount.intValue();
					}
					sqlforLimit = getDialect()
							.getLimitString(sql, 0, endRecord);
					rse.setOffset(startRecord);
					items = (Document) query(sqlforLimit, rse, params);
				}
				Element totalitem = items.getRootElement().addElement(
						this.itemName + "1");
				totalitem.addElement("TotalCount").addText(
						String.valueOf(totalCount));
			}
			return items;
		}
		return null;
	}

	public Document fillByPagedQuery(String sql, String rootName,
			String itemName, int startPage, int pageSize, SqlParameter[] params)
			throws SQLException {
		String coutsql = "SELECT COUNT(0) " + removeSelect(sql);
		Long totalCount = (Long) queryForObject(Long.class, coutsql, params);
		if (totalCount != null) {
			int pagecount = (int) Math.ceil(totalCount.longValue() / pageSize);
			int curpage = startPage;
			if (curpage >= pagecount)
				curpage = pagecount - 1;
			int startRecord = curpage >= 0 ? curpage * pageSize : 0;
			Document items = null;
			if (0L == totalCount.longValue()) {
				items = DocumentHelper.createDocument();
				Element rootElement = items.addElement(rootName);
				Element totalitem = rootElement.addElement(itemName + "1");
				totalitem.addElement("TotalCount").addText("0");
			} else {
				if (getDialect() == null) {
					throw new SQLException("can not find SQL Dialect.");
				}
				RowsResultSetXmlExtractor rse = new RowsResultSetXmlExtractor(
						this.xmlReader, rootName, itemName);
				String sqlforLimit = null;
				if (getDialect().supportsOffset()) {
					sqlforLimit = getDialect().getLimitString(sql, startRecord,
							pageSize);
					items = (Document) query(sqlforLimit, rse, params);
				} else {
					int endRecord = (curpage + 1) * pageSize;
					if (endRecord > totalCount.intValue()) {
						endRecord = totalCount.intValue();
					}
					sqlforLimit = getDialect()
							.getLimitString(sql, 0, endRecord);
					rse.setOffset(startRecord);
					items = (Document) query(sqlforLimit, rse, params);
				}
				Element totalitem = items.getRootElement().addElement(
						itemName + "1");
				totalitem.addElement("TotalCount").addText(
						String.valueOf(totalCount));
			}
			return items;
		}
		return null;
	}

	public int saveModelData(Class<?> cls, Map<String, Object>[] models)
			throws SQLException {
		Assert.notEmpty(models, "models can not be null.");
		if (autoManagerTransaction) {
			beginTransation();
		}
		try {
			final String sql = generateInsertSql(cls);
			int rnt = batchSaveBusinessObjs(sql, cls, models);
			if (autoManagerTransaction) {
				commitTransation();
			}
			return rnt;
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

	private Integer batchSaveBusinessObjs(final String insertsql,
			final Class<?> cls, final Map<String, Object>... objValMaps)
			throws Exception {
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
					Map<String, Object> objValMap = objValMaps[0];
					Object val = objValMap.get(field.columnName());
					if (val == null) {
						idgenerator.generate(getDialect(), dao, cls,
								objValMaps, field, true);
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
					int batchsize = 0;
					stmt = connection.prepareStatement(insertsql);
					Serializable[] pkvals = null;
					int ik = 0;
					for (Map<String, Object> objValMap : objValMaps) {
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
												getDialect(), dao, cls,
												objValMaps, field, true);
										if (pkvals == null) {
											pkvals = new Serializable[objValMaps.length];
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
						if (batchsize >= getBatchSize()) {
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

	public int saveModelDataCol(Class<?> cls,
			Collection<Map<String, Object>> models) throws SQLException {
		if ((models != null) && (!models.isEmpty())) {
			saveModelData(cls, (Map[]) models.toArray(new Map[models.size()]));
		}
		return 0;
	}

	public int saveModelData(Class<?> cls, Map<String, Object> model)
			throws SQLException {
		Assert.notNull(model, "model can not be null.");
		return saveModelData(cls, new Map[] { model });
	}

	public String fillJson(String sql, SqlParameter[] params)
			throws SQLException {
		return (String) query(sql, new RowsResultSetJsonExtractor(
				this.jsonReader), params);
	}

	public String fillJson(String sql, int startRecord, int maxRecord,
			SqlParameter[] params) throws SQLException {
		if (getDialect() == null) {
			throw new SQLException("can not find SQL Dialect.");
		}
		String items = null;
		String sqlforLimit = null;
		RowsResultSetJsonExtractor rse = new RowsResultSetJsonExtractor(
				this.jsonReader);
		if (getDialect().supportsOffset()) {
			sqlforLimit = getDialect().getLimitString(sql, startRecord,
					maxRecord);
			items = (String) query(sqlforLimit, rse, params);
		} else {
			sqlforLimit = getDialect().getLimitString(sql, 0,
					startRecord + maxRecord);
			rse.setOffset(startRecord);
			items = (String) query(sqlforLimit, rse, params);
		}
		return items;
	}

	public String fillJsonByPagedQuery(String sql, int startPage, int pageSize,
			SqlParameter[] params) throws SQLException {
		String coutsql = "SELECT COUNT(0) " + removeSelect(sql);
		Long totalCount = (Long) queryForObject(Long.class, coutsql, params);
		if (totalCount != null) {
			int pagecount = (int) Math.ceil(totalCount.longValue() / pageSize);
			int curpage = startPage;
			if (curpage >= pagecount)
				curpage = pagecount - 1;
			int startRecord = curpage >= 0 ? curpage * pageSize : 0;
			StringBuilder items = new StringBuilder();
			if (0L == totalCount.longValue()) {
				items.append("{\"total\":0,\"rows\":[]}");
			} else {
				if (getDialect() == null) {
					throw new SQLException("can not find SQL Dialect.");
				}
				items.append("{\"total\":").append(totalCount)
						.append(",\"rows\":");

				RowsResultSetJsonExtractor rse = new RowsResultSetJsonExtractor(
						this.jsonReader);
				String sqlforLimit = null;
				if (getDialect().supportsOffset()) {
					sqlforLimit = getDialect().getLimitString(sql, startRecord,
							pageSize);
					items.append((String) query(sqlforLimit, rse, params));
				} else {
					int endRecord = (curpage + 1) * pageSize;
					if (endRecord > totalCount.intValue()) {
						endRecord = totalCount.intValue();
					}
					sqlforLimit = getDialect()
							.getLimitString(sql, 0, endRecord);
					rse.setOffset(startRecord);
					items.append((String) query(sqlforLimit, rse, params));
				}
				items.append('}');
			}
			return items.toString();
		}
		return null;
	}

	public IXmlReader getXmlReader() {
		return this.xmlReader;
	}

	public void setXmlReader(IXmlReader xmlReader) {
		this.xmlReader = xmlReader;
	}

	public void setJsonReader(IJsonReader jsonReader) {
		this.jsonReader = jsonReader;
	}

	public void setRootName(String rootName) {
		this.rootName = rootName;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}
}