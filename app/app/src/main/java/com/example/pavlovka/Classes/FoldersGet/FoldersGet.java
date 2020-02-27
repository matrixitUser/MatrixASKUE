package com.example.pavlovka.Classes.FoldersGet;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class FoldersGet {
    //----------------------------Set null

    //---------------------------Get

    @SerializedName("root")
    @Expose
    public Folder root;

    public Folder getRoot() {
        return root;
    }
}

