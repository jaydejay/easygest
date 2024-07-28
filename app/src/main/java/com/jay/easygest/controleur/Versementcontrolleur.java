package com.jay.easygest.controleur;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.jay.easygest.model.ClientModel;
import com.jay.easygest.model.CreditModel;
import com.jay.easygest.model.VersementsModel;
import com.jay.easygest.outils.AccessLocalCredit;
import com.jay.easygest.outils.AccessLocalVersement;
import com.jay.easygest.outils.MesOutils;

import java.util.ArrayList;

public class Versementcontrolleur {

    private static Versementcontrolleur versementcontrolleurInstance = null;
    private static AccessLocalVersement accessLocalVersement;
    private static AccessLocalCredit accessLocalCredit;
    private VersementsModel versement;
    private final MutableLiveData Mversement = new MutableLiveData<>();
    private final MutableLiveData<ArrayList<VersementsModel>> Mversements = new MutableLiveData<>();
    private int versementNumber;

    public Versementcontrolleur(){
        super();
    }

    public static Versementcontrolleur getVersementcontrolleurInstance(Context contexte) {
        if(Versementcontrolleur.versementcontrolleurInstance == null){
            Versementcontrolleur.versementcontrolleurInstance = new Versementcontrolleur();
            accessLocalVersement = new AccessLocalVersement(contexte);
            accessLocalCredit = new AccessLocalCredit(contexte);

        }

        return versementcontrolleurInstance;
    }


    public VersementsModel getVersement() {
        return versement;
    }

    public void setVersement(VersementsModel versement) {
        this.versement = versement;
    }

    public MutableLiveData getMversement() {
        return Mversement;
    }

    public void setMversement(VersementsModel versement) {
        Mversement.setValue(versement); ;
    }

    public MutableLiveData<ArrayList<VersementsModel>> getMversements() {
        return Mversements;
    }

    public void setMversements(ArrayList<VersementsModel> versements) {
        Mversements.setValue(versements);
    }

    public int getVersementNumber() {
        return versementNumber;
    }

    public void setVersementNumber(int versementNumber) {
        this.versementNumber = versementNumber;
    }


    public boolean ajouterversement(ClientModel client, long sommeverse,String dateversement) {

        boolean success = accessLocalVersement.ajouterversement(client,sommeverse,dateversement);
        return success;

    }



    public ArrayList<VersementsModel> listeversements() {
        ArrayList<VersementsModel> liste_versements = accessLocalVersement.listeVersement();
        setMversements(liste_versements);
        return liste_versements ;
    }

    public boolean modifierVersement(CreditModel credit,VersementsModel versement_a_modifier,int nouveau_total_versement, int nouvellesommeverse, String date) {
       long dateversement =  MesOutils.convertStringToDate(date).getTime();
        boolean resultat = accessLocalVersement.modifierVersement(credit,versement_a_modifier,nouveau_total_versement,nouvellesommeverse,dateversement);
        return resultat;
    }

    public void supprimerversement(String codeclient) {
        accessLocalVersement.supprimerversement(codeclient);
    }

    public boolean annullerversement(VersementsModel versement,CreditModel credit){

        return  accessLocalVersement.annullerversement(versement,credit);
    }


}
