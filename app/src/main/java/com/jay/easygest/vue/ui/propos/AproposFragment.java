package com.jay.easygest.vue.ui.propos;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.jay.easygest.databinding.FragmentAproposBinding;
import com.jay.easygest.outils.MesOutils;


/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class AproposFragment extends Fragment {

    private FragmentAproposBinding binding;



    public AproposFragment() {
        // Required empty public constructor

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAproposBinding.inflate(inflater, container, false);

        View root = binding.getRoot();
        init();
        return root;
    }

    private void init(){

        try {

            String version ="EasyGest version : " + MesOutils.versionapp;
            binding.txteasygestversion.setText(version);

        } catch (Exception e) {
            Toast.makeText(getContext(), "erreure de donnees", Toast.LENGTH_SHORT).show();
        }

    }
}