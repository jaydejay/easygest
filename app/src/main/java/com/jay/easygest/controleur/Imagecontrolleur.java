package com.jay.easygest.controleur;

import android.content.Context;
import android.graphics.Bitmap;

import androidx.lifecycle.MutableLiveData;

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
    public MutableLiveData<ArrayList<Image>> getMimages() {
        return mimages;
    }
    public MutableLiveData<Bitmap> getMimageface() {
        return mimageface;
    }
    public MutableLiveData<Bitmap> getMimageleft() {
        return mimageleft;
    }



    public MutableLiveData<Bitmap> getMimageright() {
        return mimageright;
    }


    public MutableLiveData<Bitmap> getMimageback() {
        return mimageback;
    }

    public long insertImage(Image image){
        return accessLocalImage.isertImage(image);
    }

    public int updateImageInt(Image newImage){
        return accessLocalImage.updateImageInt( newImage);
    }
}
