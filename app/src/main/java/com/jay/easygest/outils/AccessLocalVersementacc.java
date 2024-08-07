package com.jay.easygest.outils;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class AccessLocalVersementacc {

    public static final String SOMMEVERSE = "sommeverse";
    public static final String ACCOUNTID = "accountid";
    public static final String CLIENTID = "clientid";
    public static final String DATEVERSEMENT = "dateversement";
    private MySqliteOpenHelper accessBD;
    private SQLiteDatabase bd;
    private Context contexte;

    public AccessLocalVersementacc(Context contexte) {
        this.contexte = contexte;
        this.accessBD = new MySqliteOpenHelper(contexte,null);
    }

    public ContentValues creerVersement(int sommeverse, int account_id, int client_id, Long dateversement) {
        ContentValues cv = new ContentValues();
        cv.put(SOMMEVERSE,sommeverse);
        cv.put(ACCOUNTID, account_id);
        cv.put(CLIENTID, client_id);
        cv.put(DATEVERSEMENT, dateversement);
        return cv;
    }
}
