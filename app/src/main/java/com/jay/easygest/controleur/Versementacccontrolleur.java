package com.jay.easygest.controleur;
import android.content.Context;
import androidx.lifecycle.MutableLiveData;
import com.jay.easygest.model.AccountModel;
import com.jay.easygest.model.ClientModel;
import com.jay.easygest.model.VersementsaccModel;
import com.jay.easygest.outils.AccessLocalVersementacc;
import com.jay.easygest.outils.MesOutils;
import java.util.ArrayList;

public class Versementacccontrolleur {

    private static Versementacccontrolleur versementacccontrolleurInstance = null;
    private static AccessLocalVersementacc accessLocalVersementacc;
    private final MutableLiveData<ArrayList<VersementsaccModel>> mlisteversementacc = new MutableLiveData<>();
    private final MutableLiveData<VersementsaccModel> mversementacc = new MutableLiveData<>();

    public static Versementacccontrolleur getVersementacccontrolleurInstance(Context contexte) {
        if(Versementacccontrolleur.versementacccontrolleurInstance == null){
            Versementacccontrolleur.versementacccontrolleurInstance = new Versementacccontrolleur();
            accessLocalVersementacc = new AccessLocalVersementacc(contexte);

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
        return accessLocalVersementacc.ajouterversement(client,sommeverse,dateversement);

    }


    public void listeversementsClient(ClientModel client) {
        ArrayList<VersementsaccModel> liste_versements = accessLocalVersementacc.listeVersementsClient(client);
        setMlisteversementacc(liste_versements);
    }

    public boolean modifierVersement(AccountModel account, VersementsaccModel versement_a_modifier, int nouveau_total_versement, int nouvellesommeverse, String date) {
        long dateversement =  MesOutils.convertStringToDate(date).getTime();
        boolean success = accessLocalVersementacc.modifierVersement(account,versement_a_modifier,nouveau_total_versement,nouvellesommeverse,dateversement);
        if (success){
            VersementsaccModel versementacc = accessLocalVersementacc.recupVersementaccById(versement_a_modifier.getId());
            this.setMversementacc(versementacc);
            this.listeversementsClient(versementacc.getClient());

        }
        return success;
    }

    public boolean annullerversement(VersementsaccModel versement,AccountModel accountModel){

        return  accessLocalVersementacc.annullerversement(versement,accountModel);
    }

}
