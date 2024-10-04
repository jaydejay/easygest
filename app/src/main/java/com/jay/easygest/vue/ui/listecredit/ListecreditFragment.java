package com.jay.easygest.vue.ui.listecredit;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.jay.easygest.controleur.Creditcontrolleur;
import com.jay.easygest.databinding.FragmentListecreditBinding;
import com.jay.easygest.model.CreditModel;
import com.jay.easygest.outils.SessionManagement;
import com.jay.easygest.vue.MainActivity;
import com.jay.easygest.vue.ui.credit.CreditViewModel;

import java.util.ArrayList;


public class ListecreditFragment extends Fragment {


    private SessionManagement sessionManagement;
    private FragmentListecreditBinding binding;
    private  CreditViewModel creditViewModel;
    private ListecreditAdapter adapter;
    private  ArrayList<CreditModel> listecredits;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        sessionManagement = new SessionManagement(requireContext());
        binding = FragmentListecreditBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        Creditcontrolleur creditcontrolleur = Creditcontrolleur.getCreditcontrolleurInstance(getContext());
        creditcontrolleur.listecredits();
        creditViewModel = new  ViewModelProvider(this).get(CreditViewModel.class);
        creerListe();
        recherchercredit();
        return root;
    }


    public void creerListe() {
        try {
            listecredits = creditViewModel.getCredits().getValue();
            adapter = new ListecreditAdapter(getContext(), listecredits);
            adapter.notifyDataSetChanged();
            binding.lstviewcredits.setAdapter(adapter);
        }catch (Exception e){}


    }

    public  ArrayList<CreditModel> getFilter(String mtext){
        ArrayList<CreditModel> filterliste = new ArrayList<>();
        try {

            for (CreditModel credit : listecredits) {
                if (credit.getClient() != null){
                    if (credit.getClient().getCodeclient().contains(mtext) || credit.getClient().getNom().contains(mtext) || credit.getClient().getPrenoms().contains(mtext)  ){
                        filterliste.add(credit);
                    }
                }

            }
            return filterliste;
        }catch (Exception e){
            return listecredits;
        }


    }

    public void recherchercredit(){

        binding.searchcredit.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                ArrayList<CreditModel> credits = ListecreditFragment.this.getFilter(newText);
                try {
                    adapter = new ListecreditAdapter(getContext(),credits );
                    adapter.notifyDataSetChanged();
                    binding.lstviewcredits.setAdapter(adapter);
                }catch (Exception e){
                    return true;
                }
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
            startActivity(intent);
        }
    }
}