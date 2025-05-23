package com.jay.easygest.outils;


import android.util.Log;

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

    public Task<File> createFile(String path){

        return Tasks.call(executor,()->{
            File fileMetadata = new File();
            fileMetadata.setName(VariablesStatique.BACKUP_DATABASE_NAME);
            java.io.File file = new java.io.File(path);
            FileContent mediaContent = new FileContent("application/db", file);

            File myfile = null;

            try {
                myfile = mdriveservice.files().create(fileMetadata, mediaContent).execute();

            } catch (Exception e) {
                Log.d("excter", "uploadFileToDrive: "+e.getMessage());
            }
            if (myfile == null){
                throw new IOException("ioexception when requesting file creation");
            }

            return myfile;
        });

    }



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
                throw new RuntimeException(e);
            }
            return outStream;
        });

    }


    public Task<File> updateFile(String path){

        return Tasks.call(executor,()->{
            String driveFileId = preferedServiceHelper.getDriveSession();

            java.io.File file = new java.io.File(path);
            FileContent mediaContent = new FileContent("application/db", file);

            File myfile = null;

            try {
                myfile = mdriveservice.files().update(driveFileId,null, mediaContent).execute();

            } catch (Exception e) {
                Log.d("excter", "uploadFileToDrive: "+e.getMessage());
            }
            if (myfile == null){
                throw new IOException("ioexception when requesting file creation");
            }

            return myfile;
        });

    }

}
