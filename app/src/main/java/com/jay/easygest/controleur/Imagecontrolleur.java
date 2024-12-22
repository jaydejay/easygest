package com.jay.easygest.controleur;

import android.content.Context;
import android.graphics.Bitmap;

import androidx.lifecycle.MutableLiveData;

import com.jay.easygest.model.ArticlesModel;
import com.jay.easygest.model.Image;
import com.jay.easygest.outils.AccessLocalImage;

import java.util.ArrayList;

public class Imagecontrolleur {
    private static Imagecontrolleur imagecontrolleurInstance = null;
    private static AccessLocalImage accessLocalImage;
    private MutableLiveData<Image> mimage = new MutableLiveData<>();
    private MutableLiveData<Bitmap> mimageface = new MutableLiveData<>();
    private MutableLiveData<Bitmap> mimageleft = new MutableLiveData<>();
    private MutableLiveData<Bitmap> mimageright = new MutableLiveData<>();
    private MutableLiveData<Bitmap> mimageback = new MutableLiveData<>();
    private MutableLiveData<ArrayList<Image>> mimages= new MutableLiveData<>();


    public static Imagecontrolleur getImagecontrolleurInstance(Context context){
        if (Imagecontrolleur.imagecontrolleurInstance == null ){
            Imagecontrolleur.imagecontrolleurInstance = new Imagecontrolleur();

        }
        accessLocalImage = new AccessLocalImage(context);
        return imagecontrolleurInstance;
    }

    public MutableLiveData<Image> getMimage() {
        return mimage;
    }

    public void setMimage(Image image) {
        this.mimage.setValue(image);
    }

    public MutableLiveData<ArrayList<Image>> getMimages() {
        return mimages;
    }

    public void setMimages(ArrayList<Image> images) {
        this.mimages.setValue(images);
    }

    public MutableLiveData<Bitmap> getMimageface() {
        return mimageface;
    }

    public void setMimageface(Bitmap imageface) {
        this.mimageface.setValue(imageface);
    }

    public MutableLiveData<Bitmap> getMimageleft() {
        return mimageleft;
    }

    public void setMimageleft(Bitmap imageleft) {
        this.mimageleft.setValue(imageleft);
    }

    public MutableLiveData<Bitmap> getMimageright() {
        return mimageright;
    }

    public void setMimageright(Bitmap imageright) {
        this.mimageright.setValue(imageright);
    }

    public MutableLiveData<Bitmap> getMimageback() {
        return mimageback;
    }

    public void setMimageback(Bitmap imageback) {
        this.mimageback.setValue(imageback);
    }

    public long insertImage(Image image){
        return accessLocalImage.isertImage(image);
    }
    public void imagesdunarticle(ArticlesModel article){
      ArrayList<Image> images = accessLocalImage.imagesDunArticles(article.getId());
      this.setMimages(images);
    }

    public Image updateImage(Image newImage){
        int rslt = accessLocalImage.updateImage(newImage);
        Image image  = null;
        if (rslt > 0){
            image  = accessLocalImage.recupererimage(newImage.getId());
        }
        return image;
    }

    public int updateImageInt(Image newImage){
        return accessLocalImage.updateImageInt( newImage);
    }
}
