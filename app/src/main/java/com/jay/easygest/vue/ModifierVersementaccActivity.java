package com.jay.easygest.vue;

import static com.jay.easygest.outils.VariablesStatique.MY_PERMISSIONS_REQUEST_SEND_SMS;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;

import com.jay.easygest.R;
import com.jay.easygest.controleur.Accountcontroller;
import com.jay.easygest.controleur.Versementacccontrolleur;
import com.jay.easygest.databinding.ActivityModifierVersementaccBinding;
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
import com.jay.easygest.vue.ui.versementacc.VersementaccViewModel;

import java.util.Date;
import java.util.Objects;

public class ModifierVersementaccActivity extends AppCompatActivity {

    private SessionManagement sessionManagement ;
    private SmsSender smsSender;
    private AccessLocalAppKes accessLocalAppKes;
    private ActivityModifierVersementaccBinding binding;
    private Versementacccontrolleur versementacccontrolleur;
    private Accountcontroller accountcontroller;
    private AccountViewModel accountViewModel;
    private  VersementaccViewModel versementaccViewModel;
    private AppKessModel appKessModel;
    private VersementsaccModel versement;
    private ClientModel client;
   EditText EDTcodeclient;
   EditText EDTsomme;
   EditText EDTdateaccount;
   Button bouton_modifier;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sessionManagement = new SessionManagement(this);
        smsSender = new SmsSender(this, this);
        accessLocalAppKes = new AccessLocalAppKes(this);
        binding = ActivityModifierVersementaccBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        versementacccontrolleur = Versementacccontrolleur.getVersementacccontrolleurInstance(this);
        accountcontroller = Accountcontroller.getAccountcontrolleurInstance(this);

         versementaccViewModel = new ViewModelProvider(this).get(VersementaccViewModel.class);
        accountViewModel = new ViewModelProvider(this).get(AccountViewModel.class);

        versement = versementaccViewModel.getMversementacc().getValue();
        client = Objects.requireNonNull(versement).getClient();


        EDTcodeclient =  findViewById(R.id.ajoutervrsmtacccodeclt);
        EDTsomme =  findViewById(R.id.ajoutervrsmntaccsomme);
        EDTdateaccount =  findViewById(R.id.ajoutervrsmntaccdate);
        bouton_modifier =  findViewById(R.id.btnversementacc);

        init();
        modifierVersement();
    }

    public void init(){

        String texte = client.getNom()+" "+client.getPrenoms()+" "+client.getCodeclient();
        binding.txtmodifversementacctitle.setText(texte);
        EDTcodeclient.setVisibility(View.GONE);
        EDTsomme.setText(String.valueOf(versement.getSommeverse()));
        EDTdateaccount.setText(MesOutils.convertDateToString(new Date(versement.getDateversement())));
    }

    public void modifierVersement(){
        bouton_modifier.setOnClickListener(v -> {
            bouton_modifier.setEnabled(false);
            String edt_somme = EDTsomme.getText().toString().trim();
            String dateversement = EDTdateaccount.getText().toString().trim();
            Date date = MesOutils.convertStringToDate(dateversement);

            if ( edt_somme.isEmpty() || dateversement.isEmpty()){
                Toast.makeText(this, "champs obligatoires", Toast.LENGTH_SHORT).show();
                bouton_modifier.setEnabled(true);
            } else if (date == null) {
                Toast.makeText(ModifierVersementaccActivity.this, "format de date incorrect", Toast.LENGTH_SHORT).show();
                bouton_modifier.setEnabled(true);
            } else {
//                if (Integer.parseInt(edt_somme) >= 1000) {
                if (ActivityCompat.checkSelfPermission(this,
                        android.Manifest.permission.SEND_SMS) !=
                        PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this,
                            new String[]{android.Manifest.permission.SEND_SMS},
                            MY_PERMISSIONS_REQUEST_SEND_SMS);
                    bouton_modifier.setEnabled(true);
                } else {

                    try {
                        int nouvellesommeverse = Integer.parseInt(edt_somme);
                        AccountModel account = versement.getAccount();
                        int somme_du_account = account.getSommeaccount();
                        int anncien_total_versement = account.getVersement();
                        int annciennesommeverse = Integer.parseInt(String.valueOf(versement.getSommeverse())) ;
                        int nouveau_total_versement = (anncien_total_versement-annciennesommeverse)+nouvellesommeverse;

                        if (  nouvellesommeverse > 0 & nouveau_total_versement <= somme_du_account){
                            boolean success = versementacccontrolleur.modifierVersement(account,versement,nouveau_total_versement,nouvellesommeverse,dateversement);
                            if (success) {
                                VersementsaccModel versementsaccModel = new VersementsaccModel(versement.getId(),client,account,(long)nouvellesommeverse,date.getTime());
                                versementaccViewModel.getMversementacc().setValue(versementsaccModel);
                                if (annciennesommeverse != nouvellesommeverse ){
                                    appKessModel = accessLocalAppKes.getAppkes();
                                    accountcontroller.setRecapTresteClient(client);
                                    accountcontroller.setRecapTaccountClient(client);

                                    int total_account_client = accountViewModel.getTotalaccountsclient().getValue();
                                    int total_reste_client = accountViewModel.getTotalrestesclient().getValue();

                                    String destinationAdress = "+225"+client.getTelephone();
//                                    String destinationAdress = "5556";
                                    String messageBody = appKessModel.getOwner() +"\n"+"\n"
                                            + client.getNom() + " "+client.getPrenoms() +"\n"
                                            +"vous avez modifier un versement pour votre account"+"\n"
                                            +"le "+ MesOutils.convertDateToString(new Date())+"\n"
                                            +"total account : "+total_account_client+"\n"
                                            +"reste a payer : "+total_reste_client;

                                    SmsnoSentModel smsnoSentModel = new SmsnoSentModel(client.getId(),messageBody);
                                    smsSender.smsSendwithInnerClass(messageBody, destinationAdress,smsnoSentModel.getSmsid() );
                                    smsSender.sentReiceiver(smsnoSentModel);


                                }else {
                                    Intent intent = new Intent(ModifierVersementaccActivity.this, AfficherversementaccActivity.class);
                                    startActivity(intent);
                                }

                            } else {
                                Toast.makeText(this, "revoyez le versement ", Toast.LENGTH_SHORT).show();
                                bouton_modifier.setEnabled(true);
                            }
                        }else {
                            Toast.makeText(this, "versement elevé", Toast.LENGTH_SHORT).show();
                            bouton_modifier.setEnabled(true);
                        }

                    } catch (Exception e) {
                        Toast.makeText(this, "erreur versement avorté", Toast.LENGTH_SHORT).show();
                        bouton_modifier.setEnabled(true);
                    }
                }


//                }else {Toast.makeText(this, "le verement doit etre de 1000 F minimum", Toast.LENGTH_SHORT).show();
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
        }
//        smsSender.sentReiceiver();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        sessionManagement.removeSession();
    }

}