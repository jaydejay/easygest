package com.jay.easygest.model;

public class SmsnoSentModel {



    private Integer id;
    private Integer clientid;
    private ClientModel client;
    private Integer sommeverse;
    private  Integer sommetotal;
    private Integer totalreste;
    private String operation;

    private long dateoperation;


    public SmsnoSentModel(Integer clientid, Integer sommeverse, Integer sommetotal, Integer totalreste, String operation,long dateoperation) {

        this.clientid = clientid;
        this.sommeverse = sommeverse;
        this.sommetotal = sommetotal;
        this.totalreste = totalreste;
        this.operation = operation;
        this.dateoperation = dateoperation;
    }

    public SmsnoSentModel(Integer id,ClientModel client,Integer sommeverse, Integer sommetotal, Integer totalreste, String operation,long dateoperation) {
        this.id = id;
        this.client = client;
        this.sommeverse = sommeverse;
        this.sommetotal = sommetotal;
        this.totalreste = totalreste;
        this.operation = operation;
        this.dateoperation = dateoperation;
    }

    public Integer getId() {
        return id;
    }

    public Integer getClientid() {
        return clientid;
    }

    public ClientModel getClient() {
        return client;
    }

    public Integer getSommeverse() {
        return sommeverse;
    }

    public Integer getSommetotal() {
        return sommetotal;
    }

    public Integer getTotalreste() {
        return totalreste;
    }

    public String getOperation() {
        return operation;
    }

    public long getDateoperation() {
        return dateoperation;
    }

    @Override
    public String toString() {
        return "SmsnoSentModel{" +
                "id=" + id +
                ", sommeverse=" + sommeverse +
                ", sommetotal=" + sommetotal +
                ", totalreste=" + totalreste +
                ", operation='" + operation + '\'' +
                '}';
    }
}
