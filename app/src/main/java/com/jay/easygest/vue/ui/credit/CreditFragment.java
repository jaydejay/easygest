package com.jay.easygest.vue.ui.credit;

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

//import androidx.annotation.NonNull;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.jay.easygest.controleur.Clientcontrolleur;
import com.jay.easygest.controleur.Creditcontrolleur;
import com.jay.easygest.databinding.FragmentCreditBinding;
import com.jay.easygest.model.AppKessModel;
import com.jay.easygest.model.Article;
import com.jay.easygest.model.ArticlesModel;
import com.jay.easygest.model.ClientModel;
import com.jay.easygest.model.CreditModel;
import com.jay.easygest.model.SmsnoSentModel;
import com.jay.easygest.outils.AccessLocalAppKes;
import com.jay.easygest.outils.MesOutils;
import com.jay.easygest.outils.SessionManagement;
import com.jay.easygest.outils.SmsSender;
import com.jay.easygest.vue.MainActivity;
import com.jay.easygest.vue.ui.articles.ArticlesViewModel;
import com.jay.easygest.vue.ui.clients.ClientViewModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


public class CreditFragment extends Fragment {

    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 1;
    private SessionManagement sessionManagement;
    private FragmentCreditBinding binding;
    private Creditcontrolleur creditcontrolleur;
    private Clientcontrolleur clientcontrolleur;
    private ClientViewModel clientViewModel;
    private SmsSender smsSender;
    private AppKessModel appKessModel;
    private Spinner spinnerArt1;
    private Spinner spinnerArt2;
    private final ArrayList<ArticlesModel> listeArticles = new ArrayList<>();
    private ArticlesViewModel articlesViewModel;
//    private ArrayList<Article> liste_article_selectionne;
    private ArticlesModel article1;
    private ArticlesModel article2;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        sessionManagement = new SessionManagement(requireContext());
        binding = FragmentCreditBinding.inflate(inflater, container, false);
        spinnerArt1 =  binding.spinnerArticle1;
        spinnerArt2 = binding.spinnerArticle2;
//        liste_article_selectionne = new ArrayList<>();
        article1 = null;
        article2 = null;
        this.creditcontrolleur = Creditcontrolleur.getCreditcontrolleurInstance(getContext());
        clientcontrolleur = Clientcontrolleur.getClientcontrolleurInstance(getContext());
        clientViewModel = new ViewModelProvider(this).get(ClientViewModel.class);
        articlesViewModel = new ViewModelProvider(this).get(ArticlesViewModel.class);
        smsSender = new SmsSender(getContext(), getActivity());
        AccessLocalAppKes accessLocalAppKes = new AccessLocalAppKes(getContext());
        appKessModel = accessLocalAppKes.getAppkes();

        this.initFragment();
        getSelectedArticle1();
        getSelectedArticle2();
        this.ajouterCredit();

        return  binding.getRoot();
    }

    private void initFragment(){

        if (appKessModel.getBasecode() != null ){
            binding.txtcreercodeclt.setText(MesOutils.generateurcodeclt(appKessModel.getBasecode()));
        }
        chargerArticlesDeBqd();

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
        binding.edittxtcreerarticle1prix.setText(String.valueOf(article1.getPrix()));

    }

    private void onSpinner2ItemSelectedHandler(AdapterView<?> adapterView, int position) {
        Adapter adapter = adapterView.getAdapter();
        article2 = (ArticlesModel) adapter.getItem(position);
        if (article1.getPrix() == 0  ){
            Toast.makeText(getContext(), "choisisez d'abord le premier article", Toast.LENGTH_SHORT).show();
        }else {
            binding.edittxtcreerarticle2prix.setText(String.valueOf(article2.getPrix()));
        }


    }

    /**
     * permet d'ajouter le premier credit d'un client
     * elle cree un client avec son premier credit
     */
    public void ajouterCredit(){
        binding.btncreercredit.setOnClickListener(v -> {
            try{
                binding.btncreercredit.setEnabled(false);
                String nomclient = binding.edittxtcreernom.getText().toString().trim();
                String prenomsclient = binding.edittxtcreerprenoms.getText().toString().trim();
                String article1prix = binding.edittxtcreerarticle1prix.getText().toString().trim();
                String article1qte = binding.edittxtcreerNbrarticle1.getText().toString().trim();
                String article2prix  = binding.edittxtcreerarticle2prix.getText().toString().trim();
                String article2qte = binding.edittxtcreerNbrarticle2.getText().toString().trim();

                String telephone =  binding.edittxtcreertelephone.getText().toString().trim();
                String versement = binding.edittxtcreerversement.getText().toString().trim();
                String date = binding.editTextDate.getText().toString().trim();

                if (nomclient.isEmpty()  || prenomsclient.isEmpty() || date.isEmpty() || telephone.isEmpty() || versement.isEmpty()) {
                    Toast.makeText(getContext(), "nom prenoms date telephone et versement  obligatoires", Toast.LENGTH_SHORT).show();
                    binding.btncreercredit.setEnabled(true);
                }else {
                    if (article1.getDesignation().equals("Choisir un article") || article1 == null ){
                        Toast.makeText(getContext(), "premier article obligatoire", Toast.LENGTH_SHORT).show();
                        binding.btncreercredit.setEnabled(true);
                    }else {
                        if (article1prix.isEmpty()){
                            Toast.makeText(getContext(), "renseignez le prix du premier article", Toast.LENGTH_SHORT).show();
                            binding.btncreercredit.setEnabled(true);
                        }else {
                            if (article1qte.isEmpty()){
                                Toast.makeText(getContext(), "renseignez la quantité du premier article", Toast.LENGTH_SHORT).show();
                                binding.btncreercredit.setEnabled(true);
                            }else {
                                if (Integer.parseInt(article1qte) > article1.getQuantite()){
                                    Toast.makeText(getContext(), "la quantité est supérieur à la quantité en stock", Toast.LENGTH_SHORT).show();
                                    binding.btncreercredit.setEnabled(true);
                                }else {
                                    if (Objects.equals(article2.getId(), article1.getId())){
                                        Toast.makeText(getContext(), "articles identiques choisissez un autre", Toast.LENGTH_SHORT).show();
                                        binding.btncreercredit.setEnabled(true);
                                    }else {
                                        if (!article2.getDesignation().equals("Choisir un article") && article2prix.isEmpty()){
                                            Toast.makeText(getContext(), "renseignez le prix du deuxieme article", Toast.LENGTH_SHORT).show();
                                            binding.btncreercredit.setEnabled(true);
                                        }else {
                                            if (!article2.getDesignation().equals("Choisir un article") && article2qte.isEmpty()){
                                                Toast.makeText(getContext(), "renseignez la quantité du deuxieme article", Toast.LENGTH_SHORT).show();
                                                binding.btncreercredit.setEnabled(true);
                                            }else {
                                                if (article2.getDesignation().equals("Choisir un article") && !article2qte.isEmpty() || article2.getDesignation().equals("Choisir un article") && !article2prix.isEmpty() ){
                                                    Toast.makeText(getContext(), "vous devez choisir un article", Toast.LENGTH_SHORT).show();
                                                    binding.btncreercredit.setEnabled(true);
                                                }else {
                                                    int prixarticle2 = article2prix.isEmpty() ? 0 : Integer.parseInt(article2prix);
                                                    int nbrarticle2 = article2qte.isEmpty() ? 0 : Integer.parseInt(article2qte) ;
                                                    if (nbrarticle2 > article2.getQuantite()){
                                                        Toast.makeText(getContext(), "la quantité du deuxieme article est supérieur à la quantité en stock", Toast.LENGTH_SHORT).show();
                                                        binding.btncreercredit.setEnabled(true);
                                                    }else {
                                                        if (MesOutils.convertStringToDate(date) == null) {
                                                            Toast.makeText(getActivity(), "format de date incorrect", Toast.LENGTH_SHORT).show();
                                                            binding.btncreercredit.setEnabled(true);
                                                        }else {
                                                            if (telephone.length() < 10) {
                                                                Toast.makeText(getContext(), "numero doit étre de 10 chiffres", Toast.LENGTH_SHORT).show();
                                                                binding.btncreercredit.setEnabled(true);
                                                            }else {
                                                                long dateouverture =  MesOutils.convertStringToDate(date).getTime();
                                                                String codeclient = binding.txtcreercodeclt.getText().toString();
                                                                Article article1_vendu = new Article(article1.getDesignation(),Integer.parseInt(article1prix),Integer.parseInt(article1qte));

                                                                Article article2_vendu = (article2 != null && !article2.getDescription().equals("Choisir un article")) ?
                                                                        new Article(article2.getDesignation(),prixarticle2,nbrarticle2) :
                                                                        new Article(article2.getDesignation(),0,0);

                                                                int sommecredit  = article1_vendu.getSomme() + article2_vendu.getSomme();
                                                                if (Integer.parseInt(versement) < sommecredit){

                                                                    if (ActivityCompat.checkSelfPermission(requireContext(),
                                                                            Manifest.permission.SEND_SMS) !=
                                                                            PackageManager.PERMISSION_GRANTED) {
                                                                        ActivityCompat.requestPermissions(requireActivity(),
                                                                                new String[]{Manifest.permission.SEND_SMS},
                                                                                MY_PERMISSIONS_REQUEST_SEND_SMS);
                                                                        binding.btncreercredit.setEnabled(true);
                                                                    } else {

                                                                        Map< String, Object> data = new HashMap<>();
                                                                        data.put("codeclient",codeclient);
                                                                        data.put("nomclient",nomclient);
                                                                        data.put("prenomclient",prenomsclient);
                                                                        data.put("telephone",telephone);
                                                                        data.put("article1vendu",article1_vendu);
                                                                        data.put("article2vendu",article2_vendu);
                                                                        data.put("versement",versement);
                                                                        data.put("dateouverture",dateouverture);
                                                                        data.put("article1",article1);
                                                                        data.put("article2",article2);

                                                                        CreditModel creditModel =  this.creditcontrolleur.creerCredit(getContext(),data);
                                                                        if (creditModel != null) {
                                                                            ClientModel client = clientcontrolleur.recupererClient(creditModel.getClient().getId());
                                                                            creditcontrolleur.setRecapTresteClient(client);
                                                                            creditcontrolleur.setRecapTcreditClient(client);
                                                                            clientViewModel.getClient().setValue(client);

                                                                            int total_reste_client = creditcontrolleur.getRecapTresteClient().getValue() != null ? creditcontrolleur.getRecapTresteClient().getValue() : 0;
                                                                            String destinationAdress = "+225"+client.getTelephone();

//                            String destinationAdress = VariablesStatique.EMULATEUR_2_TELEPHONE;

                                                                            messageSender(client, creditModel, date, total_reste_client, destinationAdress);
                                                                        }else {
                                                                            Toast.makeText(getContext(), "un probleme est survenu : crédit non enregistrer", Toast.LENGTH_SHORT).show();
                                                                            binding.btncreercredit.setEnabled(true);
                                                                        }

                                                                    }

                                                                }else {
                                                                    Toast.makeText(getContext(), "versement superieur ou egal au credit", Toast.LENGTH_SHORT).show();
                                                                    binding.btncreercredit.setEnabled(true);
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
            }catch (Exception e){
                Toast.makeText(getContext(), "probleme interne innattendu", Toast.LENGTH_SHORT).show();
                binding.btncreercredit.setEnabled(true);
            }
        });
    }

    private void messageSender(ClientModel client, CreditModel creditModel, String date, int total_reste_client, String destinationAdress) {
        String messageBody = appKessModel.getOwner() +"\n"+"\n"
                +"bienvenu(e) "+ client.getNom() + " "+ client.getPrenoms()+"\n"
                +"votre credit est de "+ creditModel.getSommecredit()+" FCFA"+"\n"
                +"pris le "+ date +"\n"
                +"reste à payer : "+ total_reste_client +"\n"
                +"votre code "+ client.getCodeclient();

        SmsnoSentModel smsnoSentModel = new SmsnoSentModel(client.getId(),messageBody);
        smsSender.smsSendwithInnerClass(messageBody, destinationAdress,smsnoSentModel.getSmsid() );
        smsSender.sentReiceiver(smsnoSentModel);
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

