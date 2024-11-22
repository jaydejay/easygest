package com.jay.easygest.outils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.jay.easygest.model.ClientModel;
import com.jay.easygest.model.SmsnoSentModel;

import java.util.ArrayList;


public class AccessLocalSmsSender {

    public static final String SMSFAILLED = "smsfailled";
    public static final String CLIENTID = "clientid";
    public static final String SOMMEVERSE = "sommeverse";
    public static final String TOTALOPERATION = "totaloperation";
    public static final String TOTALRESTE = "totalreste";
    public static final String OPERATION = "operation";
    public static final String ISDEFERE = "isdefere";
    public static final String DATEOPERATION = "dateoperation";
    public static final String MESSAGE = "message";
    public static final String SMSID = "smsid";
    private final MySqliteOpenHelper accessBD;
    private SQLiteDatabase db;
    private final Context context;
    private final AccessLocalClient accessLocalClient;

    public AccessLocalSmsSender(Context context) {
        this.context = context;
     accessBD = new MySqliteOpenHelper(context,null);
     accessLocalClient = new AccessLocalClient(context);
    }

    public boolean insert(SmsnoSentModel smsnoSentModel){
        boolean success;
        try{
        db = accessBD.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(CLIENTID,smsnoSentModel.getClientid());
        cv.put(MESSAGE,smsnoSentModel.getMessage());
        cv.put(SMSID,smsnoSentModel.getSmsid());
        db.insert(SMSFAILLED,null,cv);
        success = true;
        }catch (Exception e){
            success=false;
        }

        return success;
    }

    public SmsnoSentModel getSmsnoSentModel(int id){
        SmsnoSentModel smsnoSentModel = null;
        try{
            db = accessBD.getReadableDatabase();
            AccessLocalClient accessLocalClient = new AccessLocalClient(context);
            Cursor cursor = db.query(SMSFAILLED,null,"id = ?",new String[]{String.valueOf(id)},null,null,null);
            cursor.moveToFirst();
            if (!cursor.isAfterLast()){
               ClientModel client = accessLocalClient.recupUnClient(cursor.getInt(1));
               smsnoSentModel = new SmsnoSentModel(cursor.getInt(0), client,cursor.getString(2), cursor.getInt(3));

            }
            cursor.close();
        }catch (Exception e){
            //do nothing
        }

        return smsnoSentModel;
    }


    public boolean delete(SmsnoSentModel smsnoSentModel){
        boolean success = false;
        try {
            db = accessBD.getWritableDatabase();
            int rslt = db.delete(SMSFAILLED, "id = ?", new String[]{String.valueOf(smsnoSentModel.getId())});
            if (rslt != -1) {
                success = true;
            }
            db.close();
        }catch (Exception e){
            //do nthing
        }
        return success;
    }

    public ArrayList<SmsnoSentModel> listSmsnoSent(){

        db = accessBD.getReadableDatabase();
        ArrayList<SmsnoSentModel> smsnoSentModels = new ArrayList<>();
        try {
            Cursor cursor = db.query(SMSFAILLED,null,"id > 0",null,null,null,null);
//            String req = "select * from smsfailled";
//            Cursor cursor = db.rawQuery(req, null);
            if (cursor != null){
                cursor.moveToFirst();
                do {
                    ClientModel client = accessLocalClient.recupUnClient(cursor.getInt(1));
                    SmsnoSentModel sms = new SmsnoSentModel(cursor.getInt(0), client,cursor.getString(2), cursor.getInt(3));
                    smsnoSentModels.add(sms);
                }while(cursor.moveToNext());
                cursor.close();
            }
        }catch (Exception e){
            //do nothing
        }

      return smsnoSentModels;
    }


}
