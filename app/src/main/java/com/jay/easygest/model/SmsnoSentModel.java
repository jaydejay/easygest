package com.jay.easygest.model;

public class SmsnoSentModel {



    private Integer id;
    private Integer clientid;
    private ClientModel client;
    private String message;


    public SmsnoSentModel(Integer clientid,String message) {

        this.clientid = clientid;
        this.message = message;
    }

    public SmsnoSentModel(Integer id,ClientModel client,String message) {
        this.id = id;
        this.client = client;
        this.message = message;
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

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "SmsnoSentModel{" +
                "id=" + id +
                '\'' +
                '}';
    }
}
