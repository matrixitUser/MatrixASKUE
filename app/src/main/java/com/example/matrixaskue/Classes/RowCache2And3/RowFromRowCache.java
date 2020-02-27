package com.example.matrixaskue.Classes.RowCache2And3;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RowFromRowCache {

    @SerializedName("id")
    @Expose
    public String id;

    @SerializedName("name")
    @Expose
    public String name;

    @SerializedName("pname")
    @Expose
    public String pname;

    @SerializedName("value")
    @Expose
    public float value;

    @SerializedName("valueUnitMeasurement")
    @Expose
    public String valueUnitMeasurement;

    @SerializedName("controllerData")
    @Expose
    public String controllerData;

    @SerializedName("event")
    @Expose
    public String event;




    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPname() {
        return pname;
    }

    public float getValue() {
        return value;
    }

    public String getvalueUnitMeasurement() {
        return valueUnitMeasurement;
    }

    public String getControllerData() {
        return controllerData;
    }

    public String getEvent() {
        return event;
    }

}
