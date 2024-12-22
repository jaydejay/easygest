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

import com.jay.easygest.controleur.Articlescontrolleur;
import com.jay.easygest.databinding.FragmentArticlesBinding;
import com.jay.easygest.model.ArticlesModel;
import com.jay.easygest.model.CreditModel;
import com.jay.easygest.vue.ArticlesActivity;
import com.jay.easygest.vue.ui.account.AccountViewModel;
import com.jay.easygest.vue.ui.listecredit.ListecreditAdapter;
import com.jay.easygest.vue.ui.listecredit.ListecreditFragment;

import java.util.ArrayList;

public class ArticlesFragment extends Fragment {
    private Articlescontrolleur articlescontrolleur;
    private ListeArticlesAdapter adapter;
    private ArticlesViewModel articlesViewModel;
    private ArrayList<ArticlesModel>  articles;
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
        articlesViewModel = new ViewModelProvider(this).get(ArticlesViewModel.class);
        creerliste();
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
            articlesViewModel.getArticlelivedatas().observe(getViewLifecycleOwner(),articlesModels -> {
                adapter = new ListeArticlesAdapter(getContext(),articlesModels);
                adapter.notifyDataSetChanged();
                binding.articleListView.setAdapter(adapter);
            });

        } catch (Exception e) {
            e.printStackTrace();
        }

    }




}