package com.jay.easygest.vue;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.jay.easygest.R;
import com.jay.easygest.controleur.Usercontrolleur;
import com.jay.easygest.databinding.ActivityActiverProduitBinding;
import com.jay.easygest.model.AppKessModel;
import com.jay.easygest.model.UserModel;
import com.jay.easygest.outils.AccessLocalAppKes;
import com.jay.easygest.outils.MesOutils;

public class ActiverProduitActivity extends AppCompatActivity {
    private ActivityActiverProduitBinding binding;
    private String[] appcredentials;
    private Usercontrolleur usercontrolleur;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appcredentials = (String[]) getIntent().getExtras().get("appcredentials");

        binding = ActivityActiverProduitBinding.inflate(getLayoutInflater());
        usercontrolleur = Usercontrolleur.getUsercontrolleurInstance(this);
        setContentView(binding.getRoot());
        init();
//        activerproduit();
        getFreeAccount();
    }

    private void init(){
        int  code_msg =  getIntent().getExtras().getInt("code_msg");
        String msg_expire = "votre licence a expirer contacter le proprietaire pour la renouveller";
        String activation_texte = "ACTIVATION DU PRODUIT";
        if (code_msg == 1 ){
            binding.txtMsgExpired.setText(msg_expire);
        }else {
            binding.txtMsgExpired.setText(activation_texte);
            binding.txtMsgExpired.setTextColor(getColor(R.color.bleue_brillant) );
        }
        MesOutils.Level level = MesOutils.getLicenceLevel(appcredentials[1]);
      if (level != MesOutils.Level.FREE || MesOutils.isLicenceExpired(appcredentials)){
          binding.txtCompteFree.setVisibility(View.GONE);
      }
    }

    public void activerproduit(){
        binding.btnactiverproduit.setOnClickListener(v -> {
            binding.btnactiverproduit.setEnabled(false);
           try {
               String proprietaire = binding.editactiverproduitAppowner.getText().toString().trim();
               String cleproduit = binding.editactiverproduitAppKey.getText().toString().trim();

               String appnumber = appcredentials[0];
               String apppowner = appcredentials[2];
              boolean is_appnuber_right = MesOutils.retrieveAppNumber(cleproduit,appnumber);

               if (!proprietaire.isEmpty() && !cleproduit.isEmpty()) {
                   if (apppowner.equals(proprietaire) && is_appnuber_right) {
                       if (appcredentials[1].equals(cleproduit) ){
                           Toast.makeText(ActiverProduitActivity.this, "un probleme est survenue", Toast.LENGTH_SHORT).show();
                           binding.btnactiverproduit.setEnabled(true);

                       }else {
                           if (MesOutils.isKeyvalide(cleproduit,appnumber) && MesOutils.getLicenceLevel(appcredentials[1]) != MesOutils.Level.PERMANENT ){
                               AppKessModel appKessModel = new AppKessModel(
                                       Integer.parseInt(appcredentials[0]),
                                       cleproduit,
                                       appcredentials[2],
                                       appcredentials[3],
                                       appcredentials[4],
                                       appcredentials[7]);
                               AccessLocalAppKes accessLocalAppKes = new AccessLocalAppKes(this);
                               boolean success =  accessLocalAppKes.updateAppkesKey(appKessModel, appcredentials);
                               if (success){
                                   UserModel user = usercontrolleur.recupProprietaire();
                                   Intent intent;
                                   if (user!= null){
                                       intent = new Intent(ActiverProduitActivity.this, MainActivity.class);
                                       intent.putExtra("msgactivation","félicitation licence "+MesOutils.getLicenceLevel(cleproduit)+ "activée");
                                   }else {
                                       intent = new Intent(ActiverProduitActivity.this, CreercompteActivity.class);
                                       intent.putExtra("msgactivation","félicitation licence "+MesOutils.getLicenceLevel(cleproduit)+ "activée");
                                   }
                                   intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                   startActivity(intent);
                                   finish();

                               }else {
                                   Toast.makeText(ActiverProduitActivity.this, "un probleme est survenue", Toast.LENGTH_SHORT).show();
                                   binding.btnactiverproduit.setEnabled(true);
                               }
                           }else {
                               Toast.makeText(ActiverProduitActivity.this, "un probleme est survenue cle incorrecte", Toast.LENGTH_SHORT).show();
                               binding.btnactiverproduit.setEnabled(true);
                           }

                       }

                   } else {
                       Toast.makeText(ActiverProduitActivity.this, "produit non conforme", Toast.LENGTH_SHORT).show();
                       binding.btnactiverproduit.setEnabled(true);
                   }
               } else {
                   Toast.makeText(ActiverProduitActivity.this, "champs obligatoire", Toast.LENGTH_SHORT).show();
                   binding.btnactiverproduit.setEnabled(true);
               }
           }catch (Exception e){
               Toast.makeText(ActiverProduitActivity.this, "un probleme est survenu si cela persiste contacter l'editeur", Toast.LENGTH_LONG).show();
               binding.btnactiverproduit.setEnabled(true);
           }
        });
    }

    public void getFreeAccount(){

        binding.txtCompteFree.setOnClickListener(v -> {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("CLE DU PRODUIT");
            builder.setMessage("noter la cle elle vous sera utile"+"\n"
                    +" cle : "+appcredentials[1] );
            builder.setPositiveButton("ok",(dialog, which) -> {
                Intent intent = new Intent(ActiverProduitActivity.this, CreercompteActivity.class);
                intent.putExtra("msgactivation","félicitation et bienvenu(e)");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            });
            builder.create().show();

        });

    }

}