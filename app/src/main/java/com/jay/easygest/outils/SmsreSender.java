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
import android.util.Log;

import androidx.core.app.ActivityCompat;

import com.jay.easygest.controleur.SmsSendercontrolleur;
import com.jay.easygest.model.ClientModel;
import com.jay.easygest.model.SmsnoSentModel;

import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;


public class SmsreSender  {

    private static final String SMS_RESENT = "SMS_RESENT";
    public static final String SMS_REDELIVERED = "SMS_REDELIVERED";
    public static final String SC_ADDRESS = null;
    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 2;
    private Context context;
    private Activity activity;
    private SmsnoSentModel smsnoSentModel;
    private SmsSendercontrolleur smsSendercontrolleur;
  AtomicBoolean hiden ;
  boolean success;
    public SmsreSender(Context context,Activity activity) {
        this.context = context;
        this.activity = activity;
        smsSendercontrolleur = SmsSendercontrolleur.getSmsSendercotrolleurInstance(context);
        hiden = new AtomicBoolean(false);

    }

    public void sendingUnSentMsg(ArrayList<SmsnoSentModel> smss,String expediteurName  ){

            ExecutorService executor = Executors.newSingleThreadExecutor();

            Future<ArrayList<SmsnoSentModel>> future = executor.submit(() -> {

//                ArrayList<SmsnoSentModel> noSentlists1 = new ArrayList<>();
//                ArrayList<SmsnoSentModel> noSentlists2 = new ArrayList<>();

                try {

                    for ( SmsnoSentModel sms : smss) {
//                        smsnoSentModel = sms;
                        ClientModel client = sms.getClient();
//            String destinationAdress = client.getTelephone();
                        String destinationAdress = "5556";

//                        String messageBody = expediteurName +"\n"+"\n"
//                                + client.getNom() + " "+client.getPrenoms() +"\n"
//                                +"vous avez fait un versement de "+sms.getSommeverse()+" FCFA"+" pour votre "+sms.getOperation()+"\n"
//                                +"le "+ MesOutils.convertDateToString(new Date(sms.getDateoperation()))+"\n"
//                                +"reste à payer : "+sms.getTotalreste() ;
                        checkForSmsPermissionBeforeSend(sms.getMessage(),destinationAdress );
                        resentReiceiver(sms);

                    }

                } catch (Exception e) {
//                    rslt = false;
                    Log.d("smsresender", "je suis das l'exeception ");
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

//    public boolean checkForSmsPermissionBeforeSend(String messageBody, String destinationAdress ) {
//        boolean success = false;
//        if (ActivityCompat.checkSelfPermission(context,
//                android.Manifest.permission.SEND_SMS) !=
//                PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(activity,
//                    new String[]{android.Manifest.permission.SEND_SMS},
//                    MY_PERMISSIONS_REQUEST_SEND_SMS);
//        } else {
//            success = true;
//        }
//        return success;
//    }



    private void smsSendwithInnerClass(String messageBody,String destinationAdress) {
        SmsManager sms = SmsManager.getDefault();
        PendingIntent sentPI = PendingIntent.getBroadcast(context, 2, new Intent(SMS_RESENT), PendingIntent.FLAG_IMMUTABLE);
        PendingIntent deliveredPI = PendingIntent.getBroadcast(context, 3, new Intent(SMS_REDELIVERED), PendingIntent.FLAG_IMMUTABLE);
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
