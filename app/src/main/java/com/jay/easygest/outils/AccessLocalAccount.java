package com.jay.easygest.outils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.jay.easygest.controleur.Accountcontroller;
import com.jay.easygest.controleur.Creditcontrolleur;
import com.jay.easygest.model.AccountModel;
import com.jay.easygest.model.ClientModel;
import com.jay.easygest.model.CreditModel;

import java.util.ArrayList;


public class AccessLocalAccount {

    public static final String TABLE_VERSEMENTACC = "versementacc";
    public static final String TABLE_CLIENT = "client";
    public static final String CLIENTID = "clientid";
    public static final String ARTICLE_1 = "article1";
    public static final String ARTICLE_2 = "article2";

    public static final String VERSEMENTS = "versements";
    public static final String RESTE = "reste";
    public static final String DATEACCOUNT = "dateaccount";
    public static final String NUMEROACCOUNT = "numeroaccount";
    public static final String ID = "id";
    public static final String SOMMEACCOUNT = "sommeaccount";
    public static final String TABLE_ACCOUNT ="account" ;
    public static final String NBREACCOUNT = "nbreaccount";
//    public static final String TOTALCREDIT = "totalcredit";
    public static final String TOTALACCOUNT = "totalaccount";
    public static final String ACCOUNTID = "accountid";

    private MySqliteOpenHelper accessBD;
    private final Context contexte;
    private SQLiteDatabase bd;
    private AccessLocalVersementacc accessLocalVersementacc;
    private AccessLocalClient accessLocalClient;


    public AccessLocalAccount(Context contexte) {
        this.contexte = contexte;
        this.accessBD = new MySqliteOpenHelper(contexte,null);
        accessLocalVersementacc = new AccessLocalVersementacc(contexte);
        accessLocalClient = new AccessLocalClient(contexte);

    }

    private ContentValues creerAccount(AccountModel account, long client_id) {
        ContentValues cv = new ContentValues();
        cv.put(CLIENTID,client_id);
        cv.put(ARTICLE_1,account.getArticle1());
        cv.put(ARTICLE_2,account.getArticle2());
        cv.put(SOMMEACCOUNT,account.getSommeaccount());
        cv.put(VERSEMENTS,account.getVersement());
        cv.put(RESTE,account.getReste());
        cv.put(DATEACCOUNT,account.getDateaccount());
        cv.put(NUMEROACCOUNT,account.getNumeroaccount());
        return cv;
    }


    public boolean creerCompteAccount(AccountModel premieraccount, String codeclt, String nomclient, String prenomsclient, String telephone, String sommeversee) {
        bd = accessBD.getWritableDatabase();
        AccessLocalVersementacc accessLocalVersementacc = new AccessLocalVersementacc(contexte);
        AccessLocalClient accessLocalClient = new AccessLocalClient(contexte);
        bd.beginTransaction();
        boolean success;
        try {

            long client_reslt = bd.insertOrThrow(TABLE_CLIENT,null,accessLocalClient.ajouterClient(codeclt, nomclient, prenomsclient,telephone,0,0,1,premieraccount.getSommeaccount()));
            long account_rslt = bd.insertOrThrow(TABLE_ACCOUNT,null,this.creerAccount(premieraccount,client_reslt));
            bd.insertOrThrow(TABLE_VERSEMENTACC,null,accessLocalVersementacc.creerVersement(Integer.parseInt(sommeversee), (int) account_rslt,(int) client_reslt,premieraccount.getDateaccount()));
            bd.setTransactionSuccessful();
            success = true;

        }catch (Exception e){
             success = false;
        }finally {
            bd.endTransaction();

        }
        return success;
    }

    public boolean ajouterAccount(AccountModel account, ClientModel client) {

        bd = accessBD.getWritableDatabase();
        accessLocalVersementacc = new AccessLocalVersementacc(contexte);
        ContentValues client_cv= new ContentValues();
//        client_cv.put(ID,client.getId());
//        client_cv.put(CODECLIENT,client.getCodeclient());
//        client_cv.put(NOM,client.getNom());
//        client_cv.put(PRENOMS,client.getPrenoms());
//        client_cv.put(TELEPHONE,client.getTelephone());
//        client_cv.put(ADRESSEELECTRO,client.getEmail());
//        client_cv.put(RESIDENCE,client.getResidence());
//        client_cv.put(CNI,client.getCni());
//        client_cv.put(PERMIS,client.getPermis());
//        client_cv.put(PASSPORT,client.getPassport());
//        client_cv.put(SOCIETE,client.getSociete());
        client_cv.put(NBREACCOUNT,client.getNbraccount() + 1);
        client_cv.put(TOTALACCOUNT,client.getTotalaccount() + account.getSommeaccount());
        bd.beginTransaction();
        boolean success ;
        try {
            long account_rslt =  bd.insertOrThrow(TABLE_ACCOUNT,null,this.creerAccount(account,client.getId()));
            bd.updateWithOnConflict(TABLE_CLIENT,client_cv, ID + "=?",new String[] {String.valueOf(client.getId())},1);
            bd.insertOrThrow(TABLE_VERSEMENTACC,null,accessLocalVersementacc.creerVersement(account.getVersement(), (int) account_rslt, client.getId(),account.getDateaccount()));
            bd.setTransactionSuccessful();
            success = true;

        }catch (Exception e){
            success = false;
        }finally {
            bd.endTransaction();

        }

        return success;
    }


    public boolean modifierAccount(AccountModel account, ClientModel client,int ancienne_somme_account ) {

        bd = accessBD.getWritableDatabase();
        bd.beginTransaction();
        boolean success ;
        try{
            int ancien_total_account_du_client =  Integer.parseInt(String.valueOf(client.getTotalaccount())) ;
            int nouvelle_sommecredit = account.getSommeaccount();

            int nouveau_total_account_du_client = ( ancien_total_account_du_client - ancienne_somme_account) + nouvelle_sommecredit;
            ContentValues account_cv = new ContentValues();
            ContentValues client_cv = new ContentValues();

            account_cv.put(ARTICLE_1,account.getArticle1());
            account_cv.put(ARTICLE_2,account.getArticle2());
            account_cv.put(SOMMEACCOUNT,nouvelle_sommecredit);
            account_cv.put(VERSEMENTS,account.getVersement());
            account_cv.put(RESTE,account.getReste());
            account_cv.put(DATEACCOUNT,account.getDateaccount());

            client_cv.put(TOTALACCOUNT,nouveau_total_account_du_client);

            bd.updateWithOnConflict(TABLE_ACCOUNT,account_cv, ID + "=" +account.getId(),null,1);
            bd.updateWithOnConflict(TABLE_CLIENT, client_cv, "id = ?", new String[] {String.valueOf(client.getId())},1);
            bd.setTransactionSuccessful();

            success = true;
        }catch (Exception e){
            success = false;
        }
        finally {
            bd.endTransaction();
        }
        return success;
    }


    public boolean anullerAccount(AccountModel account){
        boolean success ;
        bd.beginTransaction();
        try {
            bd = accessBD.getWritableDatabase();

            ContentValues cvclient = new ContentValues();
            cvclient.put(NBREACCOUNT,account.getClient().getNbrcredit() - 1);
            cvclient.put(TOTALACCOUNT,account.getClient().getTotalaccount() - account.getSommeaccount());

            bd.delete(TABLE_ACCOUNT,ID +"=?",new String[]{String.valueOf(account.getId())});
            bd.delete(TABLE_VERSEMENTACC, ACCOUNTID +"=?",new String[]{String.valueOf(account.getId())});
            bd.updateWithOnConflict(TABLE_CLIENT,cvclient, ID + "=" +account.getClient().getId(),null,1);
            bd.setTransactionSuccessful();
            success = true;
        }catch (Exception e){
            success = false;
        }finally {
            bd.endTransaction();
        }
        return  success;
    }

    public AccountModel recupAccountById(int accountId) {
        AccountModel account = null;

        try {
            bd = accessBD.getReadableDatabase();
            String req = "select * from accout where " + ID + "="+accountId+"";
            Cursor cursor = bd.rawQuery(req, null);
            cursor.moveToLast();
            if (!cursor.isAfterLast()) {
                int clientid = cursor.getInt(1);
                String article1 = cursor.getString(2);
                String article2 = cursor.getString(3);
                int sommeaccount = cursor.getInt(4);
                int versement = cursor.getInt(5);
                int reste = cursor.getInt(6);
                long dateaccount = cursor.getLong(7);
                Integer nbraccount = cursor.getInt(8);
                account = new AccountModel(accountId, clientid, article1, article2, sommeaccount, versement, reste, dateaccount,nbraccount);

            }
            cursor.close();

        }catch (Exception e){
            return account;
        }
        return account;

    }


    /**
     *
     * @return la liste des accounts en cours
     */

    public ArrayList<AccountModel> listeAccounts(){
        ArrayList<AccountModel> accounts = new ArrayList<>();

        try {
            bd = accessBD.getReadableDatabase();

            String req = "select * from account where reste != 0 ";
            Cursor cursor = bd.rawQuery(req, null);
            cursor.moveToFirst();
            do {
                ClientModel client = accessLocalClient.recupUnClient(cursor.getInt(1));
                AccountModel account = new AccountModel(cursor.getInt(0),client,cursor.getString(2),cursor.getString(3),cursor.getInt(4),cursor.getInt(5),cursor.getInt(6),cursor.getLong(7),cursor.getInt(8));
                accounts.add(account);
            }
            while (cursor.moveToNext());

            cursor.close();
            Accountcontroller accountController = Accountcontroller.getAccountcontrolleurInstance(contexte);
            accountController.setAccounts(accounts);

        }catch(Exception e){
            return null;
        }
        return accounts;

    }

    /**
     *
     * @param client le client
     * @return retourne la liste des accounts en cours du client
     */

    public ArrayList<AccountModel> listeAccountsClient(ClientModel client) {

        ArrayList<AccountModel> accounts = new ArrayList<>();
        try {
            bd = accessBD.getReadableDatabase();
            String req = "select * from account where  reste != 0 and clientid ='" + client.getId()+"'";
            Cursor cursor = bd.rawQuery(req, null);
            cursor.moveToFirst();
            do {
                AccountModel account = new AccountModel(cursor.getInt(0),client,cursor.getString(2),cursor.getString(3),cursor.getInt(4),cursor.getInt(5),cursor.getInt(6),cursor.getLong(7),cursor.getInt(8));
                accounts.add(account);
            }
            while (cursor.moveToNext());

            cursor.close();

        }catch(Exception e){
            return accounts;
        }
        return accounts;
    }

    /**
     *
     * @param client le client
     * @return retourne la liste des accounts soldé du client
     */

    public ArrayList<AccountModel> listeDESAccountsSoldesClient(ClientModel client) {

        ArrayList<AccountModel> accounts = new ArrayList<>();
        try {
            bd = accessBD.getReadableDatabase();
            String req = "select * from account where  reste = 0 and clientid ='" + client.getId()+"'";
            Cursor cursor = bd.rawQuery(req, null);
            cursor.moveToFirst();
            do {
                AccountModel account = new AccountModel(cursor.getInt(0),client,cursor.getString(2),cursor.getString(3),cursor.getInt(4),cursor.getInt(5),cursor.getInt(6),cursor.getLong(7),cursor.getInt(8));
                accounts.add(account);
            }
            while (cursor.moveToNext());

            cursor.close();
        }catch(Exception e){
            return accounts;
        }
        return accounts;
    }


    public int getRecapTaccountClient(ClientModel client){
        bd = accessBD.getReadableDatabase();
//        String req  = "select SUM(sommecredit) AS t_credit from credit where reste != 0 and clientid = "+client.getId();
        String req = "select SUM(sommeaccount) AS t_account from account where  reste != 0 and clientid ='" + client.getId()+"'";
        Cursor cursor = bd.rawQuery(req,null);
        cursor.moveToFirst();
        int totalaccount = cursor.getInt(cursor.getColumnIndexOrThrow("t_account"));
        cursor.close();
        return totalaccount;
    }

    public int getRecapTversementClient(ClientModel client){
        bd = accessBD.getReadableDatabase();
//        String req  = "select SUM(versements) AS t_versement from credit where reste != 0 and clientid = "+client.getId();
        String req = "select SUM(versements) AS t_versement from account where  reste != 0 and clientid ='" + client.getId()+"'";
        Cursor cursor = bd.rawQuery(req,null);
        cursor.moveToFirst();
        int totalversement = cursor.getInt(cursor.getColumnIndexOrThrow("t_versement"));
        cursor.close();
        return totalversement;
    }

    public int getRecapTresteClient(ClientModel client){
        bd = accessBD.getReadableDatabase();
//        String req  = "select SUM(reste) AS t_reste from credit where reste != 0 and clientid = "+client.getId();
        String req = "select SUM(reste) AS t_reste from account where  reste != 0 and clientid ='" + client.getId()+"'";
        Cursor cursor = bd.rawQuery(req,null);
        cursor.moveToFirst();
        int totalreste = cursor.getInt(cursor.getColumnIndexOrThrow("t_reste"));
        cursor.close();

        return totalreste;
    }
}