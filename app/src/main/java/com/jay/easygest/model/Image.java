package com.jay.easygest.model;


import android.graphics.Bitmap;

import androidx.annotation.NonNull;

public class Image {

    private Integer id;
    private Bitmap image;
    private byte[] image2;
    private Integer articleid;



    public Image(byte[] image2) {
        this.image2 = image2;
    }

    public Image(Bitmap image) {
        this.image = image;
    }

    public Image(Bitmap image,Integer articleid) {
        this.image = image;
        this.articleid = articleid;
    }

    public Image(byte[] image2,Integer articleid) {
        this.image2 = image2;
        this.articleid = articleid;
    }


    public Image(Integer id, Bitmap image, Integer articleid) {
        this.id = id;
        this.image = image;
        this.articleid = articleid;
    }

    public Image(Integer id, byte[] image2, Integer articleid) {
        this.id = id;
        this.image2 = image2;
        this.articleid = articleid;
    }

    public Integer getId() {
        return id;
    }

    public Bitmap getImage() {
        return image;
    }

    public Integer getArticleid() {
        return articleid;
    }

    public byte[] getImage2() {
        return image2;
    }

    @NonNull
    @Override
    public String toString() {
        return "Image{" +
                "id=" + id +
                '}';
    }
}
