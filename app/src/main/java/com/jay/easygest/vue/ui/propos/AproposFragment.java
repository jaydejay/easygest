package com.jay.easygest.vue.ui.propos;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.jay.easygest.databinding.FragmentAproposBinding;


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
        PackageInfo packageInfo;
        try {
            packageInfo = getContext().getPackageManager().getPackageInfo(getContext().getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException(e);
        }
        String versionName = packageInfo.versionName;
        String version ="EasyGest version : " + versionName;
        binding.txteasygestversion.setText(version);
    }
}