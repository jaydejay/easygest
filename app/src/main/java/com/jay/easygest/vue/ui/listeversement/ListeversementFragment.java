package com.jay.easygest.vue.ui.listeversement;



import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;


import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import com.jay.easygest.controleur.Versementcontrolleur;
import com.jay.easygest.databinding.FragmentListeversementBinding;

import com.jay.easygest.model.ClientModel;
import com.jay.easygest.model.VersementsModel;
import com.jay.easygest.vue.ui.clients.ClientViewModel;
import com.jay.easygest.vue.ui.versement.VersementViewModel;

import java.util.ArrayList;


public class ListeversementFragment extends Fragment {

    private ListeversementAdapter adapter;
    private FragmentListeversementBinding binding;
    private Versementcontrolleur versementcontrolleur;
    private ClientViewModel clientViewModel;
    private VersementViewModel versementViewModel;
    private ClientModel client ;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {


        binding = FragmentListeversementBinding.inflate(inflater,container,false);
        versementcontrolleur = Versementcontrolleur.getVersementcontrolleurInstance(getContext());
        clientViewModel = new ViewModelProvider(this).get(ClientViewModel.class);
        versementViewModel = new ViewModelProvider(this).get(VersementViewModel.class);
        client = clientViewModel.getClient().getValue();

        View root = binding.getRoot();
        creerListe();
        rechercherversement();
        return root;
    }



    public void creerListe() {

        ArrayList<VersementsModel> listeversements = new ArrayList<>();
        ArrayList<VersementsModel> versementViewModel_listeversements = versementViewModel.getMversements().getValue();

        if (listeversements != null){
           listeversements = versementViewModel_listeversements;

        }else {
           listeversements = versementcontrolleur.listeversements();
        }
        adapter = new ListeversementAdapter(listeversements,getContext());
        binding.lstviewversement.setAdapter(adapter);
    }

    public ArrayList<VersementsModel> getFilter(String mtext){

        ArrayList<VersementsModel> filterliste = new ArrayList<>();
        ArrayList<VersementsModel> listeVersements = versementcontrolleur.listeversements();

        for (VersementsModel versement: listeVersements) {

            if (versement.getClient().getCodeclient().contains(mtext) ){
                filterliste.add(versement);
            }

        }
        return filterliste;
    }

    public void rechercherversement(){

        binding.searchversement.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                ArrayList<VersementsModel> versements = ListeversementFragment.this.getFilter(newText);

                adapter = new ListeversementAdapter(versements,getContext() );
                binding.lstviewversement.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                return false;
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}