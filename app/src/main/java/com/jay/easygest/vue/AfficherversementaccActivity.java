package com.jay.easygest.vue;

import static com.jay.easygest.outils.VariablesStatique.MY_PERMISSIONS_REQUEST_SEND_SMS;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;

import com.jay.easygest.R;
import com.jay.easygest.controleur.Accountcontroller;
import com.jay.easygest.controleur.Clientcontrolleur;
import com.jay.easygest.controleur.Versementacccontrolleur;
import com.jay.easygest.databinding.ActivityAfficherversementaccBinding;
import com.jay.easygest.model.AccountModel;
import com.jay.easygest.model.AppKessModel;
import com.jay.easygest.model.ClientModel;
import com.jay.easygest.model.SmsnoSentModel;
import com.jay.easygest.model.VersementsaccModel;
import com.jay.easygest.outils.AccessLocalAppKes;
import com.jay.easygest.outils.MesOutils;
import com.jay.easygest.outils.SessionManagement;
import com.jay.easygest.outils.SmsSender;
import com.jay.easygest.vue.ui.account.AccountViewModel;
import com.jay.easygest.vue.ui.clients.ClientViewModel;
import com.jay.easygest.vue.ui.versementacc.VersementaccViewModel;

import java.util.Date;
import java.util.Objects;

public class AfficherversementaccActivity extends AppCompatActivity {

    SessionManagement sessionManagement;
    private AccessLocalAppKes accessLocalAppKes;
    private SmsSender smsSender;
    private ActivityAfficherversementaccBinding binding;
    private Versementacccontrolleur versementacccontrolleur;
    private Clientcontrolleur clientcontrolleur;
    private Accountcontroller accountcontroller;
    private ClientViewModel clientViewModel;
    private AccountViewModel accountViewModel;
    private AppKessModel appKessModel;
    private VersementsaccModel versement;
    private int position_versement;
    private int nbr_versement;
    private AccountModel account;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sessionManagement = new SessionManagement(this);
        smsSender = new SmsSender(this,this);
        accessLocalAppKes = new AccessLocalAppKes(this);
        binding = ActivityAfficherversementaccBinding.inflate(getLayoutInflater());
        versementacccontrolleur = Versementacccontrolleur.getVersementacccontrolleurInstance(this);
        clientcontrolleur = Clientcontrolleur.getClientcontrolleurInstance(this);
        accountcontroller = Accountcontroller.getAccountcontrolleurInstance(this);

        VersementaccViewModel versementaccViewModel = new ViewModelProvider(this).get(VersementaccViewModel.class);
        clientViewModel = new ViewModelProvider(this).get(ClientViewModel.class);
        accountViewModel = new ViewModelProvider(this).get(AccountViewModel.class);

        setContentView(binding.getRoot());

        versement = versementaccViewModel.getMversementacc().getValue();
        account = Objects.requireNonNull(versement).getAccount();
        position_versement = getIntent().getIntExtra("versementaccposition",0);
        nbr_versement = getIntent().getIntExtra("nbrversementacc",1);
        setContentView(binding.getRoot());
        afficherVersement();
        annullerversement();
        modifierVersement();
        desactiverModifAndCancel();
        redirectToClient();
        redirectTolisteCredits();

    }

    public void afficherVersement(){

        ClientModel client = versement.getClient();
        String texte1 = client.getNom() + " " + client.getPrenoms()+" "+client.getCodeclient() ;
        String date = "Date : "+ MesOutils.convertDateToString(new Date(versement.getDateversement()));
        String texte2 = "Somme : "+versement.getSommeverse();
        String texte3 = "Account numero : " + versement.getAccount().getNumeroaccount();

        binding.textViewAfVersmaccClient.setText(texte1);
        binding.textViewAfVersmaccDate.setText(date);
        binding.textViewAfVersmaccSomme.setText(texte2);
        binding.textViewAfVersmaccNumeroCredit.setText(texte3);

        binding.afVersmaccToCredits.setText(getResources().getString(R.string.liste_credit));
        binding.afVersmaccToClient.setText(R.string.le_client);

        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setTitle("versement account infos");
        }

    }
    private void desactiverModifAndCancel(){
        if (position_versement != nbr_versement - 1){
            binding.afficheVersaccModifButton.setVisibility(View.GONE);
            binding.afficheVersaccCancelButton.setVisibility(View.GONE);
        }
    }

    public void modifierVersement(){
        binding.afficheVersaccModifButton.setOnClickListener(v->{

            Intent intent = new Intent(this, ModifierVersementaccActivity.class);
            startActivity(intent);

        });

    }

    public void annullerversement(){
        binding.afficheVersaccCancelButton.setOnClickListener(v->{

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("annuller versement");
            builder.setMessage("êtes vous sûre de vouloir annuller le versement"+"\n");
            builder.setPositiveButton(
                    "oui",(dialog,which)->{
                        boolean success =  versementacccontrolleur.annullerversement(versement,account);
                        if (success){

                            ClientModel clientModel = clientcontrolleur.recupererClient(versement.getClient().getId());
                            appKessModel = accessLocalAppKes.getAppkes();
                            clientViewModel.getClient().setValue(clientModel);
                            accountcontroller.setRecapTresteClient(clientModel);
                            accountcontroller.setRecapTaccountClient(clientModel);

                            int total_account_client = accountViewModel.getTotalaccountsclient().getValue();
                            int total_reste_client = accountViewModel.getTotalrestesclient().getValue();

                              String destinationAdress = "+225"+clientModel.getTelephone();
//                            String destinationAdress = "5556";
                            String messageBody = appKessModel.getOwner() +"\n"+"\n"
                                + clientModel.getNom() + " "+clientModel.getPrenoms() +"\n"
                                +"vous avez annuller le versement de "+versement.getSommeverse()+" de votre account"+"\n"
                                +"le "+ MesOutils.convertDateToString(new Date())+"\n"
                                +"total account : "+total_account_client+"\n"
                                +"reste a payer : "+total_reste_client;

                            SmsnoSentModel smsnoSentModel = new SmsnoSentModel(clientModel.getId(),messageBody);

                            if (ActivityCompat.checkSelfPermission(this,
                                    android.Manifest.permission.SEND_SMS) !=
                                    PackageManager.PERMISSION_GRANTED) {
                                ActivityCompat.requestPermissions(this,
                                        new String[]{android.Manifest.permission.SEND_SMS},
                                        MY_PERMISSIONS_REQUEST_SEND_SMS);
                            } else {

                                smsSender.smsSendwithInnerClass(messageBody, destinationAdress,account.getId() );
                                smsSender.sentReiceiver(smsnoSentModel);
                            }

                        }

                    }
            );
            builder.setNegativeButton("non", (dialog, which) -> {

            });

            builder.create().show();


        });

    }


    public void redirectTolisteCredits(){
        binding.afVersmaccToCredits.setOnClickListener(view -> {

            Intent intent = new Intent(this, GestionActivity.class);
            startActivity(intent);
        });
    }


    public void redirectToClient(){
        binding.afVersmaccToClient.setOnClickListener(view -> {
            ClientModel clientModel = clientcontrolleur.recupererClient(versement.getClient().getId());
            clientViewModel.getClient().setValue(clientModel);
            Intent intent = new Intent(this, AfficherclientActivity.class);
            startActivity(intent);
        });
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}