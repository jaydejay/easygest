package com.jay.easygest.controleur;

import android.content.Context;

import com.jay.easygest.model.ArticlesModel;
import com.jay.easygest.model.Image;
import com.jay.easygest.outils.AccessLocalImage;

import java.util.ArrayList;

public class Imagecontrolleur {
    private static Imagecontrolleur imagecontrolleurInstance = null;
    private static AccessLocalImage accessLocalImage;


    public static Imagecontrolleur getImagecontrolleurInstance(Context context){
        if (Imagecontrolleur.imagecontrolleurInstance == null ){
            Imagecontrolleur.imagecontrolleurInstance = new Imagecontrolleur();
            accessLocalImage = new AccessLocalImage(context);
        }
        return imagecontrolleurInstance;
    }

    public ArrayList<Image> imagesdunarticle(ArticlesModel article){
      return accessLocalImage.imagesDunArticles(article);
    }

    public long insertImage(Image image){
        return accessLocalImage.isertImage(image);
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
