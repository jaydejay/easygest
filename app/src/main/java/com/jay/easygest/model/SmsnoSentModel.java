package com.jay.easygest.model;

import androidx.annotation.NonNull;

import com.jay.easygest.outils.MesOutils;

public class SmsnoSentModel {



    private Integer id;
    private Integer clientid;
    private ClientModel client;
    private String message;
    private int smsid;


    public SmsnoSentModel(Integer clientid, String message) {

        this.clientid = clientid;
        this.message = message;
        calculUuid();
    }

    public SmsnoSentModel(Integer id, ClientModel client, String message, int smsid) {
        this.id = id;
        this.client = client;
        this.message = message;
        this.smsid = smsid;
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

    public int getSmsid() {
        return smsid;
    }

    public void calculUuid(){

        this.smsid = MesOutils.smsidnumbergenerator();
    }

    @NonNull
    @Override
    public String toString() {
        return ""+clientid;
    }
}
