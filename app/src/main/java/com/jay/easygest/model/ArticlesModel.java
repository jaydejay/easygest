package com.jay.easygest.model;

import androidx.annotation.NonNull;

import java.util.ArrayList;

public class ArticlesModel {

    private Integer id ;
    private String designation;
    private Integer prix;
    private Integer quantite;
    private String description;
    private ArrayList<Image> images;

    public ArticlesModel(String designation, Integer prix, Integer quantite, String description, ArrayList<Image> images) {
        this.designation = designation;
        this.prix = prix;
        this.quantite = quantite;
        this.description = description;
        this.images = images;
    }

    public ArticlesModel(Integer id, String designation, Integer prix, Integer quantite, String description, ArrayList<Image> images) {
        this.id = id;
        this.designation = designation;
        this.prix = prix;
        this.quantite = quantite;
        this.description = description;
        this.images = images;
    }

    public ArticlesModel(Integer id, String designation, Integer prix, Integer quantite, String description) {
        this.id = id;
        this.designation = designation;
        this.prix = prix;
        this.quantite = quantite;
        this.description = description;
    }


    public Integer getId() {
        return id;
    }

    public String getDesignation() {
        return designation;
    }

    public Integer getPrix() {
        return prix;
    }

    public Integer getQuantite() {
        return quantite;
    }

    public String getDescription() {
        return description;
    }

    public ArrayList<Image> getImages() {
        return images;
    }

    public void setImages(ArrayList<Image> images) {
        this.images = images;
    }

    @NonNull
    @Override
    public String toString() {
        return  "" + designation + ": " + prix;
    }
}
