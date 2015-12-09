package com.linkprise.orm.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 字段Mapping
 * 
 * @author <a href="mailto:wt47@live.com">linkprise.com 犬少</a>
 * @version 1.0.0 ＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝<br/>
 *          修订日期 修订人 描述<br/>
 *          2016-1-18 linkprise.com 犬少 创建<br/>
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface FieldMapping {
	/** column's name */
	String columnName();

	/** column's type. in SQL type */
	int columnType() default 12;

	/** is primary key */
	boolean primary() default false;

	/**
	 * for save, true = column is from table, false = column is from a
	 * view/query only.
	 */
	boolean includeInWrites()  default true;
}