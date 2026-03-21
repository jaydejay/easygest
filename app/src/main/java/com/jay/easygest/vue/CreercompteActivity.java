package com.jay.easygest.vue;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteConstraintException;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.jay.easygest.controleur.Usercontrolleur;
import com.jay.easygest.databinding.ActivityCreercompteBinding;
import com.jay.easygest.outils.MesOutils;
import com.jay.easygest.outils.PasswordHascher;
import com.jay.easygest.outils.SessionManagement;
import com.jay.easygest.outils.VariablesStatique;

import java.util.Objects;


public class CreercompteActivity extends AppCompatActivity {

    private SharedPreferences.Editor editor;
   private ActivityCreercompteBinding binding;
   private Usercontrolleur usercontrolleur;
   private PasswordHascher passwordHascher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sharedPreferences = this.getSharedPreferences(VariablesStatique.SETTING_SHARED_PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        binding = ActivityCreercompteBinding.inflate(getLayoutInflater());
        passwordHascher = new PasswordHascher();
        setContentView(binding.getRoot());
        usercontrolleur = Usercontrolleur.getUsercontrolleurInstance(this);
//        recupererIntent();
        init();
    }

    public void init(){
        creerCompte();
        afficherMesage();
        desactivatebtncreation();
    }

    public void creerCompte(){
        binding.btncreercompte.setOnClickListener(view -> {
            binding.btncreercompte.setEnabled(false);
            String username = Objects.requireNonNull(binding.txtcreercompteusername.getText()).toString().trim();
            String password = Objects.requireNonNull(binding.txtcreercomptepassword.getText()).toString().trim();
            String repassword = Objects.requireNonNull(binding.txtcreercompterepeat.getText()).toString().trim();

            if (username.isEmpty() || password.isEmpty() || repassword.isEmpty()){
                Toast.makeText(CreercompteActivity.this, "champs sont obligatoires", Toast.LENGTH_SHORT).show();
                binding.btncreercompte.setEnabled(true);
            }
            else{
                if (username.length() >= 6 && password.length() >= 8 && repassword.length() >= 8){
                    if (MesOutils.asDigit(password)){
                        if (repassword.equals(password)){
                            try {
                                int nbrutilisateur = usercontrolleur.nbrUtilisateur();
                                if (nbrutilisateur < 1){
                                    String _password = passwordHascher.getHashingPass(password,VariablesStatique.MY_SALT);
                                    boolean success = usercontrolleur.creerUser(username, _password);
                                    if (success){
                                        editor.putString(VariablesStatique.SETTING_SHARED_PREF_VARIABLE,_password).commit();
                                        SessionManagement sessionManagement = new SessionManagement(this);
                                        sessionManagement.saveUtilisateurCreated(true);
                                        Intent intent = new Intent(CreercompteActivity.this, MainActivity.class);
                                        startActivity(intent);
                                    }
                                }else {
                                    Toast.makeText(CreercompteActivity.this, "action non autorisée", Toast.LENGTH_SHORT).show();
                                    binding.btncreercompte.setEnabled(true);
                                }

                            }catch (SQLiteConstraintException e){
                                Toast.makeText(CreercompteActivity.this, "formulaire invalde", Toast.LENGTH_SHORT).show();
                                binding.btncreercompte.setEnabled(true);
                            }
                        }else {
                            Toast.makeText(CreercompteActivity.this, "mot de passes différents", Toast.LENGTH_SHORT).show();
                            binding.btncreercompte.setEnabled(true);
                        }

                    }else {
                        Toast.makeText(CreercompteActivity.this, "mot de passe doit contenir au moins un chiffre", Toast.LENGTH_SHORT).show();
                        binding.btncreercompte.setEnabled(true);
                    }

                }else{
                    Toast.makeText(CreercompteActivity.this, "username ou mot de passe trop court", Toast.LENGTH_SHORT).show();
                    binding.btncreercompte.setEnabled(true);
                }
            }

        });
    }

    public void desactivatebtncreation(){
        int nbrutilisateur = usercontrolleur.nbrUtilisateur();
        if(nbrutilisateur >=1){
            binding.btncreercompte.setVisibility(View.GONE);
        }
    }

    public void afficherMesage(){
        Intent intent = getIntent();
        if (intent.getExtras() != null){
            String msg = getIntent().getExtras().getString("msgactivation");
            if (msg != null && !msg.isEmpty()) {
                Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
            }
        }


    }


}