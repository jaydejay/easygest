package com.jay.easygest.model;

import androidx.annotation.NonNull;

public class AppKessModel {

    private final int appnumber;
    private final String apppkey;
    private final String owner;
    private final String basecode;
    private final String telephone;
    private final String adresseelectro;

    public AppKessModel(int appnumber, String apppkey, String owner, String basecode, String telephone, String adresseelectro) {
        this.appnumber = appnumber;
        this.apppkey = apppkey;
        this.owner = owner;
        this.basecode = basecode;
        this.telephone = telephone;
        this.adresseelectro = adresseelectro;
    }

    public int getAppnumber() {
        return appnumber;
    }

    public String getApppkey() {
        return apppkey;
    }

    public String getOwner() {
        return owner;
    }

    public String getBasecode() {
        return basecode;
    }

    public String getTelephone() {
        return telephone;
    }

    public String getAdresseelectro() {
        return adresseelectro;
    }

    @NonNull
    @Override
    public String toString() {
        return "AppKessModel{" +
                "appnumber=" + appnumber +
                ", apppkey='" + apppkey + '\'' +
                ", owner='" + owner + '\'' +
                ", basecode='" + basecode + '\'' +
                ", telephone='" + telephone + '\'' +
                ", adresseelectro='" + adresseelectro + '\'' +
                '}';
    }
}
