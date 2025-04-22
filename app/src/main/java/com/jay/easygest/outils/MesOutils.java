package com.jay.easygest.outils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Random;



public abstract  class MesOutils {



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
        stringBuilder1.append(VariablesStatique.STR.charAt(c1+10));
        stringBuilder1.append(VariablesStatique.STR.charAt(c1+20));
        stringBuilder1.append(VariablesStatique.STR.charAt(c1+13));
        stringBuilder1.append(VariablesStatique.STR.charAt(c1+17));

        // generation de la deuxieme zone
        // la deuxieme lettre est determiner par l'index du premeier number appnuber plus  deuxieme number appnuber  le redte aleatoire

        stringBuilder2.append(VariablesStatique.STR.charAt(c2+8));
        stringBuilder2.append(VariablesStatique.STR.charAt(c1+c2));
        stringBuilder2.append(VariablesStatique.STR.charAt(c2+15));
        stringBuilder2.append(VariablesStatique.STR.charAt(c2+22));
        stringBuilder2.append("3");

        // generation de la troisieme zone
        // la qutrieme lettre est determiner par l'index du 2  plus  3 number appnuber

        stringBuilder3.append(VariablesStatique.STR.charAt(c3+26));
        stringBuilder3.append(VariablesStatique.STR.charAt(c3+5));
        stringBuilder3.append(VariablesStatique.STR.charAt(c3+14));
        stringBuilder3.append(VariablesStatique.STR.charAt(c3+c4));//
        stringBuilder3.append(VariablesStatique.STR.charAt(c3+23));



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
        int c3 = VariablesStatique.STR.indexOf(zone3.charAt(3));

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
        long duree_licence;
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
                // code block

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


    public static boolean isLicenceExpired(String[] appcredentials){
        Timestamp maintenant  = new Timestamp(new Date().getTime());
        Timestamp duree_licence  = new Timestamp(Long.parseLong(appcredentials[6]));

        return maintenant.after(duree_licence);

    }


    public static boolean verifyApppkeyFree(String appkey, String appnumber){
        String[] zone_array = appkey.split("-");
        String zone1 = zone_array[0];
        String zone2 = zone_array[1];
        String zone3 = zone_array[2];

        boolean is_appkey_free = false;

        if (zone1.length() == 5 && zone2.length() == 5 && zone3.length() == 5  ){

            int c1 =Integer.parseInt(String.valueOf(appnumber.charAt(0)));
            int c2 =Integer.parseInt(String.valueOf(appnumber.charAt(1)));
            int c3 =Integer.parseInt(String.valueOf(appnumber.charAt(2)));
            int c4 =Integer.parseInt(String.valueOf(appnumber.charAt(3)));

            int z11 = VariablesStatique.STR.indexOf(zone1.charAt(0)) ;
            int z12 = VariablesStatique.STR.indexOf(zone1.charAt(1)) ;
            int z13 = VariablesStatique.STR.indexOf(zone1.charAt(2)) ;
            int z14 = VariablesStatique.STR.indexOf(zone1.charAt(3)) ;
            int z15 = VariablesStatique.STR.indexOf(zone1.charAt(4)) ;

            if (z11 == c1 && z12 == c1+10 && z13 == c1+20 && z14 == c1+13 && z15 == c1+17  ){

                int z21 = VariablesStatique.STR.indexOf(zone2.charAt(0)) ;
                int z22 = VariablesStatique.STR.indexOf(zone2.charAt(1)) ;
                int z23 = VariablesStatique.STR.indexOf(zone2.charAt(2)) ;
                int z24 = VariablesStatique.STR.indexOf(zone2.charAt(3)) ;
                int z25 = 3 ;

                if (z21 == c2+8 && z22 == c1+c2 && z23 == c2+15 && z24 == c2+22 && z25 == 3){

                    int z31 = VariablesStatique.STR.indexOf(zone3.charAt(0)) ;
                    int z32 = VariablesStatique.STR.indexOf(zone3.charAt(1)) ;
                    int z33 = VariablesStatique.STR.indexOf(zone3.charAt(2)) ;
                    int z34 = VariablesStatique.STR.indexOf(zone3.charAt(3)) ;
                    int z35 = VariablesStatique.STR.indexOf(zone3.charAt(4)) ;

                    if (z31 == c3+26 && z32 == c3+5 && z33 == c3+14 && z34 == c3+c4 && z35 == c3+23){
                        is_appkey_free = true;
                    }

                }

            }
        }
        return is_appkey_free;
    }


    public static boolean verifyApppkeyLow(String appkey, String appnumber){
        String[] zone_array = appkey.split("-");
        String zone1 = zone_array[0];
        String zone2 = zone_array[1];
        String zone3 = zone_array[2];
        boolean is_appkey_low = false;

        if (zone1.length() == 5 && zone2.length() == 5 && zone3.length() == 5  ){

            int c1 =Integer.parseInt(String.valueOf(appnumber.charAt(0)));
            int c2 =Integer.parseInt(String.valueOf(appnumber.charAt(1)));
            int c3 =Integer.parseInt(String.valueOf(appnumber.charAt(2)));
            int c4 =Integer.parseInt(String.valueOf(appnumber.charAt(3)));

            int z11 = VariablesStatique.STR.indexOf(zone1.charAt(0)) ;
            int z12 = VariablesStatique.STR.indexOf(zone1.charAt(1)) ;
            int z13 = 1 ;
            int z14 = VariablesStatique.STR.indexOf(zone1.charAt(3)) ;
            int z15 = VariablesStatique.STR.indexOf(zone1.charAt(4)) ;

            if (z11 == c1 && z13 == 1 && z15 == c1+7  ){
                if (z14 == z12+3 || z14 == z12 - 10  ){

                    int z21 = VariablesStatique.STR.indexOf(zone2.charAt(0)) ;
                    int z22 = VariablesStatique.STR.indexOf(zone2.charAt(1)) ;
                    int z23 = VariablesStatique.STR.indexOf(zone2.charAt(2)) ;
                    int z24 = VariablesStatique.STR.indexOf(zone2.charAt(3)) ;
                    int z25 = VariablesStatique.STR.indexOf(zone2.charAt(4)) ;

                    if (z21 == c2+4 && z22 == c1+c2 && z25 ==c2+16 ){

                        if (z24 == z23+10 || z24 == z23-10){

                            int z31 = VariablesStatique.STR.indexOf(zone3.charAt(0)) ;
                            int z32 = VariablesStatique.STR.indexOf(zone3.charAt(1)) ;
                            int z33 = VariablesStatique.STR.indexOf(zone3.charAt(2)) ;
                            int z34 = VariablesStatique.STR.indexOf(zone3.charAt(3)) ;
                            int z35 = VariablesStatique.STR.indexOf(zone3.charAt(4)) ;

                            if ( z33 == c3+19 && z34 == c3+c4 && z35 == c3+21){

                                if (  z32 == z31 + 14 || z32 == z31-10){

                                    is_appkey_low = true;
                                }

                            }
                        }


                    }
                }


            }
        }


        return is_appkey_low;
    }


    public static boolean verifyApppkeyMedium(String appkey, String appnumber){
        String[] zone_array = appkey.split("-");
        String zone1 = zone_array[0];
        String zone2 = zone_array[1];
        String zone3 = zone_array[2];

        boolean is_appkey_medium = false;

        if (zone1.length() == 5 && zone2.length() == 5 && zone3.length() == 5  ){
            int c1 =Integer.parseInt(String.valueOf(appnumber.charAt(0)));
            int c2 =Integer.parseInt(String.valueOf(appnumber.charAt(1)));
            int c3 =Integer.parseInt(String.valueOf(appnumber.charAt(2)));
            int c4 =Integer.parseInt(String.valueOf(appnumber.charAt(3)));

            int z11 = VariablesStatique.STR.indexOf(zone1.charAt(0)) ;
            int z12 = VariablesStatique.STR.indexOf(zone1.charAt(1)) ;
            int z13 = VariablesStatique.STR.indexOf(zone1.charAt(2)) ;
            int z14 = VariablesStatique.STR.indexOf(zone1.charAt(3)) ;
            int z15 = VariablesStatique.STR.indexOf(zone1.charAt(4)) ;

            if (z11 == c1 && z12 == c1+25 && z14 == c1+1 ){

                if (z15 == z13-11 || z15 == z13 + 9  ){

                    int z21 = 7 ;
                    int z22 = VariablesStatique.STR.indexOf(zone2.charAt(1)) ;
                    int z23 = VariablesStatique.STR.indexOf(zone2.charAt(2)) ;
                    int z24 = VariablesStatique.STR.indexOf(zone2.charAt(3)) ;
                    int z25 = VariablesStatique.STR.indexOf(zone2.charAt(4)) ;

                    if (z21 == 7 && z22 == c1+c2 && z23 == c3+20 ){
                        if (z25 == z24-9 || z25 == z24 + 7  ){

                            int z31 = VariablesStatique.STR.indexOf(zone3.charAt(0)) ;
                            int z32 = VariablesStatique.STR.indexOf(zone3.charAt(1)) ;
                            int z33 = VariablesStatique.STR.indexOf(zone3.charAt(2)) ;
                            int z34 = VariablesStatique.STR.indexOf(zone3.charAt(3)) ;
                            int z35 = VariablesStatique.STR.indexOf(zone3.charAt(4)) ;

                            if ( z33 == c4+24 && z34 == c3+c4 && z35 == c2+20){
                                if (z32 == z31-15 || z32 == z31+12 ){
                                    is_appkey_medium = true;
                                }

                            }
                        }


                    }
                }

            }
        }

        return is_appkey_medium;
    }

    public static boolean verifyApppkeyHigh(String appkey, String appnumber){
        String[] zone_array = appkey.split("-");
        String zone1 = zone_array[0];
        String zone2 = zone_array[1];
        String zone3 = zone_array[2];

        boolean is_appkey_high = false;

        if (zone1.length() == 5 && zone2.length() == 5 && zone3.length() == 5  ){

            int c1 =Integer.parseInt(String.valueOf(appnumber.charAt(0)));
            int c2 =Integer.parseInt(String.valueOf(appnumber.charAt(1)));
            int c3 =Integer.parseInt(String.valueOf(appnumber.charAt(2)));
            int c4 =Integer.parseInt(String.valueOf(appnumber.charAt(3)));

            int z11 = VariablesStatique.STR.indexOf(zone1.charAt(0)) ;
            int z12 = VariablesStatique.STR.indexOf(zone1.charAt(1)) ;
            int z13 = VariablesStatique.STR.indexOf(zone1.charAt(2)) ;
            int z14 = VariablesStatique.STR.indexOf(zone1.charAt(3)) ;
            int z15 = VariablesStatique.STR.indexOf(zone1.charAt(4)) ;

//            if (z11 == c1 && z12 == c1+15 && z13 == c2+17 && z14 == c1+19 && z15 == c4+3 ){
            if (z11 == c1 && z13 == c2+17 && z14 == c1+19 ){

                if (z15 == z12-7 || z15 == z12 + 13  ){


                    int z21 = VariablesStatique.STR.indexOf(zone2.charAt(0)) ;
                    int z22 = VariablesStatique.STR.indexOf(zone2.charAt(1)) ;
                    int z23 = VariablesStatique.STR.indexOf(zone2.charAt(2)) ;
                    int z24 = VariablesStatique.STR.indexOf(zone2.charAt(3)) ;
                    int z25 = VariablesStatique.STR.indexOf(zone2.charAt(4)) ;


//                if (z21 == c3+c2 && z22 == c1+c2 && z23 == c2+5 && z24 == c1+11 && z25 == c4+c2){
                    if ( z22 == c1+c2 && z24 == c1+11 && z25 == c4+c2){
                        if (z23 == z21-16 || z23 == z21 + 5  ){

                            int z31 = VariablesStatique.STR.indexOf(zone3.charAt(0)) ;
                            int z32 = 5;
                            int z33 = VariablesStatique.STR.indexOf(zone3.charAt(2)) ;
                            int z34 = VariablesStatique.STR.indexOf(zone3.charAt(3)) ;
                            int z35 = VariablesStatique.STR.indexOf(zone3.charAt(4)) ;

//                    if (z31 == c4+25 && z32 == 5 && z34 == c3+c4 && z35 == c3+13){
                            if (z32 == 5 && z34 == c3+c4 && z35 == c3+13){

                                if (z31 == z33-6 || z31 == z33+6  ){
                                    is_appkey_high = true;
                                }


                            }
                        }

                    }

                }


            }
        }

        return is_appkey_high;
    }


    public static boolean verifyApppkeyPermanent(String appkey, String appnumber){
        String[] zone_array = appkey.split("-");
        String zone1 = zone_array[0];
        String zone2 = zone_array[1];
        String zone3 = zone_array[2];

        boolean is_appkey_permanent = false;

        if (zone1.length() == 5 && zone2.length() == 5 && zone3.length() == 5  ){
            int c1 =Integer.parseInt(String.valueOf(appnumber.charAt(0)));
            int c2 =Integer.parseInt(String.valueOf(appnumber.charAt(1)));
            int c3 =Integer.parseInt(String.valueOf(appnumber.charAt(2)));
            int c4 =Integer.parseInt(String.valueOf(appnumber.charAt(3)));

            int z11 = VariablesStatique.STR.indexOf(zone1.charAt(0)) ;
            int z12 = VariablesStatique.STR.indexOf(zone1.charAt(1)) ;
            int z13 = VariablesStatique.STR.indexOf(zone1.charAt(2)) ;
            int z14 = VariablesStatique.STR.indexOf(zone1.charAt(3)) ;
            int z15 = VariablesStatique.STR.indexOf(zone1.charAt(4)) ;

            if (z11 == c1 && z12 == c1+14 && z13 == c1+20 && z14 == c4+7 && z15 == c3+15 ){

                int z21 = VariablesStatique.STR.indexOf(zone2.charAt(0)) ;
                int z22 = VariablesStatique.STR.indexOf(zone2.charAt(1)) ;
                int z23 = VariablesStatique.STR.indexOf(zone2.charAt(2)) ;
                int z24 = VariablesStatique.STR.indexOf(zone2.charAt(3)) ;
                int z25 = VariablesStatique.STR.indexOf(zone2.charAt(4)) ;

                if ( z21 == c1+c4 && z22 == c1+c2 && z23 == c3+c3 && z24 == c4+c4 && z25 == c2+c2 ){

                    int z31 = VariablesStatique.STR.indexOf(zone3.charAt(0)) ;
                    int z32 = VariablesStatique.STR.indexOf(zone3.charAt(1)) ;
                    int z33 = VariablesStatique.STR.indexOf(zone3.charAt(2)) ;
                    int z34 = VariablesStatique.STR.indexOf(zone3.charAt(3)) ;
                    int z35 = 9 ;


                    if (z31 == c1+c4 && z32 == c3+c4+10 && z33 == c1+c2+15 && z34 == c3+c4 && z35 == 9 ){
                        is_appkey_permanent = true;
                    }

                }

            }
        }

        return is_appkey_permanent;
    }

    public static boolean isKeyvalide(String appkey, String appnumber){

      Level level =  MesOutils.getLicenceLevel(appkey);
      boolean success ;

        switch(level) {
            case LOW:
                success =  verifyApppkeyLow(appkey,appnumber);
                break;
            case MEDIUM:
                // code block 6 mois
                success = verifyApppkeyMedium(appkey,appnumber);
                break;
            case HIGH:
                // code block 12 mois
                success = verifyApppkeyHigh(appkey,appnumber);
                break;
            case PERMANENT:
                // code block 49 ans
                success = verifyApppkeyPermanent(appkey,appnumber);
                break;
            case FREE:
                // code block 10 jours
                success = verifyApppkeyFree(appkey,appnumber);
                break;
            default:
                // code block
                success = false ;
        }

        return success;
    }



}
