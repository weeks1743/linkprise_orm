package com.linkprise.dao.common;

import java.io.Serializable;

public class SqlParameter {
	private String name;
	private Serializable value;
	private Class<?> ormClass = null;

	public SqlParameter() {
	}

	public SqlParameter(String name, Serializable value) {
		this.name = name;
		this.value = value;
	}

	public SqlParameter(String name, Serializable value, Class<?> ormClass) {
		this.name = name;
		this.value = value;
		this.ormClass = ormClass;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Serializable getValue() {
		return this.value;
	}

	public void setValue(Serializable value) {
		this.value = value;
	}

	public Class<?> getOrmClass() {
		return this.ormClass;
	}

	public void setOrmClass(Class<?> ormClass) {
		this.ormClass = ormClass;
	}

	public String toString() {
		return String.format("%s=%s", new Object[] { this.name, this.value });
	}
}