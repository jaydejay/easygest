package com.jay.easygest.outils;

import static com.jay.easygest.outils.AccessLocalCredit.ID;
import static com.jay.easygest.outils.AccessLocalCredit.CLIENTID;
import static com.jay.easygest.outils.AccessLocalCredit.ARTICLE_1;
import static com.jay.easygest.outils.AccessLocalCredit.ARTICLE_2;
import static com.jay.easygest.outils.AccessLocalCredit.DATECREDIT;
import static com.jay.easygest.outils.AccessLocalCredit.NUMEROCREDIT;
import static com.jay.easygest.outils.AccessLocalCredit.SOMMECREDIT;
import static com.jay.easygest.outils.AccessLocalCredit.TABLE_CREDIT;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.jay.easygest.controleur.Clientcontrolleur;
import com.jay.easygest.controleur.Creditcontrolleur;
import com.jay.easygest.model.ClientModel;
import com.jay.easygest.model.CreditModel;
import com.jay.easygest.model.VersementsModel;

import java.util.ArrayList;

public class AccessLocalVersement {

    public static final String TABLE_VERSEMENT = "versement";
    public static final String VERSEMENTS = "versements";
    public static final String CODECLIENT = "codeclient";
    public static final String SOMMEVERSE = "sommeverse";
    public static final String CREDIT = "credit";
    public static final String RESTE = "reste";

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
        cv.put("creditid", creditid);
        cv.put("clientid", clientid);
        cv.put("dateversement", dateversement);
        return cv;
    }

    public ContentValues ajoutversementnouveaucredit(String versement){

        ContentValues cv = new ContentValues();
        cv.put(SOMMEVERSE, versement);
        return  cv;
    }

//    /**
//     * ajoute le nnieme versement
//     * @param codeclt code client
//     * @param versement versement
//     * @param sommeverse somme versée
//     * @return boolean
//     */
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

                        ContentValues credit_cv = new ContentValues();
                        credit_cv.put(ID,credit.getId());
                        credit_cv.put(CLIENTID,client.getId());
                        credit_cv.put(ARTICLE_1,credit.getArticle1());
                        credit_cv.put(ARTICLE_2,credit.getArticle2());
                        credit_cv.put(SOMMECREDIT,credit.getSommecredit());
                        credit_cv.put(VERSEMENTS,versements);
                        credit_cv.put(RESTE,reste);
                        credit_cv.put(DATECREDIT,date);
                        credit_cv.put(NUMEROCREDIT,credit.getNumerocredit());
                        bd.insertOrThrow(TABLE_VERSEMENT,null,creerVersement( somme_a_verse,credit.getId(),client.getId(),date));
                        bd.replaceOrThrow("credit",null,credit_cv);
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
        boolean success = false ;
        bd = accessBD.getWritableDatabase();
        ContentValues cv_versement = new ContentValues();
        ContentValues credit_cv = new ContentValues();

        bd.beginTransaction();
        try{
            cv_versement.put("id",versement_a_modifier.getId());
            cv_versement.put("sommeverse",nouvellesommeverse);
            cv_versement.put("creditid",credit.getId());
            cv_versement.put("clientid",versement_a_modifier.getClient().getId());
            cv_versement.put("dateversement",dateversement);

            int reste = credit.getSommecredit() - nouveau_total_versement;
            credit_cv.put("id",credit.getId());
            credit_cv.put("clientid",credit.getClientid());
            credit_cv.put("article1",credit.getArticle1());
            credit_cv.put("article2",credit.getArticle2());
            credit_cv.put("sommecredit",credit.getSommecredit());
            credit_cv.put("versements",nouveau_total_versement);
            credit_cv.put("reste",reste);
            credit_cv.put("datecredit",credit.getDatecredit());
            credit_cv.put("numerocredit",credit.getNumerocredit());

            bd.replaceOrThrow(TABLE_VERSEMENT, null, cv_versement);
            bd.replaceOrThrow(CREDIT,null,credit_cv);
            bd.setTransactionSuccessful();
            success= true;
        }finally {
            bd.endTransaction();
        }
        return success;

    }


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

    public ArrayList<VersementsModel> listeVersementsClient(String codeclt){
        ArrayList<VersementsModel> versements = new ArrayList<>();
        try {
            bd = accessBD.getReadableDatabase();
            String req = "select * from versement where " + CODECLIENT + "='" +codeclt+"'";
            Cursor cursor = bd.rawQuery(req, null);
            cursor.moveToFirst();
            do {
                VersementsModel versement = new VersementsModel(cursor.getString(1),cursor.getLong(2),cursor.getInt(3),cursor.getLong(4));
                versements.add(versement);

            }
            while (cursor.moveToNext());
            cursor.close();


        }catch(Exception e){
//            do nothing
        }
        return  versements;

    }

    public void supprimerversement(String codeclient) {
        bd = accessBD.getWritableDatabase();
        bd.delete(TABLE_VERSEMENT,CODECLIENT +"=?",new String[]{codeclient});
    }

    public boolean annullerversement(VersementsModel versement,CreditModel credit){

        bd = accessBD.getWritableDatabase();
        long ancienne_sommeversee = versement.getSommeverse();
        boolean success;

        long nouveau_versement_du_credit = credit.getVersement() - ancienne_sommeversee;
        long reste = credit.getSommecredit() - nouveau_versement_du_credit;

        ContentValues credit_cv = new ContentValues();
//        ContentValues contentValues = new ContentValues();
        credit_cv.put(ID,credit.getId());
        credit_cv.put(CLIENTID,credit.getClientid());
        credit_cv.put(ARTICLE_1,credit.getArticle1());
        credit_cv.put(ARTICLE_2,credit.getArticle2());
        credit_cv.put(SOMMECREDIT,credit.getSommecredit());
        credit_cv.put(VERSEMENTS,nouveau_versement_du_credit);
        credit_cv.put(RESTE,reste);
        credit_cv.put(DATECREDIT,credit.getDatecredit());
        credit_cv.put(NUMEROCREDIT,credit.getNumerocredit());

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
}
