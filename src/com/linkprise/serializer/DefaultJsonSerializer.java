package com.linkprise.serializer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class DefaultJsonSerializer
  implements IJsonSerializer
{
  private Gson gson = null;
  private boolean serializeNulls = true;
  private String datePattern = "yyyy-MM-dd HH:mm:ss";
  private boolean prettyPrinting = false;
  private boolean excludeFieldsWithoutExposeAnnotation = false;
  private boolean enableComplexMapKey = false;
  private Double version = null;

  public String serialize(Object src)
    throws Exception
  {
    return getGson().toJson(src);
  }

  public <T> T deserialize(Class<T> cls, String json)
    throws Exception
  {
    return getGson().fromJson(json, cls);
  }

  public Gson getGson()
  {
    if (this.gson == null) {
      GsonBuilder gbuilder = new GsonBuilder();
      if (this.serializeNulls) {
        gbuilder.serializeNulls();
      }
      gbuilder.setDateFormat(this.datePattern);
      if (this.prettyPrinting) {
        gbuilder.setPrettyPrinting();
      }
      if (this.excludeFieldsWithoutExposeAnnotation) {
        gbuilder.excludeFieldsWithoutExposeAnnotation();
      }
      if (this.enableComplexMapKey) {
        gbuilder.enableComplexMapKeySerialization();
      }
      if (this.version != null) {
        gbuilder.setVersion(this.version.doubleValue());
      }
      this.gson = gbuilder.create();
    }
    return this.gson;
  }

  public void setGson(Gson gson)
  {
    this.gson = gson;
  }

  public boolean isSerializeNulls()
  {
    return this.serializeNulls;
  }

  public void setSerializeNulls(boolean serializeNulls)
  {
    this.serializeNulls = serializeNulls;
  }

  public String getDatePattern()
  {
    return this.datePattern;
  }

  public void setDatePattern(String datePattern)
  {
    this.datePattern = datePattern;
  }

  public boolean isPrettyPrinting()
  {
    return this.prettyPrinting;
  }

  public void setPrettyPrinting(boolean prettyPrinting)
  {
    this.prettyPrinting = prettyPrinting;
  }

  public boolean isExcludeFieldsWithoutExposeAnnotation()
  {
    return this.excludeFieldsWithoutExposeAnnotation;
  }

  public void setExcludeFieldsWithoutExposeAnnotation(boolean excludeFieldsWithoutExposeAnnotation)
  {
    this.excludeFieldsWithoutExposeAnnotation = excludeFieldsWithoutExposeAnnotation;
  }

  public boolean isEnableComplexMapKey()
  {
    return this.enableComplexMapKey;
  }

  public void setEnableComplexMapKey(boolean enableComplexMapKey)
  {
    this.enableComplexMapKey = enableComplexMapKey;
  }

  public Double getVersion()
  {
    return this.version;
  }

  public void setVersion(Double version)
  {
    this.version = version;
  }
}