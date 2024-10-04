package com.jay.easygest.vue;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.jay.easygest.databinding.ActivitySettingsBinding;
import com.jay.easygest.model.AppKessModel;
import com.jay.easygest.outils.AccessLocalAppKes;
import com.jay.easygest.outils.SessionManagement;

import java.util.Objects;

public class SettingsActivity extends AppCompatActivity {

    private SessionManagement sessionManagement;
    private ActivitySettingsBinding binding;
    private AccessLocalAppKes accessLocalAppKes;
    private AppKessModel appkess;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sessionManagement = new SessionManagement(this);
        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        accessLocalAppKes = new AccessLocalAppKes(this);
        appkess = accessLocalAppKes.getAppkes();
        setContentView(binding.getRoot());
        initField();
        afficherOwnerForm();
        afficherBasecodeForm();
        afficherTelephoneForm();
        afficherMailForm();
        updateSettingOwner();
        updateSettingbasecode();
        updateSettingTelephone();
        updateSettingMail();
    }

    public void initField(){
        try {
            String owner = appkess.getOwner();
            String baseCode = appkess.getBasecode();
            String _telephone = appkess.getTelephone();
            String email = appkess.getAdresseelectro();

        String proprietaire = "Proprietaire "+"\n"+owner;
        String base_code = "Base Code Client"+"\n"+ baseCode;
        String telephone = "Telephone"+"\n"+_telephone;
        String e_mail = "Adresse Electronique"+"\n"+email;

        binding.txtsettingOwner.setText(proprietaire);
        binding.txtsettingBaseCode.setText(base_code);
        binding.txtsettingTelephone.setText(telephone);
        binding.txtsettingMail.setText(e_mail);

        }catch (Exception e){
            //do nothings
        }

    }

    public void afficherOwnerForm(){

        binding.txtsettingToggleOwnerEdit.setOnClickListener(view -> binding.llsettingOwnerEdit.setVisibility(View.VISIBLE));
    }

    public void updateSettingOwner(){

        binding.settingButtonOwner.setOnClickListener(view -> {
            String setting_owner = Objects.requireNonNull(binding.editsettingOwner.getText()).toString().trim();
            if (setting_owner.isEmpty()){
                binding.lleditsettingOwner.setError("obligatoire");

            }else if (setting_owner.length() < 5 ) {
                binding.lleditsettingOwner.setError("5 minimum");

            }else if (setting_owner.length() > 16 ) {
                binding.lleditsettingOwner.setError("16 maximum");

            }else {
                AppKessModel _appkes = new AppKessModel(appkess.getAppnumber(),appkess.getApppkey(),setting_owner,appkess.getBasecode(),appkess.getTelephone(),appkess.getAdresseelectro());
                boolean success = accessLocalAppKes.updateAppkes(_appkes);
                if (success){
                    binding.llsettingOwnerEdit.setVisibility(View.GONE);

                    Intent intent = new Intent(SettingsActivity.this, SettingsActivity.class);
                    startActivity(intent);
                }else {
                    binding.lleditsettingOwner.setError("enregistrement avorté");
                }
            }
        });
    }




    public void afficherBasecodeForm(){

        binding.txtsettingToggleBaseCodeEdit.setOnClickListener(view -> binding.llsettingBaseCodeEdit.setVisibility(View.VISIBLE));
    }

    public void updateSettingbasecode(){

        binding.settinButtonBaseCode.setOnClickListener(view -> {
            String setting_base_code = Objects.requireNonNull(binding.editsettingBaseCode.getText()).toString().trim();
            if (setting_base_code.isEmpty()){
                binding.lleditsettingBaseCode.setError("obligatoire");

            } else if (setting_base_code.length() != 4 ) {
                binding.lleditsettingBaseCode.setError("4 lettres attendus ");

            } else {
                AppKessModel _appkes = new AppKessModel(appkess.getAppnumber(),appkess.getApppkey(),appkess.getOwner(),setting_base_code,appkess.getTelephone(),appkess.getAdresseelectro());
                boolean success = accessLocalAppKes.updateAppkes(_appkes);
                if (success){
                    binding.llsettingBaseCodeEdit.setVisibility(View.GONE);
                    Intent intent = new Intent(SettingsActivity.this, SettingsActivity.class);
                    startActivity(intent);
                }else {
                    binding.lleditsettingBaseCode.setError("enregistrement avorté");

                }
            }
        });
    }

    public void afficherTelephoneForm(){

        binding.txtsettingToggleTelephoneEdit.setOnClickListener(view -> binding.llsettingTelephoneEdit.setVisibility(View.VISIBLE));
    }

    public void updateSettingTelephone(){

        binding.settingButtonTelephone.setOnClickListener(view -> {
            String setting_telephone = Objects.requireNonNull(binding.editsettingTelephone.getText()).toString().trim();
            if (setting_telephone.isEmpty()){
                binding.lleditsettingTelephone.setError("obligatoire");

            } else if (setting_telephone.length() != 10) {
                binding.lleditsettingTelephone.setError("10 caracteres");

            } else {
                AppKessModel _appkes = new AppKessModel(appkess.getAppnumber(),appkess.getApppkey(),appkess.getOwner(),appkess.getBasecode(),setting_telephone,appkess.getAdresseelectro());
                boolean success = accessLocalAppKes.updateAppkes(_appkes);
                if (success){
                    binding.llsettingTelephoneEdit.setVisibility(View.GONE);
                    Intent intent = new Intent(SettingsActivity.this, SettingsActivity.class);
                    startActivity(intent);
                }else {
                    binding.lleditsettingTelephone.setError("enregistrement avorté");
                }
            }
        });
    }

    public void afficherMailForm(){

        binding.txtsettingToggleMailEdit.setOnClickListener(view -> binding.llsettingMailEdit.setVisibility(View.VISIBLE));
    }

    public void updateSettingMail(){

        binding.settingButtonMail.setOnClickListener(view -> {
            String setting_mail = Objects.requireNonNull(binding.editsettingMail.getText()).toString().trim();
            if (setting_mail.isEmpty()){
                binding.lleditsettingMail.setError("obligatoire");
            } else {
                AppKessModel _appkes = new AppKessModel(appkess.getAppnumber(),appkess.getApppkey(),appkess.getOwner(),appkess.getBasecode(),appkess.getTelephone(),setting_mail);
                boolean success = accessLocalAppKes.updateAppkes(_appkes);
                if (success){
                    binding.llsettingMailEdit.setVisibility(View.GONE);
                    Intent intent = new Intent(SettingsActivity.this, SettingsActivity.class);
                    startActivity(intent);
                }else {
                    binding.lleditsettingMail.setError("enregistrement avorté");
                }
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
//            finish();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        sessionManagement.removeSession();
    }

}