package com.jay.easygest.vue;

import static com.jay.easygest.outils.VariablesStatique.MY_PERMISSIONS_REQUEST_SEND_SMS;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import com.google.android.material.textfield.TextInputEditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;

import com.jay.easygest.R;
import com.jay.easygest.controleur.Accountcontroller;
import com.jay.easygest.controleur.Articlescontrolleur;
import com.jay.easygest.controleur.Clientcontrolleur;
import com.jay.easygest.databinding.ActivityAfficherAccountBinding;
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
import java.util.Date;
import java.util.Objects;

public class AfficherAccountActivity extends AppCompatActivity {

    private ActivityAfficherAccountBinding binding;
    private SessionManagement sessionManagement;
    private SmsSender smsSender;
    private AppKessModel appKessModel;
    private Accountcontroller accountcontroller;
    private Clientcontrolleur clientcontrolleur;
    private ClientViewModel clientViewModel;
    private  AccountViewModel accountViewModel;
    private  AccountModel account;

    private Spinner spinnerArt;
    private TextInputEditText editModifCreditArticle;
    private TextInputEditText editModifCreditqte;
    private Article c_article1;
    private Article c_article2;
    private ArticlesModel article;
    private ArticlesViewModel articlesViewModel;
    private final ArrayList<ArticlesModel> listeArticles = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAfficherAccountBinding.inflate(getLayoutInflater());
        sessionManagement = new SessionManagement(this);
        smsSender = new SmsSender(this, this);

        AccessLocalAppKes accessLocalAppKes = new AccessLocalAppKes(this);
        accountcontroller = Accountcontroller.getAccountcontrolleurInstance(this);
        clientcontrolleur = Clientcontrolleur.getClientcontrolleurInstance(this);
        accountViewModel = new ViewModelProvider(this).get(AccountViewModel.class);
        clientViewModel = new ViewModelProvider(this).get(ClientViewModel.class);
        articlesViewModel = new ViewModelProvider(this).get(ArticlesViewModel.class);
        appKessModel = accessLocalAppKes.getAppkes();

        afficheraccount();
        modifierdateaccont();
        affichermodifarticle1Form();
        affichermodifarticle2Form();
        annullerAccount();
        redirectAfficherClient();
        redirectListeArticles();
        desactiverButtum();
        setContentView(binding.getRoot());
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

    }

    public void afficheraccount(){
        account = accountViewModel.getAccount().getValue();
        assert account != null;
        c_article1 = account.getArticle1();
         c_article2 = account.getArticle2();

        Articlescontrolleur articlescontrolleur = Articlescontrolleur.getArticlescontrolleurInstance(this);
        ArticlesModel articlesModel1 = articlescontrolleur.getArticleById(Integer.parseInt(c_article1.getDesignation()));
        ArticlesModel articlesModel2 = articlescontrolleur.getArticleById(Integer.parseInt(c_article2.getDesignation()));

        String article1 = "ARTICLE 1  "+articlesModel1.getDesignation() +"\n "+" quantite : "+c_article1.getNbrarticle()+"\n "+"somme : "+c_article1.getSomme();
        String article2 = "ARTICLE 2  "+articlesModel2.getDesignation() +"\n "+" quantite : "+c_article2.getNbrarticle()+"\n "+"somme : "+c_article2.getSomme();
        String account1 = "ACCOUNT: "+account.getSommeaccount();
        String versement ="VERSEMENT : "+account.getVersement();
        String reste ="RESTE : "+account.getReste();

        binding.cardafficheraccounttitle.setText(account.toString3());
        binding.cardafficheraccountarticle1.setText(article1);
        binding.cardafficheraccountarticle2 .setText(article2);

        binding.cardafficheraccountaccount.setText(account1);
        binding.cardafficheraccountreste.setText(reste);
        binding.cardafficheraccountverement.setText(versement);

        if (c_article2.getNbrarticle() == 0 ){
            binding.layoutaccountarticle2.setVisibility(View.GONE);
            binding.accountArticleDivider.setVisibility(View.GONE);
        }
    }

    private void affichermodifarticle1Form(){

        binding.btnmodifaccountarticle1.setOnClickListener(v -> {

            View view = LayoutInflater.from(AfficherAccountActivity.this).inflate(R.layout.layout_modifarticle, null);
            spinnerArt = view.findViewById(R.id.spinnermodifcreditArticle);

            editModifCreditArticle = view.findViewById(R.id.editModifCreditArticleprix);
            editModifCreditqte = view.findViewById(R.id.editModifCreditqte);
            editModifCreditArticle.setText(String.valueOf(c_article1.getPrix()));
            editModifCreditqte.setText(String.valueOf(c_article1.getNbrarticle()));

            Articlescontrolleur articlescontrolleur = Articlescontrolleur.getArticlescontrolleurInstance(AfficherAccountActivity.this);
//            ArticlesModel articlesModel = articlescontrolleur.getArticleByDesignation(c_article1.getDesignation());
            ArticlesModel articlesModel = articlescontrolleur.getArticleById(Integer.parseInt(c_article1.getDesignation()));


            chargerArticlesDeBqd(articlesModel);
            getSelectedArticle();
            String title = "modifier l'article 1";
            String message = "en modifiant l'article vous modifier aussi l'account" +"\n"+
                    "voulez vous pousuivre ?";
            String positiveButtonText = "oui";
            String negativeButtonText = "non";

            String article_a_modifier = "article1";
            int quantite_articles_dispo = articlesModel.getQuantite() + c_article1.getNbrarticle();
            AlertDialog.Builder builder = getBuilderModifierArticleAccount(view,title,message,positiveButtonText,negativeButtonText,article_a_modifier,quantite_articles_dispo) ;
            builder.create().show();
        });
    }

    private void affichermodifarticle2Form(){

        binding.btnmodifaccountarticle2.setOnClickListener(v -> {
            View view = LayoutInflater.from(AfficherAccountActivity.this).inflate(R.layout.layout_modifarticle,null);
            spinnerArt = view.findViewById(R.id.spinnermodifcreditArticle);
            editModifCreditArticle = view.findViewById(R.id.editModifCreditArticleprix);
            editModifCreditqte = view.findViewById(R.id.editModifCreditqte);
            editModifCreditArticle.setText(String.valueOf(c_article2.getPrix()));
            editModifCreditqte.setText(String.valueOf(c_article2.getNbrarticle()));
            Articlescontrolleur articlescontrolleur = Articlescontrolleur.getArticlescontrolleurInstance(AfficherAccountActivity.this);
            ArticlesModel articlesModel = articlescontrolleur.getArticleById(Integer.parseInt(c_article2.getDesignation()));
            chargerArticlesDeBqd(articlesModel);
            getSelectedArticle();

            String title = "modifier l'article 2";
            String message = "voulez vous modifier l'article ?";
            String positiveButtonText = "oui";
            String negativeButtonText = "non";
            String article_a_modifier = "article2";
            int quantite_articles_dispo = articlesModel.getQuantite() + c_article1.getNbrarticle();
            AlertDialog.Builder builder = getBuilderModifierArticleAccount(view,title,message,positiveButtonText,negativeButtonText,article_a_modifier,quantite_articles_dispo);
            builder.create().show();

        });

    }


    @NonNull
    private AlertDialog.Builder getBuilderModifierArticleAccount(View view, String title, String message, String positiveButtonText, String negativeeButtonText, String article_a_modifier, int quantite_articles_dispo) {


        AlertDialog.Builder builder = new AlertDialog.Builder(this) ;
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setView(view);

        builder.setPositiveButton(positiveButtonText, (dialog, which)->{
            try {
                getSelectedArticle();
                String articleprix = editModifCreditArticle.getText().toString().trim() ;
                String articleqte = editModifCreditqte.getText().toString().trim() ;
                if (article.getDesignation().equals("Choisir un article") || article.getPrix() == 0 ){
                    Toast.makeText(AfficherAccountActivity.this, "article obligatoire", Toast.LENGTH_SHORT).show();

                }else {
                    if (articleprix.isEmpty()){
                        Toast.makeText(AfficherAccountActivity.this, "renseignez le prix de l'article", Toast.LENGTH_SHORT).show();
                    }else {
                        if (articleqte.isEmpty()){
                            Toast.makeText(AfficherAccountActivity.this, "renseignez la quantité de l'article", Toast.LENGTH_SHORT).show();
                        }else {
                            int nbrarticle = Integer.parseInt(articleqte) ;
                            if (nbrarticle > quantite_articles_dispo){
                                Toast.makeText(AfficherAccountActivity.this, "la quantité est supérieur à la quantité en stock", Toast.LENGTH_SHORT).show();
                            }else {
                                AccountModel accountModel = accountcontroller.modifierArticledunAccount(account,articleprix,articleqte,article_a_modifier,article);
                                if (accountModel != null){
                                    accountViewModel.getAccount().setValue(accountModel);
                                    MesOutils.grantSmsPermission(this);
                                    ClientModel client = account.getClient();
                                    this.messageSender(client,accountModel,MesOutils.convertDateToString(new Date()));
                                }
                            }

                        }
                    }
                }
            } catch (Exception e) {
                Toast.makeText(this, "une erreur est survenue verifie les donnees", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton(negativeeButtonText, (dialog, which)-> Toast.makeText(this, "modification annuler", Toast.LENGTH_SHORT).show());
        return builder;
    }
    private void messageSender(ClientModel client, AccountModel accountModel,String date) {
        String messageBody = appKessModel.getOwner() +"\n"+"\n"
                + client.getNom() + " "+client.getPrenoms() +"\n"
                +"vous avez modifier l'account "+account.getNumeroaccount()+"\n"
                +"le "+date+"\n"
                +"ancien account : "+accountModel.getSommeaccount()+"\n"
                +"nouveau account : "+account.getSommeaccount()+"\n"
                +"reste à payer : "+accountModel.getReste();

        SmsnoSentModel smsnoSentModel = new SmsnoSentModel(client.getId(),messageBody);
        smsSender.smsSendwithInnerClass(messageBody, client.getTelephone(),smsnoSentModel.getSmsid() );
        Intent intent = new Intent(this, AfficherAccountActivity.class);
        smsSender.sentReiceiverGeneric(smsnoSentModel,intent);
    }

    private void chargerArticlesDeBqd(ArticlesModel articleModel) {
        articlesViewModel.getLesArticleInstocklivedatascredit().observe(this, articles -> {
            listeArticles.clear();
//            listeArticles.add(new ArticlesModel(0,"Choisir un article", 0,0,"Choisir un article"));
            if ( articleModel.getQuantite() == 0){
                listeArticles.add(articleModel);
            }
            listeArticles.addAll(articles);
            int article_position = -1;
            for (ArticlesModel article : listeArticles) {
                if (Objects.equals(article.getId(), articleModel.getId())){
                    article_position = listeArticles.indexOf(article);
                }
            }

            if (spinnerArt != null){
                ArrayAdapter<ArticlesModel> adapter = new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_spinner_item,
                        listeArticles);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerArt.setAdapter(adapter);
                spinnerArt.setSelection(article_position);
            }

        });

    }
    private void getSelectedArticle(){
        spinnerArt.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                onSpinner1ItemSelectedHandler(parent, position, id);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
    private void onSpinner1ItemSelectedHandler(AdapterView<?> adapterView, int position, long id) {
        Adapter adapter = adapterView.getAdapter();
        article = (ArticlesModel) adapter.getItem(position);
        if (id != 0){
            editModifCreditArticle.setText(String.valueOf(article.getPrix()));
        }
    }
    public void redirectListeArticles(){
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
            binding.modifierAccountDate.setVisibility(View.GONE);
        }
    }


    public void modifierdateaccont(){
        binding.modifierAccountDate.setOnClickListener(v -> {
            View view = LayoutInflater.from(AfficherAccountActivity.this).inflate(R.layout.layout_update_credit_date, null);
            ClientModel client = this.account.getClient();
            this.clientViewModel.getClient().setValue(client);

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("modifier la date de l'account");
            builder.setMessage("vous êtes sur le point de modifier la date de l'account");
            builder.setView(view);

            builder.setPositiveButton("oui", (dialog, which) -> {
               TextInputEditText editModifCreditdate = view.findViewById(R.id.update_credit_date);
                String date = editModifCreditdate.getText().toString().trim();
                if (date.isEmpty()){
                    Toast.makeText(this, "renseignez la date", Toast.LENGTH_SHORT).show();
                }else {
                    AccountModel accountModel = accountcontroller.modifierDateAccount(account,date);
                    if (accountModel != null){
                        accountViewModel.getAccount().setValue(accountModel);
                        afficheraccount();
                    }
                }
            });
            builder.setNegativeButton("non", (dialog, which) -> Toast.makeText(this, "modification avortée", Toast.LENGTH_SHORT).show());
            builder.create().show();

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
                    if (ActivityCompat.checkSelfPermission(this,
                            android.Manifest.permission.SEND_SMS) !=
                            PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(this,
                                new String[]{android.Manifest.permission.SEND_SMS},
                                MY_PERMISSIONS_REQUEST_SEND_SMS);
                        binding.supAccount.setEnabled(true);
                    } else {
                        boolean success = accountcontroller.annullerAccount(account);
                        if (success){
                            ClientModel clientModel = clientcontrolleur.recupererClient(account.getClient().getId());
                            clientViewModel.getClient().setValue(clientModel);
                            accountcontroller.setRecapTresteClient(clientModel);
                            accountcontroller.setRecapTaccountClient(clientModel);
                            int total_account_client = accountViewModel.getTotalaccountsclient().getValue() != null ? accountViewModel.getTotalaccountsclient().getValue() : 0;
                            int total_reste_client = accountViewModel.getTotalrestesclient().getValue() != null ? accountViewModel.getTotalrestesclient().getValue() : 0;

                            String destinationAdress = "+225"+clientModel.getTelephone();
                            String messageBody = appKessModel.getOwner() +"\n"+"\n"
                                    + clientModel.getNom() + " "+clientModel.getPrenoms() +"\n"
                                    +"vous avez annullé l'account  "+account.getNumeroaccount()+"\n"
                                    +"le "+ MesOutils.convertDateToString(new Date())+"\n"
                                    +"total account "+total_account_client+"\n"
                                    +"reste à payer : "+total_reste_client;

                            SmsnoSentModel smsnoSentModel = new SmsnoSentModel(clientModel.getId(),messageBody);
                            smsSender.smsSendwithInnerClass(messageBody, destinationAdress,smsnoSentModel.getSmsid() );
                            smsSender.sentReiceiver(smsnoSentModel);
                        }
                    }
                });

                builder.setNegativeButton("non", (dialog, which) -> binding.supAccount.setEnabled(true));
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

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


}