package com.jay.easygest.vue.ui.articles;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jay.easygest.controleur.Articlescontrolleur;
import com.jay.easygest.databinding.FragmentArticlesBinding;
import com.jay.easygest.model.ArticlesModel;
import com.jay.easygest.vue.ArticlesActivity;

import java.util.ArrayList;

public class ArticlesFragment extends Fragment {
    private ArticlesViewModel articlesViewModel;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private FragmentArticlesBinding binding;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentArticlesBinding.inflate(inflater,container,false);
        //    private ListeArticlesAdapter adapter;
        Articlescontrolleur articlescontrolleur = Articlescontrolleur.getArticlescontrolleurInstance(getContext());
        articlescontrolleur.listeArticles2();
        articlesViewModel = new ViewModelProvider(this).get(ArticlesViewModel.class);
        articlesViewModel.getArticleInstocklivedatas();

        recyclerView = binding.articleListView;
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

//        getAllArticle();
        creerliste();
        getArticleInStock();
        getArticleOutStock();
        rechercherArticle();
        redirectToArticleActivity();

        return binding.getRoot() ;
    }


 public void redirectToArticleActivity(){
        binding.btnRedirectToAricle.setOnClickListener(view -> {
            Intent intent = new Intent(getActivity(),ArticlesActivity.class);
           int itemid = binding.btnRedirectToAricle.getId();
            intent.putExtra("itemclickedId",itemid);
            startActivity(intent);
        });
 }

    public void creerliste(){
        try {
               articlesViewModel.getArticleAdapterlivedatas().observe(getViewLifecycleOwner(),articlesAdapterModels -> {
                   adapter = new RecycleViewArticleAdapter(getContext(),articlesAdapterModels);
                   adapter.notifyDataSetChanged();
                   recyclerView.setAdapter(adapter);
               });

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public  ArrayList<ArticlesModel> getFilter(String mtext){
        ArrayList<ArticlesModel> filteredliste = new ArrayList<>();
        try {

            articlesViewModel.getArticleAdapterlivedatas().observe(getViewLifecycleOwner(),articlesModels -> {
                for (ArticlesModel article : articlesModels) {
                    if (article.getId() != null){
                        if (article.getDesignation().contains(mtext) ){
                            filteredliste.add(article);
                        }
                    }

                }

            });
            return filteredliste;

        }catch (Exception e){
            return filteredliste;
        }


    }

    public void rechercherArticle(){

        binding.searchArticle.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                ArrayList<ArticlesModel> articles = ArticlesFragment.this.getFilter(newText);
                try {
                    adapter = new RecycleViewArticleAdapter(getContext(),articles);
                    adapter.notifyDataSetChanged();
                    recyclerView.setAdapter(adapter);
                }catch (Exception e){
                    return true;
                }
                return false;
            }
        });
    }

    public void getArticleInStock(){
        binding.btnAricleStock.setOnClickListener(view -> articlesViewModel.getArticleInstocklivedatas());
    }

    public void getArticleOutStock(){
        binding.btnListeAricle.setOnClickListener(view -> articlesViewModel.getArticleOutStocklivedatas());

    }

}