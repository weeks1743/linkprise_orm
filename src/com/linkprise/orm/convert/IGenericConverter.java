package com.linkprise.orm.convert;

import java.util.List;

public abstract interface IGenericConverter extends ITypeConverter {
	public abstract List<Class<?>> getTargetTypes(Class<?> paramClass);
}