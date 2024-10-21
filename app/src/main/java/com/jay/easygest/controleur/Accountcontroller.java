package com.jay.easygest.controleur;

import android.content.Context;
import androidx.lifecycle.MutableLiveData;
import com.jay.easygest.model.AccountModel;
import com.jay.easygest.model.Articles;
import com.jay.easygest.model.ClientModel;
import com.jay.easygest.model.VersementsaccModel;
import com.jay.easygest.outils.AccessLocalAccount;
import com.jay.easygest.outils.AccessLocalInfo;
import com.jay.easygest.outils.AccessLocalVersementacc;
import com.owlike.genson.Genson;

import java.util.ArrayList;

public class Accountcontroller {

    private static Accountcontroller accountcontrolleurInstance = null;
    private static AccessLocalAccount accessLocalAccount;
    private static AccessLocalInfo accessLocalInfo;
    private final MutableLiveData<AccountModel>  maccount = new MutableLiveData<>();
    private final MutableLiveData<ArrayList<AccountModel>> maccounts = new MutableLiveData<>();

    private final MutableLiveData<Integer> mtotalresteaccountclient = new MutableLiveData<>();
    private final  MutableLiveData<Integer> mtotalaccountClient = new MutableLiveData<>();



    /**
     * la liste de tous les accounts
     */
    private final MutableLiveData<ArrayList<AccountModel>> mlisteaccounts = new MutableLiveData<>();
    private static AccessLocalVersementacc accessLocalVersementacc;

    public Accountcontroller() {
        super();
    }

    public static Accountcontroller getAccountcontrolleurInstance(Context contexte){
        if(Accountcontroller.accountcontrolleurInstance == null){
            Accountcontroller.accountcontrolleurInstance = new Accountcontroller();
            accessLocalAccount = new AccessLocalAccount(contexte);
            accessLocalVersementacc = new AccessLocalVersementacc(contexte);
            accessLocalInfo = new AccessLocalInfo(contexte);
        }
        return accountcontrolleurInstance;
    }


    public void setAccount(AccountModel accountModel) {
        this.maccount.setValue(accountModel);
    }


    public MutableLiveData<AccountModel> getMaccount() {
        return maccount;
    }

    public void setAccounts(ArrayList<AccountModel> accountsmodel) {
        this.maccounts.setValue(accountsmodel);
    }


    public MutableLiveData<ArrayList<AccountModel>> getMaccounts() {
        return maccounts;
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
     * @return cree un account si le client n'existe pas
     */
    public AccountModel creerAccount(String codeclt, String nomclient, String prenomsclient, String telephone , Articles c_article1, Articles c_article2, String versement, long dateaccount){

        String article1 = new Genson().serialize(c_article1);
        String article2 = new Genson().serialize(c_article2);

        int sommeaccoount = c_article1.getSomme() + c_article2.getSomme();
        int reste = sommeaccoount - Integer.parseInt(versement);

        AccountModel premieraccount = new AccountModel( codeclt,nomclient,prenomsclient,article1, article2,sommeaccoount, Integer.parseInt(versement), reste,dateaccount,1);
        AccountModel account  = accessLocalAccount.creerCompteAccount(premieraccount,codeclt,nomclient,prenomsclient,telephone,versement);
        if (account != null){
            accessLocalInfo.updateAccountInfos(sommeaccoount);
            this.listeDesAccounts();
            this.setAccount(account);
        }
        return  account;
    }


    /**
     *
     * @param client le client concerné
     * @param c_article1 premier article
     * @param c_article2 deuxieme article
     * @param versement premier versement pour l'account
     * @param dateaccount la date de l'opération
     * @return  retourne vraie si l"account à été crée avec succes sinon faux
     */
    public boolean ajouterAccount( ClientModel client,Articles c_article1, Articles c_article2, String versement, long dateaccount) {

        String article1 = new Genson().serialize(c_article1);
        String article2 = new Genson().serialize(c_article2);

        int sommeaccount = c_article1.getSomme() + c_article2.getSomme();
        int reste = sommeaccount - Integer.parseInt(versement);
        int numeroaccount = client.getNbraccount()+1;
        AccountModel account = new AccountModel(client.getCodeclient(), client.getNom(), client.getPrenoms(), article1, article2,sommeaccount, Integer.parseInt(versement), reste,dateaccount,numeroaccount);
        AccountModel accountModel = accessLocalAccount.ajouterAccount(account,client);
        boolean success = false ;
        if (accountModel != null){
            accessLocalInfo.updateAccountInfos(sommeaccount);
            this.listeaccounts();
            this.setAccount(accountModel);
            success = true;
        }
        return  success;

    }

    /**
     *
     * @param accountModel l'account modifier
     * @param client le client proprietaire de l'account
     * @param ancienne_somme_account ancienne somme avant modification
     * @return vraie si la requette reussie sinon faux
     */
    public boolean modifierAccount(AccountModel accountModel,ClientModel client, int ancienne_somme_account){
        long date_de_solde;
        if (accountModel.getReste() == 0){
            ArrayList<VersementsaccModel> liste_versements = accessLocalVersementacc.listeVersementsAccount(accountModel);
            int last_index  = liste_versements.size() -1;
            VersementsaccModel dernier_versemt = liste_versements.get(last_index);
            date_de_solde = dernier_versemt.getDateversement();
        }else {date_de_solde = 0L;}

        accountModel.setSoldedat(date_de_solde);
        boolean success = false;
        AccountModel account = accessLocalAccount.modifierAccount(accountModel,client,ancienne_somme_account);
        if (account != null ){
            int somme_a_verse = accountModel.getSommeaccount()-ancienne_somme_account;
            accessLocalInfo.modifierAccountInfos(somme_a_verse);
            this.setAccount(account);
            this.listeaccounts();
            success = true;
        }

        return success;
    }



    /**
     *
     * @param account l'account à annuller
     * @return retourne vraie si l'account a été annuller sinon faux
     */

    public boolean annullerAccount(AccountModel account){
        boolean  success = accessLocalAccount.anullerAccount(account);
        if (success){
            accessLocalInfo.annullerAccountInfos(account);
            this.listeaccounts();
        }
        return success;
    }



    /**
     * la liste de tous les accounts
     */
    public void listeDesAccounts(){
        ArrayList<AccountModel> accounts = new ArrayList<>();
        this.setMlisteaccounts(accounts);
    }

    /**
     *
     */
    public void listeaccounts(){
        ArrayList<AccountModel> listeAccounts = accessLocalAccount.listeAccounts();
        this.setAccounts(listeAccounts);
    }


    /**
     * @param client le client
     */
    public void listeAccountsClient(ClientModel client) {

        ArrayList<AccountModel> accounts = accessLocalAccount.listeAccountsClient(client);
        this.setAccounts(accounts);
    }

    public void  listeAccountsoldeClient(ClientModel client) {
        ArrayList<AccountModel> accounts = accessLocalAccount.listeDESAccountsSoldesClient(client);
        this.setAccounts(accounts);

    }

    public  void setRecapTaccountClient(ClientModel client){
        int totalaccount   = accessLocalAccount.getRecapTaccountClient(client);
        mtotalaccountClient.setValue(totalaccount);

    }

    public MutableLiveData<Integer> getRecapTaccounttClient(){
        return mtotalaccountClient;
    }

    public  void setRecapTresteClient(ClientModel client){
        int totalresteaccountclient = accessLocalAccount.getRecapTresteClient(client);
        mtotalresteaccountclient.setValue(totalresteaccountclient);
    }

    public MutableLiveData<Integer> getRecapTresteClient(){
        return mtotalresteaccountclient;
    }


    public void supprimerAccountsSoldes(AccountModel account) {
        boolean success = accessLocalAccount.supprimerAccountSoldeClient(account.getClient());
        if (success){
            this.listeAccountsoldeClient(account.getClient());
        }
    }

    public boolean isClientOwnAccount(ClientModel clientModel) {
        return accessLocalAccount.isClientOwnAccount(clientModel);
    }
}
