package com.linkprise.orm.mapping;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.linkprise.orm.annotation.ClassMapping;
import com.linkprise.orm.annotation.FieldMapping;

public class ObjectMappingCache {
	private static final String _FILTERED_FIELD = "class";
	private static ObjectMappingCache _instance = new ObjectMappingCache();
	private Map<Class<?>, Map<String, PropertyDescriptor>> objectPropertyMap = null;
	private Map<Class<?>, Map<String, FieldMapping>> objectFieldMap = null;
	private Map<Class<?>, ClassMapping> classMappingMap = null;

	private ObjectMappingCache() {
		this.objectPropertyMap = new HashMap();
		this.objectFieldMap = new HashMap();
		this.classMappingMap = new HashMap();
	}

	public static ObjectMappingCache getInstance() {
		return _instance;
	}

	/**
	 * 获取get方法
	 * @param cls
	 * @param columnName
	 * @param tableName
	 * @return
	 */
	public Map<String, Method> getPojoSetMethod(Class<?> cls) {
		Map propertys = (Map) objectPropertyMap.get(cls);
		if (propertys == null)
			propertys = initClassProperty(cls);
		Map methodsMap = new HashMap();
		String key;
		for (Iterator iterator = propertys.keySet().iterator(); iterator.hasNext();){
			key = (String) iterator.next();
			methodsMap.put(key,((PropertyDescriptor) propertys.get(key)).getWriteMethod());
		} 

		return methodsMap;
	}

	/**
	 *  获取所有get方法
	 * @param cls
	 * @param tableName
	 * @return
	 */
	public Map<String, Method> getPojoGetMethod(Class<?> cls) {
		Map propertys = (Map) objectPropertyMap.get(cls);
		if (propertys == null)
			propertys = initClassProperty(cls);
		Map methodsMap = new HashMap();
		String key;
		for (Iterator iterator = propertys.keySet().iterator(); iterator
				.hasNext(); methodsMap.put(key,
				((PropertyDescriptor) propertys.get(key)).getReadMethod()))
			key = (String) iterator.next();

		return methodsMap;
	}

	public ClassMapping getClassMapping(Class<?> cls) {
		ClassMapping clsMapping = (ClassMapping) this.classMappingMap.get(cls);
		if (clsMapping == null) {
			clsMapping = (ClassMapping) cls.getAnnotation(ClassMapping.class);
			if (clsMapping != null) {
				this.classMappingMap.put(cls, clsMapping);
			}
		}
		return clsMapping;
	}

	private Map<String, PropertyDescriptor> initClassProperty(Class<?> cls) {
		Map propMap = new HashMap();
		try {
			BeanInfo beanInfo = Introspector.getBeanInfo(cls, Object.class);
			PropertyDescriptor[] props = beanInfo.getPropertyDescriptors();

			Map fieldMappings = (Map) this.objectFieldMap.get(cls);
			if (fieldMappings == null) {
				fieldMappings = initClassFieldMapping(cls);
			}
			for (PropertyDescriptor propertyDescriptor : props) {
				String fieldName = propertyDescriptor.getName();
				if (Character.isUpperCase(fieldName.charAt(0))) {
					char[] chars = fieldName.toCharArray();
					chars[0] = Character.toLowerCase(chars[0]);
					fieldName = new String(chars);
				}
				if (!"class".equals(fieldName)) {
					FieldMapping fieldmapping = (FieldMapping) fieldMappings
							.get(fieldName);
					if (fieldmapping == null) {
						propMap.put(fieldName.toUpperCase(), propertyDescriptor);
					} else
						propMap.put(fieldmapping.columnName(),
								propertyDescriptor);
				}
			}

			this.objectPropertyMap.put(cls, propMap);
		} catch (IntrospectionException e) {
			e.printStackTrace();
		}
		return propMap;
	}

	private Map<String, FieldMapping> initClassFieldMapping(Class<?> cls) {
		Map fieldMappings = new HashMap();
		while ((cls != null) && (!cls.equals(Object.class))) {
			Field[] flds = cls.getDeclaredFields();
			for (Field field : flds) {
				if (((field.getModifiers() & 0x8) != 0)
						|| ((field.getModifiers() & 0x10) != 0)) {
					continue;
				}
				FieldMapping fieldMapping = (FieldMapping) field
						.getAnnotation(FieldMapping.class);
				if (fieldMapping != null) {
					fieldMappings.put(field.getName(), fieldMapping);
				}
			}
			cls = cls.getSuperclass();
		}
		this.objectFieldMap.put(cls, fieldMappings);
		return fieldMappings;
	}

	public Map<String, FieldMapping> getObjectFieldMap(Class<?> cls) {
		Map fieldMappings = (Map) this.objectFieldMap.get(cls);
		if (fieldMappings == null) {
			fieldMappings = initClassFieldMapping(cls);
		}
		return fieldMappings;
	}

	public Map<String, PropertyDescriptor> getObjectPropertyMap(Class<?> cls) {
		Map propMap = (Map) this.objectPropertyMap.get(cls);
		if (propMap == null) {
			propMap = initClassProperty(cls);
		}
		return propMap;
	}
}