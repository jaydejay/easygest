package com.jay.easygest.vue;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.jay.easygest.R;
import com.jay.easygest.controleur.SmsSendercontrolleur;
import com.jay.easygest.controleur.Usercontrolleur;
import com.jay.easygest.databinding.ActivityMainBinding;
import com.jay.easygest.model.SmsnoSentModel;
import com.jay.easygest.model.UserModel;
import com.jay.easygest.outils.MesOutils;
import com.jay.easygest.outils.SessionManagement;
import com.jay.easygest.outils.SmsreSender;

import java.util.ArrayList;
import java.util.Objects;


public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private SessionManagement sessionManagement;
    private Usercontrolleur usercontrolleur;
    private UserModel user;
    private String[] appcredentials;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.usercontrolleur = Usercontrolleur.getUsercontrolleurInstance(this);
         user = usercontrolleur.getUser();
        appcredentials = usercontrolleur.getAppCredentials();

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        if (getIntent().getExtras() != null && getIntent().getExtras().getString("msgactivation") != null){
            Toast.makeText(this, getIntent().getExtras().getString("msgactivation"), Toast.LENGTH_LONG).show();
        }

        if (MesOutils.isLicenceExpired(appcredentials)){
            Intent intent = new Intent(MainActivity.this, ActiverProduitActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.putExtra("appcredentials",appcredentials);
            intent.putExtra("code_msg",1);
            startActivity(intent);
            finish();
        }else {
            sessionManagement = new SessionManagement(this);
            boolean is_authenticated = sessionManagement.getSession();
            if (is_authenticated){
                Intent intent = new Intent(MainActivity.this, GestionActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }

            SmsSendercontrolleur smsSendercontrolleur = SmsSendercontrolleur.getSmsSendercotrolleurInstance(this);

            SmsreSender smsreSender = new SmsreSender(this, this);
            ArrayList<SmsnoSentModel> sms_no_Sents = smsSendercontrolleur.getSmsnoSentList();
            if (sms_no_Sents.size() > 0){
                smsreSender.sendingUnSentMsg(sms_no_Sents);
            }


            init();
            authentification();
            redirectToAppActivation();
            redirectToInitMdp();
        }

    }

    private void init() {
        fillTxtVConnectionError();
        desactivatetxtCreation();
        desactiverbtnAuthInit();
        parametres();
        hideInitMdpText();
    }

    /**
     * hide le bouton d'initialisation du mdp
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
     * redirection pour activer le produit
     */
    private void redirectToAppActivation() {
        binding.txtCreateCompte.setOnClickListener(view -> activerProduit());

    }


    /**
     * affiche les donnees pour activer le produit
     */
    private void activerProduit() {
        try {

            String apppnumber = appcredentials[0];
            String apppowner = appcredentials[2];

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("cle d'activation");
            builder.setMessage("les donnees d'activations sont necessaires pour l'activation de votre produit, il est fortement recommendé de les noter." +"\n"
                    +"appli number : " + apppnumber + "\n"
                    +"appli owner : " + apppowner );

            builder.setPositiveButton("ok", (dialog, which) -> {
                Intent intent = new Intent(this, ActiverProduitActivity.class);
                intent.putExtra("appcredentials", appcredentials);
                intent.putExtra("code_msg",2);
                startActivity(intent);
            });
            builder.create().show();
        }catch (Exception e){
            Toast.makeText(this, "activation interrumpue", Toast.LENGTH_SHORT).show();
        }

    }




    /**
     * affichage du texte d'erreur
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
     * le nbr d'utilisateur est limité a 1
     */
    private void desactivatetxtCreation(){
        int nbrutilisateur = usercontrolleur.nbrUtilisateur();
        if (nbrutilisateur >= 1){
            binding.txtCreateCompte.setVisibility(View.GONE);
        }
    }


    /**
     * desactive le bouton d'authentification increment le compteur
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
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
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
        super.onDestroy();
        binding = null;
    }



}