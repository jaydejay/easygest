package com.jay.easygest.vue.ui.articles;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.widget.PopupMenu;

import com.jay.easygest.R;
import com.jay.easygest.controleur.Articlescontrolleur;
import com.jay.easygest.model.ArticlesModel;
import com.jay.easygest.model.Image;
import com.jay.easygest.outils.MesOutils;
import com.jay.easygest.vue.GestionActivity;

import java.util.ArrayList;

public class ListeArticlesAdapter extends BaseAdapter {

    private final Context contexte;
    private final LayoutInflater inflater;
    private final ArrayList<ArticlesModel> articles;


    public ListeArticlesAdapter(Context contexte,ArrayList<ArticlesModel> articles) {
        this.contexte = contexte;
        this.inflater = LayoutInflater.from(contexte);
        this.articles = articles;
    }

    @Override
    public int getCount() {
        return articles.size();
    }

    @Override
    public Object getItem(int position) {
        return articles.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        ArticlesViewHolder holder ;

        if (view == null){

            holder = new ArticlesViewHolder();
            view = inflater.inflate(R.layout.layout_liste_articles,null);

            holder.ll_articleliste = view.findViewById(R.id.ll_article_liste);
            holder.articleImage = view.findViewById(R.id.article_imageView);
            holder.btn_article_popup_menu = view.findViewById(R.id.btn_article_popup_menu);
            holder.articlelistedesignation = view.findViewById(R.id.article_liste_designation);
            holder.articlelistequantite = view.findViewById(R.id.article_liste_quantite);

            view.setTag(holder);
        }else {
            holder = (ArticlesViewHolder) view.getTag();
        }
        String quantite = "Quantite : "+articles.get(position).getQuantite();
        String designation = articles.get(position).getDesignation();
        ArrayList<Image> images = articles.get(position).getImages();
        if (images.size() > 0){
            Bitmap img_bit = MesOutils.convertByterryToBitmap(images.get(0).getImage2());
            holder.articleImage.setImageBitmap(img_bit);
        }
        holder.articlelistedesignation.setText(designation);
        holder.articlelistequantite.setText(quantite);


        holder.btn_article_popup_menu.setOnClickListener(view1 -> {

            PopupMenu popupMenu = new PopupMenu(contexte, holder.btn_article_popup_menu);
            popupMenu.getMenuInflater().inflate(R.menu.article_menu,popupMenu.getMenu());


            popupMenu.setOnMenuItemClickListener(item -> {
                if (item.getItemId()== R.id.article_popup_modifier ){

                    ((GestionActivity)contexte).redirectToModifierArticleActivity(articles.get(position));

                }
                if (item.getItemId()== R.id.article_popup_supprimer ){
                    Articlescontrolleur articlescontrolleur = Articlescontrolleur.getArticlescontrolleurInstance(null);
                    int rslt = articlescontrolleur.deleteArticle(articles.get(position));
                    if (rslt > 0){
                        articles.remove(position);
                        articlescontrolleur.setMarticles(articles);
                    }
                }

                if (item.getItemId()== R.id.article_popup_detail ){
                    articles.get(position).setImages(images);

                    ((GestionActivity)contexte).redirectToArticleDetailsActivity(articles.get(position));
                }

                return true;
            });

            popupMenu.show();
        });

        holder.articleImage.setOnClickListener(view1 -> ((GestionActivity)contexte).redirectToModifierArticleImgesActivity(articles.get(position)));



        return view;
    }


    private static class ArticlesViewHolder{
        ImageView articleImage;
        TextView articlelistedesignation;
        TextView articlelistequantite;
        ImageButton btn_article_popup_menu;
        LinearLayout ll_articleliste;

    }
}
