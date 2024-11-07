package com.jay.easygest.vue.ui.versement;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.jay.easygest.R;
import com.jay.easygest.controleur.Creditcontrolleur;
import com.jay.easygest.controleur.Versementcontrolleur;
import com.jay.easygest.databinding.FragmentVersementBinding;
import com.jay.easygest.model.AppKessModel;
import com.jay.easygest.model.ClientModel;
import com.jay.easygest.model.SmsnoSentModel;
import com.jay.easygest.outils.AccessLocalAppKes;
import com.jay.easygest.outils.MesOutils;
import com.jay.easygest.outils.SessionManagement;
import com.jay.easygest.outils.SmsSender;
import com.jay.easygest.vue.AfficherclientActivity;
import com.jay.easygest.vue.MainActivity;
import com.jay.easygest.vue.ui.clients.ClientViewModel;
import com.jay.easygest.vue.ui.credit.CreditViewModel;

import java.util.Date;
import java.util.Objects;

public class VersementFragment extends Fragment {

    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 2;
    public static final String CREDIT = "credit";
    private SessionManagement sessionManagement;
    private SmsSender smsSender;
    private FragmentVersementBinding binding;
    private Versementcontrolleur versementcontrolleur;
    private ClientViewModel clientViewModel;
    private CreditViewModel creditViewModel;
    private ClientModel client;
//    private Integer total_reste_credit;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        sessionManagement = new SessionManagement(requireContext());
         smsSender = new SmsSender(getContext(),getActivity());
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
            binding.btnversement.setEnabled(false);
            String somme_versee = binding.editversementsomme.getText().toString().trim();
            String  date = binding.editversementdate.getText().toString().trim();

                if ( somme_versee.isEmpty() || date.isEmpty()){
                    Toast.makeText(getContext(), "champs obligatoires", Toast.LENGTH_SHORT).show();
                    binding.btnversement.setEnabled(true);

                } else if ( MesOutils.convertStringToDate(date) == null) {
                    Toast.makeText(getActivity(), "format de date incorrect", Toast.LENGTH_LONG).show();
                    binding.btnversement.setEnabled(true);

                } else {

                        try {

                            if (Objects.equals(client.getCodeclient(),binding.editversementcodeclt.getText().toString().trim())){
                                int somme_total_credit = creditViewModel.getTotalcreditsclient().getValue();
                                int total_reste_credit_avant = creditViewModel.getTotalrestesclient().getValue();

                                if (   Integer.parseInt(somme_versee) > 0 &  Integer.parseInt(somme_versee) <= somme_total_credit){

                                    if (ActivityCompat.checkSelfPermission(requireContext(),
                                            android.Manifest.permission.SEND_SMS) !=
                                            PackageManager.PERMISSION_GRANTED) {
                                        ActivityCompat.requestPermissions(requireActivity(),
                                                new String[]{android.Manifest.permission.SEND_SMS},
                                                MY_PERMISSIONS_REQUEST_SEND_SMS);
                                        binding.btnversement.setEnabled(true);
                                    } else {
                                        int sommeverse;
                                        if ( Integer.parseInt(somme_versee) >= total_reste_credit_avant){
                                            sommeverse = total_reste_credit_avant;
                                        }else {
                                            sommeverse =  Integer.parseInt(somme_versee) ;
                                        }
                                        boolean success = versementcontrolleur.ajouterversement(client,sommeverse,date );
                                        if (success) {

                                            AccessLocalAppKes accessLocalAppKes = new AccessLocalAppKes(getContext());
                                            AppKessModel appKessModel = accessLocalAppKes.getAppkes();
                                            String expediteurName = appKessModel.getOwner();

                                            int total_reste_credit = creditViewModel.getTotalrestesclient().getValue();

//                                        String destinationAdress = "+225"+client.getTelephone();
                                            String destinationAdress = "5556";
                                            String nomDestinataire = client.getNom();
                                            String prenomsDestinataire = client.getPrenoms();

                                            String messageBody = expediteurName +"\n"+"\n"
                                                    + nomDestinataire + " "+prenomsDestinataire +"\n"
                                                    +"vous avez fait un versement de "+sommeverse+" FCFA"+" pour votre credit"+"\n"
                                                    +"le "+date+"\n"
                                                    +"reste à payer : "+total_reste_credit ;

                                            SmsnoSentModel smsnoSentModel = new SmsnoSentModel(client.getId(),messageBody);
                                            smsSender.smsSendwithInnerClass(messageBody, destinationAdress, client.getId() );
                                            smsSender.sentReiceiver(smsnoSentModel);

                                        } else {
                                            Toast.makeText(getContext(), "revoyez le versement ", Toast.LENGTH_SHORT).show();
                                            binding.btnversement.setEnabled(true);
                                        }

                                    }

                                }else {
                                    Toast.makeText(getContext(), "la somme versée superieur au crédit ou est égale à 0", Toast.LENGTH_SHORT).show();
                                    binding.btnversement.setEnabled(true);
                                }

                            }else {
                                Toast.makeText(getContext(), "le client ne correspond pas", Toast.LENGTH_SHORT).show();
                                binding.btnversement.setEnabled(true);
                            }

                        } catch (Exception e) {
                            Toast.makeText(getContext(), "erreur versement avorté", Toast.LENGTH_SHORT).show();
                            binding.btnversement.setEnabled(true);
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
//        smsSender.sentReiceiver();
//        smsSender.deliveredReceiver();

    }
}