package com.linkprise.orm.mapping;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import org.dom4j.Element;

public abstract interface IXmlReader {
	public abstract Element read(String paramString, ResultSet paramResultSet,
			ResultSetMetaData paramResultSetMetaData) throws Exception;
}