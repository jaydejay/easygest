package com.jay.easygest.vue;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.jay.easygest.R;
import com.jay.easygest.databinding.ActivitySettingsBinding;
import com.jay.easygest.model.AppKessModel;
import com.jay.easygest.model.InfosModel;
import com.jay.easygest.outils.AccessLocalAppKes;
import com.jay.easygest.outils.AccessLocalInfo;

public class SettingsActivity extends AppCompatActivity {

    private ActivitySettingsBinding binding;
  private AccessLocalAppKes accessLocalAppKes;
    private AppKessModel appkess;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        }catch (Exception e){}

    }

    public void afficherOwnerForm(){

        binding.txtsettingToggleOwnerEdit.setOnClickListener(view -> {

            binding.llsettingOwnerEdit.setVisibility(View.VISIBLE);
        });
    }

    public void updateSettingOwner(){

        binding.settingButtonOwner.setOnClickListener(view -> {
            String setting_owner = binding.editsettingOwner.getText().toString().trim();
            if (setting_owner.isEmpty()){
                binding.lleditsettingOwner.setError("champ obligatoire");
                binding.txtSettingOwnerError.setVisibility(View.VISIBLE);
                binding.txtSettingOwnerError.setText(binding.lleditsettingOwner.getError());
            }else {
                AppKessModel _appkes = new AppKessModel(appkess.getAppnumber(),appkess.getApppkey(),setting_owner,appkess.getBasecode(),appkess.getTelephone(),appkess.getAdresseelectro());
                boolean success = accessLocalAppKes.updateAppkes(_appkes);
                if (success){
                    binding.llsettingOwnerEdit.setVisibility(View.GONE);
                    binding.txtSettingOwnerError.setVisibility(View.GONE);
                    Intent intent = new Intent(SettingsActivity.this, SettingsActivity.class);
                    startActivity(intent);
                }else {
                    binding.lleditsettingOwner.setError("enregistrement avorté");
                    binding.txtSettingOwnerError.setText(binding.lleditsettingOwner.getError());
                }
            }
        });
    }




    public void afficherBasecodeForm(){

        binding.txtsettingToggleBaseCodeEdit.setOnClickListener(view -> {

            binding.llsettingBaseCodeEdit.setVisibility(View.VISIBLE);
        });
    }

    public void updateSettingbasecode(){

        binding.settinButtonBaseCode.setOnClickListener(view -> {
            String setting_base_code = binding.editsettingBaseCode.getText().toString().trim();
            if (setting_base_code.isEmpty()){
                binding.lleditsettingBaseCode.setError("champ obligatoire");
                binding.txtSettingBaseCodeError.setVisibility(View.VISIBLE);
                binding.txtSettingBaseCodeError.setText(binding.lleditsettingBaseCode.getError());
            } else if (setting_base_code.length() < 4 || setting_base_code.length() >4) {
                binding.lleditsettingBaseCode.setError("4 lettres attendus ");
                binding.txtSettingBaseCodeError.setVisibility(View.VISIBLE);
                binding.txtSettingBaseCodeError.setText(binding.lleditsettingBaseCode.getError());

            } else {
                AppKessModel _appkes = new AppKessModel(appkess.getAppnumber(),appkess.getApppkey(),appkess.getOwner(),setting_base_code,appkess.getTelephone(),appkess.getAdresseelectro());
                boolean success = accessLocalAppKes.updateAppkes(_appkes);
                if (success){
                    binding.llsettingBaseCodeEdit.setVisibility(View.GONE);
                    binding.txtSettingBaseCodeError.setVisibility(View.GONE);
                    Intent intent = new Intent(SettingsActivity.this, SettingsActivity.class);
                    startActivity(intent);
                }else {
                    binding.lleditsettingBaseCode.setError("enregistrement avorté");
                    binding.txtSettingBaseCodeError.setText(binding.lleditsettingBaseCode.getError());
                }
            }
        });
    }

    public void afficherTelephoneForm(){

        binding.txtsettingToggleTelephoneEdit.setOnClickListener(view -> {

            binding.llsettingTelephoneEdit.setVisibility(View.VISIBLE);
        });
    }

    public void updateSettingTelephone(){

        binding.settingButtonTelephone.setOnClickListener(view -> {
            String setting_telephone = binding.editsettingTelephone.getText().toString().trim();
            if (setting_telephone.isEmpty()){

                binding.lleditsettingTelephone.setError("champ obligatoire");
                binding.txtSettingTeleponeError.setVisibility(View.VISIBLE);
                binding.txtSettingTeleponeError.setText(binding.lleditsettingTelephone.getError());
            } else if (setting_telephone.length() < 10 || setting_telephone.length() >10) {
                binding.lleditsettingTelephone.setError("10 caracteres attendus ");
                binding.txtSettingTeleponeError.setVisibility(View.VISIBLE);
                binding.txtSettingTeleponeError.setText(binding.lleditsettingTelephone.getError());

            } else {
                AppKessModel _appkes = new AppKessModel(appkess.getAppnumber(),appkess.getApppkey(),appkess.getOwner(),appkess.getBasecode(),setting_telephone,appkess.getAdresseelectro());
                boolean success = accessLocalAppKes.updateAppkes(_appkes);
                if (success){
                    binding.llsettingTelephoneEdit.setVisibility(View.GONE);
                    binding.txtSettingTeleponeError.setVisibility(View.GONE);
                    Intent intent = new Intent(SettingsActivity.this, SettingsActivity.class);
                    startActivity(intent);
                }else {
                    binding.lleditsettingTelephone.setError("enregistrement avorté");
                    binding.txtSettingTeleponeError.setText(binding.lleditsettingMail.getError());
                }
            }
        });
    }

    public void afficherMailForm(){

        binding.txtsettingToggleMailEdit.setOnClickListener(view -> {

            binding.llsettingMailEdit.setVisibility(View.VISIBLE);
        });
    }

    public void updateSettingMail(){

        binding.settingButtonMail.setOnClickListener(view -> {
            String setting_mail = binding.editsettingMail.getText().toString().trim();
            if (setting_mail.isEmpty()){

                binding.lleditsettingMail.setError("champ obligatoire");
                binding.txtSettingMailError.setVisibility(View.VISIBLE);
                binding.txtSettingMailError.setText(binding.lleditsettingMail.getError());
            } else {
                AppKessModel _appkes = new AppKessModel(appkess.getAppnumber(),appkess.getApppkey(),appkess.getOwner(),appkess.getBasecode(),appkess.getTelephone(),setting_mail);
                boolean success = accessLocalAppKes.updateAppkes(_appkes);
                if (success){
                    binding.llsettingMailEdit.setVisibility(View.GONE);
                    binding.txtSettingMailError.setVisibility(View.GONE);
                    Intent intent = new Intent(SettingsActivity.this, SettingsActivity.class);
                    startActivity(intent);
                }else {
                    binding.lleditsettingMail.setError("enregistrement avorté");
                    binding.txtSettingMailError.setText(binding.lleditsettingMail.getError());
                }
            }
        });
    }

}