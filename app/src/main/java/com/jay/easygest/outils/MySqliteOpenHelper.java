package com.jay.easygest.outils;

import static com.jay.easygest.outils.VariablesStatique.TABLE_ACCOUNT;
import static com.jay.easygest.outils.VariablesStatique.TABLE_APPPKES;
import static com.jay.easygest.outils.VariablesStatique.TABLE_ARTICLES;
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

import androidx.annotation.Nullable;

import java.sql.Timestamp;
import java.util.Date;


public class MySqliteOpenHelper extends SQLiteOpenHelper {


    public static final int version = 2;
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
    public static final String DATELICENCE = "datelicence";
    public static final String DUREELICENCE = "dureelicence";

    private  String appkey ;
    private String apppnumber ;


    public MySqliteOpenHelper(@Nullable Context context, @Nullable SQLiteDatabase.CursorFactory factory) {
        super(context, name, factory, version);
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        apppnumber = MesOutils.apppnumbergenerator();
        appkey = MesOutils.apppkeygenerator(apppnumber);
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
            String createTable_articles = "create table " + TABLE_ARTICLES + " ("
                    + "id Integer primary key,"
                    + "designation Text not null,"
                    + "prix Integer not null,"
                    + "quantite Integer not null,"
                    + "description Text)";
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
            String createTable_apppkes = "create table " + TABLE_APPPKES + " ("
                    + "appnumber Integer primary key,"
                    + "apppkey Text,"
                    + "owner Text not null,"
                    + "basecode Text,"
                    + "telephone Text,"
                    + "datelicence Long not null,"
                    + "dureelicence Long not null,"
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
            String createTable_image = "create table " + TABLE_IMAGE + " ("
                    + "id Integer primary key,"
                    + "image Blob,"
                    + "articleid Integer not null,"
                    + "foreign key(articleid) references articles(id) on delete cascade)";
            sqLiteDatabase.execSQL(createTable_image);

            sqLiteDatabase.insert(TABLE_APPPKES,null,apppPersitence());
            sqLiteDatabase.insert(TABLE_INFO,null,creeeinfo());

            sqLiteDatabase.setTransactionSuccessful();


        }finally {
            sqLiteDatabase.endTransaction();
        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldversion, int newversion) {
        sqLiteDatabase.beginTransaction();

        try {

            sqLiteDatabase.execSQL("alter table APPPKES add datelicence Long not null default 1000");
            sqLiteDatabase.execSQL("alter table APPPKES add dureelicence Long not null default 1000");

            Cursor cursor = sqLiteDatabase.query(TABLE_APPPKES,null,null,null,null,null,null);
            if (cursor.moveToFirst()){
                String app_number = String.valueOf(cursor.getInt(0)) ;
                appkey = MesOutils.apppkeygenerator(app_number);
                sqLiteDatabase.update(TABLE_APPPKES,apppUdateCv(),"appnumber =?",new String[]{app_number});

            }
            cursor.close();
            sqLiteDatabase.delete(TABLE_UTILISATEUR,STATUS +"!=?",new String[]{String.valueOf(1)});

            sqLiteDatabase.setTransactionSuccessful();

        }finally {
            sqLiteDatabase.endTransaction();

        }

    }


    public ContentValues apppPersitence(){
        Date ladate = new Date();
        Timestamp timestamp = new Timestamp(ladate.getTime());
       long duree_licence = MesOutils.getDureeLicence(appkey);
        ContentValues cv = new ContentValues();
        cv.put(APPNUMBER,apppnumber);
        cv.put(APPPKEY,appkey);
        cv.put(OWNER, NAME_OWNER);
        cv.put(TELEPHONE,"");
        cv.put(ADRESSEELECTRO,"");
        cv.put(BASECODE,"clt");
        cv.put(DATELICENCE, timestamp.getTime());
        cv.put(DUREELICENCE,duree_licence);

        return cv;
    }

    public ContentValues apppUdateCv(){
        Date ladate = new Date();
        Timestamp timestamp = new Timestamp(ladate.getTime());
        long duree_licence = MesOutils.getDureeLicence(appkey);
        ContentValues cv = new ContentValues();
        cv.put(APPPKEY,appkey);
        cv.put(DATELICENCE, timestamp.getTime());
        cv.put(DUREELICENCE,duree_licence);

        return cv;
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
