package com.jay.easygest.vue.ui.versement;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.jay.easygest.controleur.Creditcontrolleur;
import com.jay.easygest.controleur.Versementcontrolleur;
import com.jay.easygest.databinding.FragmentVersementBinding;
import com.jay.easygest.model.ClientModel;
import com.jay.easygest.outils.MesOutils;
import com.jay.easygest.outils.SessionManagement;
import com.jay.easygest.vue.AfficherclientActivity;
import com.jay.easygest.vue.MainActivity;
import com.jay.easygest.vue.ui.clients.ClientViewModel;
import com.jay.easygest.vue.ui.credit.CreditViewModel;

import java.util.Date;
import java.util.Objects;

public class VersementFragment extends Fragment {

    private SessionManagement sessionManagement;
    private FragmentVersementBinding binding;
    private Versementcontrolleur versementcontrolleur;
    private ClientViewModel clientViewModel;
    private CreditViewModel creditViewModel;
    private ClientModel client;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        sessionManagement = new SessionManagement(requireContext());

        binding = FragmentVersementBinding.inflate(inflater, container, false);
        this.versementcontrolleur = Versementcontrolleur.getVersementcontrolleurInstance(getContext());
        Creditcontrolleur creditcontrolleur = Creditcontrolleur.getCreditcontrolleurInstance(getContext());

        clientViewModel = new ViewModelProvider(this).get(ClientViewModel.class);
        creditViewModel = new ViewModelProvider(this).get(CreditViewModel.class);
        client = clientViewModel.getClient().getValue();
        creditcontrolleur.setRecapTcreditClient(client);

        ajouterversement();
        desactiverEditextCodeclient();
        return binding.getRoot();
    }

    public void ajouterversement(){
        binding.btnversement.setOnClickListener(v -> {
            String somme_versee = binding.editversementsomme.getText().toString().trim();
            String  date = binding.editversementdate.getText().toString().trim();
            Date date_versement_credit  = MesOutils.convertStringToDate(date);

                if ( somme_versee.isEmpty() || date.isEmpty()){
                    Toast.makeText(getContext(), "champs obligatoires", Toast.LENGTH_SHORT).show();

                } else if ( date_versement_credit == null) {
                    Toast.makeText(getActivity(), "format de date incorrect", Toast.LENGTH_LONG).show();

                } else {

                        try {
                            String codeclient = binding.editversementcodeclt.getText().toString();
                            String dateversement = binding.editversementdate.getText().toString();
                            int sommeverse = Integer.parseInt(somme_versee);
                            if (Objects.equals(client.getCodeclient(), codeclient)){

                                int somme_total_credit = creditViewModel.getTotalcreditsclient().getValue();

                                if (  sommeverse > 0 & sommeverse <= somme_total_credit){

                                    boolean success = versementcontrolleur.ajouterversement(client,sommeverse,dateversement );
                                    if (success) {
                                        Intent intent = new Intent(getActivity(), AfficherclientActivity.class);
                                        startActivity(intent);
                                    } else {
                                        Toast.makeText(getContext(), "revoyez le versement ", Toast.LENGTH_SHORT).show();
                                    }
                                }else {Toast.makeText(getContext(), "la somme versée superieur au crédit ou est égale à 0", Toast.LENGTH_SHORT).show();}

                            }else {Toast.makeText(getContext(), "le client ne correspond pas", Toast.LENGTH_SHORT).show();}

                        } catch (Exception e) {
                            Toast.makeText(getContext(), "erreur versement avorté", Toast.LENGTH_SHORT).show();
                        }

                }
        });
    }

    public void desactiverEditextCodeclient(){
        clientViewModel.getClient().observe(getViewLifecycleOwner(),clientModel -> {
            if (clientModel != null){
                binding.editversementcodeclt.setText(clientModel.getCodeclient());
                binding.editversementcodeclt.setEnabled(false);
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