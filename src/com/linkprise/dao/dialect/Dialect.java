package com.linkprise.dao.dialect;

public abstract interface Dialect {
	public abstract boolean supportsOffset();

	public abstract String getLimitString(String paramString, int paramInt1,
			int paramInt2);

	public abstract String getSelectGUIDString();

	public abstract String getSequenceNextValString(String paramString);
}