package com.jay.easygest.controleur;


import android.content.Context;

import androidx.lifecycle.MutableLiveData;

import com.jay.easygest.model.Article;
import com.jay.easygest.model.ArticlesModel;
import com.jay.easygest.model.ClientModel;
import com.jay.easygest.model.CreditModel;
import com.jay.easygest.model.VersementsModel;
import com.jay.easygest.outils.AccessLocalCredit;
import com.jay.easygest.outils.AccessLocalInfo;
import com.jay.easygest.outils.AccessLocalVersement;
import com.jay.easygest.outils.MesOutils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public final class Creditcontrolleur {

    private static Creditcontrolleur creditcontrolleurInstance = null;
    private CreditModel credit;
    private ArrayList<CreditModel> credits = new ArrayList<>();
    private static AccessLocalCredit accessLocalcredit;
    private static AccessLocalInfo accessLocalInfo ;
    private static AccessLocalVersement accessLocalVersement;
    private final MutableLiveData<Integer> mtotalcredit = new MutableLiveData<>();
    private final MutableLiveData<Integer> mtotalversement = new MutableLiveData<>();
    private final MutableLiveData<Integer> mtotalreste = new MutableLiveData<>();
    private  final  MutableLiveData<CreditModel> mcredit = new MutableLiveData<>();
    private  final  MutableLiveData<ArrayList<CreditModel>> mcredits = new MutableLiveData<>();
    private final MutableLiveData<Integer> mtotalresteclient = new MutableLiveData<>();
    private final MutableLiveData<Integer> mtotalversementclient = new MutableLiveData<>();
    private final  MutableLiveData<Integer> mtotalcreditClient = new MutableLiveData<>();

//    private Integer idmenu;

    /**
     * constructeur
     */
    private Creditcontrolleur(){
        super();

    }

    public static Creditcontrolleur getCreditcontrolleurInstance(Context contexte){

        if(Creditcontrolleur.creditcontrolleurInstance == null){
            Creditcontrolleur.creditcontrolleurInstance = new Creditcontrolleur();
            accessLocalcredit = new AccessLocalCredit(contexte);
            accessLocalVersement = new AccessLocalVersement(contexte);
            accessLocalInfo = new AccessLocalInfo(contexte);
        }

        return creditcontrolleurInstance;
    }


    public CreditModel getCredit() {
        return credit;
    }

    public void setCredit(CreditModel credit ) {
        this.credit = credit;
        setMCredit(credit);
    }

    public void setCredits(ArrayList<CreditModel> credits) {
        this.credits = credits;
        setMCredits(credits);
    }


    public MutableLiveData<CreditModel> getMCredit() {
        return mcredit;
    }
    public void setMCredit(CreditModel credit ) {
        this.mcredit.setValue(credit);
    }
    public MutableLiveData<ArrayList<CreditModel>> getMCredits() {return mcredits;}

    public void setMCredits(ArrayList<CreditModel> credits) {this.mcredits.setValue(credits); }

    public CreditModel creerCredit(Map<String,Object> data){

        Article article1vendu = (Article) data.get("article1vendu");
        Article article2vendu = (Article) data.get("article2vendu");

        CreditModel premiercredit = new CreditModel((String) data.get("codeclient"), (String) data.get("nomclient"),
                (String) data.get("prenomclient"),article1vendu, article2vendu, Integer.parseInt((String) data.get("versement")),
                 (Long) data.get("dateouverture"),1);

        CreditModel credit = accessLocalcredit.creerCompteCredit(premiercredit,data);
        if (credit != null){
            credits.add(premiercredit);
            this.setCredits(credits);
            this.setCredit(credit);
        }
        return  credit;
    }

    public boolean ajouterCredit(Map<String, Object> data) {

        Article article1_vendu = (Article)data.get("article1vendu");
        Article article2_vendu = (Article)data.get("article2vendu");

        ArticlesModel article1 = (ArticlesModel)data.get("article1");
        ArticlesModel article2 = (ArticlesModel) data.get("article2");
        ClientModel client = (ClientModel)data.get("client");
        String versement = (String) data.get("versement");
        long datecredit = (long) data.get("datecredit");


        int nbr_articles1_restant = article1.getQuantite() - article1_vendu.getNbrarticle();
        int nbr_articles2_restant = article2.getQuantite() - article2_vendu.getNbrarticle();

        Map<String, Object> newdata = new HashMap<>();
        newdata.put("article1",article1);
        newdata.put("article2",article2);
        newdata.put("nbrarticle1restant",nbr_articles1_restant);
        newdata.put("nbrarticle2restant",nbr_articles2_restant);

        assert client != null;
        int numerocredit = client.getNbrcredit()+1;
        CreditModel credit = new CreditModel(client.getCodeclient(), client.getNom(), client.getPrenoms(), article1_vendu, article2_vendu, Integer.parseInt(versement), datecredit,numerocredit);
        boolean success = false;
          CreditModel le_credit_ajoute = accessLocalcredit.ajouterCredit(credit,client,newdata);
        if (le_credit_ajoute != null){
//            accessLocalInfo.updateTableInfosWhenCreateOrAddCredit(sommecredit);
            credits.add(le_credit_ajoute);
            this.setCredits(credits);
            this.setCredit(le_credit_ajoute);
            success = true;
        }
        return  success;

    }


    public boolean modifierCredit(CreditModel nouveau_creditModel, ClientModel client, int ancienne_somme_credit){

        boolean success = false;
        ArrayList<VersementsModel> liste_versements = accessLocalVersement.listeVersementsCredit(nouveau_creditModel);
        int last_index  = liste_versements.size()-1;
        VersementsModel dernier_versemt = liste_versements.get(last_index);
        Long date_de_solde = dernier_versemt.getDateversement();
        nouveau_creditModel.setSoldedat(date_de_solde);
        CreditModel credit = accessLocalcredit.modifierCredit(nouveau_creditModel,client,ancienne_somme_credit);
        if (credit != null ){
            int somme_a_ajoute = nouveau_creditModel.getSommecredit()-ancienne_somme_credit;
           accessLocalInfo.modifierCreditInfos(somme_a_ajoute);
            this.setCredit(credit);
            this.listecredits();
            success = true;
        }

        return success;
    }

    public boolean annullerCredit(CreditModel credit){
        boolean success = false;
      boolean rslt = accessLocalcredit.anullerCredit(credit);
        if (rslt){
           success = true;
           this.listecredits();
        }
      return success;
    }
    public boolean isClientOwnCredit(ClientModel client){
        return accessLocalcredit.isClientOwnCredit(client);

    }

    /**
     *
     * @param id idendifient unique du credit
     * @return le credit associé
     */
    public CreditModel recupUnCreditById(Integer id){
        return accessLocalcredit.recupCreditById(id);

    }


    /**
     *
     * @return la liste des credits en cours
     */
    public ArrayList<CreditModel> listecredits(){
            ArrayList<CreditModel> lsteCredits = accessLocalcredit.listeCredits();
            this.setCredits(lsteCredits);
            return lsteCredits;
    }

    /**
     *
     * @param client le client
     * @return la liste des credits en cours d'un client
     */
    public ArrayList<CreditModel> listecreditsclient(ClientModel client){

        ArrayList<CreditModel> lsteCredits = accessLocalcredit.listeCreditsclient(client);
        this.setCredits(lsteCredits);
        return lsteCredits;
    }

    /**
     *
     * @param client le client
     * @return la liste des credits soldés d'un client
     */
    public ArrayList<CreditModel> listecreditsSoldesclient(ClientModel client){

        ArrayList<CreditModel> lsteCredits = accessLocalcredit.listeDEScreditsSoldesClient(client);
        this.setCredits(lsteCredits);
        return lsteCredits;
    }


    public  void setRecapTcreditClient(ClientModel client){
        int totalcredit   = accessLocalcredit.getRecapTcreditClient(client);
        mtotalcreditClient.setValue(totalcredit);

    }

    public  void setRecapTversementClient(ClientModel client ){
       int totalversementclient = accessLocalcredit.getRecapTversementClient(client);
        mtotalversementclient.setValue(totalversementclient);
    }

    public  void setRecapTresteClient(ClientModel client){
        int totalresteclient = accessLocalcredit.getRecapTresteClient(client);
        mtotalresteclient.setValue(totalresteclient);
        mtotalresteclient.postValue(totalresteclient);
    }

    public MutableLiveData<Integer> getRecapTcreditClient(){
        return mtotalcreditClient;
    }


    public MutableLiveData<Integer> getRecapTresteClient(){
        return mtotalresteclient;
    }

    public  void setRecapTcredit(){
        int totalcredit   = accessLocalcredit.getRecapTcredit();
        mtotalcredit.setValue(totalcredit);
    }

    public  void setRecapTversement( ){
        int totalversement = accessLocalcredit.getRecapTversement();
        mtotalversement.setValue(totalversement);
    }

    public  void setRecapTreste(){
        int totalreste = accessLocalcredit.getRecapTreste();
        mtotalreste.setValue(totalreste);
    }

    public MutableLiveData<Integer> getRecapTcredit(){
        setRecapTcredit();
        return mtotalcredit;
    }

    public MutableLiveData<Integer> getRecapTversement(){
        setRecapTversement();
        return mtotalversement;
    }

    public MutableLiveData<Integer> getRecapTreste(){
        setRecapTreste();
        return mtotalreste;
    }


    public void supprimeCreditSoldes(CreditModel credit) {
        boolean success = accessLocalcredit.supprimerUncredit(credit);
        if (success){
            this.listecreditsSoldesclient(credit.getClient());
        }
    }

    public CreditModel modifierArticledunCredit(CreditModel anciencredit, String nouveauprixarticle, String nouvelleqtearticle, String nomarticleAModifier, ArticlesModel articleselectione) {
        ClientModel client = anciencredit.getClient();
        CreditModel nouveau_credit = null;
        Article ancien_article = null;
        Article nouvel_article = null;
        if (nomarticleAModifier.equals("article1") ){
             ancien_article = anciencredit.getArticle1();
             nouvel_article = new Article(articleselectione.getDesignation(),Integer.parseInt(nouveauprixarticle),Integer.parseInt(nouvelleqtearticle));
            nouveau_credit = new CreditModel(anciencredit.getId(),client,nouvel_article,anciencredit.getArticle2(),anciencredit.getVersement(),anciencredit.getDatecredit(),anciencredit.getNumerocredit());
        }
        if (nomarticleAModifier.equals("article2") ){
            ancien_article = anciencredit.getArticle2() ;
            nouvel_article = new Article(articleselectione.getDesignation(),Integer.parseInt(nouveauprixarticle),Integer.parseInt(nouvelleqtearticle));
            nouveau_credit = new CreditModel(anciencredit.getId(),client,anciencredit.getArticle1(),nouvel_article,anciencredit.getVersement(),anciencredit.getDatecredit(),anciencredit.getNumerocredit());
        }
      return  accessLocalcredit.modifierArticledunCredit(nouveau_credit,anciencredit,client,nouvel_article,ancien_article);
    }
    public CreditModel modifierDateCredit(CreditModel credit, String date) {
        return accessLocalcredit.modifierDateCredit(credit, MesOutils.convertStringToDate(date).getTime());
    }
}

