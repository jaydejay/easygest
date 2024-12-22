package com.jay.easygest.vue.ui.articles;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.jay.easygest.controleur.Articlescontrolleur;
import com.jay.easygest.model.ArticlesModel;

import java.util.ArrayList;

public class ArticlesViewModel extends ViewModel {
   private final MutableLiveData<ArticlesModel> articlelivedata;
   private final MutableLiveData<ArrayList<ArticlesModel>> articlelivedatas ;

    public ArticlesViewModel() {
        Articlescontrolleur  articlescontrolleur = Articlescontrolleur.getArticlescontrolleurInstance(null);
        this.articlelivedata = articlescontrolleur.getMarticle();
        this.articlelivedatas = articlescontrolleur.getMarticles();
    }

    public MutableLiveData<ArticlesModel> getArticlelivedata() {
        return articlelivedata;
    }

    public MutableLiveData<ArrayList<ArticlesModel>> getArticlelivedatas() {
        return articlelivedatas;
    }
}