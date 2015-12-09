/**
 * Copyright 2010-2016 the original author or authors.
 * 
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.linkprise;


import com.linkprise.orm.annotation.KeyGenertator;
import com.linkprise.pojo.generator.PojoGenerator;

/**
 * POJO 生成工具 Mapping对应的类生成工具
 * @author <a href="mailto:wt47@live.com">linkprise.com 犬少</a>
 * @version 1.0.0
 * ＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝<br/>
 * 修订日期                 修订人            描述<br/>
 * 2015-12-8       linkprise.com 犬少            创建<br/>
 */
public class Generator {
	//TODO edit below value
	String driver = "com.mysql.jdbc.Driver";//"oracle.jdbc.driver.OracleDriver";//"com.microsoft.sqlserver.jdbc.SQLServerDriver";//
	/**连接字串 */
	String url = "jdbc:mysql://127.0.0.1/simdb";//"jdbc:oracle:thin:@127.0.0.1:1521:orcl";//"jdbc:sqlserver://192.168.213.220:1433;DatabaseName=CastMain";//
	/** 数据库用户名 */
	String username = "root";//"cctv7";//"cctv";//"root";
	/** 数据库密码 */
	String password = "root";//"password";//"1";//"root";
	/** 生成的POJO类包名 */
	private String packageName = "test";
    /** 生成表对应的POJO类名需要去除的表名前缀，如 “T_”, “DVN_”等 */
	private String prefix = "";//"UUM_";
	/** 生成类的目标地，默认为当前路径的 /src下，即 "./src" */
	private String destination = "./src";
	
	/**
	 * 生成指定表对应的 pojo类
	 * @param tableName 表名
	 * @param idgenerator 主键生成方式 {@link KeyGenertator}
	 */
	public void pojoGen (String tableName, String idgenerator) {
		PojoGenerator generator = new PojoGenerator(driver, url, username, password, packageName, destination);
		if(prefix != null){
			generator.setPrefix(prefix);
		}
		generator.createDatabaseEntities(tableName, idgenerator);
	}
	
	public static void main(String[] args) {
		Generator generator = new Generator();
		generator.pojoGen("UUM_USER", KeyGenertator.SELECT);//TODO edit this
	}
}
