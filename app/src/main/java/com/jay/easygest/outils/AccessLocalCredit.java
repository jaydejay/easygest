package com.jay.easygest.outils;

import static com.jay.easygest.outils.VariablesStatique.ARTICLE_1;
import static com.jay.easygest.outils.VariablesStatique.ARTICLE_2;
import static com.jay.easygest.outils.VariablesStatique.DATECREDIT;
import static com.jay.easygest.outils.VariablesStatique.DESIGNATION;
import static com.jay.easygest.outils.VariablesStatique.ID;
import static com.jay.easygest.outils.VariablesStatique.NBRCREDIT;
import static com.jay.easygest.outils.VariablesStatique.QUANTITE;
import static com.jay.easygest.outils.VariablesStatique.RESTE;
import static com.jay.easygest.outils.VariablesStatique.SOLDEDAT;
import static com.jay.easygest.outils.VariablesStatique.SOMMECREDIT;
import static com.jay.easygest.outils.VariablesStatique.TABLE_ARTICLE;
import static com.jay.easygest.outils.VariablesStatique.TABLE_CLIENT;
import static com.jay.easygest.outils.VariablesStatique.TABLE_CREDIT;
import static com.jay.easygest.outils.VariablesStatique.TABLE_INFO;
import static com.jay.easygest.outils.VariablesStatique.TABLE_VERSEMENT;
import static com.jay.easygest.outils.VariablesStatique.TOTALCREDIT;
import static com.jay.easygest.outils.VariablesStatique.VERSEMENTS;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jay.easygest.model.Article;
import com.jay.easygest.model.ArticlesModel;
import com.jay.easygest.model.ClientModel;
import com.jay.easygest.model.CreditModel;
import com.jay.easygest.model.InfosModel;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Map;


public class AccessLocalCredit {

    private final MySqliteOpenHelper accessBD;
    Gson gson = new Gson();
    private SQLiteDatabase bd;
    private AccessLocalClient accessLocalClient;
    private AccessLocalVersement accessLocalVersement;
    private final Context contexte;
    private final Type articletype = new TypeToken<Article>(){}.getType();

    public AccessLocalCredit(Context contexte) {
        this.contexte = contexte;
        this.accessBD = MySqliteOpenHelper.getInstance(contexte, null);
        accessLocalClient = new AccessLocalClient(contexte);
    }

    public ContentValues creerCreditContentValue(CreditModel credit, long client_id) {

        ContentValues cv = new ContentValues();
        long date_de_solde = credit.getReste() == 0 ? credit.getDatecredit() : 0L ;
        credit.setSoldedat(date_de_solde);

        cv.put(VariablesStatique.CLIENTID,client_id);
        cv.put(ARTICLE_1,gson.toJson(credit.getArticle1()));
        cv.put(ARTICLE_2,gson.toJson(credit.getArticle2()));
        cv.put(SOMMECREDIT,credit.getSommecredit());
        cv.put(VERSEMENTS,credit.getVersement());
        cv.put(RESTE,credit.getReste());
        cv.put(DATECREDIT,credit.getDatecredit());
        cv.put(VariablesStatique.NUMEROCREDIT,credit.getNumerocredit());
        cv.put(SOLDEDAT,date_de_solde);
       return cv;
    }

    public CreditModel creerCompteCredit(CreditModel premiercredit, Map<String, Object> data){
        bd = accessBD.getWritableDatabase();
        accessLocalVersement = new AccessLocalVersement(contexte);
        accessLocalClient = new AccessLocalClient(contexte);
        bd.beginTransaction();
        CreditModel creditModel;

        ContentValues article1_cv = new ContentValues();
        ArticlesModel article1 = (ArticlesModel) data.get("article1");
        Article article1vendu = (Article) data.get("article1vendu");
        assert article1 != null;
        assert article1vendu != null;
        int nbr_articles1_restant = article1.getQuantite() - article1vendu.getNbrarticle();
        article1_cv.put(QUANTITE,nbr_articles1_restant);

        ContentValues article2_cv = new ContentValues();
        ArticlesModel article2 = (ArticlesModel) data.get("article2");
        Article article2vendu = (Article) data.get("article2vendu");
        int nbr_articles2_restant = ((article2 != null) ? article2.getQuantite() : 0) - ((article2vendu != null) ? article2vendu.getNbrarticle() : 0);
        article2_cv.put(QUANTITE,nbr_articles2_restant);

        try {

            long client_reslt = bd.insertWithOnConflict(TABLE_CLIENT,null,accessLocalClient.ajoutClientContentValue((String) data.get("codeclient"), (String) data.get("nomclient"), (String) data.get("prenomclient"), (String) data.get("telephone"),1,premiercredit.getSommecredit(),0,0),1);
            long credit_rslt = bd.insertWithOnConflict(TABLE_CREDIT,null,this.creerCreditContentValue(premiercredit,client_reslt),1);
            if (Integer.parseInt((String) data.get("versement")) > 0){
                bd.insertWithOnConflict(TABLE_VERSEMENT,null,accessLocalVersement.creerVersement(Integer.parseInt((String) data.get("versement")), (int) credit_rslt,(int) client_reslt,premiercredit.getDatecredit()),1);
            }

            bd.updateWithOnConflict(TABLE_ARTICLE,article1_cv,"designation =?", new String[] {article1.getDesignation()},1);
            if (article2 != null && !article2.getDescription().equals("Choisir un article")) {
                bd.updateWithOnConflict(TABLE_ARTICLE, article2_cv, "designation =?", new String[]{article2.getDesignation()},1);
            }
            creditModel = this.recupCreditById((int) credit_rslt);

            InfosModel info = this.getInfo(bd);
            ContentValues infos_cv = new ContentValues();
            infos_cv.put(NBRCREDIT,info.getNbrcredit()+1);
            infos_cv.put(TOTALCREDIT,info.getTotalcredit()+creditModel.getSommecredit());
            bd.updateWithOnConflict(TABLE_INFO,infos_cv,"appnumber = ?", new String[] {String.valueOf(info.getAppnumber())},1);

            bd.setTransactionSuccessful();

        }catch (Exception e){
            creditModel = null;
        }finally {
            if (bd.inTransaction()){
                bd.endTransaction();
            }
        }
        return creditModel;

    }

    /**
     * @param credit  le credit a ajouter
     * @param client  le client proprietaire du credit
     * @param newdata  les données de l'article1 et l'article2
     * @return boolean
     */
    public CreditModel ajouterCredit(CreditModel credit, ClientModel client, Map<String, Object> newdata){
        bd = accessBD.getWritableDatabase();
        accessLocalVersement = new AccessLocalVersement(contexte);
        ContentValues client_cv = getClientCv(credit, client);

        ContentValues article1_cv = new ContentValues();
        ArticlesModel article1 = (ArticlesModel) newdata.get("article1");
        int nbrarticle1restant = (int) newdata.get("nbrarticle1restant");
        article1_cv.put(QUANTITE,nbrarticle1restant);

        ContentValues article2_cv = new ContentValues();
        ArticlesModel article2 = (ArticlesModel) newdata.get("article2");
        int nbrarticle2restant = (int) newdata.get("nbrarticle2restant");
        article2_cv.put(QUANTITE,nbrarticle2restant);

        bd.beginTransaction();
        CreditModel creditModel;

        try {

            long credit_rslt =  bd.insertWithOnConflict(TABLE_CREDIT,null,this.creerCreditContentValue(credit,client.getId()),1);
            bd.updateWithOnConflict(TABLE_CLIENT,client_cv, ID + "= ?" ,new String[] {String.valueOf(client.getId())},1);
            if (credit.getVersement() != 0){bd.insertWithOnConflict(TABLE_VERSEMENT,null,accessLocalVersement.creerVersement(credit.getVersement(), (int) credit_rslt, client.getId(),credit.getDatecredit()),1);}

            bd.updateWithOnConflict(TABLE_ARTICLE,article1_cv,"designation =?", new String[] {article1.getDesignation()},1);
            if (article2 != null && !article2.getDescription().equals("Choisir un article") ){
                bd.updateWithOnConflict(TABLE_ARTICLE,article2_cv,"designation =?", new String[] {article2.getDesignation()},1);
            }

            creditModel = this.recupCreditById((int) credit_rslt);
            InfosModel info = this.getInfo(bd);
            ContentValues infos_cv = new ContentValues();
            infos_cv.put(NBRCREDIT,info.getNbrcredit()+1);
            infos_cv.put(TOTALCREDIT,info.getTotalcredit()+creditModel.getSommecredit());
            bd.updateWithOnConflict(TABLE_INFO,infos_cv,"appnumber = ?", new String[] {String.valueOf(info.getAppnumber())},1);

            bd.setTransactionSuccessful();

        }catch (Exception e){
             creditModel = null;
        }finally {
            if (bd.inTransaction()){
                bd.endTransaction();
            }
        }
        return creditModel;
    }

    @NonNull
    private ContentValues getClientCv(CreditModel credit, ClientModel client) {
        ContentValues client_cv= new ContentValues();
        client_cv.put(NBRCREDIT, client.getNbrcredit() + 1);
        client_cv.put(TOTALCREDIT, client.getTotalcredit() + credit.getSommecredit());
        return client_cv;
    }


    public CreditModel modifierCredit(CreditModel nouveau_creditModel,ClientModel client,int ancienne_sommecredit) {

        bd = accessBD.getWritableDatabase();
        bd.beginTransaction();
        CreditModel credit;
        try{
            int ancien_total_credit_du_client =  Integer.parseInt(String.valueOf(client.getTotalcredit())) ;
            int ancienne_somme_credit = Integer.parseInt(String.valueOf(ancienne_sommecredit)) ;
            int nouveau_total_credit_du_client = ( ancien_total_credit_du_client - ancienne_somme_credit) + nouveau_creditModel.getSommecredit();

            ContentValues credit_cv = new ContentValues();
            ContentValues client_cv = new ContentValues();

            credit_cv.put(ARTICLE_1,gson.toJson(nouveau_creditModel.getArticle1()));
            credit_cv.put(ARTICLE_2,gson.toJson(nouveau_creditModel.getArticle2()));
            credit_cv.put(SOMMECREDIT,nouveau_creditModel.getSommecredit());
            credit_cv.put(VERSEMENTS,nouveau_creditModel.getVersement());
            credit_cv.put(RESTE,nouveau_creditModel.getReste());
            credit_cv.put(DATECREDIT,nouveau_creditModel.getDatecredit());
            credit_cv.put(SOLDEDAT,nouveau_creditModel.getSoldedat());

            client_cv.put(TOTALCREDIT,nouveau_total_credit_du_client);

            bd.updateWithOnConflict(TABLE_CREDIT,credit_cv, ID + "=" +nouveau_creditModel.getId(),null,1);
            bd.updateWithOnConflict(TABLE_CLIENT, client_cv, ID+ "= ?", new String[] {String.valueOf(client.getId())},1);
            //il faut mettre a jour la table infos aussi
            credit = this.recupCreditById(nouveau_creditModel.getId());
            bd.setTransactionSuccessful();

        }catch (Exception e){
            credit = null;
        }
        return credit;
    }

    /**
     * annule un credit qui n'est pas soldé
     * @param credit le credit à annuler
     * @return vrai si l'annulation est faite sinon faux
     */
    public boolean anullerCredit(CreditModel credit){
        boolean success = false;
        bd = accessBD.getWritableDatabase();
        bd.setForeignKeyConstraintsEnabled(true);
        bd.beginTransaction();
        try {
            Article article1 = credit.getArticle1();
            ContentValues article1_cv = new ContentValues();
            ArticlesModel articlesModel1 = this.getArticleidAndDesignation(bd,article1.getDesignation());
            article1_cv.put(QUANTITE,articlesModel1.getQuantite() + article1.getNbrarticle() );

            Article article2 = credit.getArticle2();
            ContentValues article2_cv = new ContentValues();
            ArticlesModel articlesModel2 = this.getArticleidAndDesignation(bd,article2.getDesignation());
            article2_cv.put(QUANTITE,articlesModel2.getQuantite() + article2.getNbrarticle() );

            ContentValues cvclient = new ContentValues();
            cvclient.put(NBRCREDIT,credit.getClient().getNbrcredit() - 1);
            cvclient.put(TOTALCREDIT,credit.getClient().getTotalcredit() - credit.getSommecredit());

           int rslt  = bd.delete(TABLE_CREDIT,ID +"=?",new String[]{String.valueOf(credit.getId())});
            if (rslt > 0){
                //            bd.delete(TABLE_VERSEMENT,CREDITID +"=?",new String[]{String.valueOf(credit.getId())});
                bd.updateWithOnConflict(TABLE_CLIENT,cvclient, ID + "=?" ,new String[]{String.valueOf(credit.getClient().getId())},1);
                bd.updateWithOnConflict(TABLE_ARTICLE,article1_cv, DESIGNATION + "=?" ,new String[]{article1.getDesignation()},1);
                if (!article2.getDesignation().equals("Choisir un article")){
                    bd.updateWithOnConflict(TABLE_ARTICLE,article2_cv, DESIGNATION + "=?",new String[]{article2.getDesignation()},1);
                }
                InfosModel info = this.getInfo(bd);
                ContentValues infos_cv = new ContentValues();
                infos_cv.put(NBRCREDIT,info.getNbrcredit()-1);
                infos_cv.put(TOTALCREDIT,info.getTotalcredit()-credit.getSommecredit());
                 bd.updateWithOnConflict(TABLE_INFO,infos_cv,"appnumber = ?", new String[] {String.valueOf(info.getAppnumber())},1);
                bd.setTransactionSuccessful();
                success = true;
            }else {
                bd.endTransaction();
            }

        }catch (Exception e){
            Toast.makeText(contexte, "un probleme est survenu lors de l'annulation du credit", Toast.LENGTH_SHORT).show();
        }finally {
            if (bd.inTransaction()){
                bd.endTransaction();
            }
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
            String req = "select * from credit where reste > 0 ";
            Cursor cursor = bd.rawQuery(req, null);
            cursor.moveToFirst();
            do {
                ClientModel client = accessLocalClient.recupUnClient(cursor.getInt(1));
                CreditModel credit = getCreditModelfromCursor(cursor,client);
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
            String req = "select * from credit where reste > 0 and clientid =" + client.getId();
            Cursor cursor = bd.rawQuery(req, null);
            cursor.moveToFirst();
            do {
                CreditModel credit = getCreditModelfromCursor(cursor, client);
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
                CreditModel credit = getCreditModelfromCursor(cursor,client);
                credit.setSoldedat(cursor.getLong(9));
                credits.add(credit);
            }
            while (cursor.moveToNext());
            cursor.close();
//            bd.close();
        }catch(Exception e){
            return credits;
        }
        return credits;
    }


    /**
     *
     * @param client le client
     * @return vrai si le client possede un credit sinon faux
     */

    public boolean isClientOwnCredit(ClientModel client){
        ArrayList<CreditModel> credits = this.listeCreditsclient(client);
        return !credits.isEmpty();
    }

    /**
     *recupere un credit avec le client associé
     * @param creditId l'identifiant unique du credit
     * @return retourne le credit associer au client
     */
    public CreditModel recupCreditaveccclientById(Integer creditId){
        CreditModel credit = null;
        try {
            bd = accessBD.getReadableDatabase();
            String req = "select * from credit where " + ID + "="+creditId;
            Cursor cursor = bd.rawQuery(req, null);
            cursor.moveToLast();
            if (!cursor.isAfterLast()) {
                int clientid = cursor.getInt(1);
                ClientModel client = recupUnClient(bd,clientid);
                credit = getCreditModelfromCursor(cursor,client);
                credit.setSoldedat(cursor.getLong(9));
            }
            cursor.close();
        }catch (Exception e){
            //do nothing
            return credit;
        }
        return credit;

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
     * @param creditId l'identifiant unique du credit
     * @return retourne le credit
     */

    public CreditModel recupCreditById(Integer creditId){
        CreditModel credit = null;
        try {
            bd = accessBD.getReadableDatabase();
            String req = "select * from credit where " + ID + "="+creditId;
            Cursor cursor = bd.rawQuery(req, null);
            cursor.moveToLast();
            if (!cursor.isAfterLast()) {
                ClientModel client = this.recupUnClient(bd,cursor.getInt(1)) ;
                credit = getCreditModelfromCursor(cursor,client);
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
//        bd.close();
        return totalversement;
    }

    /**
     *
     * @param client le client
     * @return total du reste des credits a payer d'un client
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


    /**
     * permet de modifier un article d'un credit
     * @param nouveau_creditModel           le nouveau credit à enregistrer
     * @param client                        le client a qui appartient le credit
     * @return le nouveau credit modifier
     */
    public CreditModel modifierArticledunCredit(CreditModel nouveau_creditModel,CreditModel ancien_creditModel, ClientModel client, Article nouvel_article,Article ancien_article) {

        bd = accessBD.getWritableDatabase();
        bd.beginTransaction();
        CreditModel credit;
        try{
            int nouveau_total_credit_du_client = (int) (( client.getTotalcredit() - ancien_creditModel.getSommecredit()) + nouveau_creditModel.getSommecredit());
            ArticlesModel nouvel_articlemodel = this.getArticleidAndDesignation(bd,nouvel_article.getDesignation());

            ContentValues credit_cv = new ContentValues();
            ContentValues client_cv = new ContentValues();
            ContentValues infos_cv = new ContentValues();
            ContentValues ancien_article_cv = new ContentValues();
            ContentValues nouvel_article_cv = new ContentValues();



            credit_cv.put(ARTICLE_1,gson.toJson(nouveau_creditModel.getArticle1()));
            credit_cv.put(ARTICLE_2,gson.toJson(nouveau_creditModel.getArticle2()));
            credit_cv.put(SOMMECREDIT,nouveau_creditModel.getSommecredit());
            credit_cv.put(RESTE,nouveau_creditModel.getReste());

            client_cv.put(TOTALCREDIT,nouveau_total_credit_du_client);
            nouvel_article_cv.put(QUANTITE,(nouvel_articlemodel.getQuantite() - nouvel_article.getNbrarticle()));

            bd.update(TABLE_CREDIT,credit_cv, ID + "=" +nouveau_creditModel.getId(),null);
            bd.update(TABLE_CLIENT, client_cv, ID + "= ?", new String[] {String.valueOf(client.getId())});
            bd.update(TABLE_ARTICLE,nouvel_article_cv,ID+ "= ?", new String[] {String.valueOf(nouvel_articlemodel.getId())});

            ArticlesModel ancien_articlemodel = this.getArticleidAndDesignation(bd,ancien_article.getDesignation());
            ancien_article_cv.put(QUANTITE,(ancien_articlemodel.getQuantite() + ancien_article.getNbrarticle()));
            bd.update(TABLE_ARTICLE,ancien_article_cv,ID+ "= ?", new String[] {String.valueOf(ancien_articlemodel.getId())});

            InfosModel infosModel = this.getInfo(bd);
            int nouveau_total_credit_info = ( infosModel.getTotalcredit() - ancien_creditModel.getSommecredit()) + nouveau_creditModel.getSommecredit();
            infos_cv.put(TOTALCREDIT,nouveau_total_credit_info);
            bd.update(TABLE_INFO, infos_cv, VariablesStatique.APPNUMBER+ "= ?", new String[] {String.valueOf(infosModel.getAppnumber())});

            credit = this.recupCreditaveccclientById(nouveau_creditModel.getId());
            bd.setTransactionSuccessful();

        }catch (Exception e){
            credit = null;
        }
        finally {
            bd.endTransaction();
        }
        return credit;

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

    public ArticlesModel getArticleidAndDesignation(SQLiteDatabase bd,String designation){
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

    public CreditModel modifierDateCredit(CreditModel credit, long date) {
        bd = accessBD.getWritableDatabase();
        CreditModel creditModel = null;
        ContentValues credit_cv = new ContentValues();
        credit_cv.put(DATECREDIT,date);
        try {
           int rslt = bd.update(TABLE_CREDIT,credit_cv, ID + "=" +credit.getId(),null);
            if (rslt > 0){
                creditModel = this.recupCreditaveccclientById(credit.getId());
            }
        } catch (Exception e) {
           return null;
        }
        return creditModel;
    }


    @NonNull
    private CreditModel getCreditModelfromCursor(Cursor credtModelcursor,ClientModel client) {
        int creditId = credtModelcursor.getInt(0);
        Article article1 = gson.fromJson(credtModelcursor.getString(2),articletype);
        Article article2 = gson.fromJson(credtModelcursor.getString(3),articletype);
        int versement = credtModelcursor.getInt(5);
        long datecredit = credtModelcursor.getLong(7);
        int nbrcredit = credtModelcursor.getInt(8);

        return new CreditModel(creditId, client, article1, article2, versement, datecredit, nbrcredit);
    }
}
