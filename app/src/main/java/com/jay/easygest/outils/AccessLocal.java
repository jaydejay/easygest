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

    private final MySqliteOpenHelper accessBD;
    private SQLiteDatabase bd;
    private final PasswordHascher passwordHascher;



    public AccessLocal(Context contexte) {

        this.accessBD =  MySqliteOpenHelper.getInstance(contexte,null);
        passwordHascher = new PasswordHascher();

    }



    public boolean ajouterUtilisateur(UserModel user){
        bd = accessBD.getWritableDatabase();
        boolean success = false;
        try{
            ContentValues cv = new ContentValues();
            cv.put(USERNAME,user.getUsername());
            cv.put(PASSWORD,user.getPassword());
            cv.put(DATE_INSCRIPTION,user.getDateInscription().getTime());
            cv.put(STATUS,user.getStatus());
            cv.put(ACTIF,user.isActif());
            cv.put(COMPTEUR,user.getCompteur());

            long rslt = bd.insert(UTILISATEUR,null,cv);
           if (rslt != -1){
               success = true;
           }
        }catch (Exception e){
            return false;
        }
        return success;
    }

    public void modifierUtilisateur(UserModel user){
        bd = accessBD.getWritableDatabase();
        try{
            ContentValues cv = new ContentValues();
            cv.put(USERNAME,user.getUsername());
            cv.put(PASSWORD,user.getPassword());
            cv.put(DATE_INSCRIPTION,user.getDateInscription().getTime());
            cv.put(ACTIF,user.isActif());
            cv.put(COMPTEUR,user.getCompteur());
            bd.update(UTILISATEUR,cv,STATUS+"="+user.getStatus(),null);
            bd.close();
        }catch (Exception e){
//            do nothing
            bd.close();
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
            boolean actif = cursor.getInt(5) == 1;
            int compteur = cursor.getInt(6);
            utilisateur = new UserModel(id,username,password,new Date(dateInscription),status,actif,compteur);
        }
        cursor.close();
        return utilisateur;
    }




    public Integer nbrUtilisateurs(){
        bd = accessBD.getReadableDatabase();
        try {
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
        if (passwordHascher.verifyHashingPass(password,proprietaire.getPassword())){
            if(proprietaire.getUsername().equals(username)){
                authenticated = true;
            }
        }

        return authenticated ;
    }

    public void desactiverProprietaire(){
        bd = accessBD.getWritableDatabase();
        try {
            ContentValues cv = new ContentValues();
            cv.put(ACTIF, false);
            bd.update(UTILISATEUR, cv, STATUS + "=" + 1, null);
            bd.close();
        }catch (Exception e){
            //do nothing
        }


    }

    public void activerProprietaire(){
        bd = accessBD.getWritableDatabase();
        try {
            ContentValues cv = new ContentValues();
            cv.put(ACTIF, true);
            cv.put(COMPTEUR, 0);
            bd.update(UTILISATEUR, cv, STATUS + "=" + 1, null);
            bd.close();
        }catch (Exception e){
            //do nothing
        }
    }


    public boolean authapp(String proprietaire, String cleproduit) {
        boolean success = false;
        bd = accessBD.getReadableDatabase();
      try{
        String req ="select * from APPPKES";
        Cursor cursor = bd.rawQuery(req,null);
        cursor.moveToFirst();
        if (!cursor.isBeforeFirst()){
            String owner = cursor.getString(2);
            String cle = cursor.getString(1);

            if (owner.equals(proprietaire) && cle.equals(cleproduit)){
                success = true;
            }
        }
        cursor.close();
        bd.close();

      }catch (Exception e){
          // do nohing
      }

      return success;
    }


}
