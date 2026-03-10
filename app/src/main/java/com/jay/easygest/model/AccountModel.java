package com.jay.easygest.model;

import androidx.annotation.NonNull;

import com.jay.easygest.outils.MesOutils;

import java.util.Date;

public class AccountModel {

    private Integer id ;
    private ClientModel client;
    private String codeclient;
    private String nomclient;
    private String prenomsclient;
    private Article article1;
    private Article article2;
    private Integer sommeaccount;
    private Integer versement;
    private Integer reste;
    private Long dateaccount;
    private Long soldedat;
    private Integer numeroaccount;

    public AccountModel() {
    }

    public AccountModel(String codeclient, String nomclient, String prenomsclient, Article article1, Article article2, Integer versement, Long dateaccount, Integer numeroaccount) {

        this.codeclient = codeclient;
        this.nomclient = nomclient;
        this.prenomsclient = prenomsclient;
        this.article1 = article1;
        this.article2 = article2;
        this.versement = versement;
        this.dateaccount = dateaccount;
        this.numeroaccount = numeroaccount;
        calculSommeAccount();
        calculResteAccount();
    }

    public AccountModel(Integer id,ClientModel client, Article article1, Article article2,int versement, long dateaccount, int numeroaccount) {
        this.id = id;
        this.client = client;
        this.article1 = article1;
        this.article2 = article2;
        this.versement = versement;
        this.dateaccount = dateaccount;
        this.numeroaccount = numeroaccount;
        calculSommeAccount();
        calculResteAccount();
    }


    public Integer getId() {
        return id;
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

    public Article getArticle1() {
        return article1;
    }

    public Article getArticle2() {
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
        this.soldedat = getReste() == 0 ? value : 0L;
    }

    private void calculSommeAccount(){
        this.sommeaccount = article1.getSomme() + article2.getSomme();
    }

    private void calculResteAccount(){
        this.reste = sommeaccount - versement;
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
