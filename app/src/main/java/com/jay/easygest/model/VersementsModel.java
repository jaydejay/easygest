package com.jay.easygest.model;

import androidx.annotation.NonNull;

public class VersementsModel {
    private Integer id;
    private String codeclient;
    private ClientModel client;
    private  CreditModel credit;
    private Long sommeverse;
    private Integer credit_id;
    private Long dateversement;


    public VersementsModel( String codeclient,Long sommeverse, Integer credit_id, Long dateversement) {

        this.codeclient = codeclient;
        this.sommeverse = sommeverse;
        this.credit_id = credit_id;
        this.dateversement = dateversement;
    }

    public VersementsModel(int id, ClientModel client,CreditModel credit,Long sommeverse, Integer credit_id, Long dateversement) {
        this.id = id;
        this.client = client;
        this.credit = credit;
        this.sommeverse = sommeverse;
        this.credit_id = credit_id;
        this.dateversement = dateversement;
    }




    public Integer getId() {
        return id;
    }

    public String getCodeclient() {
        return codeclient;
    }

    public ClientModel getClient() {
        return client;
    }

    public CreditModel getCredit() {
        return credit;
    }

    public Long getSommeverse() {
        return sommeverse;
    }

    public Integer getCredit_id() {
        return credit_id;
    }

    public Long getDateversement() {
        return dateversement;
    }



    @NonNull
    @Override
    public String toString() {
        return "VersementsModel{" +
                "id=" + id +
                ", codeclient='" + client.getCodeclient() + '\'' +
                ", dateversement=" + getDateversement() +
                '}';
    }

}
