package com.jay.easygest.vue;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.jay.easygest.R;
import com.jay.easygest.controleur.Creditcontrolleur;
import com.jay.easygest.databinding.ActivityAffichercreditBinding;
import com.jay.easygest.model.Articles;
import com.jay.easygest.model.ClientModel;
import com.jay.easygest.model.CreditModel;
import com.jay.easygest.vue.ui.clients.ClientViewModel;
import com.jay.easygest.vue.ui.credit.CreditViewModel;
import com.owlike.genson.Genson;

public class AffichercreditActivity extends AppCompatActivity {

   private Creditcontrolleur creditcontrolleur;
    TextView cardaffichercredittitle;
    TextView cardaffichercreditarticle1;
    TextView cardaffichercreditarticle2;
    TextView cardaffichercreditcredit;
    TextView cardaffichercreditversement;
    TextView cardaffichercreditreste;
    private  CreditModel credit;
    private ActivityAffichercreditBinding binding;
    private ClientViewModel clientViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAffichercreditBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        creditcontrolleur = Creditcontrolleur.getCreditcontrolleurInstance(this);


        CreditViewModel creditViewModel = new ViewModelProvider(this).get(CreditViewModel.class);
        clientViewModel = new ViewModelProvider(this).get(ClientViewModel.class);

        credit = creditViewModel.getCredit().getValue();
        creditcontrolleur.listecredits();
        cardaffichercredittitle = findViewById(R.id.cardaffichercredittitle);
        cardaffichercreditarticle1 = findViewById(R.id.cardaffichercreditarticle1);
        cardaffichercreditarticle2 = findViewById(R.id.cardaffichercreditarticle2);
        cardaffichercreditcredit = findViewById(R.id.cardaffichercreditcredit);
        cardaffichercreditreste = findViewById(R.id.cardaffichercreditreste);
        cardaffichercreditversement = findViewById(R.id.cardaffichercreditverement);

        affichercredit();
        annullerCredit();
        redirectModifierCreditActivity();
        redirectListeCredits();
        redirectAfficherClient();
    }


    public void affichercredit(){

        Articles c_article1 = new Genson().deserialize(credit.getArticle1(), Articles.class);
        Articles c_article2 = new Genson().deserialize(credit.getArticle2(), Articles.class);
        String article1 = "ARTICLE 1  "+c_article1.getDesignation() +"\n "+" quantite : "+c_article1.getNbrarticle()+"\n "+"somme : "+c_article1.getSomme();
        String article2 = "ARTICLE 2  "+c_article2.getDesignation() +"\n "+" quantite : "+c_article2.getNbrarticle()+"\n "+"somme : "+c_article2.getSomme();
        String credt = "CREDIT : "+credit.getSommecredit();
        String versement ="VERSEMENT : "+credit.getVersement();
        String reste ="RESTE : "+credit.getReste();
        cardaffichercredittitle.setText(credit.toString3());
        cardaffichercreditarticle1.setText(article1);
        cardaffichercreditarticle2.setText(article2);
        cardaffichercreditcredit.setText(credt);
        cardaffichercreditreste.setText(reste);
        cardaffichercreditversement.setText(versement);
    }

    public void redirectModifierCreditActivity(){

        binding.modifierCredit.setOnClickListener(v -> {

            ClientModel client = this.credit.getClient();
            this.clientViewModel.getClient().setValue(client);
            Intent intent = new Intent(AffichercreditActivity.this, ModifiercreditActivity.class);

            startActivity(intent);

        });

    }

    public void redirectListeCredits(){

        binding.recapListecredits.setOnClickListener(v -> {

            Intent intent = new Intent(AffichercreditActivity.this, GestionActivity.class);
            startActivity(intent);

        });

    }

    public void redirectAfficherClient(){

        binding.recapClient.setOnClickListener(v -> {
            ClientModel client = this.credit.getClient();
            this.clientViewModel.getClient().setValue(client);
            Intent intent = new Intent(AffichercreditActivity.this, AfficherclientActivity.class);
            startActivity(intent);

        });

    }
    public void annullerCredit(){

        binding.supCredit.setOnClickListener(view -> {

            if (credit.getReste() > 0) {

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("anuller un credit");
                builder.setMessage("êtes vous sûre de vouloir annuller le credit"+"\n"
                        +"tous les versements associés seront également annullés"+"\n"
                        +"l'annullation d'un credit est soumise à une pénalité allant de 1000 F à 10%"
                        +"de la somme du credit");

                builder.setPositiveButton("oui", (dialog, which) -> {
                   boolean success = creditcontrolleur.annullerCredit(credit);
                   if (success){
                       Intent intent = new Intent(AffichercreditActivity.this, GestionActivity.class);
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