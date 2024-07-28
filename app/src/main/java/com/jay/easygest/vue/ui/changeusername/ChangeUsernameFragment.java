package com.jay.easygest.vue.ui.changeusername;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.jay.easygest.controleur.Usercontrolleur;
import com.jay.easygest.databinding.FragmentChangeUsernameBinding;
import com.jay.easygest.model.UserModel;
import com.jay.easygest.vue.GestionActivity;
import com.jay.easygest.vue.MainActivity;

public class ChangeUsernameFragment extends Fragment {

    private FragmentChangeUsernameBinding binding;
    private Integer compteur;
    private UserModel user;
    private Usercontrolleur usercontrolleur;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentChangeUsernameBinding.inflate(inflater, container, false);
        usercontrolleur = Usercontrolleur.getUsercontrolleurInstance(getContext());
        View root = binding.getRoot();
        changerUsername();
        return root;
    }

    public void changerUsername(){
        binding.btnchangeusername.setOnClickListener(v -> {

            if (binding.editchangerusernameusername.length() != 0 && binding.editchangerusernamenouveauusername.length() != 0 && binding.editchangerusernamepassword.length() != 0 ){

                if (binding.editchangerusernameusername.length() >= 6 && binding.editchangerusernamenouveauusername.length() >= 6 && binding.editchangerusernamepassword.length() >= 8){

                    String username = binding.editchangerusernameusername.getText().toString();
                    String nouveauusername = binding.editchangerusernamenouveauusername.getText().toString();
                    String password = binding.editchangerusernamepassword.getText().toString();
                    user = usercontrolleur.getUser();
                    if (username.equals(user.getUsername()) && password.equals(user.getPassword())){
                        UserModel userModel = new UserModel(user.getId(),nouveauusername,user.getPassword(),user.getDateInscription(),user.getStatus(),user.isActif(),0);
                        usercontrolleur.modifierUser(userModel);
                        usercontrolleur.setUser(userModel);
                        Intent intent = new Intent(getActivity(), GestionActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }else{
                        Toast.makeText(getContext(), "username ou mot de passe ne correspond pas", Toast.LENGTH_SHORT).show();
                        desactiverbtnchangerusername(user);
                    }

                }else{
                    Toast.makeText(getContext(), "username ou mot de passe trop court", Toast.LENGTH_SHORT).show();
                    desactiverbtnchangerusername(user);
                }

            }else { Toast.makeText(getContext(), "champs obligatoires", Toast.LENGTH_SHORT).show(); }
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


}