package com.jay.easygest.model;

public class VersementsaccModel {
    private Integer id;
    private String codeclient;
    private ClientModel client;
    private  AccountModel account;
    private Long sommeverse;
    private Integer account_id;
    private Long dateversement;


    public VersementsaccModel(String codeclient, Long sommeverse, Integer account_id, Long dateversement) {

        this.codeclient = codeclient;
        this.sommeverse = sommeverse;
        this.account_id = account_id;
        this.dateversement = dateversement;
    }

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

    public String getCodeclient() {
        return codeclient;
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

    public Integer getAccount_id() {
        return account_id;
    }

    public Long getDateversement() {
        return dateversement;
    }



    @Override
    public String toString() {
        return "VersementsaccModel{" +
                "id=" + id +
                ", codeclient='" + client.getCodeclient() + '\'' +
                ", dateversement=" + getDateversement() +
                '}';
    }

}
