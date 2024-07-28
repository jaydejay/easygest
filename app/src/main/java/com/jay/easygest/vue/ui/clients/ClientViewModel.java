package com.jay.easygest.vue.ui.clients;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.jay.easygest.controleur.Clientcontrolleur;
import com.jay.easygest.model.ClientModel;

import java.util.ArrayList;

public class ClientViewModel extends ViewModel {
    private final MutableLiveData<ClientModel> mclient ;
    private final MutableLiveData<ArrayList<ClientModel>> mlisteClients ;


    public ClientViewModel() {
        Clientcontrolleur clientcontrolleur = Clientcontrolleur.getClientcontrolleurInstance(null);
//        unclient = new MutableLiveData<>();
//        unclient = new MutableLiveData<>(clientcontrolleur.getClient());
//        lalisteClients = new MutableLiveData<>();
        mclient= clientcontrolleur.getMclient();
        mlisteClients = clientcontrolleur.getMlisteClients();
    }

    public MutableLiveData<ClientModel> getClient(){return mclient;}

    public MutableLiveData<ArrayList<ClientModel>> getListeClients(){
        return mlisteClients;
    }


}
