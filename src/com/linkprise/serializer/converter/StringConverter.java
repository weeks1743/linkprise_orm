package com.linkprise.serializer.converter;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import com.linkprise.orm.convert.ITypeConverter;
import com.linkprise.orm.convert.TypeConvertException;

public class StringConverter
  implements ITypeConverter
{
  private String dateParten = "yyyy-MM-dd HH:mm:ss";

  public Object convert(Object source, Class<?> targetType)
    throws TypeConvertException
  {
    if (source != null) {
      String srcval = (String)source;
      if ((targetType == Integer.class) || (targetType == Integer.TYPE))
        return Integer.valueOf(srcval);
      if ((targetType == Byte.class) || (targetType == Byte.TYPE))
        return Byte.valueOf(srcval);
      if ((targetType == Double.class) || (targetType == Double.TYPE))
        return Double.valueOf(srcval);
      if ((targetType == Float.class) || (targetType == Float.TYPE))
        return Float.valueOf(srcval);
      if ((targetType == Long.class) || (targetType == Long.TYPE))
        return Long.valueOf(srcval);
      if ((targetType == Short.class) || (targetType == Short.TYPE))
        return Short.valueOf(srcval);
      if ((targetType == Boolean.class) || (targetType == Boolean.TYPE))
        return Boolean.valueOf(srcval);
      if (targetType == BigDecimal.class)
        return new BigDecimal(srcval);
      if (targetType == BigInteger.class)
        return new BigInteger(srcval);
      if(targetType == byte[].class)
        return srcval.getBytes();
      if (targetType == Timestamp.class) {
        Long time = parserDate(srcval);
        if (time == null) {
          return null;
        }
        return new Timestamp(time.longValue());
      }

      if (targetType == java.sql.Date.class) {
        Long time = parserDate(srcval);
        if (time == null) {
          return null;
        }
        return new java.sql.Date(time.longValue());
      }

      if (targetType == java.util.Date.class) {
        Long time = parserDate(srcval);
        if (time == null) {
          return null;
        }
        return new java.util.Date(time.longValue());
      }

      throw new TypeConvertException(source, targetType);
    }
    return null;
  }

  private Long parserDate(String strDate) {
    SimpleDateFormat sdf = new SimpleDateFormat(this.dateParten);
    java.util.Date dt = null;
    try {
      dt = sdf.parse(strDate);
    } catch (Exception e) {
      return null;
    }
    return Long.valueOf(dt.getTime());
  }

  public String getDateParten()
  {
    return this.dateParten;
  }

  public void setDateParten(String dateParten)
  {
    this.dateParten = dateParten;
  }
}