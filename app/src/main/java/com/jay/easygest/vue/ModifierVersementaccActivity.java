package com.jay.easygest.vue;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.jay.easygest.R;
import com.jay.easygest.controleur.Versementacccontrolleur;
import com.jay.easygest.databinding.ActivityModifierVersementaccBinding;
import com.jay.easygest.model.AccountModel;
import com.jay.easygest.model.ClientModel;
import com.jay.easygest.model.VersementsaccModel;
import com.jay.easygest.outils.MesOutils;
import com.jay.easygest.vue.ui.versementacc.VersementaccViewModel;

import java.util.Date;
import java.util.Objects;

public class ModifierVersementaccActivity extends AppCompatActivity {

    private ActivityModifierVersementaccBinding binding;
    private Versementacccontrolleur versementacccontrolleur;
    private VersementsaccModel versement;
    private ClientModel client;
   EditText EDTcodeclient;
   EditText EDTsomme;
   EditText EDTdateaccount;
   Button bouton_modifier;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityModifierVersementaccBinding.inflate(getLayoutInflater());
        versementacccontrolleur = Versementacccontrolleur.getVersementacccontrolleurInstance(this);
        VersementaccViewModel versementaccViewModel = new ViewModelProvider(this).get(VersementaccViewModel.class);
        setContentView(binding.getRoot());
        versement = versementaccViewModel.getMversementacc().getValue();
        client = Objects.requireNonNull(versement).getClient();


        EDTcodeclient =  findViewById(R.id.ajoutervrsmtacccodeclt);
        EDTsomme =  findViewById(R.id.ajoutervrsmntaccsomme);
        EDTdateaccount =  findViewById(R.id.ajoutervrsmntaccdate);
        bouton_modifier =  findViewById(R.id.btnversementacc);
//        versementNumber = versementacccontrolleur.getVersementNumber();
        init();
        modifierVersement();
        setContentView(binding.getRoot());
    }

    public void init(){

        String texte = client.getNom()+" "+client.getPrenoms()+" "+client.getCodeclient();
        binding.txtmodifversementacctitle.setText(texte);
        EDTcodeclient.setVisibility(View.GONE);
        EDTsomme.setText(String.valueOf(versement.getSommeverse()));
        EDTdateaccount.setText(MesOutils.convertDateToString(new Date(versement.getDateversement())));
    }

    public void modifierVersement(){
        bouton_modifier.setOnClickListener(v -> {
            String edt_somme = EDTsomme.getText().toString().trim();
            String dateversement = EDTdateaccount.getText().toString().trim();
            Date date = MesOutils.convertStringToDate(dateversement);

            if ( edt_somme.isEmpty() || dateversement.isEmpty()){
                Toast.makeText(this, "champs obligatoires", Toast.LENGTH_SHORT).show();

            } else if (date == null) {
                Toast.makeText(ModifierVersementaccActivity.this, "format de date incorrect", Toast.LENGTH_SHORT).show();
            } else {
                if (Integer.parseInt(edt_somme) >= 1000) {

                    try {
                        int nouvellesommeverse = Integer.parseInt(edt_somme);
                        AccountModel account = versement.getAccount();
                        int somme_du_account = account.getSommeaccount();
                        int anncien_total_versement = account.getVersement();
                        int annciennesommeverse = Integer.parseInt(String.valueOf(versement.getSommeverse())) ;
                        int nouveau_total_versement = (anncien_total_versement-annciennesommeverse)+nouvellesommeverse;

                        if (  nouvellesommeverse > 0 & nouveau_total_versement <= somme_du_account){

                            boolean success = versementacccontrolleur.modifierVersement(account,versement,nouveau_total_versement,nouvellesommeverse,dateversement);
                            if (success) {
                                Intent intent = new Intent(this, AfficherversementaccActivity.class);
                                startActivity(intent);
                            } else { Toast.makeText(this, "revoyez le versement ", Toast.LENGTH_SHORT).show();}
                        }else { Toast.makeText(this, "versement elevé", Toast.LENGTH_SHORT).show(); }

                    } catch (Exception e) {
                        Toast.makeText(this, "erreur versement avorté", Toast.LENGTH_SHORT).show();
                    }
                }else {Toast.makeText(this, "le verement doit etre de 1000 F minimum", Toast.LENGTH_SHORT).show();}

            }
        });
    }

}