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
   private final MutableLiveData<ArrayList<ArticlesModel>> article_credit_livedatas;
   private final MutableLiveData<ArrayList<ArticlesModel>> articleAdapterlivedatas;


    public ArticlesViewModel() {
        Articlescontrolleur  articlescontrolleur = Articlescontrolleur.getArticlescontrolleurInstance(null);
        this.articlelivedata = articlescontrolleur.getMarticle();
        //liste de tous les articles avec les images
        this.articlelivedatas = articlescontrolleur.getMarticles();
        //liste de tous les articles sans les images
        this.article_credit_livedatas = articlescontrolleur.getMarticlesCredit();

        this.articleAdapterlivedatas = new MutableLiveData<>();
    }
    public MutableLiveData<ArticlesModel> getArticlelivedata() {
        return articlelivedata;
    }

    public MutableLiveData<ArrayList<ArticlesModel>> getArticlelivedatas() {
        return articlelivedatas;
    }
    public MutableLiveData<ArrayList<ArticlesModel>> getArticlelivedatascredit() {
        return article_credit_livedatas;
    }


    public void getArticleslivedatas() {
        ArrayList<ArticlesModel> filteredliste = new ArrayList<>();
        for (ArticlesModel article : Objects.requireNonNull(getArticlelivedatas().getValue())) {
            if (article.getPrix() > 0 ){
                filteredliste.add(article);
            }
        }
        articleAdapterlivedatas.setValue(filteredliste);
    }

    //utiliser pour remplir les spinner avec les articles
    public MutableLiveData<ArrayList<ArticlesModel>> getLesArticleInstocklivedatas2() {
        ArrayList<ArticlesModel> filteredliste = new ArrayList<>();
        MutableLiveData<ArrayList<ArticlesModel>> Mfilteredliste = new MutableLiveData<>();
        for (ArticlesModel articleModel : Objects.requireNonNull(getArticlelivedatas().getValue())) {
            if (articleModel.getQuantite() > 0 ){
                filteredliste.add(articleModel);
            }
        }
        Mfilteredliste.setValue(filteredliste);
        return Mfilteredliste;
    }

    //utiliser pour afficher un credit ou un account avec les articles
    //articles sans immages
    public MutableLiveData<ArrayList<ArticlesModel>> getLesArticleInstocklivedatascredit() {
        ArrayList<ArticlesModel> filteredliste = new ArrayList<>();
        MutableLiveData<ArrayList<ArticlesModel>> Mfilteredliste = new MutableLiveData<>();
        for (ArticlesModel articleModel : Objects.requireNonNull(getArticlelivedatascredit().getValue())) {
            if (articleModel.getId() != null){
                if (articleModel.getQuantite() > 0 ){
                    filteredliste.add(articleModel);
                }
            }
        }
        Mfilteredliste.setValue(filteredliste);
        return Mfilteredliste;
    }


    public void getArticleOutStocklivedatas() {
        ArrayList<ArticlesModel> filteredliste = new ArrayList<>();
        for (ArticlesModel article : Objects.requireNonNull(getArticlelivedatas().getValue())) {
            if (article.getQuantite() == 0 ){
                filteredliste.add(article);
            }
        }
        articleAdapterlivedatas.setValue(filteredliste);
    }

    public void getArticlesInStocklivedatas() {
        ArrayList<ArticlesModel> filteredliste = new ArrayList<>();
        for (ArticlesModel article : Objects.requireNonNull(getArticlelivedatas().getValue())) {
            if (article.getQuantite() > 0 && article.getPrix() > 0){
                filteredliste.add(article);
            }
        }
        articleAdapterlivedatas.setValue(filteredliste);
    }
    public MutableLiveData<ArrayList<ArticlesModel>> getArticleAdapterlivedatas() {
        return articleAdapterlivedatas;
    }
}