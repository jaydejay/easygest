package com.jay.easygest.vue.ui.upgrade;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.jay.easygest.controleur.Usercontrolleur;
import com.jay.easygest.databinding.FragmentUpgradeBinding;
import com.jay.easygest.model.AppKessModel;
import com.jay.easygest.outils.AccessLocalAppKes;
import com.jay.easygest.outils.MesOutils;

import java.util.Arrays;

public class UpgradeFragment extends Fragment {

    private FragmentUpgradeBinding binding;
    private String[] appcredentials;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentUpgradeBinding.inflate(inflater, container, false);
        Usercontrolleur usercontrolleur = Usercontrolleur.getUsercontrolleurInstance(requireContext());
        appcredentials = usercontrolleur.getAppCredentials();
        Log.i("accesappkess", "updateAppkesKey: "+ Arrays.toString(appcredentials));
        upgradeAppli();
        return  binding.getRoot();
    }

    private void upgradeAppli(){
        binding.btnValidateKey.setOnClickListener(v -> {
            binding.btnValidateKey.setEnabled(false);
            String cleproduit = binding.editKey.getText().toString().trim();
            String appnumber = appcredentials[0];
            if (cleproduit.isEmpty()){
                Toast.makeText(getContext(), "champ obligatoire", Toast.LENGTH_SHORT).show();
            }else {
                boolean is_appnuber_right = MesOutils.retrieveAppNumber(cleproduit,appnumber);
                if (is_appnuber_right && MesOutils.isKeyvalide(cleproduit,appnumber)){

                    AppKessModel appKessModel = new AppKessModel(
                            Integer.parseInt(appcredentials[0]),
                            cleproduit,
                            appcredentials[2],
                            appcredentials[3],
                            appcredentials[4],
                            appcredentials[7]);

                    AccessLocalAppKes accessLocalAppKes = new AccessLocalAppKes(getContext());
                    boolean success =  accessLocalAppKes.updateAppkesKey(appKessModel, appcredentials);
                    if (success){
                        binding.editKey.setText("");
                        binding.btnValidateKey.setEnabled(true);
                        Toast.makeText(getContext(), "félicitation licence "+MesOutils.getLicenceLevel(cleproduit)+ "activée", Toast.LENGTH_SHORT).show();

                    }else {
                        Toast.makeText(getContext(), "un probleme est survenue", Toast.LENGTH_SHORT).show();
                        binding.btnValidateKey.setEnabled(true);
                    }

                }else {
                    Toast.makeText(getContext(), "produit non conforme", Toast.LENGTH_SHORT).show();
                    binding.btnValidateKey.setEnabled(true);
                }
            }

        });
    }
}