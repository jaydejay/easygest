package com.jay.easygest.vue;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.jay.easygest.R;
import com.jay.easygest.controleur.Accountcontroller;
import com.jay.easygest.controleur.Clientcontrolleur;
import com.jay.easygest.controleur.Creditcontrolleur;
import com.jay.easygest.controleur.Versementacccontrolleur;
import com.jay.easygest.controleur.Versementcontrolleur;
import com.jay.easygest.databinding.ActivityAfficherversementaccBinding;
import com.jay.easygest.model.AccountModel;
import com.jay.easygest.model.ClientModel;
import com.jay.easygest.model.CreditModel;
import com.jay.easygest.model.VersementsaccModel;
import com.jay.easygest.outils.MesOutils;
import com.jay.easygest.vue.ui.account.AccountViewModel;
import com.jay.easygest.vue.ui.clients.ClientViewModel;
import com.jay.easygest.vue.ui.credit.CreditViewModel;
import com.jay.easygest.vue.ui.versement.VersementViewModel;
import com.jay.easygest.vue.ui.versementacc.VersementaccViewModel;

import java.util.Date;

public class AfficherversementaccActivity extends AppCompatActivity {

    private ActivityAfficherversementaccBinding binding;
    private Versementacccontrolleur versementacccontrolleur;
    private Accountcontroller accountcontroller;
    private Clientcontrolleur clientcontrolleur;
    private AccountViewModel accountViewModel;
    private ClientViewModel clientViewModel;
    private VersementsaccModel versement;
    private VersementaccViewModel versementaccViewModel;
    private int position_versement;
    private int nbr_versement;
    private AccountModel account;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAfficherversementaccBinding.inflate(getLayoutInflater());
        versementacccontrolleur = Versementacccontrolleur.getVersementacccontrolleurInstance(this);
        accountcontroller = Accountcontroller.getAccountcontrolleurInstance(this);
        clientcontrolleur = Clientcontrolleur.getClientcontrolleurInstance(this);

        versementaccViewModel = new ViewModelProvider(this).get(VersementaccViewModel.class);
        accountViewModel = new ViewModelProvider(this).get(AccountViewModel.class);
        clientViewModel = new ViewModelProvider(this).get(ClientViewModel.class);

        setContentView(binding.getRoot());
//        account = accountViewModel.getAccount().getValue();

        versement = versementaccViewModel.getMversementacc().getValue();
        account = versement.getAccount();
        position_versement = getIntent().getIntExtra("versementaccposition",0);
        nbr_versement = getIntent().getIntExtra("nbrversementacc",1);
        setContentView(binding.getRoot());
        afficherVersement();
        annullerversement();
        modifierVersement();
        desactiverModifAndCancel();
        redirectToClient();
        redirectTolisteCredits();

    }

    public void afficherVersement(){

        ClientModel client = versement.getClient();
        String texte1 = client.getNom() + " " + client.getPrenoms()+" "+client.getCodeclient() ;
        String date = "Date : "+ MesOutils.convertDateToString(new Date(versement.getDateversement()));
        String texte2 = "Somme : "+versement.getSommeverse();
        String texte3 = "Account numero : " + versement.getAccount().getNumeroaccount();

        binding.textViewAfVersmaccClient.setText(texte1);
        binding.textViewAfVersmaccDate.setText(date);
        binding.textViewAfVersmaccSomme.setText(texte2);
        binding.textViewAfVersmaccNumeroCredit.setText(texte3);

        binding.afVersmaccToCredits.setText("liste credits");
        binding.afVersmaccToClient.setText("le client");

        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setTitle("versement account infos");
        }

    }
    private void desactiverModifAndCancel(){
        if (position_versement != nbr_versement - 1){
            binding.afficheVersaccModifButton.setVisibility(View.GONE);
            binding.afficheVersaccCancelButton.setVisibility(View.GONE);
        }
    }

    public void modifierVersement(){
        binding.afficheVersaccModifButton.setOnClickListener(v->{

            Intent intent = new Intent(this, ModifierVersementaccActivity.class);
            startActivity(intent);

        });

    }

    public void annullerversement(){
        binding.afficheVersaccCancelButton.setOnClickListener(v->{
            boolean success =  versementacccontrolleur.annullerversement(versement,account);
            if (success){
                Intent intent = new Intent(this,GestionActivity.class);
                startActivity(intent);
            }

        });

    }


    public void redirectTolisteCredits(){
        binding.afVersmaccToCredits.setOnClickListener(view -> {

            Intent intent = new Intent(this, GestionActivity.class);
            startActivity(intent);
        });
    }


    public void redirectToClient(){
        binding.afVersmaccToClient.setOnClickListener(view -> {
            ClientModel clientModel = clientcontrolleur.recupererClient(versement.getClient().getId());
            clientViewModel.getClient().setValue(clientModel);
            Intent intent = new Intent(this, AfficherclientActivity.class);
            startActivity(intent);
        });
    }
}