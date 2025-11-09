package com.jay.easygest.outils;

import static androidx.core.content.ContextCompat.RECEIVER_NOT_EXPORTED;
import static androidx.core.content.ContextCompat.registerReceiver;
import static androidx.core.content.ContextCompat.startActivity;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.telephony.SmsManager;

import com.jay.easygest.controleur.SmsSendercontrolleur;
import com.jay.easygest.model.SmsnoSentModel;
import com.jay.easygest.vue.AfficherclientActivity;


public class SmsSender {
    private static final String SMS_SENT = "SMS_SENT";
    public static final String SC_ADDRESS = null;
    private final Context context;
    private final Activity activity;

    public SmsSender(Context context, Activity activity) {
        this.context = context;
        this.activity = activity;
    }


    public void smsSendwithInnerClass(String messageBody, String destinationAdress,int SMS_ID ) {
        SmsManager sms = SmsManager.getDefault();
        Intent sent_intent = new Intent(SMS_SENT);
        sent_intent.putExtra("sms_id",SMS_ID);
        PendingIntent sentPI = PendingIntent.getBroadcast(context, SMS_ID ,sent_intent , PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
        sms.sendTextMessage(destinationAdress, SC_ADDRESS, messageBody, sentPI, null);
    }

    public void sentReiceiver(SmsnoSentModel sms){
        BroadcastReceiver smsSentReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
//
               int sms_id  =intent.getIntExtra("sms_id",1);
                if (getResultCode() == Activity.RESULT_OK && sms.getSmsid() == sms_id ) {
                    Intent intent_to_aff = new Intent(activity, AfficherclientActivity.class);
                    intent_to_aff.putExtra("smssentmessge"," client notifier");
                    startActivity(context, intent_to_aff, null);
                }

                if (getResultCode() == SmsManager.RESULT_ERROR_GENERIC_FAILURE && sms.getSmsid() == sms_id) {

                    SmsSendercontrolleur smsSendercontrolleur = SmsSendercontrolleur.getSmsSendercotrolleurInstance(context);
                    boolean success = smsSendercontrolleur.insert(sms);
                    if(success){
                        Intent intent_to_aff = new Intent(activity, AfficherclientActivity.class);
                        intent_to_aff.putExtra("smssentmessge","message non envoye,un probleme de reseau ou vos sms sont épuisés");
                        startActivity(context, intent_to_aff, null);
                    }
                }

            }
        };
        registerReceiver(context, smsSentReceiver, new IntentFilter(SMS_SENT),RECEIVER_NOT_EXPORTED);

    }

    public void sentReiceiverGeneric(SmsnoSentModel sms, Intent intent_to){
        BroadcastReceiver smsSentReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
//
                int sms_id  =intent.getIntExtra("sms_id",1);
                if (getResultCode() == Activity.RESULT_OK && sms.getSmsid() == sms_id ) {
                    intent_to.putExtra("smssentmessge"," client notifier");

                    startActivity(context, intent_to, null);
                }

                if (getResultCode() == SmsManager.RESULT_ERROR_GENERIC_FAILURE && sms.getSmsid() == sms_id) {
//                    SmsSendercontrolleur smsSendercontrolleur = SmsSendercontrolleur.getSmsSendercotrolleurInstance(context);
//                    boolean success = smsSendercontrolleur.insert(sms);
//                    if(success){
                        intent_to.putExtra("smssentmessge","message non envoye,un probleme de reseau ou vos sms sont épuisés");
                        startActivity(context, intent_to, null);
//                    }
                }

            }
        };
        registerReceiver(context, smsSentReceiver, new IntentFilter(SMS_SENT),RECEIVER_NOT_EXPORTED);

    }


}
