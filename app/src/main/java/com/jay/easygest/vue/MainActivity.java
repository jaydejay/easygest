package com.jay.easygest.vue;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.jay.easygest.model.UserModel;
import com.jay.easygest.controleur.Usercontrolleur;
import com.jay.easygest.databinding.ActivityMainBinding;

import java.util.Objects;


public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private Usercontrolleur usercontrolleur;
    private Integer compteur;
    private UserModel user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        this.usercontrolleur = Usercontrolleur.getUsercontrolleurInstance(this);
        init();
        authentification();
        redirectToAppActivation();
        debloquerCompteProprietaire();
    }

    private void init() {
        desactivatetxtCreation();
        desactiverbtnAuthInit();
        desactiverbtnAuthAdmineInit();
        parametres();


    }

    private void authentification() {

        binding.btnauth.setOnClickListener(view -> {
            try {

                if (binding.editTextUsername.length() != 0 & binding.editTextTextPassword.length() != 0 ) {

                    if (binding.editTextUsername.length() >= 6 & binding.editTextTextPassword.length() >= 8) {
                        String username = Objects.requireNonNull(binding.editTextUsername.getText()).toString();
                        String password = Objects.requireNonNull(binding.editTextTextPassword.getText()).toString();
                        user = usercontrolleur.recupProprietaire();
                        if (usercontrolleur.isAuthenticated(username, password)) {
                            UserModel userModel = new UserModel(user.getId(), user.getUsername(), user.getPassword(), user.getDateInscription(), user.getStatus(), user.isActif(), 0);
                            usercontrolleur.modifierUser(userModel);
                            usercontrolleur.setUser(userModel);
                            Intent intent = new Intent(MainActivity.this, GestionActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();

                        } else {
                            Toast.makeText(MainActivity.this, "username ou mot de passe incorrecte", Toast.LENGTH_SHORT).show();
                            desactiverbtnAuth(user);
                        }
                    } else {
                        Toast.makeText(MainActivity.this, "username ou mot de passe trop court", Toast.LENGTH_SHORT).show();
                        desactiverbtnAuth(user);
                    }
                } else {
                    Toast.makeText(MainActivity.this, "champs obligatoires", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Toast.makeText(MainActivity.this, "compte inexistant creer un compte", Toast.LENGTH_SHORT).show();
            }

        });
    }

    private void debloquerCompteProprietaire() {

        binding.btnmaindeloqueadmine.setOnClickListener(view -> {
            try {
                if ( binding.editTextUsername.length() != 0 & binding.editTextTextPassword.length() != 0) {
                    if (binding.editTextUsername.length() >= 6 & binding.editTextTextPassword.length() >= 8) {

                        String username = Objects.requireNonNull(binding.editTextUsername.getText()).toString();
                        String password = Objects.requireNonNull(binding.editTextTextPassword.getText()).toString();
                        user = usercontrolleur.recupAdministrateur();
                        if (username.equals(user.getUsername()) && password.equals(user.getPassword())) {
                            usercontrolleur.activerProprietaire();
                            usercontrolleur.activerAdministrateur();
                            initChamp();
                            activerbtn(binding.btnauth);
                            binding.mainlayoutadmine.setVisibility(View.INVISIBLE);
                            afficherAlerte();
                        } else {
                            Toast.makeText(MainActivity.this, "username ou mot de passe incorrecte", Toast.LENGTH_SHORT).show();
                            desactiverbtnAuthAdmine(user);
                        }
                    } else {
                        Toast.makeText(MainActivity.this, "username ou mot de passe trop court", Toast.LENGTH_SHORT).show();
                        desactiverbtnAuthAdmine(user);
                    }
                } else {
                    Toast.makeText(MainActivity.this, "champs obligatoires", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Toast.makeText(MainActivity.this, "un probleme d'integrité est survenu contacter votre administrateur", Toast.LENGTH_SHORT).show();
            }

        });
    }


    private void parametres() {
        binding.txtParametres.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ParametresActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }


    private void activerProduit() {
        String[] appcredentials = usercontrolleur.getAppCredentials();
        String apppkey = appcredentials[1];
        String apppowner = appcredentials[2];


            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("cle d'activation");
            builder.setMessage("les donnees d'activations sont necessaires pour l'activation de votre produit, il est fortement recommendé de les noter." +"\n"
                                +"appli key : " + apppkey + "\n"+
                                "appli owner : " + apppowner );

            builder.setPositiveButton("ok", (dialog, which) -> {
                Intent intent = new Intent(this, ActiverProduitActivity.class);
                intent.putExtra("apppowner", apppowner);
                intent.putExtra("apppkey", apppkey);
                startActivity(intent);

            });

            builder.create().show();

    }

    private void redirectToAppActivation() {
        binding.txtCreateCompte.setOnClickListener(view -> activerProduit());

    }

//    private void messagePermissionObligatoire() {
//        Snackbar.make(binding.layoutmain, "permission obligatoire", Snackbar.LENGTH_LONG)
//                .setAction("Parametres", v -> {
//                    final Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
//                    final Uri uri = Uri.fromParts("package", MainActivity.this.getPackageName(), null);
//                    intent.setData(uri);
//                    startActivity(intent);
//                }).show();
//
//    }

//    public void envoieSms(String destinationAddress, String text) {
//        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED & ActivityCompat.checkSelfPermission(this, Manifest.permission.MODIFY_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
//                // TODO: Consider calling
//                //    ActivityCompat#requestPermissions
//                // here to request the missing permissions, and then overriding
//                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//                //                                          int[] grantResults)
//                // to handle the case where the user grants the permission. See the documentation
//                // for ActivityCompat#requestPermissions for more details.
//                String[] permisions = {Manifest.permission.SEND_SMS, Manifest.permission.MODIFY_PHONE_STATE};
//                this.requestPermissions(permisions, MY_PERMISSION_REQUEST_CODE_SEND_SMS);
//
//                return;
//            }else {
//
//                messagePermissionObligatoire();
//            }
//            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
//                this.getSystemService(SmsManager.class).sendTextMessageWithoutPersisting(destinationAddress, null, text, null, null);
//            }else{ SmsManager.getDefault().sendTextMessage(destinationAddress,null,text,null,null);}
//
//       }else if( android.os.Build.VERSION.SDK_INT >=  Build.VERSION_CODES.KITKAT) {
//            SmsManager.getDefault().sendTextMessage(destinationAddress,null,text,null,null);
//        }
//    }


    private void desactivatetxtCreation(){
        int nbrutilisateur = usercontrolleur.nbrUtilisateur();
        if (nbrutilisateur >= 3){
            binding.txtCreateCompte.setVisibility(View.INVISIBLE);
        }
    }

    private void activerbtn(Button btn){
            btn.setVisibility(View.VISIBLE);
            btn.setEnabled(true);

    }

    private void desactiverbtnAuthInit(){
        try {
            UserModel userModel = usercontrolleur.recupProprietaire();
            if (userModel.getCompteur()>=3){
                binding.btnauth.setVisibility(View.INVISIBLE);
                binding.mainlayoutadmine.setVisibility(View.VISIBLE);
            }
        }catch (Exception e){
            //do nothing
        }

    }

    private void desactiverbtnAuth(UserModel userModel){
        int cmpteur = incrementCompteur(userModel);
        if (cmpteur >= 3){
            usercontrolleur.desactiverProprietaire();
            initChamp();
            binding.btnauth.setVisibility(View.INVISIBLE);
            binding.mainlayoutadmine.setVisibility(View.VISIBLE);
        }

    }

    private void desactiverbtnAuthAdmineInit(){
        try {
            UserModel userModel = usercontrolleur.recupAdministrateur();
            if (userModel.getCompteur()>=3){
                binding.txtParametres.setVisibility(View.VISIBLE);
                binding.mainlayoutadmine.setVisibility(View.INVISIBLE);

            }
        }catch (Exception e){
            //do nothing
        }

    }

    private void desactiverbtnAuthAdmine(UserModel userModel){
        int cmpteur = incrementCompteur(userModel);
        if (cmpteur >= 3){
            usercontrolleur.desactiverAdministrateur();
            initChamp();
            binding.txtParametres.setVisibility(View.VISIBLE);
            binding.mainlayoutadmine.setVisibility(View.INVISIBLE);

        }

    }

    private Integer incrementCompteur( UserModel userModel){
        try {
            Integer compteur1 = userModel.getCompteur();
            compteur = compteur1 + 1;
            user = new UserModel(userModel.getId(),userModel.getUsername(),userModel.getPassword(),userModel.getDateInscription(),userModel.getStatus(),userModel.isActif(),compteur);
            usercontrolleur.modifierUser(user);
            usercontrolleur.setUser(user);
        }catch (Exception e){
            //do nothing
            }
        return compteur;

    }




    private void initChamp(){
        binding.editTextUsername.setText("");
        binding.editTextTextPassword.setText("");
    }


    private void afficherAlerte() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("mot de passe reinitialisé");
        builder.setMessage("votre mot de passe a été reinitialisé, il est fortement recommendé de le noter." +
                "vous pouver le changer en accedant à modifier mot de passe dans le menu." +
                "ceci est une alerte elle diparaitra lorsque vous aurez cliquer sur ok." +
                "mot de passe : "+usercontrolleur.getProprietaireMdpInit()+"");

        builder.setPositiveButton("ok", (dialog, which) -> {

        });

        builder.create().show();
    }


}