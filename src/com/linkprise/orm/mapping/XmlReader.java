package com.linkprise.orm.mapping;

import java.sql.Blob;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.linkprise.utils.Utils;

public class XmlReader implements IXmlReader {
	private String dataFormatParten = "yyyy-MM-dd HH:mm:ss";

	public Element read(String itemname, ResultSet result,
			ResultSetMetaData rsmd) throws Exception {
		Element dataElement = DocumentHelper.createElement(itemname);
		int count = rsmd.getColumnCount();
		for (int i = 1; i <= count; i++) {
			String columnName = rsmd.getColumnLabel(i);
			if ((columnName == null) || (columnName.length() == 0)) {
				columnName = rsmd.getColumnName(i);
			}
			int columnType = rsmd.getColumnType(i);
			Element curElement = dataElement.addElement(columnName);
			String val = getValue(result, i, columnType);
			if (val != null) {
				curElement.addText(val);
			}

		}

		return dataElement;
	}

	protected String getValue(ResultSet result, int columnIndex, int columnType)
			throws SQLException {
		String val = null;
		switch (columnType) {
		case 93:
			Timestamp timestamp = result.getTimestamp(columnIndex);
			if (timestamp == null)
				break;
			val = Utils.dateFormat(timestamp, this.dataFormatParten);

			break;
		case 91:
			Date date = result.getDate(columnIndex);
			if (date == null)
				break;
			val = Utils.dateFormat(date, this.dataFormatParten);

			break;
		case 92:
			Time time = result.getTime(columnIndex);
			if (time == null)
				break;
			val = Utils.dateFormat(time, "HH:mm:ss");

			break;
		case 2005:
			val = result.getString(columnIndex);
			break;
		case 2004:
			Blob blob = result.getBlob(columnIndex);
			if (blob == null)
				break;
			val = new String(blob.getBytes(1L, (int) blob.length()));

			break;
		default:
			val = result.getString(columnIndex);
		}

		return val;
	}

	public void setDataFormatParten(String dataFormatParten) {
		this.dataFormatParten = dataFormatParten;
	}
}