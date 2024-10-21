package com.jay.easygest.vue;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.jay.easygest.R;
import com.jay.easygest.controleur.Usercontrolleur;
import com.jay.easygest.databinding.ActivityInitMdpBinding;
import com.jay.easygest.model.AppKessModel;
import com.jay.easygest.model.UserModel;
import com.jay.easygest.outils.AccessLocalAppKes;

public class InitMdpActivity extends AppCompatActivity {
    private ActivityInitMdpBinding binding;
    private Usercontrolleur usercontrolleur;
    private AccessLocalAppKes accessLocalAppKes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        accessLocalAppKes = new AccessLocalAppKes(this);
        usercontrolleur = Usercontrolleur.getUsercontrolleurInstance(this);
        binding = ActivityInitMdpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initMdp();
    }

    private void initMdp(){

        binding.btnInitMdp.setOnClickListener(view -> {

            String username = binding.editTextInitMdpUsername.getText().toString().trim();
            String owner = binding.editTextInitMdpOwner.getText().toString().trim();
            String email = binding.editTextInitMdpOwnerMail.getText().toString().trim();

            UserModel utilisateur = usercontrolleur.recupProprietaire();
            AppKessModel appli_data = accessLocalAppKes.getAppkes();
            if (username.isEmpty() || owner.isEmpty() || email.isEmpty()){
                Toast.makeText(InitMdpActivity.this, "champs obligatoires", Toast.LENGTH_SHORT).show();
            }else {
                if (utilisateur.getUsername().equals(username) && appli_data.getOwner().equals(owner) && appli_data.getAdresseelectro().equals(email))
                {
                   afficherMdpAlertebefor(utilisateur);

                }else {
                    Toast.makeText(InitMdpActivity.this, "informations incorrecttes", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    private void afficherMdpAlertebefor(UserModel utilisateur) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("reinitialisation du mot de passe ");
        builder.setMessage("en cliquant sur OK "+"\n"
                + "votre mot de passe sera reinitialisé");

        builder.setPositiveButton("OK", (dialog, which) -> {
            usercontrolleur.initMpdp(utilisateur);
            afficherMdpAlerte();
        });

        builder.setNegativeButton("annuller",(dialog, which) -> {

        });

        builder.create().show();
    }

    private void afficherMdpAlerte() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("mot de passe reinitialisé");
        builder.setMessage("votre mot de passe a été reinitialisé, il est fortement recommender de le noter." +
                "vous pouver le changer en accedant à modifier mot de passe dans le menu." +
                "ceci est une alerte elle diparaitra lorsque vous aurez cliquer sur ok." +
                "mot de passe : "+usercontrolleur.getProprietaireMdpInit()+"");

        builder.setPositiveButton("ok", (dialog, which) -> {
            Intent intent = new Intent(InitMdpActivity.this,MainActivity.class);
            startActivity(intent);
        });

        builder.create().show();
    }


}