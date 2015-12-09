package com.linkprise.pojo.generator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GenUtil
{
  public static final String LINE_END = System.getProperty("line.separator");
  public static final int PASCAL_CASE = 0;
  public static final int CAMEL_CASE = 1;

  public static String capitalize(String value, int cap)
  {
    String ret = "";
    String tmpValue = value;
    if (value.toUpperCase().equals(value)) {
      tmpValue = value.toLowerCase();
    }
    String[] split = splitWords(tmpValue).split("_");
    boolean firstPart = true;
    for (String part : split) {
      if (part.length() > 0) {
        if ((firstPart) && (cap == 1)) {
          ret = ret + part.toLowerCase();
          firstPart = false;
        } else {
          ret = ret + part.substring(0, 1).toUpperCase() + 
            part.substring(1).toLowerCase();
        }
      }
    }
    return ret;
  }

  private static String splitWords(String value) {
    Pattern pattern = Pattern.compile("([a-z0-9]+?)([A-Z]+?)");
    Matcher matcher = pattern.matcher(value);
    return matcher.replaceAll("$1_$2");
  }
}