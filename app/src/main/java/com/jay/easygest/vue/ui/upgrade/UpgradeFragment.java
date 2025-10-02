package com.jay.easygest.vue.ui.upgrade;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

//import androidx.annotation.NonNull;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.jay.easygest.R;
import com.jay.easygest.controleur.Usercontrolleur;
import com.jay.easygest.databinding.FragmentUpgradeBinding;
import com.jay.easygest.model.AppKessModel;
import com.jay.easygest.outils.AccessLocalAppKes;
import com.jay.easygest.outils.MesOutils;

public class UpgradeFragment extends Fragment {

    private FragmentUpgradeBinding binding;
    private String[] appcredentials;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentUpgradeBinding.inflate(inflater, container, false);
        Usercontrolleur usercontrolleur = Usercontrolleur.getUsercontrolleurInstance(requireContext());
        appcredentials = usercontrolleur.getAppCredentials();
        upgradeAppli();
        return  binding.getRoot();
    }

    private void upgradeAppli(){
        binding.btnValidateKey.setOnClickListener(v -> {
            binding.btnValidateKey.setEnabled(false);
            String cleproduit = binding.editKey.getText().toString().trim();
            String appnumber = appcredentials[0];
            if (cleproduit.isEmpty()){
                binding.btnValidateKey.setEnabled(true);
                Toast.makeText(getContext(), "champ obligatoire", Toast.LENGTH_SHORT).show();
            }else {
                boolean is_appnuber_right = MesOutils.retrieveAppNumber(cleproduit,appnumber);
                if (is_appnuber_right){
                    getUpgrade(cleproduit, appnumber);

                }else {
                    if ( MesOutils.getLicenceLevel(appcredentials[1]) != MesOutils.Level.FREE ){
                        binding.btnValidateKey.setEnabled(true);
                        Toast.makeText(getContext(), "verifiez les informations fournies", Toast.LENGTH_SHORT).show();
                    }else {
                        View view = LayoutInflater.from(getContext()).inflate(R.layout.layout_upgrade_appkey,null);
                        EditText editext_app_number = view.findViewById(R.id.upgrade_appli_key);
                        String app_number = editext_app_number.getText().toString().trim();

                        AlertDialog.Builder builder = getBuilder(view);

                        builder.setPositiveButton("upgrade",(dialog, which) -> {
                            if ( MesOutils.retrieveAppNumber(cleproduit,app_number)){
                                getUpgrade(cleproduit,app_number);
                            }else {
                                binding.btnValidateKey.setEnabled(true);
                                Toast.makeText(getContext(), " produit non conforme", Toast.LENGTH_SHORT).show();
                            }

                        });
                        builder.setNegativeButton("annuller",(dialog, which) ->{
                            binding.btnValidateKey.setEnabled(true);
                        });
                        builder.create().show();
                    }


                }

            }

        });
    }
    @NonNull
    private AlertDialog. Builder getBuilder(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext()) ;
        builder.setTitle("entree l'application id ");
        builder.setView(view);
        return builder;
    }

    private void getUpgrade(String cleproduit, String _appnumber) {
        if ( MesOutils.isKeyvalide(cleproduit, _appnumber)){

            AppKessModel appKessModel = new AppKessModel(
                    Integer.parseInt(_appnumber),
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
                Toast.makeText(getContext(), "félicitation licence "+MesOutils.getLicenceLevel(cleproduit)+ " activée", Toast.LENGTH_SHORT).show();

            }else {
                Toast.makeText(getContext(), "un probleme est survenue", Toast.LENGTH_SHORT).show();
                binding.btnValidateKey.setEnabled(true);
            }

        }else {
            Toast.makeText(getContext(), "produit non conforme", Toast.LENGTH_SHORT).show();
            binding.btnValidateKey.setEnabled(true);
        }
    }
}