package com.linkprise.utils;

import java.io.IOException;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.security.MessageDigest;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils
{
  private static final String[] hexDigits = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f" };

  public String byteToASCI(byte[] b)
    throws IOException
  {
    String tepStr = null;
    String ASCString = null;
    for (int i = 0; i < b.length; i++)
    {
      tepStr = Character.toString((char)b[i]);
      ASCString = ASCString + tepStr;
    }
    return ASCString;
  }

	/**
	 * �����ĸ��������ɵ�����ַ�
	 * @param len ����ɵ��ַ���
	 * @return
	 */
  public static String genRandomNum(int len)
  {
    if (len <= 0)
      return "";
    int maxNum = 36;

    int count = 0;
    char[] str = { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 
      'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 
      'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' };

    StringBuffer rtn = new StringBuffer("");
    Random r = new Random();
    while (count < len)
    {
      int i = Math.abs(r.nextInt(36));
      if ((i >= 0) && (i < str.length)) {
        rtn.append(str[i]);
        count++;
      }
    }

    return rtn.toString();
  }

	/**
	 * �ַ�ת��Ϊ����
	 * @param str
	 * @return int
	 */
  public static int strToInt(String str)
  {
    if ((str == null) || (str.trim().length() == 0))
      return 0;
    try {
      return Integer.parseInt(str); } catch (Exception e) {
    }
    return 0;
  }

	/**
	 * �ַ�ת��Ϊ����
	 * @param str
	 * @param radixr
	 * @return
	 */
  public static int strToInt(String str, int radixr)
  {
    if ((str == null) || (str.trim().length() == 0))
      return 0;
    try {
      return Integer.parseInt(str, radixr); } catch (Exception e) {
    }
    return 0;
  }

	/**
	 * �ַ�ת��Ϊ����
	 * @param str
	 * @return Integer
	 */
  public static Integer strToInteger(String str)
  {
    if ((str == null) || (str.trim().length() == 0))
      return null;
    try {
      return new Integer(str); } catch (Exception e) {
    }
    return null;
  }

	/**
	 * �ַ�ת��Ϊ������
	 * @param str
	 * @return
	 */
  public static long str2long(String str)
  {
    if ((str == null) || (str.trim().length() == 0))
      return 0L;
    try {
      return Long.parseLong(str); } catch (Exception e) {
    }
    return 0L;
  }

  public static long str2long(String str, int radixr)
  {
    if ((str == null) || (str.trim().length() == 0))
      return 0L;
    try {
      return Long.parseLong(str, radixr); } catch (Exception e) {
    }
    return 0L;
  }

  public static Long strToLong(String str)
  {
    if ((str == null) || (str.trim().length() == 0))
      return null;
    try {
      return Long.valueOf(Long.parseLong(str)); } catch (Exception e) {
    }
    return null;
  }

  public static Float str2float(String str)
  {
    if ((str == null) || (str.trim().length() == 0))
      return Float.valueOf(0.0F);
    try {
      return Float.valueOf(Float.parseFloat(str)); } catch (Exception e) {
    }
    return null;
  }

	/**
	 * �ַ�ת��Ϊ������
	 * @param str
	 * @return
	 */
  public static Float strToFloat(String str)
  {
    if ((str == null) || (str.trim().length() == 0))
      return null;
    try {
      return Float.valueOf(Float.parseFloat(str)); } catch (Exception e) {
    }
    return null;
  }

	/**
	 * �ַ�ת��ΪBoolean��
	 * @param str
	 * @return
	 */
  public static Boolean str2Boolean(String str)
  {
    if ((str == null) || (str.trim().length() == 0))
      return null;
    try {
      return Boolean.valueOf(Boolean.parseBoolean(str)); } catch (Exception e) {
    }
    return null;
  }

	/**
	 * Date����foramt
	 * @param obj ��Ҫ��ʽ����object,�����Date���ͣ���ֱ�ӵ��ø����toString()�������ء�
	 * @param parten format��ʽ�����Ϊ�գ���Ĭ��Ϊ "yyyy-MM-dd"
	 * @return String
	 */
  public static String dateFormat(Object obj, String parten)
  {
    if (obj == null)
      return null;
    if ((parten == null) || (parten.trim().length() == 0))
      parten = "yyyy-MM-dd";
    SimpleDateFormat dateFormat = new SimpleDateFormat(parten);
    if ((obj instanceof java.sql.Date))
      return dateFormat.format(obj);
    if ((obj instanceof java.util.Date)) {
      return dateFormat.format(obj);
    }
    return obj.toString();
  }

	/**
	 * Decimal����foramt
	 * @param obj ��Ҫ��ʽ����object,�����BigDecimal��Float���ͣ���ֱ�ӵ��ø����toString()�������ء�
	 * @param parten format��ʽ�����Ϊ�գ���Ĭ��Ϊ "###,##0.00"
	 * @return String
	 */
  public static String bigDicimalFormat(Object obj, String parten)
  {
    if (obj == null)
      return null;
    if ((parten == null) || (parten.trim().length() == 0))
      parten = "###,##0.00";
    DecimalFormat decimalFormat = new DecimalFormat(parten);
    if ((obj instanceof BigDecimal))
      return decimalFormat.format(obj);
    if ((obj instanceof Float))
      return decimalFormat.format(obj);
    if ((obj instanceof Double)) {
      return decimalFormat.format(obj);
    }
    return obj.toString();
  }

	/**
	 * �ַ�����ת����Decimal
	 * @param strbigdecimal:Ҫת�����ַ�(Decimal)
	 * @return
	 */
  public static BigDecimal str2BigDicimal(String strbigdecimal)
  {
    if ((strbigdecimal == null) || (strbigdecimal.trim().length() == 0))
      return null;
    BigDecimal bd = null;
    try {
      bd = new BigDecimal(strbigdecimal);
    } catch (Exception e) {
      return null;
    }
    return bd;
  }

	/**
	 * �ַ�����ת����Date����
	 * @param strDate Ҫת�����ַ�����
	 * @param parten ���ڸ�ʽ��Ĭ��Ϊ "yyyy-MM-dd"
	 * @return Date
	 */
  public static java.util.Date str2Date(String strDate, String parten)
  {
    java.util.Date dt = null;
    if (parten == null)
      parten = "yyyy-MM-dd";
    SimpleDateFormat sdf = new SimpleDateFormat(parten);
    try {
      dt = sdf.parse(strDate);
    } catch (Exception e) {
      return null;
    }
    return dt;
  }

  private static String byteArrayToHexString(byte[] b) {
    StringBuffer resultSb = new StringBuffer();
    for (int i = 0; i < b.length; i++) {
      int n = b[i];
      resultSb.append(hexDigits[(n >>> 4 & 0xF)] + hexDigits[(n & 0xF)]);
    }
    return resultSb.toString();
  }

	/**MD5����*/
  public static String MD5Encode(String origin)
  {
    String resultString = null;
    try {
      resultString = new String(origin);
      MessageDigest md = MessageDigest.getInstance("MD5");
      resultString = byteArrayToHexString(md.digest(resultString
        .getBytes()));
    } catch (Exception ex) {
      ex.printStackTrace();
    }
    return resultString;
  }

	/**
	 * IP��ַת��������
	 * @param strIp
	 * @return
	 */
  public static long ipToLong(String strIp)
  {
    long[] ip = new long[4];

    String[] strIps = strIp.split("[.]");

    ip[0] = Long.parseLong(strIps[0]);
    ip[1] = Long.parseLong(strIps[1]);
    ip[2] = Long.parseLong(strIps[2]);
    ip[3] = Long.parseLong(strIps[3]);
    return (ip[0] << 24) + (ip[1] << 16) + (ip[2] << 8) + ip[3];
  }

	/**
	 * ��ʮ��������ת����127.0.0.1��ʽ��ip��ַ  
	 * @param longIp
	 * @return
	 */
  public static String longToIP(long longIp)
  {
    StringBuffer sb = new StringBuffer();

    sb.append(String.valueOf(longIp >>> 24));
    sb.append('.');

    sb.append(String.valueOf((longIp & 0xFFFFFF) >>> 16));
    sb.append('.');

    sb.append(String.valueOf((longIp & 0xFFFF) >>> 8));
    sb.append('.');

    sb.append(String.valueOf(longIp & 0xFF));
    return sb.toString();
  }

  public static String genRandomMac()
  {
    int count = 0;
    char[] str = { 'a', 'b', 'c', 'd', 'e', 'f', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' };

    StringBuffer rtn = new StringBuffer("");
    Random r = new Random();
    while (count < 16)
    {
      int i = Math.abs(r.nextInt(16));
      if ((i >= 0) && (i < str.length)) {
        rtn.append(str[i]);
        count++;
        if ((count % 2 == 0) && (count < 16)) {
          rtn.append(':');
        }
      }
    }

    return rtn.toString();
  }

	/**
	 * �ж��ַ��Ƿ�Ϊ����
	 * 
	 * @param str
	 * @return
	 */
  public static boolean isNumeric(String str)
  {
    Pattern pattern = Pattern.compile("[0-9]*");
    Matcher isNum = pattern.matcher(str);

    return isNum.matches();
  }

	/**
	 * bytes to int
	 * @param bytes
	 * @return
	 */
  public static int bytes2int(byte[] bytes)
  {
    int result = 0;
    for (int i = 0; i < 4; i++) {
      result = (result << 8) - -128 + bytes[i];
    }
    return result;
  }

	/**
	 * short to bytes
	 * @param shortValue
	 * @return
	 */
  public static byte[] short2bytes(int shortValue)
  {
    byte[] bytes = new byte[2];
    bytes[0] = (byte)(shortValue >> 8);
    bytes[1] = (byte)(shortValue << 8 >> 8);
    return bytes;
  }

	/**
	 * int to bytes
	 * @param intValue
	 * @return 
	 */
  public static byte[] int2bytes(int intValue)
  {
    byte[] bytes = new byte[4];
    bytes[0] = (byte)(intValue >> 24);
    bytes[1] = (byte)(intValue << 8 >> 24);
    bytes[2] = (byte)(intValue << 16 >> 24);
    bytes[3] = (byte)(intValue << 24 >> 24);
    return bytes;
  }

	/**
	 * long to bytes
	 * @param longValue
	 * @return
	 */
  public static byte[] long2bytes(long longValue)
  {
    byte[] bytes = new byte[8];
    bytes[0] = (byte)(int)(longValue >> 56);
    bytes[1] = (byte)(int)(longValue << 8 >> 56);
    bytes[2] = (byte)(int)(longValue << 16 >> 56);
    bytes[3] = (byte)(int)(longValue << 24 >> 56);
    bytes[4] = (byte)(int)(longValue << 32 >> 56);
    bytes[5] = (byte)(int)(longValue << 40 >> 56);
    bytes[6] = (byte)(int)(longValue << 48 >> 56);
    bytes[7] = (byte)(int)(longValue << 56 >> 56);
    return bytes;
  }

	/**
	 * bytes to long
	 * @param bytes The bytes to interpret.
	 * @return
	 */
  public static long bytes2Long(byte[] bytes)
  {
    if (bytes == null) {
      return 0L;
    }
    if (bytes.length != 8) {
      throw new IllegalArgumentException("Expecting 8 byte values to construct a long");
    }
    long value = 0L;
    for (int i = 0; i < 8; i++) {
      value = value << 8 | bytes[i] & 0xFF;
    }
    return value;
  }

  public static void main(String[] args) throws Exception {
    long ip = ipToLong("127.0.0.1");
    System.out.println(ip);
    System.out.println(longToIP(2147483647L));
    for (int i = 0; i < 8; i++)
      System.out.println(genRandomMac());
  }
}