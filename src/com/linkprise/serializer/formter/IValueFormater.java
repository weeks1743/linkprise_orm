package com.linkprise.serializer.formter;

/**
 * 格式工具
 * @author <a href="mailto:wt47@live.com">linkprise.com 犬少</a>
 * @version 1.0.0
 * ＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝<br/>
 * 修订日期                 修订人            描述<br/>
 * 2015-12-8       linkprise.com 犬少            创建<br/>
 */
public abstract interface IValueFormater
{
	/**
	 * ��ʽ��
	 * @param obj
	 * @return
	 */
  public abstract String format(Object paramObject)
    throws Exception;
}