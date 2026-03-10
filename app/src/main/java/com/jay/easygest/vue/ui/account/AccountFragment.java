package com.jay.easygest.vue.ui.account;

import static com.jay.easygest.outils.VariablesStatique.MY_PERMISSIONS_REQUEST_SEND_SMS;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
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
import com.jay.easygest.model.Article;
import com.jay.easygest.model.ArticlesModel;
import com.jay.easygest.model.ClientModel;
import com.jay.easygest.model.SmsnoSentModel;
import com.jay.easygest.outils.AccessLocalAppKes;
import com.jay.easygest.outils.MesOutils;
import com.jay.easygest.outils.SessionManagement;
import com.jay.easygest.outils.SmsSender;
import com.jay.easygest.vue.MainActivity;
import com.jay.easygest.vue.ui.articles.ArticlesViewModel;
import com.jay.easygest.vue.ui.clients.ClientViewModel;

import java.util.ArrayList;
import java.util.List;
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
    private ArticlesViewModel articlesViewModel;
    private final List<ArticlesModel> listeArticles = new ArrayList<>() ;
    private Spinner spinnerArt1;
    private Spinner spinnerArt2;
    private ArticlesModel article2;
    private ArticlesModel article1;

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
        articlesViewModel = new ViewModelProvider(this).get(ArticlesViewModel.class);
        appKessModel = accessLocalAppKes.getAppkes();
        smsSender = new SmsSender(getContext(), getActivity());
        spinnerArt1 =  binding.spinneraccArticle1;
        spinnerArt2 = binding.spinneraccArticle2;
        initFragment();
        creerAccount();
        chargerArticlesDeBqd();
        getSelectedArticle1();
        getSelectedArticle2();
        return binding.getRoot();
    }

    private void initFragment(){
        AppKessModel appKessModel = accessLocalAppKes.getAppkes();
        if (appKessModel.getBasecode() != null ){
            binding.txtcreeracccodeclt.setText(MesOutils.generateurcodeclt(appKessModel.getBasecode()));
        }

    }

    private void chargerArticlesDeBqd() {
        articlesViewModel.getLesArticleInstocklivedatas2().observe(getViewLifecycleOwner(), articles -> {
            listeArticles.addAll(articles);
            ArrayAdapter<ArticlesModel> adapter = new ArrayAdapter<>(
                    requireContext(),
                    android.R.layout.simple_spinner_item,
                    listeArticles);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerArt1.setAdapter(adapter);
            spinnerArt2.setAdapter(adapter);
        });

    }

    private void getSelectedArticle1(){

        this.spinnerArt1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                onSpinner1ItemSelectedHandler(parent, position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void getSelectedArticle2(){

        this.spinnerArt2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                onSpinner2ItemSelectedHandler(parent, position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void onSpinner1ItemSelectedHandler(AdapterView<?> adapterView, int position) {
        Adapter adapter = adapterView.getAdapter();
        article1 = (ArticlesModel) adapter.getItem(position);
        binding.edittxtcreeraccarticle1prix.setText(String.valueOf(article1.getPrix()));
    }

    private void onSpinner2ItemSelectedHandler(AdapterView<?> adapterView, int position) {
        Adapter adapter = adapterView.getAdapter();
        article2 = (ArticlesModel) adapter.getItem(position);
        if (article1.getPrix() == 0  ){
            Toast.makeText(getContext(), "choisisez d'abord le premier article", Toast.LENGTH_SHORT).show();
        }else {
            binding.edittxtcreeraccarticle2prix.setText(String.valueOf(article2.getPrix()));
        }
    }

    public void creerAccount(){
        binding.btncreeraccount.setOnClickListener(v -> {
            binding.btncreeraccount.setEnabled(false);
            String nomclient = binding.edittxtcreeaccrnom.getText().toString().trim();
            String prenomsclient = binding.edittxtcreeraccprenoms.getText().toString().trim();
            String article1prix = binding.edittxtcreeraccarticle1prix.getText().toString().trim();
            String article1qte = binding.edittxtcreeraccNbrarticle1.getText().toString().trim();
            String article2prix  = binding.edittxtcreeraccarticle2prix.getText().toString().trim();
            String article2qte = binding.edittxtcreeraccNbrarticle2.getText().toString().trim();

            String telephone =  binding.edittxtcreeracctelephone.getText().toString().trim();
            String versement = binding.edittxtcreeraccversement.getText().toString().trim();
            String date = binding.editTextaccDate.getText().toString().trim();
//            Date date_ouverture = MesOutils.convertStringToDate(date);

            if (nomclient.isEmpty()  || prenomsclient.isEmpty() || date.isEmpty() || telephone.isEmpty() || versement.isEmpty())
            {
                Toast.makeText(getContext(), "nom prenoms date telephone et versement  obligatoires", Toast.LENGTH_SHORT).show();
                binding.btncreeraccount.setEnabled(true);

            }else {
                if (article1.getDesignation().equals("Choisir un article") || article1 == null ){
                    Toast.makeText(getContext(), "premier article obligatoire", Toast.LENGTH_SHORT).show();
                    binding.btncreeraccount.setEnabled(true);
                }else {
                    if (article1prix.isEmpty()){
                        Toast.makeText(getContext(), "renseignez le prix du premier article", Toast.LENGTH_SHORT).show();
                        binding.btncreeraccount.setEnabled(true);
                    }else {
                        if (article1qte.isEmpty()){
                            Toast.makeText(getContext(), "renseignez la quantité du premier article", Toast.LENGTH_SHORT).show();
                            binding.btncreeraccount.setEnabled(true);
                        }else {
                            if (Integer.parseInt(article1qte) > article1.getQuantite()){
                                Toast.makeText(getContext(), "la quantité est supérieur à la quantité en stock", Toast.LENGTH_SHORT).show();
                                binding.btncreeraccount.setEnabled(true);
                            }else {
                                if (!article2.getDesignation().equals("Choisir un article") && article2prix.isEmpty()){
                                    Toast.makeText(getContext(), "renseignez le prix du deuxieme article", Toast.LENGTH_SHORT).show();
                                    binding.btncreeraccount.setEnabled(true);
                                }else {
                                    if (!article2.getDesignation().equals("Choisir un article") && article2qte.isEmpty()){
                                        Toast.makeText(getContext(), "renseignez la quantité du deuxieme article", Toast.LENGTH_SHORT).show();
                                        binding.btncreeraccount.setEnabled(true);
                                    }else {
                                        if (article2.getDesignation().equals("Choisir un article") && !article2qte.isEmpty() || article2.getDesignation().equals("Choisir un article") && !article2prix.isEmpty() ){
                                            Toast.makeText(getContext(), "vous devez choisir un article", Toast.LENGTH_SHORT).show();
                                            binding.btncreeraccount.setEnabled(true);
                                        }else {
                                            if (Objects.equals(article2.getId(), article1.getId())){
                                                Toast.makeText(getContext(), "articles identiques choisissez un autre", Toast.LENGTH_SHORT).show();
                                                binding.btncreeraccount.setEnabled(true);
                                            }else {

                                                int prixarticle2 = article2prix.isEmpty() ? 0 : Integer.parseInt(article2prix);
                                                int nbrarticle2 = article2qte.isEmpty() ? 0 : Integer.parseInt(article2qte) ;
                                                if (nbrarticle2 > article2.getQuantite()){
                                                    Toast.makeText(getContext(), "la quantité du deuxieme article est supérieur à la quantité en stock", Toast.LENGTH_SHORT).show();
                                                    binding.btncreeraccount.setEnabled(true);
                                                }else {
                                                    if (MesOutils.convertStringToDate(date) == null) {
                                                        Toast.makeText(getActivity(), "format de date incorrect", Toast.LENGTH_SHORT).show();
                                                        binding.btncreeraccount.setEnabled(true);
                                                    }else {
                                                        if (telephone.length() < 10) {
                                                            Toast.makeText(getContext(), "numero doit étre de 10 chiffres", Toast.LENGTH_SHORT).show();
                                                            binding.btncreeraccount.setEnabled(true);
                                                        }else {
                                                            long dateouverture =  MesOutils.convertStringToDate(date).getTime();
                                                            String codeclient = binding.txtcreeracccodeclt.getText().toString();

                                                            Article article1_vendu = new Article(article1.getDesignation(),Integer.parseInt(article1prix),Integer.parseInt(article1qte));
                                                            Article article2_vendu = (article2 != null && !article2.getDescription().equals("Choisir un article")) ?
                                                                    new Article(article2.getDesignation(),prixarticle2,nbrarticle2) :
                                                                    new Article(article2.getDesignation(),0,0);
                                                            int sommeaccount  = article1_vendu.getSomme() + article2_vendu.getSomme();
                                                            if (Integer.parseInt(versement) < sommeaccount){

                                                                if (ActivityCompat.checkSelfPermission(requireContext(),
                                                                        Manifest.permission.SEND_SMS) !=
                                                                        PackageManager.PERMISSION_GRANTED) {
                                                                    ActivityCompat.requestPermissions(requireActivity(),
                                                                            new String[]{Manifest.permission.SEND_SMS},
                                                                            MY_PERMISSIONS_REQUEST_SEND_SMS);
                                                                    binding.btncreeraccount.setEnabled(true);
                                                                } else {
                                                                    AccountModel account =  this.accountcontroller.creerAccount(codeclient, nomclient,prenomsclient,telephone, article1_vendu, article2_vendu, versement, dateouverture,article1,article2);
                                                                    if (account != null) {
                                                                        ClientModel clientModel = clientcontrolleur.recupererClient(account.getClient().getId());
                                                                        clientViewModel.getClient().setValue(clientModel);
                                                                        AccountModel account_ajoute = accountViewModel.getAccount().getValue();
                                                                        AccountModel accountModel = new AccountModel(Objects.requireNonNull(account_ajoute).getId(),clientModel,account_ajoute.getArticle1(),account_ajoute.getArticle2(),account_ajoute.getVersement(),account_ajoute.getDateaccount(),account_ajoute.getNumeroaccount());
                                                                        accountViewModel.getAccount().setValue(accountModel);
                                                                        accountcontroller.setRecapTresteClient(clientModel);
                                                                        accountcontroller.setRecapTaccountClient(clientModel);
                                                                        int total_reste_client = accountViewModel.getTotalrestesclient().getValue();
                                                                        messageSender(clientModel, accountModel, date, total_reste_client);
                                                                    }else {
                                                                        Toast.makeText(getContext(), "un probleme est survenu : account non enregistrer", Toast.LENGTH_SHORT).show();
                                                                        binding.btncreeraccount.setEnabled(true);
                                                                    }
                                                                }
                                                            }else {
                                                                Toast.makeText(getContext(), "versement superieur ou egal au credit", Toast.LENGTH_SHORT).show();
                                                                binding.btncreeraccount.setEnabled(true);
                                                            }
                                                        }
                                                    }
                                                }

                                            }
                                        }
                                    }

                                }
                            }

                        }
                    }
                }

            }
        });
    }

    private void messageSender(ClientModel clientModel, AccountModel accountModel, String date, int total_reste_client) {
        String destinationAdress = "+225"+ clientModel.getTelephone();
        String messageBody = appKessModel.getOwner() +"\n"+"\n"
                +"bienvenu(e) "+ clientModel.getNom() + " "+ clientModel.getPrenoms()+"\n"
                +"votre account est de "+ accountModel.getSommeaccount()+" FCFA"+"\n"
                +"pris le "+ date +"\n"
                +"reste à payer : "+ total_reste_client +"\n"
                +"votre code "+ clientModel.getCodeclient();

        SmsnoSentModel smsnoSentModel = new SmsnoSentModel(clientModel.getId(),messageBody);
        smsSender.smsSendwithInnerClass(messageBody, destinationAdress,smsnoSentModel.getSmsid() );
        smsSender.sentReiceiver(smsnoSentModel);
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