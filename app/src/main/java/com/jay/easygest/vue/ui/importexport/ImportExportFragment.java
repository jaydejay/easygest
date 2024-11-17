package com.jay.easygest.vue.ui.importexport;

import android.graphics.Typeface;
import android.media.MediaScannerConnection;
import android.os.Bundle;
import android.os.Environment;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.jay.easygest.databinding.FragmentImportExportBinding;
import com.jay.easygest.outils.VariablesStatique;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ImportExportFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ImportExportFragment extends Fragment {

    private FragmentImportExportBinding binding;
    public ImportExportFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment ImportExportFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ImportExportFragment newInstance() {

        return new ImportExportFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentImportExportBinding.inflate(inflater,container,false);

        String export_consigne ="il est important de faire" + "\n"
                +"une suvegarde reguliere"+"\n"
                +"pour pouvoir restorer vos" + "\n"
                +"donnees en cas de " + "\n"
                +"problemes la restoration" + "\n"
                +"ne conserne que la derniere" +"\n"
                +"version sauvegardee.";

        String import_consigne = "suite a un probleme une " +"\n"
                +"restoration permet d'avoir" +"\n"
                +"la derniere sauvegarde des" +"\n"
                +"donnees.les etapes suivantes" +"\n"
                +"decrivent les actions pour" +"\n"
                +"bien mener une restoration.";

        binding.exportConsigne.setText(export_consigne);
        binding.importConsigne.setText(import_consigne);
        exportAction();
        importAction();
        sauvegardeAmovible();
        sauvegardeDrive();
        restorationPremierPas();
        restorationDeconnecter();
        restorationReconnecter();

        return binding.getRoot();
    }

    /**
     *
     * @return returne le chemin d'access a la base de donnees
     */
    public String getDatabasePath(){
      return  this.requireContext().getDatabasePath(VariablesStatique.DATABASE_NAME).getPath();

    }

    /**
     * amorce la sauvegarde
     */
    public void exportAction(){
        binding.btnexport.setOnClickListener(v -> exportDB());
    }

    /**
     * amorce la restoration
     */
    public void importAction(){
        binding.btnimport.setOnClickListener(v -> importDB());
    }

    /**
     * sauvegarde amovible
     */
    private void sauvegardeAmovible(){

        binding.exportAmovible.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            builder.setTitle("sauvegarde amovible");
            builder.setMessage("cette sauvegarde permet d'avoir"+ "\n"
                    +"une version sur support amovible"+ "\n"
                    + "aller dans gestionnaire de fichiers"+ "\n"
                    + "dans android"+ "\n"
                    + "dans data"+ "\n"
                    + "dans com.jay.easygest"+ "\n"
                    + "dans files"+ "\n"
                    + "dans documents"+ "\n"
                    + "dans copier le fichier data sur le support"+ "\n");

            builder.setPositiveButton("ok", (dialog, which) -> {

            });
            builder.create().show();
        });

    }
    private void sauvegardeDrive(){

        binding.exportDrive.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            builder.setTitle(" sauvegarde Drive");
            builder.setMessage("cette sauvegarde permet d'avoir une version sur google Drive"+ "\n"
                    +"aller dans gestionnaire de fichiers"+ "\n"
                    + "dans android"+ "\n"
                    + "dans data"+ "\n"
                    + "dans com.jay.easygest"+ "\n"
                    + "dans files"+ "\n"
                    + "dans documents"+ "\n"
                    + "copier le fichier data "+ "\n"
                    + "ouvrez l'application Drive"+ "\n"
                    + "allez dans le dossier mydrive et coller"+ "\n"
                    + "si vous ne connaissez pas Drive je vous invite "+ "\n"
                    + "a vous familliarisez avec se outil de google "+ "\n");

            builder.setPositiveButton("ok", (dialog, which) -> {

            });
            builder.create().show();
        });

    }

    private void restorationPremierPas(){
        binding.importPremier.setOnClickListener(view -> {
            SpannableString titre_amovible = new SpannableString("restoration support amovible");
            titre_amovible.setSpan(new StyleSpan(Typeface.BOLD), 0, titre_amovible.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            titre_amovible.setSpan(new StyleSpan(Typeface.ITALIC), 9, titre_amovible.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            SpannableString titre_drive = new SpannableString("restoration support Drive");
            titre_drive.setSpan(new StyleSpan(Typeface.BOLD), 0, titre_drive.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            titre_drive.setSpan(new StyleSpan(Typeface.ITALIC), 9, titre_drive.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            builder.setTitle("Premiers pas");
            builder.setMessage("cette section decrit la restoration"+ "\n"
                    + titre_amovible + "\n"
                    + "inserrer le support"+ "\n"
                    + "copier le fichier gestioncredit"+ "\n"
                    + "aller dans gestionnaire de fichiers"+ "\n"
                    + "dans android"+ "\n"
                    + "dans data"+ "\n"
                    + "dans com.jay.easygest"+ "\n"
                    + "dans files"+ "\n"
                    + "dans documents puis coller"+ "\n"
                    + "cliquer sur restorer"+ "\n"
                    + titre_drive+ "\n"
                    + "les étapes precedentes sont valables"+ "\n");

            builder.setPositiveButton("ok", (dialog, which) -> {

            });
            builder.create().show();
        });


    }

    private void restorationDeconnecter(){
        binding.importDeconnecter.setOnClickListener(view -> {

            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            builder.setTitle("consigne de deconnection");
            builder.setMessage("pour parfaire la restoration apres les premieres pas"+ "\n"
                    + "deconnecter l'application est neccessaire");

            builder.setPositiveButton("ok", (dialog, which) -> {

            });
            builder.create().show();
        });




    }


    private void restorationReconnecter(){

        binding.importReconnecter.setOnClickListener(view -> {

            SpannableString titre_content = new SpannableString("consigne de deconnection");
//            titre_content.setSpan(new boldS());

            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            builder.setTitle(titre_content);
            builder.setMessage("apres la deconnection ouvrir"+ "\n"
                    + "a nouveau l'application pour prendre"+ "\n"
                    + "en compte la mise a jour");

            builder.setPositiveButton("ok", (dialog, which) -> {
            });
            builder.create().show();
        });

    }

    /**
     * creation de la savegarde
     */
   private void exportDB() {

        File path = this.requireContext().getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
        File file = new File(path, "data.db");

        String currentDBPath = getDatabasePath();
        File currentDB = new File(currentDBPath);

        try {

            if (Environment.getExternalStorageState().equals(Environment.MEDIA_REMOVED) ) {
                Toast.makeText(getContext(), "inserer une carte memoire", Toast.LENGTH_SHORT).show();

            } else if (Environment.getExternalStorageState().equals(Environment.MEDIA_UNMOUNTED)) {
                Toast.makeText(getContext(), "une carte memoire non montee", Toast.LENGTH_SHORT).show();

            }else {
                InputStream is = new FileInputStream(currentDB);
                OutputStream os = new FileOutputStream(file);
                byte[] data = new byte[is.available()];
                is.read(data);
                os.write(data);
                is.close();
                os.close();
                AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                builder.setTitle("consigne de sauvegarde");
                builder.setMessage("cette action constitue la premiere étape"+ "\n"
                        + "- étape 2 savegarde sur un support amovible"+ "\n"
                        + "- étape 3 sauvegarde sur Drive"+ "\n"
                        + "consulter le dictatiel pour connaitre chaque procedure");

                builder.setPositiveButton("ok", (dialog, which) -> {
                    MediaScannerConnection.scanFile(getContext(),
                            new String[] { file.toString() }, null,
                            (path1, uri) -> {
                                Log.i("ExternalStorage", "Scanned " + path1 + ":");
                                Log.i("ExternalStorage", "-> uri=" + uri);
                            });
                });
                builder.create().show();

            }

        } catch (IOException e) {
            Toast.makeText(getContext(), "inserer une carte memoire", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * la restoration
     */
    private void importDB() {

        File currentDBPath = this.requireContext().getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
        File currentDB = new File(currentDBPath, "data.db");

        String path = getDatabasePath();
        File file = new File(path);

        try {

            if (Environment.getExternalStorageState().equals(Environment.MEDIA_REMOVED) ) {
                Toast.makeText(getContext(), "inserer une carte memoire", Toast.LENGTH_SHORT).show();

            } else if (Environment.getExternalStorageState().equals(Environment.MEDIA_UNMOUNTED)) {
                Toast.makeText(getContext(), "une carte memoire non montee", Toast.LENGTH_SHORT).show();

            }else {
                InputStream is = new FileInputStream(currentDB);
                OutputStream os = new FileOutputStream(file);
                byte[] data = new byte[is.available()];
                is.read(data);
                os.write(data);
                is.close();
                os.close();
                AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                builder.setTitle("consigne de restoration");
                builder.setMessage("cette action constitue la premiere étape"+ "\n"
                        + "- étape 2 deconnecter l'appli"+ "\n"
                        + "- étape 3 relancer l'appli"+ "\n"
                        + "consulter le dictatiel pour connaitre chaque procedure");

                builder.setPositiveButton("ok", (dialog, which) -> {

                });
                builder.create().show();

            }

        } catch (Exception e) {
            Log.d("importexport", "importDB: "+e.getMessage());
            Toast.makeText(getContext(), "inserer une carte memoire ou fichier introuvable", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * supprime la sauvegarde
     */

    private void deleteExternalStoragePrivateDoc() {

        File path = this.requireContext().getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
        if (path != null) {
            File file = new File(path, "gestioncredit.db");
            file.delete();
        }
    }

    /**
     *
     * @return vrai si le fichier ou le repertoire de sauvegarde existe
     */
   private boolean hasExternalStoragePrivateDoc() {
        File path =this.requireContext(). getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
        if (path != null) {
            File file = new File(path, "gestioncredit.db");
            return file.exists();
        }
        return false;
    }


}