package com.jay.easygest.controleur;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;

import com.jay.easygest.model.ClientModel;
import com.jay.easygest.outils.AccessLocalClient;

import java.util.ArrayList;

public class Clientcontrolleur {

    private static Clientcontrolleur clientcontrolleurInstance = null;
    private static AccessLocalClient accessLocalClient;
    private final MutableLiveData<ClientModel> mclient = new MutableLiveData<>();
    private final MutableLiveData<ArrayList<ClientModel>> mlisteClients = new MutableLiveData<>() ;
    private  ClientModel client;
   private ArrayList<ClientModel> clients;


    public static Clientcontrolleur getClientcontrolleurInstance(Context contexte){
        if (clientcontrolleurInstance == null){
            clientcontrolleurInstance = new Clientcontrolleur();
            accessLocalClient = new AccessLocalClient(contexte);
        }
        return clientcontrolleurInstance;
    }


    public MutableLiveData<ClientModel>  getMclient() { return this.mclient;}
    public void setMclient(ClientModel client) {
        this.mclient.setValue(client);
    }
    public MutableLiveData<ArrayList<ClientModel>> getMlisteClients() { return mlisteClients;}
    public void setMlisteClients(ArrayList<ClientModel> listeClients) {this.mlisteClients.setValue(listeClients);
    }

    public ClientModel  getClient() { return this.client;}
    public void setClient(ClientModel client) {
        this.client = client;
        setMclient(client);

    }

    public ArrayList<ClientModel> getClients() {
        return clients;
    }
    public void setClients(ArrayList<ClientModel> clients) {
        this.clients = clients;
        setMlisteClients(clients);
    }

    public ArrayList<ClientModel> listeClients() {
        ArrayList<ClientModel> clients = accessLocalClient.listeClients();
        setMlisteClients(clients);
        return clients;
    }

    public ClientModel recupererClient(Integer clientid){ return accessLocalClient.recupUnClient(clientid); }

    public boolean modifierclient(ClientModel clientModel) {
       boolean success = accessLocalClient.modifierclient(clientModel);
       if (success){
           ClientModel client = this.recupererClient(clientModel.getId());
           this.setMclient(client);
           this.listeClients();
       }
        return success;
    }

    public boolean supprimerclient(ClientModel client) {return  accessLocalClient.supprimerclient(client);  }



}
