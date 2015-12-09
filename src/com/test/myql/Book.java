/*
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
 * 文件：com.test.Book.java
 * 日 期：Tue Dec 08 17:09:03 CST 2015
 */
package com.test.myql;

import java.io.Serializable;
import java.sql.Timestamp;

import com.linkprise.orm.annotation.ClassMapping;
import com.linkprise.orm.annotation.FieldMapping;

/**
 *
 * this file is generated by the uorm pojo tools.
 *
 * @author <a href="mailto:wt47@live.com">linkprise.com 犬少</a>
 * @version 1.0.0
 */
@ClassMapping(tableName = "book", keyGenerator = "native")
public class Book implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	public static String PROP_AUTHOR = "AUTHOR";
	public static String PROP_DATES = "DATES";
	public static String PROP_ID = "ID";
	public static String PROP_NAME = "NAME";
	public static String PROP_PAGES = "PAGES";
	
	//primary key field of id
	@FieldMapping(columnName = "ID", columnType = 4, primary = true)
	private Integer id;
	@FieldMapping(columnName = "AUTHOR", columnType = 12)
	private String author;
	@FieldMapping(columnName = "DATES", columnType = 93)
	private Timestamp dates;
	@FieldMapping(columnName = "NAME", columnType = 12)
	private String name;
	@FieldMapping(columnName = "PAGES", columnType = 4)
	private Integer pages;
	
	public Book() {
		super();
	}

	public Book(Integer id) {
		this.id = id;
	}

	/**
	 * @return the author
	 */
	public String getAuthor() {
		return this.author;
	}
	
	/**
	 * @param author the author to set
	 */
	public void setAuthor(String value) {
		this.author = value;
	}

	/**
	 * @return the dates
	 */
	public Timestamp getDates() {
		return this.dates;
	}
	
	/**
	 * @param dates the dates to set
	 */
	public void setDates(Timestamp value) {
		this.dates = value;
	}

	/**
	 * @return the id
	 */
	public Integer getId() {
		return this.id;
	}
	
	/**
	 * @param id the id to set
	 */
	public void setId(Integer value) {
		this.id = value;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return this.name;
	}
	
	/**
	 * @param name the name to set
	 */
	public void setName(String value) {
		this.name = value;
	}

	/**
	 * @return the pages
	 */
	public Integer getPages() {
		return this.pages;
	}
	
	/**
	 * @param pages the pages to set
	 */
	public void setPages(Integer value) {
		this.pages = value;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object o) {
		if ((o == null) || !(o instanceof Book)) {
			return false;
		}
		Book other = (Book)o;
		if (null == this.id) {
			if (other.id != null)
				return false;
		} else if (!this.id.equals(other.id))
			return false;
		return true;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}
	
}