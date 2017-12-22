package com.apps.frederik.treetracker.Model.Metadata;

/**
 * Created by frede on 20/12/2017.
 */

public class Metadata {
    private String Type;

    public Metadata(){

    }

    public Metadata(String type){
        Type = type;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        this.Type = type;
    }
}
