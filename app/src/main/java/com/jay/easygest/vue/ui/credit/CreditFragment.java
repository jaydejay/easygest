package com.jay.easygest.vue.ui.credit;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
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

import com.google.gson.Gson;
import com.jay.easygest.controleur.Clientcontrolleur;
import com.jay.easygest.controleur.Creditcontrolleur;
import com.jay.easygest.databinding.FragmentCreditBinding;
import com.jay.easygest.model.AppKessModel;
import com.jay.easygest.model.Article;
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
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


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
    private ArrayList<Article> listeArticles = new ArrayList<>();
    private ArticlesViewModel articlesViewModel;
    private ArrayList<Article> liste_article_selectionne;
    private Article article1;
    private Article article2;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        sessionManagement = new SessionManagement(requireContext());
        binding = FragmentCreditBinding.inflate(inflater, container, false);
        spinnerArt1 =  binding.spinnerArticle1;
        spinnerArt2 = binding.spinnerArticle2;
        liste_article_selectionne = new ArrayList<>();
        article1 = null;
        article2 = null;
        this.creditcontrolleur = Creditcontrolleur.getCreditcontrolleurInstance(getContext());
        clientcontrolleur = Clientcontrolleur.getClientcontrolleurInstance(getContext());
        clientViewModel = new ViewModelProvider(this).get(ClientViewModel.class);
        articlesViewModel = new ViewModelProvider(this).get(ArticlesViewModel.class);
        smsSender = new SmsSender(getContext(), getActivity());
        AccessLocalAppKes accessLocalAppKes = new AccessLocalAppKes(getContext());
        appKessModel = accessLocalAppKes.getAppkes();

        View root = binding.getRoot();
        this.initFragment();
        getSelectedArticle1();
        getSelectedArticle2();
        this.ajouterCredit();

        return root;
    }

    private void initFragment(){

        if (appKessModel.getBasecode() != null ){
            binding.txtcreercodeclt.setText(MesOutils.generateurcodeclt(appKessModel.getBasecode()));
        }
        chargerArticlesDeBqd();

    }

    private void chargerArticlesDeBqd() {

        articlesViewModel.getLesArticleInstocklivedatas().observe(getViewLifecycleOwner(), articles -> {
            listeArticles.add(new Article("Choisir un article", 0,0));
            listeArticles.addAll(articles);
                ArrayAdapter<Article> adapter = new ArrayAdapter<>(
                        getContext(),
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
                onSpinner1ItemSelectedHandler(parent, view, position, id);
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
                onSpinner2ItemSelectedHandler(parent, view, position, id);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void onSpinner1ItemSelectedHandler(AdapterView<?> adapterView, View view, int position, long id) {
        Adapter adapter = adapterView.getAdapter();
         article1 = (Article) adapter.getItem(position);

        if (id != 0){
            liste_article_selectionne.add(article1);
            binding.edittxtcreerarticle1prix.setText(String.valueOf(article1.getPrix()));
            binding.edittxtcreerarticle1.setText(article1.getDesignation());
        }
    }

    private void onSpinner2ItemSelectedHandler(AdapterView<?> adapterView, View view, int position, long id) {
        Adapter adapter = adapterView.getAdapter();
        article2 = (Article) adapter.getItem(position);
        if (id != 0){
            if (article1.getPrix() == 0  ){
                Toast.makeText(getContext(), "choisisez d'abord le premier article", Toast.LENGTH_SHORT).show();
            }else {
                liste_article_selectionne.add(article2);
                binding.edittxtcreerarticle2prix.setText(String.valueOf(article2.getPrix()));
                binding.edittxtcreerarticle2.setText(article2.getDesignation());
            }
        }

    }





    /**
     * permet d'ajouter le premier credit d'un client
     * elle cree un client avec son premier credit
     */
    public void ajouterCredit(){


        binding.btncreercredit.setOnClickListener(v -> {
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
            Date date_ouverture = MesOutils.convertStringToDate(date);

            if (nomclient.isEmpty()  || prenomsclient.isEmpty() || date.isEmpty() || telephone.isEmpty() || versement.isEmpty())
            {
                Toast.makeText(getContext(), "nom prenoms date telephone et versement  obligatoires", Toast.LENGTH_SHORT).show();
                binding.btncreercredit.setEnabled(true);

            }else {
                if (article1 == null ){
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
                            if (article2 != null && article2prix.isEmpty()){
                                Toast.makeText(getContext(), "renseignez le prix du deuxieme article", Toast.LENGTH_SHORT).show();
                                binding.btncreercredit.setEnabled(true);
                            }else {
                                if (article2 != null && article2qte.isEmpty()){
                                    Toast.makeText(getContext(), "renseignez la quantité du deuxieme article", Toast.LENGTH_SHORT).show();
                                    binding.btncreercredit.setEnabled(true);
                                }else {
                                    if (date_ouverture == null) {
                                        Toast.makeText(getActivity(), "format de date incorrect", Toast.LENGTH_SHORT).show();
                                        binding.btncreercredit.setEnabled(true);
                                    }else {
                                        if (telephone.length() < 10) {
                                            Toast.makeText(getContext(), "numero doit étre de 10 chiffres", Toast.LENGTH_SHORT).show();
                                            binding.btncreercredit.setEnabled(true);
                                        }else {
                                            int prixarticle2 = article2prix.isEmpty() ? 0 : Integer.parseInt(article2prix);
                                            int nbrarticle2 = article2qte.isEmpty() ? 0 : Integer.parseInt(article2qte) ;

                                            long dateouverture = date_ouverture.getTime();
                                            String codeclient = binding.txtcreercodeclt.getText().toString();

                                            Article article1_vendu;
                                            Article article2_vendu = null;
                                            int sommecredit;

                                            article1_vendu = new Article(article1.getDesignation(),Integer.parseInt(article1prix),Integer.parseInt(article1qte));

                                            if (article2 != null){
                                                article2_vendu = new Article(article2.getDesignation(),prixarticle2,nbrarticle2);
                                                sommecredit  = article1_vendu.getSomme() + article2_vendu.getSomme();
                                            }else {
                                                sommecredit  = article1_vendu.getSomme();
                                            }

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
                                                    data.put("sommecredit",sommecredit);

                                                    CreditModel creditModel =  this.creditcontrolleur.creerCredit(data);
                                                    if (creditModel != null) {
                                                        ClientModel client = clientcontrolleur.recupererClient(creditModel.getClientid());
                                                        creditcontrolleur.setRecapTresteClient(client);
                                                        creditcontrolleur.setRecapTcreditClient(client);
                                                        clientViewModel.getClient().setValue(client);

                                                        int total_reste_client = creditcontrolleur.getRecapTresteClient().getValue();
                                                        String destinationAdress = "+225"+client.getTelephone();

//                            String destinationAdress = VariablesStatique.EMULATEUR_2_TELEPHONE;

                                                        String messageBody = appKessModel.getOwner() +"\n"+"\n"
                                                                +"bienvenu(e) "+client.getNom() + " "+client.getPrenoms()+"\n"
                                                                +"votre credit est de "+creditModel.getSommecredit()+" FCFA"+"\n"
                                                                +"pris le "+date+"\n"
                                                                +"reste à payer : "+total_reste_client+"\n"
                                                                +"votre code "+client.getCodeclient();

                                                        SmsnoSentModel smsnoSentModel = new SmsnoSentModel(client.getId(),messageBody);
                                                        smsSender.smsSendwithInnerClass(messageBody, destinationAdress,smsnoSentModel.getSmsid() );
                                                        smsSender.sentReiceiver(smsnoSentModel);
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
        });
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

