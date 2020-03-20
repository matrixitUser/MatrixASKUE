package com.example.matrixaskue.Classes.RowCache2And3;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

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

    @SerializedName("date")
    @Expose
    public String date;

    @SerializedName("resource")
    @Expose
    public String resource;



    public String getResource() {
        return resource;
    }

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

    public Date getDate() {
        Date date1 = new Date();
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSS");
        try {
            date1 = formatter.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date1;
    }
}
