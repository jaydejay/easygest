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
        accessBD =  MySqliteOpenHelper.getInstance(context, null);
    }

    public AppKessModel getAppkes() {

        ArrayList<AppKessModel> _appKessModels = new ArrayList<>();
        AppKessModel appKessModel;
        bd = accessBD.getReadableDatabase();
        try {
            String req = "select * from APPPKES";
            Cursor cursor = bd.rawQuery(req, null);
            cursor.moveToFirst();
            do {
                AppKessModel appKes = new AppKessModel(
                        cursor.getInt(cursor.getColumnIndexOrThrow(APPNUMBER)),
                        cursor.getString(cursor.getColumnIndexOrThrow(APPPKEY)),
                        cursor.getString(cursor.getColumnIndexOrThrow(OWNER)),
                        cursor.getString(cursor.getColumnIndexOrThrow(BASECODE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(TELEPHONE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(ADRESSEELECTRO)));
                _appKessModels.add(appKes);
            }
            while (cursor.moveToNext());
            cursor.close();

            appKessModel = _appKessModels.get(0);

        } catch (Exception e) {
            appKessModel = null;
        }
        return appKessModel;
    }

    /**
     * permet de mettre a jour les infos generales sans la cle et app number
     * @param appKessModel Le model
     * @return true si reussi sinon faux
     */
    public boolean updateAppkes(AppKessModel appKessModel) {
        boolean success = false;
        bd = accessBD.getWritableDatabase();
        try {
            ContentValues cv = new ContentValues();
            cv.put(OWNER, appKessModel.getOwner());
            cv.put(BASECODE, appKessModel.getBasecode());
            cv.put(TELEPHONE, appKessModel.getTelephone());
            cv.put(ADRESSEELECTRO, appKessModel.getAdresseelectro());
            int rslt = bd.update("APPPKES", cv, APPNUMBER + "=" + appKessModel.getAppnumber(), null);
            if (rslt > 0) {
                success = true;
            }
        } catch (Exception e) {
            // do nothing
            return false;
        }
        return success;
    }

    /**
     * permet de mettre a jour la cle d'activation du produit
     * @param appKessModel gestIonnaire d'activation
     * @return boolean
     */
    public boolean updateAppkesKey(AppKessModel appKessModel) {
        boolean success = false;
        bd = accessBD.getWritableDatabase();
        bd.beginTransaction();
        try {

            ContentValues appkey_cv = new ContentValues();

            appkey_cv.put(APPPKEY, appKessModel.getApppkey());
            int rslt = bd.updateWithOnConflict(VariablesStatique.TABLE_APPPKES, appkey_cv, APPNUMBER + "= ?", new String[] {String.valueOf(appKessModel.getAppnumber())},1 );
            if (rslt > 0) {
                bd.setTransactionSuccessful();
                success = true;

            }
        } catch (Exception e) {
            return false;
        }finally {
            if (bd.inTransaction()){
                bd.endTransaction();
            }
        }
        return success;
    }


    /**
     * creer une agence
     *
     * @param appKessModel   gestIonnaire d'activation
     * @return boolean
     */
    public boolean createAgence(AppKessModel appKessModel) {
        boolean success = false;
        bd = accessBD.getWritableDatabase();
        try {

        ContentValues appkey_cv = new ContentValues();
        appkey_cv.put(OWNER, appKessModel.getOwner());
        appkey_cv.put(BASECODE, appKessModel.getBasecode());
        appkey_cv.put(TELEPHONE, appKessModel.getTelephone());
        appkey_cv.put(ADRESSEELECTRO, appKessModel.getAdresseelectro());
        int rslt = bd.updateWithOnConflict(VariablesStatique.TABLE_APPPKES, appkey_cv, APPNUMBER + "= ?", new String[] {String.valueOf(appKessModel.getAppnumber())},1 );
        if (rslt > 0){
            success = true;
        }
        } catch (Exception e) {
            return false;
        }
        return success;
    }


}
