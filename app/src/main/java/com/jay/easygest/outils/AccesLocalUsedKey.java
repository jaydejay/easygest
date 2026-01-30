package com.jay.easygest.outils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.jay.easygest.model.CleModel;

import java.util.ArrayList;

public class AccesLocalUsedKey {

    private final MySqliteOpenHelper accessBD;
    private SQLiteDatabase bd ;

    public AccesLocalUsedKey(Context context) {

        accessBD = new MySqliteOpenHelper(context, null);
        bd = accessBD.getWritableDatabase();
    }

    public ArrayList<CleModel> listeCles(){
        ArrayList<CleModel> cles = new ArrayList<>();
        try {
            //SQLiteDatabase bd = accessBD.getReadableDatabase();
//            String req = "select * from usedkey";
            String req = "select cle from usedkey";
            Cursor cursor = bd.rawQuery(req, null);
            cursor.moveToFirst();
            do {

                CleModel cle = new CleModel(cursor.getInt(0),cursor.getString(1));
                cles.add(cle);
            }
            while (cursor.moveToNext());
            cursor.close();
        }catch(Exception e){
            return cles;
        }
        return cles;

    }

    public ArrayList<String> listeDesCles(){
        ArrayList<String> cles = new ArrayList<>();
        try {
            //SQLiteDatabase bd = accessBD.getReadableDatabase();
//            String req = "select * from usedkey";
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
//        boolean success = false;
        ArrayList<String> cles = listeDesCles();

//        if (cles.contains(cle)){
//            success = true;
//        }

        return cles.contains(cle);
    }

    public boolean iscleExiste2(String cle) {
        boolean success = false;
        try {
            bd = accessBD.getReadableDatabase();
            String req = "select * from usedkey where cle = cle";
            Cursor cursor = bd.rawQuery(req, null);
            int nbrcle = cursor.getCount();
            if ( nbrcle > 0){
                success = true;
            }
            cursor.close();
            return success;
        }catch (Exception e){
            return success;
        }

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
