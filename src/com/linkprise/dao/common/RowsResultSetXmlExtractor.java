package com.linkprise.dao.common;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.linkprise.orm.mapping.IXmlReader;

public class RowsResultSetXmlExtractor implements ResultSetExtractor<Document> {
	private int offset = 0;
	private int max = 2147483647;

	private String rootName = null;
	private String itemName = null;

	private IXmlReader xmlReader = null;

	public RowsResultSetXmlExtractor(IXmlReader xmlReader, String rootName,
			String itemName) {
		this.xmlReader = xmlReader;
		this.rootName = rootName;
		this.itemName = itemName;
	}

	public Document extractData(ResultSet rs) throws SQLException {
		Document results = DocumentHelper.createDocument();
		Element rootElement = results.addElement(this.rootName);
		ResultSetMetaData rsmd = rs.getMetaData();
		int pos = 0;
		int len = 0;
		while (rs.next()) {
			if (pos >= this.offset) {
				try {
					rootElement.add(this.xmlReader
							.read(this.itemName, rs, rsmd));
					len++;
				} catch (Exception e) {
					throw new SQLException(e);
				}
			}
			if (len >= this.max) {
				break;
			}
			pos++;
		}
		return results;
	}

	public int getOffset() {
		return this.offset;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

	public int getMax() {
		return this.max;
	}

	public void setMax(int max) {
		this.max = max;
	}

	public void setRootName(String rootName) {
		this.rootName = rootName;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	public String getRootName() {
		return this.rootName;
	}

	public String getItemName() {
		return this.itemName;
	}
}