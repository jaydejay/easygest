package com.jay.easygest.vue;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.jay.easygest.R;
import com.jay.easygest.model.UserModel;
import com.jay.easygest.controleur.Usercontrolleur;
import com.jay.easygest.databinding.ActivityMainBinding;

import java.util.Objects;


public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private Usercontrolleur usercontrolleur;
    private Integer compteur;
    private UserModel user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        this.usercontrolleur = Usercontrolleur.getUsercontrolleurInstance(this);
        init();
        authentification();
        redirectToAppActivation();
        debloquerCompteProprietaire();
    }

    private void init() {
        fillTxtVConnestion();
        desactivatetxtCreation();
        desactiverbtnAuthInit();
        desactiverbtnAuthAdmineInit();
        parametres();


    }

    private void authentification() {

        binding.btnauth.setOnClickListener(view -> {
            try {
                String username = Objects.requireNonNull(binding.editTextUsername.getText()).toString().trim();
                String password = Objects.requireNonNull(binding.editTextTextPassword.getText()).toString().trim();

                if (username.isEmpty() || password.isEmpty()) {
                    Toast.makeText(MainActivity.this, "champs obligatoires", Toast.LENGTH_SHORT).show();

                } else {
                    if (username.length() >= 6 & password.length() >= 8) {

                        user = usercontrolleur.recupProprietaire();
                        if (usercontrolleur.isAuthenticated(username, password)) {
                            UserModel userModel = new UserModel(user.getId(), user.getUsername(), user.getPassword(), user.getDateInscription(), user.getStatus(), user.isActif(), 0);
                            usercontrolleur.modifierUser(userModel);
                            usercontrolleur.setUser(userModel);
                            Intent intent = new Intent(MainActivity.this, GestionActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();

                        } else {
                            Toast.makeText(MainActivity.this, "username ou mot de passe incorrecte", Toast.LENGTH_SHORT).show();
                            desactiverbtnAuth(user);
                        }
                    } else {
                        Toast.makeText(MainActivity.this, "username ou mot de passe trop court", Toast.LENGTH_SHORT).show();
                        desactiverbtnAuth(user);
                    }

                }
            } catch (Exception e) {
                Toast.makeText(MainActivity.this, "compte inexistant creer un compte", Toast.LENGTH_SHORT).show();
            }

        });
    }

    private void debloquerCompteProprietaire() {

        binding.btnmaindeloqueadmine.setOnClickListener(view -> {
            try {
                String username = Objects.requireNonNull(binding.editTextUsername.getText()).toString().trim();
                String password = Objects.requireNonNull(binding.editTextTextPassword.getText()).toString().trim();
                if ( username.isEmpty() || password.isEmpty()) {
                    Toast.makeText(MainActivity.this, "champs obligatoires", Toast.LENGTH_SHORT).show();
                } else {
                    if (username.length() >= 6 && password.length() >= 8) {

                        user = usercontrolleur.recupAdministrateur();
                        if (username.equals(user.getUsername()) && password.equals(user.getPassword())) {
                            usercontrolleur.activerProprietaire();
                            usercontrolleur.activerAdministrateur();
                            initChamp();
                            activerbtn(binding.btnauth);
                            binding.mainlayoutadmine.setVisibility(View.GONE);
                            afficherAlerte();
                        } else {
                            Toast.makeText(MainActivity.this, "username ou mot de passe incorrecte", Toast.LENGTH_SHORT).show();
                            desactiverbtnAuthAdmine(user);
                        }
                    } else {
                        Toast.makeText(MainActivity.this, "username ou mot de passe trop court", Toast.LENGTH_SHORT).show();
                        desactiverbtnAuthAdmine(user);
                    }
                }
            } catch (Exception e) {
                Toast.makeText(MainActivity.this, "un probleme d'integrité est survenu contacter votre administrateur", Toast.LENGTH_SHORT).show();
            }

        });
    }


    private void parametres() {
        binding.txtParametres.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ParametresActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }


    private void activerProduit() {
        String[] appcredentials = usercontrolleur.getAppCredentials();
        String apppkey = appcredentials[1];
        String apppowner = appcredentials[2];

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("cle d'activation");
            builder.setMessage("les donnees d'activations sont necessaires pour l'activation de votre produit, il est fortement recommendé de les noter." +"\n"
                                +"appli key : " + apppkey + "\n"+
                                "appli owner : " + apppowner );

            builder.setPositiveButton("ok", (dialog, which) -> {
                Intent intent = new Intent(this, ActiverProduitActivity.class);
                intent.putExtra("apppowner", apppowner);
                intent.putExtra("apppkey", apppkey);
                startActivity(intent);

            });

            builder.create().show();

    }

    private void redirectToAppActivation() {
        binding.txtCreateCompte.setOnClickListener(view -> activerProduit());

    }

    private void desactivatetxtCreation(){
        int nbrutilisateur = usercontrolleur.nbrUtilisateur();
        if (nbrutilisateur >= 3){
            binding.txtCreateCompte.setVisibility(View.GONE);
        }
    }

    private void activerbtn(Button btn){
        btn.setVisibility(View.VISIBLE);
        btn.setEnabled(true);
    }

    private void desactiverbtnAuthInit(){
        try {
            UserModel userModel = usercontrolleur.recupProprietaire();
            if (userModel.getCompteur()>=3){
                binding.btnauth.setVisibility(View.GONE);
                binding.mainlayoutadmine.setVisibility(View.VISIBLE);
            }
        }catch (Exception e){
            //do nothing
        }

    }

    private void desactiverbtnAuth(UserModel userModel){

        if (userModel != null){
            int cmpteur = incrementCompteur(userModel);
            if (cmpteur >= 3){
                usercontrolleur.desactiverProprietaire();
                initChamp();
//                fillTxtVConnestion();
                binding.btnauth.setVisibility(View.GONE);
                binding.mainlayoutadmine.setVisibility(View.VISIBLE);
            }
        }

    }

    public void fillTxtVConnestion(){
        user = usercontrolleur.recupProprietaire();
        if (user.getCompteur() >=3){
            String msg_texte = "débloquer votre compte"+"\n"
                    +"vous êtes ici parce que"+"\n"
                    + "vous avez fait 3 tentatives"+"\n"
                    +"de fausse connection"+"\n"
                    +"entrer l'username et le mot de passe administrateur"+"\n"
                    +"pour debloquer votre compte utilisateur";
            binding.textVConnection.setTextSize(12);
            binding.textVConnection.setTextColor(getResources().getColor(R.color.red));
            binding.textVConnection.setText(msg_texte);
        }


    }

    private void desactiverbtnAuthAdmineInit(){
        try {
            UserModel userModel = usercontrolleur.recupAdministrateur();
            if (userModel.getCompteur()>=3){
                binding.txtParametres.setVisibility(View.VISIBLE);
                binding.mainlayoutadmine.setVisibility(View.GONE);

            }
        }catch (Exception e){
            //do nothing
        }

    }

    private void desactiverbtnAuthAdmine(UserModel userModel){
        int cmpteur = incrementCompteur(userModel);
        if (cmpteur >= 3){
            usercontrolleur.desactiverAdministrateur();
            initChamp();
            binding.txtParametres.setVisibility(View.VISIBLE);
            binding.mainlayoutadmine.setVisibility(View.GONE);

        }

    }

    private Integer incrementCompteur( UserModel userModel){
        try {
            Integer compteur1 = userModel.getCompteur();
            compteur = compteur1 + 1;
            user = new UserModel(userModel.getId(),userModel.getUsername(),userModel.getPassword(),userModel.getDateInscription(),userModel.getStatus(),userModel.isActif(),compteur);
            usercontrolleur.modifierUser(user);
            usercontrolleur.setUser(user);
        }catch (Exception e){
            //do nothing
            }
        return compteur;

    }




    private void initChamp(){
        binding.editTextUsername.setText("");
        binding.editTextTextPassword.setText("");
    }


    private void afficherAlerte() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("mot de passe reinitialisé");
        builder.setMessage("votre mot de passe a été reinitialisé, il est fortement recommendé de le noter." +
                "vous pouver le changer en accedant à modifier mot de passe dans le menu." +
                "ceci est une alerte elle diparaitra lorsque vous aurez cliquer sur ok." +
                "mot de passe : "+usercontrolleur.getProprietaireMdpInit()+"");

        builder.setPositiveButton("ok", (dialog, which) -> {

        });

        builder.create().show();
    }


}