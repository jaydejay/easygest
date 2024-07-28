package com.jay.easygest.controleur;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;

import com.jay.easygest.model.AccountModel;
import com.jay.easygest.model.Articles;
import com.jay.easygest.model.ClientModel;
import com.jay.easygest.model.CreditModel;
import com.jay.easygest.outils.AccessLocalAccount;
import com.jay.easygest.outils.AccessLocalCredit;
import com.owlike.genson.Genson;

import java.util.ArrayList;

public class Accountcontroller {

    private static Accountcontroller accountcontrolleurInstance;
    private static AccessLocalAccount accessLocalAccount;
    private MutableLiveData<AccountModel>  maccount;
    private MutableLiveData<ArrayList<AccountModel>>  maccounts;
    public Accountcontroller() {
        super();
    }

    public static Accountcontroller getAccountcontrolleurInstance(Context contexte){
        if(Accountcontroller.accountcontrolleurInstance == null){
            Accountcontroller.accountcontrolleurInstance = new Accountcontroller();
            accessLocalAccount = new AccessLocalAccount(contexte);
        }
        return accountcontrolleurInstance;
    }


    public void setAccount(AccountModel accountModel) {
        this.maccount.setValue(accountModel);
    }

    public void setAccounts(ArrayList<AccountModel> accountsmodel) {
        this.maccounts.setValue(accountsmodel);
    }

    public MutableLiveData<AccountModel> getMaccount() {
        return maccount;
    }

    public MutableLiveData<ArrayList<AccountModel>> getMaccounts() {
        return maccounts;
    }

    /**
     *
     * @param codeclt code du client
     * @param nomclient nom du client
     * @param prenomsclient prenoms du client
     * @param telephone le telephone du client
     * @param c_article1 premier article
     * @param c_article2 deuxieme article
     * @param versement le prmier versement
     * @param dateaccount la date de l'account
     * @return
     */
    public boolean creerAccount(String codeclt, String nomclient, String prenomsclient, String telephone , Articles c_article1, Articles c_article2, String versement, long dateaccount){

        String article1 = new Genson().serialize(c_article1);
        String article2 = new Genson().serialize(c_article2);

        int sommeaccoount = c_article1.getSomme() + c_article2.getSomme();
        int reste = sommeaccoount - Integer.parseInt(versement);

        AccountModel premieraccount = new AccountModel( codeclt,nomclient,prenomsclient,article1, article2,sommeaccoount, Integer.parseInt(versement), reste,dateaccount,1);
        boolean success = accessLocalAccount.creerCompteAccount(premieraccount,codeclt,nomclient,prenomsclient,telephone,versement);
        if (success){

            this.setAccount(premieraccount);
        }
        return  success;
    }

    /**
     * crée un account si le client existe
     * @return retourne vraie si l"account à été crée avec succes
     */
    public boolean ajouterAccount(){
        return true;
    }

    /**
     *
     * @param account l'account
     * @return retourne vraie si l"account à été modifié avec succes
     */
    public boolean modifierAccount(AccountModel account){

        return true;
    }

    public AccountModel getAccountById(int accountId){
        AccountModel account = accessLocalAccount.recupAccountById(accountId);
        return account;
    }

    /**
     *
     * @return la liste de tous les accounts
     */
    public ArrayList<AccountModel> listeDesAccounts(){
        ArrayList<AccountModel> accounts = new ArrayList<>();

        return accounts;
    }

    /**
     *
     * @return la liste des accounts en cours
     */
    public ArrayList<AccountModel> listeAccounts(){
        ArrayList<AccountModel> accounts = accessLocalAccount.listeAccounts();

        return accounts;
    }


    public static ArrayList<AccountModel> listeaccountsclient(ClientModel client) {

        ArrayList<AccountModel> aaccounts = accessLocalAccount.listeAccountsclient(client);
        return aaccounts;
    }


}
