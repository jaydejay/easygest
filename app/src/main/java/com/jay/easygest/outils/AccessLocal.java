package com.jay.easygest.outils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.jay.easygest.model.AppKessModel;
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
    public static final String BASECODE = "basecode";
    public static final String DATELICENCE = "datelicence";
    public static final String DUREELICENCE = "dureelicence";
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

//            appkess_cv.put(OWNER,appKessModel.getOwner());
//            appkess_cv.put(BASECODE,appKessModel.getBasecode());
//            appkess_cv.put(TELEPHONE,appKessModel.getTelephone());
//            appkess_cv.put(ADRESSEELECTRO,appKessModel.getAdresseelectro());
//            appkess_cv.put(APPPKEY,appKessModel.getApppkey());


            long rslt = bd.insert(UTILISATEUR,null,cv);
//            bd.updateWithOnConflict("APPPKES",appkess_cv, APPNUMBER+"= ?", new String[] { String.valueOf(appKessModel.getAppnumber())},1);
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
            boolean actif = cursor.getInt(5)==1?true:false;
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


    public String[] appCredential(){
        bd = accessBD.getReadableDatabase();
        String[] credentials =null;
        try{
            String req ="select * from APPPKES";
            Cursor cursor = bd.rawQuery(req,null);
            cursor.moveToFirst();
            if (!cursor.isBeforeFirst()){
                credentials = new String[]{
                        cursor.getString(cursor.getColumnIndexOrThrow(APPNUMBER)),
                        cursor.getString(cursor.getColumnIndexOrThrow(APPPKEY)),
                        cursor.getString(cursor.getColumnIndexOrThrow(OWNER)),
                        cursor.getString(cursor.getColumnIndexOrThrow(BASECODE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(TELEPHONE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DATELICENCE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DUREELICENCE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(ADRESSEELECTRO))};

            }
            cursor.close();
        } catch (Exception e) {
            // do nothing
        }
        return credentials;
    }

    public boolean saveAppkey(String owner, String licence, String email, String telephone, String basecode, String appnumber, long dureelicence) {
        bd = accessBD.getReadableDatabase();
        try{

           ContentValues app_cv = new ContentValues();
           app_cv.put(OWNER,owner);
           app_cv.put(BASECODE,basecode);
           app_cv.put(TELEPHONE,telephone);
           app_cv.put(ADRESSEELECTRO,email);
           app_cv.put(DATELICENCE,new Date().getTime());
           app_cv.put(DUREELICENCE,dureelicence);
           app_cv.put(APPPKEY,licence);

          int rslt = bd.update("APPPKES",app_cv, APPNUMBER+"= ?", new String[] { String.valueOf(appnumber)});
            return rslt > 0;

        }catch (Exception e) {
            return false;
        }
    }

}
