package com.linkprise.dao.transation.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.linkprise.dao.transation.TransactionIsolationLevel;

@Retention(RetentionPolicy.RUNTIME)
@Target({ java.lang.annotation.ElementType.METHOD })
public @interface Transactional {
	public abstract TransactionIsolationLevel isolationLevel();

	public abstract boolean rollbackOnly();
}