package com.jay.easygest.model;

import androidx.annotation.NonNull;

public class VersementsaccModel {
    private Integer id;
    private ClientModel client;
    private  AccountModel account;
    private Long sommeverse;
    private Long dateversement;


    public VersementsaccModel(int id, ClientModel client, AccountModel account, Long sommeverse, Long dateversement) {
        this.id = id;
        this.client = client;
        this.account = account;
        this.sommeverse = sommeverse;
        this.dateversement = dateversement;
    }

    public Integer getId() {
        return id;
    }

    public ClientModel getClient() {
        return client;
    }

    public AccountModel getAccount() {
        return account;
    }

    public Long getSommeverse() {
        return sommeverse;
    }
    public Long getDateversement() {
        return dateversement;
    }



    @NonNull
    @Override
    public String toString() {
        return "VersementsaccModel{" +
                "id=" + id +
                ", codeclient='" + client.getCodeclient() + '\'' +
                ", dateversement=" + getDateversement() +
                '}';
    }

}
