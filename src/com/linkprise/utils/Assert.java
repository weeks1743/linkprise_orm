package com.linkprise.utils;

public class Assert {
	public static void isNull(Object object, String message) {
		if (object != null)
			throw new IllegalArgumentException(message);
	}

	public static void isNull(Object object) {
		isNull(object, "[Assertion failed] - the object argument must be null");
	}

	public static void notNull(Object object, String message) {
		if (object == null)
			throw new IllegalArgumentException(message);
	}

	public static void notNull(Object object) {
		notNull(object,
				"[Assertion failed] - this argument is required; it must not be null");
	}

	public static void notEmpty(Object[] objects) {
		notEmpty(objects,
				"[Assertion failed] - this argument is required; it must not be null");
	}

	public static void notEmpty(Object[] objects, String message) {
		if ((objects == null) || (objects.length == 0))
			throw new IllegalArgumentException(message);
	}
}