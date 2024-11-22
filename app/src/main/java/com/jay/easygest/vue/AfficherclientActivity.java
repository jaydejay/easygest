package com.jay.easygest.vue;

import static com.jay.easygest.outils.VariablesStatique.MY_PERMISSIONS_REQUEST_SEND_SMS;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;

import com.jay.easygest.R;
import com.jay.easygest.controleur.Accountcontroller;
import com.jay.easygest.controleur.Clientcontrolleur;
import com.jay.easygest.controleur.Creditcontrolleur;
import com.jay.easygest.controleur.Versementacccontrolleur;
import com.jay.easygest.controleur.Versementcontrolleur;
import com.jay.easygest.databinding.ActivityAfficherclientBinding;
import com.jay.easygest.model.AccountModel;
import com.jay.easygest.model.AppKessModel;
import com.jay.easygest.model.ClientModel;
import com.jay.easygest.model.CreditModel;
import com.jay.easygest.model.SmsnoSentModel;
import com.jay.easygest.model.VersementsModel;
import com.jay.easygest.outils.AccessLocalAppKes;
import com.jay.easygest.outils.MesOutils;
import com.jay.easygest.outils.SessionManagement;
import com.jay.easygest.outils.SmsSender;
import com.jay.easygest.vue.ui.account.AccountViewModel;
import com.jay.easygest.vue.ui.clients.ClientViewModel;
import com.jay.easygest.vue.ui.credit.CreditViewModel;
import com.jay.easygest.vue.ui.versement.VersementViewModel;

import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

public class AfficherclientActivity extends AppCompatActivity {
    private ActivityAfficherclientBinding binding;
    private  SessionManagement sessionManagement;
    private Clientcontrolleur clientcontrolleur;
    private  Creditcontrolleur creditcontrolleur;
    private Accountcontroller accountcontrolleur;
    private Versementacccontrolleur versementacccontrolleur;
    private Versementcontrolleur versementcontrolleur;
    private ClientViewModel clientViewModel;
    private VersementViewModel versementViewModel;
    private AccountViewModel accountViewModel;
    private ClientModel client;
    private AccessLocalAppKes accessLocalAppKes;
    private AppKessModel appKessModel;
    private SmsSender smsSender;
    private int id ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAfficherclientBinding.inflate(getLayoutInflater());
        sessionManagement = new SessionManagement(this);
        smsSender = new SmsSender(this, this);
        clientcontrolleur = Clientcontrolleur.getClientcontrolleurInstance(this);
        creditcontrolleur = Creditcontrolleur.getCreditcontrolleurInstance(this);
        accountcontrolleur = Accountcontroller.getAccountcontrolleurInstance(this);
        versementacccontrolleur = Versementacccontrolleur.getVersementacccontrolleurInstance(this);
        versementcontrolleur = Versementcontrolleur.getVersementcontrolleurInstance(this);

        clientViewModel = new ViewModelProvider(this).get(ClientViewModel.class);
        versementViewModel = new ViewModelProvider(this).get(VersementViewModel.class);
        CreditViewModel creditViewModel = new ViewModelProvider(this).get(CreditViewModel.class);
        accountViewModel = new ViewModelProvider(this).get(AccountViewModel.class);
        accessLocalAppKes = new AccessLocalAppKes(this);
        appKessModel = accessLocalAppKes.getAppkes();
        client = clientViewModel.getClient().getValue();
        try {
            creditcontrolleur.setRecapTresteClient(client);
            creditcontrolleur.setRecapTversementClient(client);
            creditcontrolleur.setRecapTcreditClient(client);

            accountcontrolleur.setRecapTaccountClient(client);
            accountcontrolleur.setRecapTresteClient(client);

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

            clientMenuDisabledAndRecpShow(TcreditClient,TresteCreditClient,TaccountClient,TresteAccClient);
        }catch (Exception e){
            //do nothing
        }


        setContentView(binding.getRoot());


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
        ajouterAcount();
        ajouterCredit();
        afficherListeCreditsoldes();
        supprimerCreditSoldes();
        supprimerAccountSoldes();
        afficherIntentMessage();
    }


        public void  afficherIntentMessage(){
        Bundle intent = getIntent().getExtras();

            if ( intent != null){
                String msg = intent.get("smssentmessge").toString();
                Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
            }
        }




    public void clientMenuDisabledAndRecpShow(Integer TcreditClient, Integer TresteCreditClient,Integer TaccountClient, Integer TresteAccClient){

        if (TresteCreditClient == 0){

            binding.afClientTextVersements.setVisibility(View.GONE);
            binding.afClientListeCredits.setVisibility(View.GONE);

        }
        if (client.getNbrcredit() == 0){
            binding.afClientListeCredits.setVisibility(View.GONE);
            binding.afClientTextVersements.setVisibility(View.GONE);
            binding.afClientListeVersements.setVisibility(View.GONE);
            binding.afClientListeHistoCredits.setVisibility(View.GONE);

        }
        if (TresteAccClient == 0){
            binding.afClientListeAccounts.setVisibility(View.GONE);
            binding.afClientTextAccounts.setVisibility(View.GONE);
        }

        if (client.getNbraccount() == 0){
            binding.afClientListeAccounts.setVisibility(View.GONE);
            binding.afClientTextAccounts.setVisibility(View.GONE);
            binding.afClientListeVersmAccounts.setVisibility(View.GONE);
            binding.afClientListeHistoAccounts.setVisibility(View.GONE);
        }

        String text_Totalcredit= "credit en cours : "+TcreditClient;
        String text_Totalreste =  "reste à payer : "+TresteCreditClient;

        String text_Totalaccount= "account en cours : "+TaccountClient ;
        String text_Totalreste_acc =  "reste à payer : "+TresteAccClient ;

        binding.textViewAfClientRecapCreditSomme.setText(text_Totalcredit);
        binding.textViewAfClientRecapCreditReste.setText(text_Totalreste);

        binding.textViewAfClientRecapAccountSomme.setText(text_Totalaccount);
        binding.textViewAfClientRecapAccountReste.setText(text_Totalreste_acc);
    }

    public void afficherclient(){
        try {
            client = clientViewModel.getClient().getValue();
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

                    binding.textViewAfClientNbraccount.setText(String.valueOf(client.getNbraccount()));
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
            binding.btnAfClientSup.setEnabled(false);
            ArrayList<CreditModel>  credits = creditcontrolleur.listecreditsclient(client);
             accountcontrolleur.listeAccountsClient(client);
             ArrayList<AccountModel> accounts = accountViewModel.getAccount_solde_ou_non().getValue();

             if (credits.size() == 0 && Objects.requireNonNull(accounts).size() == 0) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("supprimer un client");
                builder.setMessage("vous etes sur le point de supprimer le client, son compte seras supprimé");

                builder.setPositiveButton("oui", (dialog, which) -> {

                    boolean success = clientcontrolleur.supprimerclient(client);
                    if (success){
                        Intent intent = new Intent(AfficherclientActivity.this, GestionActivity.class);
                        startActivity(intent);
                    }else {Toast.makeText(this, "echec de la suppression", Toast.LENGTH_LONG).show();
                        binding.btnAfClientSup.setEnabled(true);}

                });
                builder.setNegativeButton("non", (dialog, which) -> {
                    binding.btnAfClientSup.setEnabled(true);
                });

                builder.create().show();
            }else {
                 Toast.makeText(this, "impossible de supprimer le client il a un credit ou un account en cours", Toast.LENGTH_LONG).show();
                 binding.btnAfClientSup.setEnabled(true);
             }

        }));


    }


    /**
     * affiche la liste des accounts en cours d'un client
     */
    public void afficherListeAccounts(){

        binding.afClientListeAccounts.setOnClickListener(view -> {

            id = R.id.af_client_liste_accounts;
            accountcontrolleur.listeAccountsClient(client);
            Intent intent = new Intent(AfficherclientActivity.this, AfficherCreditsClientActivity.class);
            intent.putExtra("fragmentid",id);
            intent.putExtra("titre","accounts en cour");
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
                    versementacccontrolleur.listeversementsClient(client);
            id = R.id.af_client_liste_versm_accounts;
            Intent intent = new Intent(AfficherclientActivity.this, AfficherCreditsClientActivity.class);
            intent.putExtra("fragmentid",id);
            intent.putExtra("titre","versements d'accounts");
            startActivity(intent);

        });

    }

    public void afficherListeAccountsoldes(){

        binding.afClientListeHistoAccounts.setOnClickListener(view -> {

            id = R.id.af_client_liste_histo_accounts;
            accountcontrolleur.listeAccountsoldeClient(client);
            Intent intent = new Intent(AfficherclientActivity.this, AfficherCreditsClientActivity.class);
            intent.putExtra("fragmentid",id);
            intent.putExtra("titre","accounts soldés");
            startActivity(intent);

        });
    }



    public void ajouterAcount(){
        binding.afClientAjouterAccount.setOnClickListener(view -> {
            Intent intent = new Intent(AfficherclientActivity.this,AjouterAccountActivity.class);
            startActivity(intent);
        });
    }


    /**
     * affiche la liste des credits en cours d'un client
     */
    public void afficherListeCredits(){

        binding.afClientListeCredits.setOnClickListener(view -> {
            int admenu = 1;
            creditcontrolleur.setIdmenu(admenu);
            id = R.id.af_client_liste_credits;
            creditcontrolleur.listecreditsclient(client);
            Intent intent = new Intent(AfficherclientActivity.this, AfficherCreditsClientActivity.class);
            intent.putExtra("fragmentid",id);
            intent.putExtra("titre","liste de credits en cour");
            startActivity(intent);

        });
    }


    /**
     * affiche le formulaire pour faire un versement d'un credit
     */
    public void afficherAjouterVersement(){

        binding.afClientTextVersements.setOnClickListener(view -> {

            id = R.id.af_client_text_versements;

            Intent intent = new Intent(AfficherclientActivity.this, AfficherCreditsClientActivity.class);
            intent.putExtra("fragmentid",id);
            intent.putExtra("titre","versement credit");
            startActivity(intent);
        });

    }


    /**
     * affiche la liste des versements des credits d'un client
     */
    public void afficherListeVersement(){

        binding.afClientListeVersements.setOnClickListener(view -> {
            versementcontrolleur.listeversements();
            ArrayList<VersementsModel> liste_versements =  versementViewModel.getVersementsClient(client);
            versementViewModel.getMversements().setValue(liste_versements);
            id = R.id.af_client_liste_versements;

            Intent intent = new Intent(AfficherclientActivity.this, AfficherCreditsClientActivity.class);
            intent.putExtra("fragmentid",id);
            intent.putExtra("titre","liste des versements");
            startActivity(intent);

        });

    }

    public void afficherListeCreditsoldes(){

        binding.afClientListeHistoCredits.setOnClickListener(view -> {
            int admenu = 0;
            creditcontrolleur.setIdmenu(admenu);
            id = R.id.af_client_liste_histo_credits;
            creditcontrolleur.listecreditsSoldesclient(client);
            Intent intent = new Intent(AfficherclientActivity.this, AfficherCreditsClientActivity.class);
            intent.putExtra("fragmentid",id);
            intent.putExtra("titre","liste des credits soldés");
            startActivity(intent);

        });
    }

    public void ajouterCredit(){
        binding.afClientAjouterCredit.setOnClickListener(view -> {

            Intent intent = new Intent(AfficherclientActivity.this,AjouterCreditActivity.class);
            startActivity(intent);
        });
    }


    public void redirectToModifiercreditActivity(CreditModel credit){
        creditcontrolleur.setCredit(credit);
        Intent intent = new Intent(this, ModifiercreditActivity.class);
        intent.putExtra("Tagx",1);
        startActivity(intent);
    }


    public void annullerCredit(CreditModel credit){
        ClientModel client = credit.getClient();

        if (credit.getReste() > 0) {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("anuller un credit");
            builder.setMessage("êtes vous sûre de vouloir annuller le credit"+"\n"
                    +"tous les versements associés seront également supprimés"+"\n"
                    +"l'annullation d'un credit est soumise a une pénalité allant de 1000 F à 10%"
                    +"de la somme du credit");

            builder.setPositiveButton("oui", (dialog, which) -> {
                boolean success = creditcontrolleur.annullerCredit(credit);
                clientViewModel.getClient().setValue(client);
                if (success){
                    creditcontrolleur.setRecapTresteClient(client);
                    creditcontrolleur.setRecapTcreditClient(client);

                    int total_credit_client = creditcontrolleur.getRecapTcreditClient().getValue();
                    int total_reste_client = creditcontrolleur.getRecapTresteClient().getValue();

                      String destinationAdress = "+225"+client.getTelephone();
//                    String destinationAdress = "5556";
                    String messageBody = "EXPEDITEUR : "+appKessModel.getOwner() +"\n"+"\n"
                            + client.getNom() + " "+client.getPrenoms() +"\n"
                            +"vous avez annuller le credit "+credit.getNumerocredit()+"\n"
                            +"le "+ MesOutils.convertDateToString(new Date())+"\n"
                            +"total credit : "+total_credit_client+"\n"
                            +"reste a payer : "+total_reste_client+"\n";

                    if (ActivityCompat.checkSelfPermission(this,
                            android.Manifest.permission.SEND_SMS) !=
                            PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(this,
                                new String[]{android.Manifest.permission.SEND_SMS},
                                MY_PERMISSIONS_REQUEST_SEND_SMS);
                    } else {
                        SmsnoSentModel smsnoSentModel = new SmsnoSentModel(client.getId(),messageBody);
                        smsSender.smsSendwithInnerClass(messageBody, destinationAdress,smsnoSentModel.getSmsid() );
                        smsSender.sentReiceiver(smsnoSentModel);
                    }


                }

            });

            builder.setNegativeButton("non", (dialog, which) -> {

            });

            builder.create().show();

        }
    }



    public void redirectToAfficherCreditActivity(CreditModel credit) {

        creditcontrolleur.setCredit(credit);
        Intent intent = new Intent(this, AffichercreditActivity.class);
        startActivity(intent);
    }

    public void supprimerCreditSoldes(){
       ArrayList<CreditModel> listeCreditsSoldes = creditcontrolleur.listecreditsSoldesclient(client);
         long now = new Date().getTime();
       if (listeCreditsSoldes.size() != 0){
           for (CreditModel credit : listeCreditsSoldes) {
               if (MesOutils.getSppressionDate(credit.getSoldedat()) <= now){
                   creditcontrolleur.supprimeCreditSoldes(credit);
               }
           }
       }

    }

    public void supprimerAccountSoldes(){
        accountcontrolleur.listeAccountsoldeClient(client);
        ArrayList<AccountModel> listeAccountsSoldes = accountViewModel.getAccount_solde_ou_non().getValue();
        long now = new Date().getTime();

        assert listeAccountsSoldes != null;
        if (listeAccountsSoldes.size() != 0){
            for (AccountModel account : listeAccountsSoldes) {
                if (MesOutils.getSppressionDate(account.getSoldedat()) <= now ){
                    accountcontrolleur.supprimerAccountsSoldes(account);
                }
            }
        }

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