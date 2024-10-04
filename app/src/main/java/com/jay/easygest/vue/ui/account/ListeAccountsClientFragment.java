package com.jay.easygest.vue.ui.account;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.jay.easygest.databinding.FragmentListeAccountsClientBinding;
import com.jay.easygest.model.AccountModel;
import com.jay.easygest.outils.SessionManagement;
import com.jay.easygest.vue.MainActivity;
import com.jay.easygest.vue.ui.clients.ClientViewModel;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class ListeAccountsClientFragment extends Fragment {


    private SessionManagement sessionManagement;
    private ClientViewModel clientViewModel;
    private  ListeaccountAdapter adapter;
    private ArrayList<AccountModel> listeaccounts;
    private FragmentListeAccountsClientBinding binding;

    public ListeAccountsClientFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        sessionManagement = new SessionManagement(requireContext());
        binding = FragmentListeAccountsClientBinding.inflate(inflater,container,false);

        this.clientViewModel = new ViewModelProvider(this).get(ClientViewModel.class);
        AccountViewModel accountViewModel = new ViewModelProvider(this).get(AccountViewModel.class);
        this.listeaccounts = accountViewModel.getAccount_solde_ou_non().getValue();
        creerListe();
        return binding.getRoot();
    }

    public void creerListe() {

        clientViewModel.getClient().observe(getViewLifecycleOwner(),clientModel -> {

            adapter = new ListeaccountAdapter(getContext(), listeaccounts);
            adapter.notifyDataSetChanged();
            binding.accountlistv.setAdapter(adapter);

        });

    }

    public void onStart() {
        super.onStart();
        if (!sessionManagement.getSession()){
            Intent intent = new Intent(getActivity(), MainActivity.class);
            startActivity(intent);

        }

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