package com.jay.easygest.outils;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.jay.easygest.controleur.Clientcontrolleur;
import com.jay.easygest.controleur.Creditcontrolleur;
import com.jay.easygest.model.AccountModel;
import com.jay.easygest.model.ClientModel;
import com.jay.easygest.model.CreditModel;
import com.jay.easygest.model.VersementsModel;

import java.util.ArrayList;
import java.util.Date;

public class AccessLocalVersement {

    public static final String TABLE_VERSEMENT = "versement";
    public static final String VERSEMENTS = "versements";
    public static final String CODECLIENT = "codeclient";
    public static final String SOMMEVERSE = "sommeverse";
    public static final String RESTE = "reste";
    public static final String CREDIT = "credit";
    public static final String CREDITID = "creditid";
    public static final String CLIENTID = "clientid";
    public static final String DATEVERSEMENT = "dateversement";
    public static final String ID = "id";
    public static final String ARTICLE_1 = "article1";
    public static final String ARTICLE_2 = "article2";
    public static final String SOMMECREDIT = "sommecredit";
    public static final String DATECREDIT = "datecredit";
    public static final String NUMEROCREDIT = "numerocredit";
    public static final String TABLE_CREDIT = "credit";
    public static final String SOLDEDAT = "soldedat";

    private final MySqliteOpenHelper accessBD;
    private SQLiteDatabase bd;
    private AccessLocalCredit accessLocalCredit;
    private AccessLocalClient accessLocalClient;
    private Creditcontrolleur creditcontrolleur;
    private Context contexte;


    public AccessLocalVersement(Context contexte) {
        this.contexte = contexte;
        accessLocalClient = new AccessLocalClient(contexte);
        accessLocalCredit = new AccessLocalCredit(contexte);
        this.accessBD = new MySqliteOpenHelper(contexte, null);

    }





    public ContentValues  creerVersement(Integer sommeverse, Integer creditid,Integer clientid, Long dateversement) {

        ContentValues cv = new ContentValues();
        cv.put(SOMMEVERSE,sommeverse);
        cv.put(CREDITID, creditid);
        cv.put(CLIENTID, clientid);
        cv.put(DATEVERSEMENT, dateversement);
        return cv;
    }



    public boolean ajouterversement(ClientModel client, long sommeverse,String dateversement)  {

           bd = accessBD.getWritableDatabase();
           boolean succes = false;
           long date = MesOutils.convertStringToDate(dateversement).getTime();
        creditcontrolleur = Creditcontrolleur.getCreditcontrolleurInstance(contexte);
        ArrayList<CreditModel> creditsunclient =  creditcontrolleur.listecreditsclient(client);

        if (creditsunclient.size() > 0){
            for (CreditModel credit : creditsunclient) {

                bd.beginTransaction();
                try{
                    if (sommeverse > 0){
                        int somme_a_verse;
                        if (sommeverse >= credit.getReste()){
                            somme_a_verse = credit.getReste();
                        }else {
                            somme_a_verse = (int)sommeverse;
                        }
                        int reste = credit.getReste() - somme_a_verse;
                        int versements = credit.getVersement() + somme_a_verse;
                        long date_de_solde;
                        if (reste == 0){
                            date_de_solde = date;
                        }else {date_de_solde = 0L;}
                        credit.setSoldedat(date_de_solde);
                        ContentValues credit_cv = new ContentValues();
                        credit_cv.put(ID,credit.getId());
                        credit_cv.put(CLIENTID,client.getId());
                        credit_cv.put(ARTICLE_1,credit.getArticle1());
                        credit_cv.put(ARTICLE_2,credit.getArticle2());
                        credit_cv.put(SOMMECREDIT,credit.getSommecredit());
                        credit_cv.put(VERSEMENTS,versements);
                        credit_cv.put(RESTE,reste);
                        credit_cv.put(DATECREDIT,credit.getDatecredit());
                        credit_cv.put(NUMEROCREDIT,credit.getNumerocredit());
                        credit_cv.put(SOLDEDAT,date_de_solde);

                        bd.insertOrThrow(TABLE_VERSEMENT,null,creerVersement( somme_a_verse,credit.getId(),client.getId(),date));
                        bd.replaceOrThrow(CREDIT,null,credit_cv);
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
    public boolean modifierVersement(CreditModel credit,VersementsModel versement_a_modifier,int nouveau_total_versement,int nouvellesommeverse, long dateversement) {
        boolean success ;
        bd = accessBD.getWritableDatabase();
        ContentValues cv_versement = new ContentValues();
        ContentValues credit_cv = new ContentValues();

        bd.beginTransaction();
        try{
            cv_versement.put(ID,versement_a_modifier.getId());
            cv_versement.put(SOMMEVERSE,nouvellesommeverse);
            cv_versement.put(CREDITID,credit.getId());
            cv_versement.put(CLIENTID,versement_a_modifier.getClient().getId());
            cv_versement.put(DATEVERSEMENT,dateversement);

            int reste = credit.getSommecredit() - nouveau_total_versement;
            long date_de_solde;
            if (reste == 0){
                date_de_solde = dateversement;
            }else {date_de_solde = 0L;}
            credit.setSoldedat(date_de_solde);

            credit_cv.put(ID,credit.getId());
            credit_cv.put(CLIENTID,credit.getClientid());
            credit_cv.put(ARTICLE_1,credit.getArticle1());
            credit_cv.put(ARTICLE_2,credit.getArticle2());
            credit_cv.put(SOMMECREDIT,credit.getSommecredit());
            credit_cv.put(VERSEMENTS,nouveau_total_versement);
            credit_cv.put(RESTE,reste);
            credit_cv.put(DATECREDIT,credit.getDatecredit());
            credit_cv.put(NUMEROCREDIT,credit.getNumerocredit());
            credit_cv.put(SOLDEDAT,date_de_solde);

            bd.replaceOrThrow(TABLE_VERSEMENT, null, cv_versement);
            bd.replaceOrThrow(CREDIT,null,credit_cv);
            bd.setTransactionSuccessful();
            success= true;
        }catch (Exception e){success = false;}
        finally {
            bd.endTransaction();
        }
        return success;

    }

    public boolean annullerversement(VersementsModel versement,CreditModel credit){

        bd = accessBD.getWritableDatabase();
        long ancienne_sommeversee = versement.getSommeverse();
        boolean success;

        long nouveau_versement_du_credit = credit.getVersement() - ancienne_sommeversee;
        long reste = credit.getSommecredit() - nouveau_versement_du_credit;

        credit.setSoldedat(0L);
        ContentValues credit_cv = new ContentValues();
        credit_cv.put(ID,credit.getId());
        credit_cv.put(CLIENTID,credit.getClientid());
        credit_cv.put(ARTICLE_1,credit.getArticle1());
        credit_cv.put(ARTICLE_2,credit.getArticle2());
        credit_cv.put(SOMMECREDIT,credit.getSommecredit());
        credit_cv.put(VERSEMENTS,nouveau_versement_du_credit);
        credit_cv.put(RESTE,reste);
        credit_cv.put(DATECREDIT,credit.getDatecredit());
        credit_cv.put(NUMEROCREDIT,credit.getNumerocredit());
        credit_cv.put(SOLDEDAT,0L);

        bd.beginTransaction();
        try {
            bd.delete(TABLE_VERSEMENT,ID+"=?",new String[]{String.valueOf(versement.getId())});
            bd.replaceOrThrow(TABLE_CREDIT,null,credit_cv);
            bd.setTransactionSuccessful();
            success = true;
        }catch (Exception e){
            success = false;
        }finally {
            bd.endTransaction();
        }
        return success;
    }


    /**
     *
     * @return retourne la liste de tous les versements
     */
    public ArrayList<VersementsModel> listeVersement(){
        ArrayList<VersementsModel> versements = new ArrayList<>();
        try {
            bd = accessBD.getReadableDatabase();
            String req = "select * from versement";
            Cursor cursor = bd.rawQuery(req, null);
            cursor.moveToFirst();
            do {
                ClientModel client = accessLocalClient.recupUnClient(cursor.getInt(3));
                CreditModel credit = accessLocalCredit.recupCreditById(cursor.getInt(2));
                VersementsModel versement = new VersementsModel(cursor.getInt(0),client,credit,cursor.getLong(1),cursor.getInt(2),cursor.getLong(4));
                versements.add(versement);
            }
            while (cursor.moveToNext());
            cursor.close();

        }catch(Exception e){
//            do nothing
        }
        return  versements;

    }

    public ArrayList<VersementsModel> listeVersementsClient(ClientModel client){
        ArrayList<VersementsModel> versements = new ArrayList<>();
        try {
            bd = accessBD.getReadableDatabase();
            String req = "select * from versement where " + CLIENTID + "='" +client.getId()+"'";
            Cursor cursor = bd.rawQuery(req, null);
            cursor.moveToFirst();
            do {
                CreditModel credit = accessLocalCredit.recupCreditById(cursor.getInt(2));
                VersementsModel versement = new VersementsModel(cursor.getInt(0),client,credit,cursor.getLong(1),cursor.getInt(2),cursor.getLong(4));
                versements.add(versement);

            }
            while (cursor.moveToNext());
            cursor.close();


        }catch(Exception e){
            versements = null;
        }
        return  versements;

    }

    public ArrayList<VersementsModel> listeVersementsCredit(CreditModel creditModel) {
        ArrayList<VersementsModel> versements = new ArrayList<>();
        try {
            bd = accessBD.getReadableDatabase();
            String req = "select * from versement where " + CREDITID + "='" +creditModel.getId()+"'";
            Cursor cursor = bd.rawQuery(req, null);
            cursor.moveToFirst();
            do {
                ClientModel client = accessLocalClient.recupUnClient(cursor.getInt(2));
                VersementsModel versement = new VersementsModel(cursor.getInt(0),client,creditModel,cursor.getLong(1),cursor.getInt(2),cursor.getLong(4));
                versements.add(versement);

            }
            while (cursor.moveToNext());
            cursor.close();


        }catch(Exception e){
            versements = null;
        }
        return  versements;
    }



    public VersementsModel recupVersementById(Integer versementid){
        VersementsModel versement = null;
        try {
            bd = accessBD.getReadableDatabase();
            String req = "select * from versement where " + ID + "="+versementid+"";
            Cursor cursor = bd.rawQuery(req, null);
            cursor.moveToLast();
            if (!cursor.isAfterLast()) {
                int id = cursor.getInt(0);
                int sommeverse = cursor.getInt(1);
                int creditid = cursor.getInt(2);
                int clientid = cursor.getInt(3);
                long dateversement = cursor.getLong(4);

                ClientModel client = accessLocalClient.recupUnClient(clientid);
                CreditModel credit = accessLocalCredit.recupCreditById(creditid);
                versement = new VersementsModel(id,client,credit, (long) sommeverse,credit.getId(),dateversement);

            }
            cursor.close();

        }catch (Exception e){
            return versement;
        }
        return versement;

    }

}
