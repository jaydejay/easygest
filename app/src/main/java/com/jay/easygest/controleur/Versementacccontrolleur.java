package com.jay.easygest.controleur;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.jay.easygest.model.AccountModel;
import com.jay.easygest.model.ClientModel;
import com.jay.easygest.model.CreditModel;
import com.jay.easygest.model.VersementsModel;
import com.jay.easygest.model.VersementsaccModel;
import com.jay.easygest.outils.AccessLocalAccount;

//import com.jay.easygest.outils.AccessLocalVersementacc;
import com.jay.easygest.outils.AccessLocalVersementacc;
import com.jay.easygest.outils.MesOutils;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class Versementacccontrolleur {

    private static Versementacccontrolleur versementacccontrolleurInstance = null;
    private static AccessLocalVersementacc accessLocalVersementacc;
    private static AccessLocalAccount accessLocalAccount;
    private MutableLiveData<ArrayList<VersementsaccModel>> mlisteversementacc = new MutableLiveData<>();
    private MutableLiveData<VersementsaccModel> mversementacc = new MutableLiveData<>();

    public static Versementacccontrolleur getVersementacccontrolleurInstance(Context contexte) {
        if(Versementacccontrolleur.versementacccontrolleurInstance == null){
            Versementacccontrolleur.versementacccontrolleurInstance = new Versementacccontrolleur();
            accessLocalVersementacc = new AccessLocalVersementacc(contexte);
           accessLocalAccount = new AccessLocalAccount(contexte);

        }

        return versementacccontrolleurInstance;
    }

    public MutableLiveData<ArrayList<VersementsaccModel>> getMlisteversementacc() {
        return mlisteversementacc;
    }

    public void setMlisteversementacc(ArrayList<VersementsaccModel> listeversementacc) {
        this.mlisteversementacc.setValue(listeversementacc);
    }

    public MutableLiveData<VersementsaccModel> getMversementacc() {
        return mversementacc;
    }

    public void setMversementacc(VersementsaccModel versementacc) {
        this.mversementacc.setValue(versementacc);
    }

    public boolean ajouterversement(ClientModel client, long sommeverse, String dateversement) {

        boolean success = accessLocalVersementacc.ajouterversement(client,sommeverse,dateversement);
        return success;

    }


    public ArrayList<VersementsaccModel> listeversementsClient(ClientModel client) {
        ArrayList<VersementsaccModel> liste_versements = accessLocalVersementacc.listeVersementsClient(client);
        setMlisteversementacc(liste_versements);
        return liste_versements ;
    }

    public boolean modifierVersement(AccountModel account, VersementsaccModel versement_a_modifier, int nouveau_total_versement, int nouvellesommeverse, String date) {
        long dateversement =  MesOutils.convertStringToDate(date).getTime();
        boolean resultat = accessLocalVersementacc.modifierVersement(account,versement_a_modifier,nouveau_total_versement,nouvellesommeverse,dateversement);
        return resultat;
    }

    public boolean annullerversement(VersementsaccModel versement,AccountModel accountModel){

        return  accessLocalVersementacc.annullerversement(versement,accountModel);
    }

    public void supprimerversement(VersementsaccModel versementsaccModel) {
        accessLocalVersementacc.supprimerversement(versementsaccModel);
    }



}