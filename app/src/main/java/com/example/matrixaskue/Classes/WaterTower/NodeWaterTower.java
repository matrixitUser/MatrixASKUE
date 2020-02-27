package com.example.matrixaskue.Classes.WaterTower;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class NodeWaterTower {
    // ---------------------Set
    @SerializedName("objectId")
    @Expose
    public String objectId;


    @SerializedName("max")
    @Expose
    public Float max;

    @SerializedName("min")
    @Expose
    public Float min;

    @SerializedName("controlMode")
    @Expose
    public int controlMode ;


    public void setNodeWaterTower(String objectId, Float max, Float min, Integer controlMode) {
        this.objectId = objectId;
        this.max = max;
        this.min = min;
        this.controlMode = controlMode;
    }



}
