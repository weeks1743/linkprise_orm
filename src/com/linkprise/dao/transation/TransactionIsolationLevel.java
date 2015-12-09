package com.linkprise.dao.transation;

public enum TransactionIsolationLevel {
	NONE(0), READ_COMMITTED(2), READ_UNCOMMITTED(1), REPEATABLE_READ(4), SERIALIZABLE(
			8);

	private final int level;

	private TransactionIsolationLevel(int level) {
		this.level = level;
	}

	public int getLevel() {
		return this.level;
	}
}