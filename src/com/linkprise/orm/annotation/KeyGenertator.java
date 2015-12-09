package com.linkprise.orm.annotation;

/**
 * 主键生成器 插入多条数据时，INCREMENT 比 SELECT 性能高很多，建议用 INCREMENT， 如果能自己生成主键的话，建议直接生成用
 * ASSIGNED 方式
 * 
 * @author <a href="mailto:wt47@live.com">linkprise.com 犬少</a>
 * @version 1.0.0 ＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝<br/>
 *          修订日期 修订人 描述<br/>
 *          2016-1-18 linkprise.com 犬少 创建<br/>
 */
public abstract interface KeyGenertator {
	public static final String ASSIGNED = "assigend";
	public static final String IDENTITY = "identity";
	public static final String UUID = "uuid";
	public static final String UUIDHEX = "uuid.hex";
	public static final String GUID = "guid";
	public static final String INCREMENT = "increment";
	public static final String SEQUENCE = "sequence";
	public static final String SELECT = "select";
	public static final String NATIVE = "native";
}