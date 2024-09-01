package com.jay.easygest.vue.ui.versementacc;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.jay.easygest.databinding.FragmentListeVersementaccBinding;
import com.jay.easygest.model.VersementsaccModel;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ListeVersementaccFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class  ListeVersementaccFragment extends Fragment {
    private FragmentListeVersementaccBinding binding;

    private ArrayList<VersementsaccModel> versements;

    public ListeVersementaccFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ListeVersementaccFragment.
     */
    public static ListeVersementaccFragment newInstance() {
        return new ListeVersementaccFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        VersementaccViewModel versementaccViewModel = new ViewModelProvider(this).get(VersementaccViewModel.class);
        versements = new ArrayList<>();
        versements = versementaccViewModel.getMlisteversementacc().getValue();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentListeVersementaccBinding.inflate(getLayoutInflater());
        creerliste();
        return binding.getRoot();
    }

    public void creerliste(){
        VersementaccAdapter adapter = new VersementaccAdapter(versements, getContext());
        binding.lstviewversementacc.setAdapter(adapter);

    }


}