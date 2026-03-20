package com.jay.easygest.outils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.widget.Toast;

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
    private final Context contexte;
    private final MySqliteOpenHelper accessBD;
    private SQLiteDatabase bd;


    public AccessLocalArticles(Context context) {
        this.contexte = context;
        this.accessBD =  MySqliteOpenHelper.getInstance(contexte,null);
    }

    public ArticlesModel insertArticle(ArticlesModel article){
        bd = accessBD.getWritableDatabase();
        ArticlesModel articlesModel = null;
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
        } catch (SQLiteConstraintException e) {
            Toast.makeText(contexte, "article existe deja", Toast.LENGTH_LONG).show();
        }
        catch (Exception e) {
            Toast.makeText(contexte, e.getMessage(), Toast.LENGTH_LONG).show();
        }finally {
            if (bd.inTransaction()){
                bd.endTransaction();
            }
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
        }catch( SQLiteException e) {
            rslt = 0;
            bd.close();
        }
        return rslt;
    }

    /**
     *
     * @return la liste des articles avec les images
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
            bd.close();
        } catch (Exception e) {
            return articlesModels;
        }
        return articlesModels;
    }


    /**
     *
     * @return la liste des articles sans les images
     */
    public ArrayList<ArticlesModel> listeArticlescredit(){
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
            return articlesModels;
        }
        return articlesModels;
    }


    public ArticlesModel getArticle(String designation){
        bd = accessBD.getReadableDatabase();
        ArticlesModel articlesModel = null;
        try {
            String req = "select * from articles where " + DESIGNATION + "='"+designation+"'";
            Cursor cursor = bd.rawQuery(req, null);
            cursor.moveToLast();
            if (!cursor.isAfterLast()) {
                articlesModel = new ArticlesModel(cursor.getInt(0),cursor.getString(1), cursor.getInt(2),cursor.getInt(3), cursor.getString(4));

            }
           cursor.close();
        } catch (Exception e) {
            return articlesModel;
        }
        return articlesModel;
    }

    public int updateArticleStandard(ArticlesModel article, String champ, String valeur, String itemId) {
        bd = accessBD.getWritableDatabase();
        int rslt;
        try{
            ContentValues articles_cv = new ContentValues();
            if (itemId.equals("article_popup_ajout_stock")){
                int quantite = article.getQuantite()  + Integer.parseInt(valeur);
                articles_cv.put(champ,quantite);
            }

            if (itemId.equals("article_popup_enlever_stock")){
                int quantite = article.getQuantite() - Integer.parseInt(valeur);
                articles_cv.put(champ,quantite);
            }
            if (itemId.equals("article_popup_modifier_prix")){
                articles_cv.put(champ,Integer.parseInt(valeur));
            }
            if (itemId.equals("article_popup_modifier_designation")){
                articles_cv.put(champ,valeur);
            }

            rslt = bd.update(TABLE_ARTICLES, articles_cv, ID+ "= ?", new String[] {String.valueOf(article.getId())});

        } catch (Exception e) {
            throw new RuntimeException(e);
        }finally {
            bd.close();
        }
        return rslt;
    }
}
