package com.jay.easygest.vue;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.auth.api.identity.AuthorizationRequest;
import com.google.android.gms.auth.api.identity.AuthorizationResult;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Scope;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.jay.easygest.R;
import com.jay.easygest.controleur.Usercontrolleur;
import com.jay.easygest.databinding.ActivityDriveKeyValidatorBinding;
import com.jay.easygest.model.AppKessModel;
import com.jay.easygest.model.DriveKeyModel;
import com.jay.easygest.outils.AccessLocal;
import com.jay.easygest.outils.AccessLocalAppKes;
import com.jay.easygest.outils.PasswordDriveServiceHelper;
import com.jay.easygest.outils.SessionManagement;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class ActiverProduitActivity extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_READ_DRIVE_FILE = 17;
    private ActivityDriveKeyValidatorBinding binding;
    private ActivityResultLauncher<IntentSenderRequest> activityResultLauncher;
    private static final GsonFactory JSON_FACTORY = new GsonFactory();
    private NetHttpTransport transport;
    private PasswordDriveServiceHelper passwordDriveServiceHelper;

   private DriveKeyModel cle_fournie;
    private String basecode;
    private String[] credentials;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityDriveKeyValidatorBinding.inflate(getLayoutInflater());
        transport = new NetHttpTransport();
        AccessLocal accessLocal = new AccessLocal(this);
       credentials = accessLocal.appCredential();
       getFreeAccount();
        verifyKey();
        activityLauncherlistener();
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void verifyKey(){
        binding.btnValidatorKey.setOnClickListener(v -> {
            binding.btnValidatorKey.setEnabled(false);
            String cle= binding.edtValidatorKey.getText().toString().trim();
            String owner = binding.edtValidatorOwner.getText().toString().trim();
            String telephone = binding.edtValidatorPhone.getText().toString().trim();
            String email = binding.edtValidatorEmail.getText().toString().trim();
             basecode = binding.edtValidatorBasecode.getText().toString().trim();
            if (cle.isEmpty() || owner.isEmpty() || email.isEmpty() || telephone.isEmpty()){
                Toast.makeText(this, "remplir tous les champs", Toast.LENGTH_SHORT).show();
                binding.btnValidatorKey.setEnabled(true);
            }else {
                if (basecode.length() != 4  ) {
                    Toast.makeText(this, "base code 4 lettres atendues", Toast.LENGTH_SHORT).show();
                    binding.btnValidatorKey.setEnabled(true);
                }else {
                    if (owner.length() < 5 || owner.length() > 25) {
                        Toast.makeText(this, "5 lettres minimum et 25 lettres maximum", Toast.LENGTH_SHORT).show();
                        binding.btnValidatorKey.setEnabled(true);
                    }else {
                        if (telephone.length() != 10) {
                            Toast.makeText(this, "10 chiffres attendu", Toast.LENGTH_SHORT).show();
                            binding.btnValidatorKey.setEnabled(true);
                        }else {
                            cle_fournie = new DriveKeyModel(owner,telephone,email,cle);
                            launchsignInIntent();
                        }
                    }

                }
            }

        });
    }

    public void launchsignInIntent(){
            if (
                ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.GET_ACCOUNTS) !=
                        PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(this,
                        new String[]{ Manifest.permission.GET_ACCOUNTS},
                        MY_PERMISSIONS_REQUEST_READ_DRIVE_FILE);
                binding.btnValidatorKey.setEnabled(true);
            }else {
                requestGoogleDriveAuthorization();
            }
    }

    public void requestGoogleDriveAuthorization(){
        List<Scope> requestedScopes = Collections.singletonList(new Scope(DriveScopes.DRIVE_READONLY));
        AuthorizationRequest authorizationRequest = AuthorizationRequest.builder()
                .setRequestedScopes(requestedScopes)
                .build();
        Identity.getAuthorizationClient(this)
                .authorize(authorizationRequest)
                .addOnSuccessListener(
                        authorizationResult -> {
                            if (authorizationResult.hasResolution()) {
                                // Access needs to be granted by the user
                                PendingIntent pendingIntent = authorizationResult.getPendingIntent();
                                try {
                                    if (pendingIntent != null) {
                                        IntentSenderRequest intentSenderRequest = new IntentSenderRequest.Builder(pendingIntent).build();
                                        activityResultLauncher.launch(intentSenderRequest);
                                    }
                                } catch (Exception e) {
                                     binding.btnValidatorKey.setEnabled(true);
                                }
                            } else {
                                // Access already granted, continue with user action
                                try {
                                    saveToDriveAppFolder(authorizationResult);
                                } catch (IOException e) {
                                    binding.btnValidatorKey.setEnabled(true);
                                }
                            }
                        })
                .addOnFailureListener(e -> Toast.makeText(this, "echec de l'autorisation", Toast.LENGTH_SHORT).show());
    }


    public void activityLauncherlistener(){
        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartIntentSenderForResult(), result ->{
            if (result.getData() != null && result.getResultCode() == RESULT_OK){
                try {
                    AuthorizationResult authorizationResult = Identity.getAuthorizationClient(this).getAuthorizationResultFromIntent(result.getData());
                    saveToDriveAppFolder(authorizationResult);
                } catch (ApiException | IOException e) {
                    binding.btnValidatorKey.setEnabled(true);
                    throw new RuntimeException(e);
                }
            }
        });
    }


    private void saveToDriveAppFolder(AuthorizationResult authorizationResult) throws IOException  {

        if (authorizationResult.toGoogleSignInAccount() != null){
            GoogleCredential credentials  =  new GoogleCredential.Builder()
                    .setJsonFactory(JSON_FACTORY)
                    .setTransport(transport)
                    .build()
                    .setAccessToken(authorizationResult.getAccessToken()) ;

            Drive driveService = new Drive.Builder(transport, JSON_FACTORY, credentials)
                    .setApplicationName("easygest")
                    .build();
            passwordDriveServiceHelper = new PasswordDriveServiceHelper(driveService);

            try {
//                String drive_file_id = preferedServiceHelper.getDriveSession();
                String drive_file_id = "1Hog8wQciE3-RihVAlrL-jNOEJU9ZfnZH";
                if (!drive_file_id.isEmpty()){
                    readFileToDrive(drive_file_id,cle_fournie);
                }
            }catch (Exception e){
                binding.btnValidatorKey.setEnabled(true);
                Toast.makeText(this, "echec de la lecture : "+e.getMessage(), Toast.LENGTH_LONG).show() ;}
        }
    }
    private void readFileToDrive(String drive_file_id, DriveKeyModel cle_fournie) {
        passwordDriveServiceHelper.readDriveFile(drive_file_id,cle_fournie, new PasswordDriveServiceHelper.OnFileReadListener() {
            @Override
            public void onSuccess(DriveKeyModel content) {
                onSuccessFunction(content);
            }

            @Override
            public void onError(String error) {
                errorFunction(error);
            }
        });
    }

    private void onSuccessFunction(DriveKeyModel content) {
        if (content != null){
            Usercontrolleur usercontrolleur = Usercontrolleur.getUsercontrolleurInstance(this);
          boolean success = usercontrolleur.saveAppkeys(content.getOwner(), content.getLicence(), content.getEmail(), content.getTelephone(),basecode,credentials[0]);

          if (success){
              SessionManagement sessionManagement = new SessionManagement(this);
              sessionManagement.savekeyActivated(true);
              sessionManagement.saveLicenceExpiredStatus(false);
              Intent intent = new Intent(this,CreercompteActivity.class);
              intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
              startActivity(intent);
              finish();
          }

        }

    }

    private void errorFunction(String error) {
        runOnUiThread(() -> {
            // Your UI code or Handler creation goes here
            if (!error.isEmpty()){
                Toast.makeText(ActiverProduitActivity.this,  error , Toast.LENGTH_LONG).show();
                binding.btnValidatorKey.setEnabled(true);
            }

        });

    }

    public void getFreeAccount(){
        binding.txtCompteFree.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("CLE DU PRODUIT");
            builder.setMessage("noter la cle elle vous sera utile"+"\n"
                    +" cle : "+credentials[1] );
            builder.setPositiveButton("ok",(dialog, which) -> {

                String owner = binding.edtValidatorOwner.getText().toString().trim();
                String telephone = binding.edtValidatorPhone.getText().toString().trim();
                String email = binding.edtValidatorEmail.getText().toString().trim();
                basecode = binding.edtValidatorBasecode.getText().toString().trim();

                if (basecode.isEmpty() || owner.isEmpty() || email.isEmpty() || telephone.isEmpty()){
                    Toast.makeText(this, "remplir tous les champs", Toast.LENGTH_SHORT).show();
                    binding.btnValidatorKey.setEnabled(true);
                }else {
                     if (basecode.length() != 4  ) {
                         Toast.makeText(this, "base code 4 lettres atendues", Toast.LENGTH_SHORT).show();
                        binding.btnValidatorKey.setEnabled(true);
                    }else {
                          if (owner.length() < 5 || owner.length() > 25) {
                             Toast.makeText(this, "5 lettres minimum et 25 lettres maximum", Toast.LENGTH_SHORT).show();
                             binding.btnValidatorKey.setEnabled(true);
                         }else {
                               if (telephone.length() != 10) {
                                   Toast.makeText(this, "10 chiffres attendu", Toast.LENGTH_SHORT).show();
                                    binding.btnValidatorKey.setEnabled(true);
                              }else {
                                   AccessLocalAppKes accessLocalAppKes = new AccessLocalAppKes(this);
                                   AppKessModel appKessModel = new AppKessModel(Integer.parseInt(credentials[0]),credentials[1],owner,basecode,telephone,email);
                                   boolean rslt = accessLocalAppKes.updateAppkes(appKessModel);
                                   if (rslt){
                                       SessionManagement sessionManagement = new SessionManagement(this);
                                       sessionManagement.saveFreekeyActivated(true);
                                       Intent intent = new Intent(ActiverProduitActivity.this, CreercompteActivity.class);
                                       intent.putExtra("msgactivation","félicitation et bienvenu(e)");
                                       intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                       startActivity(intent);
                                       finish();
                                   }
                               }

                          }
                     }

                }
            });
            builder.create().show();

        });

    }

}