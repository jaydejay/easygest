package com.jay.easygest.vue.ui.changepassword;


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
import com.jay.easygest.databinding.FragmentChangePaswordBinding;
import com.jay.easygest.model.UserModel;
import com.jay.easygest.outils.PasswordHascher;
import com.jay.easygest.outils.SessionManagement;
import com.jay.easygest.outils.VariablesStatique;
import com.jay.easygest.vue.GestionActivity;
import com.jay.easygest.vue.MainActivity;

public class ChangePaswordFragment extends Fragment {


    private SessionManagement sessionManagement;
    private Usercontrolleur usercontrolleur;
    private UserModel user;
    private FragmentChangePaswordBinding binding;
    private int compteur;
    private PasswordHascher passwordHascher;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        sessionManagement = new SessionManagement(requireContext());
        binding = FragmentChangePaswordBinding.inflate(inflater,container,false);
        usercontrolleur = Usercontrolleur.getUsercontrolleurInstance(getContext());
        passwordHascher = new PasswordHascher();
        View root = binding.getRoot();
        changerPassword();
        return root;
    }

    public void changerPassword(){

        binding.btnchangepassword.setOnClickListener(v -> {
            binding.btnchangepassword.setEnabled(false);
            try {
                String username = binding.editchangerpasswordusername.getText().toString().trim();
                String nouveaupassword = binding.editchangerpasswordnouveaupassword.getText().toString().trim();
                String repeat_nouveaupassword = binding.editchangerpasswordRenouveaupassword.getText().toString().trim();
                String password = binding.editchangerpasswordpassword.getText().toString().trim();
                if (username.isEmpty() || password.isEmpty() || nouveaupassword.isEmpty()|| repeat_nouveaupassword.isEmpty() ){
                    Toast.makeText(getContext(), "champs obligatoires", Toast.LENGTH_SHORT).show();
                    binding.btnchangepassword.setEnabled(true);

                }else {
                    if (username.length() >= 6 && password.length() >= 8 && nouveaupassword.length() >= 8){
                        if (repeat_nouveaupassword.equals(nouveaupassword)){
                            user = usercontrolleur.recupProprietaire();
                            if(passwordHascher.verifyHashingPass(password,user.getPassword())){

                                if (username.equals(user.getUsername())){
                                    String _nouveaupassword = passwordHascher.getHashingPass(nouveaupassword, VariablesStatique.MY_SALT);
                                    UserModel userModel = new UserModel(user.getId(),user.getUsername(),_nouveaupassword,user.getDateInscription(),user.getStatus(),user.isActif(),0);
                                    usercontrolleur.modifierUser(userModel);
                                    usercontrolleur.setUser(userModel);
                                    Intent intent = new Intent(getActivity(), GestionActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                }else{
                                    binding.btnchangepassword.setEnabled(true);
                                    Toast.makeText(getContext(), "username ne correspond pas", Toast.LENGTH_SHORT).show();
                                    desactiverbtnchangepassword(user);
                                }

                            }else{
                                binding.btnchangepassword.setEnabled(true);
                                Toast.makeText(getContext(), " mot de passe ne correspond pas", Toast.LENGTH_SHORT).show();
                                desactiverbtnchangepassword(user);
                            }
                        }else{
                            binding.btnchangepassword.setEnabled(true);
                            Toast.makeText(getContext(), "nouveaux mot de passe incoherents", Toast.LENGTH_SHORT).show();
                        }


                    }else{
                        binding.btnchangepassword.setEnabled(true);
                        Toast.makeText(getContext(), "username ou mot de passe trop court", Toast.LENGTH_SHORT).show();
                        desactiverbtnchangepassword(user);
                    }

                }
            }catch (Exception e){
                binding.btnchangepassword.setEnabled(true);
                Toast.makeText(getContext(), "un probleme est survenu impossible de traiter la demande", Toast.LENGTH_SHORT).show();
            }


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
            //do nothing
        }
        return compteur;

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