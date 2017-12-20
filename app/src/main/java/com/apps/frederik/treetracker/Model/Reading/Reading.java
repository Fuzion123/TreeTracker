package com.apps.frederik.treetracker.Model.Reading;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Frederik on 12/20/2017.
 */

public class Reading {
    @SerializedName("Data")
    @Expose
    private Double data;
    @SerializedName("TimeStamp")
    @Expose
    private Long timeStamp;

    public Double getData() {
        return data;
    }

    public void setData(Double data) {
        this.data = data;
    }

    public Long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Long timeStamp) {
        this.timeStamp = timeStamp;
    }
}
