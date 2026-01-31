package com.jay.easygest.outils;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManagement {
    private final SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public SessionManagement(Context context) {
        sharedPreferences = context.getSharedPreferences(VariablesStatique.AUTH_SHARED_PREF_NAME, Context.MODE_PRIVATE);
         editor = sharedPreferences.edit();
    }

    public void saveSession(boolean status){
        editor.putBoolean(VariablesStatique.AUTH_SHARED_PREF_IS_AUTHENTICATED,status).commit();
    }

    public boolean getSession(){
        return sharedPreferences.getBoolean(VariablesStatique.AUTH_SHARED_PREF_IS_AUTHENTICATED, false);
    }

    public void removeSession(){
        editor.remove(VariablesStatique.AUTH_SHARED_PREF_IS_AUTHENTICATED).commit();
    }

    public void savekeyActivated(boolean status){
        editor.putBoolean(VariablesStatique.AUTH_SHARED_PREF_KEY_ACTIVATED,status).commit();
    }

    public boolean getkeyActivated(){
        return sharedPreferences.getBoolean(VariablesStatique.AUTH_SHARED_PREF_KEY_ACTIVATED, false);
    }

    public void saveLicenceExpiredStatus(boolean status){
        editor.putBoolean(VariablesStatique.AUTH_SHARED_PREF_LICENCE_STATUS,status).commit();
    }

    public boolean getLicenceExpiredStatus(){
        return sharedPreferences.getBoolean(VariablesStatique.AUTH_SHARED_PREF_LICENCE_STATUS, false);
    }

    public void saveUtilisateurCreated(boolean status){
        editor.putBoolean(VariablesStatique.AUTH_SHARED_PREF_COMPTE_CREATED,status).commit();
    }

    public boolean getUtilisateurCreated(){
        return sharedPreferences.getBoolean(VariablesStatique.AUTH_SHARED_PREF_COMPTE_CREATED, false);
    }
}
