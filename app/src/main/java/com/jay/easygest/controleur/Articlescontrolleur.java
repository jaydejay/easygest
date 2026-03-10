package com.jay.easygest.controleur;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;

import com.jay.easygest.model.ArticlesModel;
import com.jay.easygest.outils.AccessLocalArticles;

import java.util.ArrayList;

public class Articlescontrolleur {
    private static Articlescontrolleur articlescontrolleurInstance = null;
    private static AccessLocalArticles accessLocalArticles;
    private final MutableLiveData<ArticlesModel> marticle = new MutableLiveData<>() ;
    private final MutableLiveData<ArrayList<ArticlesModel>> marticles = new MutableLiveData<>();
    private final MutableLiveData<ArrayList<ArticlesModel>> marticlescredit = new MutableLiveData<>();


    public Articlescontrolleur() {
        super();
    }

    public static Articlescontrolleur getArticlescontrolleurInstance(Context context) {
        if (Articlescontrolleur.articlescontrolleurInstance == null){
            Articlescontrolleur.articlescontrolleurInstance = new Articlescontrolleur();
            accessLocalArticles = new AccessLocalArticles(context);
        }
//        accessLocalArticles = new AccessLocalArticles(context);
        return articlescontrolleurInstance;
    }

    public MutableLiveData<ArticlesModel> getMarticle() {
        return marticle;
    }

    public void setMarticle(ArticlesModel marticle) {
        this.marticle.setValue(marticle);
    }


    public void setMarticles(ArrayList<ArticlesModel> marticles) {
        this.marticles.setValue(marticles);
    }
    public MutableLiveData<ArrayList<ArticlesModel>> getMarticles() {
        return marticles;
    }

    public void setMarticlescredit(ArrayList<ArticlesModel> marticles) {
        this.marticlescredit.setValue(marticles);
    }
    public MutableLiveData<ArrayList<ArticlesModel>> getMarticlesCredit() {
        return marticlescredit;
    }

//    public MutableLiveData<ArrayList<ArticlesModel>> getAdaptermarticles() {
//        return adaptermarticles;
//    }
//
//    public void setAdaptermarticles(ArrayList<ArticlesModel> articles) {
//        this.adaptermarticles.setValue(articles);
//    }

    public ArticlesModel insertArticle(ArticlesModel article){
        return accessLocalArticles.insertArticle(article);

    }


    public ArticlesModel updateArticle(ArticlesModel article){
       return accessLocalArticles.updateArticle(article);
    }

    public int deleteArticle(ArticlesModel article){
        return accessLocalArticles.deleteArticle(article);
    }


    public void listeArticles2(){
        ArrayList<ArticlesModel> liste_articles =  accessLocalArticles.listeArticles();
        this.setMarticles(liste_articles);
    }

    public void listeArticlescredit(){
        ArrayList<ArticlesModel> liste_articles_credit =  accessLocalArticles.listeArticlescredit();
        this.setMarticlescredit(liste_articles_credit);
    }

    public ArticlesModel getArticleByDesignation(String designation){
      return   accessLocalArticles.getArticle(designation);
    }

    public int modifierArticle(ArticlesModel article, String champ, String valeur, String itemId) {
        return accessLocalArticles.updateArticleStandard(article,champ,valeur,itemId);
    }
}
