package com.linkprise.dao.id;

import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.SQLException;
import java.util.Map;

import com.linkprise.dao.common.ICommonDao;
import com.linkprise.dao.common.SqlParameter;
import com.linkprise.dao.dialect.Dialect;
import com.linkprise.orm.annotation.ClassMapping;
import com.linkprise.orm.annotation.FieldMapping;
import com.linkprise.orm.mapping.ObjectMappingCache;

public class IncrementGenerator implements IdentifierGenerator {
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
				String sql = "SELECT MAX(" + idFieldInfo.columnName()
						+ ") FROM " + tableName;
				Object idval = dao.querySingleObject(fieldType, sql,
						new SqlParameter[0]);
				if (idval == null) {
					idval = Integer.valueOf(0);
				}
				if (Long.class.isAssignableFrom(fieldType))
					idval = Long.valueOf(((Number) idval).longValue() + 1L);
				else if (Integer.class.isAssignableFrom(fieldType))
					idval = Integer
							.valueOf((int) (((Number) idval).longValue() + 1L));
				else if (Short.class.isAssignableFrom(fieldType))
					idval = Short.valueOf((short) (int) (((Number) idval)
							.longValue() + 1L));
				else if (BigInteger.class.isAssignableFrom(fieldType))
					idval = BigInteger
							.valueOf(((Number) idval).longValue() + 1L);
				else if (BigDecimal.class.isAssignableFrom(fieldType)) {
					idval = BigDecimal
							.valueOf(((Number) idval).longValue() + 1L);
				}
				if (writevalue) {
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

	public Serializable[] generate(Dialect dialect, ICommonDao dao,
			Object[] pojos, FieldMapping idFieldInfo, boolean writevalue)
			throws SQLException {
		if (pojos.length <= 1) {
			return new Serializable[] { generate(dialect, dao, pojos[0],
					idFieldInfo, writevalue) };
		}
		Serializable[] rtns = new Serializable[pojos.length];
		try {
			Class cls = pojos[0].getClass();
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
				String sql = "SELECT MAX(" + idFieldInfo.columnName()
						+ ") FROM " + tableName;
				Object idval = dao.querySingleObject(fieldType, sql,
						new SqlParameter[0]);
				if (idval == null) {
					idval = Integer.valueOf(0);
				}
				if (Long.class.isAssignableFrom(fieldType)) {
					long lidval = ((Number) idval).longValue();
					for (int i = 0; i < rtns.length; i++) {
						rtns[i] = Long.valueOf(lidval + i + 1L);
						if (writevalue)
							prop.getWriteMethod().invoke(pojos[i],
									new Object[] { rtns[i] });
					}
				} else if (Integer.class.isAssignableFrom(fieldType)) {
					int iidval = (int) ((Number) idval).longValue();
					for (int i = 0; i < rtns.length; i++) {
						rtns[i] = Integer.valueOf(iidval + i + 1);
						if (writevalue)
							prop.getWriteMethod().invoke(pojos[i],
									new Object[] { rtns[i] });
					}
				} else if (Short.class.isAssignableFrom(fieldType)) {
					short sidval = (short) (int) ((Number) idval).longValue();
					for (int i = 0; i < rtns.length; i++) {
						rtns[i] = Integer.valueOf(sidval + i + 1);
						if (writevalue)
							prop.getWriteMethod().invoke(pojos[i],
									new Object[] { rtns[i] });
					}
				} else if (BigInteger.class.isAssignableFrom(fieldType)) {
					BigInteger bidval = BigInteger.valueOf(((Number) idval)
							.longValue());
					for (int i = 0; i < rtns.length; i++) {
						if (i == 0)
							rtns[i] = bidval.add(BigInteger.ONE);
						else {
							rtns[i] = ((BigInteger) rtns[(i - 1)])
									.add(BigInteger.ONE);
						}
						if (writevalue)
							prop.getWriteMethod().invoke(pojos[i],
									new Object[] { rtns[i] });
					}
				} else if (BigDecimal.class.isAssignableFrom(fieldType)) {
					BigDecimal bidval = BigDecimal.valueOf(((Number) idval)
							.longValue());
					for (int i = 0; i < rtns.length; i++) {
						if (i == 0)
							rtns[i] = bidval.add(BigDecimal.ONE);
						else {
							rtns[i] = ((BigDecimal) rtns[(i - 1)])
									.add(BigDecimal.ONE);
						}
						if (writevalue) {
							prop.getWriteMethod().invoke(pojos[i],
									new Object[] { rtns[i] });
						}
					}
				}
				return rtns;
			}
			throw new SQLException("Unknown integral data type for ids : "
					+ fieldType.getName());
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
				String sql = "SELECT MAX(" + idFieldInfo.columnName()
						+ ") FROM " + tableName;
				Object idval = dao.querySingleObject(fieldType, sql,
						new SqlParameter[0]);
				if (idval == null) {
					idval = Integer.valueOf(0);
				}
				if (Long.class.isAssignableFrom(fieldType))
					idval = Long.valueOf(((Number) idval).longValue() + 1L);
				else if (Integer.class.isAssignableFrom(fieldType))
					idval = Integer
							.valueOf((int) (((Number) idval).longValue() + 1L));
				else if (Short.class.isAssignableFrom(fieldType))
					idval = Short.valueOf((short) (int) (((Number) idval)
							.longValue() + 1L));
				else if (BigInteger.class.isAssignableFrom(fieldType))
					idval = BigInteger
							.valueOf(((Number) idval).longValue() + 1L);
				else if (BigDecimal.class.isAssignableFrom(fieldType)) {
					idval = BigDecimal
							.valueOf(((Number) idval).longValue() + 1L);
				}
				if (writevalue) {
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
		if (valmaps.length <= 1) {
			return new Serializable[] { generate(dialect, dao, cls, valmaps[0],
					idFieldInfo, writevalue) };
		}
		Serializable[] rtns = new Serializable[valmaps.length];
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
				String sql = "SELECT MAX(" + idFieldInfo.columnName()
						+ ") FROM " + tableName;
				Object idval = dao.querySingleObject(fieldType, sql,
						new SqlParameter[0]);
				if (idval == null) {
					idval = Integer.valueOf(0);
				}
				if (Long.class.isAssignableFrom(fieldType)) {
					long lidval = ((Number) idval).longValue();
					for (int i = 0; i < rtns.length; i++) {
						rtns[i] = Long.valueOf(lidval + i + 1L);
						if (writevalue)
							valmaps[i].put(idFieldInfo.columnName(), rtns[i]);
					}
				} else if (Integer.class.isAssignableFrom(fieldType)) {
					int iidval = (int) ((Number) idval).longValue();
					for (int i = 0; i < rtns.length; i++) {
						rtns[i] = Integer.valueOf(iidval + i + 1);
						if (writevalue)
							valmaps[i].put(idFieldInfo.columnName(), rtns[i]);
					}
				} else if (Short.class.isAssignableFrom(fieldType)) {
					short sidval = (short) (int) ((Number) idval).longValue();
					for (int i = 0; i < rtns.length; i++) {
						rtns[i] = Integer.valueOf(sidval + i + 1);
						if (writevalue)
							valmaps[i].put(idFieldInfo.columnName(), rtns[i]);
					}
				} else if (BigInteger.class.isAssignableFrom(fieldType)) {
					BigInteger bidval = BigInteger.valueOf(((Number) idval)
							.longValue());
					for (int i = 0; i < rtns.length; i++) {
						if (i == 0)
							rtns[i] = bidval.add(BigInteger.ONE);
						else {
							rtns[i] = ((BigInteger) rtns[(i - 1)])
									.add(BigInteger.ONE);
						}
						if (writevalue)
							valmaps[i].put(idFieldInfo.columnName(), rtns[i]);
					}
				} else if (BigDecimal.class.isAssignableFrom(fieldType)) {
					BigDecimal bidval = BigDecimal.valueOf(((Number) idval)
							.longValue());
					for (int i = 0; i < rtns.length; i++) {
						if (i == 0)
							rtns[i] = bidval.add(BigDecimal.ONE);
						else {
							rtns[i] = ((BigDecimal) rtns[(i - 1)])
									.add(BigDecimal.ONE);
						}
						if (writevalue) {
							valmaps[i].put(idFieldInfo.columnName(), rtns[i]);
						}
					}
				}
				return rtns;
			}
			throw new SQLException("Unknown integral data type for ids : "
					+ fieldType.getName());
		} catch (SQLException e) {
			throw e;
		} catch (Exception e) {
			throw new SQLException(e);
		}
	}
}