package com.jay.easygest.vue;


import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;
import com.jay.easygest.R;
import com.jay.easygest.controleur.Clientcontrolleur;
import com.jay.easygest.controleur.Creditcontrolleur;
import com.jay.easygest.controleur.Versementcontrolleur;
import com.jay.easygest.databinding.ActivityGestionBinding;
import com.jay.easygest.model.ClientModel;
import com.jay.easygest.model.CreditModel;
import com.jay.easygest.vue.ui.clients.ClientViewModel;
import com.jay.easygest.vue.ui.credit.CreditViewModel;
import com.jay.easygest.vue.ui.versement.VersementViewModel;

import java.util.ArrayList;

public class GestionActivity extends AppCompatActivity {
    private ActivityGestionBinding binding;
    private AppBarConfiguration mAppBarConfiguration;
    private Creditcontrolleur creditcontrolleur;
    private Clientcontrolleur clientcontrolleur;
    private CreditViewModel creditViewModel;
    private ClientViewModel clientViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityGestionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        creditcontrolleur = Creditcontrolleur.getCreditcontrolleurInstance(this);
        Versementcontrolleur versementcontrolleur = Versementcontrolleur.getVersementcontrolleurInstance(this);
        clientcontrolleur = Clientcontrolleur.getClientcontrolleurInstance(this);

        VersementViewModel versementViewModel = new ViewModelProvider(this).get(VersementViewModel.class);
        creditViewModel = new ViewModelProvider(this).get(CreditViewModel.class);
        clientViewModel = new ViewModelProvider(this).get(ClientViewModel.class);

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
                R.id.nav_clients, R.id.nav_listecredit, R.id.nav_credit, R.id.nav_account,R.id.nav_listeversement,R.id.nav_changepsssword,R.id.nav_changeusername,R.id.nav_import_export, R.id.nav_a_propos )
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


    public void redirectToAfficherCreditActivity(CreditModel credit) {

        creditcontrolleur.setCredit(credit);
        Intent intent = new Intent(this, AffichercreditActivity.class);
        startActivity(intent);

    }

    public void supprimerClient(ClientModel client) {
        boolean success = creditcontrolleur.isClientOwnCredit(client);
        if (success) {
            Toast.makeText(this, "impossible de supprimer le client il a un ou des credits en cours", Toast.LENGTH_LONG).show();
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

    public void annullerCredit(CreditModel credit){

        if (credit.getReste() > 0) {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("anuller un credit");
            builder.setMessage("êtes vous sûre de vouloir annuller le credit"+"\n"
                    +"tous les versements associés seront également supprimés"+"\n"
                    +"l'annullation d'un credit est soumise a une pénalité allant de 1000 F à 10%"
                    +"de la somme du credit");

            builder.setPositiveButton("oui", (dialog, which) -> {
                 boolean success = creditcontrolleur.annullerCredit(credit);
               if (success){
                   Intent intent = new Intent(GestionActivity.this, GestionActivity.class);
                   startActivity(intent);
               }

            });

            builder.setNegativeButton("non", (dialog, which) -> {

            });

            builder.create().show();

        }
    }


}