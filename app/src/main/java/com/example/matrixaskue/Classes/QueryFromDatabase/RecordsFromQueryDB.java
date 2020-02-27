package com.example.matrixaskue.Classes.QueryFromDatabase;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class RecordsFromQueryDB {

    @SerializedName("objectId")
    @Expose
    public String objectId;

    @SerializedName("date")
    @Expose
    public String date;

    @SerializedName("s1")
    @Expose
    public String s1;

    @SerializedName("s2")
    @Expose
    public String s2;

    @SerializedName("d1")
    @Expose
    public String d1;

    @SerializedName("dt1")
    @Expose
    public String dt1;


    public Date getDateDt() {
        Date date1 = new Date();
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSS");
        try {
            date1 = formatter.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date1;
    }

    public String getDateStr() {
        try{
            return date;
        }
        catch (Exception ex){
            return "undefined";
        }
    }

    public String getS1() {
        try{
            return s1;
        }
        catch (Exception ex){
            return "undefined";
        }
    }

    public String getS2() {
        try{
            return s2;
        }
        catch (Exception ex){
            return "undefined";
        }
    }

    public String getD1s() {
        try{
            return d1;
        }
        catch (Exception ex){
            return "undefined";
        }
    }
    public double getD1d() {
        try {
            return Double.parseDouble(d1);
        }
        catch (Exception ex){
            return 0.;
        }
    }
    public Date getDt1() {
        Date date1 = new Date();
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSS");
        try {
            date1 = formatter.parse(dt1);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date1;
    }
}
