package com.jay.easygest.vue.ui.credit;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.jay.easygest.controleur.Clientcontrolleur;
import com.jay.easygest.controleur.Creditcontrolleur;
import com.jay.easygest.databinding.FragmentCreditBinding;
import com.jay.easygest.model.Articles;
import com.jay.easygest.model.ClientModel;
import com.jay.easygest.model.CreditModel;
import com.jay.easygest.outils.MesOutils;
import com.jay.easygest.vue.GestionActivity;
import com.jay.easygest.vue.ModifiercreditActivity;
import com.jay.easygest.vue.ui.clients.ClientViewModel;
import com.owlike.genson.Genson;

import java.util.ArrayList;
import java.util.Date;


public class CreditFragment extends Fragment {


    private FragmentCreditBinding binding;
    private Creditcontrolleur creditcontrolleur;
    private CreditViewModel creditViewModel;
    private Clientcontrolleur clientcontrolleur;
    private ClientViewModel clientViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        this.creditcontrolleur = Creditcontrolleur.getCreditcontrolleurInstance(getContext());
        clientcontrolleur = Clientcontrolleur.getClientcontrolleurInstance(getContext());
        clientViewModel = new ViewModelProvider(this).get(ClientViewModel.class);
        creditViewModel = new ViewModelProvider(this).get(CreditViewModel.class);
        binding = FragmentCreditBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        this.initFragment();
        this.ajouterCredit();

        return root;
    }

    private void initFragment(){
        binding.txtcreercodeclt.setText(MesOutils.generateurcodeclt());

    }

    public void ajouterCredit(){


        binding.btncreercredit.setOnClickListener(v -> {

            String nomclient = binding.edittxtcreernom.getText().toString().trim();
            String prenomsclient = binding.edittxtcreerprenoms.getText().toString().trim();

            String designationarticle1 = binding.edittxtcreerarticle1.getText().toString().trim();
            String article1somme = binding.edittxtcreerarticle1somme.getText().toString().trim();
            String article1qte = binding.edittxtcreerNbrarticle1.getText().toString().trim();

//            String designationarticle2 = binding.edittxtcreerarticle2.getText().toString().trim();
//            String article2somme = binding.edittxtcreerarticle2somme.getText().toString().trim();
//            String article2qte = binding.edittxtcreerNbrarticle2.getText().toString().trim();

            String telephone =  binding.edittxtcreertelephone.getText().toString().trim();
            String versement = binding.edittxtcreerversement.getText().toString().trim();
            String date = binding.editTextDate.getText().toString().trim();
            Date date_ouverture = MesOutils.convertStringToDate(date);

            if (nomclient.isEmpty()  || prenomsclient.isEmpty() ||designationarticle1.isEmpty() || article1somme.isEmpty() ||
                    article1qte.isEmpty() || telephone.isEmpty() || versement.isEmpty()||date.isEmpty())
            {
                Toast.makeText(getContext(), "remplissez les champs obligatoires", Toast.LENGTH_SHORT).show();

            } else if (date_ouverture == null) {
                Toast.makeText(getActivity(), "format de date incorect", Toast.LENGTH_SHORT).show();

            } else if  (binding.edittxtcreerarticle2.getText().toString().trim().length() != 0 && binding.edittxtcreerarticle2somme.getText().toString().trim().isEmpty() ||
                    binding.edittxtcreerarticle2.getText().toString().trim().length() != 0 & binding.edittxtcreerNbrarticle2.getText().toString().trim().isEmpty()) {
                Toast.makeText(getActivity(), "renseigner le nombre et le prix du deuxieme article", Toast.LENGTH_SHORT).show();
            } else if (telephone.length() < 10) {
                Toast.makeText(getContext(), "numero doit étre de 10 chiffres", Toast.LENGTH_SHORT).show();

            } else {
//                    try {

                        int sommearticle1 =Integer.parseInt(article1somme);
                        int nbrarticle1 = Integer.parseInt(article1qte);

                String designation_article2 ;
                String designationarticle2 ;
                int sommearticle2 ;
                int nbrarticle2 ;
                String somme_article2 ;
                String nbr_article2 ;

                if (binding.edittxtcreerarticle2.getText().toString().trim().length() != 0 && binding.edittxtcreerarticle2somme.getText().toString().trim().equals("0") ||
                        binding.edittxtcreerarticle2.getText().toString().trim().length() != 0 & binding.edittxtcreerNbrarticle2.getText().toString().trim().equals("0")){
                    designation_article2 = "";
                    somme_article2 = "0";
                    nbr_article2 = "0";
                }else if (binding.edittxtcreerarticle2.getText().toString().trim().isEmpty()){
                    designation_article2 = binding.edittxtcreerarticle2.getText().toString().trim();
                    somme_article2 = "0";
                    nbr_article2 = "0";
                }else {
                    designation_article2 = binding.edittxtcreerarticle2.getText().toString().trim();
                    somme_article2 = binding.edittxtcreerarticle2somme.getText().toString().trim();
                    nbr_article2 = binding.edittxtcreerNbrarticle2.getText().toString().trim();
                }
                designationarticle2 = designation_article2;
                sommearticle2 = Integer.parseInt(somme_article2);
                nbrarticle2 = Integer.parseInt(nbr_article2);

                long dateouverture = date_ouverture.getTime();
                String codeclient = binding.txtcreercodeclt.getText().toString();


                Articles c_article1 = new Articles(designationarticle1, sommearticle1,nbrarticle1);
                Articles c_article2 = new Articles(designationarticle2, sommearticle2,nbrarticle2);

                int sommecredit  = c_article1.getSomme() + c_article2.getSomme();
               if (Integer.parseInt(versement) < sommecredit){

                   boolean success;
                       success =  this.creditcontrolleur.creerCredit(codeclient, nomclient,prenomsclient,telephone, c_article1, c_article2, versement, dateouverture);

                   if (success) {
                       Intent intent = new Intent(getActivity(), GestionActivity.class);
                       intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                       startActivity(intent);
                   }else {
                       Toast.makeText(getContext(), "un probleme est survenu : crédit non enregistrer", Toast.LENGTH_SHORT).show();

                   }

               }else {Toast.makeText(getContext(), "versement superieur ou egal au credit", Toast.LENGTH_SHORT).show();}

            }
        });
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        creditcontrolleur.setCredit(null);
        binding = null;
    }
}