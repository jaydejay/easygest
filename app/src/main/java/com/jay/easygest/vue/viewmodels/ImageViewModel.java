package com.jay.easygest.vue.viewmodels;

import android.graphics.Bitmap;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.jay.easygest.controleur.Imagecontrolleur;
import com.jay.easygest.model.Image;

import java.util.ArrayList;

public class ImageViewModel extends ViewModel {

    private  MutableLiveData<Image> image = new MutableLiveData<>();
    private MutableLiveData<ArrayList<Image>> images ;
    private MutableLiveData<Bitmap> bitmapimageface ;
    private MutableLiveData<Bitmap> bitmapimageleft ;
    private MutableLiveData<Bitmap> bitmapimageright ;
    private MutableLiveData<Bitmap> bitmapimageback ;

    public ImageViewModel() {
        Imagecontrolleur imagecontrolleur = Imagecontrolleur.getImagecontrolleurInstance(null);
        this.image = imagecontrolleur.getMimage();
        this.images = imagecontrolleur.getMimages();
        this.bitmapimageface = imagecontrolleur.getMimageface();
        this.bitmapimageleft = imagecontrolleur.getMimageleft();
        this.bitmapimageright = imagecontrolleur.getMimageright();
        this.bitmapimageback = imagecontrolleur.getMimageback();
    }

    public MutableLiveData<Image> getImage() {
        return image;
    }
    public MutableLiveData<ArrayList<Image>> getImages() {
        return images;
    }

    public MutableLiveData<Bitmap> getBitmapimageface() {
        return bitmapimageface;
    }

    public MutableLiveData<Bitmap> getBitmapimageleft() {
        return bitmapimageleft;
    }

    public MutableLiveData<Bitmap> getBitmapimageright() {
        return bitmapimageright;
    }

    public MutableLiveData<Bitmap> getBitmapimageback() {
        return bitmapimageback;
    }
}