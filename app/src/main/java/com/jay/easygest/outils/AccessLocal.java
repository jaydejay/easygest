package com.jay.easygest.outils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.jay.easygest.model.UserModel;

import java.util.Date;

public class AccessLocal {

    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";
    private static final String DATE_INSCRIPTION = "dateInscription";
    private static final String STATUS = "status";
    private static final String ACTIF = "actif";
    private static final String COMPTEUR = "compteur";
    private static final String UTILISATEUR = "utilisateur";


    public static final String APPNUMBER = "appnumber";
    public static final String APPPKEY = "apppkey";
    public static final String OWNER = "owner";
    public static final String TELEPHONE = "telephone";
    public static final String ADRESSEELECTRO = "adresseelectro";
    private final MySqliteOpenHelper accessBD;
    private SQLiteDatabase bd;



    public AccessLocal(Context contexte) {

        this.accessBD = new MySqliteOpenHelper(contexte,null);

    }



    public void ajouterUtilisateur(UserModel user){
        try{
            bd = accessBD.getWritableDatabase();
            ContentValues cv = new ContentValues();
            cv.put(USERNAME,user.getUsername());
            cv.put(PASSWORD,user.getPassword());
            cv.put(DATE_INSCRIPTION,user.getDateInscription().getTime());
            cv.put(STATUS,user.getStatus());
            cv.put(ACTIF,user.isActif());
            cv.put(COMPTEUR,user.getCompteur());
            bd.insert(UTILISATEUR,null,cv);

        }catch (Exception e){
            //do nothing

        }

    }

    public void modifierUtilisateur(UserModel user){
        try{
            bd = accessBD.getWritableDatabase();
            ContentValues cv = new ContentValues();
            cv.put(USERNAME,user.getUsername());
            cv.put(PASSWORD,user.getPassword());
            cv.put(DATE_INSCRIPTION,user.getDateInscription().getTime());
            cv.put(ACTIF,user.isActif());
            cv.put(COMPTEUR,user.getCompteur());

            bd.update(UTILISATEUR,cv,STATUS+"="+user.getStatus(),null);

        }catch (Exception e){
//            do nothing

        }

    }


    public UserModel recupProprietaire(){
        bd = accessBD.getReadableDatabase();
        String req = "select * from utilisateur where status = 1";

        UserModel utilisateur = null;
        Cursor cursor = bd.rawQuery(req,null);
        cursor.moveToLast();

        if(!cursor.isAfterLast()){

            int id = cursor.getInt(0);
            String username = cursor.getString(1);
            String password = cursor.getString(2);
            long dateInscription = cursor.getLong(3);
            int status = cursor.getInt(4);
            boolean actif = cursor.getInt(5)==1?true:false;
            int compteur = cursor.getInt(6);
            utilisateur = new UserModel(id,username,password,new Date(dateInscription),status,actif,compteur);
        }
        cursor.close();
        return utilisateur;
    }


    public UserModel recupAdministrateur(){
        bd = accessBD.getReadableDatabase();
        String req = "select * from utilisateur where status = 0";

        UserModel utilisateur = null;
        Cursor cursor = bd.rawQuery(req,null);
        cursor.moveToLast();
        if(!cursor.isAfterLast()){

            int id = cursor.getInt(0);
            String username = cursor.getString(1);
            String password = cursor.getString(2);
            long dateInscription = cursor.getLong(3);
            int status = cursor.getInt(4);
            boolean actif = cursor.getInt(5)==1?true:false;
            int compteur = cursor.getInt(6);
            utilisateur = new UserModel(id,username,password,new Date(dateInscription),status,actif,compteur);
        }
        cursor.close();
        return utilisateur;
    }

    public Integer nbrUtilisateurs(){
        try {
            bd = accessBD.getReadableDatabase();
            String req = "select * from utilisateur";
            Cursor cursor = bd.rawQuery(req, null);
            int nbrUtilisateur = cursor.getCount();
            cursor.close();
            return nbrUtilisateur;
        }catch (Exception e){
            return 0;
        }
    }


    public boolean isAuthenticated(String username, String password){
        boolean authenticated = false;
        UserModel proprietaire = this.recupProprietaire();
        if(proprietaire.getUsername().equals(username) && proprietaire.getPassword().equals(password)){
            authenticated = true;
        }
        return authenticated ;
    }

    public void desactiverProprietaire(){
        try {
            bd = accessBD.getWritableDatabase();
            ContentValues cv = new ContentValues();
            cv.put(ACTIF, false);
            bd.update(UTILISATEUR, cv, STATUS + "=" + 1, null);
        }catch (Exception e){
            //do nothing
        }


    }

    public void activerProprietaire(String mdp){
        try {
            bd = accessBD.getWritableDatabase();
            ContentValues cv = new ContentValues();
            cv.put(ACTIF, true);
            cv.put(COMPTEUR, 0);
            cv.put(PASSWORD, mdp);
            bd.update(UTILISATEUR, cv, STATUS + "=" + 1, null);
        }catch (Exception e){
            //do nothing
        }


    }

    public void desactiverAdministrateur(){
        bd = accessBD.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(ACTIF,false);
        bd.update(UTILISATEUR,cv,STATUS+"="+0,null);


    }

    public void activerAdministrateur(){

        bd = accessBD.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(ACTIF,true);
        cv.put(COMPTEUR,0);
        bd.update(UTILISATEUR,cv,STATUS+"="+0,null);


    }

    public boolean authapp(String proprietaire, String cleproduit) {
      try{
          bd = accessBD.getReadableDatabase();
        boolean success = false;
        String req ="select * from APPPKES";
        Cursor cursor = bd.rawQuery(req,null);
        cursor.moveToFirst();
        if (!cursor.isBeforeFirst()){
                       if(cursor.getString(cursor.getColumnIndexOrThrow("owner")).equals(proprietaire) && cursor.getString(cursor.getColumnIndexOrThrow("apppkey")).equals(cleproduit) ){
                success = true;
            }
        }
        cursor.close();

        return success;
      }catch (Exception e){return false;}
    }


    public String[] appCredential(){

        bd = accessBD.getReadableDatabase();
        String[] credentials =null;
        String req ="select * from APPPKES";
        Cursor cursor = bd.rawQuery(req,null);
        cursor.moveToFirst();
        if (!cursor.isBeforeFirst()){
            credentials = new String[]{cursor.getString(cursor.getColumnIndexOrThrow(APPNUMBER)),cursor.getString(cursor.getColumnIndexOrThrow(APPPKEY)),cursor.getString(cursor.getColumnIndexOrThrow(OWNER)),cursor.getString(cursor.getColumnIndexOrThrow(TELEPHONE)),cursor.getString(cursor.getColumnIndexOrThrow(ADRESSEELECTRO))};

        }
        cursor.close();

        return credentials;
    }

}