package com.linkprise.orm.convert;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class BaseConverter implements IGenericConverter {
	public Object convert(Object source, Class<?> targetType)
			throws TypeConvertException {
		try {
			if ((source instanceof Calendar)) {
				Calendar cal = (Calendar) source;
				return cal.getTime();
			}
			if ((source instanceof Date)) {
				Date date = (Date) source;
				Calendar cal = Calendar.getInstance();
				cal.setTime(date);
				return cal;
			}

			Number num = (Number) source;

			if ((targetType == Integer.class) || (targetType == Integer.TYPE))
				return Integer.valueOf(num.intValue());
			if ((targetType == Byte.class) || (targetType == Byte.TYPE))
				return Byte.valueOf(num.byteValue());
			if ((targetType == Double.class) || (targetType == Double.TYPE))
				return Double.valueOf(num.doubleValue());
			if ((targetType == Float.class) || (targetType == Float.TYPE))
				return Float.valueOf(num.floatValue());
			if ((targetType == Long.class) || (targetType == Long.TYPE))
				return Long.valueOf(num.longValue());
			if ((targetType == Short.class) || (targetType == Short.TYPE))
				return Short.valueOf(num.shortValue());
			if (targetType == BigDecimal.class)
				return new BigDecimal(num.doubleValue());
			if (targetType == BigInteger.class)
				return BigInteger.valueOf(num.longValue());
		} catch (Exception e) {
			throw new TypeConvertException(source, targetType, e);
		}
		throw new TypeConvertException(source, targetType);
	}

	public List<Class<?>> getTargetTypes(Class<?> sourceType) {
		List list = null;
		if (Date.class.isAssignableFrom(sourceType)) {
			list = new ArrayList(1);
			list.add(Calendar.class);
		} else if (Calendar.class.isAssignableFrom(sourceType)) {
			list = new ArrayList(1);
			list.add(Date.class);
		} else if (Number.class.isAssignableFrom(sourceType)) {
			list = new ArrayList(13);
			list.add(Byte.class);
			list.add(Double.class);
			list.add(Float.class);
			list.add(Integer.class);
			list.add(Long.class);
			list.add(Short.class);
			list.add(BigDecimal.class);
			list.add(Byte.TYPE);
			list.add(Double.TYPE);
			list.add(Float.TYPE);
			list.add(Integer.TYPE);
			list.add(Long.TYPE);
			list.add(Short.TYPE);
		} else {
			list = Collections.emptyList();
		}
		return list;
	}
}