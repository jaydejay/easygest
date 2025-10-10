package com.jay.easygest.outils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.jay.easygest.model.AppKessModel;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;

public class AccessLocalAppKes {

    public static final String APPNUMBER = "appnumber";
    public static final String APPPKEY = "apppkey";
    public static final String OWNER = "owner";
    public static final String BASECODE = "basecode";
    public static final String TELEPHONE = "telephone";
    public static final String ADRESSEELECTRO = "adresseelectro";

    public static final String DATELICENCE = "datelicence";
    public static final String DUREELICENCE = "dureelicence";
    private final MySqliteOpenHelper accessBD;
    private SQLiteDatabase bd;

    public AccessLocalAppKes(Context context) {
        accessBD = new MySqliteOpenHelper(context, null);
    }

    public AppKessModel getAppkes() {

        ArrayList<AppKessModel> _appKessModels = new ArrayList<>();
        AppKessModel appKessModel;

        try {
            bd = accessBD.getReadableDatabase();
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
     * permet de mettre a jour les infos generales
     *
     * @param appKessModel Le model
     * @return true si reussi sinon faux
     */
    public boolean updateAppkes(AppKessModel appKessModel) {
        boolean success = false;
        try {
            bd = accessBD.getWritableDatabase();
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
     *
     * @param appKessModel   gestIonnaire d'activation
     * @param appcredentials Les credentials
     * @return boolean
     */
    public boolean updateAppkesKey(AppKessModel appKessModel, String[] appcredentials) {
        boolean success = false;
        try {
            bd = accessBD.getWritableDatabase();
            ContentValues cv = new ContentValues();
            Timestamp timestamp = new Timestamp(new Date().getTime());

            long temp_restant = Long.parseLong(appcredentials[6]) - (new Date().getTime());
            long duree_licence = MesOutils.getDureeLicence(appKessModel.getApppkey()) + temp_restant;

            cv.put(APPNUMBER, appKessModel.getApppkey());
            cv.put(APPPKEY, appKessModel.getApppkey());
            cv.put(DATELICENCE, timestamp.getTime());
            cv.put(DUREELICENCE, duree_licence);
            int rslt = bd.update(VariablesStatique.TABLE_APPPKES, cv, APPNUMBER + "= ?", new String[] {appcredentials[0]} );
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
     *
     * @param appcredentials Les credentials
     * @return boolean
     */
    public boolean newupdateAppkesKey(String cleproduit, String _appnumber, String[] appcredentials) {
        boolean success = false;
        bd = accessBD.getWritableDatabase();
        bd.beginTransaction();
        try {

            ContentValues cv = new ContentValues();
            Timestamp timestamp = new Timestamp(new Date().getTime());

            long temp_restant = Long.parseLong(appcredentials[6]) - (new Date().getTime());
            long duree_licence = MesOutils.getDureeLicence(cleproduit) + temp_restant;

            cv.put(APPNUMBER, _appnumber);
            cv.put(APPPKEY, cleproduit);
            cv.put(DATELICENCE, timestamp.getTime());
            cv.put(DUREELICENCE, duree_licence);
            cv.put(OWNER, appcredentials[2]);
            cv.put(TELEPHONE,appcredentials[4]);
            cv.put(ADRESSEELECTRO,appcredentials[7]);
            cv.put(BASECODE,appcredentials[3]);

//            int rslt = bd.update("APPPKES", cv, APPNUMBER + "=" + Integer.parseInt(appcredentials[0]), null);
            long rslt = bd.insertOrThrow(VariablesStatique.TABLE_APPPKES,null,cv);
            if (rslt != -1) {
                bd.delete(VariablesStatique.TABLE_APPPKES,APPNUMBER + "= ?", new String[] {appcredentials[0]});
                success = true;

                bd.setTransactionSuccessful();
            }
        } catch (Exception e) {
            // do nothing
            return false ;

        }finally {
            bd.endTransaction();
        }
        return success;
    }
}
