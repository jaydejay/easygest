package com.jay.easygest.controleur;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;

import com.jay.easygest.model.AccountModel;
import com.jay.easygest.model.Articles;
import com.jay.easygest.model.ClientModel;
//import com.jay.easygest.outils.AccessLocalAccount;
import com.jay.easygest.model.CreditModel;
import com.jay.easygest.outils.AccessLocalAccount;
import com.owlike.genson.Genson;

import java.util.ArrayList;

public class Accountcontroller {

    private static Accountcontroller accountcontrolleurInstance;
    private static AccessLocalAccount accessLocalAccount;
    private MutableLiveData<AccountModel>  maccount = new MutableLiveData<>();
    private MutableLiveData<ArrayList<AccountModel>> maccounts = new MutableLiveData<>();

    private final MutableLiveData<Integer> mtotalresteaccountclient = new MutableLiveData<>();
    private final MutableLiveData<Integer> mtotalversementaccountclient = new MutableLiveData<>();
    private final  MutableLiveData<Integer> mtotalaccountClient = new MutableLiveData<>();

    /**
     * la liste de tous les accounts
     */
    private MutableLiveData<ArrayList<AccountModel>> mlisteaccounts = new MutableLiveData<>();
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

    public MutableLiveData<ArrayList<AccountModel>> getMlisteaccounts() {
        return mlisteaccounts;
    }

    public void setMlisteaccounts(ArrayList<AccountModel> listeaccounts) {
        this.mlisteaccounts.setValue(listeaccounts);
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

            this.listeDesAccounts();
        }
        return  true;
    }

    /**
     * crée un account si le client existe
     * @return retourne vraie si l"account à été crée avec succes
     */
    public boolean ajouterAccount( ClientModel client,Articles c_article1, Articles c_article2, String versement, long dateaccount) {

        String article1 = new Genson().serialize(c_article1);
        String article2 = new Genson().serialize(c_article2);

        int sommeaccount = c_article1.getSomme() + c_article2.getSomme();
        int reste = sommeaccount - Integer.parseInt(versement);
        int numeroaccount = client.getNbrcredit()+1;
        AccountModel account = new AccountModel(client.getCodeclient(), client.getNom(), client.getPrenoms(), article1, article2,sommeaccount, Integer.parseInt(versement), reste,dateaccount,numeroaccount);
        boolean success = accessLocalAccount.ajouterAccount(account,client);
        if (success){

            this.listeDesAccounts();
        }
        return  success;

    }

    /**
     *
     * @param account l'account modifier
     * @param client le client proprietaire de l'account
     * @param ancienne_somme_account ancienne somme avant modification
     * @return vraie si la requette reussie sinon faux
     */
    public boolean modifierAccount(AccountModel account,ClientModel client, int ancienne_somme_account){

        boolean success = accessLocalAccount.modifierAccount(account,client,ancienne_somme_account);
        if (success){
            this.listeDesAccounts();
        }

        return success;
    }

    /**
     *
     * @param account l'account à annuller
     * @return retourne vraie si l'account a été annuller sinon faux
     */

    public boolean annullerAccout(AccountModel account){
        boolean  success = accessLocalAccount.anullerAccount(account);
        if (success){
            this.listeDesAccounts();
        }
        return success;
    }

    /**
     *
     * @param accountId id de l'account à recuperer
     * @return retourne l'account
     */

    public AccountModel getAccountById(int accountId){
        AccountModel account = accessLocalAccount.recupAccountById(accountId);
        return account;
    }

    /**
     *
     * @return la liste de tous les accounts
     */
    public void listeDesAccounts(){
        ArrayList<AccountModel> accounts = new ArrayList<>();
        this.setMlisteaccounts(accounts);
    }

    /**
     *
     * @return la liste des accounts en cours
     */
    public ArrayList<AccountModel> listeaccounts(){
        ArrayList<AccountModel> listeAccounts = accessLocalAccount.listeAccounts();
        this.setAccounts(listeAccounts);
        return listeAccounts;
    }


    /**
     *
     * @param client le client
     * @return retourne la liste des accounts d'un client
     */
    public  ArrayList<AccountModel> listeAccountsClient(ClientModel client) {

        ArrayList<AccountModel> accounts = accessLocalAccount.listeAccountsClient(client);
        this.setAccounts(accounts);
        return accounts;
    }

    public void  listeAccountsoldeClient(ClientModel client) {

        ArrayList<AccountModel> accounts = accessLocalAccount.listeDESAccountsSoldesClient(client);
        this.setAccounts(accounts);

    }

    public  void setRecapTaccountClient(ClientModel client){
        int totalcredit   = accessLocalAccount.getRecapTaccountClient(client);
        mtotalaccountClient.setValue(totalcredit);

    }

    public  void setRecapTversementClient(ClientModel client ){
        int totalversementclient = accessLocalAccount.getRecapTversementClient(client);
        mtotalversementaccountclient.setValue(totalversementclient);
    }

    public  void setRecapTresteClient(ClientModel client){
        int totalresteaccountclient = accessLocalAccount.getRecapTresteClient(client);
        mtotalresteaccountclient.setValue(totalresteaccountclient);
    }

    public MutableLiveData<Integer> getRecapTaccounttClient(){
        return mtotalaccountClient;
    }

    public MutableLiveData<Integer> getRecapTversementClient(){
        return mtotalversementaccountclient;
    }

    public MutableLiveData<Integer> getRecapTresteClient(){
        return mtotalresteaccountclient;
    }





}
