package com.linkprise.pojo.generator;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import com.linkprise.DataBaseType;

/**
 * POJO 生成器
 * 
 * @author <a href="mailto:wt47@live.com">linkprise.com 犬少</a>
 * @version 1.0.0
 * ＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝<br/>
 * 修订日期                 修订人            描述<br/>
 * 2016-1-18       linkprise.com 犬少            创建<br/>
 */
public class PojoGenerator
{
  private Connection con;
  private String destfile = "./src";
  private File destination;
  private String packageName = "com.linkprise";
  private String driver = "";
  private String url = "";
  private String username = "";
  private String password = "";
  private String prefix;

  public PojoGenerator(String driver, String url, String username, String password, String packageName)
  {
    this.driver = driver;
    this.url = url;
    this.username = username;
    this.password = password;
    this.packageName = packageName;
    this.destination = new File(this.destfile);
  }

  public PojoGenerator(String driver, String url, String username, String password, String packageName, String destination) {
    this.driver = driver;
    this.url = url;
    this.username = username;
    this.password = password;
    this.packageName = packageName;
    this.destfile = destination;
    this.destination = new File(this.destfile);
  }

  public DataBaseType guessDataBaseType()
  {
    String dbDriver = this.driver.toLowerCase();
    if (dbDriver.indexOf("oracledriver") >= 0)
      return DataBaseType.ORACLE;
    if (dbDriver.indexOf("db2driver") >= 0)
      return DataBaseType.DB2;
    if (dbDriver.indexOf("postgresql") >= 0)
      return DataBaseType.PSQL;
    if (dbDriver.indexOf("sqlserverdriver") >= 0)
      return DataBaseType.SQLSERVER;
    if (dbDriver.indexOf("mysql") >= 0)
      return DataBaseType.MYSQL;
    if (dbDriver.indexOf("h2") >= 0) {
      return DataBaseType.H2;
    }

    if (dbDriver.indexOf("hsqldb") >= 0)
      return DataBaseType.HSQL;
    if (dbDriver.indexOf("derby") >= 0)
      return DataBaseType.DERBY;
    if (dbDriver.indexOf("firebirdsql") >= 0)
      return DataBaseType.FIREBIRD;
    if (dbDriver.indexOf("interbase") >= 0)
      return DataBaseType.INTERBASE;
    if (dbDriver.indexOf("informix") >= 0)
      return DataBaseType.INFORMIX;
    if (dbDriver.indexOf("ingres") >= 0)
      return DataBaseType.INGRES10;
    if (dbDriver.indexOf("rdms2200") >= 0)
      return DataBaseType.RDMS2200;
    if (dbDriver.indexOf("timesten") >= 0) {
      return DataBaseType.TIMESTEN;
    }
    return DataBaseType.OTHER;
  }

  public String getDestfile()
  {
    return this.destfile;
  }

  public String getPackageName()
  {
    return this.packageName;
  }

  public String getPrefix()
  {
    return this.prefix;
  }

  public void setPrefix(String prefix)
  {
    this.prefix = prefix.toUpperCase();
  }

  public Connection getConnection() throws SQLException, ClassNotFoundException {
    if (this.con == null) {
      Class.forName(this.driver);
      this.con = DriverManager.getConnection(this.url, this.username, this.password);
    }
    return this.con;
  }

  public void closeConnection() {
    if (this.con != null) {
      try {
        this.con.close();
      } catch (SQLException e) {
        e.printStackTrace();
      }
      this.con = null;
    }
  }

	/**
	 * 生成指定的数据库里所有的表对应的POJO类
	 * @throws SQLException
	 * @throws ClassNotFoundException 
	 */
  public void createDatabaseEntities()
  {
    ResultSet rs = null;
    try {
      Connection conn = getConnection();
      DatabaseMetaData dbMeta = conn.getMetaData();
      rs = dbMeta.getTables(null, this.username.toUpperCase(), "%", new String[] { "TABLE", "VIEW" });

      File folder = new File(this.destination.getAbsolutePath() + 
        File.separatorChar + 
        this.packageName.replace('.', File.separatorChar));

      if (!folder.exists()) {
        folder.mkdirs();
      }

      while (rs.next()) {
        String tablename = rs.getString(3);
        String className = tablename;
        Set members = fetchMembers(conn, className);
        if ((this.prefix != null) && 
          (className.toUpperCase().startsWith(this.prefix))) {
          className = className.substring(this.prefix.length());
        }

        className = GenUtil.capitalize(className, 0);
        String javaCode = generateJavaCode(tablename, className, members, null);
        File javaFile = new File(folder, className + ".java");
        save(javaFile, javaCode);
      }
      rs.close();
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      if (rs != null) {
        try {
          rs.close();
        } catch (SQLException e) {
          e.printStackTrace();
        }
        rs = null;
      }
      closeConnection();
    }
  }

	/**
	 * 生成指定表对应的POJO类
	 * @param tablename
	 * @param idgenerator 主键生成方式 {@link KeyGenertator}
	 */
  public void createDatabaseEntities(String tablename, String idgenerator)
  {
    ResultSet rs = null;
    try {
      File folder = new File(this.destination.getAbsolutePath() + 
        File.separatorChar + 
        this.packageName.replace('.', File.separatorChar));

      if (!folder.exists()) {
        folder.mkdirs();
      }

      Connection conn = getConnection();
      String className = tablename;
      Set members = fetchMembers(conn, className);
      if ((this.prefix != null) && 
        (className.toUpperCase().startsWith(this.prefix))) {
        className = className.substring(this.prefix.length());
      }

      className = GenUtil.capitalize(className, 0);
      String javaCode = generateJavaCode(tablename, className, members, idgenerator);
      File javaFile = new File(folder, className + ".java");
      save(javaFile, javaCode);
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      if (rs != null) {
        try {
          rs.close();
        } catch (SQLException e) {
          e.printStackTrace();
        }
        rs = null;
      }
      closeConnection();
    }
  }

	/**
	 * fetch table meta info 2 Member set
	 * @param conn
	 * @param tableName
	 * @return
	 */
  private Set<Member> fetchMembers(Connection conn, String tableName)
  {
    Set members = new TreeSet();
    ResultSet rs = null;
    try {
      DataBaseType dataBaseType = guessDataBaseType();
      Set pkfieldSet = new HashSet();
      boolean bupper = true;
      if (DataBaseType.SQLSERVER == dataBaseType) {
        bupper = false;
        String sql = "SELECT upper(column_name) FROM INFORMATION_SCHEMA.KEY_COLUMN_USAGE WHERE OBJECTPROPERTY(OBJECT_ID(constraint_name), 'IsPrimaryKey') = 1 AND table_name = '" + tableName + "'";
        PreparedStatement stmt = conn.prepareStatement(sql);
        rs = stmt.executeQuery();
        while (rs.next()) {
          String pk = rs.getString(1);
          if (pk != null) {
            pkfieldSet.add(pk);
          }
        }
        rs.close();
        rs = null;
        stmt.close();
        stmt = null;
      } else {
        DatabaseMetaData dbmd = conn.getMetaData();
        rs = dbmd.getPrimaryKeys(null, this.username.toUpperCase(), tableName);
        while (rs.next()) {
          String pk = rs.getString(4);
          if (pk != null) {
            pk = pk.toUpperCase();
            pkfieldSet.add(pk);
          }
        }
        rs.close();
        rs = null;
      }

      String sql = "SELECT * FROM " + tableName + " WHERE 1=2";
      PreparedStatement stmt = conn.prepareStatement(sql);
      rs = stmt.executeQuery();
      ResultSetMetaData rsMeta = rs.getMetaData();
      for (int col = 1; col <= rsMeta.getColumnCount(); col++) {
        Member member = new Member();
        String name = rsMeta.getColumnName(col);
        if (bupper) {
          if (pkfieldSet.contains(name.toUpperCase())) {
            member.setPk(true);
          }
          member.setColname(name.toUpperCase());
        } else {
          if (pkfieldSet.contains(name.toLowerCase()) || pkfieldSet.contains(name.toUpperCase())) {
            member.setPk(true);
          }
          member.setColname(name);
        }
        name = GenUtil.capitalize(name, 1);
        member.setName(name);
        member.setSize(rsMeta.getPrecision(col));
        member.setWritable(rsMeta.isWritable(col));
        member.setNullable(rsMeta.isNullable(col) == 1);
        member.setColtype(rsMeta.getColumnType(col));
        if ((member.getColtype() == 2005) || 
          (member.getColtype() == 2004) || 
          (member.getColtype() == 2011)) {
        	member.setType(byte[].class);
        } else {
          String typeName = rsMeta.getColumnClassName(col);
          typeName = change2properTypeName(typeName, member.isPk());
          member.setType(getClass(typeName));
        }

        members.add(member);
      }
      stmt.close();
      stmt = null;
    } catch (Exception ex) {
      ex.printStackTrace();
    } finally {
      if (rs != null) {
        try {
          rs.close();
        } catch (SQLException e) {
          e.printStackTrace();
        }
        rs = null;
      }
    }

    return members;
  }

  private Class<?> getClass(String typeName) {
    if ((typeName.endsWith("[]")) && 
      (typeName.equals("byte[]"))) {
    	return byte[].class;
    }
    try
    {
      return Class.forName(typeName);
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    }
    return Object.class;
  }

	/**
	 * 转换成合适的类型
	 * @param typeName
	 * @param pk
	 * @return
	 */
  protected String change2properTypeName(String typeName, boolean pk)
  {
    String name = typeName;
    if (typeName.equals("oracle.sql.TIMESTAMP"))
      name = "java.sql.Timestamp";
    else if (typeName.equals("java.math.BigDecimal")) {
      if (pk)
        name = "java.lang.Long";
      else
        name = "java.lang.Integer";
    }
    else if (typeName.equals("java.sql.Date"))
      name = "java.util.Date";
    else if (typeName.equals("java.sql.Clob"))
      name = "byte[]";
    else if (typeName.equals("java.sql.Blob")) {
      name = "byte[]";
    }
    return name;
  }

  private String generateJavaCode(String tablename, String className, Set<Member> members, String idgenerator)
  {
    TabStack code = new TabStack();
    addPackage(code, className);
    addimports(code, members);
    addClass(code, tablename, className, members, idgenerator);
    return code.toString();
  }

	/**
	 * add package
	 * @param code
	 */
  private void addPackage(TabStack code, String className)
  {
    code.append("/*");
    code.appendEOL();
    code.append(" * Copyright 2010-2016 the original author or authors.");
    code.appendEOL();
    code.append(" * ");
    code.appendEOL();
    code.append(" * Licensed to the Apache Software Foundation (ASF) under one or more");
    code.appendEOL();
    code.append(" * contributor license agreements.  See the NOTICE file distributed with");
    code.appendEOL();
    code.append(" * this work for additional information regarding copyright ownership.");
    code.appendEOL();
    code.append(" * The ASF licenses this file to You under the Apache License, Version 2.0");
    code.appendEOL();
    code.append(" * (the \"License\"); you may not use this file except in compliance with");
    code.appendEOL();
    code.append(" * the License.  You may obtain a copy of the License at");
    code.appendEOL();
    code.append(" *");
    code.appendEOL();
    code.append(" *      http://www.apache.org/licenses/LICENSE-2.0");
    code.appendEOL();
    code.append(" *");
    code.appendEOL();
    code.append(" * Unless required by applicable law or agreed to in writing, software");
    code.appendEOL();
    code.append(" * distributed under the License is distributed on an \"AS IS\" BASIS,");
    code.appendEOL();
    code.append(" * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.");
    code.appendEOL();
    code.append(" * See the License for the specific language governing permissions and");
    code.appendEOL();
    code.append(" * limitations under the License.");
    code.appendEOL();
    code.append(" * 文件：");
    code.append(this.packageName);
    code.append(".");
    code.append(className);
    code.append(".java");
    code.appendEOL();
    code.append(" * 日 期：");
    code.append(new Date().toString());
    code.appendEOL();
    code.append(" */");
    code.appendEOL();

    code.append("package ");
    code.append(this.packageName);
    code.append(";");
    code.appendEOL();
    code.appendEOL();
  }

	/**
	 * add needed import classes
	 * @param code
	 * @param members
	 */
  private void addimports(TabStack code, Set<Member> members)
  {
      Set imports = new TreeSet();
      imports.add((new StringBuilder("import java.io.Serializable;")).append(GenUtil.LINE_END).toString());
      imports.add((new StringBuilder("import com.linkprise.orm.annotation.ClassMapping;")).append(GenUtil.LINE_END).toString());
      imports.add((new StringBuilder("import com.linkprise.orm.annotation.FieldMapping;")).append(GenUtil.LINE_END).toString());
      Member member;
      for(Iterator iterator = members.iterator(); iterator.hasNext(); imports.add(member.getImport()))
          member = (Member)iterator.next();

      String imp;
      for(Iterator iterator1 = imports.iterator(); iterator1.hasNext(); code.append(imp))
          imp = (String)iterator1.next();

      code.appendEOL();
  }

	/**
	 * add class file contents
	 * @param code
	 * @param className
	 * @param members
	 * @param idgenerator
	 */
  private void addClass(TabStack code, String tablename, String className, Set<Member> members, String idgenerator)
  {
      code.append("/**");
      code.appendEOL();
      code.append(" *");
      code.appendEOL();
      code.append(" * this file is generated by the uorm pojo tools.");
      code.appendEOL();
      code.append(" *");
      code.appendEOL();
      code.append(" * @author <a href=\"mailto:wt47@live.com\">\u006c\u0069\u006e\u006b\u0070\u0072\u0069\u0073\u0065\u002e\u0063\u006f\u006d\u0020\u72ac\u5c11</a>");
      code.appendEOL();
      code.append(" * @version 1.0.0");
      code.appendEOL();
      code.append(" */");
      code.appendEOL();
      code.append("@ClassMapping(tableName = \"");
      code.append(tablename);
      String keyorder = null;
      int count = 0;
      for(Iterator iterator = members.iterator(); iterator.hasNext();)
      {
          Member member = (Member)iterator.next();
          if(member.isPk())
          {
              if(keyorder == null)
                  keyorder = member.getName();
              else
                  keyorder = (new StringBuilder(String.valueOf(keyorder))).append(",").append(member.getName()).toString();
              count++;
          }
      }

      if(count > 1)
      {
          code.append("\", keyOrder = \"");
          code.append(keyorder);
      }
      if(idgenerator != null)
      {
          code.append("\", keyGenerator = \"");
          code.append(idgenerator);
      }
      code.append("\")");
      code.appendEOL();
      code.append("public class ");
      code.append(className);
      code.append(" implements Serializable {");
      code.push();
      code.appendEOL();
      addSerialVersionUID(code, className, members);
      code.appendEOL();
      addStaticPorps(code, members);
      code.appendEOL();
      boolean haspk = false;
      boolean hasnonnull = false;
      for(Iterator iterator1 = members.iterator(); iterator1.hasNext();)
      {
          Member member = (Member)iterator1.next();
          if(member.isPk())
          {
              member.addField(code);
              haspk = true;
          } else
          if(!member.isNullable())
              hasnonnull = true;
      }

      for(Iterator iterator2 = members.iterator(); iterator2.hasNext();)
      {
          Member member = (Member)iterator2.next();
          if(!member.isPk())
              member.addField(code);
      }

      code.appendEOL();
      code.append("public ");
      code.append(className);
      code.append("() {");
      code.push();
      code.append("super();");
      code.pop();
      code.append("}");
      code.appendEOL(2);
      boolean sameconstructor = false;
      if(haspk)
      {
          StringBuffer argFieldBuf = new StringBuffer();
          String notnullArgField = "";
          code.append("public ");
          code.append(className);
          code.append("(");
          int idx = 0;
          for(Iterator iterator6 = members.iterator(); iterator6.hasNext();)
          {
              Member member = (Member)iterator6.next();
              if(member.isPk())
              {
                  if(idx != 0)
                      code.append(", ");
                  code.append(member.getType().getSimpleName());
                  argFieldBuf.append(member.getType().getSimpleName()).append(", ");
                  code.append(" ");
                  code.append(member.decapitalize(member.getName()));
                  idx++;
              }
              if(!member.isNullable() && !member.isPk())
                  notnullArgField = (new StringBuilder(String.valueOf(notnullArgField))).append(member.getType().getSimpleName()).append(", ").toString();
          }

          if(notnullArgField.equals(argFieldBuf.toString()))
              sameconstructor = true;
          code.append(") {");
          code.push();
          int argcount = 0;
          for(Iterator iterator7 = members.iterator(); iterator7.hasNext();)
          {
              Member member = (Member)iterator7.next();
              if(member.isPk())
              {
                  argcount++;
                  code.append("this.");
                  code.append(member.decapitalize(member.getName()));
                  code.append(" = ");
                  code.append(member.decapitalize(member.getName()));
                  code.append(";");
                  if(argcount == idx)
                      code.pop();
                  else
                      code.appendEOL();
              }
          }

          code.append("}");
          code.appendEOL(2);
      }
      if(hasnonnull && !sameconstructor)
      {
          code.append("public ");
          code.append(className);
          code.append("(");
          int idx = 0;
          for(Iterator iterator4 = members.iterator(); iterator4.hasNext();)
          {
              Member member = (Member)iterator4.next();
              if(!member.isNullable() && !member.isPk())
              {
                  if(idx != 0)
                      code.append(", ");
                  code.append(member.getType().getSimpleName());
                  code.append(" ");
                  code.append(member.decapitalize(member.getName()));
                  idx++;
              }
          }

          code.append(") {");
          code.push();
          int argcount = 0;
          for(Iterator iterator5 = members.iterator(); iterator5.hasNext();)
          {
              Member member = (Member)iterator5.next();
              if(!member.isNullable() && !member.isPk())
              {
                  argcount++;
                  code.append("this.");
                  code.append(member.decapitalize(member.getName()));
                  code.append(" = ");
                  code.append(member.decapitalize(member.getName()));
                  code.append(";");
                  if(argcount == idx)
                      code.pop();
                  else
                      code.appendEOL();
              }
          }

          code.append("}");
          code.appendEOL(2);
      }
      for(Iterator iterator3 = members.iterator(); iterator3.hasNext(); code.appendEOL(2))
      {
          Member member = (Member)iterator3.next();
          member.addAccessors(code);
      }

      addEquals(code, className, members);
      code.appendEOL();
      addHashCode(code, className, members);
      code.pop();
      code.append("}");
  }

  private void addStaticPorps(TabStack code, Set<Member> members) {
    for (Member member : members) {
      code.append("public static String PROP_");
      code.append(member.getColname().toUpperCase());

      code.append(" = \"" + member.getColname().toUpperCase() + "\";");
      code.appendEOL();
    }
  }

	/**
	 * add serialVersionUID by default 1L
	 * @param code
	 * @param className
	 * @param members
	 */
  private void addSerialVersionUID(TabStack code, String className, Set<Member> members)
  {
    code.append("private static final long serialVersionUID = 1L;");
    code.appendEOL();
  }

	/**
	 * add hashCode method
	 * @param code
	 * @param className
	 * @param members
	 */
  private void addHashCode(TabStack code, String className, Set<Member> members)
  {
    code.append("/* (non-Javadoc)");
    code.appendEOL();
    code.append(" * @see java.lang.Object#hashCode()");
    code.appendEOL();
    code.append(" */");
    code.appendEOL();

    code.append("@Override");
    code.appendEOL();
    code.append("public int hashCode() {");
    code.push();

    code.append("final int prime = 31;");
    code.appendEOL();
    code.append("int result = 1;");
    code.appendEOL();
    for (Member member : members) {
      if (!member.isPk())
        continue;
      code.append("result = prime * result + ((");
      code.append(member.getName());
      code.append(" == null) ? 0 : ");
      code.append(member.getName());
      code.append(".hashCode());");
      code.appendEOL();
    }

    code.append("return result;");
    code.pop();
    code.append("}");
    code.appendEOL();
  }

	/**
	 * add equals method
	 * @param code
	 * @param className
	 * @param members
	 */
  private void addEquals(TabStack code, String className, Set<Member> members)
  {
    code.append("/* (non-Javadoc)");
    code.appendEOL();
    code.append(" * @see java.lang.Object#equals(java.lang.Object)");
    code.appendEOL();
    code.append(" */");
    code.appendEOL();

    code.append("@Override");
    code.appendEOL();
    code.append("public boolean equals(Object o) {");
    code.push();

    code.append("if ((o == null) || !(o instanceof ");
    code.append(className);
    code.append(")) {");
    code.push();
    code.append("return false;");
    code.pop();
    code.append("}");
    code.appendEOL();

    code.append(className);
    code.append(" other = (");
    code.append(className);
    code.append(")");
    code.append("o;");
    code.appendEOL();

    for (Member member : members) {
      if (!member.isPk())
      {
        continue;
      }

      code.append("if (null == this.");
      code.append(member.getName());
      code.append(") {");
      code.push();
      code.append("if (other.");
      code.append(member.getName());
      code.append(" != null)");
      code.push();
      code.append("return false;");
      code.pop(2);
      code.append("} else if (!this.");
      code.append(member.getName());
      code.append(".equals(other.");
      code.append(member.getName());
      code.append("))");
      code.push();
      code.append("return false;");
      code.pop();
    }

    code.append("return true;");
    code.pop();
    code.append("}");
    code.appendEOL();
  }

	/**
	 * save java source file
	 * @param file
	 * @param content
	 */
  private void save(File file, String content)
  {
    try
    {
      FileWriter fw = new FileWriter(file);
      fw.write(content);
      fw.close();
      System.out.println("\"" + file.getName() + "\" created successfully!");
    } catch (IOException ex) {
      ex.printStackTrace();
    }
  }

  public static void main(String[] args) {
    String driver = "com.mysql.jdbc.Driver";
    String url = "jdbc:mysql://127.0.0.1/simdb";
    String username = "root";
    String password = "root";
    String packageName = "test";
    PojoGenerator generator = new PojoGenerator(driver, url, username, password, packageName);
    generator.setPrefix("UUM_");

    generator.createDatabaseEntities("TEST_UUID", null);
  }
}