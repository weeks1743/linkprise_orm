package com.linkprise.orm.convert;

public abstract interface ITypeConverter {
	public abstract Object convert(Object paramObject, Class<?> paramClass)
			throws TypeConvertException;
}