package com.example.matrixaskue.Classes.BodyForSignalR;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ListUpdateBody {
    @SerializedName("ids")
    @Expose
    public String[] ids;

    @SerializedName("idswithstate")
    @Expose
    public IdswithstateListUpdate[] idswithstate;

    public String[] getIds() {
        return ids;
    }

    public IdswithstateListUpdate[] getIdswithstate() {
        return idswithstate;
    }
}

class IdswithstateListUpdate {
    @SerializedName("Key")
    @Expose
    public String Key;

    @SerializedName("Value")
    @Expose
    public String Value;
}