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

import com.jay.easygest.controleur.Clientcontrolleur;
import com.jay.easygest.controleur.Creditcontrolleur;
import com.jay.easygest.databinding.ActivityAjouterCreditBinding;
import com.jay.easygest.model.AppKessModel;
import com.jay.easygest.model.Article;
import com.jay.easygest.model.ArticlesModel;
import com.jay.easygest.model.ClientModel;
import com.jay.easygest.model.CreditModel;
import com.jay.easygest.model.SmsnoSentModel;
import com.jay.easygest.outils.AccessLocalAppKes;
import com.jay.easygest.outils.MesOutils;
import com.jay.easygest.outils.SessionManagement;
import com.jay.easygest.outils.SmsSender;
import com.jay.easygest.vue.ui.articles.ArticlesViewModel;
import com.jay.easygest.vue.ui.clients.ClientViewModel;
import com.jay.easygest.vue.ui.credit.CreditViewModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class AjouterCreditActivity extends AppCompatActivity {

   private SessionManagement sessionManagement;
    private ActivityAjouterCreditBinding binding;
    private Clientcontrolleur clientcontrolleur;
    private Creditcontrolleur creditcontroller;
    private CreditViewModel creditViewModel;
    private ClientViewModel clientViewModel;
    private ClientModel client;
    private  SmsSender smsSender;
    private AppKessModel appKessModel;
    private ArticlesViewModel articlesViewModel;
    private final ArrayList<ArticlesModel> listeArticles = new ArrayList<>();
    private Spinner spinnerArt1;
    private Spinner spinnerArt2;
    private ArticlesModel article1;
    private ArticlesModel article2;
//    private ArrayList<Article> liste_article_selectionne;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sessionManagement = new SessionManagement(this);
        binding = ActivityAjouterCreditBinding.inflate(getLayoutInflater());

        clientcontrolleur = Clientcontrolleur.getClientcontrolleurInstance(this);
        creditcontroller = Creditcontrolleur.getCreditcontrolleurInstance(this);

        creditViewModel = new ViewModelProvider(this).get(CreditViewModel.class);
        clientViewModel = new ViewModelProvider(this).get(ClientViewModel.class);
        articlesViewModel =  new ViewModelProvider(this).get(ArticlesViewModel.class);

        spinnerArt1 =  binding.spinnerajoutcreditArticle1;
        spinnerArt2 = binding.spinnerajoutcreditArticle2;
//        liste_article_selectionne = new ArrayList<>();
        article1 = null;
        article2 = null;

        client = clientViewModel.getClient().getValue();
        smsSender = new SmsSender(this, this);
        AccessLocalAppKes accessLocalAppKes = new AccessLocalAppKes(this);
         appKessModel = accessLocalAppKes.getAppkes();

        init();
        ajouterCredit();
        getSelectedArticle1();
        getSelectedArticle2();

        setContentView(binding.getRoot());
    }



    public  void init(){
        binding.ajoutcrednom.setText(client.getNom());
        binding.ajoutcredcodeclt.setText(client.getCodeclient());
        binding.ajoutcredprenoms.setText(client.getPrenoms());
        chargerArticlesDeBqd();
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
                onSpinner1ItemSelectedHandler(parent, position, id);
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
                onSpinner2ItemSelectedHandler(parent, position, id);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void onSpinner1ItemSelectedHandler(AdapterView<?> adapterView, int position, long id) {
        Adapter adapter = adapterView.getAdapter();
        article1 = (ArticlesModel) adapter.getItem(position);

        if (id != 0){
//            liste_article_selectionne.add(article1);
            binding.ajoutcredarticle1prix.setText(String.valueOf(article1.getPrix()));
        }
    }

    private void onSpinner2ItemSelectedHandler(AdapterView<?> adapterView, int position, long id) {
        Adapter adapter = adapterView.getAdapter();
        article2 = (ArticlesModel) adapter.getItem(position);
        if (id != 0){
            if (article1.getPrix() == 0 ){
                Toast.makeText(this, "choisisez d'abord le premier article", Toast.LENGTH_SHORT).show();
            }else {
//                liste_article_selectionne.add(article2);
                binding.ajoutcredarticle2prix.setText(String.valueOf(article2.getPrix()));
            }
        }

    }
    public void ajouterCredit(){
        binding.btnajoutcredit.setOnClickListener(v -> {
            binding.btnajoutcredit.setEnabled(false);
            String article1prix = binding.ajoutcredarticle1prix.getText().toString().trim();
            String article1qte = binding.ajoutcredNbrarticle1.getText().toString().trim();
            String article2prix  = binding.ajoutcredarticle2prix.getText().toString().trim();
            String article2qte = binding.ajoutcredNbrarticle2.getText().toString().trim();

            String versement = binding.ajoutcredversement.getText().toString().trim();
            String date = binding.ajoutcredDate.getText().toString().trim();
            if ( date.isEmpty() || versement.isEmpty())
            {
                Toast.makeText(this, "date et versement  obligatoires", Toast.LENGTH_SHORT).show();
                binding.btnajoutcredit.setEnabled(true);
            }else {
                if (article1.getDescription().equals("Choisir un article") || article1 == null ){
                    Toast.makeText(this, "premier article obligatoire", Toast.LENGTH_SHORT).show();
                    binding.btnajoutcredit.setEnabled(true);
                }else {
                    if (article1prix.isEmpty()){
                        Toast.makeText(this, "renseignez le prix du premier article", Toast.LENGTH_SHORT).show();
                        binding.btnajoutcredit.setEnabled(true);
                    }else {
                        if (article1qte.isEmpty()){
                            Toast.makeText(this, "renseignez la quantité du premier article", Toast.LENGTH_SHORT).show();
                            binding.btnajoutcredit.setEnabled(true);
                        }else {
                            if (Integer.parseInt(article1qte) > article1.getQuantite()){
                                Toast.makeText(this, "la quantité est supérieur à la quantité en stock", Toast.LENGTH_SHORT).show();
                                binding.btnajoutcredit.setEnabled(true);
                            }else {
                                if (Objects.equals(article2.getId(), article1.getId())){
                                    Toast.makeText(this, "articles identiques choisissez un autre", Toast.LENGTH_SHORT).show();
                                    binding.btnajoutcredit.setEnabled(true);
                                }else {
                                    if (!article2.getDescription().equals("Choisir un article") && article2prix.isEmpty()){
                                        Toast.makeText(this, "renseignez le prix du deuxieme article", Toast.LENGTH_SHORT).show();
                                        binding.btnajoutcredit.setEnabled(true);
                                    }else {
                                        if (!article2.getDescription().equals("Choisir un article")  && article2qte.isEmpty()){
                                            Toast.makeText(this, "renseignez la quantité du deuxieme article", Toast.LENGTH_SHORT).show();
                                            binding.btnajoutcredit.setEnabled(true);
                                        }else {
                                            if (article2.getDesignation().equals("Choisir un article") && !article2qte.isEmpty() || article2.getDesignation().equals("Choisir un article") && !article2prix.isEmpty() ){
                                                Toast.makeText(this, "vous devez choisir un article", Toast.LENGTH_SHORT).show();
                                                binding.btnajoutcredit.setEnabled(true);
                                            }else {
                                                int prixarticle2 = article2prix.isEmpty() ? 0 : Integer.parseInt(article2prix);
                                                int nbrarticle2 = article2qte.isEmpty() ? 0 : Integer.parseInt(article2qte) ;
                                                if (nbrarticle2 > article2.getQuantite()){
                                                    Toast.makeText(this, "la quantité est supérieur à la quantité en stock", Toast.LENGTH_SHORT).show();
                                                    binding.btnajoutcredit.setEnabled(true);
                                                }else {
                                                    if (MesOutils.convertStringToDate(date) == null) {
                                                        Toast.makeText(this, "format de date incorrect", Toast.LENGTH_SHORT).show();
                                                        binding.btnajoutcredit.setEnabled(true);
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
                                                                binding.btnajoutcredit.setEnabled(true);
                                                            } else {

                                                                Map< String, Object> data = new HashMap<>();
                                                                data.put("client",client);
                                                                data.put("article1vendu",article1_vendu);
                                                                data.put("article2vendu",article2_vendu);
                                                                data.put("versement",versement);
                                                                data.put("datecredit",date_credit);
                                                                data.put("article1",article1);
                                                                data.put("article2",article2);
                                                                data.put("sommecredit",sommecredit);
                                                                boolean success = creditcontroller.ajouterCredit(data);
                                                                if (success) {
                                                                    ClientModel clientModel = clientcontrolleur.recupererClient(client.getId());
                                                                    CreditModel credit_ajoute = creditViewModel.getCredit().getValue();
                                                                    CreditModel creditModel = new CreditModel(credit_ajoute.getId(),clientModel,credit_ajoute.getArticle1(),credit_ajoute.getArticle2(),credit_ajoute.getVersement(),credit_ajoute.getDatecredit(),credit_ajoute.getNumerocredit());

                                                                    creditViewModel.getCredit().setValue(creditModel);
                                                                    clientViewModel.getClient().setValue(clientModel);
                                                                    creditcontroller.setRecapTresteClient(clientModel);
                                                                    creditcontroller.setRecapTcreditClient(clientModel);

                                                                    int total_credit_client = creditcontroller.getRecapTcreditClient().getValue() != null ? creditcontroller.getRecapTcreditClient().getValue() : 0;
                                                                    int total_reste_client = creditcontroller.getRecapTresteClient().getValue() != null ? creditcontroller.getRecapTresteClient().getValue() : 0;

//                            String destinationAdress = VariablesStatique.EMULATEUR_2_TELEPHONE;

                                                                    messageSender(clientModel, creditModel, date, total_credit_client, total_reste_client);
                                                                }else {
                                                                    Toast.makeText(this, "un probleme est survenu : crédit non enregistrer", Toast.LENGTH_SHORT).show();
                                                                    binding.btnajoutcredit.setEnabled(true);
                                                                }
                                                            }
                                                        }else {
                                                            Toast.makeText(this, "versement superieur ou egal au credit", Toast.LENGTH_SHORT).show();
                                                            binding.btnajoutcredit.setEnabled(true);
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

    private void messageSender(ClientModel clientModel, CreditModel creditModel, String date, int total_credit_client, int total_reste_client) {
        String destinationAdress = "+225"+ clientModel.getTelephone();
        String messageBody = appKessModel.getOwner() +"\n"+"\n"
                + clientModel.getNom() + " "+ clientModel.getPrenoms() +"\n"
                +"vous avez pris un autre credit de "+ creditModel.getSommecredit()+" FCFA"+"\n"
                +"le "+ date +"\n"
                +"total credit "+ total_credit_client +"\n"
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
}