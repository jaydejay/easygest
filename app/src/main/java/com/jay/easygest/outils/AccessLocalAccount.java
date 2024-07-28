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

public class AccessLocalAccount  {

    public static final String TABLE_VERSEMENTACC = "versementacc";
    public static final String CODECLIENT = "codeclient";
    public static final String CLIENTID = "clientid";
    public static final String VERSEMENTS = "versements";
    public static final String TABLE_CLIENT = "client";

    public static final String NOM = "nom";
    public static final String ARTICLE_1 = "article1";
    public static final String ARTICLE_2 = "article2";

    public static final String SOMMEACCOUNT = "sommeaccount";
    public static final String RESTE = "reste";
    public static final String DATEACCOUNT = "dateaccount";
    public static final String TABLE_ACCOUNT = "account";
    public static final String ID = "id";
    public static final String NUMEROACCOUNT = "numeroaccount";
    public static final String NBRACCOUNT = "nbraccount";

    private static final String ACCOUNTID = "accountid";
    public static final String NBRCREDIT="nbrcredit";
    public static final String TOTALCREDIT="totalcredit";
    public static final String PRENOMS = "prenoms";
    public static final String TELEPHONE = "telephone";
    public static final String ADRESSEELECTRO = "adresseelectro";
    public static final String RESIDENCE = "residence";
    public static final String CNI = "cni";
    public static final String PERMIS = "permis";
    public static final String PASSPORT = "passport";
    public static final String SOCIETE = "societe";
    public static final String TOTALACCOUNT = "totalaccount";

    private final MySqliteOpenHelper accessBD;
    private final Context contexte;
    private AccessLocalVersementacc accessLocalVersementacc;
    private  AccessLocalClient accessLocalClient;
    private SQLiteDatabase bd;

    public AccessLocalAccount(Context contexte){
        this.contexte = contexte;
        this.accessBD = new MySqliteOpenHelper(contexte, null);
        accessLocalVersementacc = new AccessLocalVersementacc(contexte);
        accessLocalClient = new AccessLocalClient(contexte);
    }

    public ContentValues creerAccount(AccountModel account, long client_id) {

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
    

    public boolean creerCompteAccount(AccountModel premieraccount, String codeclt, String nom, String prenoms, String telephone, String sommeversee ){
        SQLiteDatabase bd = accessBD.getWritableDatabase();
//        AccessLocalVersement accessLocalVersement = new AccessLocalVersement(contexte);
        AccessLocalClient accessLocalClient = new AccessLocalClient(contexte);

        bd.beginTransaction();
        boolean success=false;
        try {

            long client_reslt = bd.insertOrThrow(TABLE_CLIENT,null, accessLocalClient.ajouterClient(codeclt, nom, prenoms,telephone,0,0,1,premieraccount.getSommeaccount()));
            long account_rslt = bd.insertOrThrow(TABLE_ACCOUNT,null,this.creerAccount(premieraccount,client_reslt));
            bd.insertOrThrow(TABLE_VERSEMENTACC,null, accessLocalVersementacc.creerVersement(Integer.parseInt(sommeversee), (int) account_rslt,(int) client_reslt,premieraccount.getDateaccount()));
            bd.setTransactionSuccessful();
            success = true;

        }catch (Exception e){
            return success;
        }finally {
            bd.endTransaction();
           
        }
        
        return success;

    }

    public boolean ajouterAccount(AccountModel account, ClientModel client){
         bd = accessBD.getWritableDatabase();
        accessLocalVersementacc = new AccessLocalVersementacc(contexte);
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
        client_cv.put(NBRCREDIT,client.getNbrcredit());
        client_cv.put(TOTALCREDIT,client.getTotalcredit());
        client_cv.put(NBRACCOUNT,client.getNbraccount()+1);
        client_cv.put(TOTALACCOUNT,client.getTotalaccount()+account.getSommeaccount());
        bd.beginTransaction();
        boolean success  ;
        try {
            bd.replaceOrThrow(TABLE_CLIENT,null,client_cv);
            long account_rslt =  bd.insertOrThrow(TABLE_ACCOUNT,null,this.creerAccount(account,client.getId()));
            bd.insertOrThrow(TABLE_VERSEMENTACC,null,accessLocalVersementacc.creerVersement(account.getVersement(),(int) account_rslt, client.getId(),account.getDateaccount()));
            bd.setTransactionSuccessful();
            success = true;

        }catch (Exception e){
            success = false;
        }finally {
            bd.endTransaction();

        }

        return success;

    }

    public AccountModel recupAccountById(int accountid) {

        AccountModel account = null;
        try {
            bd = accessBD.getReadableDatabase();
            String req = "select * from account where " + ID + "="+accountid+"";
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
                account = new AccountModel(accountid, clientid, article1, article2, sommecredit, versement, reste, datecredit,nbrcredit);

            }
            cursor.close();

        }catch (Exception e){
            //do nothing
        }
        return account;

    }

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
//            creditcontrolleur = Creditcontrolleur.getCreditcontrolleurInstance(null);
//            creditcontrolleur.setCredits(credits);

        }catch(Exception e){
            //  do nothing
        }
        return accounts;

    }

    public ArrayList<AccountModel> listeAccountsclient(ClientModel client){
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
           Accountcontroller accountcontroller = Accountcontroller.getAccountcontrolleurInstance(contexte);
            accountcontroller.setAccounts(accounts);

        }catch(Exception e){
            //  do nothing
        }
        return accounts;

    }

    public ArrayList<AccountModel> listeDesAccountsSoldesClient(ClientModel client) {

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
//            creditcontrolleur = Creditcontrolleur.getCreditcontrolleurInstance(null);
//            creditcontrolleur.setCredits(credits);

        }catch(Exception e){
            //  do nothing
        }
        return accounts;


    }

    public boolean modifierAccount(int id,ClientModel client, String article1, String article2, Integer nouvelle_sommeaccount, Integer versement, Integer reste,  Long dateaccount, long ancienne_sommeaccount) {

        accessLocalClient = new AccessLocalClient(contexte);
        bd = accessBD.getWritableDatabase();
        bd.beginTransaction();
        boolean success ;
        try{
            int ancien_total_account_du_client =  Integer.parseInt(String.valueOf(client.getTotalaccount())) ;
            int ancienne_somme_account = Integer.parseInt(String.valueOf(ancienne_sommeaccount)) ;
            int nouveau_total_account_du_client = ( ancien_total_account_du_client - ancienne_somme_account) + nouvelle_sommeaccount;
            ContentValues account_cv = new ContentValues();
            ContentValues client_cv = new ContentValues();

            account_cv.put(ARTICLE_1,article1);
            account_cv.put(ARTICLE_2,article2);
            account_cv.put(SOMMEACCOUNT,nouvelle_sommeaccount);
            account_cv.put(VERSEMENTS,versement);
            account_cv.put(RESTE,reste);
            account_cv.put(DATEACCOUNT,dateaccount);

            client_cv.put(TOTALACCOUNT,nouveau_total_account_du_client);

//            AccountModel account;
            bd.updateWithOnConflict(TABLE_ACCOUNT,account_cv, ID + "=" +id,null,1);
            bd.updateWithOnConflict(TABLE_CLIENT, client_cv, "id = ?", new String[] {String.valueOf(client.getId())},1);
            bd.setTransactionSuccessful();

//            Accountcontroller accountcontroller =  Accountcontroller.getAccountcontrolleurInstance(contexte);
//            account = new AccountModel(id,client,article1,article2,nouvelle_sommeaccount,versement,reste,dateaccount,numeroaccount);
//            creditcontrolleur.setCredit(credit);
            success = true;
        }catch (Exception e){
            success=false;
        }
        finally {
            bd.endTransaction();
        }
        return success;
    }
}
