package com.jay.easygest.outils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.jay.easygest.controleur.Accountcontroller;
import com.jay.easygest.model.AccountModel;
import com.jay.easygest.model.ClientModel;

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
    public static final String TABLE_ACCOUNT = "account";
    public static final String NBRACCOUNT = "nbraccount";
    public static final String TOTALACCOUNT = "totalaccount";
    public static final String ACCOUNTID = "accountid";
    public static final String SOLDEDAT = "soldedat";
    private final MySqliteOpenHelper accessBD;
    private final Context contexte;
    private SQLiteDatabase bd;
    private AccessLocalVersementacc accessLocalVersementacc;
    private final AccessLocalClient accessLocalClient;


    public AccessLocalAccount(Context contexte) {
        this.contexte = contexte;
        this.accessBD = new MySqliteOpenHelper(contexte,null);
        accessLocalClient = new AccessLocalClient(contexte);

    }

    private ContentValues creerAccount(AccountModel account, long client_id) {
        ContentValues cv = new ContentValues();
        long date_de_solde;
        if (account.getReste() == 0){
            date_de_solde = account.getDateaccount();
        }else {date_de_solde = 0L;}
        account.setSoldedat(date_de_solde);
        cv.put(CLIENTID,client_id);
        cv.put(ARTICLE_1,account.getArticle1());
        cv.put(ARTICLE_2,account.getArticle2());
        cv.put(SOMMEACCOUNT,account.getSommeaccount());
        cv.put(VERSEMENTS,account.getVersement());
        cv.put(RESTE,account.getReste());
        cv.put(DATEACCOUNT,account.getDateaccount());
        cv.put(NUMEROACCOUNT,account.getNumeroaccount());
        cv.put(SOLDEDAT,date_de_solde);
        return cv;
    }


    /**
     * cree l'account si le client n'existe pas encore
     * @param premieraccount premier account du client
     * @param codeclt code du client fraichement crée
     * @param nomclient le nom du client fraichement crée
     * @param prenomsclient les prenoms du client fraichement crée
     * @param telephone le numero de téléphone du client fraichement crée
     * @param sommeversee la somme de l'account
     * @return l'account fraichement crée
     */
    public AccountModel creerCompteAccount(AccountModel premieraccount, String codeclt, String nomclient, String prenomsclient, String telephone, String sommeversee) {
        bd = accessBD.getWritableDatabase();
         accessLocalVersementacc = new AccessLocalVersementacc(contexte);
        bd.beginTransaction();
        AccountModel accountModel;
        try {

            long client_reslt = bd.insertOrThrow(TABLE_CLIENT,null,accessLocalClient.ajouterClient(codeclt, nomclient, prenomsclient,telephone,0,0,1,premieraccount.getSommeaccount()));
            long account_rslt = bd.insertOrThrow(TABLE_ACCOUNT,null,this.creerAccount(premieraccount,client_reslt));
            if (Integer.parseInt(sommeversee) != 0){bd.insertOrThrow(TABLE_VERSEMENTACC,null,accessLocalVersementacc.creerVersement(Integer.parseInt(sommeversee), (int) account_rslt,(int) client_reslt,premieraccount.getDateaccount()));}
            accountModel = this.recupAccountById((int) account_rslt);
            bd.setTransactionSuccessful();

        }catch (Exception e){
            accountModel = null;
        }finally {
            bd.endTransaction();
        }

        return accountModel;
    }

    /**
     * ajoute un account au compte d'un client
     * @param account l'account ajouté
     * @param client le client
     * @return l'account ajouté
     */
    public AccountModel ajouterAccount(AccountModel account, ClientModel client) {

        bd = accessBD.getWritableDatabase();
        accessLocalVersementacc = new AccessLocalVersementacc(contexte);
        ContentValues client_cv= new ContentValues();

        client_cv.put(NBRACCOUNT,client.getNbraccount() + 1);
        client_cv.put(TOTALACCOUNT,client.getTotalaccount() + account.getSommeaccount());
        bd.beginTransaction();

        AccountModel accountModel;
        try {
            long account_rslt =  bd.insertOrThrow(TABLE_ACCOUNT,null,this.creerAccount(account,client.getId()));
            bd.updateWithOnConflict(TABLE_CLIENT,client_cv, ID + "=?",new String[] {String.valueOf(client.getId())},1);
            if (account.getVersement() != 0){bd.insertOrThrow(TABLE_VERSEMENTACC,null,accessLocalVersementacc.creerVersement(account.getVersement(), (int) account_rslt, client.getId(),account.getDateaccount()));}
            accountModel = this.recupAccountById((int) account_rslt);
            bd.setTransactionSuccessful();

        }catch (Exception e){

            accountModel = null;
        }finally {
            bd.endTransaction();

        }

        return accountModel;
    }


    /**
     *
     * @param account l'account
     * @param client le client
     * @param ancienne_somme_account ancien account versé
     * @return l'account modifier
     */
    public AccountModel modifierAccount(AccountModel account, ClientModel client,int ancienne_somme_account ) {

        bd = accessBD.getWritableDatabase();
        bd.beginTransaction();
        AccountModel accountModel;
        try{
            int ancien_total_account_du_client =  Integer.parseInt(String.valueOf(client.getTotalaccount())) ;
            int nouveau_total_account_du_client = ( ancien_total_account_du_client - ancienne_somme_account) + account.getSommeaccount();
            ContentValues account_cv = new ContentValues();
            ContentValues client_cv = new ContentValues();

            account_cv.put(ARTICLE_1,account.getArticle1());
            account_cv.put(ARTICLE_2,account.getArticle2());
            account_cv.put(SOMMEACCOUNT,account.getSommeaccount());
            account_cv.put(VERSEMENTS,account.getVersement());
            account_cv.put(RESTE,account.getReste());
            account_cv.put(DATEACCOUNT,account.getDateaccount());
            account_cv.put(SOLDEDAT,account.getSoldedat());

            client_cv.put(TOTALACCOUNT,nouveau_total_account_du_client);
            bd.updateWithOnConflict(TABLE_ACCOUNT,account_cv, ID + "=" +account.getId(),null,1);
            bd.updateWithOnConflict(TABLE_CLIENT, client_cv, "id = ?", new String[] {String.valueOf(client.getId())},1);
             accountModel = this.recupAccountById(account.getId());
            bd.setTransactionSuccessful();
        }catch (Exception e){
            accountModel = null;
        }
        finally {
            bd.endTransaction();
        }
        return accountModel;
    }


    /**
     * annulation d'un account
     * @param account l'account à annuller
     * @return vrai si l'account à été annulé sinon faux
     */
    public boolean anullerAccount(AccountModel account){
        boolean success ;
        bd.beginTransaction();
        try {
            bd = accessBD.getWritableDatabase();
            ContentValues cvclient = new ContentValues();

            cvclient.put(NBRACCOUNT,account.getClient().getNbraccount() - 1);
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

    /**
     * suppression d'un account
     * @param client le client
     * @return vrai si l'account à été supprimé sinon faux
     */
    public boolean supprimerAccountSoldeClient(ClientModel client) {
        bd = accessBD.getReadableDatabase();
        boolean success;
        try {
            bd.delete(TABLE_ACCOUNT,ID+"=?",new String[]{String.valueOf(client.getId())});

            success=true;
        }catch (Exception e){
            success = false;
        }
        return success;
    }

    /**
     * recupere un account par son id
     * @param accountId l'id de l'account à récupérer
     * @return l'account
     */
    public AccountModel recupAccountById(int accountId) {
        AccountModel account = null;

        try {
            bd = accessBD.getReadableDatabase();
            String req = "select * from account where " + ID + "="+accountId+"";
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
                int nbraccount = cursor.getInt(8);

                account = new AccountModel(accountId, clientid, article1, article2, sommeaccount, versement, reste, dateaccount,nbraccount);
                account.setSoldedat(cursor.getLong(9));
            }
            cursor.close();

        }catch (Exception e){
            return account;
        }
        return account;

    }


    /**
     *
     * @return la liste de tous les accounts en cours
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
                account.setSoldedat(cursor.getLong(9));
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
                account.setSoldedat(cursor.getLong(9));
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
     * @return retourne la liste des accounts soldés du client
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
                account.setSoldedat(cursor.getLong(9));
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
     * @return le total des accounts du client
     */
    public int getRecapTaccountClient(ClientModel client){
        bd = accessBD.getReadableDatabase();
        String req = "select SUM(sommeaccount) AS t_account from account where  reste != 0 and clientid ='" + client.getId()+"'";
        Cursor cursor = bd.rawQuery(req,null);
        cursor.moveToFirst();
        int totalaccount = cursor.getInt(cursor.getColumnIndexOrThrow("t_account"));
        cursor.close();
        return totalaccount;
    }

    /**
     *
     * @param client le clent
     * @return le total du reste des accounts du client à payer
     */
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


    public boolean isClientOwnAccount(ClientModel clientModel) {

        ArrayList<AccountModel> accounts = this.listeAccountsClient(clientModel);

        if (accounts.size() == 0){
            return false;
        }else {return true;}

    }
}
