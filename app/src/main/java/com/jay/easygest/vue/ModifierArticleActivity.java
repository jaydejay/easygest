package com.jay.easygest.vue;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.jay.easygest.controleur.Articlescontrolleur;
import com.jay.easygest.databinding.ActivityModofierArticleBinding;
import com.jay.easygest.model.ArticlesModel;
import java.util.Objects;

public class ModifierArticleActivity extends AppCompatActivity {
   private ArticlesModel articlesModel;
   private ActivityModofierArticleBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityModofierArticleBinding.inflate(getLayoutInflater());
        Articlescontrolleur articlescontrolleur = Articlescontrolleur.getArticlescontrolleurInstance(this);
//       articlesModel = articlescontrolleur.recupererArticle(getIntent().getIntExtra("articleid",1));
        articlesModel = articlescontrolleur.getMarticle().getValue();
        setContentView(binding.getRoot());

        initA();
        updateArtice();
    }

    public void initA(){
        binding.editarticlesModifDesignation.setText(articlesModel.getDesignation());
        binding.editarticlesModifPrix.setText(String.valueOf(articlesModel.getPrix()));
        binding.editarticlesModifQuantite.setText(String.valueOf(articlesModel.getQuantite()));
        binding.editarticlesModifDescription.setText(articlesModel.getDescription());
    }

    public void updateArtice(){
        binding.btnArticleModif.setOnClickListener(view -> {
            binding.btnArticleModif.setEnabled(false);
            String string_designation = Objects.requireNonNull(binding.editarticlesModifDesignation.getText()).toString().trim();
            String string_prix = Objects.requireNonNull(binding.editarticlesModifPrix.getText()).toString().trim();
            String string_quantite = Objects.requireNonNull(binding.editarticlesModifQuantite.getText()).toString().trim();
            String string_description = Objects.requireNonNull(binding.editarticlesModifDescription.getText()).toString().trim();
            if (string_designation.isEmpty()){
                binding.lleditarticleModifDesignation.setError("obligatoire");
                binding.btnArticleModif.setEnabled(true);
            }else if (string_designation.length() < 2 ) {
                binding.lleditarticleModifDesignation.setError("2 minimum");
                binding.btnArticleModif.setEnabled(true);

            }else if (string_designation.length() > 30 ) {
                binding.lleditarticleModifDesignation.setError("30 maximum");
                binding.btnArticleModif.setEnabled(true);

            }else if (string_prix.isEmpty() ) {
                binding.lleditarticleModifPrix.setError("obligatoire");
                binding.btnArticleModif.setEnabled(true);

            }else if (string_prix.length() > 16 ) {
                binding.lleditarticleModifPrix.setError("16 maximum");
                binding.btnArticleModif.setEnabled(true);

            }else if (string_quantite.isEmpty() ) {
                binding.lleditarticleModifQuantite.setError("obligatoire");
                binding.btnArticleModif.setEnabled(true);

            }else if (string_quantite.length() > 16 ) {
                binding.lleditarticleModifQuantite.setError("16 maximum");
                binding.btnArticleModif.setEnabled(true);

            }else if (string_description.isEmpty() ) {
                Toast.makeText(this, "vous devez decrire l'article", Toast.LENGTH_LONG).show();
                binding.btnArticleModif.setEnabled(true);

            }else if (string_description.length() < 10) {
                Toast.makeText(this, "10 caracteres minimum", Toast.LENGTH_LONG).show();
                binding.btnArticleModif.setEnabled(true);

            }else {
                ArticlesModel article = new ArticlesModel(articlesModel.getId(),string_designation,Integer.parseInt(string_prix),Integer.parseInt(string_quantite), string_description);
                Articlescontrolleur articlescontrolleur = Articlescontrolleur.getArticlescontrolleurInstance(this);
                ArticlesModel articlesModel1 = articlescontrolleur.updateArticle(article);
                if (articlesModel1 != null){
                    Intent intent = new Intent(this,GestionActivity.class);
                    startActivity(intent);
                }else {
                    Toast.makeText(this,"article non persister",Toast.LENGTH_LONG).show();
                    binding.btnArticleModif.setEnabled(true);

                }
            }
        });
    }

}