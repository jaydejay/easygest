package com.jay.easygest.vue;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.jay.easygest.controleur.Clientcontrolleur;
import com.jay.easygest.controleur.Versementcontrolleur;
import com.jay.easygest.databinding.ActivityAfficheversementBinding;
import com.jay.easygest.model.ClientModel;
import com.jay.easygest.model.CreditModel;
import com.jay.easygest.model.VersementsModel;
import com.jay.easygest.outils.MesOutils;
import com.jay.easygest.vue.ui.clients.ClientViewModel;
import com.jay.easygest.vue.ui.credit.CreditViewModel;
import com.jay.easygest.vue.ui.versement.VersementViewModel;

import java.util.Date;

public class AfficheversementActivity extends AppCompatActivity {

    private ActivityAfficheversementBinding binding;
    private Versementcontrolleur versementcontrolleur;
    private Clientcontrolleur clientcontrolleur;
    private ClientViewModel clientViewModel;
    private  VersementsModel versement;
    private CreditModel credit;
    private int position_versement;
    private int nbr_versement;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAfficheversementBinding.inflate(getLayoutInflater());
        versementcontrolleur = Versementcontrolleur.getVersementcontrolleurInstance(this);
        VersementViewModel versementViewModel = new ViewModelProvider(this).get(VersementViewModel.class);
//        creditcontrolleur = Creditcontrolleur.getCreditcontrolleurInstance(this);
        clientcontrolleur =Clientcontrolleur.getClientcontrolleurInstance(this);
        CreditViewModel creditViewModel = new ViewModelProvider(this).get(CreditViewModel.class);
        clientViewModel = new ViewModelProvider(this).get(ClientViewModel.class);
        setContentView(binding.getRoot());
        credit = creditViewModel.getCredit().getValue();
        versement = versementViewModel.getMversement().getValue();
        position_versement = getIntent().getIntExtra("versementposition",0);
        nbr_versement = getIntent().getIntExtra("nbrversement",1);

        desactiverModifAndCancel();
        afficherVersement();
        modifierVersement();
        annullerversement();
        redirectTolisteCredits();
        redirectToClient();

    }

    public void afficherVersement(){

        ClientModel client = versement.getClient();
        String texte1 = client.getNom() + " " + client.getPrenoms()+" "+client.getCodeclient() ;
        String date = "date : "+ MesOutils.convertDateToString(new Date(versement.getDateversement()));
        String texte2 = "somme : "+versement.getSommeverse();
        String texte3 = "credit numero : " + versement.getCredit().getNumerocredit();

        binding.textViewAfVersmClient.setText(texte1);
        binding.textViewAfVersmDate.setText(date);
        binding.textViewAfVersmSomme.setText(texte2);
        binding.textViewAfVersmNumeroCredit.setText(texte3);

        binding.afVersmToCredits.setText("liste credits");
        binding.afVersmToClient.setText("le client");

        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setTitle("versement infos");
        }

    }
    private void desactiverModifAndCancel(){
        if (position_versement != nbr_versement - 1){
            binding.afficheVersModifButton.setVisibility(View.GONE);
            binding.afficheVersCancelButton.setVisibility(View.GONE);
        }
    }

    public void modifierVersement(){
        binding.afficheVersModifButton.setOnClickListener(v->{

         Intent intent = new Intent(this, ModifierVersementActivity.class);
         startActivity(intent);

        });

    }

    public void annullerversement(){
        binding.afficheVersCancelButton.setOnClickListener(v->{
            boolean success =  versementcontrolleur.annullerversement(versement,credit);
            if (success){
                Intent intent = new Intent(this,GestionActivity.class);
                startActivity(intent);
            }

        });

    }


    public void redirectTolisteCredits(){
        binding.afVersmToCredits.setOnClickListener(view -> {

            Intent intent = new Intent(this, GestionActivity.class);
            startActivity(intent);
        });
    }


    public void redirectToClient(){
        binding.afVersmToClient.setOnClickListener(view -> {
        ClientModel clientModel = clientcontrolleur.recupererClient(versement.getClient().getId());
        clientViewModel.getClient().setValue(clientModel);
        Intent intent = new Intent(this, AfficherclientActivity.class);
            startActivity(intent);
        });
    }



}