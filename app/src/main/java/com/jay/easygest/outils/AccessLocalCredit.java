package com.jay.easygest.outils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import com.jay.easygest.model.ClientModel;
import com.jay.easygest.model.CreditModel;

import java.util.ArrayList;


public class AccessLocalCredit {

    public static final String TABLE_VERSEMENT = "versement";
    public static final String CODECLIENT = "codeclient";
    public static final String CLIENTID = "clientid";
    public static final String VERSEMENTS = "versements";
//    public static final String TABLE_CLIENT = CLIENT;

    public static final String NOM = "nom";
    public static final String ARTICLE_1 = "article1";
    public static final String ARTICLE_2 = "article2";

    public static final String SOMMECREDIT = "sommecredit";
    public static final String RESTE = "reste";
    public static final String DATECREDIT = "datecredit";
    public static final String TABLE_CREDIT = "credit";
    public static final String ID = "id";
    public static final String NUMEROCREDIT = "numerocredit";
    public static final String NBRCREDIT = "nbrcredit";
    public static final String TOTALCREDIT = "totalcredit";
    private static final String CREDITID = "creditid";
    public static final String PRENOMS = "prenoms";
    public static final String TELEPHONE = "telephone";
    public static final String ADRESSEELECTRO = "adresseelectro";
    public static final String RESIDENCE = "residence";
    public static final String CNI = "cni";
    public static final String PERMIS = "permis";
    public static final String PASSPORT = "passport";
    public static final String SOCIETE = "societe";
    public static final String NBRACCOUNT = "nbraccount";
    public static final String TOTALACCOUNT = "totalaccount";
    public static final String TABLE_CLIENT = "client";
    public static final String SOLDEDAT = "soldedat";
    private final MySqliteOpenHelper accessBD;
    private SQLiteDatabase bd;
    private AccessLocalClient accessLocalClient;
    private AccessLocalVersement accessLocalVersement;
    private final Context contexte;

    public AccessLocalCredit(Context contexte) {
        this.contexte = contexte;
        this.accessBD = new MySqliteOpenHelper(contexte, null);
        accessLocalClient = new AccessLocalClient(contexte);
//        clientcontrolleur =  Clientcontrolleur.getClientcontrolleurInstance(contexte);


    }

    public ContentValues creerCredit(CreditModel credit, long client_id) {

        ContentValues cv = new ContentValues();
        long date_de_solde;
        if (credit.getReste() == 0){
            date_de_solde = credit.getDatecredit();
        }else {date_de_solde = 0L;}
        credit.setSoldedat(date_de_solde);

        cv.put(CLIENTID,client_id);
        cv.put(ARTICLE_1,credit.getArticle1());
        cv.put(ARTICLE_2,credit.getArticle2());
        cv.put(SOMMECREDIT,credit.getSommecredit());
        cv.put(VERSEMENTS,credit.getVersement());
        cv.put(RESTE,credit.getReste());
        cv.put(DATECREDIT,credit.getDatecredit());
        cv.put(NUMEROCREDIT,credit.getNumerocredit());
        cv.put(SOLDEDAT,date_de_solde);
       return cv;
    }

    public CreditModel creerCompteCredit(CreditModel premiercredit, String codeclt,String nom,String prenoms,String telephone, String sommeversee ){
        bd = accessBD.getWritableDatabase();
        accessLocalVersement = new AccessLocalVersement(contexte);
        accessLocalClient = new AccessLocalClient(contexte);

        bd.beginTransaction();
        CreditModel creditModel;
        try {

            long client_reslt = bd.insertOrThrow(TABLE_CLIENT,null,accessLocalClient.ajouterClient(codeclt, nom, prenoms,telephone,1,premiercredit.getSommecredit(),0,0));
            long credit_rslt = bd.insertOrThrow(TABLE_CREDIT,null,this.creerCredit(premiercredit,client_reslt));
            bd.insertOrThrow(TABLE_VERSEMENT,null,accessLocalVersement.creerVersement(Integer.parseInt(sommeversee), (int) credit_rslt,(int) client_reslt,premiercredit.getDatecredit()));
             creditModel = this.recupCreditById((int) credit_rslt);
            bd.setTransactionSuccessful();

        }catch (Exception e){
            creditModel = null;
        }finally {
            bd.endTransaction();

        }
        return creditModel;

    }

    /**
     * @param credit le credit a ajouter
     * @param client le client proprietaire du credit
     * @return boolean
     */
    public CreditModel ajouterCredit(CreditModel credit,ClientModel client){
        bd = accessBD.getWritableDatabase();
        accessLocalVersement = new AccessLocalVersement(contexte);
        ContentValues client_cv= new ContentValues();
        client_cv.put(ID,client.getId());
        client_cv.put(CODECLIENT,client.getCodeclient());
        client_cv.put(NOM,client.getNom());
        client_cv.put(PRENOMS,client.getPrenoms());
        client_cv.put(TELEPHONE,client.getTelephone());
        client_cv.put(ADRESSEELECTRO,client.getEmail());
        client_cv.put(RESIDENCE,client.getResidence());
        client_cv.put(CNI,client.getCni());
        client_cv.put(PERMIS,client.getPermis());
        client_cv.put(PASSPORT,client.getPassport());
        client_cv.put(SOCIETE,client.getSociete());
        client_cv.put(NBRCREDIT,client.getNbrcredit() + 1);
        client_cv.put(TOTALCREDIT,client.getTotalcredit() + credit.getSommecredit());
        client_cv.put(NBRACCOUNT,client.getNbraccount());
        client_cv.put(TOTALACCOUNT,client.getTotalaccount());
        bd.beginTransaction();
        CreditModel creditModel;

        try {
            long credit_rslt =  bd.insertOrThrow(TABLE_CREDIT,null,this.creerCredit(credit,client.getId()));
            bd.replaceOrThrow(TABLE_CLIENT,null,client_cv);
            bd.insertOrThrow(TABLE_VERSEMENT,null,accessLocalVersement.creerVersement(credit.getVersement(), (int) credit_rslt, client.getId(),credit.getDatecredit()));
             creditModel = this.recupCreditById((int) credit_rslt);
            bd.setTransactionSuccessful();

        }catch (Exception e){
             creditModel = null;
        }finally {
            bd.endTransaction();

        }

        return creditModel;

    }


    public CreditModel modifierCredit(CreditModel creditModel,ClientModel client,int ancienne_sommecredit) {

        bd = accessBD.getWritableDatabase();

        bd.beginTransaction();
        CreditModel credit;
//        try{
            int ancien_total_credit_du_client =  Integer.parseInt(String.valueOf(client.getTotalcredit())) ;
            int ancienne_somme_credit = Integer.parseInt(String.valueOf(ancienne_sommecredit)) ;
            int nouveau_total_credit_du_client = ( ancien_total_credit_du_client - ancienne_somme_credit) + creditModel.getSommecredit();

            ContentValues credit_cv = new ContentValues();
            ContentValues client_cv = new ContentValues();

            credit_cv.put(ARTICLE_1,creditModel.getArticle1());
            credit_cv.put(ARTICLE_2,creditModel.getArticle2());
            credit_cv.put(SOMMECREDIT,creditModel.getSommecredit());
            credit_cv.put(VERSEMENTS,creditModel.getVersement());
            credit_cv.put(RESTE,creditModel.getReste());
            credit_cv.put(DATECREDIT,creditModel.getDatecredit());
            credit_cv.put(SOLDEDAT,creditModel.getSoldedat());

            client_cv.put(TOTALCREDIT,nouveau_total_credit_du_client);

            bd.updateWithOnConflict(TABLE_CREDIT,credit_cv, ID + "=" +creditModel.getId(),null,1);
            bd.updateWithOnConflict(TABLE_CLIENT, client_cv, "id = ?", new String[] {String.valueOf(client.getId())},1);
             credit = this.recupCreditById(creditModel.getId());
            bd.setTransactionSuccessful();

//        }catch (Exception e){
//            credit = null;
//        }
//        finally {
            bd.endTransaction();
//        }
        return credit;
    }


    public boolean anullerCredit(CreditModel credit){
        boolean success ;
        bd.beginTransaction();
        try {
            bd = accessBD.getWritableDatabase();

            ContentValues cvclient = new ContentValues();
            cvclient.put(NBRCREDIT,credit.getClient().getNbrcredit() - 1);
            cvclient.put(TOTALCREDIT,credit.getClient().getTotalcredit() - credit.getSommecredit());

            bd.delete(TABLE_CREDIT,ID +"=?",new String[]{String.valueOf(credit.getId())});
            bd.delete(TABLE_VERSEMENT,CREDITID +"=?",new String[]{String.valueOf(credit.getId())});
            bd.updateWithOnConflict(TABLE_CLIENT,cvclient, ID + "=" +credit.getClient().getId(),null,1);
            bd.setTransactionSuccessful();
            success = true;
        }catch (Exception e){
            success = false;
        }finally {
            bd.endTransaction();
        }
        return  success;
    }

    /**
     *
     * @return la liste de tous les credits en cours
     */

    public ArrayList<CreditModel> listeCredits(){
        ArrayList<CreditModel> credits = new ArrayList<>();

        try {
            bd = accessBD.getReadableDatabase();

            String req = "select * from credit where reste != 0 ";
            Cursor cursor = bd.rawQuery(req, null);
            cursor.moveToFirst();
            do {
                ClientModel client = accessLocalClient.recupUnClient(cursor.getInt(1));
                CreditModel credit = new CreditModel(cursor.getInt(0),client,cursor.getString(2),cursor.getString(3),cursor.getInt(4),cursor.getInt(5),cursor.getInt(6),cursor.getLong(7),cursor.getInt(8));
                credit.setSoldedat(cursor.getLong(9));
                credits.add(credit);
            }
            while (cursor.moveToNext());
            cursor.close();

        }catch(Exception e){
            return credits;
        }
        return credits;

    }

    /**
     *
     * @param client le client
     * @return liste des credits en cours du client
     */

    public ArrayList<CreditModel> listeCreditsclient(ClientModel client){
        ArrayList<CreditModel> credits = new ArrayList<>();
        try {
            bd = accessBD.getReadableDatabase();
            String req = "select * from credit where  reste != 0 and clientid ='" + client.getId()+"'";
            Cursor cursor = bd.rawQuery(req, null);
            cursor.moveToFirst();
            do {
                CreditModel credit = new CreditModel(cursor.getInt(0),client,cursor.getString(2),cursor.getString(3),cursor.getInt(4),cursor.getInt(5),cursor.getInt(6),cursor.getLong(7),cursor.getInt(8));
                credit.setSoldedat(cursor.getLong(9));
                credits.add(credit);
            }
            while (cursor.moveToNext());

            cursor.close();

        }catch(Exception e){
            return credits;
        }
        return credits;

    }

    /**
     *
     * @param client le client
     * @return la liste des credits soldés d'un client
     */
    public ArrayList<CreditModel> listeDEScreditsSoldesClient(ClientModel client) {

        ArrayList<CreditModel> credits = new ArrayList<>();
        try {
            bd = accessBD.getReadableDatabase();
            String req = "select * from credit where  reste = 0 and clientid ='" + client.getId()+"'";
            Cursor cursor = bd.rawQuery(req, null);
            cursor.moveToFirst();
            do {
                CreditModel credit = new CreditModel(cursor.getInt(0),client,cursor.getString(2),cursor.getString(3),cursor.getInt(4),cursor.getInt(5),cursor.getInt(6),cursor.getLong(7),cursor.getInt(8));
                credit.setSoldedat(cursor.getLong(9));
                credits.add(credit);
            }
            while (cursor.moveToNext());

            cursor.close();
        }catch(Exception e){
            return credits;
        }
        return credits;
    }




    public boolean isClientOwnCredit(ClientModel client){
        ArrayList<CreditModel> credits = this.listeCreditsclient(client);
         if (credits.size() == 0){
             return false;
         }
            return true;
        }

    /**
     *
     * @param creditId l'identifiant unique du credit
     * @return retourne le credit
     */

    public CreditModel recupCreditById(Integer creditId){
        CreditModel credit = null;
        try {
            bd = accessBD.getReadableDatabase();
            String req = "select * from credit where " + ID + "="+creditId+"";
            Cursor cursor = bd.rawQuery(req, null);
            cursor.moveToLast();
            if (!cursor.isAfterLast()) {
                int clientid = cursor.getInt(1);
                String article1 = cursor.getString(2);
                String article2 = cursor.getString(3);
                int sommecredit = cursor.getInt(4);
                int versement = cursor.getInt(5);
                int reste = cursor.getInt(6);
                long datecredit = cursor.getLong(7);
                Integer nbrcredit = cursor.getInt(8);
                credit = new CreditModel(creditId, clientid, article1, article2, sommecredit, versement, reste, datecredit,nbrcredit);
                credit.setSoldedat(cursor.getLong(9));
            }
            cursor.close();

        }catch (Exception e){
            //do nothing
            return credit;
        }
        return credit;

    }

    /**
     *
     * @return retourne le total des credits en cour
     */
        public int getRecapTcredit(){
            bd = accessBD.getReadableDatabase();
            String req  = "select SUM(sommecredit) AS t_credit from credit where reste != 0";

            Cursor cursor = bd.rawQuery(req,null);
            cursor.moveToFirst();
            int totalcredit = cursor.getInt(cursor.getColumnIndexOrThrow("t_credit"));
            cursor.close();
            return totalcredit;
        }

    /**
     *
     * @return retourne le total des versements des credits en cour
     */
    public int getRecapTversement(){
            bd = accessBD.getReadableDatabase();
            String req  = "select SUM(versements) AS t_versement from credit where reste != 0";

            Cursor cursor = bd.rawQuery(req,null);
            cursor.moveToFirst();
            int totalversement = cursor.getInt(cursor.getColumnIndexOrThrow("t_versement"));
            cursor.close();
            return totalversement;
        }

    /**
     *
     * @return retourne le total du reste des credits en cour
     */
    public int getRecapTreste(){
            bd = accessBD.getReadableDatabase();
            String req  = "select SUM(reste) AS t_reste from credit where reste != 0";

            Cursor cursor = bd.rawQuery(req,null);
            cursor.moveToFirst();
            int totalreste = cursor.getInt(cursor.getColumnIndexOrThrow("t_reste"));
            cursor.close();

            return totalreste;
        }


    /**
     *
     * @param client le client
     * @return total des credits d'un client
     */
    public int getRecapTcreditClient(ClientModel client){
        bd = accessBD.getReadableDatabase();
        String req = "select SUM(sommecredit) AS t_credit from credit where  reste != 0 and clientid ='" + client.getId()+"'";
        Cursor cursor = bd.rawQuery(req,null);
        cursor.moveToFirst();
        int totalcredit = cursor.getInt(cursor.getColumnIndexOrThrow("t_credit"));
        cursor.close();
        return totalcredit;
    }

    /**
     *
     * @param client le client
     * @return total desversements d'un client
     */
    public int getRecapTversementClient(ClientModel client){
        bd = accessBD.getReadableDatabase();
        String req = "select SUM(versements) AS t_versement from credit where  reste != 0 and clientid ='" + client.getId()+"'";
        Cursor cursor = bd.rawQuery(req,null);
        cursor.moveToFirst();
        int totalversement = cursor.getInt(cursor.getColumnIndexOrThrow("t_versement"));
        cursor.close();
        return totalversement;
    }

    /**
     *
     * @param client le client
     * @return total du reste des credits d'un client
     */
    public int getRecapTresteClient(ClientModel client){
        bd = accessBD.getReadableDatabase();

        String req = "select SUM(reste) AS t_reste from credit where  reste != 0 and clientid ='" + client.getId()+"'";
        Cursor cursor = bd.rawQuery(req,null);
        cursor.moveToFirst();
        int totalreste = cursor.getInt(cursor.getColumnIndexOrThrow("t_reste"));
        cursor.close();

        return totalreste;
    }


    // cette fonction doit etre appeler automatiquement pour supprimer un crdit
    // 6 mois apres que le credit est ete solder
    public boolean supprimerUncredit(CreditModel credit) {
        boolean success;
        try{
            accessBD.getWritableDatabase().delete(TABLE_CREDIT,ID +"=?",new String[]{String.valueOf(credit.getId())});
            success=true;
        }catch( SQLiteException e) {
            success=false;
        }
        return success;
    }



}
