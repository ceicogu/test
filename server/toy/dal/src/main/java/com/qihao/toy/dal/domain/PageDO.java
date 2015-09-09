package com.qihao.toy.dal.domain;

import lombok.Getter;
import lombok.Setter;



/**
 * PageDO 默认需要分页的就都可以继承这个DO，直接提供分页相关的东西
 */
@Getter
@Setter
public abstract class PageDO extends BaseDO {
	
	private static final long serialVersionUID = -8211167542197595896L;
	
	private static final int DEFULT_PAGE_SIZE = 20;
	private static final int MAX_PAGE_SIZE =100;
	/**
	 * 偏移量
	 */
	private int offset = 0;
	/**
	 * 分页大小
	 */
	private int limit = DEFULT_PAGE_SIZE;
	/**
	 * 页数
	 */
	private int page = 1;
	/**
	 * 分页排序用，常用常量参见PageConstans，可以的话都加到这里，这样方便重用
	 */
	private String orderBy;

	/**
	 * 特殊处理负数页
	 * 
	 * @param page
	 */
	public void setPage(int page) {
		page = page < 1 ? 1 : page;
		this.page = page;
		if (this.offset == 0) {
			this.offset = (page - 1) * limit;
		}
	}

	/**
	 * 特殊处理分页大小
	 * 
	 * @param limit
	 */
	public void setLimit(int limit) {
		limit = limit < 1 ? 1 : limit;
		limit = limit > MAX_PAGE_SIZE ? MAX_PAGE_SIZE
				: limit;
		this.limit = limit;
	}

	/**
	 * 设置分页，用于取多一个时使用。
	 * 按照尽量降低消耗的方式进行分页，那么就需要每次多取一个，然后发现结果集多的话就是有下一页。只有在这种情况下这个方法才有用
	 * 
	 * @param page
	 *            当前页
	 * @param pageSize
	 *            页面显示条数
	 * @param limit
	 *            取多少个
	 */
	public void buildPageLimit(int page, int pageSize, int limit) {
		setLimit(pageSize);
		setPage(page);
		setLimit(limit);
	}

	/**
	 * 设置分页,第几页，每页数
	 * 
	 * @param page
	 *            当前页
	 * @param pageSize
	 *            页面显示条数
	 */
	public void buildPageLimit(int page, int pageSize) {
		setLimit(pageSize);
		setPage(page);
	}

	/**
	 * 设置分页，每页数
	 * 
	 * @param pageSize
	 *            页面显示条数
	 */
	public void buildPageLimit(int pageSize) {
		setLimit(pageSize);
		setPage(page);
	}
}
