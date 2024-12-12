package com.jay.easygest.outils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.jay.easygest.model.ArticlesModel;
import com.jay.easygest.model.Image;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class AccessLocalImage {
    public static final String ARTICLEID = "articleid";
    public static final String TABLE_IMAGE = "image";
    public static final String IMAGE = "image";
    public static final String ID = "id";
    private final MySqliteOpenHelper accessBD;
    private final Context contexte;
    private SQLiteDatabase bd;

    public AccessLocalImage(Context context) {
        this.contexte = context;
        this.accessBD = new MySqliteOpenHelper(contexte,null);
    }

    public ArrayList<Image> imagesDunArticles(ArticlesModel article){
        bd = accessBD.getReadableDatabase();
        ArrayList<Image> images = new ArrayList<>();
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<ArrayList<Image>> future = executor.submit(() -> {
            try {
                String req = "select * from image where " + ARTICLEID + "="+article.getId()+"";
                Cursor cursor = bd.rawQuery(req, null);
                cursor.moveToFirst();
                do {
                    Image image = new Image(cursor.getInt(0),cursor.getBlob(1),cursor.getInt(2));
                    images.add(image);
                }while (cursor.moveToNext());

                cursor.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return images;
        });
        try {
            return  future.get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        executor.shutdown();
        return images;

    }

    public int updateImageInt(Image newImage) {
        bd = accessBD.getWritableDatabase();
        int rslt ;
        ContentValues image_cv = new ContentValues();
        image_cv.put(IMAGE,newImage.getImage2());
        try {
            rslt = bd.update(TABLE_IMAGE,image_cv,ID+"=?",new String[] {String.valueOf(newImage.getId())});
        }catch (Exception e){
            rslt = 0;
        }
        return rslt;
    }

    public int updateImage(Image newImage) {
        bd = accessBD.getWritableDatabase();
        int rslt ;
        ContentValues image_cv = new ContentValues();
        image_cv.put(IMAGE,newImage.getImage2());

        try {
            rslt = bd.update(TABLE_IMAGE,image_cv,ID+"=?",new String[] {String.valueOf(newImage.getId())});
        }catch (Exception e){
            rslt = 0;
        }
        bd.close();
        return rslt;
    }

    public Image recupererimage(int imageid){
        bd = accessBD.getWritableDatabase();
        Image  image = null;
        try{
           Cursor cursor = bd.query(TABLE_IMAGE,null,ID+"=?",new String[] {String.valueOf(imageid)},null,null,null);
           cursor.moveToFirst();
           if (!cursor.isAfterLast()){
               image = new Image(cursor.getInt(0),cursor.getBlob(1), cursor.getInt(2) );
           }
           cursor.close();
        }catch (Exception e){
            return image;
        }

        bd.close();
        return image;
    }

    public long isertImage(Image image) {
        bd = accessBD.getWritableDatabase();
        long rslt;
        try {
            ContentValues image_cv = new ContentValues();
            image_cv.put(IMAGE,image.getImage2());
            image_cv.put(ARTICLEID,image.getArticleid());
            rslt = bd.insertOrThrow(TABLE_IMAGE,null,image_cv);
        }catch (Exception e){
            rslt = -1;
            Log.d("accesslocalimage", "isertImage: SQLException "+e.getMessage());

        }
        return rslt;
    }
}
