package com.jay.easygest.outils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.jay.easygest.model.AppKessModel;

import java.util.ArrayList;

public class AccessLocalAppKes {

    public static final String APPNUMBER = "appnumber";
    public static final String APPPKEY = "apppkey";
    public static final String OWNER = "owner";
    public static final String BASECODE = "basecode";
    public static final String TELEPHONE = "telephone";
    public static final String ADRESSEELECTRO = "adresseelectro";
    private final MySqliteOpenHelper accessBD;
    private SQLiteDatabase bd;

    public AccessLocalAppKes(Context context) {
        accessBD = new MySqliteOpenHelper(context,null);
    }

    public AppKessModel getAppkes(){

        ArrayList<AppKessModel> _appKessModels = new ArrayList<>();
        AppKessModel appKessModel;

        try {
            bd = accessBD.getReadableDatabase();
            String req = "select * from APPPKES";
            Cursor cursor = bd.rawQuery(req, null);
            cursor.moveToFirst();
            do {
                AppKessModel appKes = new AppKessModel(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5) );
                _appKessModels.add(appKes);
            }
            while (cursor.moveToNext());
            cursor.close();

            appKessModel = _appKessModels.get(0);

        }catch(Exception e){
            appKessModel = null;
        }
        return appKessModel;
    }

    public boolean updateAppkes(AppKessModel appKessModel){
        boolean success = false;
        try{
            bd = accessBD.getWritableDatabase();
            ContentValues cv = new ContentValues();
            cv.put(APPNUMBER,appKessModel.getAppnumber());
            cv.put(APPPKEY,appKessModel.getApppkey());
            cv.put(OWNER,appKessModel.getOwner());
            cv.put(BASECODE,appKessModel.getBasecode());
            cv.put(TELEPHONE,appKessModel.getTelephone());
            cv.put(ADRESSEELECTRO,appKessModel.getAdresseelectro());
            int rslt = bd.update("APPPKES",cv, APPNUMBER +"="+appKessModel.getAppnumber(),null);
            if (rslt > 0){
                success = true;
            }
        }catch (Exception e){
          return success  ;

        }
        return  success;
    }

    /**
     * permet de mettre a jour la cle d'activation du produit
     * @param appKessModel gestonnaire d'activation
     * @return boolean
     */
    public boolean updateAppkesKey(AppKessModel appKessModel){
        boolean success = false;
        try{
            bd = accessBD.getWritableDatabase();
            ContentValues cv = new ContentValues();
            cv.put(APPPKEY,appKessModel.getApppkey());
            int rslt = bd.update("APPPKES",cv, APPNUMBER +"="+appKessModel.getAppnumber(),null);
            if (rslt > 0){
                success = true;
            }
        }catch (Exception e){
            return success  ;

        }
        return  success;
    }
}
