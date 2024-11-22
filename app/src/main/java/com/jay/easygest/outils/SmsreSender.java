package com.jay.easygest.outils;


import static androidx.core.content.ContextCompat.RECEIVER_NOT_EXPORTED;
import static androidx.core.content.ContextCompat.registerReceiver;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.telephony.SmsManager;

import androidx.core.app.ActivityCompat;

import com.jay.easygest.controleur.SmsSendercontrolleur;
import com.jay.easygest.model.ClientModel;
import com.jay.easygest.model.SmsnoSentModel;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;


public class SmsreSender  {

    private static final String SMS_RESENT = "SMS_RESENT";
    public static final String SC_ADDRESS = null;
    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 2;
    private final Context context;
    private final Activity activity;
    private final SmsSendercontrolleur smsSendercontrolleur;
  AtomicBoolean hiden ;
    public SmsreSender(Context context,Activity activity) {
        this.context = context;
        this.activity = activity;
        smsSendercontrolleur = SmsSendercontrolleur.getSmsSendercotrolleurInstance(context);
        hiden = new AtomicBoolean(false);

    }

    public void sendingUnSentMsg(ArrayList<SmsnoSentModel> smss){

            ExecutorService executor = Executors.newSingleThreadExecutor();

            Future<ArrayList<SmsnoSentModel>> future = executor.submit(() -> {

                try {

                    for ( SmsnoSentModel sms : smss) {

                        ClientModel client = sms.getClient();
                        String destinationAdress = client.getTelephone();
                        checkForSmsPermissionBeforeSend(sms.getMessage(),destinationAdress );
                        resentReiceiver(sms);

                    }

                } catch (Exception e) {
//                    rslt = false;

                }

               return smss ;
            });

            try {
                 ArrayList<SmsnoSentModel> rs= future.get();

            } catch (Exception e) {
                //do nothing
            }

        executor.shutdown();


    }

    public void checkForSmsPermissionBeforeSend(String messageBody, String destinationAdress ) {
        if (ActivityCompat.checkSelfPermission(context,
                android.Manifest.permission.SEND_SMS) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity,
                    new String[]{android.Manifest.permission.SEND_SMS},
                    MY_PERMISSIONS_REQUEST_SEND_SMS);
        } else {
            smsSendwithInnerClass(messageBody,destinationAdress );
        }
    }

    private void smsSendwithInnerClass(String messageBody,String destinationAdress) {
        SmsManager sms = SmsManager.getDefault();
        PendingIntent sentPI = PendingIntent.getBroadcast(context, 2, new Intent(SMS_RESENT), PendingIntent.FLAG_IMMUTABLE| PendingIntent.FLAG_CANCEL_CURRENT);
        sms.sendTextMessage(destinationAdress, SC_ADDRESS, messageBody, sentPI, null);
    }

    public void resentReiceiver(SmsnoSentModel sms){
        BroadcastReceiver smsreSentReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {

                if (getResultCode() == Activity.RESULT_OK) {
                    smsSendercontrolleur.delete(sms);
                }
            }
        };
        registerReceiver(context, smsreSentReceiver, new IntentFilter(SMS_RESENT),RECEIVER_NOT_EXPORTED);

    }


}
