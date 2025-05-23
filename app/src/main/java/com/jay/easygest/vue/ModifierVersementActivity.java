package com.jay.easygest.vue;

import static com.jay.easygest.outils.VariablesStatique.MY_PERMISSIONS_REQUEST_SEND_SMS;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;

import com.jay.easygest.R;
import com.jay.easygest.controleur.Creditcontrolleur;
import com.jay.easygest.controleur.Versementcontrolleur;
import com.jay.easygest.databinding.ActivityModifierVersementBinding;
import com.jay.easygest.model.AppKessModel;
import com.jay.easygest.model.ClientModel;
import com.jay.easygest.model.CreditModel;
import com.jay.easygest.model.SmsnoSentModel;
import com.jay.easygest.model.VersementsModel;
import com.jay.easygest.outils.AccessLocalAppKes;
import com.jay.easygest.outils.MesOutils;
import com.jay.easygest.outils.SessionManagement;
import com.jay.easygest.outils.SmsSender;
import com.jay.easygest.vue.ui.credit.CreditViewModel;
import com.jay.easygest.vue.ui.versement.VersementViewModel;

import java.util.Date;

public class ModifierVersementActivity extends AppCompatActivity {

    private SessionManagement sessionManagement;
    private AccessLocalAppKes accessLocalAppKes;
    private SmsSender smsSender;
    Button bouton_modifier;
    TextView EDTcodeclient ;
    EditText EDTsomme ;
    EditText EDTdatecredit;
    TextView title;
    private Versementcontrolleur versementcontrolleur;
    private Creditcontrolleur creditcontrolleur;
    private VersementViewModel versementViewModel;
    private CreditViewModel creditViewModel;
   private VersementsModel versement;
   private AppKessModel appKessModel;
   private ClientModel client;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sessionManagement = new SessionManagement(this);
        smsSender = new SmsSender(this, this);
        accessLocalAppKes = new AccessLocalAppKes(this);
        versementcontrolleur = Versementcontrolleur.getVersementcontrolleurInstance(this);
        creditcontrolleur = Creditcontrolleur.getCreditcontrolleurInstance(this);

        versementViewModel = new ViewModelProvider(this).get(VersementViewModel.class);
        creditViewModel = new ViewModelProvider(this).get(CreditViewModel.class);

        com.jay.easygest.databinding.ActivityModifierVersementBinding binding = ActivityModifierVersementBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        versement = versementViewModel.getMversement().getValue();
        assert versement != null;
        client = versement.getClient();

        EDTcodeclient =  findViewById(R.id.editversementcodeclt);
        EDTsomme =  findViewById(R.id.editversementsomme);
        EDTdatecredit = findViewById(R.id.editversementdate);
        bouton_modifier =  findViewById(R.id.btnversement);
        title =  findViewById(R.id.txtmodifversementtitle);
        init();
        modifierVersement();

    }

    public void init(){

        String texte = client.getNom()+" "+client.getPrenoms()+" "+client.getCodeclient();
        title.setText(texte);
        EDTcodeclient.setVisibility(View.GONE);
        EDTsomme.setText(String.valueOf(versement.getSommeverse()));
        EDTdatecredit.setText(MesOutils.convertDateToString(new Date(versement.getDateversement())));
    }

    public void modifierVersement(){
        bouton_modifier.setOnClickListener(v -> {
            bouton_modifier.setEnabled(false);
            String edt_somme = EDTsomme.getText().toString().trim();
            String dateversement = EDTdatecredit.getText().toString().trim();
            Date date = MesOutils.convertStringToDate(dateversement);

            if ( edt_somme.isEmpty() || dateversement.isEmpty()){
                Toast.makeText(this, "champs obligatoires", Toast.LENGTH_SHORT).show();
                bouton_modifier.setEnabled(true);

            } else if (date == null) {
                Toast.makeText(ModifierVersementActivity.this, "format de date incorrect", Toast.LENGTH_SHORT).show();
                bouton_modifier.setEnabled(true);
            } else {
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
                        CreditModel credit = creditViewModel.getCredit().getValue();
                        int somme_du_credit = 0;
                        if (credit != null) {
                            somme_du_credit = credit.getSommecredit();
                        }

                        int anncien_total_versement = creditViewModel.getCredit().getValue().getVersement();
                        int annciennesommeverse = Integer.parseInt(String.valueOf(versement.getSommeverse())) ;
                        int nouveau_total_versement = (anncien_total_versement-annciennesommeverse)+nouvellesommeverse;
                        if (  nouvellesommeverse > 0 & nouveau_total_versement <= somme_du_credit){

                            boolean success = versementcontrolleur.modifierVersement(credit,versement,nouveau_total_versement,nouvellesommeverse,dateversement);
                            if (success) {
                                VersementsModel versementsModel = new VersementsModel(versement.getId(),client,credit,(long)nouvellesommeverse,credit.getId(), date.getTime());
                                versementViewModel.getMversement().setValue(versementsModel);
                                if (annciennesommeverse != nouvellesommeverse ){

                                    appKessModel = accessLocalAppKes.getAppkes();
                                    appKessModel = accessLocalAppKes.getAppkes();
                                    creditcontrolleur.setRecapTresteClient(client);
                                    creditcontrolleur.setRecapTcreditClient(client);

                                    int total_credit_client = creditViewModel.getTotalcreditsclient().getValue();
                                    int total_reste_client = creditViewModel.getTotalrestesclient().getValue();

                                    String destinationAdress = "+225"+client.getTelephone();
//                                    String destinationAdress = "5556";
                                    String messageBody = appKessModel.getOwner() +"\n"+"\n"
                                            + client.getNom() + " "+client.getPrenoms() +"\n"
                                            +"vous avez modifier un versement pour votre credit"+"\n"
                                            +"le "+ MesOutils.convertDateToString(new Date())+"\n"
                                            +"total credit : "+total_credit_client+"\n"
                                            +"reste a payer : "+total_reste_client;

                                    SmsnoSentModel smsnoSentModel = new SmsnoSentModel(client.getId(),messageBody);
                                    smsSender.smsSendwithInnerClass(messageBody, destinationAdress,smsnoSentModel.getSmsid() );
                                    smsSender.sentReiceiver(smsnoSentModel);

                                }else {
                                    Intent intent = new Intent(ModifierVersementActivity.this, AfficheversementActivity.class);
                                    startActivity(intent);
                                    finish();
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