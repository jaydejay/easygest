package com.jay.easygest.model;

public class InfosModel {

    private Integer appnumber ;
    private Integer nbrcredit;
    private Integer totalcredit ;
    private Integer nbraccount ;
    private Integer totalaccount ;


    public InfosModel(Integer appnumber, Integer nbrcredit, Integer totalcredit,  Integer nbraccount, Integer totalaccount) {
        this.appnumber = appnumber;
        this.nbrcredit = nbrcredit;
        this.totalcredit = totalcredit;
        this.nbraccount = nbraccount;
        this.totalaccount = totalaccount;

    }

    public Integer getAppnumber() {
        return appnumber;
    }

    public Integer getNbrcredit() {
        return nbrcredit;
    }

    public Integer getTotalcredit() {
        return totalcredit;
    }
    public Integer getNbraccount() {
        return nbraccount;
    }

    public Integer getTotalaccount() {
        return totalaccount;
    }


    @Override
    public String toString() {
        return "infocredit{" +
                ", nbr credit = " + nbrcredit +
                ", total credit = " + totalcredit +
                ", nbr account = " + nbraccount +
                ", total account = " + totalaccount +
                '}';
    }
}
