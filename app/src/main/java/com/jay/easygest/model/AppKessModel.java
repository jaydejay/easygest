package com.jay.easygest.model;

public class AppKessModel {

    private int appnumber;
    private String apppkey;
    private String owner;
    private String basecode;
    private String telephone;
    private String adresseelectro;

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
