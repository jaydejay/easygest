package com.jay.easygest.vue;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.method.TextKeyListener;
import android.text.style.StyleSpan;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.jay.easygest.R;
import com.jay.easygest.databinding.ActivitySettingsBinding;
import com.jay.easygest.model.AppKessModel;
import com.jay.easygest.outils.AccessLocalAppKes;
import com.jay.easygest.outils.PasswordHascher;
import com.jay.easygest.outils.SessionManagement;
import com.jay.easygest.outils.VariablesStatique;
import com.jay.easygest.vue.viewmodels.SettingsViewModel;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

public class SettingsActivity extends AppCompatActivity {

    private SessionManagement sessionManagement;
    private  SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private ActivitySettingsBinding binding;
    private AccessLocalAppKes accessLocalAppKes;
    private AppKessModel appkess;
    private SettingsViewModel settingsViewModel;

    private PasswordHascher passwordHascher;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sessionManagement = new SessionManagement(this);
        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        accessLocalAppKes = new AccessLocalAppKes(this);
        appkess = accessLocalAppKes.getAppkes();
        passwordHascher = new PasswordHascher();

        sharedPreferences = this.getSharedPreferences(VariablesStatique.SETTING_SHARED_PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        settingsViewModel = new ViewModelProvider(this).get(SettingsViewModel.class);
        settingsViewModel.getOwner().setValue(appkess.getOwner());
        settingsViewModel.getBase_code().setValue(appkess.getBasecode());
        settingsViewModel.getTelephone().setValue(appkess.getTelephone());
        settingsViewModel.getEmail().setValue(appkess.getAdresseelectro());
        settingsViewModel.getSetting_password().setValue(getIntent().getExtras().get("mdp").toString());

        setContentView(binding.getRoot());
        initField();
        afficherOwnerForm();
        afficherBasecodeForm();
        afficherTelephoneForm();
        afficherMailForm();
        afficherPasswordForm();
        updateSettingOwner();
        updateSettingbasecode();
        updateSettingTelephone();
        updateSettingMail();
        updateSettingPassword();
    }

    public void initField(){
        try {

            settingsViewModel.getOwner().observe(this,owner->{
                String proprietaire = "Proprietaire "+"\n"+owner;
                binding.txtsettingOwner.setText(proprietaire);

            });

            settingsViewModel.getBase_code().observe(this,baseCode->{
                String base_code = "Base Code Client"+"\n"+ baseCode;
                binding.txtsettingBaseCode.setText(base_code);

            });

            settingsViewModel.getTelephone().observe(this,_telephone->{
                String telephone = "Telephone"+"\n"+_telephone;
                binding.txtsettingTelephone.setText(telephone);

            });

            settingsViewModel.getEmail().observe(this,email->{
                String e_mail = "Emal"+"\n"+email;
                binding.txtsettingMail.setText(e_mail);

            });

            settingsViewModel.getSetting_password().observe(this,password->{
                String pass_word = "MDP setting"+"\n"+password;
                binding.txtsettingPassword.setText(pass_word);

            });
        }catch (Exception e){
            //do nothings
        }

    }

    public void afficherOwnerForm(){
        AtomicBoolean hiden = new AtomicBoolean(false);

        binding.txtsettingToggleOwnerEdit.setOnClickListener(view ->{
           if (hiden.get()){
               binding.txtsettingToggleOwnerEdit.setText("modifier");
               binding.llsettingOwnerEdit.setVisibility(View.GONE);
               hiden.set(false);
           }else {
               binding.txtsettingToggleOwnerEdit.setText("annuller");
               binding.llsettingOwnerEdit.setVisibility(View.VISIBLE);
               hiden.set(true);
           }


        } );
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

                AppKessModel _appkes = new AppKessModel(appkess.getAppnumber(),appkess.getApppkey(),setting_owner.toUpperCase(),appkess.getBasecode(),appkess.getTelephone(),appkess.getAdresseelectro());
                boolean success = accessLocalAppKes.updateAppkes(_appkes);
                if (success){
                    settingsViewModel.getOwner().postValue(setting_owner);
                    binding.llsettingOwnerEdit.setVisibility(View.GONE);
                    binding.txtsettingToggleOwnerEdit.setText("modifier");

                }else {
                    binding.lleditsettingOwner.setError("enregistrement avorté");
                }
            }
        });
    }




    public void afficherBasecodeForm(){
        AtomicBoolean hiden = new AtomicBoolean(false);
        binding.txtsettingToggleBaseCodeEdit.setOnClickListener(view -> {
            if (hiden.get()){
                binding.txtsettingToggleBaseCodeEdit.setText("modifier");
                binding.llsettingBaseCodeEdit.setVisibility(View.GONE);
                hiden.set(false);
            }else {
                binding.txtsettingToggleBaseCodeEdit.setText("annuller");
                binding.llsettingBaseCodeEdit.setVisibility(View.VISIBLE);
                hiden.set(true);
            }

        });
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
                    settingsViewModel.getBase_code().postValue(setting_base_code);
                    binding.txtsettingToggleBaseCodeEdit.setText("modifier");

                }else {
                    binding.lleditsettingBaseCode.setError("enregistrement avorté");

                }
            }
        });
    }

    public void afficherTelephoneForm(){
        AtomicBoolean hiden = new AtomicBoolean(false);
        binding.txtsettingToggleTelephoneEdit.setOnClickListener(view -> {

            if (hiden.get()){
                binding.txtsettingToggleTelephoneEdit.setText("modifier");
                binding.llsettingTelephoneEdit.setVisibility(View.GONE);
                hiden.set(false);
            }else {
                binding.txtsettingToggleTelephoneEdit.setText("annuller");
                binding.llsettingTelephoneEdit.setVisibility(View.VISIBLE);
                hiden.set(true);
            }

        });
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
                    settingsViewModel.getTelephone().postValue(setting_telephone);
                    binding.llsettingTelephoneEdit.setVisibility(View.GONE);
                    binding.txtsettingToggleTelephoneEdit.setText("modifier");
                }else {
                    binding.lleditsettingTelephone.setError("enregistrement avorté");
                }
            }
        });
    }

    public void afficherMailForm(){
        AtomicBoolean hiden = new AtomicBoolean(false);
        binding.txtsettingToggleMailEdit.setOnClickListener(view ->{
            if (hiden.get()){
                binding.txtsettingToggleMailEdit.setText("modifier");
                binding.llsettingMailEdit.setVisibility(View.GONE);
                hiden.set(false);
            }else {
                binding.txtsettingToggleMailEdit.setText("annuller");
                binding.llsettingMailEdit.setVisibility(View.VISIBLE);
                hiden.set(true);
            }

        });
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
                    settingsViewModel.getEmail().postValue(setting_mail);
                    binding.llsettingMailEdit.setVisibility(View.GONE);
                    binding.txtsettingToggleMailEdit.setText("modifier");

                }else {
                    binding.lleditsettingMail.setError("enregistrement avorté");
                }
            }
        });
    }

    public void afficherPasswordForm(){
        AtomicBoolean hiden = new AtomicBoolean(false);
        binding.txtsettingTogglePasswordEdit.setOnClickListener(view ->{
            if (hiden.get()){
                binding.txtsettingTogglePasswordEdit.setText("modifier");
                binding.llsettingPasswordEdit.setVisibility(View.GONE);
                hiden.set(false);
            }else {
                binding.txtsettingTogglePasswordEdit.setText("annuller");
                binding.llsettingPasswordEdit.setVisibility(View.VISIBLE);
                hiden.set(true);
            }

        });
    }

    public void updateSettingPassword(){

        binding.settingButtonPassword.setOnClickListener(view -> {
            String setting_password = Objects.requireNonNull(binding.editsettingPassword.getText()).toString().trim();
            if (setting_password.isEmpty()){
                binding.lleditsettingPassword.setError("obligatoire");
            } else {
              String _setting_password = passwordHascher.getHashingPass(setting_password,VariablesStatique.MY_SALT);
                editor.putString(VariablesStatique.SETTING_SHARED_PREF_VARIABLE,_setting_password).commit();
                settingsViewModel.getSetting_password().postValue(setting_password);
                binding.llsettingPasswordEdit.setVisibility(View.GONE);
                binding.txtsettingTogglePasswordEdit.setText("modifier");
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