package com.jay.easygest.vue.viewmodels;


import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.jay.easygest.model.SmsnoSentModel;

public class SmsSenderViewModel extends ViewModel {

//    private AccessLocalAppKes accessLocalAppKes;
    private final MutableLiveData<SmsnoSentModel> smsfailled ;
    private final MutableLiveData<Integer> smsSent_id ;


    public SmsSenderViewModel() {

        smsfailled = new MutableLiveData<>();
        smsSent_id = new MutableLiveData<>();

    }

    public MutableLiveData<SmsnoSentModel> getSmsfailled() {
        return smsfailled;
    }

    public MutableLiveData<Integer> getSmsSentId() {
        return smsSent_id;
    }


}
