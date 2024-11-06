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
import com.jay.easygest.controleur.Clientcontrolleur;
import com.jay.easygest.controleur.Creditcontrolleur;
import com.jay.easygest.controleur.Versementcontrolleur;
import com.jay.easygest.databinding.ActivityAfficheversementBinding;
import com.jay.easygest.model.AppKessModel;
import com.jay.easygest.model.ClientModel;
import com.jay.easygest.model.CreditModel;
import com.jay.easygest.model.SmsnoSentModel;
import com.jay.easygest.model.VersementsModel;
import com.jay.easygest.outils.AccessLocalAppKes;
import com.jay.easygest.outils.MesOutils;
import com.jay.easygest.outils.SessionManagement;
import com.jay.easygest.outils.SmsSender;
import com.jay.easygest.vue.ui.clients.ClientViewModel;
import com.jay.easygest.vue.ui.credit.CreditViewModel;
import com.jay.easygest.vue.ui.versement.VersementViewModel;

import java.util.Date;

public class AfficheversementActivity extends AppCompatActivity {


    private SessionManagement sessionManagement;
    private AccessLocalAppKes accessLocalAppKes;
    private SmsSender smsSender;
    private ActivityAfficheversementBinding binding;
    private Versementcontrolleur versementcontrolleur;
    private Clientcontrolleur clientcontrolleur;
    private Creditcontrolleur creditcontrolleur;
    private AppKessModel appKessModel;
    private ClientViewModel clientViewModel;
    private  VersementsModel versement;
    private CreditModel credit;
    private int position_versement;
    private int nbr_versement;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sessionManagement= new SessionManagement(this);
        accessLocalAppKes = new AccessLocalAppKes(this);
        smsSender= new SmsSender(this,this);
        binding = ActivityAfficheversementBinding.inflate(getLayoutInflater());
        versementcontrolleur = Versementcontrolleur.getVersementcontrolleurInstance(this);
        clientcontrolleur =Clientcontrolleur.getClientcontrolleurInstance(this);
        creditcontrolleur = Creditcontrolleur.getCreditcontrolleurInstance(this);

        VersementViewModel versementViewModel = new ViewModelProvider(this).get(VersementViewModel.class);
        CreditViewModel creditViewModel = new ViewModelProvider(this).get(CreditViewModel.class);
        clientViewModel = new ViewModelProvider(this).get(ClientViewModel.class);

        setContentView(binding.getRoot());
        appKessModel = accessLocalAppKes.getAppkes();
        credit = creditViewModel.getCredit().getValue();
        versement = versementViewModel.getMversement().getValue();
        position_versement = getIntent().getIntExtra("versementposition",0);
        nbr_versement = getIntent().getIntExtra("nbrversement",1);

        desactiverModifAndCancel();
        afficherVersement();
        modifierVersement();
        annullerversement();
        redirectTolisteCredits();
        redirectToClient();

    }

    public void afficherVersement(){

        ClientModel client = versement.getClient();
        String texte1 = client.getNom() + " " + client.getPrenoms()+" "+client.getCodeclient() ;
        String date = "date : "+ MesOutils.convertDateToString(new Date(versement.getDateversement()));
        String texte2 = "somme : "+versement.getSommeverse();
        String texte3 = "credit numero : " + versement.getCredit().getNumerocredit();

        binding.textViewAfVersmClient.setText(texte1);
        binding.textViewAfVersmDate.setText(date);
        binding.textViewAfVersmSomme.setText(texte2);
        binding.textViewAfVersmNumeroCredit.setText(texte3);

        binding.afVersmToCredits.setText(getResources().getString(R.string.liste_credits));
        binding.afVersmToClient.setText(getResources().getString(R.string.le_client));

        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setTitle("versement infos");
        }

    }
    private void desactiverModifAndCancel(){
        if (position_versement != nbr_versement - 1){
            binding.afficheVersModifButton.setVisibility(View.GONE);
            binding.afficheVersCancelButton.setVisibility(View.GONE);
        }
    }

    public void modifierVersement(){
        binding.afficheVersModifButton.setOnClickListener(v->{

         Intent intent = new Intent(this, ModifierVersementActivity.class);
         startActivity(intent);

        });

    }

    public void annullerversement(){
        binding.afficheVersCancelButton.setOnClickListener(v->{
            binding.afficheVersCancelButton.setEnabled(false);
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("annuller versement");
            builder.setMessage("êtes vous sûre de vouloir annuller le versement"+"\n");
            builder.setPositiveButton("oui",(dialog,which)->{
                   boolean success =  versementcontrolleur.annullerversement(versement,credit);
                   if (success){

                       ClientModel clientModel = clientcontrolleur.recupererClient(versement.getClient().getId());
                       clientViewModel.getClient().setValue(clientModel);
                       creditcontrolleur.setRecapTresteClient(clientModel);
                       creditcontrolleur.setRecapTcreditClient(clientModel);

                       int total_credit_client = creditcontrolleur.getRecapTcreditClient().getValue();
                       int total_reste_client = creditcontrolleur.getRecapTresteClient().getValue();

                         String destinationAdress = "+225"+clientModel.getTelephone();
//                       String destinationAdress = "5556";
                       String messageBody = appKessModel.getOwner() +"\n"+"\n"
                               + clientModel.getNom() + " "+clientModel.getPrenoms() +"\n"
                               +"vous avez annuller le versement de "+versement.getSommeverse()+" de votre credit"+"\n"
                               +"le "+ MesOutils.convertDateToString(new Date())+"\n"
                               +"total credit : "+total_credit_client+"\n"
                               +"reste a payer : "+total_reste_client;

                       SmsnoSentModel smsnoSentModel = new SmsnoSentModel(clientModel.getId(),messageBody);

                       if (ActivityCompat.checkSelfPermission(this,
                               android.Manifest.permission.SEND_SMS) !=
                               PackageManager.PERMISSION_GRANTED) {
                           ActivityCompat.requestPermissions(this,
                                   new String[]{android.Manifest.permission.SEND_SMS},
                                   MY_PERMISSIONS_REQUEST_SEND_SMS);
                       } else {

                           smsSender.smsSendwithInnerClass(messageBody, destinationAdress,versement.getId() );
                           smsSender.sentReiceiver(smsnoSentModel);
                       }

                   }

               }
           );

            builder.setNegativeButton("non", (dialog, which) -> {
                binding.afficheVersCancelButton.setEnabled(true);
            });

            builder.create().show();

        });

    }


    public void redirectTolisteCredits(){
        binding.afVersmToCredits.setOnClickListener(view -> {

            Intent intent = new Intent(this, GestionActivity.class);
            startActivity(intent);
        });
    }


    public void redirectToClient(){
        binding.afVersmToClient.setOnClickListener(view -> {
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



}