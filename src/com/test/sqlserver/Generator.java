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
package com.test.sqlserver;


import com.linkprise.orm.annotation.KeyGenertator;
import com.linkprise.pojo.generator.PojoGenerator;


public class Generator {
	
	String driver = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
	String url = "jdbc:sqlserver://127.0.0.1;DatabaseName=my_orm_db";
	String username = "sa";
	String password = "Tubashu666";
	
	/** 生成的POJO类包名 */
	private String packageName = "com.test.sqlserver";
    /** 生成表对应的POJO类名需要去除的表名前缀，如 “T_”, “DVN_”等 */
	private String prefix = "";
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
		generator.pojoGen("book", KeyGenertator.NATIVE);
	}
}
