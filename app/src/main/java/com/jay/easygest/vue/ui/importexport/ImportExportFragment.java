package com.jay.easygest.vue.ui.importexport;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.app.PendingIntent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.contract.ActivityResultContracts;
//import androidx.annotation.NonNull;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

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
import com.jay.easygest.databinding.FragmentImportExportBinding;
import com.jay.easygest.model.AppKessModel;
import com.jay.easygest.model.ArticlesModel;
import com.jay.easygest.model.ClientModel;
import com.jay.easygest.outils.AccessLocalAppKes;
import com.jay.easygest.outils.AccessLocalArticles;
import com.jay.easygest.outils.AccessLocalClient;
import com.jay.easygest.outils.DriveServiceHelper;
import com.jay.easygest.outils.MesOutils;
import com.jay.easygest.outils.PreferedServiceHelper;
import com.jay.easygest.outils.SmsSender;
import com.jay.easygest.outils.VariablesStatique;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class ImportExportFragment extends Fragment {

    public static final String IMPORT_CONSIGNE = "suite a un probleme une " +"\n"
            +"restoration permet d'avoir" +"\n"
            +"la derniere sauvegarde des donnees." +"\n"
            +"cliquer sur restorer pour amorcer une restoration";

    public static final String EXPORT_CONSIGNE = "il est important de faire" + "\n"
            +"une suvegarde regulière"+"\n"
            +"pour pouvoir restorer vos" + "\n"
            +"données en cas de problemes. " + "\n"
            +"La restoration ne conserne que" + "\n"
            +"la dernière version sauvegardée." +"\n"
            +"cliquer sur sauvegarder pour amorcer une sauvegarde";
    private static final int MY_PERMISSIONS_REQUEST_UPLOAD_DRIVE_FILE = 2;

    private String drive_db_key = "" ;
    private NetHttpTransport transport ;
    private static final GsonFactory JSON_FACTORY = new GsonFactory();
    private DriveServiceHelper driveServiceHelper;
    private PreferedServiceHelper preferedServiceHelper;

    private FragmentImportExportBinding binding;
    private int main_restore_btn_clicked;
    private ActivityResultLauncher<IntentSenderRequest> activityResultLaunchersave;
    private ActivityResultLauncher<IntentSenderRequest> activityResultLauncherrestore;
    private ActivityResultLauncher<IntentSenderRequest> activityResultLauncherreinstall;

    public ImportExportFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentImportExportBinding.inflate(inflater,container,false);

        preferedServiceHelper = new PreferedServiceHelper(requireContext());
        transport = new NetHttpTransport();
        binding.exportConsigne.setText(EXPORT_CONSIGNE);
        binding.importConsigne.setText(IMPORT_CONSIGNE);

        init();
        mainUploadMethod();
        mainRestoreMethod();
        mainRestoreReinstallMethod();

        activityLauncherlistenersave();
        activityLauncherlistenerrestore();
        activityLauncherlistenerreintall();
        return binding.getRoot();
    }

    private void init(){
        AccessLocalClient accessLocalClient = new AccessLocalClient(getContext());
        AccessLocalArticles accessLocalArticles = new AccessLocalArticles(getContext());
//        AccessLocalAppKes accessLocalAppKes = new AccessLocalAppKes(getContext());
        AppKessModel appKessModel = new AccessLocalAppKes(getContext()).getAppkes();
        ArrayList<ClientModel> clients = accessLocalClient.listeClients();
        ArrayList<ArticlesModel> articles = accessLocalArticles.listeArticles();
        if (!MesOutils.isDataPresent(clients,articles)){
            binding.btnexport.setVisibility(View.GONE);

        }

        if (MesOutils.getLicenceLevel(appKessModel.getApppkey()) != MesOutils.Level.FREE && !MesOutils.isDataPresent(clients,articles)){
            binding.btnimport.setVisibility(View.VISIBLE);
        }
    }


    /**
     *
     * @return returne le chemin d'access a la base de donnees
     */
    public String getDatabasePath(){
        return  this.requireContext().getDatabasePath(VariablesStatique.DATABASE_NAME).getPath();
    }

    public void launchsignInIntent(){
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            if (ActivityCompat.checkSelfPermission(requireContext(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                    PackageManager.PERMISSION_GRANTED ||
                    ActivityCompat.checkSelfPermission(requireContext(),
                            Manifest.permission.GET_ACCOUNTS) !=
                            PackageManager.PERMISSION_GRANTED

            ) {
                ActivityCompat.requestPermissions(requireActivity(),
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.GET_ACCOUNTS},
                        MY_PERMISSIONS_REQUEST_UPLOAD_DRIVE_FILE);
                binding.btnexport.setEnabled(true);

            }else {
                requestGoogleDriveSaveAuthorization();

            }
        }else {

            if (
                    ActivityCompat.checkSelfPermission(requireContext(),
                            Manifest.permission.GET_ACCOUNTS) !=
                            PackageManager.PERMISSION_GRANTED

            ) {
                ActivityCompat.requestPermissions(requireActivity(),
                        new String[]{ Manifest.permission.GET_ACCOUNTS},
                        MY_PERMISSIONS_REQUEST_UPLOAD_DRIVE_FILE);
                binding.btnexport.setEnabled(true);

            }
            requestGoogleDriveSaveAuthorization();
        }

    }

    public void requestGoogleDriveSaveAuthorization(){

        List<Scope> requestedScopes = Collections.singletonList(new Scope(DriveScopes.DRIVE_FILE));
        AuthorizationRequest authorizationRequest = AuthorizationRequest.builder()
                .setRequestedScopes(requestedScopes)
                .build();
        Identity.getAuthorizationClient(requireActivity())
                .authorize(authorizationRequest)
                .addOnSuccessListener(
                        authorizationResult -> {
                            if (authorizationResult.hasResolution()) {
                                // Access needs to be granted by the user
                                PendingIntent pendingIntent = authorizationResult.getPendingIntent();
                                try {
                                    if (pendingIntent != null) {
                                        IntentSenderRequest intentSenderRequest = new IntentSenderRequest.Builder(pendingIntent).build();
                                        activityResultLaunchersave.launch(intentSenderRequest);
                                    }
                                } catch (Exception e) {
                                    Log.e("importexport", "Couldn't start Authorization UI: " + e.getLocalizedMessage());
                                }
                            } else {
                                // Access already granted, continue with user action
                                try {
                                    saveToDriveAppFolder(authorizationResult);
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                        })
                .addOnFailureListener(e -> Log.e("importexpor", "Failed to authorize", e.getCause()));
    }


    public void launchRestoresignInIntent(){
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            if (ActivityCompat.checkSelfPermission(requireContext(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                    PackageManager.PERMISSION_GRANTED ||
                    ActivityCompat.checkSelfPermission(requireContext(),
                            Manifest.permission.GET_ACCOUNTS) !=
                            PackageManager.PERMISSION_GRANTED

            ) {
                ActivityCompat.requestPermissions(requireActivity(),
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.GET_ACCOUNTS},
                        MY_PERMISSIONS_REQUEST_UPLOAD_DRIVE_FILE);
                    binding.btnimport.setEnabled(true);
            }else {
                requestGoogleDriveRestoreAuthorization();

            }
        }else {

            if (
                    ActivityCompat.checkSelfPermission(requireContext(),
                            Manifest.permission.GET_ACCOUNTS) !=
                            PackageManager.PERMISSION_GRANTED

            ) {
                ActivityCompat.requestPermissions(requireActivity(),
                        new String[]{ Manifest.permission.GET_ACCOUNTS},
                        MY_PERMISSIONS_REQUEST_UPLOAD_DRIVE_FILE);
                binding.btnimport.setEnabled(true);

            }
            requestGoogleDriveRestoreAuthorization();
        }

    }

    public void requestGoogleDriveRestoreAuthorization(){

        List<Scope> requestedScopes = Collections.singletonList(new Scope(DriveScopes.DRIVE_FILE));
        AuthorizationRequest authorizationRequest = AuthorizationRequest.builder()
                .setRequestedScopes(requestedScopes)
                .build();
        Identity.getAuthorizationClient(requireActivity())
                .authorize(authorizationRequest)
                .addOnSuccessListener(
                        authorizationResult -> {
                            if (authorizationResult.hasResolution()) {
                                // Access needs to be granted by the user
                                PendingIntent pendingIntent = authorizationResult.getPendingIntent();
                                try {
                                    if (pendingIntent != null) {
                                        IntentSenderRequest intentSenderRequest = new IntentSenderRequest.Builder(pendingIntent).build();
                                        activityResultLauncherrestore.launch(intentSenderRequest);
                                    }
                                } catch (Exception e) {
                                    Log.e("importexport", "Couldn't start Authorization UI: " + e.getLocalizedMessage());
                                }
                            } else {
                                // Access already granted, continue with user action
                                try {
                                    retriveToDriveAppFolder(authorizationResult);
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                        })
                .addOnFailureListener(e -> Log.e("importexpor", "Failed to authorize", e.getCause()));
    }

    public void launchRestoreReinstasignInIntent(){
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            if (ActivityCompat.checkSelfPermission(requireContext(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                    PackageManager.PERMISSION_GRANTED ||
                    ActivityCompat.checkSelfPermission(requireContext(),
                            Manifest.permission.GET_ACCOUNTS) !=
                            PackageManager.PERMISSION_GRANTED

            ) {
                ActivityCompat.requestPermissions(requireActivity(),
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.GET_ACCOUNTS},
                        MY_PERMISSIONS_REQUEST_UPLOAD_DRIVE_FILE);

            }else {
                requestGoogleDriveRestoreReinstaAuthorization();
            }
        }else {

            if (
                    ActivityCompat.checkSelfPermission(requireContext(),
                            Manifest.permission.GET_ACCOUNTS) !=
                            PackageManager.PERMISSION_GRANTED

            ) {
                ActivityCompat.requestPermissions(requireActivity(),
                        new String[]{ Manifest.permission.GET_ACCOUNTS},
                        MY_PERMISSIONS_REQUEST_UPLOAD_DRIVE_FILE);

            }
            requestGoogleDriveRestoreReinstaAuthorization();
        }

    }

    public void requestGoogleDriveRestoreReinstaAuthorization(){

        List<Scope> requestedScopes = Collections.singletonList(new Scope(DriveScopes.DRIVE_FILE));
        AuthorizationRequest authorizationRequest = AuthorizationRequest.builder()
                .setRequestedScopes(requestedScopes)
                .build();
        Identity.getAuthorizationClient(requireActivity())
                .authorize(authorizationRequest)
                .addOnSuccessListener(
                        authorizationResult -> {
                            if (authorizationResult.hasResolution()) {
                                // Access needs to be granted by the user
                                PendingIntent pendingIntent = authorizationResult.getPendingIntent();
                                try {
                                    if (pendingIntent != null) {
                                        IntentSenderRequest intentSenderRequest = new IntentSenderRequest.Builder(pendingIntent).build();
                                        activityResultLauncherreinstall.launch(intentSenderRequest);
                                    }
                                } catch (Exception e) {
                                    Log.e("importexport", "Couldn't start Authorization UI: " + e.getLocalizedMessage());
                                }
                            } else {
                                // Access already granted, continue with user action
                                try {
                                    retriveReinstaToDriveAppFolder(authorizationResult);
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                        })
                .addOnFailureListener(e -> Log.e("importexpor", "Failed to authorize", e.getCause()));
    }

//### 4. Handle the Sign-In Result
//**a. Override onActivityResult:**



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
            driveServiceHelper = new DriveServiceHelper(driveService, preferedServiceHelper);

            try {
                String drive_file_id = preferedServiceHelper.getDriveSession();
                if (drive_file_id.length() == 0){
                    uploadFileToDrive();

                }else {
                    updateDriveFile();
                }
            }catch (Exception e){Toast.makeText(requireContext(), "echec de la sauvegarde : "+e.getMessage(), Toast.LENGTH_LONG).show() ;}
        }

    }

    private void retriveToDriveAppFolder(AuthorizationResult authorizationResult) throws IOException {

        if (authorizationResult.toGoogleSignInAccount() != null){
            GoogleCredential credentials  =  new GoogleCredential.Builder()
                    .setJsonFactory(JSON_FACTORY)
                    .setTransport(transport)
                    .build()
                    .setAccessToken(authorizationResult.getAccessToken());

            Drive driveService = new Drive.Builder(transport, JSON_FACTORY, credentials)
                    .setApplicationName("easygest")
                    .build();

            driveServiceHelper = new DriveServiceHelper(driveService, preferedServiceHelper);

            try {
                String drive_file_id = preferedServiceHelper.getDriveSession();
                if (drive_file_id.length() != 0){
                    retriveFileToDrive(drive_file_id);
                }else {
                    binding.layoutBtnReinsta.setVisibility(View.VISIBLE);
                    binding.btnimport.setEnabled(false);
                }
            }catch (Exception e){
                Toast.makeText(requireContext(), "echec 1 : "+e.getMessage(), Toast.LENGTH_LONG).show() ;
            }

        }

    }

    private void retriveReinstaToDriveAppFolder(AuthorizationResult authorizationResult) throws IOException {
        if (authorizationResult.toGoogleSignInAccount() != null){
            GoogleCredential credentials  =  new GoogleCredential.Builder()
                    .setJsonFactory(JSON_FACTORY)
                    .setTransport(transport)
                    .build()
                    .setAccessToken(authorizationResult.getAccessToken());

            Drive driveService = new Drive.Builder(transport, JSON_FACTORY, credentials)
                    .setApplicationName("easygest")
                    .build();

            driveServiceHelper = new DriveServiceHelper(driveService, preferedServiceHelper);

            try {
                retriveFileToDrive(drive_db_key);

            }catch (Exception e){
                Toast.makeText(requireContext(), "echec : "+e.getMessage(), Toast.LENGTH_LONG).show() ;
            }

        }
    }
    //### end 4. Handle the Sign-In Result

    private void uploadFileToDrive() {
        Log.d("uploadFileToDrive", "je suis ds uploadFileToDrive: ");
        String mon_fichier = new java.io.File(getDatabasePath()).getPath();
        driveServiceHelper.createFile(mon_fichier)
                .addOnSuccessListener(s -> {
                    Toast.makeText(requireContext(), "succes de la sauvegarde ", Toast.LENGTH_SHORT).show();
                    try {

                        preferedServiceHelper.saveDriveSession(s.getId());
                        SmsSender smsSender = new SmsSender(getContext(),getActivity());
                        AccessLocalAppKes accessLocalAppKes = new AccessLocalAppKes(getContext());
                        AppKessModel appKessModel = accessLocalAppKes.getAppkes();
                        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                        builder.setTitle("sauvegarde de donnees");
                        builder.setMessage("ces donnees sont necessaires , noter et les conservées." +"\n"
                                +s.getId() );

                        builder.setPositiveButton("ok", (dialog, which) -> {
                            String messageBody = "\n"
                                    +"proprietaire "+appKessModel.getOwner() +"\n"
                                    +"application id "+appKessModel.getAppnumber() +"\n"
                                    +"telephone proprietaire  "+appKessModel.getTelephone() +"\n"
                                    +"document id "+s.getId()+"\n";
                            String destinationAddress = VariablesStatique.DEVELOPER_PHONE;
                            smsSender.smsSendwithInnerClass(messageBody,destinationAddress, MesOutils.smsidnumbergenerator());

                        });
                        builder.create().show();
                        binding.btnexport.setEnabled(true);
                    }catch (Exception e){
                        Toast.makeText(requireContext(), "echec save Drive Session "  +e.getMessage(), Toast.LENGTH_SHORT).show();
                        binding.btnexport.setEnabled(true);
                    }

                })
                .addOnFailureListener(e -> {
                    Toast.makeText(requireContext(), "echec de la sauvegarde ", Toast.LENGTH_SHORT).show();
                    Log.d("iportexport", "uploadFileToDrive: "+e.getMessage());
                    binding.btnexport.setEnabled(true);
                });
    }



    private void updateDriveFile()  {

        String mon_fichier = new java.io.File(getDatabasePath()).getPath();
        driveServiceHelper.updateFile(mon_fichier)
                .addOnSuccessListener(s -> {
                    AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                    builder.setTitle("sauvegarde de donnees");
                    builder.setMessage("ces donnees sont necessaires , noter et les conservées." +"\n"
                            +s.getId() );

                    builder.setPositiveButton("ok", (dialog, which) -> Toast.makeText(requireContext(), "succes de la mise  a jour ", Toast.LENGTH_SHORT).show());
                    builder.create().show();
                    binding.btnexport.setEnabled(true);
                } )
                .addOnFailureListener(e -> {
                    Toast.makeText(requireContext(), "echec de la mise  jour ", Toast.LENGTH_SHORT).show();
                    Log.d("iportexport", "updateDriveFile: addOnFailureListener "+e.getMessage());
                    Log.d("iportexport", "updateDriveFile: addOnFailureListener cause "+e.getCause());
                    binding.btnexport.setEnabled(true);
                });

    }

    public void mainUploadMethod(){
        binding.btnexport.setOnClickListener(v -> {
            binding.btnexport.setEnabled(false);
            launchsignInIntent();
        });
    }


    public void mainRestoreMethod(){
        binding.btnimport.setOnClickListener(v -> {
            binding.btnimport.setEnabled(false);
            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            builder.setTitle("restoration de donnees");
            builder.setMessage("vous etes sur le point de restorer des donnees." +"\n"
                    +"si vous avez fait des modifications sans les enregistrées " +"\n"
                    +"ses modifications seront perdues" +"\n"
                    +"voulez vous continuer ?" +"\n"
            );
            builder.setPositiveButton("oui", (dialog, which) -> {
                main_restore_btn_clicked = 1;
                launchRestoresignInIntent();
            });
            builder.setNegativeButton("non",(dialog, which) -> binding.btnimport.setEnabled(true));
            builder.create().show();
        }); }

    public void mainRestoreReinstallMethod(){
        binding.btnImportReinsta.setOnClickListener(v ->{
            binding.btnImportReinsta.setEnabled(false);
            main_restore_btn_clicked = 2;
            drive_db_key  = binding.editKeyReinsta.getText().toString().trim();
            if (drive_db_key.isEmpty()){
                Toast.makeText(getContext(), "champ obligatoire", Toast.LENGTH_SHORT).show();
                binding.btnimport.setEnabled(true);
            }else {
                launchRestoreReinstasignInIntent();
            }

        }); }

    private void retriveFileToDrive(String drive_file_id) {
        driveServiceHelper.retriveFile(drive_file_id,getDatabasePath())
                .addOnSuccessListener(s -> {
                    if ( main_restore_btn_clicked ==  2){
                        preferedServiceHelper.saveDriveSession(drive_file_id);
                    }
                    Toast.makeText(getContext(), "donnees restorees", Toast.LENGTH_SHORT).show();
                    binding.btnimport.setEnabled(true);
                    binding.btnImportReinsta.setEnabled(true);
                    binding.layoutBtnReinsta.setVisibility(View.GONE);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "echec de la restoration", Toast.LENGTH_SHORT).show();
                    binding.btnimport.setEnabled(true);
                    binding.btnImportReinsta.setEnabled(true);
                });
    }


//    ### end 5. Use the Signed-In Account to Access Google Drive

    public void activityLauncherlistenersave(){

        activityResultLaunchersave = registerForActivityResult(new ActivityResultContracts.StartIntentSenderForResult(), result ->{
            if (result.getData() != null && result.getResultCode() == RESULT_OK){
                try {
                    AuthorizationResult authorizationResult = Identity.getAuthorizationClient(requireActivity()).getAuthorizationResultFromIntent(result.getData());
                    saveToDriveAppFolder(authorizationResult);
                } catch (ApiException | IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    public void activityLauncherlistenerrestore(){

        activityResultLauncherrestore = registerForActivityResult(new ActivityResultContracts.StartIntentSenderForResult(), result ->{
            if (result.getData() != null && result.getResultCode() == RESULT_OK){

                try {
                    AuthorizationResult  authorizationResult = Identity.getAuthorizationClient(requireActivity()).getAuthorizationResultFromIntent(result.getData());
                    retriveToDriveAppFolder(authorizationResult);
                } catch (ApiException | IOException e) {
                    throw new RuntimeException(e);
                }

            }
        });
    }

    public void activityLauncherlistenerreintall(){

        activityResultLauncherreinstall = registerForActivityResult(new ActivityResultContracts.StartIntentSenderForResult(), result ->{
            if (result.getData() != null && result.getResultCode() == RESULT_OK){

                try {
                    AuthorizationResult  authorizationResult = Identity.getAuthorizationClient(requireActivity()).getAuthorizationResultFromIntent(result.getData());
                    retriveToDriveAppFolder(authorizationResult);
                } catch (ApiException | IOException e) {
                    throw new RuntimeException(e);
                }

            }
        });
    }


}