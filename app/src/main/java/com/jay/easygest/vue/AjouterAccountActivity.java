package com.jay.easygest.vue;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.jay.easygest.controleur.Accountcontroller;
import com.jay.easygest.controleur.Clientcontrolleur;
import com.jay.easygest.databinding.ActivityAjouterAccountBinding;
import com.jay.easygest.model.AccountModel;
import com.jay.easygest.model.Articles;
import com.jay.easygest.model.ClientModel;
import com.jay.easygest.outils.MesOutils;
import com.jay.easygest.vue.ui.account.AccountViewModel;
import com.jay.easygest.vue.ui.clients.ClientViewModel;

import java.util.Date;
import java.util.Objects;

public class AjouterAccountActivity extends AppCompatActivity {

    private ActivityAjouterAccountBinding binding;
    private ClientViewModel clientViewModel;
    private AccountViewModel accountViewModel;
    private Accountcontroller accountcontroller;
    private Clientcontrolleur clientcontrolleur;
//    private AccountModel account;
    private ClientModel client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityAjouterAccountBinding.inflate(getLayoutInflater());
        clientViewModel = new ViewModelProvider(this).get(ClientViewModel.class);
        clientcontrolleur = Clientcontrolleur.getClientcontrolleurInstance(this);
        accountcontroller = Accountcontroller.getAccountcontrolleurInstance(this);
        accountViewModel = new ViewModelProvider(this).get(AccountViewModel.class);
         client = clientViewModel.getClient().getValue();
//         account = accountViewModel.getAccount().getValue();
         init();
         ajouterAccount();
        setContentView(binding.getRoot());

    }

    public  void init(){
        binding.ajoutaccnom.setText(client.getNom());
        binding.ajoutacccodeclt.setText(client.getCodeclient());
        binding.ajoutaccprenoms.setText(client.getPrenoms());
    }


    public void  ajouterAccount(){

        binding.btnajoutaccount.setOnClickListener(view -> {
            String designationarticle1 = binding.ajoutaccarticle1.getText().toString().trim();
            String article1somme = binding.ajoutaccarticle1somme.getText().toString().trim();
            String article1qte = binding.ajoutaccNbrarticle1.getText().toString().trim();
            String date = binding.ajoutaccDate.getText().toString().trim();
            String versement = binding.ajoutaccversement.getText().toString().trim();
            Date date_account = MesOutils.convertStringToDate(date);

            if ( designationarticle1.isEmpty() || article1somme.isEmpty() ||article1qte.isEmpty()
                    || versement.isEmpty() ||date.isEmpty() )
            {
                Toast.makeText(AjouterAccountActivity.this, "remplissez les champs obligatoires", Toast.LENGTH_SHORT).show();

            } else if (date_account == null) {

                Toast.makeText(AjouterAccountActivity.this, "format de date incorrect", Toast.LENGTH_SHORT).show();
            }else if (binding.ajoutaccarticle2.getText().toString().trim().length() != 0 && binding.ajoutaccarticle2somme.getText().toString().trim().isEmpty() ||
                    binding.ajoutaccarticle2.getText().toString().trim().length() != 0 & binding.ajoutaccNbrarticle2.getText().toString().trim().isEmpty()) {
                Toast.makeText(AjouterAccountActivity.this, "renseigner le nombre ou le prix du deuxieme article", Toast.LENGTH_SHORT).show();
            } else {
                int sommearticle1 =Integer.parseInt(article1somme) ;
                int nbrarticle1 = Integer.parseInt(article1qte);

                String designation_article2 ;
                String designationarticle2 ;
                int sommearticle2 ;
                int nbrarticle2 ;
                String somme_article2 ;
                String nbr_article2 ;

                if (binding.ajoutaccarticle2.getText().toString().trim().length() != 0 && binding.ajoutaccarticle2somme.getText().toString().trim().equals("0") ||
                        binding.ajoutaccarticle2.getText().toString().trim().length() != 0 & binding.ajoutaccNbrarticle2.getText().toString().trim().equals("0")){
                    designation_article2 = "";
                    somme_article2 = "0";
                    nbr_article2 = "0";
                }else if (binding.ajoutaccarticle2.getText().toString().trim().isEmpty()){
                    designation_article2 = binding.ajoutaccarticle2.getText().toString().trim();
                    somme_article2 = "0";
                    nbr_article2 = "0";
                }else {
                    designation_article2 = binding.ajoutaccarticle2.getText().toString().trim();
                    somme_article2 = binding.ajoutaccarticle2somme.getText().toString().trim();
                    nbr_article2 = binding.ajoutaccNbrarticle2.getText().toString().trim();
                }
                designationarticle2 = designation_article2;
                sommearticle2 = Integer.parseInt(somme_article2);
                nbrarticle2 = Integer.parseInt(nbr_article2);
                long dateaccount = date_account.getTime();

                Articles c_article1 = new Articles(designationarticle1, sommearticle1,nbrarticle1);
                Articles c_article2 =  new Articles(designationarticle2, sommearticle2,nbrarticle2);
                int sommecredit  = c_article1.getSomme() + c_article2.getSomme();
                if (Integer.parseInt(versement) < sommecredit){
                    boolean success = accountcontroller.ajouterAccount( client,c_article1,c_article2,versement, dateaccount);
                    if (success) {
                        ClientModel clientModel = clientcontrolleur.recupererClient(client.getId());
                        AccountModel account_ajoute = accountViewModel.getAccount().getValue();
                        AccountModel accountModel = new AccountModel(Objects.requireNonNull(account_ajoute).getId(),clientModel,account_ajoute.getArticle1(),account_ajoute.getArticle2(),account_ajoute.getSommeaccount(),account_ajoute.getVersement(),account_ajoute.getReste(),account_ajoute.getDateaccount(),account_ajoute.getNumeroaccount());
                        accountViewModel.getAccount().setValue(accountModel);
                        clientViewModel.getClient().setValue(clientModel);
                        Intent intent = new Intent(AjouterAccountActivity.this, AfficherAccountActivity.class);
                        startActivity(intent);
                        finish();
                    } else { Toast.makeText(this, "un probleme est survenu : ajout avortée", Toast.LENGTH_SHORT).show();}
                }else {Toast.makeText(this, "versement superieur ou égal à l'account", Toast.LENGTH_SHORT).show();}

            }
        });
    }


}