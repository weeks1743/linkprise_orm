package com.linkprise.orm.convert;

public class TypeConvertException extends Exception {
	private static final long serialVersionUID = 1L;

	public TypeConvertException(Object source, Class<?> targetType,
			Throwable error) {
		super(_getMessage(source, targetType), error);
	}

	public TypeConvertException(Object source, Class<?> targetType) {
		super(_getMessage(source, targetType));
	}

	private static String _getMessage(Object source, Class<?> targetType) {
		return "Could not convert instance:" + source + " of type:"
				+ source.getClass() + " into type:" + targetType;
	}
}