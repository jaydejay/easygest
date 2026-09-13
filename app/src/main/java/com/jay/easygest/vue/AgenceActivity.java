package com.jay.easygest.vue;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.jay.easygest.controleur.Usercontrolleur;
import com.jay.easygest.databinding.ActivityAgenceBinding;
import com.jay.easygest.model.AppKessModel;
import com.jay.easygest.model.UserModel;
import com.jay.easygest.outils.AccessLocalAppKes;
import com.jay.easygest.outils.MesOutils;
import com.jay.easygest.outils.SessionManagement;

public class AgenceActivity extends AppCompatActivity {
    private ActivityAgenceBinding binding;
    private Usercontrolleur usercontrolleur;
    private String[] appcredentials;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
        binding = ActivityAgenceBinding.inflate(getLayoutInflater());
        this.usercontrolleur = Usercontrolleur.getUsercontrolleurInstance(this);
//        appcredentials = usercontrolleur.getAppCredentials();
        setContentView(binding.getRoot());
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            setupAgence();
//            return insets;
//        });
    }

    public void setupAgence(){
        binding.btnAgence.setOnClickListener(v -> {
            binding.btnAgence.setEnabled(false);
            String owner = binding.edtAgenceOwner.getText().toString().trim();
            String telephone = binding.edtAgencePhone.getText().toString().trim();
            String email = binding.edtAgenceEmail.getText().toString().trim();
            String basecode = binding.edtAgenceBasecode.getText().toString().trim();
            if (basecode.isEmpty() || owner.isEmpty() || email.isEmpty() || telephone.isEmpty()){
                Toast.makeText(this, "remplir tous les champs", Toast.LENGTH_SHORT).show();
                binding.btnAgence.setEnabled(true);
            }else {
                if (basecode.length() != 4  ) {
                    Toast.makeText(this, "base code 4 lettres atendues", Toast.LENGTH_SHORT).show();
                    binding.btnAgence.setEnabled(true);
                }else {
                    if (owner.length() < 5 || owner.length() > 25) {
                        Toast.makeText(this, "5 lettres minimum et 25 lettres maximum", Toast.LENGTH_SHORT).show();
                        binding.btnAgence.setEnabled(true);
                    }else {
                        if (telephone.length() != 10) {
                            Toast.makeText(this, "10 chiffres attendu", Toast.LENGTH_SHORT).show();
                            binding.btnAgence.setEnabled(true);
                        }else {

                            if (MesOutils.isValidEmail(email)){
                                boolean success = iscreateAgenceSuccessfull(owner,basecode,telephone,email);
                                if (success){
                                    SessionManagement sessionManagement = new SessionManagement(this);
                                    sessionManagement.saveAgenceCreated(true);
                                    UserModel user = usercontrolleur.recupProprietaire();
                                    Intent intent;
                                    if (user!= null){
                                        intent = new Intent(AgenceActivity.this, MainActivity.class);
                                    }else {
                                        intent = new Intent(AgenceActivity.this, CreercompteActivity.class);
                                    }
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                    finish();

                                }else {
                                    Toast.makeText(AgenceActivity.this, "un probleme est survenue", Toast.LENGTH_SHORT).show();
                                    binding.btnAgence.setEnabled(true);
                                }

                            }else {
                                Toast.makeText(AgenceActivity.this, "invalide email", Toast.LENGTH_SHORT).show();
                                binding.btnAgence.setEnabled(true);
                            }

                        }
                    }

                }
            }

        });
    }

    private boolean iscreateAgenceSuccessfull(String owner, String basecode, String telephone, String email) {
        AccessLocalAppKes accessLocalAppKes = new AccessLocalAppKes(this);
        AppKessModel appKess_Model = accessLocalAppKes.getAppkes();
                                AppKessModel appKessModel = new AppKessModel(
                                        appKess_Model.getAppnumber(),
                                        appKess_Model.getApppkey(),
                                        owner,
                                        basecode,
                                        telephone,
                                        email);
//        AppKessModel appKessModel = accessLocalAppKes.getAppkes();
        boolean success =  accessLocalAppKes.createAgence(appKessModel);
        return success;
    }
}