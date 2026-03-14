package com.jay.easygest.controleur;

import android.content.Context;
import androidx.lifecycle.MutableLiveData;

import com.jay.easygest.model.AccountModel;
import com.jay.easygest.model.Article;
import com.jay.easygest.model.ArticlesModel;
import com.jay.easygest.model.ClientModel;
import com.jay.easygest.model.VersementsaccModel;
import com.jay.easygest.outils.AccessLocalAccount;
import com.jay.easygest.outils.AccessLocalInfo;
import com.jay.easygest.outils.AccessLocalVersementacc;
import com.jay.easygest.outils.MesOutils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Accountcontroller {

    private static Accountcontroller accountcontrolleurInstance = null;
    private static AccessLocalAccount accessLocalAccount;
    private static AccessLocalInfo accessLocalInfo;
    private final MutableLiveData<AccountModel>  maccount = new MutableLiveData<>();
    private final MutableLiveData<ArrayList<AccountModel>> maccounts = new MutableLiveData<>();

    private final MutableLiveData<Integer> mtotalresteaccountclient = new MutableLiveData<>();
    private final  MutableLiveData<Integer> mtotalaccountClient = new MutableLiveData<>();

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


    /**
     *
     * @param codeclt code du client
     * @param nomclient nom du client
     * @param prenomsclient prenoms du client
     * @param telephone le telephone du client
     * @param article1vendu premier article vendu
     * @param article2vendu deuxieme article vendu
     * @param versement le prmier versement
     * @param dateaccount la date de l'account
     * @return cree un account si le client n'existe pas
     */
    public AccountModel creerAccount(String codeclt, String nomclient, String prenomsclient, String telephone , Article article1vendu, Article article2vendu, String versement, long dateaccount, ArticlesModel article1,ArticlesModel article2){

        int nbr_articles1_restant = article1.getQuantite() - article1vendu.getNbrarticle();
        int nbr_articles2_restant = article2.getQuantite() - article2vendu.getNbrarticle();

        Map<String, Object> newdata = new HashMap<>();
        newdata.put("article1",article1);
        newdata.put("article2",article2);
        newdata.put("nbrarticle1restant",nbr_articles1_restant);
        newdata.put("nbrarticle2restant",nbr_articles2_restant);

        AccountModel premieraccount = new AccountModel( codeclt,nomclient,prenomsclient,article1vendu, article2vendu, Integer.parseInt(versement),dateaccount,1);
        AccountModel account  = accessLocalAccount.creerCompteAccount(premieraccount,telephone,newdata);
        if (account != null){
//            accessLocalInfo.updateAccountInfos(premieraccount.getSommeaccount());
            this.setAccount(account);
        }
        return  account;
    }


    /**
     * @param client        le client concerné
     * @param article1_vendu      premier article vendu
     * @param article2_vendu      deuxieme article vencu
     * @param versement     premier versement pour l'account
     * @param dateaccount   la date de l'opération
     * @param article1      le premier article passé en commande
     * @param article2      le deuxieme article passé en commande
     * @return retourne vraie si l"account à été crée avec succes sinon faux
     */
    public boolean ajouterAccount(ClientModel client, Article article1_vendu, Article article2_vendu, String versement, long dateaccount, ArticlesModel article1, ArticlesModel article2) {

        int numeroaccount = client.getNbraccount()+1;
        AccountModel account = new AccountModel(0,client, article1_vendu, article2_vendu,Integer.parseInt(versement),dateaccount,numeroaccount);
        AccountModel accountModel = accessLocalAccount.ajouterAccount(account,article1,article2);
        boolean success = false ;
        if (accountModel != null){
            accessLocalInfo.updateAccountInfos(accountModel.getSommeaccount());
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
        ArrayList<VersementsaccModel> liste_versements = accessLocalVersementacc.listeVersementsAccount(accountModel);
        int last_index  = liste_versements.size() -1;
        VersementsaccModel dernier_versemt = liste_versements.get(last_index);
        date_de_solde = dernier_versemt.getDateversement();
        accountModel.setSoldedat(date_de_solde);
        boolean success = false;
        AccountModel account = accessLocalAccount.modifierAccount(accountModel,client,ancienne_somme_account);
        if (account != null ){
            int somme_a_verse = accountModel.getSommeaccount()-ancienne_somme_account;
            accessLocalInfo.modifierAccountInfos(somme_a_verse);
            this.setAccount(account);

            this.listeAccountsClient(client);
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
//            accessLocalInfo.annullerAccountInfos(account);
            this.listeaccounts();
        }
        return success;
    }


    /**
     * sauvegarde la liste des accounts en cours
     */
    public void listeaccounts(){
        ArrayList<AccountModel> listeAccounts = accessLocalAccount.listeAccounts();
        this.setAccounts(listeAccounts);
    }


    /**
     * @param client le client
     * suvegarde la liste des accounts d'un client
     */
    public void listeAccountsClient(ClientModel client) {
        ArrayList<AccountModel> accounts = accessLocalAccount.listeAccountsClient(client);
        this.setAccounts(accounts);
    }

    /**
     *
     * @param client le client
     * suvegarde la liste des accounts d'un client
     */
    public void  listeAccountsoldeClient(ClientModel client) {
        ArrayList<AccountModel> accounts = accessLocalAccount.listeDESAccountsSoldesClient(client);
        this.setAccounts(accounts);

    }

    public  void setRecapTaccountClient(ClientModel client){
        int totalaccount   = accessLocalAccount.getRecapTaccountClient(client);
        mtotalaccountClient.setValue(totalaccount);

    }

    public MutableLiveData<Integer> getRecapTaccountClient(){
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

    public AccountModel modifierArticledunAccount(AccountModel ancienaccount,String nouveauprixarticle, String nouvelleqtearticle, String nomarticleAModifier, ArticlesModel articleselectione) {

        ClientModel client = ancienaccount.getClient();
        AccountModel nouveau_account = null;
        Article ancien_article = null;
        Article nouvel_article = null;
        if (nomarticleAModifier.equals("article1") ){
            ancien_article = ancienaccount.getArticle1();
            nouvel_article = new Article(articleselectione.getDesignation(),Integer.parseInt(nouveauprixarticle),Integer.parseInt(nouvelleqtearticle));
            nouveau_account = new AccountModel(ancienaccount.getId(),client,nouvel_article,ancienaccount.getArticle2(),ancienaccount.getVersement(),ancienaccount.getDateaccount(),ancienaccount.getNumeroaccount());
        }

        if (nomarticleAModifier.equals("article2") ){
             ancien_article = ancienaccount.getArticle1();
            nouvel_article = new Article(articleselectione.getDesignation(),Integer.parseInt(nouveauprixarticle),Integer.parseInt(nouvelleqtearticle));
            nouveau_account = new AccountModel(ancienaccount.getId(),client,ancienaccount.getArticle1(),nouvel_article,ancienaccount.getVersement(),ancienaccount.getDateaccount(),ancienaccount.getNumeroaccount());

        }
//        int ancienne_somme_account = ancienaccount.getSommeaccount();
        return  accessLocalAccount.modifierArticledunAccount(nouveau_account,ancienaccount,client,nouvel_article,ancien_article);
    }

    public AccountModel modifierDateAccount(AccountModel account,String date) {
        return accessLocalAccount.modifierDateAccount(account, MesOutils.convertStringToDate(date).getTime());
    }
}
