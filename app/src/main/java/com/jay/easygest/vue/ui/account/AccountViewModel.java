package com.jay.easygest.vue.ui.account;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.jay.easygest.controleur.Accountcontroller;
import com.jay.easygest.model.AccountModel;

import java.util.ArrayList;

public class AccountViewModel extends ViewModel {

    private MutableLiveData<ArrayList<AccountModel>> account_solde_ou_non ;
    private MutableLiveData<AccountModel> account;
    private Accountcontroller accountcontroller;
    private final MutableLiveData<Integer> totalaccountsclient ;
    private final MutableLiveData<Integer> totalversementsclient ;
    private final MutableLiveData<Integer> totalrestesclient ;

    public AccountViewModel() {
        accountcontroller =  Accountcontroller.getAccountcontrolleurInstance(null);
        this.account_solde_ou_non = new MutableLiveData<>();
        this.account = new MutableLiveData<>();
        this.account = accountcontroller.getMaccount();
        this.account_solde_ou_non = accountcontroller.getMaccounts();

        totalaccountsclient = accountcontroller.getRecapTaccounttClient();
        totalversementsclient = accountcontroller.getRecapTversementClient();
        totalrestesclient = accountcontroller.getRecapTresteClient();

    }

    public MutableLiveData<ArrayList<AccountModel>> getAccount_solde_ou_non() {
        return account_solde_ou_non;
    }

    public MutableLiveData<AccountModel> getAccount() {
        return account;
    }

    public MutableLiveData<Integer> getTotalaccountsclient() {
        return totalaccountsclient;
    }

    public MutableLiveData<Integer> getTotalversementsclient() {
        return totalversementsclient;
    }

    public MutableLiveData<Integer> getTotalrestesclient() {
        return totalrestesclient;
    }
}