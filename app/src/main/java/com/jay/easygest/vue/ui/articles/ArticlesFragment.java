package com.jay.easygest.vue.ui.articles;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.jay.easygest.controleur.Articlescontrolleur;
import com.jay.easygest.databinding.FragmentArticlesBinding;
import com.jay.easygest.vue.ArticlesActivity;

public class ArticlesFragment extends Fragment {
    private Articlescontrolleur articlescontrolleur;
    private ListeArticlesAdapter adapter;
    private FragmentArticlesBinding binding;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentArticlesBinding.inflate(inflater,container,false);
        articlescontrolleur = Articlescontrolleur.getArticlescontrolleurInstance(getContext());
        articlescontrolleur.listeArticles();
        creerliste();
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
            articlescontrolleur.getMarticles().observe(getViewLifecycleOwner(),articlesModels -> {
                adapter = new ListeArticlesAdapter(getContext(),articlesModels);
                adapter.notifyDataSetChanged();
                binding.articleListView.setAdapter(adapter);
            });

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


}