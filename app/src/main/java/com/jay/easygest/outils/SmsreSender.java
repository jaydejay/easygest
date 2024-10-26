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
import android.content.pm.PackageManager;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.jay.easygest.R;
import com.jay.easygest.controleur.SmsSendercontrolleur;
import com.jay.easygest.model.AppKessModel;
import com.jay.easygest.model.ClientModel;
import com.jay.easygest.model.SmsnoSentModel;
import com.jay.easygest.vue.AfficherclientActivity;

import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


public class SmsreSender  {

    private static final String SMS_RESENT = "SMS_RESENT";
    public static final String SMS_REDELIVERED = "SMS_REDELIVERED";
    public static final String SC_ADDRESS = null;
    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 2;
    private Context context;
    private Activity activity;
    private SmsnoSentModel smsnoSentModel;
    private SmsSendercontrolleur smsSendercontrolleur;
    public SmsreSender(Context context,Activity activity) {
        this.context = context;
        this.activity = activity;
        smsSendercontrolleur = SmsSendercontrolleur.getSmsSendercotrolleurInstance(context);

    }

    public void sendingUnSentMsg(ArrayList<SmsnoSentModel> smss,String expediteurName  ){
        for ( SmsnoSentModel sms : smss) {
            ExecutorService executor = Executors.newSingleThreadExecutor();
            smsnoSentModel = sms;
            ClientModel client = sms.getClient();
//            String destinationAdress = client.getTelephone();
            String destinationAdress = "5556";

            String messageBody = expediteurName +"\n"+"\n"
                    + client.getNom() + " "+client.getPrenoms() +"\n"
                    +"vous avez fait un versement de "+sms.getSommeverse()+" FCFA"+" pour votre "+sms.getOperation()+"\n"
                    +"le "+ MesOutils.convertDateToString(new Date(sms.getDateoperation()))+"\n"
                    +"reste à payer : "+sms.getTotalreste() ;

            Future<Boolean> future = executor.submit(() -> {
                boolean success;
                try {
                    checkForSmsPermissionBeforeSend(messageBody,destinationAdress );
                    success = true;

                } catch (Exception e) {
                    success = false;
                }
               return success ;
            });

            try {
                boolean result = future.get();
                Log.d("smsresender", "sendingUnSentMsg: result "+result);
            } catch (Exception e) {
                //do nothing
            }
            executor.shutdown();
        }

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
        PendingIntent sentPI = PendingIntent.getBroadcast(context, 2, new Intent(SMS_RESENT), PendingIntent.FLAG_IMMUTABLE);
        PendingIntent deliveredPI = PendingIntent.getBroadcast(context, 3, new Intent(SMS_REDELIVERED), PendingIntent.FLAG_IMMUTABLE);
        sms.sendTextMessage(destinationAdress, SC_ADDRESS, messageBody, sentPI, deliveredPI);
    }

    public void resentReiceiver(){
        BroadcastReceiver smsSentReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (getResultCode() == Activity.RESULT_OK) {
                    smsSendercontrolleur.delete(smsnoSentModel);
                }else {
                    //do nothing
                }
            }
        };
        registerReceiver(context, smsSentReceiver, new IntentFilter(SMS_RESENT),RECEIVER_NOT_EXPORTED);
    }


}
