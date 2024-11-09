package com.jay.easygest.model;

import androidx.annotation.NonNull;

import com.jay.easygest.outils.MesOutils;

import java.util.Date;

public class CreditModel {
    private Integer id ;
     private Integer clientid;
     private ClientModel client;
    private String codeclient;
    private String nomclient;
    private String prenomsclient;
    private String article1;
    private String article2;
    private Integer sommecredit;
    private Integer versement;
    private Integer reste;
    private Long datecredit;
    private Long soldedat;
    private Integer numerocredit;


    public CreditModel(Integer id,Integer clientid,String article1, String article2, Integer sommecredit, Integer versement, Integer reste, Long datecredit, Integer numerocredit) {
        this.id = id;
        this.clientid = clientid;
        this.article1 = article1;
        this.article2 = article2;
        this.sommecredit = sommecredit;
        this.versement = versement;
        this.reste = reste;
        this.datecredit = datecredit;
        this.numerocredit = numerocredit;
    }

    public CreditModel(String codeclient,String nomclient,String prenomsclient,String article1, String article2, Integer sommecredit, Integer versement, Integer reste, Long datecredit, Integer numerocredit) {

        this.codeclient = codeclient;
        this.nomclient = nomclient;
        this.prenomsclient = prenomsclient;
        this.article1 = article1;
        this.article2 = article2;
        this.sommecredit = sommecredit;
        this.versement = versement;
        this.reste = reste;
        this.datecredit = datecredit;
        this.numerocredit = numerocredit;
    }

    public CreditModel(Integer id,ClientModel client, String article1, String article2, int sommecredit, int versement, int reste, long datecredit, int numerocredit) {
        this.id = id;
        this.client = client;
        this.article1 = article1;
        this.article2 = article2;
        this.sommecredit = sommecredit;
        this.versement = versement;
        this.reste = reste;
        this.datecredit = datecredit;
        this.numerocredit = numerocredit;
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

    public String getCodeclient() {
        return codeclient;
    }

    public String getNomclient() {
        return nomclient;
    }

    public String getPrenomsclient() {
        return prenomsclient;
    }

    public String getArticle1() {
        return article1;
    }

    public String getArticle2() {
        return article2;
    }

    public Integer getSommecredit() {
        return sommecredit;
    }

    public Integer getVersement() {
        return versement;
    }

    public Integer getReste() {
        return reste;
    }

    public Long getDatecredit() {
        return datecredit;
    }

    public Long getSoldedat() {
        return soldedat;
    }

    public void setSoldedat(long value) {

        this.soldedat = value;

    }

    public Integer getNumerocredit() {
        return numerocredit;
    }



    @NonNull
    public String toString() {
        return getCodeclient()+" "+getNomclient()+" "+getPrenomsclient()+"\n"
                +"credit du "+ MesOutils.convertDateToString(new Date(datecredit));
    }

    @NonNull
    public String toString2() {
        return client.getCodeclient()+"\n "+client.getNom()+" "+client.getPrenoms()+"\n"
                +"credit du "+ MesOutils.convertDateToString(new Date(datecredit));
    }

    @NonNull
    public String toString3() {
        return client.getCodeclient()+"\n "+client.getNom()+" "+client.getPrenoms()+"\n"
                +"credit du "+ MesOutils.convertDateToString(new Date(datecredit))+"\n"
                +"credit no "+ numerocredit;
    }




}
