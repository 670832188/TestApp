package com.dev.kit.basemodule.result;

import java.util.List;

/**
 * 通用列表数据结构
 * Created by cy on 2018/3/14.
 */

public class CommonListEntity<T> {
    /**
     * 当前页码
     */
    public int pageNo;
    /**
     * 每页数目
     */
    public int pageSize;
    /**
     * 数据总数
     */
    public int totalCount;
    /**
     * 数据总页数
     */
    public int totalPages;

    private List<T> dataList;

    public int getPageNo() {
        return pageNo;
    }

    public int getPageSize() {
        return pageSize;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public List<T> getDataList() {
        return dataList;
    }

    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public void setDataList(List<T> dataList) {
        this.dataList = dataList;
    }
}
