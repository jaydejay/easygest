package com.jay.easygest.model;

import androidx.annotation.NonNull;

public class DriveKeyModel {
    private String owner ;
    private String telephone ;
    private String email ;
    private String licence ;

    public DriveKeyModel(String owner, String telephone, String email, String licence) {
        this.owner = owner;
        this.telephone = telephone;
        this.email = email;
        this.licence = licence;
    }

    public String getLicence() {
        return licence;
    }

    public String getOwner() {
        return owner;
    }

    public String getTelephone() {
        return telephone;
    }
    public String getEmail() {
        return email;
    }

    @NonNull
    @Override
    public String toString() {
        return "DriveKeyModel{" +
                "licence='" + licence + '\'' +
                ", owner='" + owner + '\'' +
                ", telephone='" + telephone + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
