package com.example.matrixaskue.Classes.EditGetRow;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class EditGetRow {
    //-------------------------------Set
    @SerializedName("isNew")
    @Expose
    public boolean isNew;


    @SerializedName("id")
    @Expose
    public String id;

    public void setEditGetRow(boolean isNew, String id) {
        this.isNew = isNew;
        this.id = id;
    }

    //-------------------------------Get
    @SerializedName("tube")
    @Expose
    public RecordFromEditGetRow tube;

    public RecordFromEditGetRow getTube() {
        return tube;
    }

}
