package com.jay.easygest.model;

import androidx.annotation.NonNull;

public class CleModel {

    private int id ;
    private final String cle;

    public CleModel(String cle) {
        this.cle = cle;
    }

    public CleModel(int id,String cle ) {
        this.cle = cle;
        this.id = id;
    }

    public String getCle() {
        return cle;
    }

    public int getId() {
        return id;
    }

    @NonNull
    @Override
    public String toString() {
        return "cle : " + cle ;

    }
}
