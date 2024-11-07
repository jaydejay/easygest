package com.jay.easygest.vue;

import static com.jay.easygest.outils.VariablesStatique.MY_PERMISSIONS_REQUEST_SEND_SMS;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;

import com.jay.easygest.controleur.Accountcontroller;
import com.jay.easygest.controleur.Clientcontrolleur;
import com.jay.easygest.databinding.ActivityModifierAccountBinding;
import com.jay.easygest.model.AccountModel;
import com.jay.easygest.model.AppKessModel;
import com.jay.easygest.model.Articles;
import com.jay.easygest.model.ClientModel;
import com.jay.easygest.model.SmsnoSentModel;
import com.jay.easygest.outils.AccessLocalAppKes;
import com.jay.easygest.outils.MesOutils;
import com.jay.easygest.outils.SessionManagement;
import com.jay.easygest.outils.SmsSender;
import com.jay.easygest.vue.ui.account.AccountViewModel;
import com.jay.easygest.vue.ui.clients.ClientViewModel;
import com.owlike.genson.Genson;

import java.util.Date;
import java.util.Objects;

public class ModifierAccountActivity extends AppCompatActivity {

    private SessionManagement sessionManagement;
    private ActivityModifierAccountBinding binding;
    private AccountViewModel accountViewModel;
    private Accountcontroller accountcontroller;
    private Clientcontrolleur clientcontrolleur;
    private ClientViewModel clientViewModel;
    private AccountModel account;
    private ClientModel client;
    private AccessLocalAppKes accessLocalAppKes;
    private SmsSender smsSender;
    private AppKessModel appKessModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sessionManagement = new SessionManagement(this);
        binding = ActivityModifierAccountBinding.inflate(getLayoutInflater());
        accountViewModel= new ViewModelProvider(this).get(AccountViewModel.class);
        clientViewModel= new ViewModelProvider(this).get(ClientViewModel.class);
        client = clientViewModel.getClient().getValue();
        account = accountViewModel.getAccount().getValue();
        accountcontroller = Accountcontroller.getAccountcontrolleurInstance(this);
        clientcontrolleur = Clientcontrolleur.getClientcontrolleurInstance(this);

        accessLocalAppKes = new AccessLocalAppKes(this);
        smsSender = new SmsSender(this,this);
        appKessModel = accessLocalAppKes.getAppkes();
        afficherAccount();
        modifierAccount();
        setContentView(binding.getRoot());
    }

    public void afficherAccount(){
        Articles article1 = new Genson().deserialize(account.getArticle1(), Articles.class);
        Articles article2 = new Genson().deserialize(account.getArticle2(), Articles.class);
        binding.modifaccrnom.setText(client.getNom());
        binding.modifaccprenoms.setText(client.getPrenoms());
        binding.modifacccodeclt.setText(client.getCodeclient());

        binding.modifaccarticle1.setText(article1.getDesignation());
        binding.modifaccarticle1somme.setText(String.valueOf(article1.getPrix()));
        binding.modifaccNbrarticle1.setText(String.valueOf(article1.getNbrarticle()));

        binding.modifaccarticle2.setText(article2.getDesignation());
        binding.modifaccarticle2somme.setText(String.valueOf(article2.getPrix()));
        binding.modifaccNbrarticle2.setText(String.valueOf(article2.getNbrarticle()));

        binding.modifaccDate.setText(MesOutils.convertDateToString(new Date(account.getDateaccount())));
    }

    public void modifierAccount(){
        binding.btnmodifaccount.setOnClickListener(view -> {
            binding.btnmodifaccount.setEnabled(false);
            String designationarticle1 = binding.modifaccarticle1.getText().toString().trim();
            String article1somme = binding.modifaccarticle1somme.getText().toString().trim();
            String article1qte = binding.modifaccNbrarticle1.getText().toString().trim();
            String date = binding.modifaccDate.getText().toString().trim();
            Date date_credit = MesOutils.convertStringToDate(date);

            if ( designationarticle1.isEmpty() || article1somme.isEmpty() ||article1qte.isEmpty()
                    ||date.isEmpty() )
            {
                Toast.makeText(ModifierAccountActivity.this, "remplissez les champs obligatoires", Toast.LENGTH_SHORT).show();
                binding.btnmodifaccount.setEnabled(true);
            } else if (date_credit == null) {
                Toast.makeText(ModifierAccountActivity.this, "format de date incorrect", Toast.LENGTH_SHORT).show();
                binding.btnmodifaccount.setEnabled(true);
            }else if (binding.modifaccarticle2.getText().toString().trim().length() != 0 && binding.modifaccarticle2somme.getText().toString().trim().isEmpty() ||
                    binding.modifaccarticle2.getText().toString().trim().length() != 0 & binding.modifaccNbrarticle2.getText().toString().trim().isEmpty()) {
                Toast.makeText(ModifierAccountActivity.this, "renseigner le nombre ou le prix du deuxieme article", Toast.LENGTH_SHORT).show();
                binding.btnmodifaccount.setEnabled(true);
            } else {

                if (ActivityCompat.checkSelfPermission(this,
                        android.Manifest.permission.SEND_SMS) !=
                        PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this,
                            new String[]{android.Manifest.permission.SEND_SMS},
                            MY_PERMISSIONS_REQUEST_SEND_SMS);
                    binding.btnmodifaccount.setEnabled(true);
                } else {

                    int sommearticle1 =Integer.parseInt(article1somme) ;
                    int nbrarticle1 = Integer.parseInt(article1qte);

                    String designation_article2 ;
                    String designationarticle2 ;
                    int sommearticle2 ;
                    int nbrarticle2 ;
                    String somme_article2 ;
                    String nbr_article2 ;

                    if (binding.modifaccarticle2.getText().toString().trim().length() != 0 && binding.modifaccarticle2somme.getText().toString().trim().equals("0") ||
                            binding.modifaccarticle2.getText().toString().trim().length() != 0 & binding.modifaccNbrarticle2.getText().toString().trim().equals("0")){
                        designation_article2 = "";
                        somme_article2 = "0";
                        nbr_article2 = "0";
                    }else if (binding.modifaccarticle2.getText().toString().trim().isEmpty()){
                        designation_article2 = binding.modifaccarticle2.getText().toString().trim();
                        somme_article2 = "0";
                        nbr_article2 = "0";
                    }else {
                        designation_article2 = binding.modifaccarticle2.getText().toString().trim();
                        somme_article2 = binding.modifaccarticle2somme.getText().toString().trim();
                        nbr_article2 = binding.modifaccNbrarticle2.getText().toString().trim();
                    }
                    designationarticle2 = designation_article2;
                    sommearticle2 = Integer.parseInt(somme_article2);
                    nbrarticle2 = Integer.parseInt(nbr_article2);
                    long dateaccount = date_credit.getTime();

                    Articles c_article1 = new Articles(designationarticle1, sommearticle1,nbrarticle1);
                    Articles c_article2 =  new Articles(designationarticle2, sommearticle2,nbrarticle2);
                    int sommeaccount = c_article1.getSomme() + c_article2.getSomme();

                    int versement;
                    if (account.getVersement() < sommeaccount){
                        versement = account.getVersement();
                    }else {
                        versement = sommeaccount;
                    }
                    int reste = sommeaccount - versement;
                    String article1 = new Genson().serialize(c_article1);
                    String article2 = new Genson().serialize(c_article2);

                    AccountModel nouveau_account = new AccountModel(account.getId(),client.getId(),article1,article2,sommeaccount,versement,reste,dateaccount,account.getNumeroaccount());
                    int ancienne_somme_account = account.getSommeaccount();
                    boolean success = accountcontroller.modifierAccount(nouveau_account, client,ancienne_somme_account);
                    if (success) {
                        ClientModel clientModel = clientcontrolleur.recupererClient(client.getId());
                        AccountModel account_modifier = accountViewModel.getAccount().getValue();
                        AccountModel accountModel = new AccountModel(Objects.requireNonNull(account_modifier).getId(),clientModel,account_modifier.getArticle1(),account_modifier.getArticle2(),account_modifier.getSommeaccount(),account_modifier.getVersement(),account_modifier.getReste(),account_modifier.getDateaccount(),account_modifier.getNumeroaccount());
                        accountViewModel.getAccount().setValue(accountModel);
                        clientViewModel.getClient().setValue(clientModel);

                        if (sommeaccount != ancienne_somme_account){
                            accountcontroller.setRecapTresteClient(clientModel);
                            accountcontroller.setRecapTaccountClient(clientModel);
                            int total_account_client = accountViewModel.getTotalaccountsclient().getValue();
                            int total_reste_client = accountViewModel.getTotalrestesclient().getValue();

                            String destinationAdress = "+225"+clientModel.getTelephone();
//                            String destinationAdress = "5556";

                            String messageBody = appKessModel.getOwner() +"\n"+"\n"
                                    + clientModel.getNom() + " "+clientModel.getPrenoms() +"\n"
                                    +"vous avez modifier l'account "+account_modifier.getNumeroaccount()+"\n"
                                    +"le "+MesOutils.convertDateToString(new Date())+"\n"
                                    +"ancien account : "+account.getSommeaccount()+"\n"
                                    +"nouveau account : "+account_modifier.getSommeaccount()+"\n"
                                    +"reste a payer : "+total_reste_client;

                            SmsnoSentModel smsnoSentModel = new SmsnoSentModel(clientModel.getId(),messageBody);
                            smsSender.smsSendwithInnerClass(messageBody, destinationAdress,account.getId() );
                            smsSender.sentReiceiver(smsnoSentModel);


                        }else {
                            Intent intent = new Intent(ModifierAccountActivity.this, AffichercreditActivity.class);
                            startActivity(intent);
                        }


                    } else {
                        Toast.makeText(this, "un probleme est survenu : modification avortée", Toast.LENGTH_SHORT).show();
                        binding.btnmodifaccount.setEnabled(true);
                    }
                }

            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (!sessionManagement.getSession()){
            Intent intent = new Intent(this, MainActivity.class);
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
//            finish();
        }

//        smsSender.sentReiceiver();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        sessionManagement.removeSession();
    }


}