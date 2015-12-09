package com.linkprise.dao.common.nativejdbc;

import java.sql.Connection;
import java.sql.SQLException;

public abstract interface INativeJdbcExtractor
{
  public abstract Connection doGetNativeConnection(Connection paramConnection)
    throws SQLException;
}