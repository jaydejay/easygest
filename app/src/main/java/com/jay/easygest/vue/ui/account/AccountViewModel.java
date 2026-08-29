package com.jay.easygest.vue.ui.account;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.jay.easygest.controleur.Accountcontroller;
import com.jay.easygest.model.AccountModel;

import java.util.ArrayList;

public class AccountViewModel extends ViewModel {

    private final MutableLiveData<ArrayList<AccountModel>> accounts;
    private final MutableLiveData<AccountModel> account;
    private final MutableLiveData<Integer> totalaccountsclient ;
    private final MutableLiveData<Integer> totalrestesclient ;

    private  MutableLiveData<Integer> totalaccounts ;
    private  MutableLiveData<Integer> totalversements ;
    private  MutableLiveData<Integer> totalrestes ;

    public AccountViewModel() {
        Accountcontroller accountcontroller = Accountcontroller.getAccountcontrolleurInstance(null);
        totalaccounts = accountcontroller.getRecapTaccount();
        totalversements = accountcontroller.getRecapTversement();
        totalrestes = accountcontroller.getRecapTreste();
        this.accounts = accountcontroller.getMaccounts();
        this.account = accountcontroller.getMaccount();
        totalaccountsclient = accountcontroller.getRecapTaccountClient();
        totalrestesclient = accountcontroller.getRecapTresteClient();

    }

    /**
     *
     * @return retourne la liste de tous les accounts soldes ou pas
     */
    public MutableLiveData<ArrayList<AccountModel>> getAccounts() {
        return accounts;
    }



    /**
     *
     * @return retour l'account sauvegardé
     */
    public MutableLiveData<AccountModel> getAccount() {
        return account;
    }

    public MutableLiveData<Integer> getTotalaccountsclient() {
        return totalaccountsclient;
    }


    public MutableLiveData<Integer> getTotalrestesclient() {
        return totalrestesclient;
    }


    public MutableLiveData<Integer> getTotalaccounts() {return totalaccounts;}
    public MutableLiveData<Integer> getTotalversements() {
        return totalversements;
    }
    public MutableLiveData<Integer> getTotalrestes() {
        return totalrestes;
    }
}