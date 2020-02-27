package com.example.matrixaskue.Classes.RowCache2And3;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RowCache {
    //--------------Set
    @SerializedName("filter")
    @Expose
    public RowCacheFilter filter;

    public void setFilter(String folderId, String text, Integer count, Integer offset, Integer[] order) {
        RowCacheFilter filter = new RowCacheFilter();
        if(folderId != ""){
            filter.setFolderId(folderId);
        }
        filter.setText(text);

        filter.setOrder(order);
        RowCacheFilterPage page = new RowCacheFilterPage();
        page.setCount(count);
        page.setOffset(offset);
        filter.setPage(page);
        this.filter = filter;
    }
    //--------------Get
    @SerializedName("rows")
    @Expose
    public RowFromRowCache[] rows;

    public RowFromRowCache[] getRows() {
        return rows;
    }
}

class RowCacheFilter{

    @SerializedName("folderId")
    @Expose
    public String folderId;

    @SerializedName("text")
    @Expose
    public String text;

    @SerializedName("order")
    @Expose
    public Integer[] order;

    @SerializedName("page")
    @Expose
    public RowCacheFilterPage page;

    public void setFolderId(String folderId) {
        this.folderId = folderId;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setPage(RowCacheFilterPage page) {
        this.page = page;
    }

    public void setOrder(Integer[] order) {
        this.order = order;
    }

}

class RowCacheFilterPage{

    @SerializedName("count")
    @Expose
    public Integer count;

    public void setCount(Integer count) {
        this.count = count;
    }

    @SerializedName("offset")
    @Expose
    public Integer offset;

    public void setOffset(Integer offset) {
        this.offset = offset;
    }
}