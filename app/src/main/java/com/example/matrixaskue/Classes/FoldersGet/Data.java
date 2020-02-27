package com.example.matrixaskue.Classes.FoldersGet;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Data {
    @SerializedName("name")
    @Expose
    public String name;

    @SerializedName("id")
    @Expose
    public String id;

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }
}
