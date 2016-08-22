package com.linkprise.dao.common;

import java.io.Serializable;
import java.util.List;

public class PaginationSupport<T> implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * 默认每页记录数
	 */
	public static final int PAGESIZE = 20;
	
	/**
	 * 页大小
	 */
	private int pagesize = 20;

	/**
	 * 总记录数
	 */
	private long totalCount = 0L;

	/**
	 * 总页数
	 */
	private int pageCount = 0;

	/**
	 * 当前页
	 */
	private int startPage = 0;

	/**
	 * 当前页数据
	 */
	private List<T> items = null;

	/**
	 * 获得页数量
	 * @return
	 */
	public int getPageCount() {
		return this.pageCount;
	}

	public PaginationSupport(int pagesize, long totalCount, int startPage,
			List<T> items) {
		setPagesize(pagesize);
		setTotalCount(totalCount);
		setStartPage(startPage);
		this.items = items;
	}

	public PaginationSupport(long totalCount, List<T> items) {
		setPagesize(20);
		setTotalCount(totalCount);
		setStartPage(0);
		this.items = items;
	}

	public PaginationSupport(int pagesize, long totalCount, List<T> items) {
		setPagesize(pagesize);
		setTotalCount(totalCount);
		setStartPage(0);
		this.items = items;
	}

	/**
	 * 设置总页数
	 * @param pageCount
	 */
	public void setPageCount(int pageCount) {
		this.pageCount = (pageCount < 0 ? 0 : pageCount);
	}

	/**
	 * 取页大小
	 * @return
	 */
	public int getPagesize() {
		return this.pagesize;
	}

	/**
	 * 设置每页大小，默认20
	 * @param pagesize
	 */
	public void setPagesize(int pagesize) {
		this.pagesize = (pagesize < 0 ? 20 : pagesize);
	}

	/**
	 * 取当前页数
	 * @return
	 */
	public int getStartPage() {
		return this.startPage;
	}

	/**
	 * 设置当前页数
	 * @param startPage
	 */
	public void setStartPage(int startPage) {
		if ((startPage < 0) || (startPage >= this.pageCount)) {
			this.startPage = 0;
		} else
			this.startPage = startPage;
	}

	/**
	 * 取总记录数
	 * @return
	 */
	public long getTotalCount() {
		return this.totalCount;
	}

	/**
	 * 设置总记录条数
	 * @param totalCount
	 */
	public void setTotalCount(long totalCount) {
		if (totalCount > 0L) {
			this.totalCount = totalCount;
			setPageCount((int) Math.ceil(totalCount / this.pagesize));
		} else {
			this.totalCount = 0L;
			setPageCount(0);
		}
	}

	/**
	 * 取存入当前页的数据
	 * @return
	 */
	public List<T> getItems() {
		return this.items;
	}

	/**
	 * set 结果到数据中
	 * @param items
	 */
	public void setItems(List<T> items) {
		this.items = items;
	}

	/**
	 * 取下一页
	 * @return
	 */
	public int getNextPage() {
		return this.startPage < this.pageCount ? this.startPage + 1 : this.startPage;
	}

	/**
	 * 取前一页
	 * @return
	 */
	public int getPreviousPage() {
//		return this.startPage <= 0 ? 0 : this.startPage;
		return this.startPage == 0 ? 0 :  this.startPage-1;
	}

	/**
	 * 是否有下一页
	 * @return
	 */
	public boolean hasNextPage() {
		return getStartPage() < getPageCount() - 1;
	}

	/**
	 * 是否有前一页
	 * @return
	 */
	public boolean hasPreviousPage() {
		return getStartPage() > 0;
	}

	protected void finalize() throws Throwable {
		if (this.items != null)
			this.items = null;
		super.finalize();
	}
}