package com.jay.easygest.vue;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.jay.easygest.databinding.ActivityActiverProduitBinding;
import com.jay.easygest.model.AppKessModel;
import com.jay.easygest.outils.AccessLocalAppKes;
import com.jay.easygest.outils.MesOutils;

public class ActiverProduitActivity extends AppCompatActivity {

    public static final String APPPOWNER = "apppowner";
    public static final String APPPKEY = "apppkey";
    public static final String APPNUMBER = "apppnumber";
    private ActivityActiverProduitBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityActiverProduitBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        activerproduit();
    }

    public void activerproduit(){
        binding.btnactiverproduit.setOnClickListener(v -> {
            binding.btnactiverproduit.setEnabled(false);
           try {
               String proprietaire = binding.editactiverproduitAppowner.getText().toString().trim();
               String cleproduit = binding.editactiverproduitAppKey.getText().toString().trim();
//               String apppowner = getIntent().getExtras().get(APPPOWNER).toString().trim();
//               String appnumber = getIntent().getExtras().get(APPNUMBER).toString().trim();
               String[] appcredentials = (String[]) getIntent().getExtras().get("appcredentials");
               String appnumber = appcredentials[0];
               String apppowner = appcredentials[2];
              boolean is_appnuber_right = MesOutils.retrieveAppNumber(cleproduit,appnumber);

               if (proprietaire.length() != 0 && cleproduit.length() != 0) {
                   if (apppowner.equals(proprietaire) && is_appnuber_right) {
                       AppKessModel appKessModel = new AppKessModel(
                              Integer.parseInt(appcredentials[0]),
                              cleproduit,
                               appcredentials[2],
                               "",
                               appcredentials[3],
                               appcredentials[4]);
                       AccessLocalAppKes accessLocalAppKes = new AccessLocalAppKes(this);
                      boolean success =  accessLocalAppKes.updateAppkesKey(appKessModel);
                      if (success){
                          Intent intent = new Intent(ActiverProduitActivity.this, CreercompteActivity.class);
                          intent.putExtra("msgactivation","félicitation produit activé");
                          intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                          startActivity(intent);
                          finish();
                      }else {
                          Toast.makeText(ActiverProduitActivity.this, "un probleme est survenue", Toast.LENGTH_SHORT).show();
                          binding.btnactiverproduit.setEnabled(true);
                      }

                   } else {
                       Toast.makeText(ActiverProduitActivity.this, "produit non conforme", Toast.LENGTH_SHORT).show();
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


}