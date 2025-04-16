package com.jay.easygest.outils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.Switch;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Random;



public abstract  class MesOutils {

    public static String versionapp ="1.0";
    private AccessLocalAppKes accessLocalAppKes;

    public enum Level {
        LOW,
        MEDIUM,
        HIGH,
        FREE,
        PERMANENT,
        INCONNU
    }

    public static Date convertStringToDate(String ladate){

        String dateFormat = "dd-MM-yyyy";
        SimpleDateFormat dateFormater = new SimpleDateFormat(dateFormat, Locale.FRANCE);
        Date date ;
        try {
          date = dateFormater.parse(ladate);
        } catch (ParseException e) {
            date = null ;
        }
        return date;
    }

    public static String convertDateToString(Date ladate){
        SimpleDateFormat date = new SimpleDateFormat("dd-MM-yyyy",Locale.FRANCE);
        return date.format(ladate);
    }

    public static synchronized byte[] convertBitmapToByterry(Bitmap bitmap){
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,40,byteArrayOutputStream);

        return byteArrayOutputStream.toByteArray();
    }

    public static synchronized  Bitmap convertByterryToBitmap(byte[] bytearr){

        return BitmapFactory.decodeByteArray(bytearr,0,bytearr.length);
    }


    public static String generateurcodeclt(String base){
        Random random = new Random();
        int chif1 = random.nextInt(10);
        int chif2 = random.nextInt(10);
        int chif3 = random.nextInt(10);
        int chif4 = random.nextInt(10);

        String random_int = chif1+""+chif2+""+chif3+""+chif4;

        return base+""+random_int;
    }


    public static int smsidnumbergenerator(){
        Random random = new Random();
        int chif1 = random.nextInt(10);
        int chif2 = random.nextInt(10);
        int chif3 = random.nextInt(10);
        int chif4 = random.nextInt(10);
        String numletter = chif1+""+chif2+""+chif3+""+chif4;
        return Integer.parseInt(numletter);
    }

    public static String apppnumbergenerator(){
        Random random = new Random();
        int chif1 = random.nextInt(10);
        int chif2 = random.nextInt(10);
        int chif3 = random.nextInt(10);
        int chif4 = random.nextInt(10);
        String numletter = chif1+""+chif2+""+chif3+""+chif4;
//        return Integer.parseInt(numletter);
        return numletter;
    }

    public static String apppkeygenerator(String appnumber){

        int c1 =Integer.parseInt(String.valueOf(appnumber.charAt(0)));
        int c2 =Integer.parseInt(String.valueOf(appnumber.charAt(1)));
        int c3 =Integer.parseInt(String.valueOf(appnumber.charAt(2)));
        int c4 =Integer.parseInt(String.valueOf(appnumber.charAt(3)));

        int n =5;
        //initialisation des trois zones du code a 5 lettres
        StringBuilder stringBuilder1 = new StringBuilder(n);
        StringBuilder stringBuilder2 = new StringBuilder(n);
        StringBuilder stringBuilder3 = new StringBuilder(n);

        // generation de la premiere zone
        // la premiere lettre est determiner par l'index du premeier number appnuber le redte aleatoire

        stringBuilder1.append(VariablesStatique.STR.charAt(c1));
        for (int i =1; i < n; i++) {
            int index = (int)(VariablesStatique.STR.length() * Math.random());
            stringBuilder1.append(VariablesStatique.STR.charAt(index));
        }

        // generation de la deuxieme zone
        // la deuxieme lettre est determiner par l'index du premeier number appnuber plus  deuxieme number appnuber  le redte aleatoire

        stringBuilder2.append(VariablesStatique.STR.charAt((int)(VariablesStatique.STR.length() * Math.random())));
        stringBuilder2.append(VariablesStatique.STR.charAt(c1+c2));
        for (int i = 2; i < n-1; i++) {
            int index = (int)(VariablesStatique.STR.length() * Math.random());
            stringBuilder2.append(VariablesStatique.STR.charAt(index));
        }
        stringBuilder2.append("3");

        // generation de la troisieme zone
        // la qutrieme lettre est determiner par l'index du 2  plus  3 number appnuber

        for (int i = 0; i < n-2; i++) {
            int index = (int)(VariablesStatique.STR.length() * Math.random());
            stringBuilder3.append(VariablesStatique.STR.charAt(index));
        }
        stringBuilder3.append(VariablesStatique.STR.charAt(c3+c4));
        stringBuilder3.append(VariablesStatique.STR.charAt((int)(VariablesStatique.STR.length() * Math.random())));



        return stringBuilder1+"-"+stringBuilder2+"-"+stringBuilder3;
    }

    public static String mdpgenerator(){
        int n=8;
        StringBuilder stringBuilder = new StringBuilder(n);

        for (int i = 0; i < n; i++) {
            int index = (int)(VariablesStatique.STR_MDP.length() * Math.random());
            stringBuilder.append(VariablesStatique.STR_MDP.charAt(index));
        }
        return stringBuilder.toString();
    }

    public  static long getSppressionDate(long value){
        SimpleDateFormat anneef = new SimpleDateFormat("yyyy",Locale.FRANCE);
        SimpleDateFormat moisf = new SimpleDateFormat("MM",Locale.FRANCE);
        SimpleDateFormat jourf = new SimpleDateFormat("dd",Locale.FRANCE);
        SimpleDateFormat datef = new SimpleDateFormat("dd-MM-yyyy",Locale.FRANCE);
        Date date = new Date(value);
        String annee = anneef.format(date);
        String mois = moisf.format(date);
        String jour = jourf.format(date);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Integer.parseInt(annee),Integer.parseInt(mois),Integer.parseInt(jour));
        calendar.add(Calendar.MONTH,6);
        return convertStringToDate(datef.format(calendar.getTime())).getTime();
    }

    public static boolean retrieveAppNumber(String appKey, String appnumber){

        int app_number1 = Integer.parseInt(String.valueOf(appnumber.charAt(0)));
        int app_number2 = Integer.parseInt(String.valueOf(appnumber.charAt(1)));
        int app_number3 = Integer.parseInt(String.valueOf(appnumber.charAt(2)));
        int app_number4 = Integer.parseInt(String.valueOf(appnumber.charAt(3)));

        String[] zone_array = appKey.split("-");
        String zone1 = zone_array[0];
        int c1 = VariablesStatique.STR.indexOf(zone1.charAt(0)) ;

        String zone2 = zone_array[1];
        int c2 = VariablesStatique.STR.indexOf(zone2.charAt(1))-c1 ;

        String zone3 = zone_array[2];
        int c3 = VariablesStatique.STR.indexOf(zone3.charAt(3)) ;

        boolean is_app_number_same = false;

        if (c1 == app_number1  ){

            if (c2 == app_number2 ){

                if (c3  == app_number3 + app_number4){
                    is_app_number_same = true;
                }
            }
        }


        return is_app_number_same;
    }

    public static long getDureeLicence(String key ){
        Level level = getLicenceLevel(key);
        Date ladate = new Date();
        Timestamp timestamp = new Timestamp(ladate.getTime());
        long millisecondsToAdd =0;
        long duree_licence = 0;
        switch(level) {
            case LOW:
                // code block  licence 2 mois
                 millisecondsToAdd = 3600L * 1000 * 24 * 30 * 2;
                 duree_licence = timestamp.getTime() + millisecondsToAdd;
                break;
            case MEDIUM:
                // code block 6 mois
                millisecondsToAdd = 3600L * 1000 * 24 * 30*6;
                duree_licence = timestamp.getTime() + millisecondsToAdd;
                break;
            case HIGH:
                // code block 12 mois
                millisecondsToAdd = 3600L * 1000 * 24 * 30*12;
                duree_licence = timestamp.getTime() + millisecondsToAdd;
                break;
            case PERMANENT:
                // code block 49 ans
                millisecondsToAdd = 3600L * 1000 * 24 * 30 * 12 * 49;
                duree_licence = timestamp.getTime() + millisecondsToAdd;
                break;
            case FREE:
                // code block 10 jours
                millisecondsToAdd = 3600 * 1000 * 24 * 10;
                duree_licence = timestamp.getTime() + millisecondsToAdd;
                break;
            default:
                // code block 49 ans

                duree_licence =0;
        }
        return duree_licence;
    }

    public static Level getLicenceLevel(String key){
        Level level = null ;

        String[] zone_array = key.split("-");
        if (zone_array.length == 3 ){
            String zone1 = zone_array[0];
            String zone2 = zone_array[1];
            String zone3 = zone_array[2];
            if (zone1.length() == 5 && zone2.length() == 5 && zone3.length() == 5  ){
//                int low = Integer.parseInt(String.valueOf(zone1.charAt(2)));
//                int medium = Integer.parseInt(String.valueOf(zone2.charAt(0)));
//                int free = Integer.parseInt(String.valueOf(zone2.charAt(4)));
//                int hight = Integer.parseInt(String.valueOf(zone3.charAt(1)));
//                int permanent = Integer.parseInt(String.valueOf(zone3.charAt(4)));

                String low = String.valueOf(zone1.charAt(2));
                String medium = String.valueOf(zone2.charAt(0));
                String free = String.valueOf(zone2.charAt(4));
                String hight = String.valueOf(zone3.charAt(1));
                String permanent = String.valueOf(zone3.charAt(4));

                if (low.equals("1") ){
                    level = Level.LOW;
                }else {
                    if (medium.equals("7") ){
                        level = Level.MEDIUM;
                    } else {
                        if (free.equals("3") ){
                            level = Level.FREE;
                        }else {
                            if (hight.equals("5") ){
                                level = Level.HIGH;
                            }else {
                                if (permanent.equals("9")){
                                    level = Level.PERMANENT;
                                }else {
                                    level = Level.INCONNU;
                                }

                            }
                        }
                    }
                }
            }

        }


        return level;
    }


}
