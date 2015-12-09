package com.linkprise.serializer;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;

import com.linkprise.orm.convert.ITypeConverter;
import com.linkprise.orm.mapping.ObjectMappingCache;
import com.linkprise.serializer.converter.StringConverter;
import com.linkprise.serializer.formter.DefaultValueFormater;
import com.linkprise.serializer.formter.IValueFormater;

public class DefaultPojoSerializer
  implements IPojoSerializer
{
  private IValueFormater formater = null;
  private ITypeConverter converter = null;

  public DefaultPojoSerializer()
  {
    this.formater = new DefaultValueFormater();
    this.converter = new StringConverter();
  }

  public Document serialize(Object pojo, String rootName)
    throws Exception
  {
      Class cls = pojo.getClass();
      Map getMethodMap = ObjectMappingCache.getInstance().getPojoGetMethod(cls);
      Document doc = DocumentHelper.createDocument();
      if(rootName == null || rootName.trim().length() == 0)
          rootName = cls.getSimpleName().toUpperCase();
      Element root = doc.addElement(rootName);
      for(Iterator iterator = getMethodMap.keySet().iterator(); iterator.hasNext();)
      {
          String colName = (String)iterator.next();
          Method getMethod = (Method)getMethodMap.get(colName);
          Object val = getMethod.invoke(pojo, new Object[0]);
          Element ele = root.addElement(colName);
          if(val != null)
              if(formater == null)
                  ele.setText(val.toString());
              else
                  ele.setText(formater.format(val));
      }

      return doc;
  }

  public String serialize2(Object pojo, String rootName)
    throws Exception
  {
      Class cls = pojo.getClass();
      Map getMethodMap = ObjectMappingCache.getInstance().getPojoGetMethod(cls);
      StringBuilder sb = new StringBuilder();
      if(rootName == null || rootName.trim().length() == 0)
          rootName = cls.getSimpleName().toUpperCase();
      sb.append('<').append(rootName).append('>');
      for(Iterator iterator = getMethodMap.keySet().iterator(); iterator.hasNext();)
      {
          String colName = (String)iterator.next();
          Method getMethod = (Method)getMethodMap.get(colName);
          Object val = getMethod.invoke(pojo, new Object[0]);
          if(val != null)
          {
              sb.append('<').append(colName).append('>');
              if(formater == null)
                  sb.append(escape(val.toString()));
              else
                  sb.append(escape(formater.format(val)));
              sb.append("</").append(colName).append('>');
          } else
          {
              sb.append('<').append(colName).append("/>");
          }
      }

      sb.append("</").append(rootName).append('>');
      return sb.toString();
  }

  public static String escape(String str)
  {
    if (str != null) {
      str = str.replaceAll("<", "&lt;");
      str = str.replaceAll(">", "&gt;");
      str = str.replaceAll("&", "&amp;");
      str = str.replaceAll("'", "&apos;");
      str = str.replaceAll("\"", "&quot;");
    }
    return str;
  }

  public <T> T deserialize(Class<T> cls, String xml)
    throws Exception
  {
    Document doc = DocumentHelper.parseText(xml);
    return deserialize(cls, doc);
  }

  public <T> T deserialize(Class<T> cls, Document xml)
    throws Exception
  {
    return deserialize(cls, xml.getRootElement());
  }

  public <T> T deserialize(Class<T> cls, Element element)
    throws Exception
  {
	  @SuppressWarnings("unchecked")
		List<Node> nodes = element.elements();
		Map<String, Method> setMethods = ObjectMappingCache.getInstance().getPojoSetMethod(cls);
		T instance = cls.newInstance();
		if(setMethods != null && !setMethods.isEmpty()) {
			for (Node node : nodes) {
				String strVal = node.getText();
				if(strVal != null) {
					Method setterMethod = setMethods.get(node.getName().toUpperCase());
					if(setterMethod != null) {
						Class<?> memberType = setterMethod.getParameterTypes()[0];
						if(memberType == String.class) {
							setterMethod.invoke(instance, strVal);
						}else{
							if(strVal.length() > 0) {
								Object val = converter.convert(strVal, memberType);
								setterMethod.invoke(instance, val);
							}
						}
					}
				}
			}
		}
		return instance;
  }

  public Map<String, Object> deserialize2(Class<?> cls, String xml)
    throws Exception
  {
    Document doc = DocumentHelper.parseText(xml);
    return deserialize2(cls, doc);
  }

  public Map<String, Object> deserialize2(Class<?> cls, Document xml)
    throws Exception
  {
    return deserialize2(cls, xml.getRootElement());
  }

  public Map<String, Object> deserialize2(Class<?> refcls, Element element)
    throws Exception
  {
	  @SuppressWarnings("unchecked")
		List<Node> nodes = element.elements();
		Map<String, PropertyDescriptor> propMap = ObjectMappingCache.getInstance().getObjectPropertyMap(refcls);
		Map<String, Object> instance = new HashMap<String, Object>();
		if(propMap != null && !propMap.isEmpty()) {
			for (Node node : nodes) {
				String strVal = node.getText();
				if(strVal != null) {
					String name = node.getName().toUpperCase();
					PropertyDescriptor descriptor = propMap.get(name);
					if(descriptor != null) {
						Class<?> memberType = descriptor.getPropertyType();
						if(memberType == String.class) {
							instance.put(name, strVal);
						}else{
							if(strVal.length() > 0) {
								Object val = converter.convert(strVal, memberType);
								instance.put(name, val);
							}
						}
					}
				}
			}
		}
		return instance;
  }

  public IValueFormater getFormater()
  {
    return this.formater;
  }

  public void setFormater(IValueFormater formater)
  {
    this.formater = formater;
  }

  public ITypeConverter getConverter()
  {
    return this.converter;
  }

  public void setConverter(ITypeConverter converter)
  {
    this.converter = converter;
  }
}