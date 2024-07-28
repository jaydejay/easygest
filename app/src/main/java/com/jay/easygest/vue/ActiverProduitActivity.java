package com.jay.easygest.vue;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.jay.easygest.databinding.ActivityActiverProduitBinding;

public class ActiverProduitActivity extends AppCompatActivity {

    public static final String APPPOWNER = "apppowner";
    public static final String APPPKEY = "apppkey";
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
           try {
               String proprietaire = binding.editactiverproduitAppowner.getText().toString();
               String cleproduit = binding.editactiverproduitAppKey.getText().toString();
               String apppowner = getIntent().getExtras().get(APPPOWNER).toString();
               String apppkey = getIntent().getExtras().get(APPPKEY).toString();

               if (proprietaire.length() != 0 && cleproduit.length() != 0) {
                   if (apppowner.equals(proprietaire) && apppkey.equals(cleproduit)) {
                       Intent intent = new Intent(ActiverProduitActivity.this, CreercompteActivity.class);
                       intent.putExtra("msgactivation","félicitation produit activé");
                       startActivity(intent);
                   } else {
                       Toast.makeText(ActiverProduitActivity.this, "produit non conforme", Toast.LENGTH_SHORT).show();
                   }
               } else {
                   Toast.makeText(ActiverProduitActivity.this, "champs obligatoire", Toast.LENGTH_SHORT).show();
               }
           }catch (Exception e){
               Toast.makeText(ActiverProduitActivity.this, "un probleme est survenu si cela persiste contacter l'editeur", Toast.LENGTH_LONG).show();
           }
        });
    }


}