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
    public static final String TABLE_INFO = "infocredit";
    private static final String TABLE_VERSEMENTACC = "versementacc";
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


    private  String apppkey ;
    private int apppnumber ;

    private final String createTable1 = "create table "+TABLE_UTILISATEUR+"("
            +"id Integer primary key autoincrement,"
            +"username Text not null unique,"
            +"password Text not null,"
            +"dateInscription Long not null,"
            +"status Integer not null,"
            +"actif boolean not null,"
            +"compteur Integer not null)";


    private final String createTable2 = "create table "+TABLE_CLIENT+"("
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

    private final String createTable3 = "create table "+TABLE_ACCOUNT+"("
            +"id Integer primary key autoincrement,"
            +"clientid Integer not null,"
            +"article1 Text,"
            +"article2 Text,"
            +"sommeaccount Integer not null,"
            +"versements Integer not null,"
            +"reste Integer not null,"
            +"dateaccount Long not null ,"
            +"numeroaccount Integer,"
            +"foreign key(clientid) references client(id) on delete cascade )";


    private final String createTable4 = "create table "+TABLE_CREDIT+"("
            +"id Integer primary key autoincrement,"
            +"clientid Integer not null,"
            +"article1 Text,"
            +"article2 Text,"
            +"sommecredit Integer not null,"
            +"versements Integer not null,"
            +"reste Integer not null,"
            +"datecredit Long not null ,"
            +"numerocredit Integer,"
            +"foreign key(clientid) references client(id) on delete cascade )";

    private final String createTable5 = "create table "+TABLE_VERSEMENTACC+"("
            +"id Integer primary key autoincrement,"
            +"sommeverse Integer not null,"
            +"accountid Integer not null,"
            +"clientid Integer not null,"
            +"dateversement Long not null,"
            +"foreign key(accountid) references account(id) on delete cascade,"
            +"foreign key(clientid) references client(id) on delete cascade )";

    private final String createTable6 = "create table "+TABLE_VERSEMENT+"("
            +"id Integer primary key autoincrement,"
            +"sommeverse Integer not null,"
            +"creditid Integer not null,"
            +"clientid Integer not null,"
            +"dateversement Long not null,"
            +"foreign key(creditid) references credit(id) on delete cascade,"
            +"foreign key(clientid) references client(id) on delete cascade )";



private final String createTable7 = "create table "+TABLE_APPPKES+" ("
            +"appnumber Integer primary key ,"
            +"apppkey Text not null unique,"
            +"owner Text not null,"
            +"telephone Text,"
            +"adresseelectro Text)";

private final String createTable8 = "create table "+TABLE_INFO+" ("
        +"appnumber Text primary key,"
        +"nbrcredit Integer,"
        +"totalcredit Integer,"
        +"nbraccount Integer,"
        +"totalaccount Integer)";





    public MySqliteOpenHelper(@Nullable Context context, @Nullable SQLiteDatabase.CursorFactory factory) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        apppnumber = MesOutils.apppnumbergenerator();
         apppkey = MesOutils.apppkeygenerator();

        sqLiteDatabase.beginTransaction();

        try {
            sqLiteDatabase.execSQL(createTable1);
            sqLiteDatabase.execSQL(createTable2);
            sqLiteDatabase.execSQL(createTable3);
            sqLiteDatabase.execSQL(createTable4);
            sqLiteDatabase.execSQL(createTable5);
            sqLiteDatabase.execSQL(createTable6);
            sqLiteDatabase.execSQL(createTable7);
            sqLiteDatabase.execSQL(createTable8);

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
        cv.put(USERNAME,"SuperJay");
        cv.put(PASSWORD,"jayrard101");
        cv.put(DATE_INSCRIPTION,new Date().getTime());
        cv.put(STATUS,3);
        cv.put(ACTIF,true);
        cv.put(COMPTEUR,0);
        return cv;

    }

    private ContentValues creerAdministrateur(){

        ContentValues cv = new ContentValues();
        cv.put(USERNAME,"JayAdmine");
        cv.put(PASSWORD,"jayrard10");
        cv.put(DATE_INSCRIPTION,new Date().getTime());
        cv.put(STATUS,0);
        cv.put(ACTIF,true);
        cv.put(COMPTEUR,0);
        return cv;
    }

    public ContentValues apppPersitence(){
        ContentValues cv = new ContentValues();

//        String apppkey = MesOutils.apppkeygenerator();
        cv.put(APPNUMBER,apppnumber);
        cv.put(APPPKEY,apppkey);
        cv.put(OWNER,"solaris");

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