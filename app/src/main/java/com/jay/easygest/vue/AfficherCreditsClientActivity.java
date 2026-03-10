package com.jay.easygest.vue;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.jay.easygest.R;
import com.jay.easygest.controleur.Accountcontroller;
import com.jay.easygest.controleur.Creditcontrolleur;
import com.jay.easygest.databinding.ActivityAfficherCreditsClientBinding;
import com.jay.easygest.model.AccountModel;
import com.jay.easygest.model.ClientModel;
import com.jay.easygest.model.CreditModel;
import com.jay.easygest.model.VersementsModel;
import com.jay.easygest.model.VersementsaccModel;
import com.jay.easygest.outils.SessionManagement;
import com.jay.easygest.vue.ui.account.ListeAccountsClientFragment;
import com.jay.easygest.vue.ui.clients.ClientViewModel;
import com.jay.easygest.vue.ui.credit.CreditViewModel;
import com.jay.easygest.vue.ui.listecredit.ListecreditsclientFragment;
import com.jay.easygest.vue.ui.listeversement.ListeversementFragment;
import com.jay.easygest.vue.ui.versement.VersementFragment;
import com.jay.easygest.vue.ui.versement.VersementViewModel;
import com.jay.easygest.vue.ui.versementacc.AjouterVersementaccFragment;
import com.jay.easygest.vue.ui.versementacc.ListeVersementaccFragment;
import com.jay.easygest.vue.ui.versementacc.VersementaccViewModel;

public class AfficherCreditsClientActivity extends AppCompatActivity {

   private  SessionManagement sessionManagement;

    private Creditcontrolleur creditcontrolleur;
    private Accountcontroller accountcontroller;
    private VersementViewModel versementViewModel;
    private CreditViewModel creditViewModel;
    private VersementaccViewModel versementaccViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sessionManagement = new SessionManagement(this);
        com.jay.easygest.databinding.ActivityAfficherCreditsClientBinding binding = ActivityAfficherCreditsClientBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        creditcontrolleur = Creditcontrolleur.getCreditcontrolleurInstance(this);
        accountcontroller = Accountcontroller.getAccountcontrolleurInstance(this);

        creditViewModel = new ViewModelProvider(this).get(CreditViewModel.class);
        versementaccViewModel = new ViewModelProvider(this).get(VersementaccViewModel.class);
        ClientViewModel clientViewModel = new ViewModelProvider(this).get(ClientViewModel.class);
        versementViewModel = new ViewModelProvider(this).get(VersementViewModel.class);

        ClientModel client = clientViewModel.getClient().getValue();
        int fragmentid =  getIntent().getIntExtra("fragmentid",R.id.af_client_liste_credits);
        String titre = getIntent().getStringExtra("titre");

        if (client != null){
            String identite_de_client = client.getNom()+" "+ client.getPrenoms()+" "+ client.getCodeclient();
            binding.textViewCredClitVers.setText(identite_de_client);
        }

       ActionBar actionBar =  getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(titre);
        }
        onMenuItemSelected(fragmentid);
    }


    public Fragment onMenuItemSelected(int id){

        Fragment fragment ;

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
            fragment = AjouterVersementaccFragment.newInstance();
        }
        else if (id == R.id.af_client_liste_versm_accounts) {
            fragment = ListeVersementaccFragment.newInstance();
        }
        else {
            fragment = new ListecreditsclientFragment();

        }

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.frame_client_layout,fragment);
        ft.commit();

        return fragment;
    }


    public void redirectToAfficherAccountActivity(AccountModel accountModel) {
        accountcontroller.setAccount(accountModel);
        Intent intent = new Intent(this, AfficherAccountActivity.class);
        startActivity(intent);
    }

    public void redirectToAfficheversementaccActivity(VersementsaccModel versementacc, int position, int nbrversement) {

        versementaccViewModel.getMversementacc().setValue(versementacc);
        Intent intent = new Intent(this, AfficherversementaccActivity.class);
        intent.putExtra("versementaccposition",position);
        intent.putExtra("nbrversementacc",nbrversement);
        startActivity(intent);
    }

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
//        smsSender.sentReiceiver();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        sessionManagement.removeSession();
    }


    public void redirectToAfficherCreditActivity(CreditModel creditModel) {
        creditcontrolleur.setCredit(creditModel);
        Intent intent = new Intent(this, AffichercreditActivity.class);
        startActivity(intent);
    }
}

