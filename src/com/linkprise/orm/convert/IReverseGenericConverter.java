package com.linkprise.orm.convert;

import java.util.List;

public abstract interface IReverseGenericConverter extends ITypeConverter {
	public abstract List<Class<?>> getSourceTypes(Class<?> paramClass);
}