package com.jay.easygest.vue;

import static com.jay.easygest.outils.VariablesStatique.MY_PERMISSIONS_REQUEST_SEND_SMS;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;
import com.jay.easygest.controleur.Accountcontroller;
import com.jay.easygest.controleur.Clientcontrolleur;
import com.jay.easygest.databinding.ActivityAfficherAccountBinding;
import com.jay.easygest.model.AccountModel;
import com.jay.easygest.model.AppKessModel;
import com.jay.easygest.model.Articles;
import com.jay.easygest.model.ClientModel;
import com.jay.easygest.model.SmsnoSentModel;
import com.jay.easygest.outils.AccessLocalAppKes;
import com.jay.easygest.outils.MesOutils;
import com.jay.easygest.outils.VariablesStatique;
import com.jay.easygest.outils.SessionManagement;
import com.jay.easygest.outils.SmsSender;
import com.jay.easygest.vue.ui.account.AccountViewModel;
import com.jay.easygest.vue.ui.clients.ClientViewModel;
import com.owlike.genson.Genson;

import java.util.Date;
import java.util.Objects;

public class AfficherAccountActivity extends AppCompatActivity {

    private ActivityAfficherAccountBinding binding;
    private SessionManagement sessionManagement;
    private SmsSender smsSender;
    private AccessLocalAppKes accessLocalAppKes;
    private AppKessModel appKessModel;
    private Accountcontroller accountcontroller;
    private Clientcontrolleur clientcontrolleur;
    private ClientViewModel clientViewModel;
    private  AccountViewModel accountViewModel;
    private  AccountModel account;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAfficherAccountBinding.inflate(getLayoutInflater());
        sessionManagement = new SessionManagement(this);
        smsSender = new SmsSender(this,this);

        accessLocalAppKes = new AccessLocalAppKes(this);
        accountcontroller = Accountcontroller.getAccountcontrolleurInstance(this);
        clientcontrolleur = Clientcontrolleur.getClientcontrolleurInstance(this);

        accountViewModel = new ViewModelProvider(this).get(AccountViewModel.class);
        clientViewModel = new ViewModelProvider(this).get(ClientViewModel.class);

        account = accountViewModel.getAccount().getValue();

        afficheraccount();
        redirectToModifierAccount();
        annullerAccount();
        redirectAfficherClient();
        redirectListeCredits();
        desactiverButtum();
        setContentView(binding.getRoot());

    }

    public void afficheraccount(){

        Articles c_article1 = new Genson().deserialize(account.getArticle1(), Articles.class);
        Articles c_article2 = new Genson().deserialize(account.getArticle2(), Articles.class);
        String article1 = "ARTICLE 1  "+c_article1.getDesignation() +"\n "+" quantite : "+c_article1.getNbrarticle()+"\n "+"somme : "+c_article1.getSomme();
        String article2 = "ARTICLE 2  "+c_article2.getDesignation() +"\n "+" quantite : "+c_article2.getNbrarticle()+"\n "+"somme : "+c_article2.getSomme();
        String account1 = "ACCOUNT: "+account.getSommeaccount();
        String versement ="VERSEMENT : "+account.getVersement();
        String reste ="RESTE : "+account.getReste();
        binding.cardafficheraccounttitle.setText(account.toString3());
        binding.cardafficheraccountarticle1.setText(article1);
        binding.cardafficheraccountarticle2 .setText(article2);
        binding.cardafficheraccountaccount.setText(account1);
        binding.cardafficheraccountreste.setText(reste);
        binding.cardafficheraccountverement.setText(versement);
    }


    public void redirectListeCredits(){

        binding.recapaccListecredits.setOnClickListener(v -> {

            Intent intent = new Intent(AfficherAccountActivity.this, GestionActivity.class);
            startActivity(intent);

        });

    }

    public void redirectAfficherClient(){

        binding.recapaccClient.setOnClickListener(v -> {
            ClientModel client = this.account.getClient();
            this.clientViewModel.getClient().setValue(client);
            Intent intent = new Intent(AfficherAccountActivity.this, AfficherclientActivity.class);
            startActivity(intent);

        });




    }

    public void desactiverButtum(){
        if (account.getReste() == 0){
            binding.supAccount.setVisibility(View.GONE);
            binding.modifierAccount.setVisibility(View.GONE);
        }
    }


    public void redirectToModifierAccount(){
        binding.modifierAccount.setOnClickListener(view -> {
            Intent intent = new Intent(this,ModifierAccountActivity.class);
            startActivity(intent);
        });
    }

    public void annullerAccount(){

        binding.supAccount.setOnClickListener(view -> {
            binding.supAccount.setEnabled(false);
            if (account.getReste() > 0) {

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("anuller un account");
                builder.setMessage("êtes vous sûre de vouloir annuller l'account"+"\n"
                        +"tous les versements associés seront également annullés"+"\n"
                        +"l'annullation d'un account est soumise à une pénalité allant de 1000 F à 10%"
                        +"de la somme de l'account");

                builder.setPositiveButton("oui", (dialog, which) -> {
                    boolean success = accountcontroller.annullerAccount(account);
                    if (success){
                        ClientModel clientModel = clientcontrolleur.recupererClient(account.getClient().getId());
                        clientViewModel.getClient().setValue(clientModel);

                        appKessModel = accessLocalAppKes.getAppkes();

                        accountcontroller.setRecapTresteClient(clientModel);
                        accountcontroller.setRecapTaccountClient(clientModel);
                        int total_account_client = accountViewModel.getTotalaccountsclient().getValue();
                        int total_reste_client = accountViewModel.getTotalrestesclient().getValue();

                        String destinationAdress = "+225"+clientModel.getTelephone();
//                        String destinationAdress1 = "+225"+VariablesStatique.CLIENT_TELEPHONE;
//                        String destinationAdress = VariablesStatique.EMULATEUR_2_TELEPHONE;

                        String messageBody = appKessModel.getOwner() +"\n"+"\n"
                                + clientModel.getNom() + " "+clientModel.getPrenoms() +"\n"
                                +"vous avez annullé l'account  "+account.getNumeroaccount()+"\n"
                                +"le "+ MesOutils.convertDateToString(new Date())+"\n"
                                +"total account "+total_account_client+"\n"
                                +"reste à payer : "+total_reste_client;

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

                });

                builder.setNegativeButton("non", (dialog, which) -> {
                    binding.supAccount.setEnabled(true);
                });

                builder.create().show();

            }

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