package com.jay.easygest.vue.ui.articles;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import com.jay.easygest.R;
import com.jay.easygest.controleur.Articlescontrolleur;
import com.jay.easygest.model.ArticlesModel;
import com.jay.easygest.model.Image;
import com.jay.easygest.outils.MesOutils;
import com.jay.easygest.vue.GestionActivity;

import java.util.ArrayList;

public class RecycleViewArticleAdapter extends RecyclerView.Adapter<RecycleViewArticleAdapter.ViewHolder>{

    private final Context contexte;
    private final ArrayList<ArticlesModel> articles;

    public RecycleViewArticleAdapter(Context contexte, ArrayList<ArticlesModel> articles) {
        this.contexte = contexte;
        this.articles = articles;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_liste_articles,parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        String quantite = "Quantite : "+articles.get(position).getQuantite();
        String designation = articles.get(position).getDesignation();
        ArrayList<Image> images = articles.get(position).getImages();

        if (!images.isEmpty()){
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

                    AlertDialog.Builder builder = new AlertDialog.Builder(contexte);
                    builder.setTitle("supprimer un article");
                    builder.setMessage("vous etes sur le point de supprimer l'article");

                    builder.setPositiveButton("oui", (dialog, which) -> {
                        Articlescontrolleur articlescontrolleur = Articlescontrolleur.getArticlescontrolleurInstance(null);
                        int rslt = articlescontrolleur.deleteArticle(articles.get(position));
                        if (rslt > 0){
                            articles.remove(position);
                            articlescontrolleur.listeArticles2();
                            articlescontrolleur.setAdaptermarticles(articles);
                        }

                    });

                    builder.setNegativeButton("non", (dialog, which) -> {


                    });

                    builder.create().show();

                }

                if (item.getItemId()== R.id.article_popup_detail ){
                    articles.get(position).setImages(images);

                    ((GestionActivity)contexte).redirectToArticleDetailsActivity(articles.get(position));
                }

                return true;
            });

            popupMenu.show();
        });

    }

    @Override
    public int getItemCount() {
        return articles.size();
    }

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder)
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView articleImage;
        TextView articlelistedesignation;
        TextView articlelistequantite;
        ImageButton btn_article_popup_menu;
        LinearLayout ll_articleliste;

        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View
            ll_articleliste = view.findViewById(R.id.ll_article_liste);
            articleImage = view.findViewById(R.id.article_imageView);
            btn_article_popup_menu = view.findViewById(R.id.btn_article_popup_menu);
            articlelistedesignation = view.findViewById(R.id.article_liste_designation);
            articlelistequantite = view.findViewById(R.id.article_liste_quantite);

        }

    }
}
