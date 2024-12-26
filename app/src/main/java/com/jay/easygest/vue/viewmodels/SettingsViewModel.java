package com.jay.easygest.vue.viewmodels;


import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SettingsViewModel extends ViewModel {

//    private AccessLocalAppKes accessLocalAppKes;
    private final MutableLiveData<String> owner ;
    private final MutableLiveData<String> base_code ;
    private final MutableLiveData<String> telephone ;
    private final MutableLiveData<String> email ;
    private final MutableLiveData<String> setting_password ;

    public SettingsViewModel() {

        owner = new MutableLiveData<>();
        base_code = new MutableLiveData<>();
        telephone = new MutableLiveData<>();
        email = new MutableLiveData<>();
        setting_password = new MutableLiveData<>();
    }


    public MutableLiveData<String> getOwner() {
        return owner;
    }

    public MutableLiveData<String> getBase_code() {
        return base_code;
    }

    public MutableLiveData<String> getTelephone() {
        return telephone;
    }

    public MutableLiveData<String> getEmail() {
        return email;
    }

    public MutableLiveData<String> getSetting_password() {
        return setting_password;
    }
}
