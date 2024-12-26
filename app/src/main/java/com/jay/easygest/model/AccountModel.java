package com.jay.easygest.model;

import androidx.annotation.NonNull;

import com.jay.easygest.outils.MesOutils;

import java.util.Date;

public class AccountModel {

    private Integer id ;
    private Integer clientid;
    private ClientModel client;
    private String codeclient;
    private String nomclient;
    private String prenomsclient;
    private String article1;
    private String article2;
    private Integer sommeaccount;
    private Integer versement;
    private Integer reste;
    private Long dateaccount;
    private Long soldedat;
    private Integer numeroaccount;

    public AccountModel() {
    }

    public AccountModel(String codeclient, String nomclient, String prenomsclient, String article1, String article2, Integer sommeaccount, Integer versement, Integer reste, Long dateaccount, Integer numeroaccount) {

        this.codeclient = codeclient;
        this.nomclient = nomclient;
        this.prenomsclient = prenomsclient;
        this.article1 = article1;
        this.article2 = article2;
        this.sommeaccount = sommeaccount;
        this.versement = versement;
        this.reste = reste;
        this.dateaccount = dateaccount;
        this.numeroaccount = numeroaccount;
    }
    public AccountModel(Integer id,ClientModel client, String article1, String article2, int sommeaccount, int versement, int reste, long dateaccount, int numeroaccount) {
        this.id = id;
        this.client = client;
        this.article1 = article1;
        this.article2 = article2;
        this.sommeaccount = sommeaccount;
        this.versement = versement;
        this.reste = reste;
        this.dateaccount = dateaccount;
        this.numeroaccount = numeroaccount;
    }

    public AccountModel(Integer id,int clientid, String article1, String article2, int sommeaccount, int versement, int reste, long dateaccount, int numeroaccount) {
        this.id = id;
        this.clientid = clientid;
        this.article1 = article1;
        this.article2 = article2;
        this.sommeaccount = sommeaccount;
        this.versement = versement;
        this.reste = reste;
        this.dateaccount = dateaccount;
        this.numeroaccount = numeroaccount;
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

    public Integer getSommeaccount() {
        return sommeaccount;
    }

    public Integer getVersement() {
        return versement;
    }

    public Integer getReste() {
        return reste;
    }

    public Long getDateaccount() {
        return dateaccount;
    }

    public Integer getNumeroaccount() {
        return numeroaccount;
    }


    public Long getSoldedat() {
        return soldedat;
    }

    public void setSoldedat(long value) {
        this.soldedat = value;
    }

    @NonNull
    public String toString() {
        return getCodeclient()+" "+getNomclient()+" "+getPrenomsclient()+"\n"
                +"accompte du "+ MesOutils.convertDateToString(new Date(dateaccount));
    }


    @NonNull
    public String toString3() {
        return client.getCodeclient()+"\n "+client.getNom()+" "+client.getPrenoms()+"\n"
                +"accompte du "+ MesOutils.convertDateToString(new Date(dateaccount))+"\n"
                +"accompte no "+ numeroaccount;
    }




}
