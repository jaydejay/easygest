package com.jay.easygest.model;

import androidx.annotation.NonNull;

public class ClientModel {

    private Integer id;
    private String codeclient;
    private String nom;
    private String prenoms;
    private String telephone;
    private String email;
    private String residence;
    private String cni;
    private String permis;
    private String passport;
    private String societe;
    private Integer nbrcredit;
    private Long totalcredit;
    private Integer nbraccount;
    private Long totalaccount;



    public ClientModel(Integer id, String codeclient, String nom, String prenoms, String telephone, String email, String residence, String cni, String permis, String passport, String societe, Integer nbrcredit,Long totalcredit,Integer nbraccount,Long totalaccount) {
        this.id = id;
        this.codeclient = codeclient;
        this.nom = nom;
        this.prenoms = prenoms;
        this.telephone = telephone;
        this.email = email;
        this.residence = residence;
        this.cni = cni;
        this.permis = permis;
        this.passport = passport;
        this.societe = societe;
        this.nbrcredit = nbrcredit;
        this.totalcredit = totalcredit;
        this.nbraccount = nbraccount;
        this.totalaccount = totalaccount;

    }


    public Integer getId() {
        return id;
    }

    public String getCodeclient() {
        return codeclient;
    }

    public String getNom() {
        return nom;
    }

    public String getPrenoms() {
        return prenoms;
    }

    public String getTelephone() {
        return telephone;
    }

    public String getEmail() {
        return email;
    }

    public String getResidence() {
        return residence;
    }

    public String getCni() {
        return cni;
    }

    public String getPermis() {
        return permis;
    }

    public String getPassport() {
        return passport;
    }

    public String getSociete() {
        return societe;
    }

    public Integer getNbrcredit() {
        return nbrcredit;
    }
    public Long getTotalcredit() {
        return totalcredit;
    }

    public Integer getNbraccount() {
        return nbraccount;
    }

    public Long getTotalaccount() {
        return totalaccount;
    }

    @NonNull
    @Override
    public String toString() {
        return "ClientModel{" +
                "codeclient='" + codeclient + '\'' +
                ", nom='" + nom + '\'' +
                ", prenoms='" + prenoms + '\'' +
                '}';
    }
}
