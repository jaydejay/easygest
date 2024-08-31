package com.jay.easygest.outils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.jay.easygest.model.AccountModel;
import com.jay.easygest.model.ClientModel;
import com.jay.easygest.model.VersementsaccModel;

import java.util.ArrayList;

public class AccessLocalVersementacc {

    public static final String SOMMEVERSE = "sommeverse";
    public static final String SOMMEACCOUNT = "sommeaccount";
    public static final String ACCOUNTID = "accountid";
    public static final String CLIENTID = "clientid";
    public static final String DATEVERSEMENT = "dateversement";
    public static final String VERSEMENTS = "versements";
    public static final String RESTE = "reste";
    public static final String TABLE_VERSEMENTACC = "versementacc";
    public static final String TABLE_ACCOUNT = "account";
    public static final String ARTICLE_1 = "article1";
    public static final String ARTICLE_2 = "article2";
    public static final String NUMEROACCOUNT = "numeroaccount";
    public static final String DATEACCOUNT = "dateaccount";
    public static final String ID = "id";
    public static final String SOLDEDAT = "soldedat";
    private final AccessLocalAccount accessLocalAccount;
    private final AccessLocalClient accessLocalClient;
    private final MySqliteOpenHelper accessBD;
    private SQLiteDatabase bd;
    private  Context contexte;

    public AccessLocalVersementacc(Context contexte) {
        this.contexte = contexte;
        this.accessBD = new MySqliteOpenHelper(contexte,null);
        accessLocalClient = new AccessLocalClient(contexte);
        accessLocalAccount = new AccessLocalAccount(contexte);


    }

    public ContentValues creerVersement(int sommeverse, int account_id, int client_id, Long dateversement) {
        ContentValues cv = new ContentValues();
        cv.put(SOMMEVERSE,sommeverse);
        cv.put(ACCOUNTID, account_id);
        cv.put(CLIENTID, client_id);
        cv.put(DATEVERSEMENT, dateversement);
        return cv;
    }

    public boolean ajouterversement(ClientModel client, long sommeverse, String dateversement)  {

        bd = accessBD.getWritableDatabase();
        boolean succes = false;
        long date = MesOutils.convertStringToDate(dateversement).getTime();

        ArrayList<AccountModel> accountsunclient =  accessLocalAccount.listeAccountsClient(client);

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

                        long date_de_solde;
                        if (reste == 0){
                            date_de_solde = date;
                        }else {date_de_solde = 0L;}
                        account.setSoldedat(date_de_solde);

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
                        account_cv.put(SOLDEDAT,date_de_solde);

                        bd.insertOrThrow(TABLE_VERSEMENTACC,null,creerVersement( somme_a_verse,account.getId(),client.getId(),date));
                        bd.replaceOrThrow(TABLE_ACCOUNT,null,account_cv);
                        sommeverse = sommeverse - somme_a_verse;
                        bd.setTransactionSuccessful();
                        succes =true;
                    }
                }catch (Exception e){succes=false;}
                finally {
                    bd.endTransaction();
                }

            }

        }
        return succes;
    }


    public boolean modifierVersement(AccountModel account, VersementsaccModel versement_a_modifier, int nouveau_total_versement, int nouvellesommeverse, long dateversement) {
        boolean success;
        bd = accessBD.getWritableDatabase();
        ContentValues cv_versementacc = new ContentValues();
        ContentValues account_cv = new ContentValues();

        bd.beginTransaction();
        try{
            cv_versementacc.put(ID,versement_a_modifier.getId());
            cv_versementacc.put(SOMMEVERSE,nouvellesommeverse);
            cv_versementacc.put(ACCOUNTID,account.getId());
            cv_versementacc.put(CLIENTID,versement_a_modifier.getClient().getId());
            cv_versementacc.put(DATEVERSEMENT,dateversement);

            int reste = account.getSommeaccount() - nouveau_total_versement;
            long date_de_solde;
            if (reste == 0){
               date_de_solde = dateversement;
            }else {date_de_solde = 0L;}
            account.setSoldedat(date_de_solde);

            account_cv.put(ID,account.getId());
            account_cv.put(CLIENTID,account.getClientid());
            account_cv.put(ARTICLE_1,account.getArticle1());
            account_cv.put(ARTICLE_2,account.getArticle2());
            account_cv.put(SOMMEACCOUNT,account.getSommeaccount());
            account_cv.put(VERSEMENTS,nouveau_total_versement);
            account_cv.put(RESTE,reste);
            account_cv.put(DATEACCOUNT,account.getDateaccount());
            account_cv.put(NUMEROACCOUNT,account.getNumeroaccount());
            account_cv.put(SOLDEDAT,date_de_solde);

            bd.replaceOrThrow(TABLE_VERSEMENTACC, null, cv_versementacc);
            bd.replaceOrThrow(TABLE_ACCOUNT,null,account_cv);
            bd.setTransactionSuccessful();
            success= true;
        }catch (Exception e){success=false;}
        finally {
            bd.endTransaction();
        }
        return success;

    }



    public boolean annullerversement(VersementsaccModel versementacc,AccountModel account){

        bd = accessBD.getWritableDatabase();
        long ancienne_sommeversee = versementacc.getSommeverse();
        boolean success;

        long nouveau_versement_du_account = account.getVersement() - ancienne_sommeversee;
        long reste = account.getSommeaccount() - nouveau_versement_du_account;

        account.setSoldedat(0L);

        ContentValues account_cv = new ContentValues();
        account_cv.put(ID,account.getId());
        account_cv.put(CLIENTID,account.getClientid());
        account_cv.put(ARTICLE_1,account.getArticle1());
        account_cv.put(ARTICLE_2,account.getArticle2());
        account_cv.put(SOMMEACCOUNT,account.getSommeaccount());
        account_cv.put(VERSEMENTS,nouveau_versement_du_account);
        account_cv.put(RESTE,reste);
        account_cv.put(DATEACCOUNT,account.getDateaccount());
        account_cv.put(NUMEROACCOUNT,account.getNumeroaccount());
        account_cv.put(SOLDEDAT,0L);
        bd.beginTransaction();
        try {
            bd.delete(TABLE_VERSEMENTACC,ID+"=?",new String[]{String.valueOf(versementacc.getId())});
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


    public ArrayList<VersementsaccModel> listeVersementsClient(ClientModel client){
        ArrayList<VersementsaccModel> versements = new ArrayList<>();
        try {
            bd = accessBD.getReadableDatabase();
            String req = "select * from versementacc where clientid ='" + client.getId()+"'";
            Cursor cursor = bd.rawQuery(req, null);
            cursor.moveToFirst();
            do {
               AccountModel account = accessLocalAccount.recupAccountById(cursor.getInt(2));
                VersementsaccModel versement = new VersementsaccModel(cursor.getInt(0),client,account, cursor.getLong(1), cursor.getLong(4) );
                versements.add(versement);

            }
            while (cursor.moveToNext());
            cursor.close();


        }catch(Exception e){
//            do nothing
        }
        return  versements;

    }

    public VersementsaccModel recupVersementaccById(Integer versementaccid){
        VersementsaccModel versement = null;
        try {
            bd = accessBD.getReadableDatabase();
            String req = "select * from versementacc where " + ID + "="+versementaccid+"";
            Cursor cursor = bd.rawQuery(req, null);
            cursor.moveToLast();
            if (!cursor.isAfterLast()) {
                int id = cursor.getInt(0);
                int sommeverse = cursor.getInt(1);
                int accountid = cursor.getInt(2);
                int clientid = cursor.getInt(3);
                long dateversement = cursor.getLong(4);

                ClientModel client = accessLocalClient.recupUnClient(clientid);
                AccountModel account = accessLocalAccount.recupAccountById(accountid);
                versement = new VersementsaccModel(id,client,account, (long) sommeverse,dateversement);

            }
            cursor.close();

        }catch (Exception e){
            return versement;
        }
        return versement;

    }

    /**
     *
     * @param account l'account
     * @return la lite des versements de l'account
     */

    public ArrayList<VersementsaccModel> listeVersementsAccount(AccountModel account) {

        ArrayList<VersementsaccModel> versements = new ArrayList<>();
        try {
            bd = accessBD.getReadableDatabase();
            String req = "select * from versementacc where " + ACCOUNTID + "='" +account.getId()+"'";
            Cursor cursor = bd.rawQuery(req, null);
            cursor.moveToFirst();
            do {
                ClientModel client = accessLocalClient.recupUnClient(cursor.getInt(3));
                VersementsaccModel versement = new VersementsaccModel(cursor.getInt(0),client,account, cursor.getLong(1), cursor.getLong(4) );
                versements.add(versement);

            }
            while (cursor.moveToNext());
            cursor.close();


        }catch(Exception e){
            versements = null;
        }
        return  versements;
    }


}
