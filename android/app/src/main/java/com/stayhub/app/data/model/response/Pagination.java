package com.stayhub.app.data.model.response;

import com.google.gson.annotations.SerializedName;

/**
 * Thông tin phân trang từ Backend
 */
public class Pagination {

    @SerializedName("total")
    private int total;

    @SerializedName("page")
    private int page;

    @SerializedName("limit")
    private int limit;

    @SerializedName("totalPages")
    private int totalPages;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }
}

