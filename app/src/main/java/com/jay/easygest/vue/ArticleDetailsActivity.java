package com.jay.easygest.vue;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.util.Log;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.jay.easygest.controleur.Articlescontrolleur;
import com.jay.easygest.controleur.Imagecontrolleur;
import com.jay.easygest.databinding.ActivityArticleDetailsBinding;
import com.jay.easygest.model.ArticlesModel;
import com.jay.easygest.model.Image;
import com.jay.easygest.outils.MesOutils;

import java.io.FileDescriptor;
import java.io.IOException;
import java.util.ArrayList;

public class ArticleDetailsActivity extends AppCompatActivity {
    private static final int MY_PERMISSIONS_REQUEST_READ_WRITE = 100;
    private ActivityArticleDetailsBinding binding;
    private ActivityResultLauncher<Intent> activityResultLauncherFace;
    private ActivityResultLauncher<Intent> activityResultLauncherBack;
    private ActivityResultLauncher<Intent> activityResultLauncherRight;
    private ActivityResultLauncher<Intent> activityResultLauncherLeft;
    private Imagecontrolleur imagecontrolleur ;
    private  ArticlesModel article;
    private Bitmap bitmapimageface;
    private Bitmap bitmapimageback;
    private Bitmap bitmapimageright;
    private Bitmap bitmapimageleft;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityArticleDetailsBinding.inflate(getLayoutInflater());
        Articlescontrolleur articlescontrolleur = Articlescontrolleur.getArticlescontrolleurInstance(this);
        imagecontrolleur = Imagecontrolleur.getImagecontrolleurInstance(this);
        article = articlescontrolleur.getMarticle().getValue();
        setContentView(binding.getRoot());
        initDetails();
        showArticlesPhotos(article.getImages());
        showArticlesPhotoclick();
        getArticleFaceImage();
        getArticleLeftImage();
        getArticleRigthImage();
        getArticleBackImage();
        getActivityResultLancherFace();
        getActivityResultLancherLeft();
        getActivityResultLancherRight();
        getActivityResultLancherBack();

    }
    public void getActivityResultLancherFace(){

        activityResultLauncherFace = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),result ->{
            if (result.getData()!= null && result.getResultCode() == RESULT_OK){
                Uri selectedImageUri = result.getData().getData();
                try {
                    int taille = article.getImages().size();
                    bitmapimageface = getBitmapFromUri(selectedImageUri);
                    byte[] bitemapTobite = MesOutils.convertBitmapToByterry(bitmapimageface);
                    if (taille == 0) {
                        Image newImage1 = new Image(bitemapTobite, article.getId());
                        long rslt1 = imagecontrolleur.insertImage(newImage1);
                        if (rslt1 > 0) {

                            Image newImage2 = new Image((int) rslt1,bitemapTobite, article.getId());
                            article.getImages().add(newImage2);
                            binding.articleDetailFace.setImageBitmap(bitmapimageface);
                            binding.articleDetailImageView.setImageBitmap(bitmapimageface);
                        }
                    } else {
                        Image imageBite2 = new Image(article.getImages().get(0).getId(), bitemapTobite, article.getId());
                        long rslt2 = imagecontrolleur.updateImageInt(imageBite2);
                        if (rslt2 > 0) {
                            article.getImages().set(0,imageBite2);
                            binding.articleDetailFace.setImageBitmap(bitmapimageface);
                            binding.articleDetailImageView.setImageBitmap(bitmapimageface);
                        }
                    }


                } catch (IOException e) {
                    Log.d("articledetailactivity", "onCreate: "+e.getMessage());
                }

            }
        });
    }

    public void getActivityResultLancherLeft(){

        activityResultLauncherLeft = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),result ->{
            if (result.getData()!= null && result.getResultCode() == RESULT_OK){
                Uri selectedImageUri = result.getData().getData();
                binding.articleDetailLeft.setImageURI(selectedImageUri);
                try {
                    int taille = article.getImages().size();
                    bitmapimageleft = getBitmapFromUri(selectedImageUri);
                    byte[] bitemapTobite = MesOutils.convertBitmapToByterry(bitmapimageleft);

                    if (taille < 2) {
                        Image newImage1 = new Image(bitemapTobite, article.getId());
                        long rslt1 = imagecontrolleur.insertImage(newImage1);
                        if (rslt1 > 0) {
                            Image newImage2 = new Image((int) rslt1,bitemapTobite, article.getId());
                            article.getImages().add(newImage2);
                            binding.articleDetailLeft.setImageBitmap(bitmapimageleft);
                            binding.articleDetailImageView.setImageBitmap(bitmapimageleft);
                        }
                    } else {
                        Image imageBite2 = new Image(article.getImages().get(1).getId(), bitemapTobite, article.getId());
                        long rslt2 = imagecontrolleur.updateImageInt(imageBite2);
                        if (rslt2 > 0) {
                            article.getImages().set(1,imageBite2);
                            binding.articleDetailLeft.setImageBitmap(bitmapimageleft);
                            binding.articleDetailImageView.setImageBitmap(bitmapimageleft);
                        }
                    }

                } catch (IOException e) {
                    Log.d("articledetailactivity", "onCreate: "+e.getMessage());
                }
            }
        });


    }

    public void getActivityResultLancherRight(){

        activityResultLauncherRight = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),result ->{
            if (result.getData()!= null && result.getResultCode() == RESULT_OK){
                Uri selectedImageUri = result.getData().getData();
                binding.articleDetailRight.setImageURI(selectedImageUri);
                try {
                    int taille = article.getImages().size();
                    bitmapimageright = getBitmapFromUri(selectedImageUri);
                    byte[] bitemapTobite = MesOutils.convertBitmapToByterry(bitmapimageright);
                    if (taille < 3) {

                        Image newImage1 = new Image(bitemapTobite, article.getId());
                        long rslt1 = imagecontrolleur.insertImage(newImage1);
                        if (rslt1 > 0) {
                            Image newImage2 = new Image((int) rslt1,bitemapTobite, article.getId());
                            article.getImages().add(newImage2);
                            binding.articleDetailRight.setImageBitmap(bitmapimageright);
                            binding.articleDetailImageView.setImageBitmap(bitmapimageright);
                        }
                    } else {
                        Image imageBite2 = new Image(article.getImages().get(2).getId(), bitemapTobite, article.getId());
                        long rslt2 = imagecontrolleur.updateImageInt(imageBite2);
                        if (rslt2 > 0) {
                            article.getImages().set(2,imageBite2);
                            binding.articleDetailRight.setImageBitmap(bitmapimageright);
                            binding.articleDetailImageView.setImageBitmap(bitmapimageright);
                        }
                    }

                } catch (IOException e) {
                    Log.d("articledetailactivity", "onCreate: "+e.getMessage());
                }
            }
        });


    }

    public void getActivityResultLancherBack(){

        activityResultLauncherBack = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),result ->{
            if (result.getData()!= null && result.getResultCode() == RESULT_OK){
                Uri selectedImageUri = result.getData().getData();
                try {
                    int taille = article.getImages().size();
                    bitmapimageback = getBitmapFromUri(selectedImageUri);
                    byte[] bitemapTobite = MesOutils.convertBitmapToByterry(bitmapimageback);
                    if (taille < 4) {
                        Image newImage1 = new Image(bitemapTobite, article.getId());
                        long rslt1 = imagecontrolleur.insertImage(newImage1);
                        if (rslt1 > 0) {
                            Image newImage2 = new Image((int) rslt1,bitemapTobite, article.getId());
                            article.getImages().add(newImage2);
                            binding.articleDetailBack.setImageBitmap(bitmapimageback);
                            binding.articleDetailImageView.setImageBitmap(bitmapimageback);
                        }
                    } else {
                        Image imageBite2 = new Image(article.getImages().get(3).getId(), bitemapTobite, article.getId());
                        long rslt2 = imagecontrolleur.updateImageInt(imageBite2);
                        if (rslt2 > 0) {
                            article.getImages().set(3,imageBite2);
                            binding.articleDetailBack.setImageBitmap(bitmapimageback);
                            binding.articleDetailImageView.setImageBitmap(bitmapimageback);
                        }
                    }

                } catch (IOException e) {
                    Log.d("articledetailactivity", "onCreate: "+e.getMessage());
                }
            }
        });


    }



    private Bitmap getBitmapFromUri(Uri uri) throws IOException {
        ParcelFileDescriptor parcelFileDescriptor = this.getContentResolver().openFileDescriptor(uri, "r");
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
        parcelFileDescriptor.close();
        return image;
    }



    private void initDetails() {
        if (article.getImages().size() > 0 ){
            Bitmap img_bit = MesOutils.convertByterryToBitmap(article.getImages().get(0).getImage2());
            binding.articleDetailImageView.setImageBitmap(img_bit);
        }
        String prix = article.getPrix() +" fcfa";
        String quantite = "disponibilite "+article.getQuantite();
        binding.articleDetailDesignation.setText(article.getDesignation());
        binding.articleDetailPrix.setText(prix);
        binding.articleDetailQuantite.setText(quantite);
        binding.articleDetailDescription.setText(article.getDescription());
    }

    /**
     *
     * @param photos la liste des images d'un article
     */
   private void showArticlesPhotos(ArrayList<Image> photos){

        try {
            if (photos.size() > 0 ){
                Bitmap img_bit = MesOutils.convertByterryToBitmap(article.getImages().get(0).getImage2());
                binding.articleDetailFace.setImageBitmap(img_bit);
                binding.articleDetailImageView.setImageBitmap(img_bit);
                if (photos.get(1).getImage2() != null ){
                    Bitmap img_bit2 = MesOutils.convertByterryToBitmap(article.getImages().get(1).getImage2());
                    binding.articleDetailLeft.setImageBitmap(img_bit2);
                }

                if (photos.get(2).getImage2() != null ){
                    Bitmap img_bit3 = MesOutils.convertByterryToBitmap(article.getImages().get(2).getImage2());
                    binding.articleDetailRight.setImageBitmap(img_bit3);
                }
                if (photos.get(3).getImage2() != null ){
                    Bitmap img_bit4 = MesOutils.convertByterryToBitmap(article.getImages().get(3).getImage2());
                    binding.articleDetailBack.setImageBitmap(img_bit4);
                }

            }
        }catch (Exception e){
            //do nothing
        }

   }

    /**
     * ppermet dafficher en grand une photo au click
     */
    private void showArticlesPhotoclick(){
        try {
            if (article.getImages().size() > 0 ){
                binding.articleDetailFace.setOnClickListener(view -> {
                    Bitmap img_bit = MesOutils.convertByterryToBitmap(article.getImages().get(0).getImage2());
                    binding.articleDetailImageView.setImageBitmap(img_bit);

                });

                if (article.getImages().get(1) != null ){
                    binding.articleDetailLeft.setOnClickListener(view -> {
                        Bitmap img_bit2 = MesOutils.convertByterryToBitmap(article.getImages().get(1).getImage2());
                        binding.articleDetailImageView.setImageBitmap(img_bit2);

                    });
                }

                if (article.getImages().get(2) != null ){
                    binding.articleDetailRight.setOnClickListener(view -> {
                        Bitmap img_bit3 = MesOutils.convertByterryToBitmap(article.getImages().get(2).getImage2());
                        binding.articleDetailImageView.setImageBitmap(img_bit3);

                    });
                }
                if (article.getImages().get(3) != null ){
                    binding.articleDetailBack.setOnClickListener(view -> {
                        Bitmap img_bit4 = MesOutils.convertByterryToBitmap(article.getImages().get(3).getImage2());
                        binding.articleDetailImageView.setImageBitmap(img_bit4);

                    });
                }
            }
        }catch (Exception e){
            //do nothing
        }

    }



    public void getArticleFaceImage(){
        binding.articleDetailFaceText.setOnClickListener(view -> getArticleImage("face"));

    }

    public void getArticleLeftImage(){
        binding.articleDetailLeftText.setOnClickListener(view -> getArticleImage("left"));
    }

    public void getArticleRigthImage(){
        binding.articleDetailRightText.setOnClickListener(view -> getArticleImage("right"));
    }


    public void getArticleBackImage(){
        binding.articleDetailBackText.setOnClickListener(view -> getArticleImage("back"));
    }





    public void getArticleImage(String orientation){

        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_READ_WRITE);
        }else {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

            if (orientation.equals("face")){
                activityResultLauncherFace.launch(intent);
            }

            if (orientation.equals("back")){
                activityResultLauncherBack.launch(intent);
            }

            if (orientation.equals("right")){
                activityResultLauncherRight.launch(intent);
            }

            if (orientation.equals("left")){
                activityResultLauncherLeft.launch(intent);
            }
        }


    }
}