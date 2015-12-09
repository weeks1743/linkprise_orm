package com.linkprise.pojo.generator;

public class Member
  implements Comparable<Member>
{
  private String name;
  private Class<?> type;
  private int size;
  private boolean nullable;
  private boolean writable;
  private boolean pk;
  private String colname;
  private int coltype = 12;

  public Member() {
    this.name = "MyClassName";
    this.type = String.class;
    this.nullable = true;
    this.writable = true;
    this.pk = false;
    this.size = 0;
  }

  public String getName()
  {
    return this.name;
  }

  public void setName(String name)
  {
    this.name = name;
  }

  public Class<?> getType()
  {
    return this.type;
  }

  public void setType(Class<?> type)
  {
    this.type = type;
  }

  public int getSize()
  {
    return this.size;
  }

  public void setSize(int size)
  {
    this.size = size;
  }

  public boolean isNullable()
  {
    return this.nullable;
  }

  public void setNullable(boolean nullable)
  {
    this.nullable = nullable;
  }

  public boolean isWritable()
  {
    return this.writable;
  }

  public void setWritable(boolean writable)
  {
    this.writable = writable;
  }

  public boolean isPk()
  {
    return this.pk;
  }

  public void setPk(boolean pk)
  {
    this.pk = pk;
  }

  public int getColtype()
  {
    return this.coltype;
  }

  public void setColtype(int coltype)
  {
    this.coltype = coltype;
  }

  public String getColname()
  {
    return this.colname;
  }

  public void setColname(String colname)
  {
    this.colname = colname;
  }

  public String getImport() {
    String className = this.type.getName();
    if ((className.startsWith("java.lang")) || (this.type.isArray())) {
      return "";
    }
    StringBuilder sb = new StringBuilder("import ");
    sb.append(className);
    sb.append(';');
    sb.append(GenUtil.LINE_END);
    return sb.toString();
  }

  public String decapitalize(String value) {
    return value.substring(0, 1).toLowerCase() + value.substring(1);
  }

  public String capitalize(String value) {
    return value.substring(0, 1).toUpperCase() + value.substring(1);
  }

  public void addField(TabStack code) {
    if (this.pk) {
      code.append("//primary key field of ");
      code.append(decapitalize(this.name));
      code.appendEOL();
    }
    code.append("@FieldMapping(columnName = \"");
    code.append(this.colname);
    code.append("\", columnType = ");
    code.append(this.coltype);
    if (this.pk) {
      code.append(", primary = true");
    }
    code.append(")");
    code.appendEOL();

    code.append("private ");
    code.append(this.type.getSimpleName());
    code.append(" ");
    code.append(decapitalize(this.name));
    code.append(";");
    code.appendEOL();
  }

  public void addAccessors(TabStack code)
  {
    addGetter(code);
    code.appendEOL();
    code.appendEOL();
    addSetter(code);
  }

  private void addGetter(TabStack code) {
    code.append("/**");
    code.appendEOL();
    code.append(" * @return the ");
    code.append(decapitalize(this.name));
    code.appendEOL();
    code.append(" */");
    code.appendEOL();

    code.append("public ");
    code.append(this.type.getSimpleName());
    code.append(" get");
    code.append(capitalize(this.name));
    code.append("() {");
    code.push();
    code.append("return this.");
    code.append(decapitalize(this.name));
    code.append(";");
    code.pop();
    code.append("}");
  }

  private void addSetter(TabStack code) {
    code.append("/**");
    code.appendEOL();
    code.append(" * @param ");
    code.append(decapitalize(this.name));
    code.append(" the ");
    code.append(decapitalize(this.name));
    code.append(" to set");
    code.appendEOL();
    code.append(" */");
    code.appendEOL();

    String modifier = "public";
//    if (!this.writable) {
//      modifier = "protected";
//    }
    code.append(modifier);
    code.append(" void");
    code.append(" set");
    code.append(capitalize(this.name));
    code.append("(");
    code.append(this.type.getSimpleName());
    code.append(" value) {");
    code.push();
    code.append("this.");
    code.append(decapitalize(this.name));
    code.append(" = value;");
    code.pop();
    code.append("}");
  }

  public int compareTo(Member o)
  {
    return this.name.compareToIgnoreCase(o.name);
  }
}