package com.jay.easygest.vue;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;

import com.jay.easygest.R;
import com.jay.easygest.controleur.Articlescontrolleur;
import com.jay.easygest.databinding.ActivityArticlesBinding;
import com.jay.easygest.model.ArticlesModel;
import com.jay.easygest.model.Image;
import com.jay.easygest.outils.MesOutils;
import com.jay.easygest.vue.viewmodels.ImageViewModel;

import java.io.FileDescriptor;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

public class ArticlesActivity extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_READ_WRITE = 12;
    private ActivityArticlesBinding binding;
    private  ArrayList<Image> images = new ArrayList<>();
    private ImageViewModel imageViewModel;

    private  ActivityResultLauncher<Intent> activityResultLauncherFace;
    private  ActivityResultLauncher<Intent> activityResultLauncherLeft;
    private  ActivityResultLauncher<Intent> activityResultLauncherRight;
    private  ActivityResultLauncher<Intent> activityResultLauncherBack;



    public ArticlesActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityArticlesBinding.inflate(getLayoutInflater());
        imageViewModel = new ViewModelProvider(this).get(ImageViewModel.class);
       int itemid = this.getIntent().getIntExtra("itemclickedId",1);
       if (itemid == R.id.article_popup_modifier){
           binding.llArticleBtn.setVisibility(View.GONE);
       }
        initimagesDrawble();

         activityResultLauncherFace = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result ->{
            if (result.getData() != null && result.getResultCode() == RESULT_OK){
                Uri selectedImageUri = result.getData().getData();
                try {
                    Bitmap  bitmapimageface = getBitmapFromUri(selectedImageUri);
                    bitmapimageface.getAllocationByteCount();
                   imageViewModel.getBitmapimageface().setValue(bitmapimageface);
                    byte[] bite_image = MesOutils.convertBitmapToByterry(bitmapimageface);
                    Image imageface = new Image(bite_image);
                    images = imageViewModel.getImages().getValue();
                    if (images != null){
                        images.set(0,imageface);
                        imageViewModel.getImages().setValue(images);
                        binding.articleImageViewFace.setImageBitmap(imageViewModel.getBitmapimageface().getValue());
                    }

                }catch (IOException e) {
                   // do nothing
                }
            }
        });

        activityResultLauncherLeft = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result ->{
            if (result.getData()!= null && result.getResultCode() == RESULT_OK){
                Uri selectedImageUri = result.getData().getData();

                try {
                    Bitmap  bitmapimageleft = getBitmapFromUri(selectedImageUri);
                    imageViewModel.getBitmapimageleft().setValue(bitmapimageleft);
                    byte[] bite_image = MesOutils.convertBitmapToByterry(bitmapimageleft);
                    Image  imageleft = new Image(bite_image);
                    images = imageViewModel.getImages().getValue();
                    if (images != null){
                        images.set(1,imageleft);
                        imageViewModel.getImages().setValue(images);
                        binding.articleImageViewLeft.setImageBitmap(imageViewModel.getBitmapimageleft().getValue());
                    }

                }catch (IOException e) {
                    // do nothing
                }
            }
        });

         activityResultLauncherRight = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result ->{
            if (result.getData()!= null && result.getResultCode() == RESULT_OK){
                Uri selectedImageUri = result.getData().getData();
                try {
                    Bitmap bitmapimageright = getBitmapFromUri(selectedImageUri);
                    imageViewModel.getBitmapimageright().setValue(bitmapimageright);
                    byte[] bite_image = MesOutils.convertBitmapToByterry(bitmapimageright);
                    Image imageright = new Image(bite_image);
                     images = imageViewModel.getImages().getValue();
                    if (images != null){
                        images.set(2,imageright);
                        imageViewModel.getImages().setValue(images);
                        binding.articleImageViewRight.setImageBitmap(imageViewModel.getBitmapimageright().getValue());
                    }

                } catch (IOException e) {
                    // do nothing
                }
            }
        });

         activityResultLauncherBack = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result ->{
            if (result.getData()!= null && result.getResultCode() == RESULT_OK){
                Uri selectedImageUri = result.getData().getData();

                try {
                    Bitmap bitmapimageback = getBitmapFromUri(selectedImageUri);
                     imageViewModel.getBitmapimageback().setValue(bitmapimageback);
                    byte[] bite_image = MesOutils.convertBitmapToByterry(bitmapimageback);
                    Image imageback = new Image(bite_image);
                     images = imageViewModel.getImages().getValue();
                    if (images != null){
                        images.set(3,imageback);
                        imageViewModel.getImages().setValue(images);
                        binding.articleImageViewBack.setImageBitmap(imageViewModel.getBitmapimageback().getValue());
                    }


                } catch (IOException e) {
                    // do nothing;
                }
            }
        });

        setContentView(binding.getRoot());
        getArticleFaceImage();
        getArticleLeftImage();
        getArticleRigthImage();
        getArticleBackImage();
        creerArticle();
        redirectTolisteCredits();

    }



    private void initimagesDrawble(){
        BitmapDrawable face_drawable = (BitmapDrawable) binding.articleImageViewFace.getDrawable();
        BitmapDrawable left_drawable = (BitmapDrawable)binding.articleImageViewLeft.getDrawable();
        BitmapDrawable right_drawable = (BitmapDrawable)binding.articleImageViewRight.getDrawable();
        BitmapDrawable back_drawable = (BitmapDrawable) binding.articleImageViewBack.getDrawable();

        if (face_drawable != null){
            Bitmap face_bitmap = face_drawable.getBitmap();
            Image face_image = new Image(MesOutils.convertBitmapToByterry(face_bitmap));
            imageViewModel.getBitmapimageface().setValue(face_bitmap);
            images.add(face_image);

        }
        if ( left_drawable != null  ){
            Bitmap left_bitmap = left_drawable.getBitmap();
            Image left_image = new Image(MesOutils.convertBitmapToByterry(left_bitmap));
            imageViewModel.getBitmapimageleft().setValue(left_bitmap);
            images.add(left_image);

        }

        if (right_drawable != null ){
            Bitmap right_bitmap = right_drawable.getBitmap();
            Image right_image = new Image(MesOutils.convertBitmapToByterry(right_bitmap));
            imageViewModel.getBitmapimageright().setValue(right_bitmap);
            images.add(right_image);
        }
        if (back_drawable != null){
            Bitmap back_bitmap = back_drawable.getBitmap();
            Image back_image = new Image(MesOutils.convertBitmapToByterry(back_bitmap));
            imageViewModel.getBitmapimageback().setValue(back_bitmap);
            images.add(back_image);
        }
        imageViewModel.getImages().setValue(images);
    }


    public void getArticleFaceImage(){
        binding.articleImageViewFace.setOnClickListener(view -> getArticleImage("face"));
    }

    public void getArticleLeftImage(){
        binding.articleImageViewLeft.setOnClickListener(view -> getArticleImage("left"));
    }

    public void getArticleRigthImage(){
        binding.articleImageViewRight.setOnClickListener(view -> getArticleImage("right"));
    }


    public void getArticleBackImage(){
        binding.articleImageViewBack.setOnClickListener(view -> getArticleImage("back"));
    }




    public void getArticleImage(String orientation){
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            if (ActivityCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(
                            this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_READ_WRITE);
                }else{

                Intent intent = new Intent(Intent.ACTION_GET_CONTENT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                if (orientation.equals("face")){
                    activityResultLauncherFace.launch(intent);
                }
                if (orientation.equals("left")){
                    activityResultLauncherLeft.launch(intent);
                }
                if (orientation.equals("right")){
                    activityResultLauncherRight.launch(intent);
                }

                if (orientation.equals("back")){
                    activityResultLauncherBack.launch(intent);
                }

            }

            }else{

                Intent intent = new Intent(Intent.ACTION_GET_CONTENT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
               if (orientation.equals("face")){
                   activityResultLauncherFace.launch(intent);
               }
               if (orientation.equals("left")){
                    activityResultLauncherLeft.launch(intent);
               }
               if (orientation.equals("right")){
                    activityResultLauncherRight.launch(intent);
               }

               if (orientation.equals("back")){
                    activityResultLauncherBack.launch(intent);
               }

            }

    }

    private  Bitmap getBitmapFromUri(Uri uri) throws IOException {
        ParcelFileDescriptor parcelFileDescriptor = this.getContentResolver().openFileDescriptor(uri, "r");
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
        parcelFileDescriptor.close();
        return image;
    }


    public void creerArticle(){
        binding.btnArticleEnreg.setOnClickListener(view -> {
            binding.btnArticleEnreg.setEnabled(false);
            String string_designation = Objects.requireNonNull(binding.editarticlesDesignation.getText()).toString().trim();
            String string_prix = Objects.requireNonNull(binding.editarticlesPrix.getText()).toString().trim();
            String string_quantite = Objects.requireNonNull(binding.editarticlesQuantite.getText()).toString().trim();
            String string_description = Objects.requireNonNull(binding.editarticlesDescription.getText()).toString().trim();

            if (string_designation.isEmpty()){
                binding.lleditarticleDesignation.setError("obligatoire");
                binding.btnArticleEnreg.setEnabled(true);
            }else if (string_designation.length() < 2 ) {
                binding.lleditarticleDesignation.setError("2 minimum");
                binding.btnArticleEnreg.setEnabled(true);

            }else if (string_designation.length() > 30 ) {
                binding.lleditarticleDesignation.setError("30 maximum");
                binding.btnArticleEnreg.setEnabled(true);

            }else if (string_prix.isEmpty() ) {
                binding.lleditarticlePrix.setError("obligatoire");
                binding.btnArticleEnreg.setEnabled(true);

            }else if (string_prix.length() > 16 ) {
                binding.lleditarticlePrix.setError("16 maximum");
                binding.btnArticleEnreg.setEnabled(true);

            }else if (string_quantite.isEmpty() ) {
                binding.lleditarticleQuantite.setError("obligatoire");
                binding.btnArticleEnreg.setEnabled(true);

            }else if (string_quantite.length() > 16 ) {
                binding.lleditarticleQuantite.setError("16 maximum");
                binding.btnArticleEnreg.setEnabled(true);

            }else if (string_description.isEmpty() ) {
                Toast.makeText(this, "vous devez decrire l'article", Toast.LENGTH_LONG).show();
                binding.btnArticleEnreg.setEnabled(true);

            }else {
                ArrayList<Image> mimages = imageViewModel.getImages().getValue();
                ArticlesModel articlesModel = new ArticlesModel(string_designation,Integer.parseInt(string_prix),Integer.parseInt(string_quantite), string_description, mimages );
                Articlescontrolleur articlescontrolleur = Articlescontrolleur.getArticlescontrolleurInstance(this);
                ArticlesModel articlesModel1 = articlescontrolleur.insertArticle(articlesModel);
                if (articlesModel1 != null){
                    Intent intent = new Intent(this,ArticlesActivity.class);
                    startActivity(intent);
                }else {
                    Toast.makeText(this,"article non persister",Toast.LENGTH_LONG).show();
                    binding.btnArticleEnreg.setEnabled(true);
                }
            }
        });
    }

    public void redirectTolisteCredits(){
        binding.ajoutArticleToListeArticles.setOnClickListener(view -> {
            Intent intent = new Intent(ArticlesActivity.this, GestionActivity.class);
            startActivity(intent);
        });

    }


}