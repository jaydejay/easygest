package com.jay.easygest.outils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.util.Log;

import com.jay.easygest.model.ArticlesModel;
import com.jay.easygest.model.Image;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class AccessLocalArticles {

    public static final String TABLE_ARTICLES = "articles";
    public static final String ID = "id";
    public static final String DESIGNATION = "designation";
    public static final String PRIX = "prix";
    public static final String QUANTITE = "quantite";
    public static final String TABLE_IMAGE = "image";
    public static final String IMAGE = "image";
    public static final String IMAGE_BACK = "imageback";
    public static final String IMAGE_RIGHT = "imageright";
    public static final String IMAGE_LEFT = "imageleft";
    public static final String ARTICLEID = "articleid";
    public static final String DESCRIPTION = "description";
    private final MySqliteOpenHelper accessBD;
    private SQLiteDatabase bd;
    private final Context contexte;

    public AccessLocalArticles( Context contexte) {
        this.accessBD = new MySqliteOpenHelper(contexte,null);
        this.contexte = contexte;
    }


    public ArticlesModel insertArticle(ArticlesModel article){
        bd = accessBD.getWritableDatabase();
        ArticlesModel articlesModel ;
        bd.beginTransaction();
        try{
            ArrayList<Image> images = new ArrayList<>();
            ContentValues article_cv = new ContentValues();
            article_cv.put(DESIGNATION,article.getDesignation());
            article_cv.put(PRIX,article.getPrix());
            article_cv.put(QUANTITE,article.getQuantite());
            article_cv.put(DESCRIPTION,article.getDescription());
            long rslt = bd.insertWithOnConflict(TABLE_ARTICLES,null,article_cv,1);
            for (Image image:article.getImages()) {
                ContentValues image_cv = new ContentValues();
                image_cv.put(IMAGE,image.getImage2());
                image_cv.put(ARTICLEID,(int)rslt);
                long imageid = bd.insertWithOnConflict(TABLE_IMAGE,null,image_cv,1);
                Image image_base = new Image((int)imageid,image.getImage(),(int)rslt);
                images.add(image_base);
            }
            articlesModel = new ArticlesModel((int)rslt,article.getDesignation(),article.getPrix(),article.getQuantite(), article.getDesignation(), images);
            bd.setTransactionSuccessful();
        }catch (Exception e){
            articlesModel = null ;
        }finally {
            bd.endTransaction();
        }

        return articlesModel;
    }

    public ArticlesModel recupererArticle(int articleid){
        ArticlesModel article = null;
        try {
            bd = accessBD.getReadableDatabase();
            String req = "select * from articles where " + ID + "="+articleid+"";
            Cursor cursor = bd.rawQuery(req, null);
            cursor.moveToLast();
            if (!cursor.isAfterLast()) {
                ArrayList<Image> images = this.imagesDunArticles(articleid);
                article = new ArticlesModel(cursor.getInt(0),cursor.getString(1), cursor.getInt(2),cursor.getInt(3), cursor.getString(4), images);
            }
            cursor.close();
//            bd.close();
        }catch (Exception e){
            //do nothing
            return article;
        }
        return article;
    }

    public ArrayList<Image> imagesDunArticles(int articleid){
        bd = accessBD.getReadableDatabase();
        ArrayList<Image> images = new ArrayList<>();
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<ArrayList<Image>> future = executor.submit(() -> {
            try {
                String req = "select * from image where " + ARTICLEID + "="+articleid+"";
                Cursor cursor = bd.rawQuery(req, null);
//            if (cursor != null){
                cursor.moveToFirst();
                do {
//                    Bitmap image_to_show = MesOutils.convertByterryToBitmap(cursor.getBlob(1));
                    Image image = new Image(cursor.getInt(0),cursor.getBlob(1),cursor.getInt(2));
                    images.add(image);
                }while (cursor.moveToNext());

//            }

                Thread.sleep(200);
                cursor.close();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return images;
        });
        try {
            ArrayList<Image> result = future.get();
            return images;
        } catch (Exception e) {
            e.printStackTrace();
        }
        executor.shutdown();
        return images;


    }

    public ArticlesModel updateArticle(ArticlesModel article){
        bd = accessBD.getWritableDatabase();
        ArticlesModel articleModel;
        try{
            ContentValues articles_cv = new ContentValues();
            articles_cv.put(DESIGNATION,article.getDesignation());
            articles_cv.put(PRIX,article.getPrix());
            articles_cv.put(QUANTITE,article.getQuantite());
            articles_cv.put(DESCRIPTION,article.getDescription());
           int rslt = bd.update(TABLE_ARTICLES, articles_cv, ID+ "= ?", new String[] {String.valueOf(article.getId())});
//
            if (rslt < 1){
               articleModel = null;
           }else {
               articleModel = article;
           }

        }catch (Exception e){
            articleModel = null;
        }

        return articleModel;
    }

    /**
     *
     * @param article l'article a supprimer
     * @return vrai si article supprimer sinon faux
     */
    public int deleteArticle(ArticlesModel article){
        bd.setForeignKeyConstraintsEnabled(true);
        int rslt;
        try{
         rslt =  accessBD.getWritableDatabase().delete(TABLE_ARTICLES, ID +"=?",new String[]{String.valueOf(article.getId())});

        }catch( SQLiteException e) {
            rslt = 0;
        }
        return rslt;
    }

    /**
     *
     * @return la liste des articles
     */
    public ArrayList<ArticlesModel> listeArticles(){
        ArrayList<ArticlesModel> articlesModels = new ArrayList<>();
        bd = accessBD.getReadableDatabase();
        try {
            String req = "select * from articles";
            Cursor cursor = bd.rawQuery(req, null);
            cursor.moveToFirst();
            do{
                ArticlesModel articlesModel = new ArticlesModel(cursor.getInt(0),cursor.getString(1), cursor.getInt(2),cursor.getInt(3), cursor.getString(4));
                articlesModels.add(articlesModel);
            }
            while (cursor.moveToNext());
            cursor.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return articlesModels;
    }

    /**
     * indique si un article est en stoc ou pas
     * @param article l'article
     * @return vrai si article en stock sinon faux
     */
    public boolean isInStock(ArticlesModel article){
        return article.getQuantite() > 0;
    }

    public int updateImage(int oldImageId, Image newImage) {
        bd = accessBD.getWritableDatabase();
        int rslt ;
        ContentValues image_cv = new ContentValues();
        byte[] imageToStore = MesOutils.convertBitmapToByterry(newImage.getImage());
        image_cv.put(TABLE_IMAGE,imageToStore);
        image_cv.put(TABLE_IMAGE,oldImageId);
        try {
          rslt = bd.update(TABLE_IMAGE,image_cv,ID+"=?",new String[] {String.valueOf(oldImageId)});
        }catch (Exception e){
            rslt = 0;
        }
        return rslt;
    }
}
