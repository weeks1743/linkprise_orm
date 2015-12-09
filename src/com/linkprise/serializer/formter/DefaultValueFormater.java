package com.linkprise.serializer.formter;

import java.math.BigDecimal;

import com.linkprise.utils.Utils;

public class DefaultValueFormater
  implements IValueFormater
{
  private static final String EMPTY_STRING = "";
  private String dateParten = "yyyy-MM-dd HH:mm:ss";
  private String numberParten = null;

  public String format(Object obj)
    throws Exception
  {
    if (obj != null) {
      if ((obj instanceof String)) {
        return (String)obj;
      }
      if ((obj instanceof byte[])) {
        return new String((byte[])obj);
      }
      if (((obj instanceof java.sql.Date)) || ((obj instanceof java.util.Date))) {
        return Utils.dateFormat(obj, this.dateParten);
      }
      if ((this.numberParten != null) && (
        ((obj instanceof Float)) || ((obj instanceof Double)) || ((obj instanceof BigDecimal)))) {
        return Utils.bigDicimalFormat(obj, this.numberParten);
      }

      return obj.toString();
    }
    return "";
  }

  public void setDateParten(String dateParten)
  {
    this.dateParten = dateParten;
  }

  public void setNumberParten(String numberParten)
  {
    this.numberParten = numberParten;
  }
}