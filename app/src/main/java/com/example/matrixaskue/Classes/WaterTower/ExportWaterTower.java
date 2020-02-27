package com.example.matrixaskue.Classes.WaterTower;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ExportWaterTower {
    // ---------------------Set
    @SerializedName("objectId")
    @Expose
    public String objectId;

    public void setWaterTower(String objectId) {
        this.objectId = objectId;
    }
    // ---------------------Get
    @SerializedName("max")
    @Expose
    public String max;

    @SerializedName("min")
    @Expose
    public String min;

    @SerializedName("interval")
    @Expose
    public String interval;

    @SerializedName("criticalMax")
    @Expose
    public String criticalMax ;

    @SerializedName("criticalMin")
    @Expose
    public String criticalMin ;

    @SerializedName("controlMode")
    @Expose
    public String controlMode ;



    public String getMax() {

        return max.replace(",", ".");
    }
    public String getMin() {
        return min.replace(",", ".");
    }

    public String getCriticalMin() {
        return criticalMin.replace(",", ".");
    }

    public String getCriticalMax() {
        return criticalMax.replace(",", ".");
    }

    public String getInterval() {
        return interval.replace(",", ".");
    }

    public String getControlMode() {
        return controlMode;
    }

}

