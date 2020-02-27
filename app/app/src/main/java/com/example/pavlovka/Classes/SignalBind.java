package com.example.pavlovka.Classes;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SignalBind {
    @SerializedName("connectionId")
    @Expose
    public String connectionId;

    public void setConnectionId(String connectionId) {
        this.connectionId = connectionId;
    }
}