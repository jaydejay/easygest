package com.jay.easygest.outils;

import android.content.ContentValues;
import android.content.Context;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;
import java.util.Date;


public class MySqliteOpenHelper extends SQLiteOpenHelper {


    public static final int version = 1;
    public static final String name = "gestioncredit.db";
    public static final String TABLE_UTILISATEUR = "utilisateur";
    public static final String TABLE_APPPKES = "APPPKES";
    public static final String TABLE_VERSEMENT = "versement";
    public static final String TABLE_CLIENT = "client";
    public static final String TABLE_CREDIT = "credit";
    public static final String TABLE_ACCOUNT = "account";
    public static final String TABLE_INFO = "infos";
    public static final String TABLE_SMSFAILLED = "smsfailled";
    public static final String NAME_ADMIN = "JayAdmine";
    public static final String PASS_ADMIN = "jayrard10";
    private static final String TABLE_VERSEMENTACC = "versementacc";
    public static final String TABLE_ARTICLES = "articles";
    public static final String TABLE_IMAGE = "image";
    public static final String APPNUMBER = "appnumber";
    public static final String APPPKEY = "apppkey";
    public static final String OWNER = "owner";
    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";
    public static final String DATE_INSCRIPTION = "dateInscription";
    public static final String STATUS = "status";
    public static final String ACTIF = "actif";
    public static final String COMPTEUR = "compteur";
    public static final String NBR_CREDIT = "nbrcredit";
    public static final String TOTAL_CREDIT = "totalcredit";
    public static final String NBR_ACCOUNT = "nbraccount";
    public static final String TOTAL_ACCOUNT = "totalaccount";
    public static final String TELEPHONE = "telephone";
    public static final String ADRESSEELECTRO = "adresseelectro";
    public static final String BASECODE = "basecode";
    public static final String NAME_OWNER = "solaris";
    public static final String NAME_SUPERADMIN = "SuperJay";
    public static final String PASS_SUPERADMIN = "jayrard101";

    private  String apppkey ;
    private int apppnumber ;

    private final String createTable_utilisateur = "create table "+TABLE_UTILISATEUR+"("
            +"id Integer primary key autoincrement,"
            +"username Text not null unique,"
            +"password Text not null,"
            +"dateInscription Long not null,"
            +"status Integer not null,"
            +"actif boolean not null,"
            +"compteur Integer not null)";


    private final String createTable_client = "create table "+TABLE_CLIENT+"("
            +"id Integer primary key autoincrement,"
            +"codeclient Text not null unique,"
            +"nom Text not null,"
            +"prenoms Text not null,"
            +"telephone Text not null,"
            +"adresseelectro Text,"
            +"residence Text,"
            +"cni Text,"
            +"permis Text,"
            +"passport Text,"
            +"societe Text,"
            +"nbrcredit Integer,"
            +"totalcredit Integer,"
            +"nbraccount Integer,"
            +"totalaccount Integer)";

    private final String createTable_account = "create table "+TABLE_ACCOUNT+"("
            +"id Integer primary key autoincrement,"
            +"clientid Integer not null,"
            +"article1 Text,"
            +"article2 Text,"
            +"sommeaccount Integer not null,"
            +"versements Integer not null,"
            +"reste Integer not null,"
            +"dateaccount Long not null ,"
            +"numeroaccount Integer,"
            +"soldedat Long,"
            +"foreign key(clientid) references client(id) on delete cascade )";


    private final String createTable_credit = "create table "+TABLE_CREDIT+"("
            +"id Integer primary key autoincrement,"
            +"clientid Integer not null,"
            +"article1 Text,"
            +"article2 Text,"
            +"sommecredit Integer not null,"
            +"versements Integer not null,"
            +"reste Integer not null,"
            +"datecredit Long not null ,"
            +"numerocredit Integer,"
            +"soldedat Long,"
            +"foreign key(clientid) references client(id) on delete cascade )";

    private final String createTable_versementacc = "create table "+TABLE_VERSEMENTACC+"("
            +"id Integer primary key autoincrement,"
            +"sommeverse Integer not null,"
            +"accountid Integer not null,"
            +"clientid Integer not null,"
            +"dateversement Long not null,"
            +"foreign key(accountid) references account(id) on delete cascade,"
            +"foreign key(clientid) references client(id) on delete cascade )";

    private final String createTable_versement = "create table "+TABLE_VERSEMENT+"("
            +"id Integer primary key autoincrement,"
            +"sommeverse Integer not null,"
            +"creditid Integer not null,"
            +"clientid Integer not null,"
            +"dateversement Long not null,"
            +"foreign key(creditid) references credit(id) on delete cascade,"
            +"foreign key(clientid) references client(id) on delete cascade )";



private final String createTable_apppkes = "create table "+TABLE_APPPKES+" ("
            +"appnumber Integer primary key,"
            +"apppkey Text not null unique,"
            +"owner Text not null,"
            +"basecode Text,"
            +"telephone Text,"
            +"adresseelectro Text)";

private final String createTable_info = "create table "+TABLE_INFO+" ("
        +"appnumber Integer primary key,"
        +"nbrcredit Integer,"
        +"totalcredit Integer,"
        +"nbraccount Integer,"
        +"totalaccount Integer)";

    private final String createTable_smsfailled = "create table "+ TABLE_SMSFAILLED +" ("
            +"id Integer primary key,"
            +"clientid Integer ,"
            +"message Text not null,"
            +"smsid Integer not null,"
            +"foreign key(clientid) references client(id) on delete cascade)";

    private final String createTable_articles = "create table "+ TABLE_ARTICLES +" ("
            +"id Integer primary key,"
            +"designation Text not null,"
            +"prix Integer not null,"
            +"quantite Integer not null,"
            +"description Text)";

    private final String createTable_image = "create table "+ TABLE_IMAGE +" ("
            +"id Integer primary key,"
            +"image Blob,"
            +"articleid Integer not null,"
            +"foreign key(articleid) references articles(id) on delete cascade)";






    public MySqliteOpenHelper(@Nullable Context context, @Nullable SQLiteDatabase.CursorFactory factory) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        apppnumber = MesOutils.apppnumbergenerator();
         apppkey = MesOutils.apppkeygenerator();

        sqLiteDatabase.beginTransaction();

        try {
            sqLiteDatabase.execSQL(createTable_utilisateur);
            sqLiteDatabase.execSQL(createTable_client);
            sqLiteDatabase.execSQL(createTable_account);
            sqLiteDatabase.execSQL(createTable_articles);
            sqLiteDatabase.execSQL(createTable_credit);
            sqLiteDatabase.execSQL(createTable_versementacc);
            sqLiteDatabase.execSQL(createTable_versement);
            sqLiteDatabase.execSQL(createTable_apppkes);
            sqLiteDatabase.execSQL(createTable_info);
            sqLiteDatabase.execSQL(createTable_smsfailled);
            sqLiteDatabase.execSQL(createTable_image);

            sqLiteDatabase.insert(TABLE_UTILISATEUR,null,creerSuperUser());
            sqLiteDatabase.insert(TABLE_UTILISATEUR,null,creerAdministrateur());
            sqLiteDatabase.insert(TABLE_APPPKES,null,apppPersitence());
            sqLiteDatabase.insert(TABLE_INFO,null,creeeinfo());
            sqLiteDatabase.setTransactionSuccessful();


        }catch (Exception e){
//            sqLiteDatabase.endTransaction();
        }finally {
            sqLiteDatabase.endTransaction();

        }




    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldversion, int newversion) {

    }

    private ContentValues creerSuperUser(){
        ContentValues cv = new ContentValues();
        cv.put(USERNAME, NAME_SUPERADMIN);
        cv.put(PASSWORD, PASS_SUPERADMIN);
        cv.put(DATE_INSCRIPTION,new Date().getTime());
        cv.put(STATUS,3);
        cv.put(ACTIF,true);
        cv.put(COMPTEUR,0);
        return cv;

    }

    private ContentValues creerAdministrateur(){

        ContentValues cv = new ContentValues();
        cv.put(USERNAME,NAME_ADMIN);
        cv.put(PASSWORD,PASS_ADMIN);
        cv.put(DATE_INSCRIPTION,new Date().getTime());
        cv.put(STATUS,0);
        cv.put(ACTIF,true);
        cv.put(COMPTEUR,0);
        return cv;
    }

    public ContentValues apppPersitence(){
        ContentValues cv = new ContentValues();
        cv.put(APPNUMBER,apppnumber);
        cv.put(APPPKEY,apppkey);
        cv.put(OWNER, NAME_OWNER);
        cv.put(TELEPHONE,"");
        cv.put(ADRESSEELECTRO,"");
        cv.put(BASECODE,"clt");

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
