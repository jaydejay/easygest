package com.jay.easygest.vue;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.jay.easygest.R;
import com.jay.easygest.controleur.Creditcontrolleur;
import com.jay.easygest.controleur.Versementcontrolleur;
import com.jay.easygest.databinding.ActivityModifierVersementBinding;
import com.jay.easygest.model.ClientModel;
import com.jay.easygest.model.CreditModel;
import com.jay.easygest.model.VersementsModel;
import com.jay.easygest.outils.MesOutils;
import com.jay.easygest.vue.ui.credit.CreditViewModel;
import com.jay.easygest.vue.ui.versement.VersementViewModel;

import java.util.Date;
import java.util.Objects;

public class ModifierVersementActivity extends AppCompatActivity {
    Button bouton_modifier;
    Button boutonannuller;
    EditText EDTcodeclient ;
    EditText EDTsomme ;
    EditText EDTdatecredit;
    TextView title;
    Versementcontrolleur versementcontrolleur;
    VersementViewModel versementViewModel;
   private VersementsModel versement;
   private ClientModel client;
   private Creditcontrolleur creditcontrolleur;
   private CreditViewModel creditViewModel;
    private  ActivityModifierVersementBinding binding;
    private int versementNumber;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        versementcontrolleur = Versementcontrolleur.getVersementcontrolleurInstance(this);
        versementViewModel = new ViewModelProvider(this).get(VersementViewModel.class);
        creditcontrolleur = Creditcontrolleur.getCreditcontrolleurInstance(this);
        creditViewModel = new ViewModelProvider(this).get(CreditViewModel.class);
         binding = ActivityModifierVersementBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
         versement = versementViewModel.getMversement().getValue();
         client = versement.getClient();

         EDTcodeclient =  findViewById(R.id.editversementcodeclt);
         EDTsomme =  findViewById(R.id.editversementsomme);
         EDTdatecredit = findViewById(R.id.editversementdate);
         bouton_modifier =  findViewById(R.id.btnversement);
         title =  findViewById(R.id.txtmodifversementtitle);
         versementNumber = versementcontrolleur.getVersementNumber();
        init();
        modifierVersement();
//        annullerversement();

    }

    public void init(){

        String texte = client.getNom()+" "+client.getPrenoms()+" "+client.getCodeclient();
        title.setText(texte);
        EDTcodeclient.setVisibility(View.GONE);
        EDTsomme.setText(String.valueOf(versement.getSommeverse()));
        EDTdatecredit.setText(MesOutils.convertDateToString(new Date(versement.getDateversement())));
    }

    public void modifierVersement(){
        bouton_modifier.setOnClickListener(v -> {
            String edt_somme = EDTsomme.getText().toString().trim();
            String dateversement = EDTdatecredit.getText().toString().trim();
            Date date = MesOutils.convertStringToDate(dateversement);

            if ( edt_somme.isEmpty() || dateversement.isEmpty()){
                Toast.makeText(this, "champs obligatoires", Toast.LENGTH_SHORT).show();

            } else if (date == null) {
                Toast.makeText(ModifierVersementActivity.this, "format de date incorrect", Toast.LENGTH_SHORT).show();
            } else {
                if (Integer.parseInt(edt_somme) >= 1000) {

                    try {
//                        String dateversement = EDTdatecredit.getText().toString();
                        Integer nouvellesommeverse = Integer.parseInt(edt_somme);

                        CreditModel credit = creditViewModel.getCredit().getValue();
                            int somme_du_credit = credit.getSommecredit();

                            int anncien_total_versement = creditViewModel.getCredit().getValue().getVersement();
                            int annciennesommeverse = Integer.parseInt(String.valueOf(versement.getSommeverse())) ;
                            int nouveau_total_versement = (anncien_total_versement-annciennesommeverse)+nouvellesommeverse;

                            if (  nouvellesommeverse > 0 & nouveau_total_versement <= somme_du_credit){

                                boolean success = versementcontrolleur.modifierVersement(credit,versement,nouveau_total_versement,nouvellesommeverse,dateversement);
                                if (success) {
                                    Intent intent = new Intent(this, AfficheversementActivity.class);
//                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
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