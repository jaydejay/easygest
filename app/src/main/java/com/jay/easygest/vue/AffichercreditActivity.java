package com.jay.easygest.vue;

import static com.jay.easygest.outils.VariablesStatique.MY_PERMISSIONS_REQUEST_SEND_SMS;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;

import com.jay.easygest.R;
import com.jay.easygest.controleur.Articlescontrolleur;
import com.jay.easygest.controleur.Clientcontrolleur;
import com.jay.easygest.controleur.Creditcontrolleur;
import com.jay.easygest.databinding.ActivityAffichercreditBinding;
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
import java.util.Date;
import java.util.Objects;

public class AffichercreditActivity extends AppCompatActivity {

//    private static final Log log = LogFactory.getLog(AffichercreditActivity.class);
    private SessionManagement sessionManagement;

   private Creditcontrolleur creditcontrolleur;
    private  TextView cardaffichercredittitle;
    private TextView cardaffichercreditarticle1;
    private TextView cardaffichercreditarticle2;
    private  TextView cardaffichercreditcredit;
    private TextView cardaffichercreditversement;
    private TextView cardaffichercreditreste;
    private  ImageButton btnmodifarticle1;
   private ImageButton btnmodifarticle2;

    private  CreditModel credit;
    private ActivityAffichercreditBinding binding;
    private ClientViewModel clientViewModel;
    private SmsSender smsSender;
    private AppKessModel appKessModel;
    private ArticlesViewModel articlesViewModel;
    private final ArrayList<ArticlesModel> listeArticles = new ArrayList<>();

    private Spinner spinnerArt;
    private EditText editModifCreditArticle;
    private  EditText editModifCreditqte;
    private  EditText editModifCreditdate;
    private ArticlesModel article;
    private Article c_article1;
    private Article c_article2;
    private CreditViewModel creditViewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sessionManagement = new SessionManagement(this);
        binding = ActivityAffichercreditBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        smsSender = new SmsSender(this, this);
        AccessLocalAppKes accessLocalAppKes = new AccessLocalAppKes(this);
        creditcontrolleur = Creditcontrolleur.getCreditcontrolleurInstance(this);
        creditViewModel = new ViewModelProvider(this).get(CreditViewModel.class);
        clientViewModel = new ViewModelProvider(this).get(ClientViewModel.class);
        articlesViewModel = new ViewModelProvider(this).get(ArticlesViewModel.class);

        creditcontrolleur.listecredits();
        appKessModel = accessLocalAppKes.getAppkes();

        cardaffichercredittitle = findViewById(R.id.cardaffichercredittitle);
        cardaffichercreditarticle1 = findViewById(R.id.cardaffichercreditarticle1);
        cardaffichercreditarticle2 = findViewById(R.id.cardaffichercreditarticle2);
        cardaffichercreditcredit = findViewById(R.id.cardaffichercreditcredit);
        cardaffichercreditreste = findViewById(R.id.cardaffichercreditreste);
        cardaffichercreditversement = findViewById(R.id.cardaffichercreditverement);
        btnmodifarticle1 = findViewById(R.id.btnmodifarticle1);
        btnmodifarticle2 = findViewById(R.id.btnmodifarticle2);



        affichercredit();
        annullerCredit();
        modifierdateCredit();
        redirectListeCredits();
        redirectAfficherClient();
        affichermodifarticle1Form();
        affichermodifarticle2Form();

    }


    public void affichercredit(){
        credit = creditViewModel.getCredit().getValue();
        assert credit != null;
        c_article1 = credit.getArticle1();
         c_article2 = credit.getArticle2();
        String article1 = "ARTICLE 1  "+c_article1.getDesignation() +"\n "+" quantite : "+c_article1.getNbrarticle()+"\n "+"somme : "+c_article1.getSomme();
        String article2 = "ARTICLE 2  "+c_article2.getDesignation() +"\n "+" quantite : "+c_article2.getNbrarticle()+"\n "+"somme : "+c_article2.getSomme();
        String credt = "CREDIT : "+credit.getSommecredit();
        String versement ="VERSEMENT : "+credit.getVersement();
        String reste ="RESTE : "+credit.getReste();

        cardaffichercredittitle.setText(credit.toString3());
        cardaffichercreditarticle1.setText(article1);
        cardaffichercreditarticle2.setText(article2);

        cardaffichercreditcredit.setText(credt);
        cardaffichercreditreste.setText(reste);
        cardaffichercreditversement.setText(versement);

        if (c_article2.getNbrarticle() == 0 ){
            binding.layoutarticle2.setVisibility(View.GONE);
            binding.articleDivider.setVisibility(View.GONE);
        }

    }


    private void affichermodifarticle1Form(){

        btnmodifarticle1.setOnClickListener(v -> {

            View view = LayoutInflater.from(AffichercreditActivity.this).inflate(R.layout.layout_modifarticle, null);
            spinnerArt = view.findViewById(R.id.spinnermodifcreditArticle);

            editModifCreditArticle = view.findViewById(R.id.editModifCreditArticleprix);
            editModifCreditqte = view.findViewById(R.id.editModifCreditqte);

            editModifCreditArticle.setText(String.valueOf(c_article1.getPrix()));
            editModifCreditqte.setText(String.valueOf(c_article1.getNbrarticle()));

            Articlescontrolleur articlescontrolleur = Articlescontrolleur.getArticlescontrolleurInstance(AffichercreditActivity.this);
            ArticlesModel articlesModel = articlescontrolleur.getArticleByDesignation(c_article1.getDesignation());

            chargerArticlesDeBqd(articlesModel);
            getSelectedArticle();
            String title = "modifier l'article 1";
            String message = "en modifiant l'article vous modifier aussi le credit" +"\n"+
                                "voulez vous pousuivre ?";
            String positiveButtonText = "oui";
            String negativeButtonText = "non";

            String nom_article_a_modifier = "article1";
            int quantite_articles_dispo = articlesModel.getQuantite() + c_article1.getNbrarticle();
            AlertDialog.Builder builder = getBuilderModifierArticleCredit(view,title,message,positiveButtonText,negativeButtonText,nom_article_a_modifier,quantite_articles_dispo) ;
            builder.create().show();
        });
    }

    private void affichermodifarticle2Form(){

        btnmodifarticle2.setOnClickListener(v -> {
            View view = LayoutInflater.from(AffichercreditActivity.this).inflate(R.layout.layout_modifarticle,null);
            spinnerArt = view.findViewById(R.id.spinnermodifcreditArticle);
            editModifCreditArticle = view.findViewById(R.id.editModifCreditArticleprix);
            editModifCreditqte = view.findViewById(R.id.editModifCreditqte);

            editModifCreditArticle.setText(String.valueOf(c_article2.getPrix()));
            editModifCreditqte.setText(String.valueOf(c_article2.getNbrarticle()));

            Articlescontrolleur articlescontrolleur = Articlescontrolleur.getArticlescontrolleurInstance(AffichercreditActivity.this);
            ArticlesModel articlesModel = articlescontrolleur.getArticleByDesignation(c_article2.getDesignation());
            chargerArticlesDeBqd(articlesModel);
            getSelectedArticle();

            String title = "modifier l'article 2";
            String message = "voulez vous modifier le credit ?";
            String positiveButtonText = "oui";
            String negativeButtonText = "non";
            String nom_article_a_modifier = "article2";
            int quantite_articles_dispo = articlesModel.getQuantite() + c_article1.getNbrarticle();
            AlertDialog.Builder builder = getBuilderModifierArticleCredit(view,title,message,positiveButtonText,negativeButtonText,nom_article_a_modifier,quantite_articles_dispo);
            builder.create().show();

        });

    }
    @NonNull
    private AlertDialog.Builder getBuilderModifierArticleCredit(View view,  String title, String message, String positiveButtonText, String negativeeButtonText, String nom_article_a_modifier,int quantite_articles_dispo) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this) ;
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setView(view);

        builder.setPositiveButton(positiveButtonText, (dialog, which)->{
           try {
               getSelectedArticle();
               String articleprix = editModifCreditArticle.getText().toString().trim() ;
               String articleqte = editModifCreditqte.getText().toString().trim() ;
               if (article.getDesignation().equals("Choisir un article") || c_article1 == null ){
                   Toast.makeText(AffichercreditActivity.this, "article obligatoire", Toast.LENGTH_SHORT).show();
               }else {
                   if (articleprix.isEmpty()){
                       Toast.makeText(AffichercreditActivity.this, "renseignez le prix de l'article", Toast.LENGTH_SHORT).show();
                   }else {
                       if (articleqte.isEmpty()){
                           Toast.makeText(AffichercreditActivity.this, "renseignez la quantité du premier article", Toast.LENGTH_SHORT).show();
                       }else {
                           int nbrarticle = Integer.parseInt(articleqte) ;
                           if (nbrarticle > quantite_articles_dispo){
                               Toast.makeText(AffichercreditActivity.this, "la quantité est supérieur à la quantité en stock", Toast.LENGTH_SHORT).show();
                           }else {
                               CreditModel creditModel = creditcontrolleur.modifierArticledunCredit(credit,articleprix,articleqte,nom_article_a_modifier,article);
                               if (creditModel != null){
                                   creditViewModel.getCredit().setValue(creditModel);
                                  MesOutils.grantSmsPermissin(this);
                                   ClientModel client = credit.getClient();
                                   messageSender(client,creditModel,MesOutils.convertDateToString(new Date()));
                                   affichercredit();
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

    private void grantSmsPermissin() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,
            new String[]{Manifest.permission.SEND_SMS},
            MY_PERMISSIONS_REQUEST_SEND_SMS);
        }
    }

    private void chargerArticlesDeBqd(ArticlesModel articleModel) {

        articlesViewModel.getLesArticleInstocklivedatascredit().observe(this, articles -> {
            listeArticles.clear();
            listeArticles.add(new ArticlesModel(0,"Choisir un article", 0,0,"Choisir un article"));

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

    /**
     * permet de modifier la date du credit
     */
    public void modifierdateCredit(){
        binding.modifierCreditDate.setOnClickListener(v -> {
            View view = LayoutInflater.from(AffichercreditActivity.this).inflate(R.layout.layout_update_credit_date, null);
            ClientModel client = this.credit.getClient();
            this.clientViewModel.getClient().setValue(client);

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("modifier la date du credit");
            builder.setMessage("vous êtes sur le point de modifier la date du credit");
            builder.setView(view);

            builder.setPositiveButton("oui", (dialog, which) -> {
                editModifCreditdate = view.findViewById(R.id.update_credit_date);
                String date = editModifCreditdate.getText().toString().trim();
                if (date.isEmpty()){
                    Toast.makeText(this, "renseignez la date", Toast.LENGTH_SHORT).show();
                }else {
                    CreditModel creditModel = creditcontrolleur.modifierDateCredit(credit,date);
                    if (creditModel != null){
                        creditViewModel.getCredit().setValue(creditModel);
                        affichercredit();
                    }
                }
            });
            builder.setNegativeButton("non", (dialog, which) -> Toast.makeText(this, "modification avortée", Toast.LENGTH_SHORT).show());
            builder.create().show();

        });

    }

    public void redirectListeCredits(){
        binding.recapListearticles.setOnClickListener(v -> {
            Intent intent = new Intent(AffichercreditActivity.this, GestionActivity.class);
            startActivity(intent);
        });
    }

    public void redirectAfficherClient(){
        binding.recapClient.setOnClickListener(v -> {
            ClientModel client = this.credit.getClient();
            this.clientViewModel.getClient().setValue(client);
            Intent intent = new Intent(AffichercreditActivity.this, AfficherclientActivity.class);
            startActivity(intent);
        });
    }

    /**
     * permet d'annuller un credit
     * le credit n'etant pas soldé
     */
    public void annullerCredit(){
        binding.supCredit.setOnClickListener(view -> {
            binding.supCredit.setEnabled(false);
            ClientModel client = this.credit.getClient();
            if (credit.getReste() > 0) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("anuller un credit");
                builder.setMessage("êtes vous sûre de vouloir annuller le credit"+"\n"
                        +"tous les versements associés seront également annullés"+"\n"
                        +"l'annullation d'un credit est soumise à une pénalité allant de 1000 F à 10%"
                        +"de la somme du credit");

                builder.setPositiveButton("oui", (dialog, which) -> {
                        this.grantSmsPermissin();
                        boolean success = creditcontrolleur.annullerCredit(credit);
                        if (success){
                            creditcontrolleur.setRecapTresteClient(client);
                            creditcontrolleur.setRecapTcreditClient(client);
                            Clientcontrolleur clientcontrolleur = Clientcontrolleur.getClientcontrolleurInstance(this);
                            ClientModel clientModel = clientcontrolleur.recupererClient(client.getId());
                            clientViewModel.getClient().setValue(clientModel);

                            int total_credit_client = creditcontrolleur.getRecapTcreditClient().getValue() != null ? creditcontrolleur.getRecapTcreditClient().getValue() : 0;
                            int total_reste_client = creditcontrolleur.getRecapTresteClient().getValue() != null ? creditcontrolleur.getRecapTresteClient().getValue() : 0;

                            String destinationAdress = "+225"+client.getTelephone();
//                       String destinationAdress = "5556";
                            String messageBody = appKessModel.getOwner() +"\n"+"\n"
                                    + client.getNom() + " "+client.getPrenoms() +"\n"
                                    +"vous avez annuller le credit "+credit.getNumerocredit()+"\n"
                                    +"le "+ MesOutils.convertDateToString(new Date())+"\n"
                                    +"total credit : "+total_credit_client+"\n"
                                    +"reste a payer : "+total_reste_client;

                            SmsnoSentModel smsnoSentModel = new SmsnoSentModel(client.getId(),messageBody);
                            smsSender.smsSendwithInnerClass(messageBody, destinationAdress,smsnoSentModel.getSmsid() );
                            smsSender.sentReiceiver(smsnoSentModel);

                        }else{
                            Intent intent = new Intent(AffichercreditActivity.this, GestionActivity.class);
                            startActivity(intent);
                        }
                });
                builder.setNegativeButton("non", (dialog, which) -> binding.supCredit.setEnabled(true));
                builder.create().show();
            }

        });


    }

    private void messageSender(ClientModel client, CreditModel creditModel,String date) {
        String messageBody = appKessModel.getOwner() +"\n"+"\n"
                + client.getNom() + " "+client.getPrenoms() +"\n"
                +"vous avez modifier le credit "+creditModel.getNumerocredit()+"\n"
                +"le "+date+"\n"
                +"ancien credit : "+credit.getSommecredit()+"\n"
                +"nouveau credit : "+creditModel.getSommecredit()+"\n"
                +"reste à payer : "+creditModel.getReste();

        SmsnoSentModel smsnoSentModel = new SmsnoSentModel(client.getId(),messageBody);
        smsSender.smsSendwithInnerClass(messageBody, client.getTelephone(),smsnoSentModel.getSmsid() );
        Intent intent = new Intent(this,AffichercreditActivity.class);
        smsSender.sentReiceiverGeneric(smsnoSentModel,intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!sessionManagement.getSession()){
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
        credit = creditViewModel.getCredit().getValue();

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