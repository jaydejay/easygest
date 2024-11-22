package com.jay.easygest.vue.ui.changeusername;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.jay.easygest.controleur.Usercontrolleur;
import com.jay.easygest.databinding.FragmentChangeUsernameBinding;
import com.jay.easygest.model.UserModel;
import com.jay.easygest.outils.PasswordHascher;
import com.jay.easygest.outils.SessionManagement;
import com.jay.easygest.vue.GestionActivity;
import com.jay.easygest.vue.MainActivity;

public class ChangeUsernameFragment extends Fragment {

    private SessionManagement sessionManagement;
    private FragmentChangeUsernameBinding binding;
    private Integer compteur;
    private UserModel user;
    private Usercontrolleur usercontrolleur;
    private PasswordHascher passwordHascher;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        sessionManagement = new SessionManagement(requireContext());
        binding = FragmentChangeUsernameBinding.inflate(inflater, container, false);
        usercontrolleur = Usercontrolleur.getUsercontrolleurInstance(getContext());
        passwordHascher = new PasswordHascher();
        View root = binding.getRoot();
        changerUsername();
        return root;
    }

    public void changerUsername(){
        binding.btnchangeusername.setOnClickListener(v -> {
            binding.btnchangeusername.setEnabled(false);
            String username = binding.editchangerusernameusername.getText().toString().trim();
            String nouveauusername = binding.editchangerusernamenouveauusername.getText().toString().trim();
            String password = binding.editchangerusernamepassword.getText().toString().trim();

            if (username.isEmpty() || nouveauusername.isEmpty() || password.isEmpty() ){
                Toast.makeText(getContext(), "champs obligatoires", Toast.LENGTH_SHORT).show();
                binding.btnchangeusername.setEnabled(true);
            }else {
                if (username.length() >= 6 && nouveauusername.length() >= 6 && password.length() >= 8){
                    user = usercontrolleur.recupProprietaire();
                    if (username.equals(user.getUsername()) && passwordHascher.verifyHashingPass(password,user.getPassword())){
                        UserModel userModel = new UserModel(user.getId(),nouveauusername,user.getPassword(),user.getDateInscription(),user.getStatus(),user.isActif(),0);
                        usercontrolleur.modifierUser(userModel);
                        usercontrolleur.setUser(userModel);
                        Intent intent = new Intent(getActivity(), GestionActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }else{
                        binding.btnchangeusername.setEnabled(true);
                        Toast.makeText(getContext(), "username ou mot de passe ne correspond pas", Toast.LENGTH_SHORT).show();
                        user = usercontrolleur.getUser();
                        desactiverbtnchangerusername(user);

                    }

                }else{
                    binding.btnchangeusername.setEnabled(true);
                    Toast.makeText(getContext(), "username ou mot de passe trop court", Toast.LENGTH_SHORT).show();
                    user = usercontrolleur.getUser();
                    desactiverbtnchangerusername(user);

                }
            }
        });
    }

    private void desactiverbtnchangerusername(UserModel user){
        int cmpteur = incrementCompteur(user);

        if (cmpteur >= 3){
            binding.btnchangeusername.setEnabled(false);
            usercontrolleur.desactiverProprietaire();
            Intent intent = new Intent(getActivity(), MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);

        }


    }


    private Integer incrementCompteur( UserModel userModel){
        try {
            Integer compteur1 = userModel.getCompteur();
            compteur = compteur1 + 1;
            user = new UserModel(userModel.getId(),userModel.getUsername(),userModel.getPassword(),userModel.getDateInscription(),userModel.getStatus(),userModel.isActif(),compteur);
            usercontrolleur.modifierUser(user);
            usercontrolleur.setUser(user);
        }catch (Exception e){
//          do nothing
        }
        return compteur;

    }
    public void onStart() {
        super.onStart();
        if (!sessionManagement.getSession()){
            Intent intent = new Intent(getActivity(), MainActivity.class);
            startActivity(intent);

        }

    }


    @Override
    public void onResume() {
        super.onResume();
        if (!sessionManagement.getSession()){
            Intent intent = new Intent(getActivity(), MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }


}