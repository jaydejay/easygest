package com.jay.easygest.vue.ui.importexport;

import static android.app.Activity.RESULT_OK;

import static com.google.android.gms.common.ConnectionResult.SERVICE_DISABLED;
import static com.google.android.gms.common.ConnectionResult.SERVICE_INVALID;
import static com.google.android.gms.common.ConnectionResult.SERVICE_MISSING;
import static com.google.android.gms.common.ConnectionResult.SERVICE_UPDATING;
import static com.google.android.gms.common.ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED;
import static com.google.android.gms.common.ConnectionResult.SUCCESS;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.Scope;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.jay.easygest.databinding.FragmentImportExportBinding;
import com.jay.easygest.outils.DriveServiceHelper;
import com.jay.easygest.outils.PreferedServiceHelper;
import com.jay.easygest.outils.VariablesStatique;

import java.util.Collections;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class ImportExportFragment extends Fragment {

    public static final String IMPORT_CONSIGNE = "suite a un probleme une " +"\n"
            +"restoration permet d'avoir" +"\n"
            +"la derniere sauvegarde des" +"\n"
            +"donnees.les etapes suivantes" +"\n"
            +"decrivent les actions pour" +"\n"
            +"bien mener une restoration.";

    public static final String EXPORT_CONSIGNE = "il est important de faire" + "\n"
            +"une suvegarde regulière"+"\n"
            +"pour pouvoir restorer vos" + "\n"
            +"données en cas de " + "\n"
            +"problemes la restoration" + "\n"
            +"ne conserne que la dernière" +"\n"
            +"version sauvegardée.";
    private static final int RC_SIGN_IN = 100;
    private static final int MY_PERMISSIONS_REQUEST_UPLOAD_DRIVE_FILE = 2;
    public static final String STORAGE_PATH = "/storage/emulated/0/";
    public static final String DRIVE_FILE_NAME = "gestioncredit.db";
    private NetHttpTransport transport ;
    private static final GsonFactory JSON_FACTORY = new GsonFactory();
    private DriveServiceHelper driveServiceHelper;
    private PreferedServiceHelper preferedServiceHelper;

    private FragmentImportExportBinding binding;
    private GoogleApiAvailability googleApiAvailability;
    public ImportExportFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentImportExportBinding.inflate(inflater,container,false);

        preferedServiceHelper = new PreferedServiceHelper(requireContext());
        transport = new NetHttpTransport();
        googleApiAvailability =  GoogleApiAvailability.getInstance();
        binding.exportConsigne.setText(EXPORT_CONSIGNE);
        binding.importConsigne.setText(IMPORT_CONSIGNE);

        mainUploadMethod();
        mainRestoreMethod();
        return binding.getRoot();
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

            }else {
                GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestEmail()
                        .requestScopes(new Scope(DriveScopes.DRIVE_FILE))
                        .build();

                GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso);

                Intent signInIntent = googleSignInClient.getSignInIntent();

                startActivityForResult(signInIntent,RC_SIGN_IN);
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
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestEmail()
                    .requestScopes(new Scope(DriveScopes.DRIVE_FILE))
                    .build();

            GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso);

            Intent signInIntent = googleSignInClient.getSignInIntent();

            startActivityForResult(signInIntent,RC_SIGN_IN);
        }

    }

//### 4. Handle the Sign-In Result
//**a. Override onActivityResult:**

        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);

            if (requestCode == RC_SIGN_IN) {
                handleSignInResult(data);
            }
        }

        private void handleSignInResult(Intent data) {
            GoogleSignIn.getSignedInAccountFromIntent(data)
                .addOnSuccessListener(googleSignInAccount -> {
                    GoogleAccountCredential credential = GoogleAccountCredential
                            .usingOAuth2(getContext(), Collections.singleton(DriveScopes.DRIVE_FILE));
                    credential.setSelectedAccount(googleSignInAccount.getAccount());
                    Drive driveService = new Drive.Builder(transport, JSON_FACTORY, credential)
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
                    }catch (Exception e){
                        Toast.makeText(requireContext(), "echec de la sauvegarde : "+e.getMessage(), Toast.LENGTH_LONG).show() ;
                    }

                }).addOnFailureListener(e -> Toast.makeText(requireContext(), "echec de la sauvegarde : "+e.getMessage(), Toast.LENGTH_LONG).show());

        }
        //### end 4. Handle the Sign-In Result

        private void uploadFileToDrive() {
            String mon_fichier = new java.io.File(getDatabasePath()).getPath();
            driveServiceHelper.createFile(mon_fichier)
                .addOnSuccessListener(s -> {
                    Toast.makeText(requireContext(), "succes de la sauvegarde ", Toast.LENGTH_SHORT).show();
                    try {
                        preferedServiceHelper.saveDriveSession(s.getId());
                    }catch (Exception e){
                        Toast.makeText(requireContext(), "echec save Drive Session "  +e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                })
                .addOnFailureListener(e -> Toast.makeText(requireContext(), "echec de la sauvegarde ", Toast.LENGTH_SHORT).show());

        }



        private void updateDriveFile()  {

            String mon_fichier = new java.io.File(getDatabasePath()).getPath();
            driveServiceHelper.updateFile(mon_fichier)
                .addOnSuccessListener(s -> Toast.makeText(requireContext(), "succes de la sauvegarde ", Toast.LENGTH_SHORT).show() )
                .addOnFailureListener(e -> Toast.makeText(requireContext(), "echec de la sauvegarde ", Toast.LENGTH_SHORT).show());

        }

    public void mainUploadMethod(){

        binding.btnexport.setOnClickListener(v -> {
          launchsignInIntent();

        });

    }


    public void mainRestoreMethod(){
        binding.btnimport.setOnClickListener(v -> {
            String drive_file_id = preferedServiceHelper.getDriveSession();
            if (drive_file_id.length() != 0){
                retiveFileToDrive(drive_file_id);
            }
        });

    }

    private void retiveFileToDrive(String drive_file_id) {
        driveServiceHelper.retriveFile(drive_file_id,getDatabasePath())
            .addOnSuccessListener(s -> Toast.makeText(getContext(), "donnees restorees", Toast.LENGTH_SHORT).show())
            .addOnFailureListener(e ->Toast.makeText(getContext(), "echec dla restoration", Toast.LENGTH_SHORT).show());
    }


//    ### end 5. Use the Signed-In Account to Access Google Drive


}