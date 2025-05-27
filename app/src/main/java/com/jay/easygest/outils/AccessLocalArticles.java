package com.jay.easygest.outils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import com.jay.easygest.model.ArticlesModel;
import com.jay.easygest.model.Image;

import java.util.ArrayList;

public class AccessLocalArticles {

    public static final String TABLE_ARTICLES = "articles";
    public static final String ID = "id";
    public static final String DESIGNATION = "designation";
    public static final String PRIX = "prix";
    public static final String QUANTITE = "quantite";
    public static final String TABLE_IMAGE = "image";
    public static final String IMAGE = "image";
    public static final String ARTICLEID = "articleid";
    public static final String DESCRIPTION = "description";
    private Context contexte;
    private MySqliteOpenHelper accessBD;
    private SQLiteDatabase bd;


    public AccessLocalArticles(Context context) {
        this.contexte = context;
        this.accessBD = new MySqliteOpenHelper(contexte,null);
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
                Image image_base = new Image((int)imageid,image.getImage2(),(int)rslt);
                images.add(image_base);
            }
            articlesModel = new ArticlesModel((int)rslt,article.getDesignation(),article.getPrix(),article.getQuantite(), article.getDesignation(), images);
            bd.setTransactionSuccessful();
        }finally {
            bd.endTransaction();
        }

        return articlesModel;
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

            if (rslt < 1){
               articleModel = null;
           }else {
               articleModel = article;
           }
//            bd.close();
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
//            bd.close();
        }catch( SQLiteException e) {
            rslt = 0;
            bd.close();
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
                AccessLocalImage accessLocalImage = new AccessLocalImage(contexte) ;
                ArrayList<Image> images = accessLocalImage.imagesDunArticles(cursor.getInt(0));
                ArticlesModel articlesModel = new ArticlesModel(cursor.getInt(0),cursor.getString(1), cursor.getInt(2),cursor.getInt(3), cursor.getString(4),images);
                articlesModels.add(articlesModel);
            }
            while (cursor.moveToNext());
            cursor.close();
//            bd.close();
        } catch (Exception e) {
            return articlesModels;
        }
        return articlesModels;
    }




}
