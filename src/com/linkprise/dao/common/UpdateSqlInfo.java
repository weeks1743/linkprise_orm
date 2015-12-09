package com.linkprise.dao.common;

import java.util.ArrayList;
import java.util.List;

import com.linkprise.orm.annotation.FieldMapping;

public class UpdateSqlInfo {
	private String sql = null;
	private List<FieldMapping> parameterMappings = null;

	public UpdateSqlInfo() {
		this.parameterMappings = new ArrayList();
	}

	public UpdateSqlInfo(String sql) {
		this.sql = sql;
		this.parameterMappings = new ArrayList();
	}

	public String getSql() {
		return this.sql;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}

	public List<FieldMapping> getParameterMappings() {
		return this.parameterMappings;
	}

	public void addParameter(FieldMapping field) {
		this.parameterMappings.add(field);
	}
}