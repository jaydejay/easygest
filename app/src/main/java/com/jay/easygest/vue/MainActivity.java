package com.jay.easygest.vue;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.jay.easygest.R;
import com.jay.easygest.controleur.Usercontrolleur;
import com.jay.easygest.databinding.ActivityMainBinding;
import com.jay.easygest.model.AppKessModel;
import com.jay.easygest.model.UserModel;
import com.jay.easygest.outils.SessionManagement;

import java.util.Objects;


public class MainActivity extends AppCompatActivity {

    public static final String CODE_MSG = "code_msg";
    private ActivityMainBinding binding;
    private SessionManagement sessionManagement;
    private Usercontrolleur usercontrolleur;
    private UserModel user;

   private  AlertDialog.Builder builder ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.usercontrolleur = Usercontrolleur.getUsercontrolleurInstance(this);
        user = usercontrolleur.getUser();
//        appcredentials = usercontrolleur.getAppCredentials();
        sessionManagement = new SessionManagement(this);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        builder = new AlertDialog.Builder(this);
        setContentView(binding.getRoot());

        if (getIntent().getExtras() != null && getIntent().getExtras().getString("msgactivation") != null){
            Toast.makeText(this, getIntent().getExtras().getString("msgactivation"), Toast.LENGTH_LONG).show();
        }

        boolean  is_key_activated = sessionManagement.getkeyActivated();

            if (is_key_activated){
                    boolean is_agence_created = sessionManagement.getAgenceCreated();
                    if (is_agence_created){
                        boolean  is_utilisateur_created = sessionManagement.getUtilisateurCreated();
                        if (is_utilisateur_created){
                            boolean is_authenticated = sessionManagement.getSession();
                            if (is_authenticated){
                                Intent intent = new Intent(MainActivity.this, GestionActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();
                            }
                        }else {
                            Intent intent = new Intent(MainActivity.this, CreercompteActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        }
                    }else {
                        Intent intent = new Intent(MainActivity.this, AgenceActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    }
            }
    }

    @Override
    protected void onStart() {
        super.onStart();
        init();
        authentification();
        redirectToAppActivation();
        redirectToInitMdp();
    }

    private void init() {
        fillTxtVConnectionError();
        deactivationsTextCreation();
        desactiverbtnAuthInit();
        parametres();
        hideInitMdpText();
    }

    /**
     * Masque le bouton d'initialisation du mdp
     */
    private void hideInitMdpText() {

        try {
            UserModel userModel = usercontrolleur.recupProprietaire();
            if (userModel == null || userModel.getCompteur() >= 3){
                binding.txtMainMdpOublie.setVisibility(View.GONE);
            }
        }catch (Exception e){
            //do nothing
        }

    }

    /**
     * permet de s'authentifier
     */
    private void authentification() {

        binding.btnauth.setOnClickListener(view -> {
            binding.btnauth.setEnabled(false);
            try {
                String username = Objects.requireNonNull(binding.editTextUsername.getText()).toString().trim();
                String password = Objects.requireNonNull(binding.editTextTextPassword.getText()).toString().trim();
                user = usercontrolleur.recupProprietaire();
                if (username.isEmpty() || password.isEmpty()) {
                    Toast.makeText(MainActivity.this, "champs obligatoires", Toast.LENGTH_SHORT).show();
                    binding.btnauth.setEnabled(true);

                } else {
                    if (username.length() >= 6 && password.length() >= 8) {

                        if (usercontrolleur.isAuthenticated(username, password)) {
                            UserModel userModel = new UserModel(user.getId(), user.getUsername(), user.getPassword(), user.getDateInscription(), user.getStatus(), user.isActif(), 0);
                            usercontrolleur.modifierUser(userModel);
                            usercontrolleur.setUser(user);
                            sessionManagement.saveSession(true);
                            Intent intent = new Intent(MainActivity.this, GestionActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        } else {
                            binding.btnauth.setEnabled(true);
                            Toast.makeText(MainActivity.this, "username ou mot de passe incorrecte", Toast.LENGTH_SHORT).show();
                            desactiverbtnAuth(usercontrolleur.getUser());
                        }
                    } else {
                        binding.btnauth.setEnabled(true);
                        Toast.makeText(MainActivity.this, "username ou mot de passe trop court", Toast.LENGTH_SHORT).show();
                        desactiverbtnAuth(usercontrolleur.getUser());
                    }

                }
            } catch (Exception e) {
                binding.btnauth.setEnabled(true);
                Toast.makeText(MainActivity.this, "compte inexistant creer un compte", Toast.LENGTH_SHORT).show();
            }

        });
    }



    /**
     * permet d"acceder au fonctionalites pour deblquer
     * l"application suite a un blocage
     */
    private void parametres() {
        binding.txtParametres.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ParametresActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }

    /**
     * Redirection pour activer le produit
     */
    private void redirectToAppActivation() {
        binding.txtCreateCompte.setOnClickListener(view -> activerProduit());

    }


    /**
     * Afficher les donnees pour activer le produit
     */
    private void activerProduit() {
        try {
           AppKessModel appKessModel = usercontrolleur.getAppCredentials2();
            String apppnumber = String.valueOf(appKessModel.getAppnumber());
            String apppowner = String.valueOf(appKessModel.getOwner());
            String[] credentials = new String[]{apppnumber,apppowner};

            builder.setTitle("cle d'activation");
            builder.setMessage("les donnees d'activations sont necessaires pour l'activation de votre produit, il est fortement recommendé de les noter." +"\n"
                    +"appli number : " + apppnumber + "\n"
                    +"appli owner : " + apppowner );

            builder.setPositiveButton("ok", (dialog, which) -> {
                Intent intent = new Intent(this, ActiverProduitActivity.class);
                intent.putExtra("credentials", credentials);
                intent.putExtra(CODE_MSG,2);
                startActivity(intent);
            });

            if (!isFinishing() && !isDestroyed()) {
                builder.create().show();
            }

        }catch (Exception e){
            Toast.makeText(this, "activation interrompue", Toast.LENGTH_SHORT).show();
        }

    }

    /**
     * Affichager du texte d'erreur
     * apres 3 tentatives de connection
     */
    public void fillTxtVConnectionError(){
        try {
            user = usercontrolleur.recupProprietaire();
            if (user != null){
                String msg_texte= "connection";
                if (user.getCompteur() >= 3){
                    msg_texte = "débloquer votre compte"+"\n"
                            +"vous êtes ici parce que"+"\n"
                            + "vous avez fait 3 tentatives de fausse connection"+"\n"
                            +"acceder aux paramettres pour"+"\n"
                            +"debloquer votre compte utilisateur";
                    binding.textVConnection.setTextColor(getColor(R.color.red));
                    binding.textVConnection.setTextSize(12);
                }

                binding.textVConnection.setText(msg_texte);
            }
        }catch (Exception e){
            Toast.makeText(this, "une erreur est survenu", Toast.LENGTH_SHORT).show();
        }

    }


    /**
     * desavtive la creation de compte utilisateur
     * le nbr d'utilisateur est limité a 1 par application
     */
    private void deactivationsTextCreation(){
        if (sessionManagement.getkeyActivated() || sessionManagement.getAgenceCreated() || sessionManagement.getUtilisateurCreated() ){
            binding.txtCreateCompte.setVisibility(View.GONE);
        }
    }


    /**
     * Desactive le bouton d'authentification increment le compteur
     * @param userModel l'utilisateur
     */
    private void desactiverbtnAuth(UserModel userModel){
        if (userModel != null){
            int cmpteur = incrementCompteur(userModel);
            if (cmpteur >= 3){
                usercontrolleur.desactiverProprietaire();
                initChamp();
                init();
            }
        }

    }


    /**
     * desactive le bouton authentification pour un utilisateur au demarrage
     * s'il realise 3 tentatives de connection infructueuses
     */
    private void desactiverbtnAuthInit(){
        try {
            UserModel userModel = usercontrolleur.recupProprietaire();
            if (userModel.getCompteur() >= 3){
                binding.btnauth.setVisibility(View.GONE);
                binding.txtMainMdpOublie.setVisibility(View.GONE);
                binding.txtParametres.setVisibility(View.VISIBLE);
                fillTxtVConnectionError();
            }
        }catch (Exception e){
            //do nothing
        }

    }

    /**
     *
     * @param userModel l'utilisateur qui se connecte
     * @return le nbr de tentative de connection
     */
    private Integer incrementCompteur( UserModel userModel){
        int compteur = userModel.getCompteur() + 1;
        try {
//            compteur = compteur ;
           UserModel user = new UserModel(userModel.getId(),userModel.getUsername(),userModel.getPassword(),userModel.getDateInscription(),userModel.getStatus(),userModel.isActif(),compteur);
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



    public void redirectToInitMdp(){
        binding.txtMainMdpOublie.setOnClickListener(view -> {
            Intent intent = new Intent(this, InitMdpActivity.class);
            startActivity(intent);
        });

    }



    @Override
    protected void onResume() {
        super.onResume();
    }


    @Override
    protected void onPause() {

        super.onPause();

    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {

        binding = null;
        super.onDestroy();

    }



}