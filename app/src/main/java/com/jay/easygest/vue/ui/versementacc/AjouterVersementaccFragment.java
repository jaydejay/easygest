package com.jay.easygest.vue.ui.versementacc;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.jay.easygest.R;
import com.jay.easygest.controleur.Versementacccontrolleur;
import com.jay.easygest.databinding.FragmentAjouterVersementaccBinding;
import com.jay.easygest.model.ClientModel;
import com.jay.easygest.outils.AccessLocalVersementacc;
import com.jay.easygest.outils.MesOutils;
import com.jay.easygest.vue.AfficherclientActivity;
import com.jay.easygest.vue.ui.account.AccountViewModel;
import com.jay.easygest.vue.ui.clients.ClientViewModel;

import java.util.Date;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AjouterVersementaccFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AjouterVersementaccFragment extends Fragment {

    private FragmentAjouterVersementaccBinding binding;
    private ClientViewModel clientViewModel;
    private ClientModel client;
    private AccountViewModel accountViewModel;
    private Versementacccontrolleur versementacccontrolleur;

    public AjouterVersementaccFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     *
     * @return A new instance of fragment AjouterVersementaccFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AjouterVersementaccFragment newInstance() {
        AjouterVersementaccFragment fragment = new AjouterVersementaccFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        versementacccontrolleur = Versementacccontrolleur.getVersementacccontrolleurInstance(getActivity());
        clientViewModel = new ViewModelProvider(this).get(ClientViewModel.class);
        client = clientViewModel.getClient().getValue();

        accountViewModel = new ViewModelProvider(this).get(AccountViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       binding = FragmentAjouterVersementaccBinding.inflate(inflater,container,false);
       ajouterversement();
       desactiverEditextCodeclient();
        return binding.getRoot();
    }

    public void ajouterversement(){
        binding.btnversementacc.setOnClickListener(v -> {
            String somme_versee = binding.ajoutervrsmntaccsomme.getText().toString().trim();
            String  date = binding.ajoutervrsmntaccdate.getText().toString().trim();
            Date date_versement_account  = MesOutils.convertStringToDate(date);

            if ( somme_versee.isEmpty() || date.isEmpty()){
                Toast.makeText(getContext(), "champs obligatoires", Toast.LENGTH_SHORT).show();

            } else if ( date_versement_account == null) {
                Toast.makeText(getActivity(), "format de date incorrect", Toast.LENGTH_LONG).show();

            } else {
                if (Integer.parseInt(somme_versee) >= 1000) {

                    try {
                        String codeclient = binding.ajoutervrsmtacccodeclt.getText().toString();
                        String dateversement = binding.ajoutervrsmntaccdate.getText().toString();
                        Integer sommeverse = Integer.parseInt(somme_versee);
                        if (Objects.equals(client.getCodeclient(), codeclient)){

                            int somme_total_account = accountViewModel.getTotalaccountsclient().getValue();
                            if (  sommeverse > 0 & sommeverse <= somme_total_account){

                                boolean success = versementacccontrolleur.ajouterversement(client,sommeverse,dateversement );
                                if (success) {
                                    Intent intent = new Intent(getActivity(), AfficherclientActivity.class);
//                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                } else {
                                    Toast.makeText(getContext(), "revoyez le versement ", Toast.LENGTH_SHORT).show();
                                }
                            }

                        }else {Toast.makeText(getContext(), "le model n'est pas a jour", Toast.LENGTH_SHORT).show();}

                    } catch (Exception e) {
                        Toast.makeText(getContext(), "erreur versement avorté", Toast.LENGTH_SHORT).show();
                    }
                }else {Toast.makeText(getContext(), "le verement doit etre de 1000 F minimum", Toast.LENGTH_SHORT).show();}

            }
        });
    }

    public void desactiverEditextCodeclient(){
        clientViewModel.getClient().observe(getViewLifecycleOwner(),clientModel -> {
            if (clientModel != null){
                binding.ajoutervrsmtacccodeclt.setText(clientModel.getCodeclient());
                binding.ajoutervrsmtacccodeclt.setEnabled(false);
            }
        });

    }
}