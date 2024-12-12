package com.jay.easygest.vue;

import static com.jay.easygest.outils.VariablesStatique.MY_PERMISSIONS_REQUEST_SEND_SMS;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;

import com.jay.easygest.controleur.Accountcontroller;
import com.jay.easygest.controleur.Clientcontrolleur;
import com.jay.easygest.databinding.ActivityAjouterAccountBinding;
import com.jay.easygest.model.AccountModel;
import com.jay.easygest.model.AppKessModel;
import com.jay.easygest.model.Article;
import com.jay.easygest.model.ClientModel;
import com.jay.easygest.model.SmsnoSentModel;
import com.jay.easygest.outils.AccessLocalAppKes;
import com.jay.easygest.outils.MesOutils;
import com.jay.easygest.outils.SessionManagement;
import com.jay.easygest.outils.SmsSender;
import com.jay.easygest.vue.ui.account.AccountViewModel;
import com.jay.easygest.vue.ui.clients.ClientViewModel;
import com.jay.easygest.vue.viewmodels.SmsSenderViewModel;

import java.util.Date;
import java.util.Objects;

public class AjouterAccountActivity extends AppCompatActivity {

    private SessionManagement sessionManagement;
    private ActivityAjouterAccountBinding binding;
    private ClientViewModel clientViewModel;
    private AccountViewModel accountViewModel;
    private Accountcontroller accountcontroller;
    private Clientcontrolleur clientcontrolleur;
    private ClientModel client;
    private SmsSender smsSender;
    private AccessLocalAppKes accessLocalAppKes;
    private AppKessModel appKessModel;
    private SmsSenderViewModel smsSenderViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sessionManagement = new SessionManagement(this);

        binding = ActivityAjouterAccountBinding.inflate(getLayoutInflater());
        clientViewModel = new ViewModelProvider(this).get(ClientViewModel.class);
        clientcontrolleur = Clientcontrolleur.getClientcontrolleurInstance(this);
        accountcontroller = Accountcontroller.getAccountcontrolleurInstance(this);
        accountViewModel = new ViewModelProvider(this).get(AccountViewModel.class);
        client = clientViewModel.getClient().getValue();
        accessLocalAppKes = new AccessLocalAppKes(this);
        smsSender = new SmsSender(this, this);
        smsSenderViewModel = new ViewModelProvider(this).get(SmsSenderViewModel.class);
        init();
        ajouterAccount();
        setContentView(binding.getRoot());

    }

    public  void init(){
        binding.ajoutaccnom.setText(client.getNom());
        binding.ajoutacccodeclt.setText(client.getCodeclient());
        binding.ajoutaccprenoms.setText(client.getPrenoms());
    }


    public void  ajouterAccount(){

        binding.btnajoutaccount.setOnClickListener(view -> {
            binding.btnajoutaccount.setEnabled(false);
            String designationarticle1 = binding.ajoutaccarticle1.getText().toString().trim();
            String article1somme = binding.ajoutaccarticle1somme.getText().toString().trim();
            String article1qte = binding.ajoutaccNbrarticle1.getText().toString().trim();

            String designationarticle2 = binding.ajoutaccarticle2.getText().toString().trim();
            String article2somme = binding.ajoutaccarticle2somme.getText().toString().trim();
            String article2qte = binding.ajoutaccNbrarticle2.getText().toString().trim();

            String date = binding.ajoutaccDate.getText().toString().trim();
            String versement = binding.ajoutaccversement.getText().toString().trim();
            Date date_account = MesOutils.convertStringToDate(date);

            if ( designationarticle1.isEmpty() || article1somme.isEmpty() ||article1qte.isEmpty()
                    || versement.isEmpty() ||date.isEmpty() )
            {
                Toast.makeText(AjouterAccountActivity.this, "remplissez les champs obligatoires", Toast.LENGTH_SHORT).show();
                binding.btnajoutaccount.setEnabled(true);

            } else if (date_account == null) {

                Toast.makeText(AjouterAccountActivity.this, "format de date incorrect", Toast.LENGTH_SHORT).show();
                binding.btnajoutaccount.setEnabled(true);
            }else if (binding.ajoutaccarticle2.getText().toString().trim().length() != 0 && binding.ajoutaccarticle2somme.getText().toString().trim().isEmpty() ||
                    binding.ajoutaccarticle2.getText().toString().trim().length() != 0 & binding.ajoutaccNbrarticle2.getText().toString().trim().isEmpty()) {
                Toast.makeText(AjouterAccountActivity.this, "renseigner le nombre ou le prix du deuxieme article", Toast.LENGTH_SHORT).show();
                binding.btnajoutaccount.setEnabled(true);
            } else {
                int sommearticle1 =Integer.parseInt(article1somme) ;
                int nbrarticle1 = Integer.parseInt(article1qte);
                int sommearticle2 = 0 ;
                int nbrarticle2 = 0 ;
                if (!designationarticle2.isEmpty()){
                    sommearticle2 =Integer.parseInt(article2somme);
                    nbrarticle2 = Integer.parseInt(article2qte);
                }

                long dateaccount = date_account.getTime();

                Article c_article1 = new Article(designationarticle1, sommearticle1,nbrarticle1);
                Article c_article2 =  new Article(designationarticle2, sommearticle2,nbrarticle2);
                int sommecredit  = c_article1.getSomme() + c_article2.getSomme();

                if (Integer.parseInt(versement) < sommecredit){
                    if (ActivityCompat.checkSelfPermission(this,
                            android.Manifest.permission.SEND_SMS) !=
                            PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(this,
                                new String[]{android.Manifest.permission.SEND_SMS},
                                MY_PERMISSIONS_REQUEST_SEND_SMS);
                        binding.btnajoutaccount.setEnabled(true);
                    } else {
                        boolean success = accountcontroller.ajouterAccount( client,c_article1,c_article2,versement, dateaccount);
                        if (success) {
                            appKessModel = accessLocalAppKes.getAppkes();
                            ClientModel clientModel = clientcontrolleur.recupererClient(client.getId());
                            AccountModel account_ajoute = accountViewModel.getAccount().getValue();
                            AccountModel accountModel = new AccountModel(Objects.requireNonNull(account_ajoute).getId(),clientModel,account_ajoute.getArticle1(),account_ajoute.getArticle2(),account_ajoute.getSommeaccount(),account_ajoute.getVersement(),account_ajoute.getReste(),account_ajoute.getDateaccount(),account_ajoute.getNumeroaccount());
                            accountViewModel.getAccount().setValue(accountModel);
                            clientViewModel.getClient().setValue(clientModel);

                            accountcontroller.setRecapTresteClient(clientModel);
                            accountcontroller.setRecapTaccountClient(clientModel);
                            int total_account_client = accountViewModel.getTotalaccountsclient().getValue();
                            int total_reste_client = accountViewModel.getTotalrestesclient().getValue();

                            String destinationAdress = "+225"+clientModel.getTelephone();
//                        String destinationAdress = "5556";

                            String messageBody = appKessModel.getOwner() +"\n"+"\n"
                                    + clientModel.getNom() + " "+clientModel.getPrenoms() +"\n"
                                    +"vous avez pris un autre account de "+accountModel.getSommeaccount()+" FCFA"+"\n"
                                    +"le "+date+"\n"
                                    +"total account "+total_account_client+"\n"
                                    +"reste à payer : "+total_reste_client;

                            SmsnoSentModel smsnoSentModel = new SmsnoSentModel(clientModel.getId(),messageBody);
                            smsSender.smsSendwithInnerClass(messageBody, destinationAdress,smsnoSentModel.getSmsid() );
                            smsSender.sentReiceiver(smsnoSentModel);

                        } else {
                            Toast.makeText(this, "un probleme est survenu : ajout avortée", Toast.LENGTH_SHORT).show();
                            binding.btnajoutaccount.setEnabled(true);
                        }

                    }

                }else {
                    Toast.makeText(this, "versement superieur ou égal à l'account", Toast.LENGTH_SHORT).show();
                    binding.btnajoutaccount.setEnabled(true);
                }

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
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        sessionManagement.removeSession();
    }


}