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
import com.jay.easygest.controleur.SmsSendercontrolleur;
import com.jay.easygest.databinding.ActivityMainBinding;
import com.jay.easygest.model.AppKessModel;
import com.jay.easygest.model.SmsnoSentModel;
import com.jay.easygest.model.UserModel;
import com.jay.easygest.controleur.Usercontrolleur;
import com.jay.easygest.outils.AccessLocalAppKes;
import com.jay.easygest.outils.MesOutils;
import com.jay.easygest.outils.SessionManagement;
import com.jay.easygest.outils.SmsreSender;

import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;


public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private SessionManagement sessionManagement;
    private Usercontrolleur usercontrolleur;
    private Integer compteur;
    private UserModel user;
    private SmsSendercontrolleur smsSendercontrolleur;
    private SmsreSender smsreSender;
    private  AppKessModel appKessModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sessionManagement = new SessionManagement(this);
        smsSendercontrolleur = SmsSendercontrolleur.getSmsSendercotrolleurInstance(this);
        this.usercontrolleur = Usercontrolleur.getUsercontrolleurInstance(this);
        boolean is_authenticated = sessionManagement.getSession();
        if (is_authenticated){
            Intent intent = new Intent(MainActivity.this, GestionActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        init();
        authentification();
        redirectToAppActivation();
        debloquerCompteProprietaire();
        redirectToInitMdp();

        AccessLocalAppKes accessLocalAppKes = new AccessLocalAppKes(this);
        appKessModel = accessLocalAppKes.getAppkes();
        smsreSender = new SmsreSender(this,this);
        ArrayList<SmsnoSentModel> sms_no_Sents = smsSendercontrolleur.getSmsnoSentList();
        String expediteurName = appKessModel.getOwner();
        if (sms_no_Sents.size()>0){smsreSender.sendingUnSentMsg(sms_no_Sents,expediteurName);}

    }

    private void init() {
        fillTxtVConnestion();
        fillTxtVConnestionErrorAdmin();
        desactivatetxtCreation();
        desactiverbtnAuthInit();
        desactiverbtnAuthAdmineInit();
        parametres();
//        reinitialiserMdp();

    }

    /**
     * permet de s'authentifier
     */
    private void authentification() {

        binding.btnauth.setOnClickListener(view -> {
            try {
                String username = Objects.requireNonNull(binding.editTextUsername.getText()).toString().trim();
                String password = Objects.requireNonNull(binding.editTextTextPassword.getText()).toString().trim();
                UserModel user = usercontrolleur.recupProprietaire();
                if (username.isEmpty() || password.isEmpty()) {
                    Toast.makeText(MainActivity.this, "champs obligatoires", Toast.LENGTH_SHORT).show();

                } else {
                    if (username.length() >= 6 && password.length() >= 8) {

                        if (usercontrolleur.isAuthenticated(username, password)) {
                            UserModel userModel = new UserModel(user.getId(), user.getUsername(), user.getPassword(), user.getDateInscription(), user.getStatus(), user.isActif(), 0);
                            usercontrolleur.modifierUser(userModel);
                            usercontrolleur.setUser(userModel);
                            sessionManagement.saveSession(true);
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

    /**
     * permet de débloquer un compte
     */
    private void debloquerCompteProprietaire() {

        binding.btnmaindeloqueadmine.setOnClickListener(view -> {
            try {
                String admin_username = Objects.requireNonNull(binding.editTextUsername.getText()).toString().trim();
                String admin_password = Objects.requireNonNull(binding.editTextTextPassword.getText()).toString().trim();
                UserModel administrateur = usercontrolleur.recupAdministrateur();
                if ( admin_username.isEmpty() || admin_password.isEmpty()) {
                    Toast.makeText(MainActivity.this, "champs obligatoires", Toast.LENGTH_SHORT).show();
                } else {
                    if (admin_username.length() >= 6 && admin_password.length() >= 8) {

                        if (admin_username.equals(administrateur.getUsername()) && admin_password.equals(administrateur.getPassword())) {
                            usercontrolleur.activerProprietaire();
                            usercontrolleur.activerAdministrateur();
                            initChamp();
                            activerbtn(binding.btnauth);
                            binding.mainlayoutadmine.setVisibility(View.GONE);
                            fillTxtVConnestion();
                            afficherAlerte();
                        } else {
                            Toast.makeText(MainActivity.this, "username ou mot de passe incorrecte", Toast.LENGTH_SHORT).show();
                            desactiverbtnAuthAdmine(administrateur);
                        }
                    } else {
                        Toast.makeText(MainActivity.this, "username ou mot de passe trop court", Toast.LENGTH_SHORT).show();
                        desactiverbtnAuthAdmine(administrateur);
                    }
                }
            } catch (Exception e) {
                Toast.makeText(MainActivity.this, "un probleme d'integrité est survenu contacter votre administrateur", Toast.LENGTH_SHORT).show();
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

    private void redirectToAppActivation() {
        binding.txtCreateCompte.setOnClickListener(view -> activerProduit());

    }


    public void fillTxtVConnestion(){
        try {
            user = usercontrolleur.recupProprietaire();
            if (user != null){
                String msg_texte= "connection";
                if (user.getCompteur() >= 3){
                     msg_texte = "débloquer votre compte"+"\n"
                            +"vous êtes ici parce que"+"\n"
                            + "vous avez fait 3 tentatives"+"\n"
                            +"de fausse connection"+"\n"
                            +"entrer l'username et le mot de passe administrateur"+"\n"
                            +"pour debloquer votre compte utilisateur";
                     binding.textVConnection.setTextColor(getResources().getColor(R.color.red));
                    binding.textVConnection.setTextSize(12);
                }

                binding.textVConnection.setText(msg_texte);
            }
        }catch (Exception e){}

    }

    public void fillTxtVConnestionErrorAdmin(){
        try {
            user = usercontrolleur.recupAdministrateur();
            if (user != null){
                String msg_texte= "connection";
                if (user.getCompteur() >= 3){
                    msg_texte = "débloquer votre compte"+"\n"
                            +"vous êtes ici parce que"+"\n"
                            + "vous avez fait 3 tentatives"+"\n"
                            +"de fausse connection"+"\n"
                            +"acceder aux paramettres pour continuer";
                    binding.textVConnection.setTextColor(getResources().getColor(R.color.red));
                    binding.textVConnection.setTextSize(12);
                }

                binding.textVConnection.setText(msg_texte);
            }
        }catch (Exception e){}

    }



    private void activerbtn(Button btn){
        btn.setVisibility(View.VISIBLE);
        btn.setEnabled(true);
    }

    private void desactivatetxtCreation(){
        int nbrutilisateur = usercontrolleur.nbrUtilisateur();
        if (nbrutilisateur >= 3){
            binding.txtCreateCompte.setVisibility(View.GONE);
        }
    }


    /**
     * desactive le bouton d'authentification
     * @param userModel l'utilisateur
     */
    private void desactiverbtnAuth(UserModel userModel){
        if (userModel != null){
            int cmpteur = incrementCompteur(userModel);
            if (cmpteur >= 3){
                usercontrolleur.desactiverProprietaire();
                initChamp();
                binding.btnauth.setVisibility(View.GONE);
                binding.mainlayoutadmine.setVisibility(View.VISIBLE);
                fillTxtVConnestion();
            }
        }

    }

    /**
     * desactive le bouton d'authentification d'un administrateur
     * @param userModel l'administrateur
     */
    private void desactiverbtnAuthAdmine(UserModel userModel){
        int cmpteur = incrementCompteur(userModel);
        if (cmpteur >= 3){
            initChamp();
            usercontrolleur.desactiverAdministrateur();
            binding.txtParametres.setVisibility(View.VISIBLE);
            binding.mainlayoutadmine.setVisibility(View.GONE);
            fillTxtVConnestionErrorAdmin();
        }

    }

    private void desactiverbtnAuthAdmineInit(){
        try {
            UserModel userModel = usercontrolleur.recupAdministrateur();
            if (userModel.getCompteur() >= 3){
                binding.txtParametres.setVisibility(View.VISIBLE);
                binding.mainlayoutadmine.setVisibility(View.GONE);
            }
        }catch (Exception e){
            //do nothing
        }

    }

    private void desactiverbtnAuthInit(){
        try {
            UserModel userModel = usercontrolleur.recupProprietaire();
            if (userModel.getCompteur() >= 3){
                binding.btnauth.setVisibility(View.GONE);
                binding.mainlayoutadmine.setVisibility(View.VISIBLE);
            }
        }catch (Exception e){
            //do nothing
        }

    }

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


    /**
     * affiche le message de reactivation du compte
     */
    private void afficherAlerte() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("compte reactivé");
        builder.setMessage("votre compte à été reactivé"+"\n"
                +"cela est dû peut etre à une tentative frauduleuse de connection" +"\n"
               + "continué de toujours bien garder vos identifiants secret" +"\n"
               + "ne les communiqués en aucun cas à personne"+"\n"
               + "de préference il est conseiller de le changer toutes les semaines"+"\n"
               + "et surtout faite attention à qui vous remettez votre telephone"+"\n"
               + "il est recommendé de toujours configurer le vérouillage"+"\n"
                +"de votre téléphone avec un mot de passe ou un schema"+"\n");

        builder.setPositiveButton("ok", (dialog, which) -> {

        });

        builder.create().show();
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
//        smsreSender.resentReiceiver();
//        ArrayList<SmsnoSentModel> sms_no_Sents = smsSendercontrolleur.getSmsnoSentList();
//        String expediteurName = appKessModel.getOwner();
//        if (sms_no_Sents.size()>0){smsreSender.sendingUnSentMsg(sms_no_Sents,expediteurName);}
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