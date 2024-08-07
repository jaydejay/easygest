package com.jay.easygest.vue;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.jay.easygest.R;
import com.jay.easygest.controleur.Accountcontroller;
import com.jay.easygest.controleur.Clientcontrolleur;
import com.jay.easygest.controleur.Creditcontrolleur;
import com.jay.easygest.controleur.Versementcontrolleur;
import com.jay.easygest.databinding.ActivityAfficherCreditsClientBinding;
import com.jay.easygest.model.AccountModel;
import com.jay.easygest.model.ClientModel;
import com.jay.easygest.model.CreditModel;
import com.jay.easygest.model.VersementsModel;
import com.jay.easygest.vue.ui.account.ListeAccountsClientFragment;
import com.jay.easygest.vue.ui.clients.ClientViewModel;
import com.jay.easygest.vue.ui.credit.CreditViewModel;
import com.jay.easygest.vue.ui.listecredit.ListecreditsclientFragment;
import com.jay.easygest.vue.ui.listeversement.ListeversementFragment;
import com.jay.easygest.vue.ui.versement.VersementFragment;
import com.jay.easygest.vue.ui.versement.VersementViewModel;

public class AfficherCreditsClientActivity extends AppCompatActivity {

    private  ActivityAfficherCreditsClientBinding binding;
    private Clientcontrolleur clientcontrolleur;
    private Versementcontrolleur versementcontrolleur;
    private Creditcontrolleur creditcontrolleur;
    private Accountcontroller accountcontroller;
    private ClientViewModel clientViewModel;
    private VersementViewModel versementViewModel;
    private CreditViewModel creditViewModel;
    private ClientModel client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAfficherCreditsClientBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        versementcontrolleur = Versementcontrolleur.getVersementcontrolleurInstance(this);
        versementViewModel = new ViewModelProvider(this).get(VersementViewModel.class);

        creditcontrolleur = Creditcontrolleur.getCreditcontrolleurInstance(this);
        creditViewModel = new ViewModelProvider(this).get(CreditViewModel.class);

        clientcontrolleur = Clientcontrolleur.getClientcontrolleurInstance(this);
        clientViewModel = new ViewModelProvider(this).get(ClientViewModel.class);
        client = clientViewModel.getClient().getValue();

        accountcontroller = Accountcontroller.getAccountcontrolleurInstance(this);
       int fragmentid =  getIntent().getIntExtra("fragmentid",R.id.af_client_liste_credits);
       String titre = getIntent().getStringExtra("titre");
       String identite_de_client = client.getNom()+" "+client.getPrenoms()+" "+client.getCodeclient();
       binding.textViewCredClitVers.setText(identite_de_client);


       ActionBar actionBar =  getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(titre);
        }
        onMenuItemSelected(fragmentid);
    }


    public Fragment onMenuItemSelected(int id){

        Fragment fragment = null;

        if (id == R.id.af_client_text_versements ){

            fragment = new VersementFragment();

        } else if (id == R.id.af_client_liste_versements) {
            fragment = new ListeversementFragment();
        }
        else if (id == R.id.af_client_liste_accounts) {
            fragment = new ListeAccountsClientFragment();
        }
        else if (id == R.id.af_client_liste_histo_accounts) {
            fragment = new ListeAccountsClientFragment();
        }
        else if (id == R.id.af_client_text_accounts) {
            fragment = null;
        }
        else if (id == R.id.af_client_liste_versm_accounts) {
            fragment = null;
        }
        else {
            fragment = new ListecreditsclientFragment();

        }


        if (fragment != null){

            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.frame_client_layout,fragment);
            ft.commit();
        }

        return fragment;
    }


    public void redirectToAfficheversementActivity(VersementsModel versement) {
      versementViewModel.getMversement().setValue(versement);
        CreditModel credit = creditcontrolleur.recupUnCreditById(versement.getCredit_id());
        creditViewModel.getCredit().setValue(credit);
        Intent intent = new Intent(this, AfficheversementActivity.class);
        startActivity(intent);
    }

    public void redirectToModifiercreditActivity(CreditModel credit){
        creditcontrolleur.setCredit(credit);
        creditcontrolleur.setTagx(1);
        Intent intent = new Intent(this, ModifiercreditActivity.class);
        intent.putExtra("Tagx",1);
        startActivity(intent);
    }

    public void annullerCredit(CreditModel credit){

        if (credit.getReste() > 0) {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("anuller un credit");
            builder.setMessage("etes vous sûre de vouloir annuller le credit, tous les versements associes seront egalement supprimés");

            builder.setPositiveButton("oui", (dialog, which) -> {
                boolean success = creditcontrolleur.annullerCredit(credit);
                if (success){
                    Intent intent = new Intent(AfficherCreditsClientActivity.this, GestionActivity.class);
                    startActivity(intent);
                }

            });

            builder.setNegativeButton("non", (dialog, which) -> {

            });

            builder.create().show();

        }
    }

    public void redirectToAfficherAccountActivity(AccountModel accountModel) {
        accountcontroller.setAccount(accountModel);
        Intent intent = new Intent(this, AfficherAccountActivity.class);
        startActivity(intent);
    }
}

