package com.jay.easygest.vue.ui.account;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jay.easygest.controleur.Accountcontroller;
import com.jay.easygest.databinding.FragmentListeaccountBinding;
import com.jay.easygest.model.AccountModel;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class ListeaccountFragment extends Fragment {

    private FragmentListeaccountBinding binding;
    private AccountViewModel accountViewModel;
    private Accountcontroller accountcontroller;
    private ArrayList<AccountModel> accounts;
    private ListeaccountAdapter adapter;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        accountViewModel = new ViewModelProvider(this).get(AccountViewModel.class);
        accountcontroller = Accountcontroller.getAccountcontrolleurInstance(getContext());
        accountcontroller.listeaccounts();
        accounts = accountViewModel.getAccounts().getValue();

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentListeaccountBinding.inflate(inflater, container, false);
        creerListe();
        // Inflate the layout for this fragment
        return binding.getRoot();
    }


    public void creerListe() {
        try {
            accounts = accountViewModel.getAccounts().getValue();
            adapter = new ListeaccountAdapter(getContext(), accounts);
            adapter.notifyDataSetChanged();
            binding.lstviewaccounts.setAdapter(adapter);
        }catch (Exception e){
            //
        }
    }
}