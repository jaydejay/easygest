package com.jay.easygest.outils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import com.jay.easygest.controleur.Clientcontrolleur;
import com.jay.easygest.model.ClientModel;

import java.util.ArrayList;

public class AccessLocalClient {

    public static final String ID = "id";
    public static final String CODECLIENT = "codeclient";
    public static final String NOM = "nom";
    public static final String PRENOMS = "prenoms";
    public static final String TELEPHONE = "telephone";
    public static final String ADRESSEELECTRO = "adresseelectro";
    public static final String RESIDENCE = "residence";
    public static final String CNI = "cni";
    public static final String PERMIS = "permis";
    public static final String PASSPORT = "passport";
    public static final String SOCIETE = "societe";
    public static final String CLIENT = "client";
    public static final String CREDIT="credit";
    public static final String TOTAL_CREDIT="totalcredit";
    public static final String TOTAL_ACCOUNT="totalaccount";
    public static final String NBRCREDIT = "nbrcredit";
    public static final String NBRACCOUNT="nbraccount";
    public static final String VERSEMENT = "versement";
    private final MySqliteOpenHelper accessBD;
    private SQLiteDatabase bd;
    private final Context contexte;

    public AccessLocalClient(Context contexte) {
        this.contexte = contexte;
        this.accessBD = new MySqliteOpenHelper(contexte, null);
    }

    public ContentValues ajouterClient(String codeclient,String nom,String prenoms, String telephone,Integer nbrcredit,Integer totalcredit, Integer nbraccount,Integer totalaccount) {
        ContentValues cv= new ContentValues();
        cv.put(CODECLIENT,codeclient);
        cv.put(NOM,nom);
        cv.put(PRENOMS,prenoms);
        cv.put(TELEPHONE,telephone);
        cv.put(NBRCREDIT,nbrcredit);
        cv.put(TOTAL_CREDIT,totalcredit);
        cv.put(NBRACCOUNT,nbraccount);
        cv.put(TOTAL_ACCOUNT,totalaccount);

        return cv;
    }

    public boolean modifierclient(ClientModel client) {

//        AccessLocalCredit accessLocalCredit = new AccessLocalCredit(contexte);
        boolean success = false;
        try{
            bd = accessBD.getWritableDatabase();
            ContentValues cv = new ContentValues();
            cv.put(CODECLIENT,client.getCodeclient());
            cv.put(NOM,client.getNom());
            cv.put(PRENOMS,client.getPrenoms());
            cv.put(TELEPHONE,client.getTelephone());
            cv.put(ADRESSEELECTRO,client.getEmail());
            cv.put(RESIDENCE,client.getResidence());
            cv.put(CNI,client.getCni());
            cv.put(PERMIS,client.getPermis());
            cv.put(PASSPORT,client.getPassport());
            cv.put(SOCIETE,client.getSociete());
            bd.update(CLIENT,cv,CODECLIENT+"='"+client.getCodeclient()+"'",null);
            Clientcontrolleur clientcontrolleur = Clientcontrolleur.getClientcontrolleurInstance(contexte);
            clientcontrolleur.setClient(client);
            clientcontrolleur.setMclient(client);

            success = true;
        }catch (Exception e){
            Toast.makeText(contexte, "un probleme est survenu lors de la modification", Toast.LENGTH_SHORT).show();

        }finally {
           bd.close();
        }

       return success;
    }

    public boolean supprimerclient(ClientModel client) {
        bd = accessBD.getReadableDatabase();
        bd.beginTransaction();
        boolean success = false;
        try{
            bd.delete(CLIENT,CODECLIENT +"=?",new String[]{client.getCodeclient()});
            bd.delete(CREDIT,"clientid =?", new String[]{String.valueOf(client.getId())});
            bd.delete(VERSEMENT,"clientid =?", new String[]{String.valueOf(client.getId())});
            bd.setTransactionSuccessful();
            success = true;
        }finally {
            bd.endTransaction();
        }

        return success;
    }

    public ArrayList<ClientModel> listeClients(){
        ArrayList<ClientModel> clients = new ArrayList<>();
        try {
            bd = accessBD.getReadableDatabase();

            String req = "select * from client ";
            Cursor cursor = bd.rawQuery(req, null);
            cursor.moveToFirst();
            do {
                int id = cursor.getInt(0);
                String code = cursor.getString(1);
                String nom = cursor.getString(2);
                String prenoms = cursor.getString(3);
                String telephone = cursor.getString(4);
                String email = cursor.getString(5);
                String residence = cursor.getString(6);
                String cni = cursor.getString(7);
                String permis = cursor.getString(8);
                String passport = cursor.getString(9);
                String societe = cursor.getString(10);
                Integer nbrcredit = cursor.getInt(11);
                Long totalcredit = cursor.getLong(12);
                Integer nbraccount = cursor.getInt(13);
                Long totalaccount = cursor.getLong(14);
                ClientModel client = new ClientModel(id,code,nom,prenoms,telephone,email,residence,cni,permis,passport,societe,nbrcredit,totalcredit,nbraccount,totalaccount);
                clients.add(client);
            }
            while (cursor.moveToNext());

            cursor.close();

        }catch(Exception e){
//            do nothing
        }
        return clients;
    }

    public ClientModel recupClient(String codeclt){
        ClientModel client = null;

        try {
            bd = accessBD.getReadableDatabase();
            String req = "select * from client where " + CODECLIENT + "='"+codeclt+"'";

            Cursor cursor = bd.rawQuery(req, null);
            cursor.moveToLast();
            if (!cursor.isAfterLast()) {

                int id = cursor.getInt(0);
                String code = cursor.getString(1);
                String nom = cursor.getString(2);
                String prenoms = cursor.getString(3);
                String telephone = cursor.getString(4);
                String email = cursor.getString(5);
                String residence = cursor.getString(6);
                String cni = cursor.getString(7);
                String permis = cursor.getString(8);
                String passport = cursor.getString(9);
                String societe = cursor.getString(10);
                Integer nbrcredit = cursor.getInt(11);
                Long totalcredit = cursor.getLong(12);
                Integer nbraccount = cursor.getInt(13);
                Long totalaccount = cursor.getLong(14);

                client = new ClientModel(id, code, nom,prenoms, telephone, email, residence, cni, permis,passport,societe,nbrcredit,totalcredit,nbraccount,totalaccount);

            }
            cursor.close();

        }catch (Exception e){
            //do nothing
        }
        return client;

    }

    public ClientModel recupUnClient(Integer clientid){
        ClientModel client = null;

        try {
            bd = accessBD.getReadableDatabase();
            String req = "select * from client where " + ID + "='"+clientid+"'";
            Cursor cursor = bd.rawQuery(req, null);
            cursor.moveToLast();
            if (!cursor.isAfterLast()) {

                int id = cursor.getInt(0);
                String code = cursor.getString(1);
                String nom = cursor.getString(2);
                String prenoms = cursor.getString(3);
                String telephone = cursor.getString(4);
                String email = cursor.getString(5);
                String residence = cursor.getString(6);
                String cni = cursor.getString(7);
                String permis = cursor.getString(8);
                String passport = cursor.getString(9);
                String societe = cursor.getString(10);
                Integer nbrcredit = cursor.getInt(11);
                Long totalcredit = cursor.getLong(12);
                Integer nbraccount = cursor.getInt(13);
                Long totalaccount = cursor.getLong(14);

                client = new ClientModel(id, code, nom,prenoms, telephone, email, residence, cni, permis,passport,societe,nbrcredit,totalcredit,nbraccount,totalaccount);

            }
            cursor.close();

        }catch (Exception e){
            //do nothing
        }
        return client;

    }


}
