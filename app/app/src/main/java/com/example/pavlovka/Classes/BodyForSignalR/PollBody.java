package com.example.pavlovka.Classes.BodyForSignalR;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PollBody {

    @SerializedName("objectIds")
    @Expose
    public String[] objectIds;

    @SerializedName("arg")
    @Expose
    public Object arg;

    @SerializedName("what")
    @Expose
    public String what;

    public String[] getObjectIds() {
        return objectIds;
    }

    public Object getArg() {
        return arg;
    }

    public String getWhat() {
        return what;
    }
}
