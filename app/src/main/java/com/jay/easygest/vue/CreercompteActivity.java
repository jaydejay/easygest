package com.jay.easygest.vue;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteConstraintException;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.jay.easygest.controleur.Usercontrolleur;
import com.jay.easygest.databinding.ActivityCreercompteBinding;
import com.jay.easygest.model.AppKessModel;
import com.jay.easygest.outils.AccessLocalAppKes;
import com.jay.easygest.outils.PasswordHascher;
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


            String owner = Objects.requireNonNull(binding.editcreerCompteOwner.getText()).toString().trim();
            String code_base = Objects.requireNonNull(binding.editcreerCompteBaseCode.getText()).toString().trim();
            String telephone = Objects.requireNonNull(binding.editcreerCompteTelephone.getText()).toString().trim();
            String email = Objects.requireNonNull(binding.editcreerCompteMail.getText()).toString().trim();


            if (username.isEmpty() || password.isEmpty() || repassword.isEmpty()){
                Toast.makeText(CreercompteActivity.this, "champs sont obligatoires", Toast.LENGTH_SHORT).show();
                binding.btncreercompte.setEnabled(true);

            } else if (owner.isEmpty()) {
                binding.lleditcreerCompteOwner.setError("obligatoire");
                binding.btncreercompte.setEnabled(true);

            }else if (owner.length() < 5 ) {
                binding.lleditcreerCompteOwner.setError("5 minimum");
                binding.btncreercompte.setEnabled(true);

            }else if (owner.length() > 50 ) {
                binding.lleditcreerCompteOwner.setError("16 maximum");
                binding.btncreercompte.setEnabled(true);

            }else if (code_base.isEmpty()){
                binding.lleditcreerCompteBaseCode.setError("obligatoire");
                binding.btncreercompte.setEnabled(true);
            } else if (code_base.length() != 4 ) {
                binding.lleditcreerCompteBaseCode.setError("4 lettres attendus ");
                binding.btncreercompte.setEnabled(true);

            }else if (telephone.isEmpty()){
                binding.llcreerCompteTelephone.setError("obligatoire");
                binding.btncreercompte.setEnabled(true);

            } else if (telephone.length() != 10) {
                binding.llcreerCompteTelephone.setError("10 caracteres");
                binding.btncreercompte.setEnabled(true);

            }else if (email.isEmpty()){
                binding.llcreerCompteMail.setError("obligatoire");
                binding.btncreercompte.setEnabled(true);
            } else{
                if (username.length() >= 6 && password.length() >= 8 && repassword.length() >= 8){

                    if (repassword.equals(password)){
                        try {

                            int nbrutilisateur = usercontrolleur.nbrUtilisateur();
                            if (nbrutilisateur < 3){
                                AccessLocalAppKes accessLocalAppKes = new AccessLocalAppKes(CreercompteActivity.this);
                                AppKessModel appKessModel = accessLocalAppKes.getAppkes();
                                String _password = passwordHascher.getHashingPass(password,VariablesStatique.MY_SALT);
                               boolean success = usercontrolleur.creerUser(username, _password,appKessModel, owner,code_base,telephone,email);
                               if (success){
                                   editor.putString(VariablesStatique.SETTING_SHARED_PREF_VARIABLE,_password).commit();
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
                        Toast.makeText(CreercompteActivity.this, "mot de passes différents", Toast.LENGTH_SHORT).show();}
                        binding.btncreercompte.setEnabled(true);

                }else{
                    Toast.makeText(CreercompteActivity.this, "username ou mot de passe trop court", Toast.LENGTH_SHORT).show();
                    binding.btncreercompte.setEnabled(true);
                }
            }

        });
    }

    public void desactivatebtncreation(){
        int nbrutilisateur = usercontrolleur.nbrUtilisateur();
        if(nbrutilisateur >=3){
            binding.btncreercompte.setVisibility(View.GONE);
//            binding.btncreercompte.setEnabled(false);
        }
    }

    public void afficherMesage(){
        String msg = getIntent().getExtras().get("msgactivation").toString();
        if ( msg.length() != 0){
            Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
        }
    }


}