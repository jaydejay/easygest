package com.jay.easygest.vue;

import static com.jay.easygest.outils.VariablesStatique.MY_PERMISSIONS_REQUEST_SEND_SMS;
import static com.jay.easygest.outils.VariablesStatique.MY_PERMISSIONS_REQUEST_SEND_SMS_2;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.RadioButton;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.jay.easygest.R;
import com.jay.easygest.databinding.ActivityDemandeCleBinding;
import com.jay.easygest.model.AppKessModel;
import com.jay.easygest.model.SmsnoSentModel;
import com.jay.easygest.outils.AccessLocalAppKes;
import com.jay.easygest.outils.SmsSender;
import com.jay.easygest.outils.VariablesStatique;

public class DemandeCleActivity extends AppCompatActivity {
    private ActivityDemandeCleBinding binding;
    private  RadioButton generic_radio;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDemandeCleBinding.inflate(getLayoutInflater());

        binding.rgKeyLevel.check(binding.checkBoxPermanent.getId());

        executeIntent();
        setContentView(binding.getRoot());
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });
    }

    private void executeIntent() {
        String intet_msg = getIntent().getStringExtra("smssentmessge");
        if (intet_msg != null){
            Toast.makeText(this, intet_msg, Toast.LENGTH_SHORT).show();
        }
    }

    public void sendDemande (){
        binding.btnDemandeKey.setOnClickListener(v -> {

            generic_radio = findViewById(binding.rgKeyLevel.getCheckedRadioButtonId());
            String key_level = generic_radio.getText().toString();

            String messageBody = getMessageBody(key_level);
//            String destinationAdress = VariablesStatique.EMULATEUR_2_TELEPHONE;
            String destinationAdress = VariablesStatique.DEVELOPER_PHONE;

            if (ActivityCompat.checkSelfPermission(this,
                    android.Manifest.permission.SEND_SMS) !=
                    PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.SEND_SMS},
                        MY_PERMISSIONS_REQUEST_SEND_SMS_2);
                binding.btnDemandeKey.setEnabled(true);
            }else {
                SmsnoSentModel smsnoSentModel = new SmsnoSentModel(1,messageBody);
                SmsSender smsSender = new SmsSender(this,this);
                Intent intent = new Intent(DemandeCleActivity.this,GestionActivity.class);

                smsSender.smsSendwithInnerClass(messageBody, destinationAdress,smsnoSentModel.getSmsid() );
                smsSender.sentReiceiverGeneric(smsnoSentModel,intent);
            }



        });
    }

    @NonNull
    private String getMessageBody(String key_level) {
        AccessLocalAppKes accessLocalAppKes = new AccessLocalAppKes(this);
        AppKessModel appKessModel = accessLocalAppKes.getAppkes();

        return "DEMANDE DE CLE D'ACTIVATION"+"\n"+"\n"
                + "proprietaire : " + appKessModel.getOwner() +"\n"
                + "appli number : "+appKessModel.getAppnumber() +"\n"
                + "type d'activation : " + key_level +"\n"
                + "telephone du requerent : " + appKessModel.getTelephone() +"\n";


    }

    @Override
    protected void onStart() {
        super.onStart();
        executeIntent();
        sendDemande();
    }

    @Override
    protected void onResume() {
        super.onResume();
        executeIntent();
    }
}