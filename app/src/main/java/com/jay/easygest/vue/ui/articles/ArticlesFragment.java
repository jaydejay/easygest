package com.jay.easygest.vue.ui.articles;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jay.easygest.R;
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
    private boolean showingInStock = true;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentArticlesBinding.inflate(inflater,container,false);
        Articlescontrolleur articlescontrolleur = Articlescontrolleur.getArticlescontrolleurInstance(getContext());
        articlescontrolleur.listeArticles2();
        articlescontrolleur.listeArticlescredit();
        articlesViewModel = new ViewModelProvider(this).get(ArticlesViewModel.class);
        articlesViewModel.getArticleslivedatas();

        recyclerView = binding.articleListView;
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        creerliste();
        getArticles();
        setupStockToggle();
        rechercherArticle();
        redirectToArticleActivity();
        return binding.getRoot() ;
    }

    private void setupStockToggle() {
        binding.btnArticleStockToggle.setOnClickListener(view -> {
            if (showingInStock) {
                articlesViewModel.getArticleOutStocklivedatas();
                binding.btnArticleStockToggle.setText(R.string.article_out_stock);
                binding.btnArticleStockToggle.setBackgroundTintList(android.content.res.ColorStateList.valueOf(getResources().getColor(android.R.color.holo_green_light)));
            } else {
                articlesViewModel.getArticlesInStocklivedatas();
                binding.btnArticleStockToggle.setText(R.string.article_en_stock);
                binding.btnArticleStockToggle.setBackgroundTintList(android.content.res.ColorStateList.valueOf(getResources().getColor(R.color.teal_200)));
            }
            showingInStock = !showingInStock;
        });
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
                   recyclerView.setAdapter(adapter);
               });
        } catch (Exception e) {
            Toast.makeText(getContext(), "un probleme rencontré", Toast.LENGTH_SHORT).show();
        }

    }


    public  ArrayList<ArticlesModel> getFilter(String mtext){
        ArrayList<ArticlesModel> filteredliste = new ArrayList<>();
        try {
            articlesViewModel.getArticleAdapterlivedatas().observe(getViewLifecycleOwner(),articlesModels -> {
                for (ArticlesModel article : articlesModels) {
                    if (article.getDesignation().contains(mtext) ){
                        filteredliste.add(article);
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

    /**
     * afficher tous les articles
     */
    public void getArticles(){
        binding.btnListeAricles.setOnClickListener(view -> articlesViewModel.getArticleslivedatas());
    }

    /**
     * afficher les articles en stock
     */
    public void getArticleInStock(){
        // Handled by setupStockToggle
    }

    /**
     * afficher les articles hors stock
     */
    public void getArticleOutStock(){
        // Handled by setupStockToggle
    }

    @Override
    public void onResume() {
        super.onResume();
        creerliste();
        setupStockToggle();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}