package com.jay.easygest.vue;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.jay.easygest.R;
import com.jay.easygest.controleur.Accountcontroller;
import com.jay.easygest.controleur.Clientcontrolleur;
import com.jay.easygest.controleur.Creditcontrolleur;
import com.jay.easygest.databinding.ActivityAfficherclientBinding;
import com.jay.easygest.model.AccountModel;
import com.jay.easygest.model.ClientModel;
import com.jay.easygest.model.CreditModel;
import com.jay.easygest.model.VersementsModel;
import com.jay.easygest.vue.ui.account.AccountViewModel;
import com.jay.easygest.vue.ui.clients.ClientViewModel;
import com.jay.easygest.vue.ui.credit.CreditViewModel;
import com.jay.easygest.vue.ui.versement.VersementViewModel;

import java.util.ArrayList;

public class AfficherclientActivity extends AppCompatActivity {
    private ActivityAfficherclientBinding binding;
    private Clientcontrolleur clientcontrolleur;
    private  Creditcontrolleur creditcontrolleur;
    private Accountcontroller accountcontroller;
    private ClientViewModel clientViewModel;
    private VersementViewModel versementViewModel;
    private AccountViewModel accountViewModel;
    private CreditViewModel creditViewModel;
    private ClientModel client;
    private int id ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAfficherclientBinding.inflate(getLayoutInflater());
        clientcontrolleur = Clientcontrolleur.getClientcontrolleurInstance(this);
        creditcontrolleur = Creditcontrolleur.getCreditcontrolleurInstance(this);
        accountcontroller = Accountcontroller.getAccountcontrolleurInstance(this);

        clientViewModel = new ViewModelProvider(this).get(ClientViewModel.class);
        versementViewModel = new ViewModelProvider(this).get(VersementViewModel.class);
        creditViewModel = new ViewModelProvider(this).get(CreditViewModel.class);
        accountViewModel = new ViewModelProvider(this).get(AccountViewModel.class);

        client = clientViewModel.getClient().getValue();

        creditcontrolleur.setRecapTresteClient(client);
        creditcontrolleur.setRecapTversementClient(client);
        creditcontrolleur.setRecapTcreditClient(client);

        accountcontroller.setRecapTaccountClient(client);
        accountcontroller.setRecapTresteClient(client);
        int TresteCreditClient = 0;
        int TcreditClient = 0;

        if (creditViewModel.getTotalcreditsclient().getValue() != null){
             TcreditClient = creditViewModel.getTotalcreditsclient().getValue();
        }

        if (creditViewModel.getTotalrestesclient().getValue() != null){
             TresteCreditClient = creditViewModel.getTotalrestesclient().getValue();
        }

        int TaccountClient = 0;
        int TresteAccClient =0;

        if (accountViewModel.getTotalaccountsclient() != null){
            TaccountClient = accountViewModel.getTotalaccountsclient().getValue();
        }

        if (accountViewModel.getTotalrestesclient().getValue() != null){
            TresteAccClient = accountViewModel.getTotalrestesclient().getValue();
        }

        setContentView(binding.getRoot());

        clientMenuDisabledAndRecpShow(TcreditClient,TresteCreditClient,TaccountClient,TresteAccClient);
        redirectToModifierClient();
        afficherclient();
        afficherListeVersementacc();
        afficherAjouterVersementacc();
        afficherListeAccounts();
        afficherListeAccountsoldes();
        afficherAjouterVersement();
        afficherListeVersement();
        afficherListeCredits();
        supprimerClient();
        afficherListeCreditsoldes();
    }


    public void clientMenuDisabledAndRecpShow(Integer TcreditClient, Integer TresteCreditClient,Integer TaccountClient, Integer TresteAccClient){

        if (TresteCreditClient == 0){
            binding.afClientListeCredits.setVisibility(View.GONE);
            binding.afClientTextVersements.setVisibility(View.GONE);
            binding.afClientListeVersements.setVisibility(View.GONE);
        }
        if (client.getNbrcredit() == 0){
            binding.afficherClientMenuCredit.setVisibility(View.GONE);
        }
        if (TresteAccClient == 0){
            binding.afClientListeAccounts.setVisibility(View.GONE);
            binding.afClientListeVersmAccounts.setVisibility(View.GONE);
            binding.afClientTextAccounts.setVisibility(View.GONE);
        }

        if (client.getNbraccount() == 0){
            binding.afficherClientMenuAccount.setVisibility(View.GONE);
        }

        String text_Totalcredit= "credit en cours : "+TcreditClient ;
        String text_Totalreste =  "reste à payer : "+TresteCreditClient ;

        String text_Totalaccount= "account en cours : "+TaccountClient ;
        String text_Totalreste_acc =  "reste à payer : "+TresteAccClient ;

        binding.textViewAfClientRecapCreditSomme.setText(text_Totalcredit);
        binding.textViewAfClientRecapCreditReste.setText(text_Totalreste);

        binding.textViewAfClientRecapAccountSomme.setText(text_Totalaccount);
        binding.textViewAfClientRecapAccountReste.setText(text_Totalreste_acc);
    }

    public void afficherclient(){
        try {

                if (client !=null){
                    String titre = "infos client "+client.getCodeclient();
                    ActionBar actionBar =  getSupportActionBar();
                    if (actionBar != null) {
                        actionBar.setTitle(titre);
                    }

                    binding.textViewAfClientNom.setText(client.getNom());
                    binding.textViewAfClientPrenoms.setText(client.getPrenoms());
                    binding.textViewAfClientTelephone.setText(client.getTelephone());

                    binding.textViewAfClientEmail.setText(client.getEmail());
                    if ( binding.textViewAfClientEmail.length() == 0){
                        binding.layoutAfClientEmail.setVisibility(View.GONE);
                    }else {binding.layoutAfClientEmail.setVisibility(View.VISIBLE);}


                    binding.textViewAfClientResidence.setText(client.getResidence());
                    if ( binding.textViewAfClientResidence.length() == 0){
                        binding.layoutAfClientResidence.setVisibility(View.GONE);
                    }else { binding.layoutAfClientResidence.setVisibility(View.VISIBLE);}

                    binding.textViewAfClientCni.setText(client.getCni());
                    if ( binding.textViewAfClientCni.length() == 0){
                        binding.layoutAfClientCni.setVisibility(View.GONE);
                    }else { binding.layoutAfClientCni.setVisibility(View.VISIBLE);}

                    binding.textViewAfClientPermis.setText(client.getPermis());
                    if ( binding.textViewAfClientPermis.length() == 0){
                        binding.layoutAfClientPermis.setVisibility(View.GONE);
                    }else {binding.layoutAfClientPermis.setVisibility(View.VISIBLE);}

                    binding.textViewAfClientPassport.setText(client.getPassport());
                    if ( binding.textViewAfClientPassport.length() == 0){
                        binding.layoutAfClientPassport.setVisibility(View.GONE);
                    }else { binding.layoutAfClientPassport.setVisibility(View.VISIBLE); }

                    binding.textViewAfClientSociete.setText(client.getSociete());
                    if ( binding.textViewAfClientSociete.length() == 0){
                        binding.layoutAfClientSociete.setVisibility(View.GONE);
                    }else {binding.layoutAfClientSociete.setVisibility(View.VISIBLE);}

                    binding.textViewAfClientNbrcredit.setText(String.valueOf(client.getNbrcredit()));
                    if ( binding.textViewAfClientNbrcredit.length() == 0){
                        binding.layoutAfClientNbrcredit.setVisibility(View.GONE);
                    }else { binding.layoutAfClientNbrcredit.setVisibility(View.VISIBLE);}


                    binding.textViewAfClientTotalcredit.setText(String.valueOf(client.getTotalcredit()));
                    if ( binding.textViewAfClientTotalcredit.length() == 0){
                        binding.layoutAfClientTotalcredit.setVisibility(View.GONE);
                    }else { binding.layoutAfClientTotalcredit.setVisibility(View.VISIBLE);}

                    binding.textViewAfClientNbraccount.setText(client.getNbraccount());
                    if ( binding.textViewAfClientNbraccount.length() == 0){
                        binding.layoutAfClientNbraccount.setVisibility(View.GONE);
                    }else { binding.layoutAfClientNbraccount.setVisibility(View.VISIBLE);}

                    binding.textViewAfClientTotalaccount.setText(String.valueOf(client.getTotalaccount()));
                    if ( binding.textViewAfClientTotalaccount.length() == 0){
                        binding.layoutAfClientTotalaccount.setVisibility(View.GONE);
                    }else {binding.layoutAfClientTotalaccount.setVisibility(View.VISIBLE); }

                }
        }catch (Exception e){
//           throw new Exception(e.getMessage());
        }

    }


    /**
     * redirection pour modifier un client
     */
    public void redirectToModifierClient(){
        binding.btnAfClientModif.setOnClickListener(v -> {
            Intent intent = new Intent(AfficherclientActivity.this, ModifierClientActivity.class);
            startActivity(intent);
        });
    }

    /**
     * methode pour supprimer un client
     */
    public void supprimerClient() {

        binding.btnAfClientSup.setOnClickListener(view -> clientViewModel.getClient().observe(this, client->{
            ArrayList<CreditModel>  credits = creditcontrolleur.listecreditsclient(client);
            if (credits.size() == 0) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("supprimer un client");
                builder.setMessage("vous etes sur le point de supprimer le client, son compte seras supprimé");

                builder.setPositiveButton("oui", (dialog, which) -> {

                    boolean success = clientcontrolleur.supprimerclient(client);
                    if (success){
                        Intent intent = new Intent(AfficherclientActivity.this, GestionActivity.class);
                        startActivity(intent);
                    }else {Toast.makeText(this, "echec de la suppression", Toast.LENGTH_LONG).show();}

                });
                builder.setNegativeButton("non", (dialog, which) -> {

                });

                builder.create().show();
            }else {Toast.makeText(this, "impossible de supprimer le client il a un credit en cours", Toast.LENGTH_LONG).show();}

        }));


    }


    /**
     * affiche la liste des accounts en cours d'un client
     */
    public void afficherListeAccounts(){

        binding.afClientListeAccounts.setOnClickListener(view -> {

            id = R.id.af_client_liste_accounts;
            accountcontroller.listeAccountsClient(client);
//            creditViewModel.getCreditsClients();
            Intent intent = new Intent(AfficherclientActivity.this, AfficherCreditsClientActivity.class);
            intent.putExtra("fragmentid",id);
            intent.putExtra("titre","accounts en cour");
            startActivity(intent);

        });
    }

    public void afficherListeAccountsoldes(){

        binding.afClientListeHistoAccounts.setOnClickListener(view -> {

            id = R.id.af_client_liste_histo_accounts;
            accountcontroller.listeAccountsoldeClient(client);
            Intent intent = new Intent(AfficherclientActivity.this, AfficherCreditsClientActivity.class);
            intent.putExtra("fragmentid",id);
            intent.putExtra("titre","accounts soldés");
            startActivity(intent);

        });
    }


    /**
     * affiche le formulaire pour faire un versement account
     */
    public void afficherAjouterVersementacc(){

        binding.afClientTextAccounts.setOnClickListener(view -> {

            id = R.id.af_client_text_accounts;

            Intent intent = new Intent(AfficherclientActivity.this, AfficherCreditsClientActivity.class);
            intent.putExtra("fragmentid",id);
            intent.putExtra("titre","versement d'account");
            startActivity(intent);
        });

    }


    /**
     * affiche la liste des versements des accounts d'un client
     */
    public void afficherListeVersementacc(){

        binding.afClientListeVersmAccounts.setOnClickListener(view -> {

            ArrayList<VersementsModel> liste_versements =  versementViewModel.getVersementsClient(client);
            versementViewModel.getMversements().setValue(liste_versements);
            id = R.id.af_client_liste_versm_accounts;

            Intent intent = new Intent(AfficherclientActivity.this, AfficherCreditsClientActivity.class);
            intent.putExtra("fragmentid",id);
            intent.putExtra("titre","versements accounts");
            startActivity(intent);

        });

    }


    /**
     * affiche la liste des credits d'un client
     */
    public void afficherListeCredits(){

        binding.afClientListeCredits.setOnClickListener(view -> {

            id = R.id.af_client_liste_credits;
            creditcontrolleur.listecredits();
            creditViewModel.getCreditsClients(client.getId());
            Intent intent = new Intent(AfficherclientActivity.this, AfficherCreditsClientActivity.class);
            intent.putExtra("fragmentid",id);
            intent.putExtra("titre","liste de credits en cour");
            startActivity(intent);

        });
    }

    public void afficherListeCreditsoldes(){

        binding.afClientListeHistoCredits.setOnClickListener(view -> {

            id = R.id.af_client_liste_histo_credits;
            creditcontrolleur.listecreditsSoldesclient(client);
            creditViewModel.getCreditsSoldesClient(client.getId());
            Intent intent = new Intent(AfficherclientActivity.this, AfficherCreditsClientActivity.class);
            intent.putExtra("fragmentid",id);
            intent.putExtra("titre","liste de credits soldés");
            startActivity(intent);

        });
    }


    /**
     * affiche le formulaire pour faire un versement
     */
    public void afficherAjouterVersement(){

        binding.afClientTextVersements.setOnClickListener(view -> {

            id = R.id.af_client_text_versements;

            Intent intent = new Intent(AfficherclientActivity.this, AfficherCreditsClientActivity.class);
            intent.putExtra("fragmentid",id);
            intent.putExtra("titre","faire un versement");
            startActivity(intent);
        });

    }


    /**
     * affiche la liste des versements des credits d'un client
     */
    public void afficherListeVersement(){

        binding.afClientListeVersements.setOnClickListener(view -> {

            ArrayList<VersementsModel> liste_versements =  versementViewModel.getVersementsClient(client);
            versementViewModel.getMversements().setValue(liste_versements);
            id = R.id.af_client_liste_versements;

            Intent intent = new Intent(AfficherclientActivity.this, AfficherCreditsClientActivity.class);
            intent.putExtra("fragmentid",id);
            intent.putExtra("titre","liste des versements");
            startActivity(intent);

        });

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
            builder.setMessage("etes vous sûre de vouloir annuller le credit, le client sera retiré de la liste des credits");

            builder.setPositiveButton("oui", (dialog, which) -> {
                creditcontrolleur.annullerCredit(credit);
                Intent intent = new Intent(AfficherclientActivity.this, GestionActivity.class);
                startActivity(intent);
            });

            builder.setNegativeButton("non", (dialog, which) -> {

            });

            builder.create().show();

        }else{
            Toast.makeText(this, "credit deja soldé", Toast.LENGTH_SHORT).show();
        }
    }



    public void redirectToAfficherCreditActivity(CreditModel credit) {

        creditcontrolleur.setCredit(credit);
        Intent intent = new Intent(this, AffichercreditActivity.class);
        startActivity(intent);
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
//       clientcontrolleur.setClient(null);
       binding = null;
    }


}