package com.jay.easygest.vue;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.sqlite.SQLiteConstraintException;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.jay.easygest.controleur.Usercontrolleur;
import com.jay.easygest.databinding.ActivityCreercompteBinding;

import java.util.Objects;


public class CreercompteActivity extends AppCompatActivity {

   private ActivityCreercompteBinding binding;
   private Usercontrolleur usercontrolleur;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCreercompteBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        usercontrolleur = Usercontrolleur.getUsercontrolleurInstance(this);
        afficherMesage();
        creerCompte();
        desactivatebtncreation();
    }

    public void creerCompte(){

        binding.btncreercompte.setOnClickListener(view -> {

            if (binding.txtcreercompteusername.length() != 0 && binding.txtcreercomptepassword.length() != 0 && binding.txtcreercompterepeat.length() != 0){

                String username = Objects.requireNonNull(binding.txtcreercompteusername.getText()).toString();
                String password = Objects.requireNonNull(binding.txtcreercomptepassword.getText()).toString();
                String repassword = Objects.requireNonNull(binding.txtcreercompterepeat.getText()).toString();
                if (binding.txtcreercompteusername.length() >= 6 & binding.txtcreercomptepassword.length() >= 8 & binding.txtcreercompterepeat.length() >= 8){

                    if (repassword.equals(password)){
                        try {
                            int nbrutilisateur = usercontrolleur.nbrUtilisateur();
                            if (nbrutilisateur < 3){
                                usercontrolleur.creerUser(username, password);

                                Intent intent = new Intent(CreercompteActivity.this, MainActivity.class);
                                startActivity(intent);


                            }else {
                                Toast.makeText(CreercompteActivity.this, "action non autorisée", Toast.LENGTH_SHORT).show();
                            }

                        }catch (SQLiteConstraintException e){
                            Toast.makeText(CreercompteActivity.this, "formulaire invalde", Toast.LENGTH_SHORT).show();
                        }
                    }else {Toast.makeText(CreercompteActivity.this, "mot de passes différents", Toast.LENGTH_SHORT).show();}

                }else{ Toast.makeText(CreercompteActivity.this, "username ou mot de passe trop court", Toast.LENGTH_SHORT).show(); }
            }else{ Toast.makeText(CreercompteActivity.this, "champs sont obligatoires", Toast.LENGTH_SHORT).show(); }

        });
    }

    public void desactivatebtncreation(){
        int nbrutilisateur = usercontrolleur.nbrUtilisateur();
        if(nbrutilisateur >=3){
            binding.btncreercompte.setVisibility(View.INVISIBLE);
            binding.btncreercompte.setEnabled(false);
        }
    }

    public void afficherMesage(){
        String msg = getIntent().getExtras().get("msgactivation").toString();
        if ( msg.length() != 0){
            Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
        }
    }

}