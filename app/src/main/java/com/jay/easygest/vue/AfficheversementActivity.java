package com.jay.easygest.vue;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.jay.easygest.controleur.Creditcontrolleur;
import com.jay.easygest.controleur.Versementcontrolleur;
import com.jay.easygest.databinding.ActivityAfficheversementBinding;
import com.jay.easygest.model.ClientModel;
import com.jay.easygest.model.CreditModel;
import com.jay.easygest.model.VersementsModel;
import com.jay.easygest.outils.MesOutils;
import com.jay.easygest.vue.ModifierVersementActivity;
import com.jay.easygest.vue.ui.credit.CreditViewModel;
import com.jay.easygest.vue.ui.versement.VersementViewModel;

import java.util.Date;

public class AfficheversementActivity extends AppCompatActivity {

    private ActivityAfficheversementBinding binding;
    private Versementcontrolleur versementcontrolleur;
    private VersementViewModel versementViewModel;
    private Creditcontrolleur creditcontrolleur;
    private CreditViewModel creditViewModel;
    private  VersementsModel versement;
    private CreditModel credit;
    private int versementNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAfficheversementBinding.inflate(getLayoutInflater());
        versementcontrolleur = Versementcontrolleur.getVersementcontrolleurInstance(this);
        versementViewModel = new ViewModelProvider(this).get(VersementViewModel.class);
        creditcontrolleur = Creditcontrolleur.getCreditcontrolleurInstance(this);
        creditViewModel = new ViewModelProvider(this).get(CreditViewModel.class);
        setContentView(binding.getRoot());
        credit = creditViewModel.getCredit().getValue();
         versement = versementViewModel.getMversement().getValue();
        afficherVersement();
        modifierVersement();
        annullerversement();

    }

    public void afficherVersement(){

        ClientModel client = versement.getClient();

        String texte1 = client.getNom() + " "+client.getPrenoms()+" "+client.getCodeclient() ;
        binding.textViewAfVersmClient.setText(texte1);
        binding.textViewAfVersmDate.setText(MesOutils.convertDateToString(new Date(versement.getDateversement())));
        binding.textViewAfVersmSomme.setText(String.valueOf(versement.getSommeverse()));
        binding.textViewAfVersmNumeroCredit.setText(String.valueOf(versement.getCredit().getNumerocredit()));
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setTitle("versement infos");
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



}