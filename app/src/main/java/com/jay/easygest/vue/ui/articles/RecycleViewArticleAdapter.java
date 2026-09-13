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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;
import com.jay.easygest.R;
import com.jay.easygest.controleur.Articlescontrolleur;
import com.jay.easygest.model.ArticlesModel;
import com.jay.easygest.model.Image;
import com.jay.easygest.outils.MesOutils;
import com.jay.easygest.outils.VariablesStatique;
import com.jay.easygest.vue.GestionActivity;

import java.util.ArrayList;

public class RecycleViewArticleAdapter extends RecyclerView.Adapter<RecycleViewArticleAdapter.ViewHolder>{

    private final Context contexte;
    private final ArrayList<ArticlesModel> articles;
    private TextInputEditText edt_article_modifer;

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
                if (item.getItemId()== R.id.article_popup_supprimer ){
                    AlertDialog.Builder builder = getRecycleViewArticleSuprimerBuider(position);
                    builder.setNegativeButton("non", (dialog, which) -> {});
                    builder.create().show();
                }

                if (item.getItemId()== R.id.article_popup_detail ){
                    articles.get(position).setImages(images);
                    ((GestionActivity)contexte).redirectToArticleDetailsActivity(articles.get(position));
                }

                if (item.getItemId()== R.id.article_popup_modifier_designation ){
                    String title = "modifier l'article";
                    String message = "voulez vous modifier la designation";
                    String itemId = "article_popup_modifier_designation";
                    AlertDialog.Builder builder = getArticleModifierBuilder(title, message,R.layout.layout_article_modifier_designation);
                    edt_article_modifer.setText(String.valueOf( articles.get(position).getDesignation()));
                    builder.setPositiveButton("oui", (dialog, which) -> {
                        String valeur_champ = edt_article_modifer.getText().toString().trim();
                        if (valeur_champ.isEmpty() ) {
                            Toast.makeText(contexte, "champ obligatoire", Toast.LENGTH_SHORT).show();
                        }else {
                            Articlescontrolleur articlescontrolleur = Articlescontrolleur.getArticlescontrolleurInstance(null);
                            ArticlesModel article = articles.get(position);
                            String champ = VariablesStatique.DESIGNATION;
                            int rslt =  articlescontrolleur.modifierArticle(article,champ,valeur_champ,itemId);
                            if (rslt > 0){
                                ((GestionActivity)contexte).refreshPage();
                            }

                        }
                    });
                    builder.create().show();
                }
                if (item.getItemId()== R.id.article_popup_modifier_prix ){
                    String title = "modifier l'article";
                    String message = "voulez vous modifier le prix";
                    String itemId = "article_popup_modifier_prix";
                    AlertDialog.Builder builder = getArticleModifierBuilder(title, message,R.layout.layout_article_modifier);
                    edt_article_modifer.setText(String.valueOf( articles.get(position).getPrix()));
                    builder.setPositiveButton("oui", (dialog, which) -> {
                        String string_prix = edt_article_modifer.getText().toString().trim();
                        if (string_prix.isEmpty() ) {
                            Toast.makeText(contexte, "champ obligatoire", Toast.LENGTH_SHORT).show();
                        }else {
                            Articlescontrolleur articlescontrolleur = Articlescontrolleur.getArticlescontrolleurInstance(null);
                            ArticlesModel article = articles.get(position);
                            String champ = VariablesStatique.PRIX;
                          int rslt =  articlescontrolleur.modifierArticle(article,champ,string_prix,itemId);
                            if (rslt > 0){
                                ((GestionActivity)contexte).refreshPage();
                            }

                        }
                    });
                    builder.create().show();
                }

                if (item.getItemId()== R.id.article_popup_ajout_stock ){
                    String title = "modifier l'article";
                    String message = "voulez vous ajouter des articles ?";
                    String itemId = "article_popup_ajout_stock";
                    AlertDialog.Builder builder = getArticleModifierBuilder(title, message,R.layout.layout_article_modifier);
                    builder.setPositiveButton("oui", (dialog, which) -> {
                        String string_nbr_article = edt_article_modifer.getText().toString().trim();
                        if (string_nbr_article.isEmpty() ) {
                            Toast.makeText(contexte, "champ obligatoire", Toast.LENGTH_SHORT).show();
                        }else {
                            Articlescontrolleur articlescontrolleur = Articlescontrolleur.getArticlescontrolleurInstance(null);
                            ArticlesModel article = articles.get(position);
                            String champ = VariablesStatique.QUANTITE;
                            int rslt =  articlescontrolleur.modifierArticle(article,champ,string_nbr_article,itemId);
                            if (rslt > 0){
                                ((GestionActivity)contexte).refreshPage();
                            }
                        }
                    });
                    builder.create().show();
                }

                if (item.getItemId()== R.id.article_popup_enlever_stock ){
                    String title = "modifier l'article";
                    String message = "voulez vous enlever des articles ?";
                    String itemId = "article_popup_enlever_stock";
                    AlertDialog.Builder builder = getArticleModifierBuilder(title, message,R.layout.layout_article_modifier);
                    builder.setPositiveButton("oui", (dialog, which) -> {
                        String string_nbr_article = edt_article_modifer.getText().toString().trim();
                        ArticlesModel article = articles.get(position);
                        if (string_nbr_article.isEmpty() ) {
                            Toast.makeText(contexte, "champ obligatoire", Toast.LENGTH_SHORT).show();
                        }else {
                            if (Integer.parseInt(string_nbr_article) > article.getQuantite()){
                                Toast.makeText(contexte, "quantite trop grand", Toast.LENGTH_SHORT).show();
                            }else {
                                Articlescontrolleur articlescontrolleur = Articlescontrolleur.getArticlescontrolleurInstance(null);
                                String champ = VariablesStatique.QUANTITE;
                                int rslt =  articlescontrolleur.modifierArticle(article,champ,string_nbr_article,itemId);
                                if (rslt > 0){
                                    ((GestionActivity)contexte).refreshPage();
                                }
                            }

                        }
                    });
                    builder.create().show();
                }

                return true;
            });

            popupMenu.show();
        });

    }

    private AlertDialog.Builder getArticleModifierBuilder(String title, String message,int layoutResource) {
        View view = LayoutInflater.from(contexte).inflate(layoutResource,null);
        edt_article_modifer = view.findViewById(R.id.edit_articles_modif);
        AlertDialog.Builder builder = new AlertDialog.Builder(contexte) ;
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setView(view);
        builder.setNegativeButton("non", (dialog, which) -> {});
        return builder;
    }

    @NonNull
    private AlertDialog. Builder getRecycleViewArticleSuprimerBuider(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(contexte);
        builder.setTitle("supprimer un article");
        builder.setMessage("vous etes sur le point de supprimer l'article");

        builder.setPositiveButton("oui", (dialog, which) -> {
            Articlescontrolleur articlescontrolleur = Articlescontrolleur.getArticlescontrolleurInstance(null);
            int rslt = articlescontrolleur.deleteArticle(articles.get(position));
            if (rslt > 0){
                articles.remove(position);
                articlescontrolleur.listeArticles2();
//                articlescontrolleur.setAdaptermarticles(articles);
            }
        });
        return builder;
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
