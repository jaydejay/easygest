package com.jay.easygest.controleur;
import android.content.Context;
import androidx.lifecycle.MutableLiveData;
import com.jay.easygest.model.ClientModel;
import com.jay.easygest.model.CreditModel;
import com.jay.easygest.model.VersementsModel;
import com.jay.easygest.outils.AccessLocalVersement;
import com.jay.easygest.outils.MesOutils;
import java.util.ArrayList;

public class Versementcontrolleur {

    private static Versementcontrolleur versementcontrolleurInstance = null;
    private static AccessLocalVersement accessLocalVersement;
    private VersementsModel versement;
    private final MutableLiveData<VersementsModel> Mversement = new MutableLiveData<>();
    private final MutableLiveData<ArrayList<VersementsModel>> Mversements = new MutableLiveData<>();

    public Versementcontrolleur(){
        super();
    }

    public static Versementcontrolleur getVersementcontrolleurInstance(Context contexte) {
        if(Versementcontrolleur.versementcontrolleurInstance == null){
            Versementcontrolleur.versementcontrolleurInstance = new Versementcontrolleur();
            accessLocalVersement = new AccessLocalVersement(contexte);

        }

        return versementcontrolleurInstance;
    }


    public VersementsModel getVersement() {
        return versement;
    }

    public void setVersement(VersementsModel versement) {
        this.versement = versement;
    }

    public MutableLiveData<VersementsModel> getMversement() {
        return Mversement;
    }

    public void setMversement(VersementsModel versement) {
        Mversement.setValue(versement);
    }

    public MutableLiveData<ArrayList<VersementsModel>> getMversements() {
        return Mversements;
    }

    public void setMversements(ArrayList<VersementsModel> versements) {
        Mversements.setValue(versements);
    }

    public boolean ajouterversement(ClientModel client, long sommeverse,String dateversement) {

        return  accessLocalVersement.ajouterversement(client,sommeverse,dateversement);
    }


    public ArrayList<VersementsModel> listeversements() {
        ArrayList<VersementsModel> liste_versements = accessLocalVersement.listeVersement();
        setMversements(liste_versements);
        return liste_versements ;
    }

    public boolean modifierVersement(CreditModel credit,VersementsModel versement_a_modifier,int nouveau_total_versement, int nouvellesommeverse, String date) {
       long dateversement =  MesOutils.convertStringToDate(date).getTime();

        boolean succes  = accessLocalVersement.modifierVersement(credit,versement_a_modifier,nouveau_total_versement,nouvellesommeverse,dateversement);
        if (succes){
            VersementsModel versement = accessLocalVersement.recupVersementById(versement_a_modifier.getId());
            this.setMversement(versement);
            this.listeversements();
        }
        return succes;
    }

    public boolean annullerversement(VersementsModel versement,CreditModel credit){

        return  accessLocalVersement.annullerversement(versement,credit);
    }


}
