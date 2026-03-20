package com.jay.easygest.outils;

import com.google.api.services.drive.Drive;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jay.easygest.model.DriveKeyModel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PasswordDriveServiceHelper {
    private final Drive mdriveservice;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public PasswordDriveServiceHelper(Drive mdriveservice) {
        this.mdriveservice = mdriveservice;
    }

    // Lit le contenu du fichier texte par son ID
    public void readDriveFile(String fileId, DriveKeyModel cle_fournie, OnFileReadListener listener) {
        executor.execute(() -> {
            String error = "donnees incorrectes";
            try {
                InputStream is = mdriveservice.files().get(fileId).executeMediaAsInputStream();
                ArrayList<DriveKeyModel> datas = getDriveKeyModelArrayList(is);
                DriveKeyModel cle_retenu = null;
                for (DriveKeyModel data : datas) {
                    if (data.getLicence().equals(cle_fournie.getLicence()) && data.getOwner().equals(cle_fournie.getOwner()) && data.getTelephone().equals(cle_fournie.getTelephone()) && data.getEmail().equals(cle_fournie.getEmail())) {
                        cle_retenu = data;
                        error = "";
                        break;
                    }
                }
                listener.onSuccess(cle_retenu);
                listener.onError(error);
            } catch (Exception e) {
                error = "impossible d'effectuer l'operation";
                listener.onError(error);
            }
        });
    }

    private ArrayList<DriveKeyModel> getDriveKeyModelArrayList(InputStream is) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            stringBuilder.append(line);
        }
        Gson gson = new Gson();
        Type keytype = new TypeToken<ArrayList<DriveKeyModel>>(){}.getType();
        return gson.fromJson(stringBuilder.toString(), keytype);

    }

    public interface OnFileReadListener {
        void onSuccess(DriveKeyModel content);
        void onError(String error);
    }
}
