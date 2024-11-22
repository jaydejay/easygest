package com.jay.easygest.vue;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.jay.easygest.controleur.Clientcontrolleur;
import com.jay.easygest.controleur.Creditcontrolleur;
import com.jay.easygest.databinding.ActivityModifierClientBinding;
import com.jay.easygest.model.ClientModel;
import com.jay.easygest.outils.SessionManagement;
import com.jay.easygest.outils.SmsSender;
import com.jay.easygest.vue.ui.clients.ClientViewModel;

public class ModifierClientActivity extends AppCompatActivity {

    private SessionManagement sessionManagement;
    private ActivityModifierClientBinding binding;
    private Clientcontrolleur clientcontrolleur;
    private Creditcontrolleur creditcontrolleur;
    private ClientViewModel clientViewModel;

    private ClientModel client;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sessionManagement = new SessionManagement(this);
        binding = ActivityModifierClientBinding.inflate(getLayoutInflater());
        clientcontrolleur= Clientcontrolleur.getClientcontrolleurInstance(this);
        creditcontrolleur = Creditcontrolleur.getCreditcontrolleurInstance(this);
        clientcontrolleur.listeClients();
        clientViewModel = new ViewModelProvider(this).get(ClientViewModel.class);
        client = clientViewModel.getClient().getValue();
        setContentView(binding.getRoot());
        init();
        modifierclient();
    }

    public void init(){
        clientViewModel.getClient().observe(this,client -> {
                    binding.editmodifierclientnom.setText(client.getNom());
                    binding.editmodifierclientprenoms.setText(client.getPrenoms());
                    binding.editmodifierclienttelephone.setText(client.getTelephone());
                    binding.editmodifierclientemail.setText(client.getEmail());
                    binding.editmodifierclientresidence.setText(client.getResidence());
                    binding.editmodifierclientcni.setText(client.getCni());
                    binding.editmodifierclientpermis.setText(client.getPermis());
                    binding.editmodifierclientpassport.setText(client.getPassport());
                    binding.editmodifierclientsociete.setText(client.getSociete());
                    String titre = "modifier client "+client.getCodeclient();
                    ActionBar ab = getSupportActionBar();
                    if (ab != null){
                        ab.setTitle(titre);
                    }
        });


    }

    public void modifierclient(){

        binding.btnmodifierclient.setOnClickListener(v -> {
            binding.btnmodifierclient.setEnabled(false);

            int id = client.getId();
               String code = client.getCodeclient().trim();
               String nom = binding.editmodifierclientnom.getText().toString().trim();
               String prenoms = binding.editmodifierclientprenoms.getText().toString().trim();
               String telephone = binding.editmodifierclienttelephone.getText().toString().trim();
               String email = binding.editmodifierclientemail.getText().toString().trim();
               String residence = binding.editmodifierclientresidence.getText().toString().trim();
               String cni = binding.editmodifierclientcni.getText().toString().trim();
               String permis = binding.editmodifierclientpermis.getText().toString().trim();
               String passport = binding.editmodifierclientpassport.getText().toString().trim();
               String societe = binding.editmodifierclientsociete.getText().toString().trim();

               Integer nbrcredit = client.getNbrcredit();
               Long totalcredit = client.getTotalcredit();
               Integer nbraccount = client.getNbraccount();
               Long totalaccount = client.getTotalaccount();

               if (nom.isEmpty() || prenoms.isEmpty() || telephone.isEmpty()){
                   Toast.makeText(ModifierClientActivity.this, "nom,prenoms et telephone obligatoires", Toast.LENGTH_SHORT).show();
                   binding.btnmodifierclient.setEnabled(true);
               }else if (telephone.length() < 10) {
                   Toast.makeText(this, "numero doit étre de 10 chiffres", Toast.LENGTH_SHORT).show();
                   binding.btnmodifierclient.setEnabled(true);
               }
                ClientModel clientModel = new ClientModel(id,code,nom,prenoms,telephone,email,residence,cni,permis,passport,societe,nbrcredit,totalcredit,nbraccount,totalaccount);
               boolean success =  clientcontrolleur.modifierclient(clientModel);
               if(success ) {
                    creditcontrolleur.listecredits();
                   Intent intent = new Intent(ModifierClientActivity.this, AfficherclientActivity.class);
                   startActivity(intent);
//                   finish();
               }
               else {
                   Toast.makeText(ModifierClientActivity.this,"erreur echec de la modification" , Toast.LENGTH_SHORT).show();
                   binding.btnmodifierclient.setEnabled(true);
               }

        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (!sessionManagement.getSession()){
            Intent intent = new Intent(this, MainActivity.class);
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        sessionManagement.removeSession();
    }
}