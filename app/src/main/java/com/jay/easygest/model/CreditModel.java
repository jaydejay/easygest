package com.jay.easygest.model;

import androidx.annotation.NonNull;

import com.jay.easygest.outils.MesOutils;

import java.util.Date;

public class CreditModel {
    private Integer id ;
    private ClientModel client;
    private String codeclient;
    private String nomclient;
    private String prenomsclient;
    private Article article1;
    private Article article2;
    private Integer sommecredit;
    private Integer versement;
    private Integer reste;
    private Long datecredit;
    private Long soldedat;
    private  Integer numerocredit;

    public CreditModel() {}

    public CreditModel(String codeclient, String nomclient, String prenomsclient, Article article1, Article article2, Integer versement, Long datecredit, Integer numerocredit) {

        this.codeclient = codeclient;
        this.nomclient = nomclient;
        this.prenomsclient = prenomsclient;
        this.article1 = article1;
        this.article2 = article2;
        this.versement = versement;
        this.datecredit = datecredit;
        this.numerocredit = numerocredit;
        calculSommeCredit();
        calculResteCredit();
    }

    public CreditModel(Integer id, ClientModel client, Article article1, Article article2, int versement, long datecredit, int numerocredit) {
        this.id = id;
        this.client = client;
        this.article1 = article1;
        this.article2 = article2;
        this.versement = versement;
        this.datecredit = datecredit;
        this.numerocredit = numerocredit;
        calculSommeCredit();
        calculResteCredit();
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

    public Integer getVersement() {
        return versement;
    }

    public Long getDatecredit() {
        return datecredit;
    }

    public void setSoldedat(long value) {
        this.soldedat = getReste() == 0 ? value : 0L;
    }
    public Long getSoldedat() {
        return soldedat;
    }
    public Integer getNumerocredit() {
        return numerocredit;
    }

    private void calculSommeCredit(){
        this.sommecredit = article1.getSomme() + article2.getSomme();
    }

    public void setSommecredit(Integer sommecredit) {
        this.sommecredit = sommecredit;
    }


    public Integer getSommecredit() {
        return sommecredit;
    }

    private void calculResteCredit(){
        this.reste = sommecredit - versement;
    }

    public void setReste(Integer reste) {
        this.reste = reste;
    }


    public Integer getReste() {
        return reste;
    }



    @NonNull
    public String toString() {
        return getCodeclient()+" "+ getNomclient()+" "+getPrenomsclient()+"\n"
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
