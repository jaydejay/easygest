package com.jay.easygest.vue;

import static com.jay.easygest.outils.VariablesStatique.MY_PERMISSIONS_REQUEST_SEND_SMS;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jay.easygest.R;
import com.jay.easygest.controleur.Creditcontrolleur;
import com.jay.easygest.databinding.ActivityAffichercreditBinding;
import com.jay.easygest.model.AppKessModel;
import com.jay.easygest.model.Article;
import com.jay.easygest.model.ClientModel;
import com.jay.easygest.model.CreditModel;
import com.jay.easygest.model.SmsnoSentModel;
import com.jay.easygest.outils.AccessLocalAppKes;
import com.jay.easygest.outils.MesOutils;
import com.jay.easygest.outils.SessionManagement;
import com.jay.easygest.outils.SmsSender;
import com.jay.easygest.vue.ui.clients.ClientViewModel;
import com.jay.easygest.vue.ui.credit.CreditViewModel;
import com.jay.easygest.vue.viewmodels.SmsSenderViewModel;

import java.lang.reflect.Type;
import java.util.Date;

public class AffichercreditActivity extends AppCompatActivity {

   private SessionManagement sessionManagement;

   private Creditcontrolleur creditcontrolleur;
    TextView cardaffichercredittitle;
    TextView cardaffichercreditarticle1;
    TextView cardaffichercreditarticle2;
    TextView cardaffichercreditcredit;
    TextView cardaffichercreditversement;
    TextView cardaffichercreditreste;
    private  CreditModel credit;
    private ActivityAffichercreditBinding binding;
    private ClientViewModel clientViewModel;
    private SmsSender smsSender;
    private AccessLocalAppKes accessLocalAppKes;
    private AppKessModel appKessModel;
    private SmsSenderViewModel smsSenderViewModel;
    private Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sessionManagement = new SessionManagement(this);
        binding = ActivityAffichercreditBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        smsSender = new SmsSender(this, this);
        accessLocalAppKes = new AccessLocalAppKes(this);
        creditcontrolleur = Creditcontrolleur.getCreditcontrolleurInstance(this);
        CreditViewModel creditViewModel = new ViewModelProvider(this).get(CreditViewModel.class);
        clientViewModel = new ViewModelProvider(this).get(ClientViewModel.class);
        smsSenderViewModel = new ViewModelProvider(this).get(SmsSenderViewModel.class);
        gson = new Gson();
        credit = creditViewModel.getCredit().getValue();
        creditcontrolleur.listecredits();
        appKessModel = accessLocalAppKes.getAppkes();

        cardaffichercredittitle = findViewById(R.id.cardaffichercredittitle);
        cardaffichercreditarticle1 = findViewById(R.id.cardaffichercreditarticle1);
        cardaffichercreditarticle2 = findViewById(R.id.cardaffichercreditarticle2);
        cardaffichercreditcredit = findViewById(R.id.cardaffichercreditcredit);
        cardaffichercreditreste = findViewById(R.id.cardaffichercreditreste);
        cardaffichercreditversement = findViewById(R.id.cardaffichercreditverement);

        affichercredit();
        annullerCredit();
        redirectModifierCreditActivity();
        redirectListeCredits();
        redirectAfficherClient();
    }


    public void affichercredit(){
//        Articles c_article1 = new Genson().deserialize(credit.getArticle1(), Articles.class);
//        Articles c_article2 = new Genson().deserialize(credit.getArticle2(), Articles.class);
        Type type = new TypeToken<Article>(){}.getType();
        Article c_article1 = gson.fromJson(credit.getArticle1(),type);
        Article c_article2 = gson.fromJson(credit.getArticle2(),type);
        String article1 = "ARTICLE 1  "+c_article1.getDesignation() +"\n "+" quantite : "+c_article1.getNbrarticle()+"\n "+"somme : "+c_article1.getSomme();
        String article2 = "ARTICLE 2  "+c_article2.getDesignation() +"\n "+" quantite : "+c_article2.getNbrarticle()+"\n "+"somme : "+c_article2.getSomme();
        String credt = "CREDIT : "+credit.getSommecredit();
        String versement ="VERSEMENT : "+credit.getVersement();
        String reste ="RESTE : "+credit.getReste();

        if (c_article2.getDesignation().length() == 0){
            cardaffichercreditarticle2.setVisibility(View.GONE);
        }
        cardaffichercredittitle.setText(credit.toString3());
        cardaffichercreditarticle1.setText(article1);
        cardaffichercreditarticle2.setText(article2);

        cardaffichercreditcredit.setText(credt);
        cardaffichercreditreste.setText(reste);
        cardaffichercreditversement.setText(versement);
    }

    public void redirectModifierCreditActivity(){

        binding.modifierCredit.setOnClickListener(v -> {

            ClientModel client = this.credit.getClient();
            this.clientViewModel.getClient().setValue(client);
            Intent intent = new Intent(AffichercreditActivity.this, ModifiercreditActivity.class);

            startActivity(intent);

        });

    }

    public void redirectListeCredits(){

        binding.recapListecredits.setOnClickListener(v -> {

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
                    if (ActivityCompat.checkSelfPermission(this,
                            android.Manifest.permission.SEND_SMS) !=
                            PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(this,
                                new String[]{android.Manifest.permission.SEND_SMS},
                                MY_PERMISSIONS_REQUEST_SEND_SMS);
                        binding.supCredit.setEnabled(true);
                    } else {

                        boolean success = creditcontrolleur.annullerCredit(credit);
                        if (success){
                            creditcontrolleur.setRecapTresteClient(client);
                            creditcontrolleur.setRecapTcreditClient(client);

                            int total_credit_client = creditcontrolleur.getRecapTcreditClient().getValue();
                            int total_reste_client = creditcontrolleur.getRecapTresteClient().getValue();

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
                    }
                });
                builder.setNegativeButton("non", (dialog, which) -> {
                    binding.supCredit.setEnabled(true);
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