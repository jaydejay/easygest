package com.jay.easygest.outils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Random;


public abstract  class MesOutils {

    public static String versionapp ="1.0";


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


//



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

    public static int apppnumbergenerator(){
        Random random = new Random();
        int chif1 = random.nextInt(10);
        int chif2 = random.nextInt(10);
        int chif3 = random.nextInt(10);
        int chif4 = random.nextInt(10);
        String numletter = chif1+""+chif2+""+chif3+""+chif4;
        return Integer.parseInt(numletter);
    }

    public static String apppkeygenerator(){
        int n =5;
        StringBuilder stringBuilder1 = new StringBuilder(n);
        StringBuilder stringBuilder2 = new StringBuilder(n);
        StringBuilder stringBuilder3 = new StringBuilder(n);

        String str = "ABCD1EF3GH0IJKL2MNOP4QRST6UVWX5YZ789";

        for (int i = 0; i < n; i++) {
            int index = (int)(str.length() * Math.random());
            stringBuilder1.append(str.charAt(index));
        }

        for (int i = 0; i < n; i++) {
            int index = (int)(str.length() * Math.random());
            stringBuilder2.append(str.charAt(index));
        }
        for (int i = 0; i < n; i++) {
            int index = (int)(str.length() * Math.random());
            stringBuilder3.append(str.charAt(index));
        }
        return stringBuilder1+"-"+stringBuilder2+"-"+stringBuilder3;
    }

    public static String mdpgenerator(){
        int n=8;
        StringBuilder stringBuilder = new StringBuilder(n);
        String str = "ABCD1EF3GH0IJKL2MNOP4QRST6UVWX5YZ789abcdefghijklmnopqrstuvwxyz";
        for (int i = 0; i < n; i++) {
            int index = (int)(str.length() * Math.random());
            stringBuilder.append(str.charAt(index));
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


}
