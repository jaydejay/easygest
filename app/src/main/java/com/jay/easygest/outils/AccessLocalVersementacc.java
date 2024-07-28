package com.jay.easygest.outils;

import static com.jay.easygest.outils.AccessLocalCredit.ARTICLE_1;
import static com.jay.easygest.outils.AccessLocalCredit.ARTICLE_2;
import static com.jay.easygest.outils.AccessLocalCredit.CLIENTID;
import static com.jay.easygest.outils.AccessLocalCredit.DATECREDIT;
import static com.jay.easygest.outils.AccessLocalCredit.ID;
import static com.jay.easygest.outils.AccessLocalCredit.NUMEROCREDIT;
import static com.jay.easygest.outils.AccessLocalCredit.SOMMECREDIT;
import static com.jay.easygest.outils.AccessLocalCredit.TABLE_CREDIT;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.jay.easygest.controleur.Accountcontroller;
import com.jay.easygest.controleur.Creditcontrolleur;
import com.jay.easygest.model.AccountModel;
import com.jay.easygest.model.ClientModel;
import com.jay.easygest.model.CreditModel;
import com.jay.easygest.model.VersementsModel;
import com.jay.easygest.model.VersementsaccModel;

import java.util.ArrayList;

public class AccessLocalVersementacc {

    public static final String TABLE_VERSEMENTACC = "versementacc";
    public static final String TABLE_ACCOUNT = "account";
    public static final String VERSEMENTS = "versements";
    public static final String CODECLIENT = "codeclient";
    public static final String SOMMEVERSE = "sommeverse";

    public static final String RESTE = "reste";
    public static final String ACCOUNTID = "accountid";

    public static final String DATEVERSEMENT = "dateversement";
    public static final String SOMMEACCOUNT = "sommeaccount";
    public static final String DATEACCOUNT = "dateaccount";
    public static final String NUMEROACCOUNT = "numeroaccount";
    public static final String ACCOUNT = "account";
    public static final String SMMEVERSE = "sommeverse";
    public static final String ID = "id";


    private final MySqliteOpenHelper accessBD;
    private SQLiteDatabase bd;
    private AccessLocalAccount accessLocalAccount;
    private AccessLocalClient accessLocalClient;
    private Creditcontrolleur creditcontrolleur;
    private Context contexte;


    public AccessLocalVersementacc(Context contexte) {
        this.contexte = contexte;
        this.accessBD = new MySqliteOpenHelper(contexte, null);
        accessLocalClient = new AccessLocalClient(contexte);
        accessLocalAccount = new AccessLocalAccount(contexte);


    }





    public ContentValues  creerVersement(Integer sommeverse, Integer accountid,Integer clientid, Long dateversement) {

        ContentValues cv = new ContentValues();
        cv.put(SOMMEVERSE,sommeverse);
        cv.put(ACCOUNTID, accountid);
        cv.put(CLIENTID, clientid);
        cv.put(DATEVERSEMENT, dateversement);
        return cv;
    }



    public boolean ajouterversement(ClientModel client, long sommeverse,String dateversement)  {

           bd = accessBD.getWritableDatabase();
           boolean succes = false;
           long date = MesOutils.convertStringToDate(dateversement).getTime();
        creditcontrolleur = Creditcontrolleur.getCreditcontrolleurInstance(contexte);
        ArrayList<AccountModel> accountsunclient =  Accountcontroller.listeaccountsclient(client);

        if (accountsunclient.size() > 0){
            for (AccountModel account : accountsunclient) {

                bd.beginTransaction();
                try{
                    if (sommeverse > 0){
                        int somme_a_verse;
                        if (sommeverse >= account.getReste()){
                            somme_a_verse = account.getReste();
                        }else {
                            somme_a_verse = (int)sommeverse;
                        }

                        int reste = account.getReste() - somme_a_verse;
                        int versements = account.getVersement() + somme_a_verse;

                        ContentValues account_cv = new ContentValues();
                        account_cv.put(ID,account.getId());
                        account_cv.put(CLIENTID,client.getId());
                        account_cv.put(ARTICLE_1,account.getArticle1());
                        account_cv.put(ARTICLE_2,account.getArticle2());
                        account_cv.put(SOMMEACCOUNT,account.getSommeaccount());
                        account_cv.put(VERSEMENTS,versements);
                        account_cv.put(RESTE,reste);
                        account_cv.put(DATEACCOUNT,date);
                        account_cv.put(NUMEROACCOUNT,account.getNumeroaccount());
                        bd.insertOrThrow(TABLE_VERSEMENTACC,null,creerVersement( somme_a_verse,account.getId(),client.getId(),date));
                        bd.replaceOrThrow(ACCOUNT,null,account_cv);
                        sommeverse = sommeverse - somme_a_verse;
                        bd.setTransactionSuccessful();
                        succes =true;

                    }
                }finally {
                    bd.endTransaction();
                }

            }

        }
        return succes;
    }

    /**
     * modifier le nnieme versement d'un client
     * @param nouvellesommeverse somme versée
     * @return boolean
     */
    public boolean modifierVersement(AccountModel account,VersementsaccModel versement_a_modifier,int nouveau_total_versement,int nouvellesommeverse, long dateversement) {
        boolean success = false ;
        bd = accessBD.getWritableDatabase();
        ContentValues cv_versement = new ContentValues();
        ContentValues account_cv = new ContentValues();

        bd.beginTransaction();
        try{

            cv_versement.put(ID,versement_a_modifier.getId());
            cv_versement.put(SMMEVERSE,nouvellesommeverse);
            cv_versement.put(ACCOUNTID,account.getId());
            cv_versement.put(CLIENTID,versement_a_modifier.getClient().getId());
            cv_versement.put(DATEVERSEMENT,dateversement);

            int reste = account.getSommeaccount() - nouveau_total_versement;
            account_cv.put(ID,account.getId());
            account_cv.put(CLIENTID,account.getClientid());
            account_cv.put(ARTICLE_1,account.getArticle1());
            account_cv.put(ARTICLE_2,account.getArticle2());
            account_cv.put(SOMMEACCOUNT,account.getSommeaccount());
            account_cv.put(VERSEMENTS,nouveau_total_versement);
            account_cv.put(RESTE,reste);
            account_cv.put(DATEACCOUNT,account.getDateaccount());
            account_cv.put(NUMEROACCOUNT,account.getNumeroaccount());

            bd.replaceOrThrow(TABLE_VERSEMENTACC, null, cv_versement);
            bd.replaceOrThrow(ACCOUNT,null,account_cv);
            bd.setTransactionSuccessful();
            success= true;
        }finally {
            bd.endTransaction();
        }
        return success;

    }


    public ArrayList<VersementsaccModel> listeVersement(){
        ArrayList<VersementsaccModel> versements = new ArrayList<>();
        try {
            bd = accessBD.getReadableDatabase();
            String req = "select * from versementacc";
            Cursor cursor = bd.rawQuery(req, null);
            cursor.moveToFirst();
            do {
                ClientModel client = accessLocalClient.recupUnClient(cursor.getInt(3));
                AccountModel account = accessLocalAccount.recupAccountById(cursor.getInt(2));
                VersementsaccModel versement = new VersementsaccModel(cursor.getInt(0),client,account,cursor.getLong(1),cursor.getLong(4));
                versements.add(versement);
            }
            while (cursor.moveToNext());
            cursor.close();

        }catch(Exception e){
//            do nothing
        }
        return  versements;

    }

//    public ArrayList<VersementsaccModel> listeVersementsClient(ClientModel client){
//        ArrayList<VersementsaccModel> versements = new ArrayList<>();
//        try {
//            bd = accessBD.getReadableDatabase();
//            String req = "select * from versementacc where " + CLIENTID + "='" +client.getId()+"'";
//            Cursor cursor = bd.rawQuery(req, null);
//            cursor.moveToFirst();
//            do {
//                VersementsaccModel versement = new VersementsaccModel(cursor.getString(1),cursor.getLong(2),cursor.getInt(3),cursor.getLong(4));
//                versements.add(versement);
//
//            }
//            while (cursor.moveToNext());
//            cursor.close();
//
//
//        }catch(Exception e){
////            do nothing
//        }
//        return  versements;
//
//    }

    public void supprimerversement(int versementid) {
        bd = accessBD.getWritableDatabase();
        bd.delete(TABLE_VERSEMENTACC,ID +"=?",new String[]{String.valueOf(versementid) });
    }

    public boolean annullerversement(VersementsaccModel versement,AccountModel account){

        bd = accessBD.getWritableDatabase();
        long ancienne_sommeversee = versement.getSommeverse();
        boolean success;

        long nouveau_versement_du_account = account.getVersement() - ancienne_sommeversee;
        long reste = account.getSommeaccount() - nouveau_versement_du_account;

        ContentValues account_cv = new ContentValues();
//        ContentValues contentValues = new ContentValues();
        account_cv.put(ID,account.getId());
        account_cv.put(CLIENTID,account.getClientid());
        account_cv.put(ARTICLE_1,account.getArticle1());
        account_cv.put(ARTICLE_2,account.getArticle2());
        account_cv.put(SOMMEACCOUNT,account.getSommeaccount());
        account_cv.put(VERSEMENTS,nouveau_versement_du_account);
        account_cv.put(RESTE,reste);
        account_cv.put(DATEACCOUNT,account.getDateaccount());
        account_cv.put(NUMEROACCOUNT,account.getNumeroaccount());

        bd.beginTransaction();
        try {
            bd.delete(TABLE_VERSEMENTACC,ID+"=?",new String[]{String.valueOf(versement.getId())});
            bd.replaceOrThrow(TABLE_ACCOUNT,null,account_cv);
            bd.setTransactionSuccessful();
            success = true;
        }catch (Exception e){
            success = false;
        }finally {
            bd.endTransaction();
        }
        return success;
    }
}
