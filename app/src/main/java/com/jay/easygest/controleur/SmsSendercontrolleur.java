package com.jay.easygest.controleur;

import android.content.Context;

import com.jay.easygest.model.SmsnoSentModel;
import com.jay.easygest.outils.AccessLocalSmsSender;

import java.util.ArrayList;

public class SmsSendercontrolleur {


    private static SmsSendercontrolleur smsSendercotrolleurInstance = null;
    private static AccessLocalSmsSender accessLocalSmsSender;
    public SmsSendercontrolleur() {
        super();
    }

    public static SmsSendercontrolleur getSmsSendercotrolleurInstance(Context context){
        if (SmsSendercontrolleur.smsSendercotrolleurInstance == null){
            SmsSendercontrolleur.smsSendercotrolleurInstance = new SmsSendercontrolleur();
             accessLocalSmsSender = new AccessLocalSmsSender(context);
        }
        return smsSendercotrolleurInstance;
    }


    public boolean insert(SmsnoSentModel sms){
       return  accessLocalSmsSender.insert(sms);
    }
    public boolean delete(SmsnoSentModel sms){
        return accessLocalSmsSender.delete(sms);
    }

    public ArrayList<SmsnoSentModel> getSmsnoSentList(){return accessLocalSmsSender.listSmsnoSent();}


}
