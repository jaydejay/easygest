package com.jay.easygest.controleur;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;

import com.jay.easygest.model.ArticlesModel;
import com.jay.easygest.model.Image;
import com.jay.easygest.outils.AccessLocalArticles;
import com.jay.easygest.outils.AccessLocalImage;

import java.util.ArrayList;

public class Articlescontrolleur {
    private static Articlescontrolleur articlescontrolleurInstance = null;
    private static AccessLocalArticles accessLocalArticles;
    private static AccessLocalImage accessLocalImage;
    private MutableLiveData<ArticlesModel> marticle = new MutableLiveData<>() ;
    private MutableLiveData<ArrayList<ArticlesModel>> marticles = new MutableLiveData<>() ;


    public Articlescontrolleur() {
        super();
    }

    public static Articlescontrolleur getArticlescontrolleurInstance(Context context) {
        if (Articlescontrolleur.articlescontrolleurInstance == null){
            Articlescontrolleur.articlescontrolleurInstance = new Articlescontrolleur();
            accessLocalArticles = new AccessLocalArticles(context);
            accessLocalImage = new AccessLocalImage(context);
        }
        return articlescontrolleurInstance;
    }

    public MutableLiveData<ArticlesModel> getMarticle() {
        return marticle;
    }

    public void setMarticle(ArticlesModel marticle) {
        this.marticle.setValue(marticle);
    }

    public MutableLiveData<ArrayList<ArticlesModel>> getMarticles() {
        return marticles;
    }

    public void setMarticles(ArrayList<ArticlesModel> marticles) {
        this.marticles.postValue(marticles);
    }

    public ArticlesModel insertArticle(ArticlesModel article){
        return accessLocalArticles.insertArticle(article);

    }

    public ArticlesModel recupererArticle(int articleid){
      return accessLocalArticles.recupererArticle(articleid);
    }

    public ArticlesModel updateArticle(ArticlesModel article){
       return accessLocalArticles.updateArticle(article);
    }

    public int deleteArticle(ArticlesModel article){
        int rslt = accessLocalArticles.deleteArticle(article);
        if (rslt > 0){
            this.listeArticles();
        }
        return rslt;
    }

    public ArrayList<ArticlesModel> listeArticles(){
        ArrayList<ArticlesModel> liste_articles =  accessLocalArticles.listeArticles();
        ArrayList<ArticlesModel> articles =  new ArrayList<>();
        if (liste_articles.size() != 0){
            for (ArticlesModel articlesModel:liste_articles) {
                ArrayList<Image> images = accessLocalImage.imagesDunArticles(articlesModel);
                ArticlesModel article = new ArticlesModel(articlesModel.getId(),articlesModel.getDesignation(),articlesModel.getPrix(),articlesModel.getQuantite(),articlesModel.getDescription(),images);
                articles.add(article);
            }
        }
        this.setMarticles(articles);
       return articles ;
    }

    public int updateImage(int oldImageId, Image newImage) {
        return accessLocalArticles.updateImage(oldImageId,newImage);
    }
}
