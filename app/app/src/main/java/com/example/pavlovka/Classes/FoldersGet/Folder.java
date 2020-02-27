package com.example.pavlovka.Classes.FoldersGet;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Folder {
    @SerializedName("data")
    @Expose
    public Data data;

    @SerializedName("children")
    @Expose
    public Children[]  children;

    public Data getData() {
        return data;
    }

    public Children[] getChildren() {
        return children;
    }
}

