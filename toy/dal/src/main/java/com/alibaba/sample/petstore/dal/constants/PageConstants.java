package com.alibaba.sample.petstore.dal.constants;

/**
 * 分页相关常数
 */
public class PageConstants {

    /**
     * 默认分页大小
     */
    public static final int   DEFULT_PAGE_SIZE    = 20;
    /**
     * 默认分页大小+1，用于分页的下一页
     */
    public static final int   DEFULT_PAGE_SIZE_P1 = 21;
    /**
     * 默认最大分页大小，毕竟不限制的话很有可能会出现全量取然后OOM的问题，但这个字段还是应该根据实际项目调整大小
     */
    public static final int   MAX_PAGE_SIZE       = 65535;
    /**
     * 默认id倒序
     */
    public static final String ORDER_BY_ID_DESC="id desc";
}
