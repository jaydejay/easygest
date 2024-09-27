package com.jay.easygest.model;

import androidx.annotation.NonNull;

import java.util.Date;

public class UserModel {

    private Integer id;
    private String username ;
    private String password;
    private Date dateInscription;
    private Integer status;
    private boolean actif;
    private Integer compteur;

    public UserModel( Integer id, String username, String password, Date dateInscription, Integer status,boolean actif, Integer compteur) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.dateInscription = dateInscription;
        this.status = status;
        this.actif = actif;
        this.compteur = compteur;
    }

    /**
     * constructeur pour creer un nouvel utilisateur
     * @param username le nom utilisteur
     * @param password le mot de passe
     * @param dateInscription la date d'inscription
     * @param status le status de l'utilisateur (utillisateur,administrateur ou super administrateur)
     * @param actif actif ou pas
     * @param compteur le nombre de tentatifs de connection
     */
    public UserModel(  String username, String password, Date dateInscription, Integer status,boolean actif, Integer compteur) {
        this.username = username;
        this.password = password;
        this.dateInscription = dateInscription;
        this.status = status;
        this.actif = actif;
        this.compteur = compteur;
    }

    

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Date getDateInscription() {
        return dateInscription;
    }

    public Integer getStatus() {
        return status;
    }

    public boolean isActif() {
        return actif;
    }

    public Integer getCompteur() {
        return compteur;
    }

    @NonNull
    @Override
    public String toString() {
        return "UserModel{" +
                "username='" + username + '\'' +
                ", dateInscription=" + dateInscription +
                '}';
    }
}
