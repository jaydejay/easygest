package com.jay.easygest.vue;

import static com.jay.easygest.outils.VariablesStatique.MY_PERMISSIONS_REQUEST_SEND_SMS;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
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
import com.jay.easygest.model.ArticlesModel;
import com.jay.easygest.model.ClientModel;
import com.jay.easygest.model.SmsnoSentModel;
import com.jay.easygest.outils.AccessLocalAppKes;
import com.jay.easygest.outils.MesOutils;
import com.jay.easygest.outils.SessionManagement;
import com.jay.easygest.outils.SmsSender;
import com.jay.easygest.vue.ui.account.AccountViewModel;
import com.jay.easygest.vue.ui.articles.ArticlesViewModel;
import com.jay.easygest.vue.ui.clients.ClientViewModel;

import java.util.ArrayList;
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
    private AppKessModel appKessModel;
    private  ArticlesModel article1 = null;
    private  ArticlesModel article2 = null;
    private Spinner spinnerArt1;
    private Spinner spinnerArt2;
    private ArticlesViewModel articlesViewModel;
    private final ArrayList<ArticlesModel> listeArticles = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sessionManagement = new SessionManagement(this);

        binding = ActivityAjouterAccountBinding.inflate(getLayoutInflater());
        clientViewModel = new ViewModelProvider(this).get(ClientViewModel.class);
        clientcontrolleur = Clientcontrolleur.getClientcontrolleurInstance(this);
        accountcontroller = Accountcontroller.getAccountcontrolleurInstance(this);
        accountViewModel = new ViewModelProvider(this).get(AccountViewModel.class);
        articlesViewModel = new ViewModelProvider(this).get(ArticlesViewModel.class);
        client = clientViewModel.getClient().getValue();
        AccessLocalAppKes accessLocalAppKes = new AccessLocalAppKes(this);
        appKessModel = accessLocalAppKes.getAppkes();
        spinnerArt1 = binding.spinnerajoutaccountArticle1;
        spinnerArt2 = binding.spinnerajoutaccountArticle2;
        smsSender = new SmsSender(this, this);
        init();
        ajouterAccount();
        setContentView(binding.getRoot());
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    public  void init(){
        chargerArticlesDeBqd();
        getSelectedArticle1();
        getSelectedArticle2();
        String ref_client = client.getNom() + " " + client.getPrenoms() + " " + client.getCodeclient();
        binding.ajoutaccclientref.setText(ref_client);
    }

    private void chargerArticlesDeBqd() {
        articlesViewModel.getLesArticleInstocklivedatas2().observe(this, articles -> {
            listeArticles.addAll(articles);
            ArrayAdapter<ArticlesModel> adapter = new ArrayAdapter<>(
                    this,
                    android.R.layout.simple_spinner_item,
                    listeArticles);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerArt1.setAdapter(adapter);
            spinnerArt2.setAdapter(adapter);
        });

    }


    private void getSelectedArticle1(){

        this.spinnerArt1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                onSpinner1ItemSelectedHandler(parent, position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void getSelectedArticle2(){

        this.spinnerArt2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                onSpinner2ItemSelectedHandler(parent, position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }


    private void onSpinner1ItemSelectedHandler(AdapterView<?> adapterView, int position) {
        Adapter adapter = adapterView.getAdapter();
        article1 = (ArticlesModel) adapter.getItem(position);
        binding.ajoutaccarticle1prix.setText(String.valueOf(article1.getPrix()));
    }

    private void onSpinner2ItemSelectedHandler(AdapterView<?> adapterView, int position) {
        Adapter adapter = adapterView.getAdapter();
        article2 = (ArticlesModel) adapter.getItem(position);
        if (article1.getPrix() == 0  ){
            Toast.makeText(AjouterAccountActivity.this, "choisisez d'abord le premier article", Toast.LENGTH_SHORT).show();
        }else {
            binding.ajoutaccarticle2prix.setText(String.valueOf(article2.getPrix()));
        }
    }


    public void  ajouterAccount(){

        binding.btnajoutaccount.setOnClickListener(view -> {
            binding.btnajoutaccount.setEnabled(false);
            String article1prix = binding.ajoutaccarticle1prix.getText().toString().trim();
            String article1qte = binding.ajoutaccNbrarticle1.getText().toString().trim();
            String article2prix  = binding.ajoutaccarticle2prix.getText().toString().trim();
            String article2qte = binding.ajoutaccNbrarticle2.getText().toString().trim();

            String versement = binding.ajoutaccversement.getText().toString().trim();
            String date = binding.ajoutaccDate.getText().toString().trim();
            if ( date.isEmpty() || versement.isEmpty())
            {
                Toast.makeText(this, "date et versement  obligatoires", Toast.LENGTH_SHORT).show();
                binding.btnajoutaccount.setEnabled(true);
            }else {
                if (article1.getDescription().equals("Choisir un article") || article1 == null ){
                    Toast.makeText(this, "premier article obligatoire", Toast.LENGTH_SHORT).show();
                    binding.btnajoutaccount.setEnabled(true);
                }else {
                    if (article1prix.isEmpty()){
                        Toast.makeText(this, "renseignez le prix du premier article", Toast.LENGTH_SHORT).show();
                        binding.btnajoutaccount.setEnabled(true);
                    }else {
                        if (article1qte.isEmpty()){
                            Toast.makeText(this, "renseignez la quantité du premier article", Toast.LENGTH_SHORT).show();
                            binding.btnajoutaccount.setEnabled(true);
                        }else {
                            if (Integer.parseInt(article1qte) > article1.getQuantite()){
                                Toast.makeText(this, "la quantité est supérieur à la quantité en stock", Toast.LENGTH_SHORT).show();
                                binding.btnajoutaccount.setEnabled(true);
                            }else {
                                if (!article2.getDescription().equals("Choisir un article") && article2prix.isEmpty()){
                                    Toast.makeText(this, "renseignez le prix du deuxieme article", Toast.LENGTH_SHORT).show();
                                    binding.btnajoutaccount.setEnabled(true);
                                }else {
                                    if (!article2.getDescription().equals("Choisir un article")  && article2qte.isEmpty()){
                                        Toast.makeText(this, "renseignez la quantité du deuxieme article", Toast.LENGTH_SHORT).show();
                                        binding.btnajoutaccount.setEnabled(true);
                                    }else {
                                        if (article2.getDesignation().equals("Choisir un article") && !article2qte.isEmpty() || article2.getDesignation().equals("Choisir un article") && !article2prix.isEmpty() ){
                                            Toast.makeText(this, "vous devez choisir un article", Toast.LENGTH_SHORT).show();
                                            binding.btnajoutaccount.setEnabled(true);
                                        }else {
                                            int prixarticle2 = article2prix.isEmpty() ? 0 : Integer.parseInt(article2prix);
                                            int nbrarticle2 = article2qte.isEmpty() ? 0 : Integer.parseInt(article2qte) ;
                                            if (nbrarticle2 > article2.getQuantite()){
                                                Toast.makeText(this, "la quantité est supérieur à la quantité en stock", Toast.LENGTH_SHORT).show();
                                                binding.btnajoutaccount.setEnabled(true);
                                            }else {
                                                if (Objects.equals(article2.getId(), article1.getId())){
                                                    Toast.makeText(this, "articles identiques choisissez un autre", Toast.LENGTH_SHORT).show();
                                                    binding.btnajoutaccount.setEnabled(true);
                                                }else {
                                                    if (MesOutils.convertStringToDate(date) == null) {
                                                        Toast.makeText(this, "format de date incorrect", Toast.LENGTH_SHORT).show();
                                                        binding.btnajoutaccount.setEnabled(true);
                                                    }else {
                                                        long date_credit = MesOutils.convertStringToDate(date).getTime();
                                                        Article article2_vendu ;
                                                        Article article1_vendu = new Article(article1.getDesignation(),Integer.parseInt(article1prix),Integer.parseInt(article1qte));

                                                        article2_vendu = (article2.getDesignation() != null && !article2.getDescription().equals("Choisir un article")) ?
                                                                new Article(article2.getDesignation(),prixarticle2,nbrarticle2) :
                                                                new Article(article2.getDesignation(),0,0);

                                                        int sommecredit  = article1_vendu.getSomme() + article2_vendu.getSomme();
                                                        if (Integer.parseInt(versement) < sommecredit){
                                                            if (ActivityCompat.checkSelfPermission(this,
                                                                    Manifest.permission.SEND_SMS) !=
                                                                    PackageManager.PERMISSION_GRANTED) {
                                                                ActivityCompat.requestPermissions(this,
                                                                        new String[]{Manifest.permission.SEND_SMS},
                                                                        MY_PERMISSIONS_REQUEST_SEND_SMS);
                                                                binding.btnajoutaccount.setEnabled(true);
                                                            } else {
                                                                boolean success = accountcontroller.ajouterAccount(client,article1_vendu,article2_vendu,versement,date_credit,article1,article2);
                                                                if (success) {
                                                                    ClientModel clientModel = clientcontrolleur.recupererClient(client.getId());
                                                                    AccountModel account_ajoute = accountViewModel.getAccount().getValue();
                                                                    AccountModel accountModel = new AccountModel(account_ajoute.getId(),clientModel,account_ajoute.getArticle1(),account_ajoute.getArticle2(),account_ajoute.getVersement(),account_ajoute.getDateaccount(),account_ajoute.getNumeroaccount());
                                                                    accountViewModel.getAccount().setValue(accountModel);
                                                                    clientViewModel.getClient().setValue(clientModel);
                                                                    accountcontroller.setRecapTresteClient(clientModel);
                                                                    accountcontroller.setRecapTaccountClient(clientModel);

                                                                    int total_account_client = accountcontroller.getRecapTaccountClient().getValue() != null ? accountcontroller.getRecapTaccountClient().getValue() : 0;
                                                                    int total_reste_client = accountcontroller.getRecapTresteClient().getValue() != null ? accountcontroller.getRecapTresteClient().getValue() : 0;
//                            String destinationAdress = VariablesStatique.EMULATEUR_2_TELEPHONE;
                                                                    messageSender(clientModel, accountModel, date,total_account_client, total_reste_client);
                                                                }else {
                                                                    Toast.makeText(this, "un probleme est survenu : crédit non enregistrer", Toast.LENGTH_SHORT).show();
                                                                    binding.btnajoutaccount.setEnabled(true);
                                                                }
                                                            }
                                                        }else {
                                                            Toast.makeText(this, "versement superieur ou egal au credit", Toast.LENGTH_SHORT).show();
                                                            binding.btnajoutaccount.setEnabled(true);
                                                        }
                                                    }

                                                }
                                            }
                                        }
                                    }

                                }
                            }

                        }
                    }
                }

            }
        });
    }

    private void messageSender(ClientModel clientModel, AccountModel accountModel, String date, int total_account_client, int total_reste_client) {
        String destinationAdress = "+225"+ clientModel.getTelephone();
        String messageBody = appKessModel.getOwner() +"\n"+"\n"
                + clientModel.getNom() + " "+ clientModel.getPrenoms() +"\n"
                +"vous avez pris un autre account de "+ accountModel.getSommeaccount()+" FCFA"+"\n"
                +"le "+ date +"\n"
                +"total credit "+ total_account_client +"\n"
                +"reste à payer : "+ total_reste_client;

        SmsnoSentModel smsnoSentModel = new SmsnoSentModel(client.getId(),messageBody);
        smsSender.smsSendwithInnerClass(messageBody, destinationAdress,smsnoSentModel.getSmsid() );
        smsSender.sentReiceiver(smsnoSentModel);
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
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}