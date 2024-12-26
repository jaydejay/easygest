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
            binding.btnactiverproduit.setEnabled(false);
           try {
               String proprietaire = binding.editactiverproduitAppowner.getText().toString().trim();
               String cleproduit = binding.editactiverproduitAppKey.getText().toString().trim();
               String apppowner = getIntent().getExtras().get(APPPOWNER).toString().trim();
               String apppkey = getIntent().getExtras().get(APPPKEY).toString().trim();

               if (proprietaire.length() != 0 && cleproduit.length() != 0) {
                   if (apppowner.equals(proprietaire) && apppkey.equals(cleproduit)) {
                       Intent intent = new Intent(ActiverProduitActivity.this, CreercompteActivity.class);
                       intent.putExtra("msgactivation","félicitation produit activé");
                       intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                       startActivity(intent);
                       finish();
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