package com.jay.easygest.vue.ui.credit;


import android.os.Build;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.jay.easygest.controleur.Creditcontrolleur;
import com.jay.easygest.model.ClientModel;
import com.jay.easygest.model.CreditModel;

import java.util.ArrayList;
import java.util.Objects;


public class  CreditViewModel extends ViewModel {


    private final MutableLiveData<Integer> totalcredits;
    private final MutableLiveData<Integer> totalversements;
    private final MutableLiveData<Integer> totalrestes;
    private final MutableLiveData<CreditModel> credit;
    private final MutableLiveData<ArrayList<CreditModel>> credits;
    private final MutableLiveData<ArrayList<CreditModel>> credits_client_soldes_ou_non ;

    private final MutableLiveData<Integer> totalcreditsclient ;
    private final MutableLiveData<Integer> totalversementsclient ;
    private final MutableLiveData<Integer> totalrestesclient ;


    public CreditViewModel() {
        Creditcontrolleur creditcontrolleur = Creditcontrolleur.getCreditcontrolleurInstance(null);

        totalcredits = creditcontrolleur.getRecapTcredit();
        totalversements = creditcontrolleur.getRecapTversement();
        totalrestes = creditcontrolleur.getRecapTreste();

         totalcreditsclient = creditcontrolleur.getRecapTcreditClient();
         totalversementsclient = creditcontrolleur.getRecapTversementClient();
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

    public void setCredits_client_soldes_ou_non(ArrayList<CreditModel> credits_soldes_ou_non_client) {
        this.credits_client_soldes_ou_non.setValue(credits_soldes_ou_non_client);
    }

    public MutableLiveData<Integer> getTotalcreditsclient() {
        return totalcreditsclient;
    }

    public MutableLiveData<Integer> getTotalversementsclient() {
        return totalversementsclient;
    }

    public MutableLiveData<Integer> getTotalrestesclient() {
        return totalrestesclient;
    }

    /**
     *
     * @param clientid id du client
     * @return la liste des credits en cours d'un client
     */
    public ArrayList<CreditModel> getCreditsClients(Integer clientid){
        ArrayList<CreditModel> liste_credits_client = new ArrayList<>();

        for (CreditModel credit_client : Objects.requireNonNull(credits.getValue())) {

            if (credit_client.getClient().getId().equals(clientid)){
                liste_credits_client.add(credit_client);
            }

        }

        setCredits_client_soldes_ou_non(liste_credits_client);
        return liste_credits_client;
    }

    public ArrayList<CreditModel> getCreditsSoldesClient(Integer clientid){
        ArrayList<CreditModel> liste_credits_soldes_client = new ArrayList<>();

        for (CreditModel credit_client : Objects.requireNonNull(credits.getValue())) {

            if (credit_client.getClient().getId().equals(clientid)){
                liste_credits_soldes_client.add(credit_client);
            }

        }

        setCredits_client_soldes_ou_non(liste_credits_soldes_client);
        return liste_credits_soldes_client;
    }

    public Integer getSommeCreditsUnClient(ClientModel client){
        int total_somme_credit_client = 0;
        ArrayList<CreditModel> liste_credits_client = this.getCreditsClients(client.getId());

        for (CreditModel credit : liste_credits_client) {

            total_somme_credit_client = total_somme_credit_client+credit.getSommecredit();
        }

        return total_somme_credit_client;
    }
}