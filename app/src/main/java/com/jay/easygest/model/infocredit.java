package com.jay.easygest.model;

public class infocredit {


    private String appnumber ;
    private Integer nbrcredit;
    private  Long totalcredit ;
    private  Integer nbraccount ;
    private  Long totalaccount ;

    public infocredit(String appnumber, Integer nbrcredit, Long totalcredit, Integer nbraccount, Long totalaccount) {
        this.appnumber = appnumber;
        this.nbrcredit = nbrcredit;
        this.totalcredit = totalcredit;
        this.nbraccount = nbraccount;
        this.totalaccount = totalaccount;
    }

    public String getApppkey() {
        return appnumber;
    }

    public void setApppkey(String apppkey) {
        this.appnumber = apppkey;
    }

    public Integer getNbrcredit() {
        return nbrcredit;
    }

    public void setNbrcredit(Integer nbrcredit) {
        this.nbrcredit = nbrcredit;
    }

    public Long getTotalcredit() {
        return totalcredit;
    }

    public void setTotalcredit(Long totalcredit) {
        this.totalcredit = totalcredit;
    }

    public Integer getNbraccount() {
        return nbraccount;
    }

    public void setNbraccount(Integer nbraccount) {
        this.nbraccount = nbraccount;
    }

    public Long getTotalaccount() {
        return totalaccount;
    }

    public void setTotalaccount(Long totalaccount) {
        this.totalaccount = totalaccount;
    }


    @Override
    public String toString() {
        return "infocredit{" +
                ", nbrcredit=" + nbrcredit +
                ", totalcredit=" + totalcredit +
                ", nbraccount=" + nbraccount +
                ", totalaccount=" + totalaccount +
                '}';
    }
}
