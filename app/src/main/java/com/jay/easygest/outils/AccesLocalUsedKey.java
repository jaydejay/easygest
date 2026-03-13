package com.jay.easygest.outils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

public class AccesLocalUsedKey {

    private final MySqliteOpenHelper accessBD;
    private final SQLiteDatabase bd ;

    public AccesLocalUsedKey(Context context) {

        accessBD = new MySqliteOpenHelper(context, null);
        bd = accessBD.getWritableDatabase();
    }

    public ArrayList<String> listeDesCles(){
        ArrayList<String> cles = new ArrayList<>();
        try {
            String req = "select cle from usedkey";
            Cursor cursor = bd.rawQuery(req, null);
            cursor.moveToFirst();
            do {
                cles.add(cursor.getString(0));
            }
            while (cursor.moveToNext());
            cursor.close();
        }catch(Exception e){
            return cles;
        }
        return cles;

    }

    public boolean iscleExiste(String cle) {
        ArrayList<String> cles = listeDesCles();
        return cles.contains(cle);
    }


    public boolean isPermanentKey(String cle){
        boolean is_permanent_key ;
        try {
             is_permanent_key = MesOutils.getLicenceLevel(cle) == MesOutils.Level.PERMANENT;

        } catch (Exception e) {
            is_permanent_key = false;
        }

        return is_permanent_key;

    }

    public long ajouterCle(String cle){
      ContentValues cle_cv = new ContentValues();
      cle_cv.put("cle",cle);
      return bd.insertOrThrow("usedkey",null,cle_cv);
    }
}
