package com.jay.easygest.outils;

import android.content.Context;
import android.content.SharedPreferences;
import com.jay.easygest.outils.VariablesStatique;

public class SessionManagement {

    public static final String SHARED_PREF_NAME = "auth_shared";
    private final SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public SessionManagement(Context context) {
        sharedPreferences = context.getSharedPreferences(VariablesStatique.AUTH_SHARED_PREF_NAME, Context.MODE_PRIVATE);
         editor = sharedPreferences.edit();
    }

    public void saveSession(boolean status){
        editor.putBoolean(VariablesStatique.AUTH_SHARED_PREF_VARIABLE,status).commit();
    }

    public boolean getSession(){
        return sharedPreferences.getBoolean(VariablesStatique.AUTH_SHARED_PREF_VARIABLE, false);
    }

    public void removeSession(){
        editor.remove("is_authenticated").commit();
    }
}