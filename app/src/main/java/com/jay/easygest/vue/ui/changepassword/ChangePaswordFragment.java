package com.jay.easygest.vue.ui.changepassword;


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
import com.jay.easygest.databinding.FragmentChangePaswordBinding;
import com.jay.easygest.model.UserModel;
import com.jay.easygest.vue.GestionActivity;
import com.jay.easygest.vue.MainActivity;

public class ChangePaswordFragment extends Fragment {


    private Usercontrolleur usercontrolleur;
    private UserModel user;
    private FragmentChangePaswordBinding binding;
    private int compteur;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentChangePaswordBinding.inflate(inflater,container,false);
        usercontrolleur = Usercontrolleur.getUsercontrolleurInstance(getContext());
        View root = binding.getRoot();
        changerPassword();
        return root;
    }

    public void changerPassword(){

        binding.btnchangepassword.setOnClickListener(v -> {

            if (binding.editchangerpasswordusername.length() != 0 && binding.editchangerpasswordpassword.length() != 0 && binding.editchangerpasswordnouveaupassword.length() != 0 ){

                if (binding.editchangerpasswordusername.length() >= 6 && binding.editchangerpasswordpassword.length() >= 8 && binding.editchangerpasswordnouveaupassword.length() >= 8){

                    String username = binding.editchangerpasswordusername.getText().toString();
                    String nouveaupassword = binding.editchangerpasswordnouveaupassword.getText().toString();
                    String password = binding.editchangerpasswordpassword.getText().toString();
                    user = usercontrolleur.getUser();
                    if (username.equals(user.getUsername()) && password.equals(user.getPassword())){
                        UserModel userModel = new UserModel(user.getId(),user.getUsername(),nouveaupassword,user.getDateInscription(),user.getStatus(),user.isActif(),0);
                        usercontrolleur.modifierUser(userModel);
                        usercontrolleur.setUser(userModel);
                        Intent intent = new Intent(getActivity(), GestionActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }else{
                        Toast.makeText(getContext(), "username ou mot de passe ne correspond pas", Toast.LENGTH_SHORT).show();
                        desactiverbtnchangepassword(user);
                    }

                }else{
                    Toast.makeText(getContext(), "username ou mot de passe trop court", Toast.LENGTH_SHORT).show();
                    desactiverbtnchangepassword(user);
                }


            }else { Toast.makeText(getContext(), "champs obligatoires", Toast.LENGTH_SHORT).show(); }
        });
    }

    private void desactiverbtnchangepassword(UserModel user){
        int cmpteur = incrementCompteur(user);

        if (cmpteur >= 3){
            binding.btnchangepassword.setEnabled(false);
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
//
        }
        return compteur;

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}