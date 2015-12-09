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
package com.linkprise.dao.id;

/**
 * 
 * @author <a href="mailto:wt47@live.com">linkprise.com 犬少</a>
 * @version 1.0.0 ＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝<br/>
 *          修订日期 修订人 描述<br/>
 *          2016-1-30 linkprise.com 犬少 创建<br/>
 */
public interface IdentifierGeneratorFactory {

	/**
	 * create identifier generator instance.
	 * 
	 * @param keygenertator
	 *            @see com.linkprise.orm.annotation.KeyGenertator
	 * @return
	 * @throws Exception
	 */
	public IdentifierGenerator createIdentifierGenerator(String keygenertator)
			throws Exception;

	/**
	 * get the class that will be used as the {@link IdentifierGenerator} for
	 * the given keygenertator
	 * 
	 * @param keygenertator
	 *            @see com.linkprise.orm.annotation.KeyGenertator
	 * @return
	 */
	public Class<?> getIdentifierGeneratorClass(String keygenertator);
}