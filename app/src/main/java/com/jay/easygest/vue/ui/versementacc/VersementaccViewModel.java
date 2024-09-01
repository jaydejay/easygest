package com.jay.easygest.vue.ui.versementacc;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.jay.easygest.controleur.Versementacccontrolleur;
import com.jay.easygest.model.VersementsaccModel;

import java.util.ArrayList;

public class VersementaccViewModel extends ViewModel {

    private MutableLiveData<ArrayList<VersementsaccModel>> mlisteversementacc;
    private MutableLiveData<VersementsaccModel> mversementacc;

    public VersementaccViewModel() {
        Versementacccontrolleur versementacccontrolleur = Versementacccontrolleur.getVersementacccontrolleurInstance(null);
        this.mlisteversementacc = versementacccontrolleur.getMlisteversementacc();
        this.mversementacc = versementacccontrolleur.getMversementacc();
    }

    public MutableLiveData<ArrayList<VersementsaccModel>> getMlisteversementacc() {
        return mlisteversementacc;
    }

    public MutableLiveData<VersementsaccModel> getMversementacc() {
        return mversementacc;
    }
}
