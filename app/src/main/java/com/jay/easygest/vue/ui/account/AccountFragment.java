package com.jay.easygest.vue.ui.account;

import static com.jay.easygest.outils.VariablesStatique.MY_PERMISSIONS_REQUEST_SEND_SMS;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.jay.easygest.controleur.Accountcontroller;
import com.jay.easygest.controleur.Clientcontrolleur;
import com.jay.easygest.databinding.FragmentAccountBinding;
import com.jay.easygest.model.AccountModel;
import com.jay.easygest.model.AppKessModel;
import com.jay.easygest.model.Articles;
import com.jay.easygest.model.ClientModel;
import com.jay.easygest.model.SmsnoSentModel;
import com.jay.easygest.outils.AccessLocalAppKes;
import com.jay.easygest.outils.MesOutils;
import com.jay.easygest.outils.SessionManagement;
import com.jay.easygest.outils.SmsSender;
import com.jay.easygest.vue.AfficherclientActivity;
import com.jay.easygest.vue.MainActivity;
import com.jay.easygest.vue.ui.clients.ClientViewModel;

import java.util.Date;
import java.util.Objects;

public class  AccountFragment extends Fragment {

    private SessionManagement sessionManagement;

    private AccountViewModel accountViewModel;
    private ClientViewModel clientViewModel;
    private Accountcontroller accountcontroller;
    private Clientcontrolleur clientcontrolleur;
    private FragmentAccountBinding binding;
    private AccessLocalAppKes accessLocalAppKes;
    private  AppKessModel appKessModel;
    private SmsSender smsSender;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        sessionManagement = new SessionManagement(requireContext());
        binding = FragmentAccountBinding.inflate(inflater,container,false);
        accountcontroller = Accountcontroller.getAccountcontrolleurInstance(getContext());
        clientcontrolleur = Clientcontrolleur.getClientcontrolleurInstance(getContext());
        accessLocalAppKes = new AccessLocalAppKes(getContext());
        accountViewModel = new ViewModelProvider(this).get(AccountViewModel.class);
        clientViewModel = new ViewModelProvider(this).get(ClientViewModel.class);

        appKessModel = accessLocalAppKes.getAppkes();
        smsSender = new SmsSender(getContext(),getActivity());
        initFragment();
        creerAccount();
        return binding.getRoot();
    }

    private void initFragment(){
        AppKessModel appKessModel = accessLocalAppKes.getAppkes();
        if (appKessModel.getBasecode() != null ){
            binding.txtcreeracccodeclt.setText(MesOutils.generateurcodeclt(appKessModel.getBasecode()));
        }

    }


    public void creerAccount(){

        binding.btncreeraccount.setOnClickListener(v -> {

            String nomclient = binding.edittxtcreeaccrnom.getText().toString().trim();
            String prenomsclient = binding.edittxtcreeraccprenoms.getText().toString().trim();

            String designationarticle1 = binding.edittxtcreeraccarticle1.getText().toString().trim();
            String article1somme = binding.edittxtcreeraccarticle1somme.getText().toString().trim();
            String article1qte = binding.edittxtcreeraccNbrarticle1.getText().toString().trim();

            String telephone =  binding.edittxtcreeracctelephone.getText().toString().trim();
            String versement = binding.edittxtcreeraccversement.getText().toString().trim();
            String date = binding.editTextaccDate.getText().toString().trim();
            Date date_ouverture = MesOutils.convertStringToDate(date);

            if (nomclient.isEmpty()  || prenomsclient.isEmpty() ||designationarticle1.isEmpty() || article1somme.isEmpty() ||
                    article1qte.isEmpty() || telephone.isEmpty() || versement.isEmpty()||date.isEmpty())
            {
                Toast.makeText(getContext(), "remplissez les champs obligatoires", Toast.LENGTH_SHORT).show();

            } else if (date_ouverture == null) {
                Toast.makeText(getActivity(), "format de date incorrect", Toast.LENGTH_SHORT).show();

            } else if  (binding.edittxtcreeraccarticle2.getText().toString().trim().length() != 0 && binding.edittxtcreeraccarticle2somme.getText().toString().trim().isEmpty() ||
                    binding.edittxtcreeraccarticle2.getText().toString().trim().length() != 0 & binding.edittxtcreeraccNbrarticle2.getText().toString().trim().isEmpty()) {
                Toast.makeText(getActivity(), "renseigner le nombre ou le prix du deuxieme article", Toast.LENGTH_SHORT).show();
            } else if (telephone.length() < 10) {
                Toast.makeText(getContext(), "numero doit étre de 10 chiffres", Toast.LENGTH_SHORT).show();

            } else {

                int sommearticle1 =Integer.parseInt(article1somme);
                int nbrarticle1 = Integer.parseInt(article1qte);

                String designation_article2 ;
                String designationarticle2 ;
                int sommearticle2 ;
                int nbrarticle2 ;
                String somme_article2 ;
                String nbr_article2 ;

                if (binding.edittxtcreeraccarticle2.getText().toString().trim().length() != 0 && binding.edittxtcreeraccarticle2somme.getText().toString().trim().equals("0") ||
                        binding.edittxtcreeraccarticle2.getText().toString().trim().length() != 0 & binding.edittxtcreeraccNbrarticle2.getText().toString().trim().equals("0")){
                    designation_article2 = "";
                    somme_article2 = "0";
                    nbr_article2 = "0";
                }else if (binding.edittxtcreeraccarticle2.getText().toString().trim().isEmpty()){
                    designation_article2 = binding.edittxtcreeraccarticle2.getText().toString().trim();
                    somme_article2 = "0";
                    nbr_article2 = "0";
                }else {
                    designation_article2 = binding.edittxtcreeraccarticle2.getText().toString().trim();
                    somme_article2 = binding.edittxtcreeraccarticle2somme.getText().toString().trim();
                    nbr_article2 = binding.edittxtcreeraccNbrarticle2.getText().toString().trim();
                }
                designationarticle2 = designation_article2;
                sommearticle2 = Integer.parseInt(somme_article2);
                nbrarticle2 = Integer.parseInt(nbr_article2);

                long dateouverture = date_ouverture.getTime();
                String codeclient = binding.txtcreeracccodeclt.getText().toString();


                Articles c_article1 = new Articles(designationarticle1, sommearticle1,nbrarticle1);
                Articles c_article2 = new Articles(designationarticle2, sommearticle2,nbrarticle2);

                int sommecredit  = c_article1.getSomme() + c_article2.getSomme();
                if (Integer.parseInt(versement) < sommecredit){
                    AccountModel account =  this.accountcontroller.creerAccount(codeclient, nomclient,prenomsclient,telephone, c_article1, c_article2, versement, dateouverture);
                    if (account != null) {
                        ClientModel clientModel = clientcontrolleur.recupererClient(account.getClientid());
                        clientViewModel.getClient().setValue(clientModel);

                        AccountModel account_ajoute = accountViewModel.getAccount().getValue();
                        AccountModel accountModel = new AccountModel(Objects.requireNonNull(account_ajoute).getId(),clientModel,account_ajoute.getArticle1(),account_ajoute.getArticle2(),account_ajoute.getSommeaccount(),account_ajoute.getVersement(),account.getReste(),account_ajoute.getDateaccount(),account_ajoute.getNumeroaccount());

                        accountViewModel.getAccount().setValue(accountModel);
                        accountcontroller.setRecapTresteClient(clientModel);
                        accountcontroller.setRecapTaccountClient(clientModel);
                       int total_account_client = accountViewModel.getTotalaccountsclient().getValue();
                       int total_reste_client = accountViewModel.getTotalrestesclient().getValue();

                        String destinationAdress = "+225"+clientModel.getTelephone();
//                        String destinationAdress = "5556";

                        String messageBody = appKessModel.getOwner() +"\n"+"\n"
                                +"bienvenu(e) "+clientModel.getNom() + " "+clientModel.getPrenoms()+"\n"
                                +"votre account est de "+accountModel.getSommeaccount()+" FCFA"+"\n"
                                +"pris le "+date+"\n"
                                +"reste à payer : "+total_reste_client+"\n"
                                +"votre code "+clientModel.getCodeclient();

                        SmsnoSentModel smsnoSentModel = new SmsnoSentModel(clientModel.getId(),messageBody);

                        if (ActivityCompat.checkSelfPermission(requireContext(),
                                android.Manifest.permission.SEND_SMS) !=
                                PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(requireActivity(),
                                    new String[]{android.Manifest.permission.SEND_SMS},
                                    MY_PERMISSIONS_REQUEST_SEND_SMS);
                        } else {

                            smsSender.smsSendwithInnerClass(messageBody, destinationAdress,accountModel.getId() );
                            smsSender.sentReiceiver(smsnoSentModel);
                        }
                    }else {Toast.makeText(getContext(), "un probleme est survenu : account non enregistrer", Toast.LENGTH_SHORT).show();}

                }else {Toast.makeText(getContext(), "versement superieur ou égal à l'account", Toast.LENGTH_SHORT).show();}

            }
        });
    }


    @Override
    public void onStart() {
        super.onStart();
        if (!sessionManagement.getSession()){
            Intent intent = new Intent(getActivity(), MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);

        }

    }

    @Override
    public void onResume() {
        super.onResume();
        if (!sessionManagement.getSession()){
            Intent intent = new Intent(getActivity(), MainActivity.class);
            startActivity(intent);

        }
    }

}