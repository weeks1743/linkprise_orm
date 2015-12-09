package com.linkprise.orm.convert;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SqlConverter implements IGenericConverter {
	public Object convert(Object source, Class<?> targetType)
			throws TypeConvertException {
		java.util.Date jDate = (java.util.Date) source;
		if (targetType.isAssignableFrom(java.sql.Date.class)) {
			return new java.sql.Date(jDate.getTime());
		}
		if (targetType.isAssignableFrom(Time.class)) {
			return new Time(jDate.getTime());
		}
		if (targetType.isAssignableFrom(Timestamp.class)) {
			return new Timestamp(jDate.getTime());
		}
		throw new TypeConvertException(source, targetType);
	}

	public List<Class<?>> getTargetTypes(Class<?> sourceType) {
		if (java.util.Date.class.isAssignableFrom(sourceType)) {
			List list = new ArrayList(3);
			list.add(java.sql.Date.class);
			list.add(Time.class);
			list.add(Timestamp.class);
			return list;
		}
		return Collections.emptyList();
	}
}