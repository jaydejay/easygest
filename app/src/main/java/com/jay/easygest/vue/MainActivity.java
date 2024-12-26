package com.jay.easygest.vue;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import com.jay.easygest.outils.SessionManagement;
import com.jay.easygest.outils.SmsreSender;

import java.util.ArrayList;
import java.util.Objects;


public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private SessionManagement sessionManagement;
    private Usercontrolleur usercontrolleur;
    private UserModel user;
    //    private  AppKessModel appKessModel;
//    private PasswordHascher passwordHascher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sessionManagement = new SessionManagement(this);
        boolean is_authenticated = sessionManagement.getSession();
        if (is_authenticated){
            Intent intent = new Intent(MainActivity.this, GestionActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        SmsSendercontrolleur smsSendercontrolleur = SmsSendercontrolleur.getSmsSendercotrolleurInstance(this);
        this.usercontrolleur = Usercontrolleur.getUsercontrolleurInstance(this);
        SmsreSender smsreSender = new SmsreSender(this, this);
        ArrayList<SmsnoSentModel> sms_no_Sents = smsSendercontrolleur.getSmsnoSentList();
        if (sms_no_Sents.size()>0){
            smsreSender.sendingUnSentMsg(sms_no_Sents);}

        setContentView(binding.getRoot());
        init();
        authentification();
        redirectToAppActivation();
        redirectToInitMdp();

    }

    private void init() {
        fillTxtVConnectionError();
        desactivatetxtCreation();
        desactiverbtnAuthInit();
        parametres();

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
                            UserModel user = usercontrolleur.getUser();
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
     * affiche les donnees pour activer le produit
     */
    private void activerProduit() {
        String[] appcredentials = usercontrolleur.getAppCredentials();
        String apppkey = appcredentials[1];
        String apppowner = appcredentials[2];

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("cle d'activation");
            builder.setMessage("les donnees d'activations sont necessaires pour l'activation de votre produit, il est fortement recommendé de les noter." +"\n"
                                +"appli key : " + apppkey + "\n"
                                +"appli owner : " + apppowner );

            builder.setPositiveButton("ok", (dialog, which) -> {
                Intent intent = new Intent(this, ActiverProduitActivity.class);
                intent.putExtra("apppowner", apppowner);
                intent.putExtra("apppkey", apppkey);
                startActivity(intent);
            });
            builder.create().show();
    }

    /**
     * redirection pour activer le produit
     */
    private void redirectToAppActivation() {
        binding.txtCreateCompte.setOnClickListener(view -> activerProduit());

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
                    binding.textVConnection.setTextColor(getResources().getColor(R.color.red));
                    binding.textVConnection.setTextSize(12);
                }

                binding.textVConnection.setText(msg_texte);
            }
        }catch (Exception e){
            Log.d("mainactivity", "fillTxtVConnectionError: erreur");
        }

    }


    /**
     * desavtive la creation de compte utilisateur
     * le nbr d'utilisateur est limité a 3
     */
    private void desactivatetxtCreation(){
        int nbrutilisateur = usercontrolleur.nbrUtilisateur();
        if (nbrutilisateur >= 3){
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
                binding.btnauth.setVisibility(View.GONE);
                binding.txtParametres.setVisibility(View.VISIBLE);
                fillTxtVConnectionError();
            }
        }

    }

//    /**
//     * desactive le bouton d'authentification d'un administrateur
//     * @param userModel l'administrateur
//     */
//    private void desactiverbtnAuthAdmine(UserModel userModel){
//        int cmpteur = incrementCompteur(userModel);
//        if (cmpteur >= 3){
//            initChamp();
//            usercontrolleur.desactiverAdministrateur();
//            binding.txtParametres.setVisibility(View.VISIBLE);
////            binding.mainlayoutadmine.setVisibility(View.GONE);
//            fillTxtVConnestionErrorAdmin();
//        }
//
//    }



//    /**
//     * desactive le bouton authentification pour un administrateur
//     */
//    private void desactiverbtnAuthAdmineInit(){
//        try {
//            UserModel userModel = usercontrolleur.recupAdministrateur();
//            if (userModel.getCompteur() >= 3){
//                binding.txtParametres.setVisibility(View.VISIBLE);
////               binding.mainlayoutadmine.setVisibility(View.GONE);
//            }
//        }catch (Exception e){
//            //do nothing
//        }
//
//    }

    /**
     * desactive le bouton authentification pour un utilisateur au demarrage
     */
    private void desactiverbtnAuthInit(){
        try {
            UserModel userModel = usercontrolleur.recupProprietaire();
            if (userModel.getCompteur() >= 3){
                binding.btnauth.setVisibility(View.GONE);
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
        int compteur = userModel.getCompteur() ;
        try {
            compteur = compteur + 1;
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