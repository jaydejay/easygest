package com.jay.easygest.outils;


import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.api.client.http.FileContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DriveServiceHelper {
    private final Drive mdriveservice;
    private final PreferedServiceHelper preferedServiceHelper;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();


    public DriveServiceHelper(Drive mdriveservice, PreferedServiceHelper preferedServiceHelper) {
        this.mdriveservice = mdriveservice;
        this.preferedServiceHelper = preferedServiceHelper;
    }

    /**
     * permet de creer un fichier sur le drive
     * @param path chemin de la base de donnee
     * @return un fichier
     */
    public Task<File> createFile(String path){

        return Tasks.call(executor,()->{
            File fileMetadata = new File();
            fileMetadata.setName(VariablesStatique.BACKUP_DATABASE_NAME);
            java.io.File file = new java.io.File(path);
            FileContent mediaContent = new FileContent("application/db", file);
            File myfile;
            try {
                myfile = mdriveservice.files().create(fileMetadata, mediaContent).execute();
            } catch (Exception e) {
                throw new Exception("probleme interne est survenu lors de la creation du fichier");
            }
            if (myfile == null){
                throw new IOException("ioexception when requesting file creation");
            }
            return myfile;
        });

    }


    /**
     * permet de recuperer un fichier du drive
     * @param fileid id du fichier
     * @param databasePath chemin de la base de donnee
     * @return un fichier
     */
    public Task<OutputStream> retriveFile(String fileid, String databasePath){

        return Tasks.call(executor,()->{
            OutputStream outStream;
            try {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    outStream = Files.newOutputStream(Paths.get(databasePath));
                    mdriveservice.files().get(fileid).executeMediaAndDownloadTo(outStream);
                }else {
                    outStream = new FileOutputStream(databasePath);
                    mdriveservice.files().get(fileid).executeMediaAndDownloadTo(outStream);
                }
            } catch (IOException e) {
                throw new IOException("ioexception when requesting file creation");
            }
            return outStream;
        });

    }


    /**
     * permet de mettre a jour la base de donnee sur le drive
     * @param path chemin de la base de donnee
     * @return un fichier
     */
    public Task<File> updateFile(String path){

        return Tasks.call(executor,()->{
            String driveFileId = preferedServiceHelper.getDriveSession();
            java.io.File file = new java.io.File(path);
            FileContent mediaContent = new FileContent("application/db", file);
            File myfile;
            try {
                myfile = mdriveservice.files().update(driveFileId,null, mediaContent).execute();
            } catch (Exception e) {
                throw new IOException(e.getMessage());
            }
            if (myfile == null){
                throw new IOException("ioexception when requesting file creation");
            }
            return myfile;
        });
    }
}
