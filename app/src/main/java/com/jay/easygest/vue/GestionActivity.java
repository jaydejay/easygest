package com.jay.easygest.vue;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import com.google.android.material.textfield.TextInputEditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;
import com.jay.easygest.R;
import com.jay.easygest.controleur.Accountcontroller;
import com.jay.easygest.controleur.Articlescontrolleur;
import com.jay.easygest.controleur.Clientcontrolleur;
import com.jay.easygest.controleur.Creditcontrolleur;
import com.jay.easygest.controleur.SmsSendercontrolleur;
import com.jay.easygest.controleur.Usercontrolleur;
import com.jay.easygest.controleur.Versementcontrolleur;
import com.jay.easygest.databinding.ActivityGestionBinding;
import com.jay.easygest.model.AccountModel;
import com.jay.easygest.model.ArticlesModel;
import com.jay.easygest.model.ClientModel;
import com.jay.easygest.model.CreditModel;
import com.jay.easygest.model.SmsnoSentModel;
import com.jay.easygest.model.UserModel;
import com.jay.easygest.model.VersementsModel;
import com.jay.easygest.outils.AccessLocalAccount;
import com.jay.easygest.outils.AccessLocalCredit;
import com.jay.easygest.outils.MesOutils;
import com.jay.easygest.outils.PasswordHascher;
import com.jay.easygest.outils.SessionManagement;
import com.jay.easygest.outils.SmsreSender;
import com.jay.easygest.outils.VariablesStatique;
import com.jay.easygest.vue.ui.account.AccountViewModel;
import com.jay.easygest.vue.ui.clients.ClientViewModel;
import com.jay.easygest.vue.ui.credit.CreditViewModel;
import com.jay.easygest.vue.ui.versement.VersementViewModel;

import java.util.ArrayList;
import java.util.Date;

public class GestionActivity extends AppCompatActivity {
    private ActivityGestionBinding binding;
    private AppBarConfiguration mAppBarConfiguration;
    private Creditcontrolleur creditcontrolleur;
    private SessionManagement sessionManagement;
    private Accountcontroller accountcontrolleur;
    private Clientcontrolleur clientcontrolleur;
    private CreditViewModel creditViewModel;
    private AccountViewModel accountViewModel;
    private ClientViewModel clientViewModel;
    private VersementViewModel versementViewModel;
    TextInputEditText settingpassw;
    private SharedPreferences sharedPreferences;
    private PasswordHascher passwordHascher;
    private Usercontrolleur usercontrolleur ;
    private int totalcredits;
    private int totalversements;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGestionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        sessionManagement = new SessionManagement(this);

        creditcontrolleur = Creditcontrolleur.getCreditcontrolleurInstance(this);
        Versementcontrolleur versementcontrolleur = Versementcontrolleur.getVersementcontrolleurInstance(this);
        clientcontrolleur = Clientcontrolleur.getClientcontrolleurInstance(this);
        accountcontrolleur = Accountcontroller.getAccountcontrolleurInstance(this);
        usercontrolleur = Usercontrolleur.getUsercontrolleurInstance(this);

        versementViewModel = new ViewModelProvider(this).get(VersementViewModel.class);
        clientViewModel = new ViewModelProvider(this).get(ClientViewModel.class);
        creditViewModel = new ViewModelProvider(this).get(CreditViewModel.class);
        accountViewModel = new ViewModelProvider(this).get(AccountViewModel.class);
        passwordHascher = new PasswordHascher();
        sharedPreferences = this.getSharedPreferences(VariablesStatique.SETTING_SHARED_PREF_NAME, Context.MODE_PRIVATE);

        try {
            ArrayList<ClientModel> listeClients = clientcontrolleur.listeClients();
            ArrayList<CreditModel> credits = creditcontrolleur.listecredits();
            clientViewModel.getListeClients().setValue(listeClients);
            creditViewModel.getCredits().setValue(credits);
            versementViewModel.getMversements().setValue(versementcontrolleur.listeversements());

        }catch (Exception e){
            //
        }


        setSupportActionBar(binding.appBarGestion.toolbar);
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;

        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_clients, R.id.nav_articles, R.id.nav_listecredit, R.id.nav_credit, R.id.nav_account,R.id.nav_changepsssword,R.id.nav_changeusername,R.id.nav_import_export, R.id.nav_a_propos )
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_gestion);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        afficherecap();


    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.gestion, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.action_settings) {
            UserModel user = usercontrolleur.recupProprietaire();
            String setting_pass = sharedPreferences.getString(VariablesStatique.SETTING_SHARED_PREF_VARIABLE,user.getPassword());
            View view = LayoutInflater.from(GestionActivity.this).inflate(R.layout.layout_settings,null);
            settingpassw = view.findViewById(R.id.update_credit_date);

            AlertDialog.Builder builder = getSettingMenuBuilder(view, setting_pass, user);
            builder.create().show();

            return true;

        } else if (item.getItemId() == R.id.action_ask_key) {
            Intent intent = new Intent(this, DemandeCleActivity.class);
            startActivity(intent);
            return true;

        } else if (item.getItemId() == R.id.action_deconnecter) {
            sessionManagement.removeSession();
            finish();
            return true;

        } else { return super.onOptionsItemSelected(item);}
    }
    @NonNull
    private AlertDialog. Builder getSettingMenuBuilder(View view, String setting_pass, UserModel user) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this) ;
        builder.setTitle("mot de passe");
        builder.setView(view);

        builder.setPositiveButton("oui", (dialog, which) -> {

            if (passwordHascher.verifyHashingPass(settingpassw.getText().toString().trim(), setting_pass)){
                Intent intent = new Intent(this, SettingsActivity.class);
                intent.putExtra("mdp",settingpassw.getText().toString().trim());
                UserModel user2 = new UserModel(user.getId(), user.getUsername(), user.getPassword(), user.getDateInscription(), user.getStatus(),true,0);
                usercontrolleur.modifierUser(user2);
                usercontrolleur.setUser(user2);
                startActivity(intent);
            }else {
                UserModel userModel = usercontrolleur.recupProprietaire();
                int compteur = incrementCompteur(userModel);
                if (compteur >= 3){
                    sessionManagement.removeSession();
                    finish();
                }
            }

        });
        builder.setNegativeButton("annuller",(dialog, which) ->{

        });
        return builder;
    }

    /**
     *
     * @param userModel l'utilisateur qui se connecte
     * @return le nbr de tentative de connexion
     */
    private Integer incrementCompteur( UserModel userModel){
        int compteur = userModel.getCompteur() ;
        try {
            compteur = compteur + 1;
            UserModel user = new UserModel(userModel.getId(),userModel.getUsername(),userModel.getPassword(),userModel.getDateInscription(),userModel.getStatus(),userModel.isActif(),compteur);
            Usercontrolleur usercontrolleur = Usercontrolleur.getUsercontrolleurInstance(this);
            usercontrolleur.modifierUser(user);
            usercontrolleur.setUser(user);
        }catch (Exception e){
            //do nothing
        }
        return compteur;

    }
    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_gestion);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    private void replaceFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.drawer_layout, fragment)
                .commit();
    }

    public void afficherecap(){
        creditViewModel.getTotalcredits().observe(this, integer -> {
            TextView total_credit = findViewById(R.id.totalcredit);
            this.totalcredits = integer;
            total_credit.setText(String.valueOf(integer));
        });

        creditViewModel.getTotalversements().observe(this, integer -> {
            TextView total_versement = findViewById(R.id.totalversement);
            total_versement.setText(String.valueOf(integer));
        });

        creditViewModel.getTotalrestes().observe(this, integer -> {
            TextView totalreste = findViewById(R.id.totalreste);
            totalreste.setText(String.valueOf(integer));
        });


//        accountViewModel.getTotalaccounts().observe(this, integer -> {
//            TextView total_credit = findViewById(R.id.totalcredit);
//            total_credit.setText(String.valueOf(integer));
//        });
//
//        accountViewModel.getTotalversements().observe(this, integer -> {
//            TextView total_versement = findViewById(R.id.totalversement);
//            this.totalversements = integer;
//            total_versement.setText(String.valueOf(integer));
//        });
//
//        accountViewModel.getTotalrestes().observe(this, integer -> {
//            TextView totalreste = findViewById(R.id.totalreste);
//            totalreste.setText(String.valueOf(integer));
//        });


    }


    public void redirectToNouveauCreditActivity(ClientModel client) {
        clientcontrolleur.setClient(client);
        Intent intent = new Intent(this, AjouterCreditActivity.class);
        startActivity(intent);
    }

    public void redirectToAfficherClientActivity(ClientModel client) {
        clientViewModel.getClient().setValue(client);
        Intent intent = new Intent(this, AfficherclientActivity.class);
        startActivity(intent);
    }

    /**
     * affiche les détails d'un credit
     * @param credit le credit à détaillé
     */
    public void redirectToAfficherCreditActivity(CreditModel credit) {

        creditcontrolleur.setCredit(credit);
        Intent intent = new Intent(this, AffichercreditActivity.class);
        startActivity(intent);

    }

    /**
     * permet de supprimer un client
     * @param client le client à supprimer
     */
    public void supprimerClient(ClientModel client) {
        boolean success_credit = creditcontrolleur.isClientOwnCredit(client);
        boolean success_account = accountcontrolleur.isClientOwnAccount(client);
        if (success_credit || success_account) {
            Toast.makeText(this, "impossible de supprimer le client il a un credit ou un account en cours", Toast.LENGTH_LONG).show();
        }else {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("supprimer un client");
            builder.setMessage("vous etes sur le point de supprimer le client, son compte sera supprimé");

            builder.setPositiveButton("oui", (dialog, which) -> {
                clientcontrolleur.supprimerclient(client);
                Intent intent = new Intent(GestionActivity.this, GestionActivity.class);
                startActivity(intent);
            });
            builder.setNegativeButton("non", (dialog, which) -> {
            });
            builder.create().show();
        }
    }


    /**
     * afficher les details d'un versement
     * @param versement le versement
     * @param position sa position dans la liste
     * @param nbrversement le nombre total de versement
     */
    public void redirectToAfficheversementActivity(VersementsModel versement,int position, int nbrversement) {
        versementViewModel.getMversement().setValue(versement);
        CreditModel credit = creditcontrolleur.recupUnCreditById(versement.getCredit_id());
        creditViewModel.getCredit().setValue(credit);
        Intent intent = new Intent(this, AfficheversementActivity.class);
        intent.putExtra("versementposition",position);
        intent.putExtra("nbrversement",nbrversement);
        startActivity(intent);
    }

//    public void redirectToModifierArticleActivity(ArticlesModel articlesModel) {
//        Articlescontrolleur articlescontrolleur = Articlescontrolleur.getArticlescontrolleurInstance(this);
//        articlescontrolleur.setMarticle(articlesModel);
//        Intent intent = new Intent(this, ModifierArticleActivity.class);
//        startActivity(intent);
//    }

//    public void redirectToModifierArticleImgesActivity(ArticlesModel articlesModel) {
//        Articlescontrolleur articlescontrolleur = Articlescontrolleur.getArticlescontrolleurInstance(this);
//        articlescontrolleur.setMarticle(articlesModel);
//        Intent intent = new Intent(this, ModifierArticleActivity.class);
////        intent.putExtra("articleid",articlesModel.getId());
//        startActivity(intent);
//    }

    public void redirectToArticleDetailsActivity(ArticlesModel articlesModel) {
        Articlescontrolleur articlescontrolleur = Articlescontrolleur.getArticlescontrolleurInstance(this);
        articlescontrolleur.setMarticle(articlesModel);
        Intent intent = new Intent(this, ArticleDetailsActivity.class);
        startActivity(intent);
    }



    @Override
    protected void onResume() {
        super.onResume();
        if (!sessionManagement.getSession()){
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d("TAG", "onRestart invoked: ");
//        sessionManagement.removeSession();
        creditViewModel.getCredits().setValue(creditcontrolleur.listecredits());
        if (getIntent().getExtras() != null && getIntent().getExtras().getString("smssentmessge") != null){
            Toast.makeText(this, getIntent().getExtras().getString("smssentmessge"), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        supprimerCreditsSoldes();
        supprimerAccounttsSoldes();
        smsresender();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }

    public void redirectToAfficherAccountActivity(AccountModel accountModel) {
        accountcontrolleur.setAccount(accountModel);
        Intent intent = new Intent(this, AfficherAccountActivity.class);
        startActivity(intent);
    }

    public void refreshPage() {
        Intent intent = new Intent(this, GestionActivity.class);
        startActivity(intent);
    }

    public void supprimerAccounttsSoldes() {
        AccessLocalAccount accessLocalAccount = new AccessLocalAccount(this);
        ArrayList<AccountModel> accounts = accessLocalAccount.listeAccountsSoldes();
        long now = new Date().getTime();
        new Thread(() -> {
            if (!accounts.isEmpty()){
                for (AccountModel account : accounts) {
                    if (MesOutils.getSppressionDate2(account.getSoldedat()) <= now){
                        accountcontrolleur.supprimerAccountsSoldes(account);
                    }
                }
            }
        });
    }
    public void supprimerCreditsSoldes() {
        AccessLocalCredit accessLocalCredit = new AccessLocalCredit(this);
        ArrayList<CreditModel> credits = accessLocalCredit.listeCreditsSoldes();
        long now = new Date().getTime();
        new Thread(() -> {
            if (!credits.isEmpty()){
                for (CreditModel credit : credits) {
                    if (MesOutils.getSppressionDate2(credit.getSoldedat()) <= now){
                        creditcontrolleur.supprimeCreditSoldes(credit);
                    }
                }
            }
        });
    }

    private void smsresender() {
        new Thread(() -> {
            SmsSendercontrolleur smsSendercontrolleur = SmsSendercontrolleur.getSmsSendercotrolleurInstance(GestionActivity.this);
            SmsreSender smsreSender = new SmsreSender(GestionActivity.this, GestionActivity.this);
            ArrayList<SmsnoSentModel> sms_no_Sents = smsSendercontrolleur.getSmsnoSentList();
            if (!sms_no_Sents.isEmpty()){
                smsreSender.sendingUnSentMsg(sms_no_Sents);
            }

        }).start();
    }

}