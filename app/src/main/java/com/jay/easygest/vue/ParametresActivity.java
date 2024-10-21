package com.jay.easygest.vue;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.jay.easygest.controleur.Usercontrolleur;
import com.jay.easygest.databinding.ActivityParametresBinding;
import com.jay.easygest.outils.SessionManagement;

public class  ParametresActivity extends AppCompatActivity {

   private ActivityParametresBinding binding;
   private Usercontrolleur usercontrolleur;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityParametresBinding.inflate(getLayoutInflater());
        usercontrolleur = Usercontrolleur.getUsercontrolleurInstance(this);
        setContentView(binding.getRoot());
        debloqueapp();

    }

    /**
     * permet de débloquer l'application apres un gele
     */
    public void debloqueapp(){

        binding.btnApikey.setOnClickListener(v -> {
            String proprietaire = binding.editAppowner.getText().toString().trim();
            String cleproduit = binding.editAppKey.getText().toString().trim();

            if (proprietaire.length() != 0 && cleproduit.length() != 0){
                if (usercontrolleur.authApp(proprietaire,cleproduit)){
                    afficherAlerte();
                }else { Toast.makeText(ParametresActivity.this, "produit non conforme", Toast.LENGTH_SHORT).show();}
            }else { Toast.makeText(ParametresActivity.this, "champs obligatoire", Toast.LENGTH_SHORT).show();}
        });
    }

    private void afficherAlerte() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("App débloqué");
        builder.setMessage("votre application vient d'être débloqué." );

        builder.setPositiveButton("ok", (dialog, which) -> {
            Intent intent = new Intent(ParametresActivity.this,MainActivity.class);
            startActivity(intent);
        });

        builder.create().show();
    }

//    private void afficherAlerte2() {
//
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setTitle("mot de passe reinitialisé");
//        builder.setMessage("votre mot de passe a été reinitialisé, il est fortement recommender de le noter." +
//                "vous pouver le changer en accedant à modifier mot de passe dans le menu." +
//                "ceci est une alerte elle diparaitra lorsque vous aurez cliquer sur ok." +
//                "mot de passe : "+usercontrolleur.getProprietaireMdpInit()+"");
//
//        builder.setPositiveButton("ok", (dialog, which) -> {
//            Intent intent = new Intent(ParametresActivity.this,MainActivity.class);
//            startActivity(intent);
//        });
//
//        builder.create().show();
//    }


}