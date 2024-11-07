package com.jay.easygest.vue.ui.versementacc;

import static com.jay.easygest.outils.VariablesStatique.MY_PERMISSIONS_REQUEST_SEND_SMS;

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

import com.jay.easygest.controleur.Versementacccontrolleur;
import com.jay.easygest.databinding.FragmentAjouterVersementaccBinding;
import com.jay.easygest.model.AppKessModel;
import com.jay.easygest.model.ClientModel;
import com.jay.easygest.model.SmsnoSentModel;
import com.jay.easygest.outils.AccessLocalAppKes;
import com.jay.easygest.outils.MesOutils;
import com.jay.easygest.outils.SessionManagement;
import com.jay.easygest.outils.SmsSender;
import com.jay.easygest.outils.VariablesStatique;
import com.jay.easygest.vue.AfficherclientActivity;
import com.jay.easygest.vue.MainActivity;
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

    public static final String ACCOUNT = "account";

    private SessionManagement sessionManagement;

    private FragmentAjouterVersementaccBinding binding;
   private SmsSender smsSender ;
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
     * @return A new instance of fragment AjouterVersementaccFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AjouterVersementaccFragment newInstance() {
        return  new AjouterVersementaccFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sessionManagement = new SessionManagement(getContext());
        versementacccontrolleur = Versementacccontrolleur.getVersementacccontrolleurInstance(getActivity());

        smsSender = new SmsSender(getContext(),getActivity());
        clientViewModel = new ViewModelProvider(this).get(ClientViewModel.class);
        client = clientViewModel.getClient().getValue();
        accountViewModel = new ViewModelProvider(this).get(AccountViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       binding = FragmentAjouterVersementaccBinding.inflate(inflater,container,false);
       ajouterversement();
       desactiverEditextCodeclient();
        return binding.getRoot();
    }

    public void ajouterversement(){
        binding.btnversementacc.setOnClickListener(v -> {
            binding.btnversementacc.setEnabled(false);
            String somme_versee = binding.ajoutervrsmntaccsomme.getText().toString().trim();
            String  date = binding.ajoutervrsmntaccdate.getText().toString().trim();
//            Date date_versement_account  = MesOutils.convertStringToDate(date);

            if ( somme_versee.isEmpty() || date.isEmpty()){
                Toast.makeText(getContext(), "champs obligatoires", Toast.LENGTH_SHORT).show();
                binding.btnversementacc.setEnabled(true);

            } else if ( MesOutils.convertStringToDate(date) == null) {
                Toast.makeText(getActivity(), "format de date incorrect", Toast.LENGTH_LONG).show();
                binding.btnversementacc.setEnabled(true);

            } else {

                try {
//                    String codeclient = binding.ajoutervrsmtacccodeclt.getText().toString().trim();
//                    String dateversement = binding.ajoutervrsmntaccdate.getText().toString();
//                    int sommeverse_formulaire = Integer.parseInt(somme_versee);
                    if (Objects.equals(client.getCodeclient(), binding.ajoutervrsmtacccodeclt.getText().toString().trim())){

                        int somme_total_account = accountViewModel.getTotalaccountsclient().getValue();
                        int total_reste_account_avant = accountViewModel.getTotalrestesclient().getValue();
                        if (  Integer.parseInt(somme_versee) > 0 & Integer.parseInt(somme_versee) <= somme_total_account){

                            if (ActivityCompat.checkSelfPermission(requireContext(),
                                    android.Manifest.permission.SEND_SMS) !=
                                    PackageManager.PERMISSION_GRANTED) {
                                ActivityCompat.requestPermissions(requireActivity(),
                                        new String[]{android.Manifest.permission.SEND_SMS},
                                        MY_PERMISSIONS_REQUEST_SEND_SMS);
                                binding.btnversementacc.setEnabled(true);
                            } else {
                                int sommeverse;
                                if (Integer.parseInt(somme_versee) >= total_reste_account_avant){
                                    sommeverse = total_reste_account_avant;
                                }else {
                                    sommeverse = Integer.parseInt(somme_versee);
                                }

                                boolean success = versementacccontrolleur.ajouterversement(client,sommeverse,date );
                                if (success) {
                                    AccessLocalAppKes accessLocalAppKes = new AccessLocalAppKes(getContext());
                                    AppKessModel appKessModel = accessLocalAppKes.getAppkes();
                                    String expediteurName = appKessModel.getOwner();

                                    int total_reste_account = accountViewModel.getTotalrestesclient().getValue();
                                    String destinationAdress = "+225"+client.getTelephone();
//                                String destinationAdress = VariablesStatique.EMULATEUR_2_TELEPHONE;
                                    String nomDestinataire = client.getNom();
                                    String prenomsDestinataire = client.getPrenoms();

                                    String messageBody = expediteurName +"\n"+"\n"
                                            + nomDestinataire + " "+prenomsDestinataire +"\n"
                                            +"vous avez fait un versement de "+sommeverse+" FCFA"+" pour votre account"+"\n"
                                            +"le "+date+"\n"
                                            +"reste à payer : "+total_reste_account ;

                                    SmsnoSentModel smsnoSentModel = new SmsnoSentModel(client.getId(),messageBody);
                                    smsSender.smsSendwithInnerClass(messageBody, destinationAdress, client.getId() );
                                    smsSender.sentReiceiver(smsnoSentModel);

                                } else {
                                    Toast.makeText(getContext(), "revoyez le versement ", Toast.LENGTH_SHORT).show();
                                    binding.btnversementacc.setEnabled(true);
                                }
                            }
                        }else {
                            Toast.makeText(getContext(), "la somme versée superieur au crédit ou est égale à 0", Toast.LENGTH_SHORT).show();
                            binding.btnversementacc.setEnabled(true);
                        }

                    }else {
                        Toast.makeText(getContext(), "le client ne correspond pas", Toast.LENGTH_SHORT).show();
                        binding.btnversementacc.setEnabled(true);
                    }

                } catch (Exception e) {
                    Toast.makeText(getContext(), "erreur versement avorté", Toast.LENGTH_SHORT).show();
                    binding.btnversementacc.setEnabled(true);
                }
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