package com.linkprise.orm.convert;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

public class ReflectionReverseGenericConverter implements
		IReverseGenericConverter {
	private final ConcurrentHashMap<Class<?>, Map<Class<?>, Constructor<?>>> _cache = new ConcurrentHashMap();

	private static final Map<Class<?>, Constructor<?>> _EMPTY_CONSTRUCTOR_MAP = Collections
			.emptyMap();
	private static final List<Class<?>> _EMPTY_SOURCE_LIST = Collections
			.emptyList();

	public Object convert(Object source, Class<?> targetType)
			throws TypeConvertException {
		Map constructors = _getConstructorMapForTarget(targetType);
		Class sourceClass = source.getClass();
		Constructor c = (Constructor) constructors.get(sourceClass);
		if (c == null) {
			for (Iterator iterator = constructors.entrySet().iterator(); iterator
					.hasNext();) {
				java.util.Map.Entry entry = (java.util.Map.Entry) iterator
						.next();
				if (((Class) entry.getKey()).isAssignableFrom(sourceClass)) {
					c = (Constructor) entry.getValue();
					break;
				}
			}

		}
		if (c != null)
			try {
				return c.newInstance(new Object[] { source });
			} catch (Exception e) {
				throw new TypeConvertException(source, targetType, e);
			}
		else
			throw new TypeConvertException(source, targetType);
	}

	public List<Class<?>> getSourceTypes(Class<?> targetType) {
		Map cachedConstructors = _getConstructorMapForTarget(targetType);

		if (cachedConstructors == _EMPTY_CONSTRUCTOR_MAP) {
			return _EMPTY_SOURCE_LIST;
		}
		return new ArrayList(cachedConstructors.keySet());
	}

	private Map<Class<?>, Constructor<?>> _getConstructorMapForTarget(
			Class<?> targetType) {
		Map cachedConstructors = (Map) this._cache.get(targetType);

		if (cachedConstructors == null) {
			cachedConstructors = _EMPTY_CONSTRUCTOR_MAP;

			Constructor[] constructors = targetType.getConstructors();
			for (Constructor c : constructors) {
				if ((Modifier.isPublic(c.getModifiers()))
						&& (c.getAnnotation(Deprecated.class) == null)) {
					Class[] params = c.getParameterTypes();

					if (params.length == 1) {
						if (cachedConstructors == _EMPTY_CONSTRUCTOR_MAP) {
							cachedConstructors = new HashMap();
						}
						cachedConstructors.put(params[0], c);
					}
				}
			}

			this._cache.put(targetType, cachedConstructors);
		}
		return cachedConstructors;
	}
}