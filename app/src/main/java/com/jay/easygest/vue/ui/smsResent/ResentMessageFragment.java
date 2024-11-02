package com.jay.easygest.vue.ui.smsResent;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.jay.easygest.R;
import com.jay.easygest.controleur.SmsSendercontrolleur;
import com.jay.easygest.databinding.FragmentResentMessageBinding;
import com.jay.easygest.model.SmsnoSentModel;
import com.jay.easygest.outils.SmsSender;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ResentMessageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ResentMessageFragment extends Fragment {

//    // TODO: Rename parameter arguments, choose names that match
//    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
//    private static final String ARG_PARAM1 = "param1";
//    private static final String ARG_PARAM2 = "param2";
//
//    // TODO: Rename and change types of parameters
//    private String mParam1;
//    private String mParam2;

    private FragmentResentMessageBinding binding;
    private SmsSender smsSender;
    private ArrayList<SmsnoSentModel>  smss_to_send;
    private  SmsSendercontrolleur smsSendercontrolleur;

    public ResentMessageFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment ResentMessageFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ResentMessageFragment newInstance() {
        ResentMessageFragment fragment = new ResentMessageFragment();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);


//        }
         smsSendercontrolleur = SmsSendercontrolleur.getSmsSendercotrolleurInstance(getContext());
         smss_to_send =  smsSendercontrolleur.getSmsnoSentList();


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentResentMessageBinding.inflate(getLayoutInflater());
        smsSender = new SmsSender(getContext(),getActivity());
        if (smss_to_send.size() > 0){ sendingUnSentMsg(smss_to_send);}else{ binding.txtvResentLoading.setText(getResources().getString(R.string.not_sms));}
//        sendingUnSentMsg();
//        if (smss_to_send.isEmpty()){
//            binding.txtvResentLoading.setText(getResources().getString(R.string.not_sms));
//        }

        return inflater.inflate(R.layout.fragment_resent_message, container, false);

    }

    public void sendingUnSentMsg(ArrayList<SmsnoSentModel> smss ){
        ExecutorService executor = Executors.newSingleThreadExecutor();
        for ( SmsnoSentModel sms : smss) {
            Future<String> future = executor.submit(() -> {
                try {
//                Thread.sleep(200);
//                    smsSender.checkForSmsPermissionBeforeSend(sms.getClient(),sms.getSommeverse(),sms.getSommetotal(),sms.getTotalreste(),sms.getOperation(),sms.getDateoperation());
//                    smsSender.sentReiceiver();
//                    smsSender.deliveredReceiver();
                } catch (Exception e) {
                    //do nothing
                }
                return "message envoyé";

            });
            try {
                String result = future.get();
//                System.out.println("The Result: " + result);
                Toast.makeText(getContext(), result, Toast.LENGTH_LONG).show();
                smsSendercontrolleur.delete(sms);
            } catch (Exception e) {
                //do nothing
            }
        }
        executor.shutdown();
        binding.txtvResentLoading.setText(getResources().getString(R.string.sending_finish));
    }

    @Override
    public void onResume() {
        super.onResume();
//        smsSender.sentReiceiver();
//        smsSender.deliveredReceiver();
    }
}