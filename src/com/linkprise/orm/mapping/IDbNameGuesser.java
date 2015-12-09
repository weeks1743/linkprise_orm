package com.linkprise.orm.mapping;

import java.lang.reflect.Method;
import java.util.Collection;

/**
 * ���ܵ���ݿ�����ֶ��� Guesser
 * @author <a href="mailto:wt47@live.com">linkprise.com Ȯ��</a>
 * @version 1.0.0
 * ����������������������������������������������������������������������<br/>
 * �޶�����                 �޶���            ����<br/>
 * 2015-12-8       linkprise.com Ȯ��            ����<br/>
 */
public abstract interface IDbNameGuesser {
	/**
	 * ���ؿ��ܵ���ݿ����ֶ����б�
	 * @param member ��ķ��� for guess the column names for
	 * @return
	 */
	public abstract Collection<String> getPossibleColumnNames(Method paramMethod);

	 /**
	  * ���ؿ������֣������������
	  * @param fieldname ��������������  for guess the column names for
	  * @return
	  */
	public abstract Collection<String> getPossibleNames(String paramString);

	 /**
	  * ���ؿ��ܵ���ݿ�����б�
	  * @param object �� for guess the table names for
	  * @return
	  */
	public abstract Collection<String> getPossibleTableNames(Class<?> paramClass);
} 