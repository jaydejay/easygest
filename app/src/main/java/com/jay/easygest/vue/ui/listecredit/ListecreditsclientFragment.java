package com.jay.easygest.vue.ui.listecredit;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.jay.easygest.controleur.Creditcontrolleur;
import com.jay.easygest.databinding.FragmentListecreditsclientBinding;
import com.jay.easygest.model.CreditModel;
import com.jay.easygest.vue.ui.clients.ClientViewModel;
import com.jay.easygest.vue.ui.credit.CreditViewModel;

import java.util.ArrayList;


public class ListecreditsclientFragment extends Fragment {


    private FragmentListecreditsclientBinding binding;

    private ListecreditClientAdapter adapter;
    private Creditcontrolleur creditcontrolleur;
    private ClientViewModel clientViewModel;
    private  CreditViewModel creditViewModel;
    private  ArrayList<CreditModel> listecredits;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentListecreditsclientBinding.inflate(inflater, container, false);

        View root = binding.getRoot();

         creditViewModel = new ViewModelProvider(this).get(CreditViewModel.class);
        clientViewModel = new ViewModelProvider(this).get(ClientViewModel.class);
        creditcontrolleur = Creditcontrolleur.getCreditcontrolleurInstance(getContext());
        listecredits = creditViewModel.getCredits_client_soldes_ou_non().getValue();
        hideSomeItem();
        creerListe();
        return root;
    }

    public void creerListe() {

        clientViewModel.getClient().observe(getViewLifecycleOwner(),clientModel -> {

            adapter = new ListecreditClientAdapter(getContext(), listecredits);
            adapter.notifyDataSetChanged();
            binding.lstviewcredits.setAdapter(adapter);

        });

    }

    private void hideSomeItem(){
        if (creditcontrolleur.getIdmenu() == 0){
            binding.textView7.setVisibility(View.GONE);
            binding.textView8.setVisibility(View.GONE);
        }

    }


    @Override
    public void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}