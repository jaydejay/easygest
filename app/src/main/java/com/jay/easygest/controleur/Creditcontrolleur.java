package com.jay.easygest.controleur;


import android.content.Context;

import androidx.lifecycle.MutableLiveData;

import com.jay.easygest.model.Articles;
import com.jay.easygest.model.ClientModel;
import com.jay.easygest.model.CreditModel;
import com.jay.easygest.model.VersementsModel;
import com.jay.easygest.outils.AccessLocalClient;
import com.jay.easygest.outils.AccessLocalCredit;
import com.jay.easygest.outils.AccessLocalInfo;
import com.jay.easygest.outils.AccessLocalVersement;
import com.jay.easygest.vue.ui.clients.ClientViewModel;
import com.owlike.genson.Genson;

import java.util.ArrayList;

public final class Creditcontrolleur {

    private static Creditcontrolleur creditcontrolleurInstance = null;
    private CreditModel credit;
    private ArrayList<CreditModel> credits = new ArrayList<>();
    private static AccessLocalCredit accessLocalcredit;
    private static AccessLocalInfo accessLocalInfo ;
    private static AccessLocalVersement accessLocalVersement;
    private static AccessLocalClient accessLocalClient;
    private final MutableLiveData<Integer> mtotalcredit = new MutableLiveData<>();
    private final MutableLiveData<Integer> mtotalversement = new MutableLiveData<>();
    private final MutableLiveData<Integer> mtotalreste = new MutableLiveData<>();
    private  final  MutableLiveData<CreditModel> mcredit = new MutableLiveData<>();
    private  final  MutableLiveData<ArrayList<CreditModel>> mcredits = new MutableLiveData<>();
    private final MutableLiveData<Integer> mtotalresteclient = new MutableLiveData<>();
    private final MutableLiveData<Integer> mtotalversementclient = new MutableLiveData<>();
    private final  MutableLiveData<Integer> mtotalcreditClient = new MutableLiveData<>();
    private Integer idmenu;

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
             accessLocalClient = new AccessLocalClient(contexte);
        }

        return creditcontrolleurInstance;
    }


    public Integer getIdmenu() {
        return idmenu;
    }

    public void setIdmenu(Integer idmenu) {
        this.idmenu = idmenu;
    }

    public CreditModel getCredit() {
        return credit;
    }

    public void setCredit(CreditModel credit ) {
        this.credit = credit;
        setMCredit(credit);
    }

    public ArrayList<CreditModel> getCredits() {
        return credits;
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

    public CreditModel creerCredit(String codeclt, String nomclient,String prenomsclient, String telephone , Articles c_article1, Articles c_article2, String versement, long datecredit){

        String article1 = new Genson().serialize(c_article1);
        String article2 = new Genson().serialize(c_article2);

        int sommecredit = c_article1.getSomme() + c_article2.getSomme();
        int reste = sommecredit - Integer.parseInt(versement);

        CreditModel premiercredit = new CreditModel( codeclt,nomclient,prenomsclient,article1, article2,sommecredit, Integer.parseInt(versement), reste,datecredit,1);
        CreditModel credit = accessLocalcredit.creerCompteCredit(premiercredit,codeclt,nomclient,prenomsclient,telephone,versement);

        if (credit != null){
             accessLocalInfo.updateCreditInfos(sommecredit);
            credits.add(premiercredit);
            this.setCredits(credits);
            this.setCredit(credit);
        }
        return  credit;
    }

    public boolean ajouterCredit( ClientModel client,Articles c_article1, Articles c_article2, String versement, long datecredit) {

        String article1 = new Genson().serialize(c_article1);
        String article2 = new Genson().serialize(c_article2);

        int sommecredit = c_article1.getSomme() + c_article2.getSomme();
        int reste = sommecredit - Integer.parseInt(versement);
        int numerocredit = client.getNbrcredit()+1;
        CreditModel credit = new CreditModel(client.getCodeclient(), client.getNom(), client.getPrenoms(), article1, article2,sommecredit, Integer.parseInt(versement), reste,datecredit,numerocredit);
        boolean success = false;
          CreditModel le_credit_ajoute = accessLocalcredit.ajouterCredit(credit,client);
        if (le_credit_ajoute != null){
            accessLocalInfo.updateCreditInfos(sommecredit);
            credits.add(le_credit_ajoute);
            this.setCredits(credits);
            this.setCredit(le_credit_ajoute);
            success = true;
        }
        return  success;

    }


    public boolean modifierCredit(CreditModel creditModel, ClientModel client, int ancienne_somme_credit){

        boolean success = false;
        Long date_de_solde;
        if (creditModel.getReste() == 0){
            ArrayList<VersementsModel> liste_versements = accessLocalVersement.listeVersementsCredit(creditModel);
            int last_index  = liste_versements.size()-1;
            VersementsModel dernier_versemt = liste_versements.get(last_index);
            date_de_solde = dernier_versemt.getDateversement();
        }else {date_de_solde = 0L;}
        creditModel.setSoldedat(date_de_solde);
        CreditModel credit = accessLocalcredit.modifierCredit(creditModel,client,ancienne_somme_credit);
        if (credit != null ){
            int somme_a_ajoute = creditModel.getSommecredit()-ancienne_somme_credit;
           accessLocalInfo.modifierCreditInfos(somme_a_ajoute);
            this.setCredit(credit);
            this.listecredits();
            success = true;
        }

        return success;
    }

    public boolean annullerCredit(CreditModel credit){
      boolean success = accessLocalcredit.anullerCredit(credit);
        if (success){
            accessLocalInfo.annullerCreditInfos(credit);
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
}
