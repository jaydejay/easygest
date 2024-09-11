package com.jay.easygest.vue;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.jay.easygest.controleur.Clientcontrolleur;
import com.jay.easygest.controleur.Creditcontrolleur;
import com.jay.easygest.databinding.ActivityAjouterCreditBinding;
import com.jay.easygest.model.Articles;
import com.jay.easygest.model.ClientModel;
import com.jay.easygest.model.CreditModel;
import com.jay.easygest.outils.MesOutils;
import com.jay.easygest.vue.ui.clients.ClientViewModel;
import com.jay.easygest.vue.ui.credit.CreditViewModel;

import java.util.Date;
import java.util.Objects;

public class AjouterCreditActivity extends AppCompatActivity {

    private ActivityAjouterCreditBinding binding;
    private ClientViewModel clientViewModel;
    private Clientcontrolleur clientcontrolleur;
    private Creditcontrolleur creditcontroller;
    private CreditViewModel creditViewModel;
    private ClientModel client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAjouterCreditBinding.inflate(getLayoutInflater());

        clientViewModel = new ViewModelProvider(this).get(ClientViewModel.class);
        clientcontrolleur = Clientcontrolleur.getClientcontrolleurInstance(this);
        creditcontroller = Creditcontrolleur.getCreditcontrolleurInstance(this);

        creditViewModel = new ViewModelProvider(this).get(CreditViewModel.class);

        client = clientViewModel.getClient().getValue();
//        CreditModel credit = creditViewModel.getCredit().getValue();
        init();
        ajouterCredit();
        setContentView(binding.getRoot());
    }

    public  void init(){
        binding.ajoutcrednom.setText(client.getNom());
        binding.ajoutcredcodeclt.setText(client.getCodeclient());
        binding.ajoutcredprenoms.setText(client.getPrenoms());
    }

    public void  ajouterCredit(){

        binding.btnajoutcredit.setOnClickListener(view -> {
            String designationarticle1 = binding.ajoutcredarticle1.getText().toString().trim();
            String article1somme = binding.ajoutcredarticle1somme.getText().toString().trim();
            String article1qte = binding.ajoutcredNbrarticle1.getText().toString().trim();
            String date = binding.ajoutcredDate.getText().toString().trim();
            String versement = binding.ajoutcredversement.getText().toString().trim();
            Date date_account = MesOutils.convertStringToDate(date);

            if ( designationarticle1.isEmpty() || article1somme.isEmpty() ||article1qte.isEmpty()
                    || versement.isEmpty() ||date.isEmpty() )
            {
                Toast.makeText(AjouterCreditActivity.this, "remplissez les champs obligatoires", Toast.LENGTH_SHORT).show();

            } else if (date_account == null) {
                Toast.makeText(AjouterCreditActivity.this, "format de date incorrect", Toast.LENGTH_SHORT).show();
            }else if (binding.ajoutcredarticle2.getText().toString().trim().length() != 0 && binding.ajoutcredarticle2somme.getText().toString().trim().isEmpty() ||
                    binding.ajoutcredarticle2.getText().toString().trim().length() != 0 & binding.ajoutcredNbrarticle2.getText().toString().trim().isEmpty()) {
                Toast.makeText(AjouterCreditActivity.this, "renseigner le nombre ou le prix du deuxieme article", Toast.LENGTH_SHORT).show();
            } else {
                int sommearticle1 =Integer.parseInt(article1somme) ;
                int nbrarticle1 = Integer.parseInt(article1qte);

                String designation_article2 ;
                String designationarticle2 ;
                int sommearticle2 ;
                int nbrarticle2 ;
                String somme_article2 ;
                String nbr_article2 ;

                if (binding.ajoutcredarticle2.getText().toString().trim().length() != 0 && binding.ajoutcredarticle2somme.getText().toString().trim().equals("0") ||
                        binding.ajoutcredarticle2.getText().toString().trim().length() != 0 & binding.ajoutcredNbrarticle2.getText().toString().trim().equals("0")){
                    designation_article2 = "";
                    somme_article2 = "0";
                    nbr_article2 = "0";
                }else if (binding.ajoutcredarticle2.getText().toString().trim().isEmpty()){
                    designation_article2 = binding.ajoutcredarticle2.getText().toString().trim();
                    somme_article2 = "0";
                    nbr_article2 = "0";
                }else {
                    designation_article2 = binding.ajoutcredarticle2.getText().toString().trim();
                    somme_article2 = binding.ajoutcredarticle2somme.getText().toString().trim();
                    nbr_article2 = binding.ajoutcredNbrarticle2.getText().toString().trim();
                }
                designationarticle2 = designation_article2;
                sommearticle2 = Integer.parseInt(somme_article2);
                nbrarticle2 = Integer.parseInt(nbr_article2);
                long dateaccount = date_account.getTime();

                Articles c_article1 = new Articles(designationarticle1, sommearticle1,nbrarticle1);
                Articles c_article2 =  new Articles(designationarticle2, sommearticle2,nbrarticle2);
                int sommecredit  = c_article1.getSomme() + c_article2.getSomme();
                if (Integer.parseInt(versement) < sommecredit){
                    boolean success = creditcontroller.ajouterCredit( client,c_article1,c_article2,versement, dateaccount);
                    if (success) {
                        ClientModel clientModel = clientcontrolleur.recupererClient(client.getId());
                        CreditModel credit_ajoute = creditViewModel.getCredit().getValue();
                        CreditModel creditModel = new CreditModel(Objects.requireNonNull(credit_ajoute).getId(),clientModel,credit_ajoute.getArticle1(),credit_ajoute.getArticle2(),credit_ajoute.getSommecredit(),credit_ajoute.getVersement(),credit_ajoute.getReste(),credit_ajoute.getDatecredit(),credit_ajoute.getNumerocredit());
                        creditViewModel.getCredit().setValue(creditModel);
                        clientViewModel.getClient().setValue(clientModel);
                        Intent intent = new Intent(AjouterCreditActivity.this, AffichercreditActivity.class);
                        startActivity(intent);
                        finish();
                    } else { Toast.makeText(this, "un probleme est survenu : ajout avortée", Toast.LENGTH_SHORT).show();}
                }else { Toast.makeText(this, "versement superieur ou egal au credit", Toast.LENGTH_SHORT).show();}

            }
        });
    }
}