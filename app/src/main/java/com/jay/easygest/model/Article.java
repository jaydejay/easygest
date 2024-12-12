package com.jay.easygest.model;

public class Article {
    private String designation;
    private Integer prix;
    private Integer nbrarticle;
    private Integer somme;

    public Article() {}

    public Article(String designation, Integer prix, Integer nbrarticle) {
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

    public void setSomme(Integer somme) {
        this.somme = somme;
    }

    @Override
    public String toString() {
        return  "" + designation + ": " + somme;
    }


}
