/*jadclipse*/// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.

package com.linkprise.orm.mapping;

import java.io.InputStream;
import java.io.Reader;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.SQLXML;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import com.linkprise.orm.annotation.ClassMapping;
import com.linkprise.orm.annotation.FieldMapping;

// Referenced classes of package org.uorm.orm.mapping:
//            IObjectReader, ObjectMappingCache, AsciiStream

public class ObjectReader implements IObjectReader {

	public ObjectReader() {
	}

	public Object read(Class cls, ResultSet result, ResultSetMetaData rsmd)
			throws Exception {
		int count = rsmd.getColumnCount();
		Map setMethods = ObjectMappingCache.getInstance().getPojoSetMethod(cls);
		if ((setMethods == null || setMethods.isEmpty())
				&& count == 1
				|| ObjectMappingCache.getInstance().getClassMapping(cls) == null
				&& count == 1) {
			Object val = result.getObject(1);
			if (val == null)
				return null;
			else
				return getValue(result, 1, cls);
		}
		Object instance = cls.newInstance();
		for (int i = 1; i <= count; i++) {
			String columnName = rsmd.getColumnLabel(i);
			if (columnName == null || columnName.length() == 0)
				columnName = rsmd.getColumnName(i);
			Method setterMethod = (Method) setMethods.get(columnName
					.toUpperCase());
			if(setterMethod==null){
				setterMethod = (Method) setMethods.get(columnName
						.toLowerCase());
			}
			if (setterMethod != null) {
				Class memberType = setterMethod.getParameterTypes()[0];
				Object val = result.getObject(i);
				if (val != null)
					if (val instanceof Clob) {
						byte lobvar[] = clob2bytes((Clob) val);
						setterMethod.invoke(instance, new Object[] { lobvar });
					} else if (val instanceof Blob) {
						byte lobvar[] = blob2bytes((Blob) val);
						setterMethod.invoke(instance, new Object[] { lobvar });
					} else {
						val = getValue(result, i, memberType);
						setterMethod.invoke(instance, new Object[] { val });
					}
			}
		}

		return instance;
	}

	static String capitalize(String s) {
		return (new StringBuilder(String.valueOf(Character.toUpperCase(s
				.charAt(0))))).append(s.substring(1)).toString();
	}

	private byte[] clob2bytes(Clob clob) throws SQLException {
		if (clob != null) {
			String date = clob.getSubString(1L, (int) clob.length());
			return date.getBytes();
		} else {
			return null;
		}
	}

	private byte[] blob2bytes(Blob blob) throws SQLException {
		if (blob != null) {
			byte data[] = blob.getBytes(1L, (int) blob.length());
			return data;
		} else {
			return null;
		}
	}

	private Object getValue(ResultSet result, int columnIndex, Class memberType)
			throws SQLException {
		if (java.lang.reflect.Array.class.equals(memberType))
			return result.getArray(columnIndex);
		if (com.linkprise.orm.mapping.AsciiStream.class.equals(memberType))
			return result.getAsciiStream(columnIndex);
		if ((Byte[].class.equals(memberType))
				|| (byte[].class.equals(memberType)))
			return result.getBytes(columnIndex);
		if (java.lang.Boolean.class.equals(memberType)
				|| Boolean.TYPE.equals(memberType))
			return Boolean.valueOf(result.getBoolean(columnIndex));
		if (java.lang.Byte.class.equals(memberType)
				|| Byte.TYPE.equals(memberType))
			return Byte.valueOf(result.getByte(columnIndex));
		if (java.lang.Double.class.equals(memberType)
				|| Double.TYPE.equals(memberType))
			return Double.valueOf(result.getDouble(columnIndex));
		if (java.lang.Float.class.equals(memberType)
				|| Float.TYPE.equals(memberType))
			return Float.valueOf(result.getFloat(columnIndex));
		if (java.lang.Integer.class.equals(memberType)
				|| Integer.TYPE.equals(memberType))
			return Integer.valueOf(result.getInt(columnIndex));
		if (java.lang.Long.class.equals(memberType)
				|| Long.TYPE.equals(memberType))
			return Long.valueOf(result.getLong(columnIndex));
		if (java.lang.Short.class.equals(memberType)
				|| Short.TYPE.equals(memberType))
			return Short.valueOf(result.getShort(columnIndex));
		if (java.math.BigInteger.class.equals(memberType)) {
			BigDecimal val = result.getBigDecimal(columnIndex);
			if (val != null)
				return val.toBigInteger();
			else
				return null;
		}
		if (java.math.BigDecimal.class.equals(memberType))
			return result.getBigDecimal(columnIndex);
		if (java.io.InputStream.class.equals(memberType))
			return result.getBinaryStream(columnIndex);
		if (java.sql.Blob.class.equals(memberType))
			return result.getBlob(columnIndex);
		if (java.io.Reader.class.equals(memberType))
			return result.getCharacterStream(columnIndex);
		if (java.sql.Clob.class.equals(memberType))
			return result.getClob(columnIndex);
		if (java.sql.Date.class.equals(memberType))
			return result.getDate(columnIndex);
		if (java.util.Date.class.equals(memberType))
			return result.getTimestamp(columnIndex);
		if (java.sql.Ref.class.equals(memberType))
			return result.getRef(columnIndex);
		if (java.lang.String.class.equals(memberType))
			return result.getString(columnIndex);
		if (java.sql.Time.class.equals(memberType))
			return result.getTime(columnIndex);
		if (java.sql.Timestamp.class.equals(memberType))
			return result.getTimestamp(columnIndex);
		if (java.net.URL.class.equals(memberType))
			return result.getURL(columnIndex);
		if (java.lang.Object.class.equals(memberType))
			return result.getObject(columnIndex);
		if (java.sql.SQLXML.class.equals(memberType))
			return result.getSQLXML(columnIndex);
		else
			return result.getObject(columnIndex);
	}

	public Map readValue2Map(Object pojo) throws Exception {
		Map getMethodMap = ObjectMappingCache.getInstance().getPojoGetMethod(
				pojo.getClass());
		Map model = new HashMap();
		String colName;
		Object val;
		for (Iterator iterator = getMethodMap.keySet().iterator(); iterator
				.hasNext(); model.put(colName, val)) {
			colName = (String) iterator.next();
			Method getMethod = (Method) getMethodMap.get(colName);
			val = getMethod.invoke(pojo, new Object[0]);
		}

		return model;
	}

	public boolean writePkVal2Pojo(Object pojo, Object pkval,
			String pkcolumnName) throws Exception {
		Map setMethods = ObjectMappingCache.getInstance().getPojoSetMethod(
				pojo.getClass());
		Method setMethod = (Method) setMethods.get(pkcolumnName);
		if (setMethod != null) {
			setMethod.invoke(pojo, new Object[] { pkval });
			return true;
		} else {
			return false;
		}
	}

	public Map readToMap(ResultSet rs, ResultSetMetaData rsmd) throws Exception {
		Map valMap = new LinkedHashMap();
		int count = rsmd.getColumnCount();
		for (int i = 1; i <= count; i++) {
			String columnName = rsmd.getColumnLabel(i);
			if (columnName == null || columnName.length() == 0)
				columnName = rsmd.getColumnName(i);
			Object val = rs.getObject(i);
			valMap.put(columnName.toUpperCase(), val);
		}

		return valMap;
	}

	public ClassMapping getClassMapping(Class cls) {
		return ObjectMappingCache.getInstance().getClassMapping(cls);
	}

	public FieldMapping[] getClassPrimaryKeys(Class cls, String keyOrder) {
		Map fieldMapping = ObjectMappingCache.getInstance().getObjectFieldMap(
				cls);
		if (fieldMapping != null)
			if (keyOrder == null || keyOrder.length() == 0) {
				for (Iterator iterator = fieldMapping.values().iterator(); iterator
						.hasNext();) {
					FieldMapping field = (FieldMapping) iterator.next();
					if (field.primary())
						return (new FieldMapping[] { field });
				}

			} else {
				String keyOArray[] = keyOrder.split(",");
				FieldMapping fields[] = new FieldMapping[keyOArray.length];
				int idx = 0;
				String as[];
				int j = (as = keyOArray).length;
				for (int i = 0; i < j; i++) {
					String key = as[i];
					fields[idx] = (FieldMapping) fieldMapping.get(key);
					idx++;
				}

				return fields;
			}
		return null;
	}

	public Map getObjectFieldMap(Class cls) {
		return ObjectMappingCache.getInstance().getObjectFieldMap(cls);
	}

	public Class getTargetSqlClass(int sqlType) {
		Class cls = null;
		/* 256 */switch (sqlType) {
		/*     */case 91:
			/* 258 */cls = java.sql.Date.class;
			/* 259 */break;
		/*     */case 92:
			/* 261 */cls = Time.class;
			/* 262 */break;
		/*     */case 93:
			/* 264 */cls = Timestamp.class;
			/* 265 */break;
		/*     */}

		return cls;
	}
}