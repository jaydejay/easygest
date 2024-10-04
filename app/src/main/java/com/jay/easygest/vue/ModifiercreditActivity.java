package com.jay.easygest.vue;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.jay.easygest.controleur.Clientcontrolleur;
import com.jay.easygest.controleur.Creditcontrolleur;
import com.jay.easygest.databinding.ActivityModifiercreditBinding;
import com.jay.easygest.model.Articles;
import com.jay.easygest.model.ClientModel;
import com.jay.easygest.model.CreditModel;
import com.jay.easygest.outils.MesOutils;
import com.jay.easygest.outils.SessionManagement;
import com.jay.easygest.vue.ui.clients.ClientViewModel;
import com.jay.easygest.vue.ui.credit.CreditViewModel;
import com.owlike.genson.Genson;

import java.util.Date;
import java.util.Objects;

public class ModifiercreditActivity extends AppCompatActivity {
    private Creditcontrolleur creditcontrolleur;
    private CreditViewModel creditViewModel;
    private Clientcontrolleur clientcontrolleur;
    private ClientViewModel clientViewModel;
    private ActivityModifiercreditBinding binding;
    private CreditModel credit;
    private ClientModel client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityModifiercreditBinding.inflate(getLayoutInflater());
        creditViewModel= new ViewModelProvider(this).get(CreditViewModel.class);
        clientViewModel= new ViewModelProvider(this).get(ClientViewModel.class);

        client = clientViewModel.getClient().getValue();
        credit = creditViewModel.getCredit().getValue();

        creditcontrolleur = Creditcontrolleur.getCreditcontrolleurInstance(this);
        clientcontrolleur = Clientcontrolleur.getClientcontrolleurInstance(this);
        afficherCredit();
        modifierCredit();
        setContentView(binding.getRoot());


    }

    public void afficherCredit(){
        Articles article1 = new Genson().deserialize(credit.getArticle1(), Articles.class);
        Articles article2 = new Genson().deserialize(credit.getArticle2(), Articles.class);
        binding.modifcredrnom.setText(client.getNom());
        binding.modifcredprenoms.setText(client.getPrenoms());
        binding.modifcredcodeclt.setText(client.getCodeclient());

        binding.modifcredarticle1.setText(article1.getDesignation());
        binding.modifcredarticle1somme.setText(String.valueOf(article1.getPrix()));
        binding.modifcredNbrarticle1.setText(String.valueOf(article1.getNbrarticle()));

        binding.modifaccarticle2.setText(article2.getDesignation());
        binding.modifcredarticle2somme.setText(String.valueOf(article2.getPrix()));
        binding.modifcredNbrarticle2.setText(String.valueOf(article2.getNbrarticle()));

        binding.modifcredDate.setText(MesOutils.convertDateToString(new Date(credit.getDatecredit())));
    }

    public void modifierCredit(){
        binding.btnmodifcredit.setOnClickListener(view -> {

            String designationarticle1 = binding.modifcredarticle1.getText().toString().trim();
            String article1somme = binding.modifcredarticle1somme.getText().toString().trim();
            String article1qte = binding.modifcredNbrarticle1.getText().toString().trim();
            String date = binding.modifcredDate.getText().toString().trim();
            Date date_credit = MesOutils.convertStringToDate(date);

            if ( designationarticle1.isEmpty() || article1somme.isEmpty() ||article1qte.isEmpty()
                    ||date.isEmpty() )
            {
                Toast.makeText(ModifiercreditActivity.this, "remplissez les champs obligatoires", Toast.LENGTH_SHORT).show();

            } else if (date_credit == null) {

                Toast.makeText(ModifiercreditActivity.this, "format de date incorrect", Toast.LENGTH_SHORT).show();
            }else if (binding.modifaccarticle2.getText().toString().trim().length() != 0 && binding.modifcredarticle2somme.getText().toString().trim().isEmpty() ||
                    binding.modifaccarticle2.getText().toString().trim().length() != 0 & binding.modifcredNbrarticle2.getText().toString().trim().isEmpty()) {
                Toast.makeText(ModifiercreditActivity.this, "renseigner le nombre ou le prix du deuxieme article", Toast.LENGTH_SHORT).show();
            } else {
                int sommearticle1 =Integer.parseInt(article1somme) ;
                int nbrarticle1 = Integer.parseInt(article1qte);

                String designation_article2 ;
                String designationarticle2 ;
                int sommearticle2 ;
                int nbrarticle2 ;
                String somme_article2 ;
                String nbr_article2 ;

                if (binding.modifaccarticle2.getText().toString().trim().length() != 0 && binding.modifcredarticle2somme.getText().toString().trim().equals("0") ||
                        binding.modifaccarticle2.getText().toString().trim().length() != 0 & binding.modifcredNbrarticle2.getText().toString().trim().equals("0")){
                    designation_article2 = "";
                    somme_article2 = "0";
                    nbr_article2 = "0";
                }else if (binding.modifaccarticle2.getText().toString().trim().isEmpty()){
                    designation_article2 = binding.modifaccarticle2.getText().toString().trim();
                    somme_article2 = "0";
                    nbr_article2 = "0";
                }else {
                    designation_article2 = binding.modifaccarticle2.getText().toString().trim();
                    somme_article2 = binding.modifcredarticle2somme.getText().toString().trim();
                    nbr_article2 = binding.modifcredNbrarticle2.getText().toString().trim();
                }
                designationarticle2 = designation_article2;
                sommearticle2 = Integer.parseInt(somme_article2);
                nbrarticle2 = Integer.parseInt(nbr_article2);
                long datecredit = date_credit.getTime();

                Articles c_article1 = new Articles(designationarticle1, sommearticle1,nbrarticle1);
                Articles c_article2 =  new Articles(designationarticle2, sommearticle2,nbrarticle2);
                int sommecredit = c_article1.getSomme() + c_article2.getSomme();

                int versement;

                if (credit.getVersement() < sommecredit){
                    versement = credit.getVersement();
                }else {
                    versement = sommecredit;
                }

                int reste = sommecredit - versement;

                String article1 = new Genson().serialize(c_article1);
                String article2 = new Genson().serialize(c_article2);

                CreditModel nouveau_credit = new CreditModel(credit.getId(),client.getId(),article1,article2,sommecredit,versement,reste,datecredit,credit.getNumerocredit());
                int ancienne_somme_credit = credit.getSommecredit();
                boolean success = creditcontrolleur.modifierCredit(nouveau_credit, client,ancienne_somme_credit);
                if (success) {
                    ClientModel clientModel = clientcontrolleur.recupererClient(client.getId());
                    CreditModel credit_modifier = creditViewModel.getCredit().getValue();
                    CreditModel creditModel = new CreditModel(Objects.requireNonNull(credit_modifier).getId(),clientModel,credit_modifier.getArticle1(),credit_modifier.getArticle2(),credit_modifier.getSommecredit(),credit_modifier.getVersement(),credit_modifier.getReste(),credit_modifier.getDatecredit(),credit_modifier.getNumerocredit());
                    creditViewModel.getCredit().setValue(creditModel);
                    clientViewModel.getClient().setValue(clientModel);
//                    SessionManagement sessionManagement = new SessionManagement(ModifiercreditActivity.this);
//                    sessionManagement.saveSession(true);
                    Intent intent = new Intent(ModifiercreditActivity.this, AffichercreditActivity.class);
                    startActivity(intent);
//                    finish();
                } else {
                    Toast.makeText(this, "un probleme est survenu : modification avortée", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


}