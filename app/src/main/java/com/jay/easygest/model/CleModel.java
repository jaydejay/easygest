package com.jay.easygest.model;

public class CleModel {

    private int id ;
    private String cle;

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

    @Override
    public String toString() {
        return "cle : " + cle ;

    }
}
