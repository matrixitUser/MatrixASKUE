package com.example.matrixaskue.Classes.QueryFromDatabase;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class QueryDB {
    //--------------Set
    @SerializedName("targets")
    @Expose
    public String[] targets;

    @SerializedName("start")
    @Expose
    public Date start;

    @SerializedName("end")
    @Expose
    public Date end;

    @SerializedName("type")
    @Expose
    public String type;

    public void setQueryDB(String[] ids, Date start, Date end, String type) {
        this.targets = ids;
        this.start = start;
        this.end = end;
        this.type = type;
    }
    //--------------Get
    @SerializedName("records")
    @Expose
    public RecordsFromQueryDB[] records;

    public RecordsFromQueryDB[] getRecords() {
        return records;
    }
}
