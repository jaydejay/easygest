package com.jay.easygest.vue.ui.account;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jay.easygest.R;
import com.jay.easygest.databinding.FragmentListeAccountsClientBinding;
import com.jay.easygest.model.AccountModel;
import com.jay.easygest.vue.ui.clients.ClientViewModel;
import com.jay.easygest.vue.ui.listecredit.ListecreditClientAdapter;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ListeAccountsClientFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ListeAccountsClientFragment extends Fragment {


    private ClientViewModel clientViewModel;
    private AccountViewModel accountViewModel;
    private  ListeaccountAdapter adapter;
    private ArrayList<AccountModel> listeaccounts;
    private FragmentListeAccountsClientBinding binding;

    public ListeAccountsClientFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment ListeAccountsClientFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ListeAccountsClientFragment newInstance() {
        ListeAccountsClientFragment fragment = new ListeAccountsClientFragment();

        return fragment;
    }

//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//        this.clientViewModel = new ViewModelProvider(this).get(ClientViewModel.class);
//        this.accountViewModel = new ViewModelProvider(this).get(AccountViewModel.class);
//        this.listeaccounts = accountViewModel.getAccount_solde_ou_non().getValue();
//    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentListeAccountsClientBinding.inflate(inflater,container,false);

        this.clientViewModel = new ViewModelProvider(this).get(ClientViewModel.class);
        this.accountViewModel = new ViewModelProvider(this).get(AccountViewModel.class);
        this.listeaccounts = accountViewModel.getAccount_solde_ou_non().getValue();
        // Inflate the layout for this fragment
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
}