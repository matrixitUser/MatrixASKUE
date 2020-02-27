package com.example.pavlovka.Classes.FoldersGet;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Children {
    @SerializedName("data")
    @Expose
    public Data data;

    public Data getData() {
        return data;
    }
}
