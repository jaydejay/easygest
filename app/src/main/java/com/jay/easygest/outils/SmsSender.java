package com.jay.easygest.outils;

import static androidx.core.content.ContextCompat.RECEIVER_EXPORTED;
import static androidx.core.content.ContextCompat.RECEIVER_NOT_EXPORTED;
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
import android.os.AsyncTask;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.jay.easygest.controleur.SmsSendercontrolleur;
import com.jay.easygest.model.AppKessModel;
import com.jay.easygest.model.ClientModel;
import com.jay.easygest.model.SmsnoSentModel;
import com.jay.easygest.vue.AfficherclientActivity;

import java.util.Date;


public class SmsSender {
    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 1;
    private static final String SMS_SENT = "SMS_SENT";
    public static final String SMS_DELIVERED = "SMS_DELIVERED";
    public static final String SC_ADDRESS = null;
    private final Context context;
    private final Activity activity;
    private SmsnoSentModel smsnoSentModel;

    public SmsSender(Context context, Activity activity) {
        this.context = context;
        this.activity = activity;
    }

//    public void checkForSmsPermissionBeforeSend(String messageBody, String destinationAdress ) {
//        if (ActivityCompat.checkSelfPermission(context,
//                android.Manifest.permission.SEND_SMS) !=
//                PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(activity,
//                    new String[]{android.Manifest.permission.SEND_SMS},
//                    VariablesStatique.MY_PERMISSIONS_REQUEST_SEND_SMS);
//        } else {
//
//            smsSendwithInnerClass(messageBody, destinationAdress );
//        }
//    }

    public void smsSendwithInnerClass(String messageBody, String destinationAdress,int SMS_ID ) {
        SmsManager sms = SmsManager.getDefault();
        PendingIntent sentPI = PendingIntent.getBroadcast(context, SMS_ID, new Intent(SMS_SENT), PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_ONE_SHOT);
//        PendingIntent deliveredPI = PendingIntent.getBroadcast(context, 1, new Intent(SMS_DELIVERED), PendingIntent.FLAG_IMMUTABLE);
        sms.sendTextMessage(destinationAdress, SC_ADDRESS, messageBody, sentPI, null);
    }


    public void sentReiceiver(SmsnoSentModel sms){

        BroadcastReceiver smsSentReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                if (getResultCode() == Activity.RESULT_OK  ) {

                    Intent intent_to_aff = new Intent(activity, AfficherclientActivity.class);
                    intent_to_aff.putExtra("smssentmessge"," message envoye");
                    startActivity(context, intent_to_aff, null);

                } else {
                    SmsSendercontrolleur smsSendercontrolleur = SmsSendercontrolleur.getSmsSendercotrolleurInstance(context);
                    boolean success = smsSendercontrolleur.insert(sms);
                    if(success){
                        Intent intent_to_aff = new Intent(activity, AfficherclientActivity.class);
                        intent_to_aff.putExtra("smssentmessge","message non envoye");
                        startActivity(context, intent_to_aff, null);
                    }

                }
            }
        };

        registerReceiver(context, smsSentReceiver, new IntentFilter(SMS_SENT),RECEIVER_NOT_EXPORTED);

    }

//    public void deliveredReceiver(){
//
//       BroadcastReceiver smsDeliveredReceiver = new BroadcastReceiver() {
//           @Override
//            public void onReceive(Context arg0, Intent arg1) {
//
//                switch (getResultCode()) {
//                    case Activity.RESULT_OK:
////                        Toast.makeText(getBaseContext(), "SMS délivré",Toast.LENGTH_SHORT).show();
//                        Log.d("SMS DELIVERED", "SMS délivré !");
//                        break;
//
//                    case Activity.RESULT_CANCELED:
////                        Toast.makeText(getBaseContext(), "“SMS non delivré",Toast.LENGTH_SHORT).show();
//                        Log.d("SMS DELIVERED", "SMS non délivré !");
//                        break;
//                }
//            }
//        };
//        registerReceiver(context, smsDeliveredReceiver, new IntentFilter(SMS_DELIVERED),RECEIVER_NOT_EXPORTED);
//
//    }





}
