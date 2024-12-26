package com.jay.easygest.vue.ui.articles;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.jay.easygest.controleur.Articlescontrolleur;
import com.jay.easygest.model.ArticlesModel;

import java.util.ArrayList;
import java.util.Objects;

public class ArticlesViewModel extends ViewModel {
   private final MutableLiveData<ArticlesModel> articlelivedata;
   private final MutableLiveData<ArrayList<ArticlesModel>> articlelivedatas ;
   private  MutableLiveData<ArrayList<ArticlesModel>> articleAdapterlivedatas ;

    public ArticlesViewModel() {
        Articlescontrolleur  articlescontrolleur = Articlescontrolleur.getArticlescontrolleurInstance(null);
        this.articlelivedata = articlescontrolleur.getMarticle();
        this.articlelivedatas = articlescontrolleur.getMarticles();
        this.articleAdapterlivedatas = articlescontrolleur.getAdaptermarticles();
    }

    public MutableLiveData<ArticlesModel> getArticlelivedata() {
        return articlelivedata;
    }

    public MutableLiveData<ArrayList<ArticlesModel>> getArticlelivedatas() {
        return articlelivedatas;
    }


    public void getArticleInstocklivedatas() {
        ArrayList<ArticlesModel> filteredliste = new ArrayList<>();
        for (ArticlesModel article : Objects.requireNonNull(getArticlelivedatas().getValue())) {
            if (article.getId() != null){
                if (article.getQuantite() > 0 ){
                    filteredliste.add(article);

                }
            }

        }
//        articlelivedatas.setValue(filteredliste);
        articleAdapterlivedatas.setValue(filteredliste);
    }

    public MutableLiveData<ArrayList<ArticlesModel>> getArticleAdapterlivedatas() {
        return articleAdapterlivedatas;
    }

    public void getArticleOutStocklivedatas() {
        ArrayList<ArticlesModel> filteredliste = new ArrayList<>();
        for (ArticlesModel article : Objects.requireNonNull(getArticlelivedatas().getValue())) {
            if (article.getId() != null){
                if (article.getQuantite() == 0 ){
                    filteredliste.add(article);

                }
            }

        }
//        articlelivedatas.setValue(filteredliste);
        articleAdapterlivedatas.setValue(filteredliste);
    }
}