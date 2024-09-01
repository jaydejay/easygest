package com.jay.easygest.vue.ui.versement;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.jay.easygest.controleur.Versementcontrolleur;
import com.jay.easygest.model.ClientModel;
import com.jay.easygest.model.VersementsModel;

import java.util.ArrayList;
import java.util.Objects;

public class VersementViewModel extends ViewModel {

   private final MutableLiveData<VersementsModel> mversement ;
    private final MutableLiveData<ArrayList<VersementsModel>> mversements;

    public VersementViewModel() {
        Versementcontrolleur versementcontrolleur = Versementcontrolleur.getVersementcontrolleurInstance(null);
        mversement = versementcontrolleur.getMversement();
        mversements = versementcontrolleur.getMversements();
    }


    public MutableLiveData<VersementsModel> getMversement(){
        return mversement;
    }

    public MutableLiveData<ArrayList<VersementsModel>> getMversements(){
        return mversements;
    }

    public ArrayList<VersementsModel> getVersementsClient(ClientModel client){
        ArrayList<VersementsModel> liste_versements_client = new ArrayList<>();
        for (VersementsModel versement : Objects.requireNonNull(mversements.getValue())) {
            if (Objects.equals(versement.getClient().getId(), client.getId())){
                liste_versements_client.add(versement);
            }
        }
        return liste_versements_client;
    }

}
