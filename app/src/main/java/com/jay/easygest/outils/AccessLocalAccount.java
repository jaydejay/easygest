package com.jay.easygest.outils;

import static com.jay.easygest.outils.VariablesStatique.DESIGNATION;
import static com.jay.easygest.outils.VariablesStatique.QUANTITE;
import static com.jay.easygest.outils.VariablesStatique.TABLE_ARTICLE;
import static com.jay.easygest.outils.VariablesStatique.TABLE_INFO;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.jay.easygest.model.AccountModel;
import com.jay.easygest.model.Article;
import com.jay.easygest.model.ArticlesModel;
import com.jay.easygest.model.ClientModel;
import com.jay.easygest.model.InfosModel;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;


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
    public static final String SOLDEDAT = "soldedat";
    private final MySqliteOpenHelper accessBD;
    private final Context contexte;
    private SQLiteDatabase bd;
    private AccessLocalVersementacc accessLocalVersementacc;
    private final AccessLocalClient accessLocalClient;
    private final Gson gson = new Gson() ;
//    private final Type articletype = new TypeToken<Article>(){}.getType();


    public AccessLocalAccount(Context context) {
        this.contexte = context;
        this.accessBD =  MySqliteOpenHelper.getInstance(contexte,null);
        accessLocalClient = new AccessLocalClient(contexte);

    }

    private ContentValues creerAccountContentValue(AccountModel account, long client_id) {
        ContentValues cv = new ContentValues();
        account.setSoldedat(account.getDateaccount());
        cv.put(CLIENTID,client_id);
        cv.put(ARTICLE_1,gson.toJson(account.getArticle1()));
        cv.put(ARTICLE_2,gson.toJson(account.getArticle2()));
        cv.put(SOMMEACCOUNT,account.getSommeaccount());
        cv.put(VERSEMENTS,account.getVersement());
        cv.put(RESTE,account.getReste());
        cv.put(DATEACCOUNT,account.getDateaccount());
        cv.put(NUMEROACCOUNT,account.getNumeroaccount());
        cv.put(SOLDEDAT,account.getSoldedat());
        return cv;
    }


    /**
     * cree l'account si le client n'existe pas encore
     * @param premieraccount premier account du client
     * @param telephone le numero de téléphone du client fraichement crée
     * @return l'account fraichement crée
     */
    public AccountModel creerCompteAccount(AccountModel premieraccount, String telephone, Map<String, Object> newdata) {
        bd = accessBD.getWritableDatabase();
        accessLocalVersementacc = new AccessLocalVersementacc(contexte);

        ContentValues client_cv = accessLocalClient.ajoutClientContentValue(premieraccount.getCodeclient(), premieraccount.getNomclient(),premieraccount.getPrenomsclient(),telephone,0,0,1,premieraccount.getSommeaccount());
        ContentValues article1_cv = new ContentValues();
        ArticlesModel article1 = (ArticlesModel) newdata.get("article1");
        int nbrarticle1restant = (int) newdata.get("nbrarticle1restant");
        article1_cv.put(QUANTITE,nbrarticle1restant);

        ContentValues article2_cv = new ContentValues();
        ArticlesModel article2 = (ArticlesModel) newdata.get("article2");
        int nbrarticle2restant = (int) newdata.get("nbrarticle2restant");
        article2_cv.put(QUANTITE,nbrarticle2restant);
        bd.beginTransaction();
        AccountModel accountModel;
        try {

            long client_reslt = bd.insertWithOnConflict(TABLE_CLIENT,null,client_cv,1);
            long account_rslt = bd.insertWithOnConflict(TABLE_ACCOUNT,null,this.creerAccountContentValue(premieraccount,client_reslt),1);
            if (premieraccount.getVersement() > 0){bd.insertWithOnConflict(TABLE_VERSEMENTACC,null,accessLocalVersementacc.creerVersement(premieraccount.getVersement(), (int) account_rslt,(int) client_reslt,premieraccount.getDateaccount()),1);}

            bd.updateWithOnConflict(TABLE_ARTICLE,article1_cv,"designation =?", new String[] {article1.getDesignation()},1);
            if (article2 != null && !article2.getDescription().equals("Choisir un article") ){
                bd.updateWithOnConflict(TABLE_ARTICLE,article2_cv,"designation =?", new String[] {article2.getDesignation()},1);
            }
            accountModel = this.recupAccountById((int) account_rslt);
            InfosModel info  = this.getInfo(bd);
            ContentValues infos_cv = new ContentValues();
            infos_cv.put("nbraccount",info.getNbraccount()+1);
            infos_cv.put("totalaccount",info.getTotalaccount()+accountModel.getSommeaccount());
            bd.updateWithOnConflict(TABLE_INFO,infos_cv,"appnumber = ?", new String[] {String.valueOf(info.getAppnumber())},1);
            bd.setTransactionSuccessful();
        }catch (Exception e){
            accountModel = null;
        }finally {
            if (bd.inTransaction()){
                bd.endTransaction();
            }
        }
        return accountModel;
    }

    /**
     * ajoute un account au compte d'un client
     * @param account l'account ajouté
     * @return l'account ajouté
     */
    public AccountModel ajouterAccount(AccountModel account,ArticlesModel article1, ArticlesModel article2) {

        bd = accessBD.getWritableDatabase();
        accessLocalVersementacc = new AccessLocalVersementacc(contexte);
        ContentValues client_cv = new ContentValues();
        client_cv.put(NBRACCOUNT,account.getNumeroaccount());
        client_cv.put(TOTALACCOUNT,account.getClient().getTotalaccount() + account.getSommeaccount());

        ContentValues article1_cv = new ContentValues();
        int nbrarticle1restant = article1.getQuantite() - account.getArticle1().getNbrarticle();
        article1_cv.put(QUANTITE,nbrarticle1restant);

        ContentValues article2_cv = new ContentValues();
        int nbrarticle2restant = article2.getQuantite() - account.getArticle2().getNbrarticle();
        article2_cv.put(QUANTITE,nbrarticle2restant);

        bd.beginTransaction();
        AccountModel accountModel;
        try {
            long account_rslt =  bd.insertWithOnConflict(TABLE_ACCOUNT,null,this.creerAccountContentValue(account,account.getClient().getId()),1);
            bd.updateWithOnConflict(TABLE_CLIENT,client_cv, ID + "=?",new String[] {String.valueOf(account.getClient().getId())},1);
            if (account.getVersement() > 0){bd.insertWithOnConflict(TABLE_VERSEMENTACC,null,accessLocalVersementacc.creerVersement(account.getVersement(), (int) account_rslt, account.getClient().getId(),account.getDateaccount()),1);}

            bd.updateWithOnConflict(TABLE_ARTICLE,article1_cv,"designation =?", new String[] {article1.getDesignation()},1);
            if (!article2.getDescription().equals("Choisir un article")){
                bd.updateWithOnConflict(TABLE_ARTICLE,article2_cv,"designation =?", new String[] {article2.getDesignation()},1);
            }
            accountModel = this.recupAccountById((int) account_rslt);
            InfosModel info  = this.getInfo(bd);
            ContentValues infos_cv = new ContentValues();
            infos_cv.put("nbraccount",info.getNbraccount()+1);
            infos_cv.put("totalaccount",info.getTotalaccount()+accountModel.getSommeaccount());
            bd.updateWithOnConflict(TABLE_INFO,infos_cv,"appnumber = ?", new String[] {String.valueOf(info.getAppnumber())},1);
            bd.setTransactionSuccessful();

        }catch (Exception e){
            accountModel = null;
        }finally {
            if (bd.inTransaction()){
                bd.endTransaction();
            }
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

            account_cv.put(ARTICLE_1,gson.toJson(account.getArticle1()));
            account_cv.put(ARTICLE_2,gson.toJson(account.getArticle2()));
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
        return accountModel;
    }


    /**
     * annulation d'un account
     * @param account l'account à annuller
     * @return vrai si l'account à été annulé sinon faux
     */
    public boolean anullerAccount(AccountModel account){
        boolean success = false;
        bd = accessBD.getWritableDatabase();
        bd.setForeignKeyConstraintsEnabled(true);
        bd.beginTransaction();
        try {
            ContentValues cvclient = new ContentValues();
            Article article1 = account.getArticle1();
            ContentValues article1_cv = new ContentValues();
            ArticlesModel articlesModel1 = this.getArticleidAndDesignation(bd,article1.getDesignation());
            article1_cv.put(QUANTITE,articlesModel1.getQuantite() + article1.getNbrarticle() );

            Article article2 = account.getArticle2();
            ContentValues article2_cv = new ContentValues();
            ArticlesModel articlesModel2 = this.getArticleidAndDesignation(bd,article2.getDesignation());
            article2_cv.put(QUANTITE,articlesModel2.getQuantite() + article2.getNbrarticle());

            cvclient.put(NBRACCOUNT,account.getClient().getNbraccount() - 1);
            cvclient.put(TOTALACCOUNT,account.getClient().getTotalaccount() - account.getSommeaccount());

          int rslt =  bd.delete(TABLE_ACCOUNT,ID +"=?",new String[]{String.valueOf(account.getId())});
            if (rslt > 0){
                //            bd.delete(TABLE_VERSEMENTACC, ACCOUNTID +"=?",new String[]{String.valueOf(account.getId())});
                bd.updateWithOnConflict(TABLE_CLIENT,cvclient, ID + "=" +account.getClient().getId(),null,1);
                if (!article2.getDesignation().equals("Choisir un article")){
                    bd.updateWithOnConflict(TABLE_ARTICLE,article2_cv, DESIGNATION + "=?",new String[]{article2.getDesignation()},1);
                }

                InfosModel info  = this.getInfo(bd);
                ContentValues infos_cv = new ContentValues();
                infos_cv.put("nbraccount",info.getNbraccount()-1);
                infos_cv.put("totalaccount",info.getTotalaccount()-account.getSommeaccount());
                bd.updateWithOnConflict(TABLE_INFO,infos_cv,"appnumber = ?", new String[] {String.valueOf(info.getAppnumber())},1);
                bd.setTransactionSuccessful();
                success = true;
            }else {
                bd.endTransaction();
            }

        }catch (Exception e){
           return false;
        }finally {
            if (bd.inTransaction()){
                bd.endTransaction();
            }
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
        bd.setForeignKeyConstraintsEnabled(true);
        boolean success = false;
        try {
            bd.delete(TABLE_ACCOUNT,ID+"=?",new String[]{String.valueOf(client.getId())});
            success = true;
        }catch (Exception e){
           return success;
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
        bd = accessBD.getReadableDatabase();
        try {
            String req = "select * from account where " + ID + "="+accountId;
            Cursor cursor = bd.rawQuery(req, null);
            cursor.moveToLast();
            if (!cursor.isAfterLast()) {
                ClientModel client = this.recupUnClient(bd,cursor.getInt(1));
                account = getAccountModelfromCursor(cursor,client);
                account.setSoldedat(cursor.getLong(9));
            }
            cursor.close();
        }catch (Exception e){
            return account;
        }
        return account;
    }

    @NonNull
    private AccountModel getAccountModelfromCursor( Cursor cursor,ClientModel client) {
       int accountId = cursor.getInt(0);
        Article article1 = gson.fromJson(cursor.getString(2),Article.class);
        Article article2 = gson.fromJson(cursor.getString(3),Article.class);
        int versement = cursor.getInt(5);
        long dateaccount = cursor.getLong(7);
        int nbraccount = cursor.getInt(8);
        return new AccountModel(accountId,client,article1,article2,versement,dateaccount,nbraccount);
    }

    private ClientModel recupUnClient(SQLiteDatabase bd,int clientid) {
        ClientModel client = null;
        try {
            String req = "select * from client where " + ID + "='"+clientid+"'";
            Cursor cursor = bd.rawQuery(req, null);
            cursor.moveToLast();
            if (!cursor.isAfterLast()) {

                int id = cursor.getInt(0);
                String code = cursor.getString(1);
                String nom = cursor.getString(2);
                String prenoms = cursor.getString(3);
                String telephone = cursor.getString(4);
                String email = cursor.getString(5);
                String residence = cursor.getString(6);
                String cni = cursor.getString(7);
                String permis = cursor.getString(8);
                String passport = cursor.getString(9);
                String societe = cursor.getString(10);
                Integer nbrcredit = cursor.getInt(11);
                Long totalcredit = cursor.getLong(12);
                Integer nbraccount = cursor.getInt(13);
                Long totalaccount = cursor.getLong(14);

                client = new ClientModel(id, code, nom,prenoms, telephone, email, residence, cni, permis,passport,societe,nbrcredit,totalcredit,nbraccount,totalaccount);

            }
            cursor.close();

        }catch (Exception e){
            //do nothing
        }
        return client;

    }

    /**
     *
     * @return la liste de tous les accounts en cours
     */

    public ArrayList<AccountModel> listeAccounts(){
        ArrayList<AccountModel> accounts = new ArrayList<>();
        bd = accessBD.getReadableDatabase();
        try {
            String req = "select * from account where reste > 0 ";
            Cursor cursor = bd.rawQuery(req, null);
            cursor.moveToFirst();
            do {
                ClientModel client = accessLocalClient.recupUnClient(cursor.getInt(1));
                AccountModel account = getAccountModelfromCursor(cursor,client);
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
     * @return la liste de tous les accounts en cours
     */

    public ArrayList<AccountModel> listeAccountsSoldes(){
        ArrayList<AccountModel> accounts = new ArrayList<>();
        bd = accessBD.getReadableDatabase();
        try {
            String req = "select * from account where reste = 0 ";
            Cursor cursor = bd.rawQuery(req, null);
            cursor.moveToFirst();
            do {
                ClientModel client = accessLocalClient.recupUnClient(cursor.getInt(1));
                AccountModel account = getAccountModelfromCursor(cursor,client);
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
     * @return retourne la liste des accounts en cours du client
     */
    public ArrayList<AccountModel> listeAccountsClient(ClientModel client) {
        bd = accessBD.getReadableDatabase();
        ArrayList<AccountModel> account_dun_client = new ArrayList<>();
        try {
            String req = "select * from account where  reste > 0 and clientid ='" + client.getId()+"'";
            Cursor cursor = bd.rawQuery(req, null);
            cursor.moveToFirst();
            do {
                AccountModel account = getAccountModelfromCursor(cursor,client);
                account.setSoldedat(cursor.getLong(9));
                account_dun_client.add(account);
            }
            while (cursor.moveToNext());

            cursor.close();
        }catch(Exception e){
            return account_dun_client;
        }
        return account_dun_client;
    }

    /**
     *
     * @param client le client
     * @return retourne la liste des accounts soldés du client
     */

    public ArrayList<AccountModel> listeDESAccountsSoldesClient(ClientModel client) {
        bd = accessBD.getReadableDatabase();
        ArrayList<AccountModel> accounts = new ArrayList<>();
        try {
            String req = "select * from account where  reste = 0 and clientid ='" + client.getId()+"'";
            Cursor cursor = bd.rawQuery(req, null);
            cursor.moveToFirst();
            do {
                AccountModel account =getAccountModelfromCursor(cursor,client);
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


    /**
     *
     * @return retourne le total des accounts en cour
     */
    public int getRecapTaccount(){
        bd = accessBD.getReadableDatabase();
        String req  = "select SUM(sommeaccount) AS t_account from account where reste != 0";
        Cursor cursor = bd.rawQuery(req,null);
        cursor.moveToFirst();
        int totalaccount = cursor.getInt(cursor.getColumnIndexOrThrow("t_account"));
        cursor.close();
        return totalaccount;
    }

    /**
     *
     * @return retourne le total des versements des accounts en cour
     */
    public int getRecapTversement(){
        bd = accessBD.getReadableDatabase();
        String req  = "select SUM(versements) AS t_versement from account where reste != 0";
        Cursor cursor = bd.rawQuery(req,null);
        cursor.moveToFirst();
        int totalversement = cursor.getInt(cursor.getColumnIndexOrThrow("t_versement"));
        cursor.close();
        return totalversement;
    }

    /**
     *
     * @return retourne le total du reste des accounts en cour
     */
    public int getRecapTreste(){
        return this.getRecapTaccount() - this.getRecapTversement();
    }


    public boolean isClientOwnAccount(ClientModel clientModel) {

        ArrayList<AccountModel> accounts = this.listeAccountsClient(clientModel);
        return !accounts.isEmpty();

    }

    public AccountModel modifierArticledunAccount(AccountModel nouveau_accountModel, AccountModel ancien_accountModel, ClientModel client, Article nouvel_article, Article ancien_article) {
        bd = accessBD.getWritableDatabase();
        bd.beginTransaction();
        AccountModel account;
        try{
            int nouveau_total_account_du_client = (int) (( client.getTotalaccount() - ancien_accountModel.getSommeaccount()) + nouveau_accountModel.getSommeaccount());
            ArticlesModel nouvel_articlemodel = this.getArticleidAndDesignation(bd,nouvel_article.getDesignation());

            ContentValues account_cv = new ContentValues();
            ContentValues client_cv = new ContentValues();
            ContentValues infos_cv = new ContentValues();
            ContentValues ancien_article_cv = new ContentValues();
            ContentValues nouvel_article_cv = new ContentValues();

            account_cv.put(ARTICLE_1,gson.toJson(nouveau_accountModel.getArticle1()));
            account_cv.put(ARTICLE_2,gson.toJson(nouveau_accountModel.getArticle2()));
            account_cv.put(SOMMEACCOUNT,nouveau_accountModel.getSommeaccount());
            account_cv.put(RESTE,nouveau_accountModel.getReste());
            account_cv.put(SOLDEDAT, nouveau_accountModel.getDateaccount());


            client_cv.put(TOTALACCOUNT,nouveau_total_account_du_client);
            nouvel_article_cv.put(QUANTITE,(nouvel_articlemodel.getQuantite() - nouvel_article.getNbrarticle()) );

            bd.update(TABLE_ACCOUNT,account_cv, ID + "=" +nouveau_accountModel.getId(),null);
            bd.update(TABLE_CLIENT, client_cv, ID+ "= ?", new String[] {String.valueOf(client.getId())});
            bd.update(TABLE_ARTICLE,nouvel_article_cv,ID+ "= ?", new String[] {String.valueOf(nouvel_articlemodel.getId())});

            ArticlesModel ancien_articlemodel = this.getArticleidAndDesignation(bd,ancien_article.getDesignation());
            ancien_article_cv.put(QUANTITE,(ancien_articlemodel.getQuantite() + ancien_article.getNbrarticle()));
            bd.update(TABLE_ARTICLE,ancien_article_cv,ID+ "= ?", new String[] {String.valueOf(ancien_articlemodel.getId())});

            InfosModel infosModel = this.getInfo(bd);
            int nouveau_total_account_info = ( infosModel.getTotalaccount() - ancien_accountModel.getSommeaccount()) + nouveau_accountModel.getSommeaccount();
            infos_cv.put(TOTALACCOUNT,nouveau_total_account_info);
            bd.update(TABLE_INFO, infos_cv, VariablesStatique.APPNUMBER+ "= ?", new String[] {String.valueOf(infosModel.getAppnumber())});

            account = this.recupAccountById(nouveau_accountModel.getId());
            account.setSoldedat(new Date().getTime());
            bd.setTransactionSuccessful();

        }catch (Exception e){
            account = null;
        }
        finally {
            bd.endTransaction();
        }
        return account;
    }

    private ArticlesModel getArticleidAndDesignation(SQLiteDatabase bd,String designation){
        ArticlesModel articlesModel = null;
        try {
            Cursor cursor = bd.query(TABLE_ARTICLE, null,DESIGNATION + "=?",new String[]{designation},null,null,null);
            cursor.moveToLast();
            if (!cursor.isAfterLast()) {
                articlesModel = new ArticlesModel(cursor.getInt(0),cursor.getString(1), cursor.getInt(2),cursor.getInt(3), cursor.getString(4));
            }
            cursor.close();
        } catch (Exception e) {
            return articlesModel;
        }
        return articlesModel;
    }


    public InfosModel getInfo(SQLiteDatabase bd){

        ArrayList<InfosModel> _infos = new ArrayList<>();
        InfosModel info;

        try {
            String req = "select * from infos";
            Cursor cursor = bd.rawQuery(req, null);
            cursor.moveToFirst();
            do {
                InfosModel infosModel = new InfosModel(cursor.getInt(0), cursor.getInt(1), cursor.getInt(2), cursor.getInt(3) , cursor.getInt(4));
                _infos.add(infosModel);
            }
            while (cursor.moveToNext());
            cursor.close();

            info = _infos.get(0);

        }catch(Exception e){
            info = null;
        }
        return info;
    }

    public AccountModel modifierDateAccount(AccountModel account, long date) {
        bd = accessBD.getWritableDatabase();
        AccountModel accountModel = null;
        ContentValues account_cv = new ContentValues();
        account_cv.put(DATEACCOUNT,date);
        try {
            int rslt = bd.update(TABLE_ACCOUNT,account_cv, ID + "=" +account.getId(),null);
            if (rslt > 0){
                accountModel = this.recupAccountById(account.getId());
                accountModel.setSoldedat(date);
            }
        } catch (Exception e) {
            return accountModel  ;
        }
        return accountModel;

    }
}
