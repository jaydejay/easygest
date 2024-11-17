package com.jay.easygest.vue;


import static com.jay.easygest.outils.VariablesStatique.MY_PERMISSIONS_REQUEST_SEND_SMS;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;
import com.jay.easygest.R;
import com.jay.easygest.controleur.Accountcontroller;
import com.jay.easygest.controleur.Clientcontrolleur;
import com.jay.easygest.controleur.Creditcontrolleur;
import com.jay.easygest.controleur.Usercontrolleur;
import com.jay.easygest.controleur.Versementcontrolleur;
import com.jay.easygest.databinding.ActivityGestionBinding;
import com.jay.easygest.model.AppKessModel;
import com.jay.easygest.model.ClientModel;
import com.jay.easygest.model.CreditModel;
import com.jay.easygest.model.SmsnoSentModel;
import com.jay.easygest.model.UserModel;
import com.jay.easygest.model.VersementsModel;
import com.jay.easygest.outils.AccessLocalAppKes;
import com.jay.easygest.outils.MesOutils;
import com.jay.easygest.outils.PasswordHascher;
import com.jay.easygest.outils.SessionManagement;
import com.jay.easygest.outils.SmsSender;
import com.jay.easygest.outils.VariablesStatique;
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
    private ClientViewModel clientViewModel;
    private VersementViewModel versementViewModel;
    private AccessLocalAppKes accessLocalAppKes;
    private AppKessModel appKessModel;
    private SmsSender smsSender;
    EditText settingpassw;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private PasswordHascher passwordHascher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        binding = ActivityGestionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        sessionManagement = new SessionManagement(this);
        smsSender = new SmsSender(this,this);
        creditcontrolleur = Creditcontrolleur.getCreditcontrolleurInstance(this);
        Versementcontrolleur versementcontrolleur = Versementcontrolleur.getVersementcontrolleurInstance(this);
        clientcontrolleur = Clientcontrolleur.getClientcontrolleurInstance(this);
        accountcontrolleur = Accountcontroller.getAccountcontrolleurInstance(this);

        versementViewModel = new ViewModelProvider(this).get(VersementViewModel.class);
        creditViewModel = new ViewModelProvider(this).get(CreditViewModel.class);
        clientViewModel = new ViewModelProvider(this).get(ClientViewModel.class);

        accessLocalAppKes = new AccessLocalAppKes(this);
        appKessModel = accessLocalAppKes.getAppkes();

        passwordHascher = new PasswordHascher();

        sharedPreferences = this.getSharedPreferences(VariablesStatique.SETTING_SHARED_PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        try {
            ArrayList<ClientModel> listeClients = clientcontrolleur.listeClients();
            clientViewModel.getListeClients().setValue(listeClients);
            creditViewModel.getCredits().setValue(creditcontrolleur.listecredits());
            versementViewModel.getMversements().setValue(versementcontrolleur.listeversements());

        }catch (Exception e){}


        setSupportActionBar(binding.appBarGestion.toolbar);
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;

        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_clients, R.id.nav_listecredit, R.id.nav_credit, R.id.nav_account,R.id.nav_sms_resent,R.id.nav_changepsssword,R.id.nav_changeusername,R.id.nav_import_export, R.id.nav_a_propos )
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

            Usercontrolleur usercontrolleur = Usercontrolleur.getUsercontrolleurInstance(this);
            UserModel user = usercontrolleur.recupProprietaire();

            String setting_pass = sharedPreferences.getString(VariablesStatique.SETTING_SHARED_PREF_VARIABLE,user.getPassword());
            View view = LayoutInflater.from(GestionActivity.this).inflate(R.layout.layout_settings,null);
            settingpassw = view.findViewById(R.id.settings_passw);

            AlertDialog.Builder builder = new AlertDialog.Builder(this) ;
            builder.setTitle("mot de passe");
            builder.setView(view);

            builder.setPositiveButton("oui", (dialog, which) -> {

                if (passwordHascher.verifyHashingPass(settingpassw.getText().toString().trim(),setting_pass)){

                    Intent intent = new Intent(this, SettingsActivity.class);
                    intent.putExtra("mdp",settingpassw.getText().toString().trim());
                    startActivity(intent);


                }

            });

            builder.setNegativeButton("annuller",(dialog, which) ->{

            });
            builder.create().show();

            return true;

        } else if (item.getItemId() == R.id.action_deconnecter) {
            sessionManagement.removeSession();
            finish();
            return true;

        } else { return super.onOptionsItemSelected(item);}
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_gestion);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }



    public void afficherecap(){

        creditViewModel.getTotalcredits().observe(this, integer -> {
            TextView totalcredit = findViewById(R.id.totalcredit);
            totalcredit.setText(String.valueOf(integer));

        });

        creditViewModel.getTotalversements().observe(this, integer -> {
            TextView totalversement = findViewById(R.id.totalversement);
            totalversement.setText(String.valueOf(integer));
        });

        creditViewModel.getTotalrestes().observe(this, integer -> {
            TextView totalreste = findViewById(R.id.totalreste);
            totalreste.setText(String.valueOf(integer));

        });

    }



    public void redirectToModifiercreditActivity(CreditModel credit){
        creditcontrolleur.setCredit(credit);
        clientViewModel.getClient().setValue(credit.getClient());
        Intent intent = new Intent(this, ModifiercreditActivity.class);
        startActivity(intent);
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
     * permet d'annuller un credit
     * @param credit le credit à annuller
     */
    public void annullerCredit(CreditModel credit){

        if (credit.getReste() > 0) {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("anuller un credit");
            builder.setMessage("êtes vous sûre de vouloir annuller le credit"+"\n"
                    +"tous les versements associés seront également supprimés"+"\n"
                    +"l'annullation d'un credit est soumise a une pénalité allant de 1000 F à 10%"
                    +"de la somme du credit");

            builder.setPositiveButton("oui", (dialog, which) -> {
                if (ActivityCompat.checkSelfPermission(this,
                        android.Manifest.permission.SEND_SMS) !=
                        PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this,
                            new String[]{android.Manifest.permission.SEND_SMS},
                            MY_PERMISSIONS_REQUEST_SEND_SMS);
                } else {
                    ClientModel client = credit.getClient();
                    boolean success = creditcontrolleur.annullerCredit(credit);

                    if (success){
                        ClientModel clientModel = clientcontrolleur.recupererClient(client.getId());
                        clientViewModel.getClient().setValue(clientModel);
                        creditcontrolleur.setRecapTresteClient(clientModel);
                        creditcontrolleur.setRecapTcreditClient(clientModel);

                        int total_credit_client = creditcontrolleur.getRecapTcreditClient().getValue();
                        int total_reste_client = creditcontrolleur.getRecapTresteClient().getValue();

                        String destinationAdress = "+225"+clientModel.getTelephone();
//                   String destinationAdress = "5556";
                        String messageBody = appKessModel.getOwner() +"\n"+"\n"
                                + clientModel.getNom() + " "+clientModel.getPrenoms() +"\n"
                                +"vous avez annuller le credit "+credit.getNumerocredit()+"\n"
                                +"le "+ MesOutils.convertDateToString(new Date())+"\n"
                                +"total credit : "+total_credit_client+"\n"
                                +"reste a payer : "+total_reste_client;

                        SmsnoSentModel smsnoSentModel = new SmsnoSentModel(clientModel.getId(),messageBody);

                        smsSender.smsSendwithInnerClass(messageBody, destinationAdress,credit.getId() );
                        smsSender.sentReiceiver(smsnoSentModel);

                    }
                }

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
        sessionManagement.removeSession();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }

}