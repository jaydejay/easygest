package com.jay.easygest.vue.ui.articles;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jay.easygest.databinding.FragmentImageBinding;
import com.jay.easygest.model.Image;

import java.lang.reflect.Type;

/**
 * A simple {@link Fragment} subclass.
 * Use the  factory method to
 * create an instance of this fragment.
 *
 */
public class ImageFragment extends Fragment {


    private static final String ARG_PARAM = "param";
    // TODO: Rename and change types of parameters
    private Image image;
    private FragmentImageBinding binding;

    public ImageFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
           String string_image = getArguments().getString(ARG_PARAM);
            Type type = new TypeToken<Image>() {}.getType();
             image = new Gson().fromJson(string_image,type);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentImageBinding.inflate(inflater,container,false);
        initfrag();
        return binding.getRoot();
    }

    public void initfrag(){
        binding.articleImageView.setImageBitmap(image.getImage());
    }
}