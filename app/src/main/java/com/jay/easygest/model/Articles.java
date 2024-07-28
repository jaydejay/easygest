package com.jay.easygest.model;

import java.util.Collection;

public class Articles{
    private String designation;
    private Integer prix;
    private Integer nbrarticle;
    private Integer somme;

    public Articles() {

    }

    public Articles(String designation, Integer prix, Integer nbrarticle) {
        this.designation = designation;
        this.prix = prix;
        this.nbrarticle = nbrarticle;
        calculSomme();
    }

    public String getDesignation() {
        return designation;
    }

    public Integer getPrix() {
        return prix;
    }

    public Integer getNbrarticle() {
        return nbrarticle;
    }

    public Integer getSomme() {
        return somme;
    }

    private void calculSomme(){

        this.somme = prix*nbrarticle;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public void setPrix(Integer prix) {
        this.prix = prix;
    }

    public void setNbrarticle(Integer nbrarticle) {
        this.nbrarticle = nbrarticle;
    }

    public void setSomme(Integer somme) {
        this.somme = somme;
    }

    @Override
    public String toString() {
        return  "" + designation + ": " + somme;
    }

//    @Override
//    public String toString() {
//        return "{" +
//                "designation:'" + designation + '\'' +
//                ", prix:" + prix +
//                '}';
//    }
}
