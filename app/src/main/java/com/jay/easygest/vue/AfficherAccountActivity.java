package com.jay.easygest.vue;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;

import com.jay.easygest.R;
import com.jay.easygest.controleur.Accountcontroller;
import com.jay.easygest.controleur.Clientcontrolleur;
import com.jay.easygest.databinding.ActivityAfficherAccountBinding;
import com.jay.easygest.model.AccountModel;
import com.jay.easygest.model.Articles;
import com.jay.easygest.model.ClientModel;
import com.jay.easygest.vue.ui.account.AccountViewModel;
import com.jay.easygest.vue.ui.clients.ClientViewModel;
import com.owlike.genson.Genson;

public class AfficherAccountActivity extends AppCompatActivity {

    private ActivityAfficherAccountBinding binding;
    private Accountcontroller accountcontroller;
    private Clientcontrolleur clientcontrolleur;
    private AccountViewModel accountViewModel;
    private ClientViewModel clientViewModel;
    private  AccountModel account;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAfficherAccountBinding.inflate(getLayoutInflater());

        accountcontroller = Accountcontroller.getAccountcontrolleurInstance(this);
        clientcontrolleur = Clientcontrolleur.getClientcontrolleurInstance(this);

        accountViewModel = new ViewModelProvider(this).get(AccountViewModel.class);
        clientViewModel = new ViewModelProvider(this).get(ClientViewModel.class);
        account = accountViewModel.getAccount().getValue();

        afficheraccount();
        redirectToModifierAccount();
        annullerAccount();
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
        binding.cardafficheraccountarticle2 .setText(article2);
        binding.cardafficheraccountaccount.setText(account1);
        binding.cardafficheraccountreste.setText(reste);
        binding.cardafficheraccountverement.setText(versement);
    }


    public void redirectToModifierAccount(){
        binding.modifierAccount.setOnClickListener(view -> {

            Intent intent = new Intent(this,ModifierAccountActivity.class);
            startActivity(intent);
        });
    }

    public void annullerAccount(){

        binding.supAccount.setOnClickListener(view -> {

            if (account.getReste() > 0) {

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("anuller un account");
                builder.setMessage("êtes vous sûre de vouloir annuller l'account"+"\n"
                        +"tous les versements associés seront également annullés"+"\n"
                        +"l'annullation d'un account est soumise à une pénalité allant de 1000 F à 10%"
                        +"de la somme de l'account");

                builder.setPositiveButton("oui", (dialog, which) -> {
                    boolean success = accountcontroller.annullerAccount(account);
                    if (success){
                        ClientModel clientModel = clientcontrolleur.recupererClient(account.getClient().getId());
                        clientViewModel.getClient().setValue(clientModel);
                        Intent intent = new Intent(AfficherAccountActivity.this, AfficherclientActivity.class);
                        startActivity(intent);
                    }

                });

                builder.setNegativeButton("non", (dialog, which) -> {

                });

                builder.create().show();

            }

        });


    }


}