package com.jay.easygest.vue.ui.clients;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.jay.easygest.controleur.Clientcontrolleur;
import com.jay.easygest.databinding.FragmentClientBinding;
import com.jay.easygest.model.ClientModel;
import com.jay.easygest.outils.SessionManagement;
import com.jay.easygest.vue.MainActivity;

import java.util.ArrayList;


public class ClientsFragment extends Fragment {

    private Clientcontrolleur clientcontrolleur;
    private ClientViewModel clientViewModel;
    private FragmentClientBinding binding;
    private ListeClientAdapter adapter;
    private SessionManagement sessionManagement;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        sessionManagement = new SessionManagement(requireContext());
       clientcontrolleur =Clientcontrolleur.getClientcontrolleurInstance(getContext());
       clientViewModel = new ViewModelProvider(this).get(ClientViewModel.class);
       clientcontrolleur.listeClients();
       binding = FragmentClientBinding.inflate(inflater,container,false);

       View root = binding.getRoot();
       creerListe();
       rechercherclient();
       return root;
    }

    public void creerListe() {
        try {
            ArrayList<ClientModel> listeclients = clientViewModel.getListeClients().getValue();
            adapter = new ListeClientAdapter(getContext(),listeclients);
            binding.lstviewclients.setAdapter(adapter);
        }catch (Exception e){
            binding.lstviewclients.setAdapter(null);
        }


    }

    public ArrayList<ClientModel> getFilter(String mtext){

        ArrayList<ClientModel> filterliste = new ArrayList<>();
        ArrayList<ClientModel> listeclients = clientcontrolleur.listeClients();

        for (ClientModel client: listeclients) {

            if (client.getCodeclient().contains(mtext) || client.getNom().contains(mtext) || client.getPrenoms().contains(mtext)  ){
                filterliste.add(client);
            }

        }
        return filterliste;
    }


    public void rechercherclient(){

        binding.searchclient.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                ArrayList<ClientModel> clients = ClientsFragment.this.getFilter(newText);

                adapter = new ListeClientAdapter(getContext(),clients );
                binding.lstviewclients.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                return false;
            }
        });
    }


    public void onStart() {
        super.onStart();

    }


    @Override
    public void onResume() {
        super.onResume();
        if (!sessionManagement.getSession()){
            Intent intent = new Intent(getActivity(), MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }
}