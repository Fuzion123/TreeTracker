package com.apps.frederik.treetracker.Model.Metadata;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by frede on 20/12/2017.
 */

public class Metadata {
    @SerializedName("Type")
    @Expose
    private String type;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
