package com.cao.util;

/**
 * @author admin_cg
 * @data 2020/10/8 20:48
 */
public class PageSupport {
    private int currentPageNo = 1;
    private int totalCount = 0;
    private int pageSize = 0;
    private int totalPageCount = 1;

    public int getCurrentPageNo() {
        return currentPageNo;
    }

    public void setCurrentPageNo(int currentPageNo) {
        if(currentPageNo > 0){
            this.currentPageNo = currentPageNo;
        }
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        if (totalCount > 0){
            this.totalCount = totalCount;
        }
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        if (pageSize > 0){
            this.pageSize = pageSize;
        }
    }

    public int getTotalPageCount() {
        this.setTotalPageCount();
        return totalPageCount;
    }

    private void setTotalPageCount() {
        if(this.totalCount % this.pageSize == 0){
            this.totalPageCount = this.totalCount / this.pageSize;
        } else if (this.totalCount % this.pageSize > 0){
            this.totalPageCount = this.totalCount / this.pageSize + 1;
        } else {
            this.totalPageCount = 0;
        }
    }
}
