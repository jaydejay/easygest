package com.jay.easygest.vue;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;

import com.jay.easygest.R;
import com.jay.easygest.databinding.ActivityAfficherAccountBinding;
import com.jay.easygest.model.AccountModel;
import com.jay.easygest.model.Articles;
import com.jay.easygest.vue.ui.account.AccountViewModel;
import com.owlike.genson.Genson;

public class AfficherAccountActivity extends AppCompatActivity {

    private ActivityAfficherAccountBinding binding;
    private AccountViewModel accountViewModel;
    private  AccountModel account;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAfficherAccountBinding.inflate(getLayoutInflater());
        accountViewModel = new ViewModelProvider(this).get(AccountViewModel.class);
        account = accountViewModel.getAccount().getValue();
        afficheraccount();
        setContentView(binding.getRoot());

    }

    public void afficheraccount(){

        Articles c_article1 = new Genson().deserialize(account.getArticle1(), Articles.class);
        Articles c_article2 = new Genson().deserialize(account.getArticle2(), Articles.class);
        String article1 = "ARTICLE 1  "+c_article1.getDesignation() +"\n "+" quantite : "+c_article1.getNbrarticle()+"\n "+"somme : "+c_article1.getSomme();
        String article2 = "ARTICLE 2  "+c_article2.getDesignation() +"\n "+" quantite : "+c_article2.getNbrarticle()+"\n "+"somme : "+c_article2.getSomme();
        String account1 = "ACCOUNT: "+account.getSommeaccount();
        String versement ="VERSEMENT : "+account.getVersement();
        String reste ="RESTE : "+account.getReste();
        binding.cardafficheraccounttitle.setText(account.toString3());
        binding.cardafficheraccountarticle1.setText(article1);
        binding.cardafficheraccountarticle2.setText(article2);
        binding.cardafficheraccountaccount.setText(account1);
        binding.cardafficheraccountreste.setText(reste);
        binding.cardafficheraccountverement.setText(versement);
    }


}