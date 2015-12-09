package com.linkprise.orm.mapping;

import java.lang.reflect.Method;

public class ClassUtil {
	/**
	 * �ж�Ŀ�����ǲ����������
	 * @param targetClass
	 * @param superClass
	 * @return
	 */
	public static boolean isSubclassOf(Class<?> targetClass, Class<?> superClass) {
		Class targetSuperClass = targetClass.getSuperclass();
		while (targetSuperClass != null) {
			if (targetSuperClass.equals(superClass))
				return true;
			targetSuperClass = targetSuperClass.getSuperclass();
		}
		return false;
	}

	/**
	 * Ŀ�����Ƿ�ʵ����ָ���ӿ�
	 * @param targetClass
	 * @param theInterface
	 * @return
	 */
	public static boolean isInterfaceOrSubInterfaceImplemented(
			Class<?> targetClass, Class<?> theInterface) {
		Class[] implementedInterfaces = targetClass.getInterfaces();
		for (Class implementedInterface : implementedInterfaces) {
			if (implementedInterface.equals(theInterface))
				return true;
			Class superInterface = implementedInterface.getSuperclass();
			while (superInterface != null) {
				if (superInterface.equals(theInterface))
					return true;
				superInterface = superInterface.getSuperclass();
			}
		}
		return false;
	}
	/**
	 * �����ǲ���get����
	 * @param member
	 * @return
	 */
	public static boolean isGetter(Method member) {
		if (member == null) {
			throw new NullPointerException("No Method instance provided");
		}

		if (member.getParameterTypes().length > 0) {
			return false;
		}

		if ((member.getReturnType() == Void.TYPE)
				|| (member.getReturnType() == null)) {
			return false;
		}

		return (member.getName().startsWith("get"))
				|| (member.getName().startsWith("is"));
	}

	/**
	 * ������ ����set����
	 * @param member
	 * @return
	 */
	public static boolean isSetter(Method member) {
		if (member == null) {
			throw new NullPointerException("No Method instance provided");
		}

		if (!member.getName().startsWith("set")) {
			return false;
		}

		return member.getParameterTypes().length == 1;
	}

	/**
	 * ��ȡclass�����֣�ȥ���
	 * @param objectClass
	 * @return
	 */
	public static String classNameWithoutPackage(Class<?> objectClass) {
		return classNameWithoutPackage(objectClass.getName());
	}

    /**
     * ��ȡclass�����֣�ȥ���
     * @param fullClassName �������� 
     * @return
     */
	public static String classNameWithoutPackage(String fullClassName) {
		return fullClassName.substring(fullClassName.lastIndexOf(".") + 1,
				fullClassName.length());
	}
}