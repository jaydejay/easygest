package com.jay.easygest.outils;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManagement {

    public static final String SHARED_PREF_NAME = "auth_shared";
    private final SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public SessionManagement(Context context) {
        sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
         editor = sharedPreferences.edit();
    }

    public void saveSession(boolean status){
        editor.putBoolean("is_authenticated",status).commit();
    }

    public boolean getSession(){
        return sharedPreferences.getBoolean("is_authenticated", false);
    }

    public void removeSession(){
        editor.remove("is_authenticated").commit();
    }
}
