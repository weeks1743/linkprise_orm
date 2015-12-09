package com.linkprise.dao.id;

import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

import com.linkprise.dao.common.ICommonDao;
import com.linkprise.dao.common.SqlParameter;
import com.linkprise.dao.dialect.Dialect;
import com.linkprise.orm.annotation.ClassMapping;
import com.linkprise.orm.annotation.FieldMapping;
import com.linkprise.orm.mapping.ObjectMappingCache;

public class SelectGenerator implements IdentifierGenerator {
	public static final String _DEFAULT_TABLE_NAME = "IDENTIFIER_TABLE";
	public static final String _DEFAULT_NAME_CLOUMN = "TABLE_NAME";
	public static final String _DEFAULT_VALUE_CLOUMN = "SERIALIZE_VALUE";
	private String tablename = null;
	private String namecolumn = null;
	private String valuecolumn = null;

	public Serializable generate(Dialect dialect, ICommonDao dao, Object pojo,
			FieldMapping idFieldInfo, boolean writevalue) throws SQLException {
		try {
			Class cls = pojo.getClass();
			Map props = ObjectMappingCache.getInstance().getObjectPropertyMap(
					cls);
			PropertyDescriptor prop = (PropertyDescriptor) props
					.get(idFieldInfo.columnName());
			Class fieldType = prop.getPropertyType();
			if ((Long.class.isAssignableFrom(fieldType))
					|| (Integer.class.isAssignableFrom(fieldType))
					|| (Short.class.isAssignableFrom(fieldType))
					|| (BigInteger.class.isAssignableFrom(fieldType))
					|| (BigDecimal.class.isAssignableFrom(fieldType))) {
				ClassMapping clsmapping = ObjectMappingCache.getInstance()
						.getClassMapping(cls);
				String tableName = null;
				if (clsmapping != null) {
					tableName = clsmapping.tableName();
				}
				if (tableName == null) {
					tableName = cls.getSimpleName().toUpperCase();
				}
				int update = dao.update(generateUpdate(tableName, 0),
						new SqlParameter[0]);
				Object idval = null;
				if (update > 0) {
					idval = dao.querySingleObject(fieldType,
							generateQuery(tableName), new SqlParameter[0]);
				} else {
					dao.execute(generateInsert(tableName, 0),
							new SqlParameter[0]);
					if (Long.class.isAssignableFrom(fieldType))
						idval = Long.valueOf(1L);
					else if (Integer.class.isAssignableFrom(fieldType))
						idval = Integer.valueOf(1);
					else if (Short.class.isAssignableFrom(fieldType))
						idval = Short.valueOf((short) 1);
					else if (BigInteger.class.isAssignableFrom(fieldType))
						idval = BigInteger.valueOf(1L);
					else if (BigDecimal.class.isAssignableFrom(fieldType)) {
						idval = BigDecimal.valueOf(1L);
					}
				}
				if ((idval != null) && (writevalue)) {
					prop.getWriteMethod().invoke(pojo, new Object[] { idval });
				}

				return (Serializable) idval;
			}
			throw new SQLException("Unknown integral data type for ids : "
					+ fieldType.getName());
		} catch (SQLException e) {
			throw e;
		} catch (Exception e) {
			throw new SQLException(e);
		}
	}

	private String generateQuery(String tablename) {
		StringBuffer sql = new StringBuffer("SELECT ");
		sql.append(getValuecolumn()).append(" FROM ").append(getTablename());
		sql.append(" WHERE ").append(getNamecolumn()).append(" = '")
				.append(tablename).append('\'');
		return sql.toString();
	}

	private String generateUpdate(String tablename, int num) {
		StringBuffer sql = new StringBuffer("UPDATE ");
		sql.append(getTablename());
		if (num > 0)
			sql.append(" SET ").append(getValuecolumn()).append(" = ")
					.append(getValuecolumn()).append('+').append(num);
		else {
			sql.append(" SET ").append(getValuecolumn()).append(" = ")
					.append(getValuecolumn()).append("+1");
		}
		sql.append(" WHERE ").append(getNamecolumn()).append(" = '")
				.append(tablename).append('\'');
		return sql.toString();
	}

	private String generateInsert(String tablename, int num) {
		StringBuffer sql = new StringBuffer("INSERT INTO ");
		sql.append(getTablename());
		sql.append('(').append(getNamecolumn()).append(", ")
				.append(getValuecolumn()).append(") VALUES('");
		if (num > 0)
			sql.append(tablename).append("', ").append(num).append(')');
		else {
			sql.append(tablename).append("', 1)");
		}
		return sql.toString();
	}

	public String getTablename() {
		if (this.tablename == null) {
			this.tablename = "IDENTIFIER_TABLE";
		}
		return this.tablename;
	}

	public void setTableName(String tablename) {
		this.tablename = tablename;
	}

	public String getNamecolumn() {
		if (this.namecolumn == null) {
			this.namecolumn = "TABLE_NAME";
		}
		return this.namecolumn;
	}

	public void setNameColumn(String namecolumn) {
		this.namecolumn = namecolumn;
	}

	public String getValuecolumn() {
		if (this.valuecolumn == null) {
			this.valuecolumn = "SERIALIZE_VALUE";
		}
		return this.valuecolumn;
	}

	public void setValueColumn(String valuecolumn) {
		this.valuecolumn = valuecolumn;
	}

	public Serializable[] generate(Dialect dialect, ICommonDao dao,
			Object[] pojos, FieldMapping idFieldInfo, boolean writevalue)
			throws SQLException {
		Serializable[] sids = new Serializable[pojos.length];
		try {
			Map<Class<?>, Integer> clsSet = new LinkedHashMap<Class<?>, Integer>();
			for (Object pojo : pojos) {
				Class cls = pojo.getClass();
				Integer v = (Integer) clsSet.get(cls);
				if (v == null)
					clsSet.put(cls, Integer.valueOf(1));
				else {
					clsSet.put(cls, Integer.valueOf(v.intValue() + 1));
				}
			}
			int objidx = 0;
			for (Class cls : clsSet.keySet()) {
				Integer numids = (Integer) clsSet.get(cls);
				Map props = ObjectMappingCache.getInstance()
						.getObjectPropertyMap(cls);
				PropertyDescriptor prop = (PropertyDescriptor) props
						.get(idFieldInfo.columnName());
				Class fieldType = prop.getPropertyType();
				if ((Long.class.isAssignableFrom(fieldType))
						|| (Integer.class.isAssignableFrom(fieldType))
						|| (Short.class.isAssignableFrom(fieldType))
						|| (BigInteger.class.isAssignableFrom(fieldType))
						|| (BigDecimal.class.isAssignableFrom(fieldType))) {
					ClassMapping clsmapping = ObjectMappingCache.getInstance()
							.getClassMapping(cls);
					String tableName = null;
					if (clsmapping != null) {
						tableName = clsmapping.tableName();
					}
					if (tableName == null) {
						tableName = cls.getSimpleName().toUpperCase();
					}
					int update = dao.update(
							generateUpdate(tableName, numids.intValue()),
							new SqlParameter[0]);
					if (update > 0) {
						Object maxidval = dao.querySingleObject(fieldType,
								generateQuery(tableName), new SqlParameter[0]);
						if (Long.class.isAssignableFrom(fieldType)) {
							Long maxlongval = (Long) maxidval;
							for (int i = 0; i < numids.intValue(); i++) {
								if (i == 0)
									sids[objidx] = maxlongval;
								else {
									sids[objidx] = Long
											.valueOf(((Long) sids[(objidx - 1)])
													.longValue() - 1L);
								}
								if (writevalue) {
									prop.getWriteMethod().invoke(pojos[objidx],
											new Object[] { sids[objidx] });
								}
								objidx++;
							}
						} else if (Integer.class.isAssignableFrom(fieldType)) {
							Integer maxIntval = (Integer) maxidval;
							for (int i = 0; i < numids.intValue(); i++) {
								if (i == 0)
									sids[objidx] = maxIntval;
								else {
									sids[objidx] = Integer
											.valueOf(((Integer) sids[(objidx - 1)])
													.intValue() - 1);
								}
								if (writevalue) {
									prop.getWriteMethod().invoke(pojos[objidx],
											new Object[] { sids[objidx] });
								}
								objidx++;
							}
						} else if (Short.class.isAssignableFrom(fieldType)) {
							Short maxshortval = (Short) maxidval;
							for (int i = 0; i < numids.intValue(); i++) {
								if (i == 0)
									sids[objidx] = maxshortval;
								else {
									sids[objidx] = Integer
											.valueOf(((Short) sids[(objidx - 1)])
													.shortValue() - 1);
								}
								if (writevalue) {
									prop.getWriteMethod().invoke(pojos[objidx],
											new Object[] { sids[objidx] });
								}
								objidx++;
							}
						} else if (BigInteger.class.isAssignableFrom(fieldType)) {
							BigInteger maxbigval = (BigInteger) maxidval;
							for (int i = 0; i < numids.intValue(); i++) {
								if (i == 0)
									sids[objidx] = maxbigval;
								else {
									sids[objidx] = ((BigInteger) sids[(objidx - 1)])
											.subtract(BigInteger.ONE);
								}
								if (writevalue) {
									prop.getWriteMethod().invoke(pojos[objidx],
											new Object[] { sids[objidx] });
								}
								objidx++;
							}
						} else if (BigDecimal.class.isAssignableFrom(fieldType)) {
							BigDecimal maxbdval = (BigDecimal) maxidval;
							for (int i = 0; i < numids.intValue(); i++) {
								if (i == 0)
									sids[objidx] = maxbdval;
								else {
									sids[objidx] = ((BigDecimal) sids[(objidx - 1)])
											.subtract(BigDecimal.ONE);
								}
								if (writevalue) {
									prop.getWriteMethod().invoke(pojos[objidx],
											new Object[] { sids[objidx] });
								}
								objidx++;
							}
						}
					} else {
						dao.execute(
								generateInsert(tableName, numids.intValue()),
								new SqlParameter[0]);
						if (Long.class.isAssignableFrom(fieldType)) {
							Long maxlongval = Long.valueOf(numids.intValue());
							for (int i = 0; i < numids.intValue(); i++) {
								if (i == 0)
									sids[objidx] = maxlongval;
								else {
									sids[objidx] = Long
											.valueOf(((Long) sids[(objidx - 1)])
													.longValue() - 1L);
								}
								if (writevalue) {
									prop.getWriteMethod().invoke(pojos[objidx],
											new Object[] { sids[objidx] });
								}
								objidx++;
							}
						} else if (Integer.class.isAssignableFrom(fieldType)) {
							for (int i = 0; i < numids.intValue(); i++) {
								if (i == 0)
									sids[objidx] = numids;
								else {
									sids[objidx] = Integer
											.valueOf(((Integer) sids[(objidx - 1)])
													.intValue() - 1);
								}
								if (writevalue) {
									prop.getWriteMethod().invoke(pojos[objidx],
											new Object[] { sids[objidx] });
								}
								objidx++;
							}
						} else if (Short.class.isAssignableFrom(fieldType)) {
							Short maxshortval = Short.valueOf(numids
									.shortValue());
							for (int i = 0; i < numids.intValue(); i++) {
								if (i == 0)
									sids[objidx] = maxshortval;
								else {
									sids[objidx] = Integer
											.valueOf(((Short) sids[(objidx - 1)])
													.shortValue() - 1);
								}
								if (writevalue) {
									prop.getWriteMethod().invoke(pojos[objidx],
											new Object[] { sids[objidx] });
								}
								objidx++;
							}
						} else if (BigInteger.class.isAssignableFrom(fieldType)) {
							BigInteger maxbigval = new BigInteger(
									numids.toString());
							for (int i = 0; i < numids.intValue(); i++) {
								if (i == 0)
									sids[objidx] = maxbigval;
								else {
									sids[objidx] = ((BigInteger) sids[(objidx - 1)])
											.subtract(BigInteger.ONE);
								}
								if (writevalue) {
									prop.getWriteMethod().invoke(pojos[objidx],
											new Object[] { sids[objidx] });
								}
								objidx++;
							}
						} else if (BigDecimal.class.isAssignableFrom(fieldType)) {
							BigDecimal maxbdval = new BigDecimal(
									numids.intValue());
							for (int i = 0; i < numids.intValue(); i++) {
								if (i == 0)
									sids[objidx] = maxbdval;
								else {
									sids[objidx] = ((BigDecimal) sids[(objidx - 1)])
											.subtract(BigDecimal.ONE);
								}
								if (writevalue) {
									prop.getWriteMethod().invoke(pojos[objidx],
											new Object[] { sids[objidx] });
								}
								objidx++;
							}
						}
					}
				} else {
					throw new SQLException(
							"Unknown integral data type for ids : "
									+ fieldType.getName());
				}
			}
			return sids;
		} catch (SQLException e) {
			throw e;
		} catch (Exception e) {
			throw new SQLException(e);
		}
	}

	public Serializable generate(Dialect dialect, ICommonDao dao, Class<?> cls,
			Map<String, Object> valmap, FieldMapping idFieldInfo,
			boolean writevalue) throws SQLException {
		try {
			Map props = ObjectMappingCache.getInstance().getObjectPropertyMap(
					cls);
			PropertyDescriptor prop = (PropertyDescriptor) props
					.get(idFieldInfo.columnName());
			Class fieldType = prop.getPropertyType();
			if ((Long.class.isAssignableFrom(fieldType))
					|| (Integer.class.isAssignableFrom(fieldType))
					|| (Short.class.isAssignableFrom(fieldType))
					|| (BigInteger.class.isAssignableFrom(fieldType))
					|| (BigDecimal.class.isAssignableFrom(fieldType))) {
				ClassMapping clsmapping = ObjectMappingCache.getInstance()
						.getClassMapping(cls);
				String tableName = null;
				if (clsmapping != null) {
					tableName = clsmapping.tableName();
				}
				if (tableName == null) {
					tableName = cls.getSimpleName().toUpperCase();
				}
				int update = dao.update(generateUpdate(tableName, 0),
						new SqlParameter[0]);
				Object idval = null;
				if (update > 0) {
					idval = dao.querySingleObject(fieldType,
							generateQuery(tableName), new SqlParameter[0]);
				} else {
					dao.execute(generateInsert(tableName, 0),
							new SqlParameter[0]);
					if (Long.class.isAssignableFrom(fieldType))
						idval = Long.valueOf(1L);
					else if (Integer.class.isAssignableFrom(fieldType))
						idval = Integer.valueOf(1);
					else if (Short.class.isAssignableFrom(fieldType))
						idval = Short.valueOf((short) 1);
					else if (BigInteger.class.isAssignableFrom(fieldType))
						idval = BigInteger.valueOf(1L);
					else if (BigDecimal.class.isAssignableFrom(fieldType)) {
						idval = BigDecimal.valueOf(1L);
					}
				}
				if ((idval != null) && (writevalue)) {
					valmap.put(idFieldInfo.columnName(), idval);
				}

				return (Serializable) idval;
			}
			throw new SQLException("Unknown integral data type for ids : "
					+ fieldType.getName());
		} catch (SQLException e) {
			throw e;
		} catch (Exception e) {
			throw new SQLException(e);
		}
	}

	public Serializable[] generate(Dialect dialect, ICommonDao dao,
			Class<?> cls, Map<String, Object>[] valmaps,
			FieldMapping idFieldInfo, boolean writevalue) throws SQLException {
		Serializable[] sids = new Serializable[valmaps.length];
		try {
			int objidx = 0;
			Integer numids = Integer.valueOf(sids.length);
			Map props = ObjectMappingCache.getInstance().getObjectPropertyMap(
					cls);
			PropertyDescriptor prop = (PropertyDescriptor) props
					.get(idFieldInfo.columnName());
			Class fieldType = prop.getPropertyType();
			if ((Long.class.isAssignableFrom(fieldType))
					|| (Integer.class.isAssignableFrom(fieldType))
					|| (Short.class.isAssignableFrom(fieldType))
					|| (BigInteger.class.isAssignableFrom(fieldType))
					|| (BigDecimal.class.isAssignableFrom(fieldType))) {
				ClassMapping clsmapping = ObjectMappingCache.getInstance()
						.getClassMapping(cls);
				String tableName = null;
				if (clsmapping != null) {
					tableName = clsmapping.tableName();
				}
				if (tableName == null) {
					tableName = cls.getSimpleName().toUpperCase();
				}
				int update = dao.update(
						generateUpdate(tableName, numids.intValue()),
						new SqlParameter[0]);
				if (update > 0) {
					Object maxidval = dao.querySingleObject(fieldType,
							generateQuery(tableName), new SqlParameter[0]);
					if (Long.class.isAssignableFrom(fieldType)) {
						Long maxlongval = (Long) maxidval;
						for (int i = 0; i < numids.intValue(); i++) {
							if (i == 0)
								sids[objidx] = maxlongval;
							else {
								sids[objidx] = Long
										.valueOf(((Long) sids[(objidx - 1)])
												.longValue() - 1L);
							}
							if (writevalue) {
								valmaps[i].put(idFieldInfo.columnName(),
										sids[objidx]);
							}
							objidx++;
						}
					} else if (Integer.class.isAssignableFrom(fieldType)) {
						Integer maxIntval = (Integer) maxidval;
						for (int i = 0; i < numids.intValue(); i++) {
							if (i == 0)
								sids[objidx] = maxIntval;
							else {
								sids[objidx] = Integer
										.valueOf(((Integer) sids[(objidx - 1)])
												.intValue() - 1);
							}
							if (writevalue) {
								valmaps[i].put(idFieldInfo.columnName(),
										sids[objidx]);
							}
							objidx++;
						}
					} else if (Short.class.isAssignableFrom(fieldType)) {
						Short maxshortval = (Short) maxidval;
						for (int i = 0; i < numids.intValue(); i++) {
							if (i == 0)
								sids[objidx] = maxshortval;
							else {
								sids[objidx] = Integer
										.valueOf(((Short) sids[(objidx - 1)])
												.shortValue() - 1);
							}
							if (writevalue) {
								valmaps[i].put(idFieldInfo.columnName(),
										sids[objidx]);
							}
							objidx++;
						}
					} else if (BigInteger.class.isAssignableFrom(fieldType)) {
						BigInteger maxbigval = (BigInteger) maxidval;
						for (int i = 0; i < numids.intValue(); i++) {
							if (i == 0)
								sids[objidx] = maxbigval;
							else {
								sids[objidx] = ((BigInteger) sids[(objidx - 1)])
										.subtract(BigInteger.ONE);
							}
							if (writevalue) {
								valmaps[i].put(idFieldInfo.columnName(),
										sids[objidx]);
							}
							objidx++;
						}
					} else if (BigDecimal.class.isAssignableFrom(fieldType)) {
						BigDecimal maxbdval = (BigDecimal) maxidval;
						for (int i = 0; i < numids.intValue(); i++) {
							if (i == 0)
								sids[objidx] = maxbdval;
							else {
								sids[objidx] = ((BigDecimal) sids[(objidx - 1)])
										.subtract(BigDecimal.ONE);
							}
							if (writevalue) {
								valmaps[i].put(idFieldInfo.columnName(),
										sids[objidx]);
							}
							objidx++;
						}
					}
				} else {
					dao.execute(generateInsert(tableName, numids.intValue()),
							new SqlParameter[0]);
					if (Long.class.isAssignableFrom(fieldType)) {
						Long maxlongval = Long.valueOf(numids.intValue());
						for (int i = 0; i < numids.intValue(); i++) {
							if (i == 0)
								sids[objidx] = maxlongval;
							else {
								sids[objidx] = Long
										.valueOf(((Long) sids[(objidx - 1)])
												.longValue() - 1L);
							}
							if (writevalue) {
								valmaps[i].put(idFieldInfo.columnName(),
										sids[objidx]);
							}
							objidx++;
						}
					} else if (Integer.class.isAssignableFrom(fieldType)) {
						for (int i = 0; i < numids.intValue(); i++) {
							if (i == 0)
								sids[objidx] = numids;
							else {
								sids[objidx] = Integer
										.valueOf(((Integer) sids[(objidx - 1)])
												.intValue() - 1);
							}
							if (writevalue) {
								valmaps[i].put(idFieldInfo.columnName(),
										sids[objidx]);
							}
							objidx++;
						}
					} else if (Short.class.isAssignableFrom(fieldType)) {
						Short maxshortval = Short.valueOf(numids.shortValue());
						for (int i = 0; i < numids.intValue(); i++) {
							if (i == 0)
								sids[objidx] = maxshortval;
							else {
								sids[objidx] = Integer
										.valueOf(((Short) sids[(objidx - 1)])
												.shortValue() - 1);
							}
							if (writevalue) {
								valmaps[i].put(idFieldInfo.columnName(),
										sids[objidx]);
							}
							objidx++;
						}
					} else if (BigInteger.class.isAssignableFrom(fieldType)) {
						BigInteger maxbigval = new BigInteger(numids.toString());
						for (int i = 0; i < numids.intValue(); i++) {
							if (i == 0)
								sids[objidx] = maxbigval;
							else {
								sids[objidx] = ((BigInteger) sids[(objidx - 1)])
										.subtract(BigInteger.ONE);
							}
							if (writevalue) {
								valmaps[i].put(idFieldInfo.columnName(),
										sids[objidx]);
							}
							objidx++;
						}
					} else if (BigDecimal.class.isAssignableFrom(fieldType)) {
						BigDecimal maxbdval = new BigDecimal(numids.intValue());
						for (int i = 0; i < numids.intValue(); i++) {
							if (i == 0)
								sids[objidx] = maxbdval;
							else {
								sids[objidx] = ((BigDecimal) sids[(objidx - 1)])
										.subtract(BigDecimal.ONE);
							}
							if (writevalue) {
								valmaps[i].put(idFieldInfo.columnName(),
										sids[objidx]);
							}
							objidx++;
						}
					}
				}
			} else {
				throw new SQLException("Unknown integral data type for ids : "
						+ fieldType.getName());
			}
			return sids;
		} catch (SQLException e) {
			throw e;
		} catch (Exception e) {
			throw new SQLException(e);
		}
	}
}