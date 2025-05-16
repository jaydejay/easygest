package com.jay.easygest.outils;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferedServiceHelper {
    private static final String DRIVE_FILE_ID = "drive_file_id";
    private final SharedPreferences sharedPreferences;
    private final SharedPreferences.Editor editor;

    public PreferedServiceHelper(Context context) {

        String DRIVE_SHARED_PREF_NAME = "drive_uploaded_data";
        sharedPreferences = context.getSharedPreferences(DRIVE_SHARED_PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public void saveDriveSession(String driveFileId){
        editor.putString(DRIVE_FILE_ID,driveFileId).commit();
    }

    public String getDriveSession(){
        return sharedPreferences.getString(DRIVE_FILE_ID,"");
    }

    public void deleteDriveSession(){
        editor.remove(DRIVE_FILE_ID);
    }

}
