package com.jay.easygest.controleur;

import android.content.Context;

import com.jay.easygest.model.AppKessModel;
import com.jay.easygest.model.UserModel;
import com.jay.easygest.outils.AccessLocal;
import com.jay.easygest.outils.MesOutils;

import java.util.Date;

public final class Usercontrolleur {

    private static  Usercontrolleur usercontrolleurInstance = null;
    private UserModel user;
    private static AccessLocal accessLocal;

    private String proprietaireMdpInit;

    /**
     * constructeur
     */
    private Usercontrolleur(){
        super();
    }

    public static Usercontrolleur getUsercontrolleurInstance(Context contexte){
        if(Usercontrolleur.usercontrolleurInstance == null){
            Usercontrolleur.usercontrolleurInstance = new Usercontrolleur();
            accessLocal = new AccessLocal(contexte);
        }
        return usercontrolleurInstance;
    }

    public UserModel getUser() {
        return user;
    }

    public void setUser(UserModel user) {
        this.user = user;
    }

    public String getProprietaireMdpInit() {
        return proprietaireMdpInit;
    }

    public void setProprietaireMdpInit(String proprietaireMdpInit) {
        this.proprietaireMdpInit = proprietaireMdpInit;
    }

    public void initMpdp(UserModel utilisateur){
        String mdp = MesOutils.mdpgenerator();
        UserModel user = new UserModel(utilisateur.getId(),utilisateur.getUsername(),mdp,utilisateur.getDateInscription(),utilisateur.getStatus(),true,0);
        accessLocal.modifierUtilisateur(user);
        this.setProprietaireMdpInit(mdp);
    }

    public boolean creerUser(String username, String password, AppKessModel appKess, String owner, String code_base, String telephone, String email){
        user = new UserModel(username,password,new Date(),1,true,0);
        AppKessModel appKessModel = new AppKessModel(appKess.getAppnumber(),appKess.getApppkey(),owner,code_base,telephone,email);
       return accessLocal.ajouterUtilisateur(user,appKessModel);
    }

    public void modifierUser(UserModel user){accessLocal.modifierUtilisateur(user);}

    public UserModel recupProprietaire(){
        return  accessLocal.recupProprietaire();
    }

    public UserModel recupAdministrateur(){
        return accessLocal.recupAdministrateur();
    }


    public boolean isAuthenticated(String username, String password){
        return accessLocal.isAuthenticated(username, password);
    }

    public Integer nbrUtilisateur(){
        return  accessLocal.nbrUtilisateurs();
    }

    public void desactiverProprietaire(){
        accessLocal.desactiverProprietaire();
    }

    public void activerProprietaire(){
//        String mdp = MesOutils.mdpgenerator();
        accessLocal.activerProprietaire();
//        this.setProprietaireMdpInit(mdp);
    }

    public void desactiverAdministrateur(){
        accessLocal.desactiverAdministrateur();
    }

    public void activerAdministrateur(){
        accessLocal.activerAdministrateur();
    }

    /**
     * permet de d'authentifier l'application pour degele
     * @param proprietaire le proprietaire
     * @param cleproduit la cle du produit
     * @return vrai si authentification reussi sinon faux
     */
    public boolean authApp(String proprietaire, String cleproduit) {

           if (accessLocal.authapp(proprietaire,cleproduit)){
//               String mdp = MesOutils.mdpgenerator();
               accessLocal.activerAdministrateur();
               accessLocal.activerProprietaire();
//               this.setProprietaireMdpInit(mdp);
           }

        return accessLocal.authapp(proprietaire,cleproduit);
    }

    public String[] getAppCredentials(){
        return accessLocal.appCredential();
    }


}
