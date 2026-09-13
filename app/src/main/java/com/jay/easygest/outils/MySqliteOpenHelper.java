package com.jay.easygest.outils;

import static com.jay.easygest.outils.VariablesStatique.CHOISIR_UN_ARTICLE;
import static com.jay.easygest.outils.VariablesStatique.DESCRIPTION;
import static com.jay.easygest.outils.VariablesStatique.DESIGNATION;
import static com.jay.easygest.outils.VariablesStatique.PRIX;
import static com.jay.easygest.outils.VariablesStatique.QUANTITE;
import static com.jay.easygest.outils.VariablesStatique.TABLE_ACCOUNT;
import static com.jay.easygest.outils.VariablesStatique.TABLE_APPPKES;
import static com.jay.easygest.outils.VariablesStatique.TABLE_ARTICLE;
import static com.jay.easygest.outils.VariablesStatique.TABLE_CLIENT;
import static com.jay.easygest.outils.VariablesStatique.TABLE_CREDIT;
import static com.jay.easygest.outils.VariablesStatique.TABLE_IMAGE;
import static com.jay.easygest.outils.VariablesStatique.TABLE_INFO;
import static com.jay.easygest.outils.VariablesStatique.TABLE_SMSFAILLED;
import static com.jay.easygest.outils.VariablesStatique.TABLE_UTILISATEUR;
import static com.jay.easygest.outils.VariablesStatique.TABLE_VERSEMENT;
import static com.jay.easygest.outils.VariablesStatique.TABLE_VERSEMENTACC;
import static com.jay.easygest.outils.VariablesStatique.name;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jay.easygest.model.Article;

import java.lang.reflect.Type;
import java.util.ArrayList;


public class MySqliteOpenHelper extends SQLiteOpenHelper {


    public static final int version = VariablesStatique.databaseversion ;
    public static final String APPNUMBER = "appnumber";
    public static final String APPPKEY = "apppkey";
    public static final String OWNER = "owner";
    public static final String STATUS = "status";
    public static final String NBR_CREDIT = "nbrcredit";
    public static final String TOTAL_CREDIT = "totalcredit";
    public static final String NBR_ACCOUNT = "nbraccount";
    public static final String TOTAL_ACCOUNT = "totalaccount";
    public static final String TELEPHONE = "telephone";
    public static final String ADRESSEELECTRO = "adresseelectro";
    public static final String BASECODE = "basecode";
    public static final String NAME_OWNER = "solaris";
    private static MySqliteOpenHelper sInstance = null;
    private String apppnumber ;
    private final Gson gson = new Gson();



    public static synchronized MySqliteOpenHelper getInstance(@Nullable Context context,@Nullable SQLiteDatabase.CursorFactory factory) {
        if (sInstance == null) {
            // Use the application context to ensure we don't leak an Activity's context
            sInstance = new MySqliteOpenHelper(context != null ? context.getApplicationContext() : null, factory);
        }
        return sInstance;
    }

    private MySqliteOpenHelper(@Nullable Context context, @Nullable SQLiteDatabase.CursorFactory factory) {
        super(context, name, factory, version);
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        apppnumber = MesOutils.apppnumbergenerator();
        sqLiteDatabase.beginTransaction();

        try {
            String createTable_utilisateur = "create table " + TABLE_UTILISATEUR + "("
                    + "id Integer primary key autoincrement,"
                    + "username Text not null unique,"
                    + "password Text not null,"
                    + "dateInscription Long not null,"
                    + "status Integer not null,"
                    + "actif boolean not null,"
                    + "compteur Integer not null)";
            sqLiteDatabase.execSQL(createTable_utilisateur);
            String createTable_client = "create table " + TABLE_CLIENT + "("
                    + "id Integer primary key autoincrement,"
                    + "codeclient Text not null unique,"
                    + "nom Text not null,"
                    + "prenoms Text not null,"
                    + "telephone Text not null,"
                    + "adresseelectro Text,"
                    + "residence Text,"
                    + "cni Text,"
                    + "permis Text,"
                    + "passport Text,"
                    + "societe Text,"
                    + "nbrcredit Integer,"
                    + "totalcredit Integer,"
                    + "nbraccount Integer,"
                    + "totalaccount Integer)";
            sqLiteDatabase.execSQL(createTable_client);
            String createTable_account = "create table " + TABLE_ACCOUNT + "("
                    + "id Integer primary key autoincrement,"
                    + "clientid Integer not null,"
                    + "article1 Text,"
                    + "article2 Text,"
                    + "sommeaccount Integer not null,"
                    + "versements Integer not null,"
                    + "reste Integer not null,"
                    + "dateaccount Long not null ,"
                    + "numeroaccount Integer,"
                    + "soldedat Long,"
                    + "foreign key(clientid) references client(id) on delete cascade )";
            sqLiteDatabase.execSQL(createTable_account);
            String createTable_articles = "create table " + TABLE_ARTICLE + " ("
                    + "id Integer primary key,"
                    + "designation Text unique ,"
                    + "prix Integer not null,"
                    + "quantite Integer not null,"
                    + "description Text not null)";
            sqLiteDatabase.execSQL(createTable_articles);
            String createTable_credit = "create table " + TABLE_CREDIT + "("
                    + "id Integer primary key autoincrement,"
                    + "clientid Integer not null,"
                    + "article1 Text,"
                    + "article2 Text,"
                    + "sommecredit Integer not null,"
                    + "versements Integer not null,"
                    + "reste Integer not null,"
                    + "datecredit Long not null ,"
                    + "numerocredit Integer,"
                    + "soldedat Long,"
                    + "foreign key(clientid) references client(id) on delete cascade )";
            sqLiteDatabase.execSQL(createTable_credit);
            String createTable_versementacc = "create table " + TABLE_VERSEMENTACC + "("
                    + "id Integer primary key autoincrement,"
                    + "sommeverse Integer not null,"
                    + "accountid Integer not null,"
                    + "clientid Integer not null,"
                    + "dateversement Long not null,"
                    + "foreign key(accountid) references account(id) on delete cascade,"
                    + "foreign key(clientid) references client(id) on delete cascade )";
            sqLiteDatabase.execSQL(createTable_versementacc);
            String createTable_versement = "create table " + TABLE_VERSEMENT + "("
                    + "id Integer primary key autoincrement,"
                    + "sommeverse Integer not null,"
                    + "creditid Integer not null,"
                    + "clientid Integer not null,"
                    + "dateversement Long not null,"
                    + "foreign key(creditid) references credit(id) on delete cascade,"
                    + "foreign key(clientid) references client(id) on delete cascade )";
            sqLiteDatabase.execSQL(createTable_versement);
//            String createTable_apppkes = "create table " + TABLE_APPPKES + " ("
//                    + "appnumber Integer primary key,"
//                    + "apppkey Text,"
//                    + "owner Text not null,"
//                    + "basecode Text,"
//                    + "telephone Text,"
//                    + "datelicence Long not null,"
//                    + "dureelicence Long not null,"
//                    + "adresseelectro Text)";
            String createTable_apppkes = "create table " + TABLE_APPPKES + " ("
                    + "appnumber Integer primary key,"
                    + "apppkey Text,"
                    + "owner Text,"
                    + "basecode Text,"
                    + "telephone Text,"
                    + "adresseelectro Text)";
            sqLiteDatabase.execSQL(createTable_apppkes);
            String createTable_info = "create table " + TABLE_INFO + " ("
                    + "appnumber Integer primary key,"
                    + "nbrcredit Integer,"
                    + "totalcredit Integer,"
                    + "nbraccount Integer,"
                    + "totalaccount Integer)";
            sqLiteDatabase.execSQL(createTable_info);
            String createTable_smsfailled = "create table " + TABLE_SMSFAILLED + " ("
                    + "id Integer primary key,"
                    + "clientid Integer ,"
                    + "message Text not null,"
                    + "smsid Integer not null,"
                    + "foreign key(clientid) references client(id) on delete cascade)";
            sqLiteDatabase.execSQL(createTable_smsfailled);
            String createTable_image = "create table " + TABLE_IMAGE + "("
                    + "id Integer primary key,"
                    + "image Blob,"
                    + "articleid Integer not null,"
                    + "foreign key(articleid) references articles(id) on delete cascade)";
            sqLiteDatabase.execSQL(createTable_image);

//            String createTable_usedkey = "create table " + TABLE_USEDKEY + "("
//                    + "id Integer primary key,"
//                    + "cle Text not null unique)";
//            sqLiteDatabase.execSQL(createTable_usedkey);

            sqLiteDatabase.insert(TABLE_ARTICLE,null, articleVideContentValue());
            sqLiteDatabase.insert(TABLE_APPPKES,null,apppPersitence());
            sqLiteDatabase.insert(TABLE_INFO,null,creeeinfo());
//            sqLiteDatabase.insert(TABLE_USEDKEY,null,getUsedkeyCv(appkey));

            sqLiteDatabase.setTransactionSuccessful();


        }finally {
            sqLiteDatabase.endTransaction();
        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldversion, int newversion) {
        sqLiteDatabase.beginTransaction();

        try {

            if ( oldversion == 1 || oldversion == 2 || oldversion == 3 || oldversion == 4 ){
                version3To4(sqLiteDatabase);
                version4To5(sqLiteDatabase);
            }

            sqLiteDatabase.setTransactionSuccessful();

        }finally {
            sqLiteDatabase.endTransaction();

        }

    }


    public void version3To4(SQLiteDatabase sqLiteDatabase){
        //renommage de la table articles
        sqLiteDatabase.execSQL("ALTER TABLE articles RENAME TO articlesold");
        //creation d'une nouvelle table articles
        String createTable_articles = "create table " + TABLE_ARTICLE + " ("
                + "id Integer primary key,"
                + "designation Text unique,"
                + "prix Integer not null,"
                + "quantite Integer not null,"
                + "description Text not null)";

        sqLiteDatabase.execSQL(createTable_articles);
        ArrayList<String> designation_liste = new ArrayList<>();
        sqLiteDatabase.insert(TABLE_ARTICLE,null, articleVideContentValue());
        designation_liste.add(CHOISIR_UN_ARTICLE);
        sqLiteDatabase.delete(TABLE_UTILISATEUR,STATUS +"!=?",new String[]{String.valueOf(1)});
        //recuperer les donnees de la table articlesold et les inserer dans la nouvelle table
        Cursor cursor = sqLiteDatabase.query("articlesold",null,null,null,"designation",null,null);
        if (cursor.moveToFirst()){
            do {

                String designation = cursor.getString(cursor.getColumnIndexOrThrow("designation"));
                String description =  cursor.getString(cursor.getColumnIndexOrThrow("description"));
                int prix = cursor.getInt(cursor.getColumnIndexOrThrow("prix"));
                int quantite = cursor.getInt(cursor.getColumnIndexOrThrow("quantite"));
                if (!description.equals(CHOISIR_UN_ARTICLE)){
                    ContentValues cv = new ContentValues();
                    cv.put("designation",designation);
                    cv.put("description",description);
                    cv.put("prix",prix);
                    cv.put("quantite",quantite);
                    sqLiteDatabase.insert(TABLE_ARTICLE,null,cv);
                }

            }while (cursor.moveToNext());
            cursor.close();
            sqLiteDatabase.execSQL("drop table if exists articlesold");

        }else {
            // inserrer des articles à partir de la designation des credits et des accounts
            Cursor creditsCuresor = sqLiteDatabase.query(TABLE_CREDIT,null,null,null,null,null,null);
            Cursor accountsCuresor = sqLiteDatabase.query(TABLE_ACCOUNT,null,null,null,null,null,null);
            Type articletype = new TypeToken<Article>(){}.getType() ;
            if (creditsCuresor.moveToFirst()){

                do {
                    String article1 = creditsCuresor.getString(creditsCuresor.getColumnIndexOrThrow("article1"));
                    String article2 = creditsCuresor.getString(creditsCuresor.getColumnIndexOrThrow("article2"));

                    Article article_1 = gson.fromJson(article1,articletype);
                    Article article_2 = gson.fromJson(article2,articletype);
                    Log.d("TAG", "version3To4: designation liste 1" + designation_liste);
                    if (!designation_liste.contains(article_1.getDesignation())){
                        designation_liste.add(article_1.getDesignation());
                        Log.d("TAG", "version3To4: designation liste 2" + designation_liste);
                        ContentValues cv1 = new ContentValues();
                        cv1.put("designation",article_1.getDesignation());
                        cv1.put("prix",article_1.getPrix());
                        cv1.put("quantite",article_1.getNbrarticle());
                        cv1.put("description",article_1.getDesignation());
                        sqLiteDatabase.insert(TABLE_ARTICLE,null,cv1);
                    }
                    Log.d("TAG", "version3To4: designation liste 3" + designation_liste);
                    if (!designation_liste.contains(article_2.getDesignation())){
                        designation_liste.add(article_2.getDesignation());
                        Log.d("TAG", "version3To4: designation liste 4" + designation_liste);
                        ContentValues cv2 = new ContentValues();
                        cv2.put("designation",article_2.getDesignation());
                        cv2.put("prix",article_2.getPrix());
                        cv2.put("quantite",article_2.getNbrarticle());
                          cv2.put("description",article_2.getDesignation());
                        sqLiteDatabase.insert(TABLE_ARTICLE,null,cv2);
                    }

                }while (creditsCuresor.moveToNext());

                creditsCuresor.close();

            }
            Log.d("TAG", "version3To4: designation liste 5" + designation_liste);
            if (accountsCuresor.moveToFirst()){
                do {
                    Log.d("TAG", "version3To4: designation liste 52" + designation_liste);
                    String article1 = accountsCuresor.getString(accountsCuresor.getColumnIndexOrThrow("article1"));
                    String article2 = accountsCuresor.getString(accountsCuresor.getColumnIndexOrThrow("article2"));

                    Article article_1 = gson.fromJson(article1,articletype);
                    Article article_2 = gson.fromJson(article2,articletype);
                    if (!designation_liste.contains(article_1.getDesignation())){
                        designation_liste.add(article_1.getDesignation());
                        Log.d("TAG", "version3To4: designation liste 7" + designation_liste);
                        ContentValues cv3 = new ContentValues();
                        cv3.put("designation",article_1.getDesignation());
                        cv3.put("prix",article_1.getPrix());
                        cv3.put("quantite",article_1.getNbrarticle());
                        cv3.put("description",article_1.getDesignation());
                        sqLiteDatabase.insert(TABLE_ARTICLE,null,cv3);
                    }
                    Log.d("TAG", "version3To4: designation liste 8" + designation_liste);
                    if (!designation_liste.contains(article_2.getDesignation())){
                        designation_liste.add(article_2.getDesignation());
                        Log.d("TAG", "version3To4: designation liste 9" + designation_liste);
                        ContentValues cv4 = new ContentValues();
                        cv4.put("designation",article_2.getDesignation());
                        cv4.put("prix",article_2.getPrix());
                        cv4.put("quantite",article_2.getNbrarticle());
                        cv4.put("description",article_2.getDesignation());
                        sqLiteDatabase.insert(TABLE_ARTICLE,null,cv4);
                    }

                }while (accountsCuresor.moveToNext());
                accountsCuresor.close();
            }
        }
    }

    /**
     * mise a jour de la version de la base de donnees
     * suppression des colonnes datelicence et dureelicence
     * @param sqLiteDatabase objet de manupulation de la base de donnees
     */


    public void version4To5(SQLiteDatabase sqLiteDatabase){
        // Try to drop columns if supported (API 31+), otherwise we might have to ignore or use a more complex migration.
        // For simplicity and compatibility, we check if they exist or just try-catch.

        //renommage de la table articles
        sqLiteDatabase.execSQL("ALTER TABLE APPPKES RENAME TO APPPKESold");
//        //creation d'une nouvelle table articles
        String createTable_apppkes = "create table " + TABLE_APPPKES + " ("
                + "appnumber Integer primary key,"
                + "apppkey Text,"
                + "owner Text,"
                + "basecode Text,"
                + "telephone Text,"
                + "adresseelectro Text)";

        sqLiteDatabase.execSQL(createTable_apppkes);
        //recuperer les donnees de la table APPPKESold et les inserer dans la nouvelle table
        Cursor cursor = sqLiteDatabase.query("APPPKESold",null,null,null,null,null,null);
        if (cursor.moveToFirst()){
            do {
                int appnumber = cursor.getInt(cursor.getColumnIndexOrThrow("appnumber"));
                String apppkey = cursor.getString(cursor.getColumnIndexOrThrow("apppkey"));
                String owner = cursor.getString(cursor.getColumnIndexOrThrow("owner"));
                String basecode = cursor.getString(cursor.getColumnIndexOrThrow("basecode"));
                String telephone = cursor.getString(cursor.getColumnIndexOrThrow("telephone"));
                String adresseelectro = cursor.getString(cursor.getColumnIndexOrThrow("adresseelectro"));
                ContentValues cv = new ContentValues();
                cv.put("appnumber",appnumber);
                cv.put("apppkey",apppkey);
                cv.put("owner",owner);
                cv.put("basecode",basecode);
                cv.put("telephone",telephone);
                cv.put("adresseelectro",adresseelectro);

                sqLiteDatabase.insert(TABLE_APPPKES,null,cv);
            }while (cursor.moveToNext());
            cursor.close();
            sqLiteDatabase.execSQL("drop table APPPKESold");
            sqLiteDatabase.execSQL("drop table if exists usedkey");
        }

        Cursor cursor_de_credits = sqLiteDatabase.query(TABLE_CREDIT,null,null,null,null,null,null);
        ArrayList<ContentValues> credits_cv = new ArrayList<>();
        if (cursor_de_credits.moveToFirst()){
            do {
                int creditid = cursor_de_credits.getInt(cursor_de_credits.getColumnIndexOrThrow("id"));
                String article1 = cursor_de_credits.getString(cursor_de_credits.getColumnIndexOrThrow("article1"));
                String article2 = cursor_de_credits.getString(cursor_de_credits.getColumnIndexOrThrow("article2"));
                Article article_12;
                Article article_22;
                ContentValues credit_cv = new ContentValues();

                Type articletype = new TypeToken<Article>(){}.getType();
                Article article_1 = gson.fromJson(article1,articletype);
                if (article_1 != null) {
                    Cursor articleModel1Cursor = sqLiteDatabase.query(TABLE_ARTICLE, null, "designation =?", new String[]{article_1.getDesignation()}, null, null, null);
                    if (articleModel1Cursor.moveToFirst()) {
                        article_12 = new Article(String.valueOf(articleModel1Cursor.getInt(articleModel1Cursor.getColumnIndexOrThrow("id"))), article_1.getPrix(), article_1.getNbrarticle());
                        credit_cv.put("article1", gson.toJson(article_12));
                    }
                    articleModel1Cursor.close();
                }

                Article article_2 = gson.fromJson(article2,articletype);
                if (article_2 != null) {
                    Cursor articleModel2Cursor = sqLiteDatabase.query(TABLE_ARTICLE, null, "designation =?", new String[]{article_2.getDesignation()}, null, null, null);
                    if (articleModel2Cursor.moveToFirst()) {
                        article_22 = new Article(String.valueOf(articleModel2Cursor.getInt(articleModel2Cursor.getColumnIndexOrThrow("id"))), article_2.getPrix(), article_2.getNbrarticle());
                        credit_cv.put("article2", gson.toJson(article_22));
                    }
                    articleModel2Cursor.close();
                }
                credit_cv.put("id",creditid);
                credits_cv.add(credit_cv);

            }while (cursor_de_credits.moveToNext());
            cursor_de_credits.close();
            for (ContentValues cv : credits_cv){
                sqLiteDatabase.update(TABLE_CREDIT,cv,"id =?",new String[]{String.valueOf(cv.get("id"))});
            }
        }

        Cursor cursor_de_accounts = sqLiteDatabase.query(TABLE_ACCOUNT,null,null,null,null,null,null);
        ArrayList<ContentValues> accounts_cv = new ArrayList<>();
        if (cursor_de_accounts.moveToFirst()){
            do {
                int accountid = cursor_de_accounts.getInt(cursor_de_accounts.getColumnIndexOrThrow("id"));
                String article1 = cursor_de_accounts.getString(cursor_de_accounts.getColumnIndexOrThrow("article1"));
                String article2 = cursor_de_accounts.getString(cursor_de_accounts.getColumnIndexOrThrow("article2"));

                Article article_22;
                Article article_12;
                ContentValues account_cv = new ContentValues();

                Type articletype = new TypeToken<Article>(){}.getType() ;
                Article article_1 = gson.fromJson(article1,articletype);
                if (article_1 != null) {
                    Cursor articleModel1Cursor = sqLiteDatabase.query(TABLE_ARTICLE, null, "designation =?", new String[]{article_1.getDesignation()}, null, null, null);
                    if (articleModel1Cursor.moveToFirst()) {
                        article_12 = new Article(String.valueOf(articleModel1Cursor.getInt(articleModel1Cursor.getColumnIndexOrThrow("id"))), article_1.getPrix(), article_1.getNbrarticle());
                        account_cv.put("article1", gson.toJson(article_12));
                    }
                    articleModel1Cursor.close();
                }

                Article article_2 = gson.fromJson(article2,articletype);
                if (article_2 != null) {
                    Cursor articleModel2Cursor = sqLiteDatabase.query(TABLE_ARTICLE, null, "designation =?", new String[]{article_2.getDesignation()}, null, null, null);
                    if (articleModel2Cursor.moveToFirst()) {
                        article_22 = new Article(String.valueOf(articleModel2Cursor.getInt(articleModel2Cursor.getColumnIndexOrThrow("id"))), article_2.getPrix(), article_2.getNbrarticle());
                        account_cv.put("article2", gson.toJson(article_22));
                    }
                    articleModel2Cursor.close();
                }
                account_cv.put("id",accountid);
                accounts_cv.add(account_cv);

            }while (cursor_de_accounts.moveToNext());
            cursor_de_accounts.close();

            for (ContentValues cv : accounts_cv){
                sqLiteDatabase.update(TABLE_ACCOUNT,cv,"id =?",new String[]{String.valueOf(cv.get("id"))});
            }
        }


    }

    public ContentValues apppPersitence(){
        ContentValues cv = new ContentValues();
        cv.put(APPNUMBER,apppnumber);
        cv.put(APPPKEY,"");
        cv.put(OWNER, NAME_OWNER);
        cv.put(TELEPHONE,"");
        cv.put(ADRESSEELECTRO,"");
        cv.put(BASECODE,"clt");

        return cv;
    }

    public ContentValues articleVideContentValue(){
        ContentValues article_cv = new ContentValues();
        article_cv.put(DESIGNATION,CHOISIR_UN_ARTICLE);
        article_cv.put(PRIX,0);
        article_cv.put(QUANTITE, 100);
        article_cv.put(DESCRIPTION,CHOISIR_UN_ARTICLE);
        return article_cv;
    }


    public ContentValues creeeinfo(){
        ContentValues cv = new ContentValues();
        cv.put(APPNUMBER,apppnumber);
        cv.put(NBR_CREDIT,0);
        cv.put(TOTAL_CREDIT,0);
        cv.put(NBR_ACCOUNT,0);
        cv.put(TOTAL_ACCOUNT,0);
        return cv;
    }





}
