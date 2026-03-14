package com.jay.easygest.outils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.jay.easygest.controleur.Accountcontroller;
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
    private final Context contexte;
    private final Gson gson = new Gson();

    public AccessLocalVersementacc(Context contexte) {
        this.contexte = contexte;
        this.accessBD =  MySqliteOpenHelper.getInstance(contexte,null);
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
        Accountcontroller accountcontroller = Accountcontroller.getAccountcontrolleurInstance(contexte);
        ArrayList<AccountModel> accountsunclient =  accessLocalAccount.listeAccountsClient(client);

        if (!accountsunclient.isEmpty()){
            for (AccountModel account : accountsunclient) {
                bd.beginTransaction();
                try{
                    if (sommeverse > 0){
                        int  somme_a_verse = sommeverse >= account.getReste() ? account.getReste() : (int)sommeverse  ;
                        int reste = account.getReste() - somme_a_verse;
                        int versements = account.getVersement() + somme_a_verse;
                        long date_de_solde = reste == 0 ? date : 0L;
                        account.setSoldedat(date_de_solde);

                        ContentValues account_cv = new ContentValues();
                        account_cv.put(SOMMEACCOUNT,account.getSommeaccount());
                        account_cv.put(VERSEMENTS,versements);
                        account_cv.put(RESTE,reste);
                        account_cv.put(DATEACCOUNT,date);
                        account_cv.put(SOLDEDAT,date_de_solde);

                        bd.insertWithOnConflict(TABLE_VERSEMENTACC,null,creerVersement( somme_a_verse,account.getId(),client.getId(),date),1);
                        bd.updateWithOnConflict(TABLE_ACCOUNT,account_cv, ID + "=?", new String[] {String.valueOf(account.getId())},1);
                        sommeverse = sommeverse - somme_a_verse;
                        bd.setTransactionSuccessful();
                        succes =true;
                    }
                }catch (Exception e){succes=false;}
                finally {
                    if (bd.inTransaction()){
                        bd.endTransaction();
                    }
                }
            }
        }
        accountcontroller.setRecapTresteClient(client);
        return succes;
    }


    public boolean modifierVersement(AccountModel account, VersementsaccModel versement_a_modifier, int nouveau_total_versement, int nouvellesommeverse, long dateversement) {
        boolean success;
        bd = accessBD.getWritableDatabase();
        ContentValues cv_versementacc = new ContentValues();
        ContentValues account_cv = new ContentValues();

        bd.beginTransaction();
        try{
            cv_versementacc.put(SOMMEVERSE,nouvellesommeverse);
            cv_versementacc.put(DATEVERSEMENT,dateversement);

            int reste = account.getSommeaccount() - nouveau_total_versement;
            long date_de_solde;
            date_de_solde = reste == 0 ? dateversement :0L;
            account.setSoldedat(date_de_solde);
            account_cv.put(VERSEMENTS,nouveau_total_versement);
            account_cv.put(RESTE,reste);
            account_cv.put(DATEACCOUNT,account.getDateaccount());
            account_cv.put(SOLDEDAT,date_de_solde);

            bd.updateWithOnConflict(TABLE_VERSEMENTACC, cv_versementacc,ID+"=?",new String[]{String.valueOf(versement_a_modifier.getId())},1);
            bd.updateWithOnConflict(TABLE_ACCOUNT,account_cv,ID + "=?",new String[]{String.valueOf(account.getId())},1);
            bd.setTransactionSuccessful();
            success= true;
        }catch (Exception e){success=false;}
        finally {
            if (bd.inTransaction()){
                bd.endTransaction();
            }
        }
        return success;

    }



    public boolean annullerversement(VersementsaccModel versementacc,AccountModel account){

        bd = accessBD.getWritableDatabase();
        bd.setForeignKeyConstraintsEnabled(true);
        long ancienne_sommeversee = versementacc.getSommeverse();
        boolean success = false;
        long nouveau_versement_du_account = account.getVersement() - ancienne_sommeversee;
        long reste = account.getSommeaccount() - nouveau_versement_du_account;
        account.setSoldedat(0L);
        ContentValues account_cv = getAccountContentValues(account, nouveau_versement_du_account, reste);
        bd.beginTransaction();
        try {
           int rslt = bd.delete(TABLE_VERSEMENTACC,ID+"=?",new String[]{String.valueOf(versementacc.getId())});
           if (rslt > 0){
               bd.updateWithOnConflict(TABLE_ACCOUNT,account_cv, ID + "=?",new String[]{String.valueOf(account.getId())},1);
               bd.setTransactionSuccessful();
               success = true;
           }else {
               bd.endTransaction();
           }
        }catch (Exception e){
            return success ;
        }finally {
            if (bd.inTransaction()){
                bd.endTransaction();
            }
        }
        return success;
    }

    @NonNull
    private ContentValues getAccountContentValues(AccountModel account, long nouveau_versement_du_account, long reste) {
        ContentValues account_cv = new ContentValues();
        account_cv.put(ID, account.getId());
        account_cv.put(CLIENTID, account.getClient().getId());
        account_cv.put(ARTICLE_1, gson.toJson(account.getArticle1()));
        account_cv.put(ARTICLE_2, gson.toJson(account.getArticle2()));
        account_cv.put(SOMMEACCOUNT, account.getSommeaccount());
        account_cv.put(VERSEMENTS, nouveau_versement_du_account);
        account_cv.put(RESTE, reste);
        account_cv.put(DATEACCOUNT, account.getDateaccount());
        account_cv.put(NUMEROACCOUNT, account.getNumeroaccount());
        account_cv.put(SOLDEDAT,0L);
        return account_cv;
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
            return versements;
        }
        return  versements;
    }

    public VersementsaccModel recupVersementaccById(Integer versementaccid){
        VersementsaccModel versement = null;
        try {
            bd = accessBD.getReadableDatabase();
            String req = "select * from versementacc where " + ID + "="+versementaccid;
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
