package com.jay.easygest.vue.ui.credit;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.jay.easygest.controleur.Creditcontrolleur;
import com.jay.easygest.model.CreditModel;
import java.util.ArrayList;


public class  CreditViewModel extends ViewModel {


    private  MutableLiveData<Integer> totalcredits ;
    private  MutableLiveData<Integer> totalversements ;
    private  MutableLiveData<Integer> totalrestes ;
    private  MutableLiveData<CreditModel> credit ;
    private  MutableLiveData<ArrayList<CreditModel>> credits;
    private  MutableLiveData<ArrayList<CreditModel>> credits_client_soldes_ou_non ;
    private  MutableLiveData<Integer> totalcreditsclient ;
    private final MutableLiveData<Integer> totalrestesclient  ;

    public CreditViewModel() {

        Creditcontrolleur creditcontrolleur = Creditcontrolleur.getCreditcontrolleurInstance(null);

        totalcredits = creditcontrolleur.getRecapTcredit();
        totalversements = creditcontrolleur.getRecapTversement();
        totalrestes = creditcontrolleur.getRecapTreste();

        totalcreditsclient = creditcontrolleur.getRecapTcreditClient();
        totalrestesclient = creditcontrolleur.getRecapTresteClient();
        credit = creditcontrolleur.getMCredit();
        credits = creditcontrolleur.getMCredits();
        credits_client_soldes_ou_non = creditcontrolleur.getMCredits();


    }

    public MutableLiveData<Integer> getTotalcredits() {
        return totalcredits;
    }
    public MutableLiveData<Integer> getTotalversements() {
        return totalversements;
    }
    public MutableLiveData<Integer> getTotalrestes() {
        return totalrestes;
    }
    public MutableLiveData<CreditModel> getCredit() {
        return credit;
    }
    public MutableLiveData<ArrayList<CreditModel>> getCredits() {
        return credits;
    }
    public MutableLiveData<ArrayList<CreditModel>> getCredits_client_soldes_ou_non() {
        return credits_client_soldes_ou_non;
    }
    public MutableLiveData<Integer> getTotalcreditsclient() { return totalcreditsclient; }

    public MutableLiveData<Integer> getTotalrestesclient() {
        return totalrestesclient;
    }

}