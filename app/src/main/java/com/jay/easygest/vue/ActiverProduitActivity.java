package com.jay.easygest.vue;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.jay.easygest.R;
import com.jay.easygest.controleur.Usercontrolleur;
import com.jay.easygest.databinding.ActivityActiverProduitBinding;
import com.jay.easygest.model.AppKessModel;
import com.jay.easygest.model.UserModel;
import com.jay.easygest.outils.AccessLocalAppKes;
import com.jay.easygest.outils.MesOutils;
import com.jay.easygest.outils.SessionManagement;

public class ActiverProduitActivity extends AppCompatActivity {
    private ActivityActiverProduitBinding binding;
    private String[] credentials;
    private Usercontrolleur usercontrolleur;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        credentials = (String[]) getIntent().getExtras().get("credentials");
        binding = ActivityActiverProduitBinding.inflate(getLayoutInflater());
        usercontrolleur = Usercontrolleur.getUsercontrolleurInstance(this);
        setContentView(binding.getRoot());
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        init();
        activerproduit();
    }

    private void init(){
        int  code_msg =  getIntent().getExtras().getInt("code_msg");
        String msg_expire = "votre licence a expirer contacter le proprietaire pour la renouveller";
        String activation_texte = "ACTIVATION DU PRODUIT";
        if (code_msg == 1 ){
            binding.txtMsgExpired.setText(msg_expire);
        }else {
            binding.txtMsgExpired.setText(activation_texte);
            binding.txtMsgExpired.setTextColor(getColor(R.color.bleue_brillant) );
        }

        binding.txtCompteFree.setVisibility(View.GONE);

    }

    public void activerproduit(){
        binding.btnactiverproduit.setOnClickListener(v -> {
            binding.btnactiverproduit.setEnabled(false);
           try {
               String proprietaire = binding.editactiverproduitAppowner.getText().toString().trim();
               String cleproduit = binding.editactiverproduitAppKey.getText().toString().trim();
               String applinumber = binding.editactiverproduitAppnuber.getText().toString().trim();

               String appnumber = credentials[0];
               String apppowner = credentials[1];

               if (!proprietaire.isEmpty() && !cleproduit.isEmpty() && !applinumber.isEmpty()) {
                   if (apppowner.equals(proprietaire)) {
                       if (appnumber.equals(applinumber)){
                           if (MesOutils.isKeyvalide(cleproduit,appnumber) ){
                               boolean success = isupdatekeySuccessfull(cleproduit);
                               if (success){
                                   SessionManagement sessionManagement = new SessionManagement(this);
                                   sessionManagement.savekeyActivated(true);
                                   UserModel user = usercontrolleur.recupProprietaire();
                                   Intent intent = getIntent(user);
                                   startActivity(intent);
                                   finish();

                               }else {
                                   Toast.makeText(ActiverProduitActivity.this, "echec de l'activation", Toast.LENGTH_SHORT).show();
                                   binding.btnactiverproduit.setEnabled(true);
                               }
                           }else {
                               Toast.makeText(ActiverProduitActivity.this, "cle incorrecte", Toast.LENGTH_SHORT).show();
                               binding.btnactiverproduit.setEnabled(true);
                           }
                       }else {
                           Toast.makeText(ActiverProduitActivity.this, "numero app incorrect", Toast.LENGTH_SHORT).show();
                           binding.btnactiverproduit.setEnabled(true);
                       }

                   } else {
                       Toast.makeText(ActiverProduitActivity.this, "propritaire incorrect", Toast.LENGTH_SHORT).show();
                       binding.btnactiverproduit.setEnabled(true);
                   }
               } else {
                   Toast.makeText(ActiverProduitActivity.this, "champs obligatoire", Toast.LENGTH_SHORT).show();
                   binding.btnactiverproduit.setEnabled(true);
               }
           }catch (Exception e){
               Toast.makeText(ActiverProduitActivity.this, "un probleme est survenu si cela persiste contacter l'editeur", Toast.LENGTH_LONG).show();
               binding.btnactiverproduit.setEnabled(true);
           }
        });
    }

    @NonNull
    private Intent getIntent(UserModel user) {
        Intent intent;
        if (user != null){
            intent = new Intent(ActiverProduitActivity.this, MainActivity.class);
            intent.putExtra("msgactivation","félicitation licence activée");
        }else {
            intent = new Intent(ActiverProduitActivity.this, AgenceActivity.class);
            intent.putExtra("msgactivation","félicitation licence activée");
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        return intent;
    }

    private boolean isupdatekeySuccessfull(String cleproduit) {
        AppKessModel appKess_Model = usercontrolleur.getAppCredentials2();
        AppKessModel appKessModel = new AppKessModel(
                appKess_Model.getAppnumber(),
                cleproduit,
                appKess_Model.getOwner(),
                appKess_Model.getBasecode(),
                appKess_Model.getTelephone(),
                appKess_Model.getAdresseelectro());
        AccessLocalAppKes accessLocalAppKes = new AccessLocalAppKes(this);
        return accessLocalAppKes.updateAppkesKey(appKessModel);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}