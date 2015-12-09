package com.linkprise.orm.mapping;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.Map;

import com.linkprise.orm.annotation.ClassMapping;
import com.linkprise.orm.annotation.FieldMapping;

public abstract interface IObjectReader {
	public abstract <T> T read(Class<T> paramClass, ResultSet paramResultSet,
			ResultSetMetaData paramResultSetMetaData) throws Exception;

	public abstract Map<String, Object> readValue2Map(Object paramObject)
			throws Exception;

	public abstract boolean writePkVal2Pojo(Object paramObject1,
			Object paramObject2, String paramString) throws Exception;

	public abstract Map<String, Object> readToMap(ResultSet paramResultSet,
			ResultSetMetaData paramResultSetMetaData) throws Exception;

	public abstract <T> ClassMapping getClassMapping(Class<T> paramClass);

	public abstract FieldMapping[] getClassPrimaryKeys(Class<?> paramClass,
			String paramString);

	public abstract Map<String, FieldMapping> getObjectFieldMap(
			Class<?> paramClass);

	public abstract Class<?> getTargetSqlClass(int paramInt);
}