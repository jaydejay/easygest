package com.jay.easygest.outils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.jay.easygest.model.Image;

import java.util.ArrayList;

public class AccessLocalImage {
    public static final String ARTICLEID = "articleid";
    public static final String TABLE_IMAGE = "image";
    public static final String IMAGE = "image";
    public static final String ID = "id";
    private final MySqliteOpenHelper accessBD;
    private SQLiteDatabase bd;

    public AccessLocalImage(Context context) {
        this.accessBD = new MySqliteOpenHelper(context,null);
    }

    public ArrayList<Image> imagesDunArticles(int articleid){
        bd = accessBD.getReadableDatabase();
        ArrayList<Image> images = new ArrayList<>();
            try {
                String req = "select * from image where " + ARTICLEID + "="+articleid+"";
                Cursor cursor = bd.rawQuery(req, null);
                cursor.moveToFirst();
                do {
                    Image image = new Image(cursor.getInt(0),cursor.getBlob(1),cursor.getInt(2));
                    images.add(image);
                }while (cursor.moveToNext());
                cursor.close();
            } catch (Exception e) {
                e.printStackTrace();
//                bd.close();
            }

        return images;

    }

    public int updateImageInt(Image newImage) {
        bd = accessBD.getWritableDatabase();
        int rslt ;
        ContentValues image_cv = new ContentValues();
        image_cv.put(IMAGE,newImage.getImage2());
        try {
            rslt = bd.update(TABLE_IMAGE,image_cv,ID+"=?",new String[] {String.valueOf(newImage.getId())});
//            bd.close();
        }catch (Exception e){
            rslt = 0;
//            bd.close();
        }
        return rslt;
    }

    public long isertImage(Image image) {
        bd = accessBD.getWritableDatabase();
        long rslt;
        try {
            ContentValues image_cv = new ContentValues();
            image_cv.put(IMAGE,image.getImage2());
            image_cv.put(ARTICLEID,image.getArticleid());
            rslt = bd.insertOrThrow(TABLE_IMAGE,null,image_cv);
//            bd.close();
        }catch (Exception e){
            rslt = -1;
//            bd.close();
            Log.d("accesslocalimage", "isertImage: SQLException "+e.getMessage());

        }
        return rslt;
    }

}
