package com.linkprise.orm.mapping;

import java.sql.Blob;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;

import com.linkprise.utils.Utils;

public class JsonReader implements IJsonReader {
	private static final String[] REPLACEMENT_CHARS = new String[''];
	private static final String[] HTML_SAFE_REPLACEMENT_CHARS;
	private boolean htmlSafe = true;

	private String dataFormatParten = "yyyy-MM-dd HH:mm:ss";

	static {
		for (int i = 0; i <= 31; i++) {
			REPLACEMENT_CHARS[i] = String.format("\\u%04x",
					new Object[] { Integer.valueOf(i) });
		}
		REPLACEMENT_CHARS[34] = "\\\"";
		REPLACEMENT_CHARS[92] = "\\\\";
		REPLACEMENT_CHARS[9] = "\\t";
		REPLACEMENT_CHARS[8] = "\\b";
		REPLACEMENT_CHARS[10] = "\\n";
		REPLACEMENT_CHARS[13] = "\\r";
		REPLACEMENT_CHARS[12] = "\\f";
		HTML_SAFE_REPLACEMENT_CHARS = (String[]) REPLACEMENT_CHARS.clone();
		HTML_SAFE_REPLACEMENT_CHARS[60] = "\\u003c";
		HTML_SAFE_REPLACEMENT_CHARS[62] = "\\u003e";
		HTML_SAFE_REPLACEMENT_CHARS[38] = "\\u0026";
		HTML_SAFE_REPLACEMENT_CHARS[61] = "\\u003d";
		HTML_SAFE_REPLACEMENT_CHARS[39] = "\\u0027";
	}

	public String read(ResultSet result, ResultSetMetaData rsmd)
			throws Exception {
		int count = rsmd.getColumnCount();
		if (count == 1) {
			Object val = result.getObject(1);
			if (val == null) {
				return "null";
			}
			int columnType = rsmd.getColumnType(1);
			String strval = getValue(result, 1, columnType);
			if (isNumberOrBoolean(columnType)) {
				return strval == null ? "null" : strval;
			}
			return string(strval);
		}

		StringBuilder sb = new StringBuilder();
		sb.append('{');
		for (int i = 1; i <= count; i++) {
			String columnName = rsmd.getColumnLabel(i);
			if ((columnName == null) || (columnName.length() == 0)) {
				columnName = rsmd.getColumnName(i);
			}
			if (i > 1) {
				sb.append(',');
			}
			sb.append('"').append(columnName).append("\":");
			int columnType = rsmd.getColumnType(i);
			String strval = getValue(result, i, columnType);
			if (isNumberOrBoolean(columnType)) {
				if (strval == null)
					sb.append("null");
				else
					sb.append(strval);
			} else {
				sb.append(string(strval));
			}
		}
		sb.append('}');
		return sb.toString();
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

	private boolean isNumberOrBoolean(int sqlType) {
		boolean number = false;
		switch (sqlType) {
		case -7:
		case -6:
		case -5:
		case 2:
		case 3:
		case 4:
		case 5:
		case 6:
		case 7:
		case 8:
		case 16:
			number = true;
			break;
		case -4:
		case -3:
		case -2:
		case -1:
		case 0:
		case 1:
		case 9:
		case 10:
		case 11:
		case 12:
		case 13:
		case 14:
		case 15:
		}
		return number;
	}

	private String string(String value) {
		if (value == null) {
			return "null";
		}
		StringBuilder out = new StringBuilder();
		String[] replacements = this.htmlSafe ? HTML_SAFE_REPLACEMENT_CHARS
				: REPLACEMENT_CHARS;
		out.append('"');
		int length = value.length();
		for (int i = 0; i < length; i++) {
			char c = value.charAt(i);
			String replacement = null;
			if (c < '')
				replacement = replacements[c];
			else if (c == ' ')
				replacement = "\\u2028";
			else if (c == ' ') {
				replacement = "\\u2029";
			}
			if (replacement == null)
				out.append(c);
			else {
				out.append(replacement);
			}
		}
		out.append('"');
		return out.toString();
	}

	public void setDataFormatParten(String dataFormatParten) {
		this.dataFormatParten = dataFormatParten;
	}

	public void setHtmlSafe(boolean htmlSafe) {
		this.htmlSafe = htmlSafe;
	}
}