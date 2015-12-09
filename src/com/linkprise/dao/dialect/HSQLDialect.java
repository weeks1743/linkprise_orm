package com.linkprise.dao.dialect;

public class HSQLDialect
  implements Dialect
{
  private int hsqldbVersion = 18;

  public boolean supportsOffset()
  {
    return true;
  }

  public String getLimitString(String sql, int offset, int limit)
  {
    if (this.hsqldbVersion < 20)
    {
      String insert;
      if (offset > 0)
        insert = String.format(" limit %d %d", new Object[] { Integer.valueOf(offset), Integer.valueOf(limit) });
      else {
        insert = String.format(" top %d", new Object[] { Integer.valueOf(limit) });
      }
      return new StringBuffer(sql.length() + 10)
        .append(sql)
        .insert(
        sql.toLowerCase().indexOf("select") + 6, 
        insert)
        .toString();
    }
    StringBuffer sb = new StringBuffer(sql.length() + 20);
    sb.append(sql);
    if (offset > 0)
      sb.append(" offset ").append(offset).append(" limit ").append(limit);
    else {
      sb.append(" limit ").append(limit);
    }
    return sb.toString();
  }

  public String getSelectGUIDString()
  {
    throw new UnsupportedOperationException(getClass().getName() + " does not support GUIDs");
  }

  public String getSequenceNextValString(String sequenceName)
  {
    return "call next value for " + sequenceName;
  }
}