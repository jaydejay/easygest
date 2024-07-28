package com.jay.easygest.vue;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.jay.easygest.controleur.Clientcontrolleur;
import com.jay.easygest.model.Articles;
import com.jay.easygest.model.ClientModel;
import com.jay.easygest.model.CreditModel;
import com.jay.easygest.outils.MesOutils;
import com.jay.easygest.R;
import com.jay.easygest.controleur.Creditcontrolleur;
import com.jay.easygest.databinding.ActivityModifiercreditBinding;
import com.jay.easygest.vue.ui.clients.ClientViewModel;
import com.jay.easygest.vue.ui.credit.CreditViewModel;
import com.owlike.genson.Genson;
import com.owlike.genson.annotation.JsonCreator;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONStringer;

import java.util.Date;

public class ModifiercreditActivity extends AppCompatActivity {
    private Creditcontrolleur creditcontrolleur;
    private CreditViewModel creditViewModel;
    private Clientcontrolleur clientcontrolleur;
    private ClientViewModel clientViewModel;
    private ActivityModifiercreditBinding binding;
    private CreditModel credit;
    private ActionBar ab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityModifiercreditBinding.inflate(getLayoutInflater());
        creditcontrolleur = Creditcontrolleur.getCreditcontrolleurInstance(this);
        creditViewModel = new ViewModelProvider(this).get(CreditViewModel.class);
        clientcontrolleur = Clientcontrolleur.getClientcontrolleurInstance(this);
        clientViewModel = new ViewModelProvider(this).get(ClientViewModel.class);

        credit = creditViewModel.getCredit().getValue();
        setContentView(binding.getRoot());
         ab = getSupportActionBar();
        recupCreditOuClient();
        modifierCredit();

    }



    public void recupCreditOuClient(){
            //Tagx=1 modification du credit
            //Tagx=2 octroie d'un autre credit
            if (getIntent().getIntExtra("Tagx",1) == 1){

                if ( credit != null){
                    String nomclient = credit.getClient().getNom().trim();
                    String prenomsclient = credit.getClient().getPrenoms().trim();
                    String codeclient = credit.getClient().getCodeclient().trim();
                    String nbrcredit ="credit "+ credit.getNumerocredit();
                    String article_1 = credit.getArticle1();
                    String article_2 = credit.getArticle2();


                    binding.textViewCodeClient.setText(codeclient);
                    binding.textViewNbrCredit.setText(nbrcredit);
                    binding.editmodificationnomclient.setText(nomclient);
                    binding.editmodificationprenomclient.setText(prenomsclient);
                try {


                    Articles article1 = new Genson().deserialize(article_1, Articles.class);
                    Articles article2 = new Genson().deserialize(article_2, Articles.class);
                    String article1designation = article1.getDesignation();
                    String article1somme = String.valueOf(article1.getPrix()).trim();

                    String article2designation = article2.getDesignation().trim();
                    String article2somme = String.valueOf(article2.getPrix()).trim() ;

                    String qte1 = String.valueOf(article1.getNbrarticle()).trim() ;
                    String qte2 = String.valueOf(article2.getNbrarticle()).trim() ;

                    binding.editmodificationarticle1designation.setText(article1designation);
                    binding.editmodificationarticle1somme.setText(article1somme);
                    binding.editmodificationarticle2designation.setText(article2designation);
                    binding.editmodificationarticle2somme.setText(article2somme);
                    binding.editmodificationversement.setText(String.valueOf(credit.getVersement()).trim());
                    binding.editmodificationversement.setEnabled(false);
                    binding.datemodification.setText( MesOutils.convertDateToString(new Date(credit.getDatecredit())).trim());
                    binding.editTextNbrarticle1.setText(qte1);
                    binding.editTextNbrarticle2.setText(qte2);
                }catch (Exception e){
                     //do nothing
                }
                if (ab != null) {
                    ab.setTitle("modifier credit");
                }

            }

        }else {
                ClientModel client = clientViewModel.getClient().getValue();
//                ClientModel client = clientcontrolleur.getClient();
                String nomclient = client.getNom().trim();
                String prenomsclient = client.getPrenoms().trim();
                String codeclient = client.getCodeclient().trim();

                binding.textViewCodeClient.setText(codeclient);
                binding.editmodificationnomclient.setText(nomclient);
                binding.editmodificationprenomclient.setText(prenomsclient);
                try {
                    binding.btnmodification.setText(R.string.nouveau_credit);
                    if (ab != null) {
                        ab.setTitle("nouveau credit");
                    }
                }catch (Exception e){
                    //do nothing
                }
        }
    }

    public void modifierCredit(){

        binding.btnmodification.setOnClickListener(v -> {

            String nomclient = binding.editmodificationnomclient.getText().toString().trim();
            String prenomsclient = binding.editmodificationprenomclient.getText().toString().trim();
            String designationarticle1 = binding.editmodificationarticle1designation.getText().toString().trim();
            String article1somme = binding.editmodificationarticle1somme.getText().toString().trim();
            String article1qte = binding.editTextNbrarticle1.getText().toString().trim();
            String versement = binding.editmodificationversement.getText().toString().trim();
            String date = binding.datemodification.getText().toString().trim();
            Date date_credit = MesOutils.convertStringToDate(date);

            if ( designationarticle1.isEmpty() || article1somme.isEmpty() ||article1qte.isEmpty()
                    ||date.isEmpty() )
            {
                Toast.makeText(ModifiercreditActivity.this, "remplissez les champs obligatoires", Toast.LENGTH_SHORT).show();

            } else if (date_credit == null) {
                Toast.makeText(ModifiercreditActivity.this, "format de date incorect", Toast.LENGTH_SHORT).show();
            }else if (binding.editmodificationarticle2designation.getText().toString().trim().length() != 0 && binding.editmodificationarticle2somme.getText().toString().trim().isEmpty() ||
                    binding.editmodificationarticle2designation.getText().toString().trim().length() != 0 & binding.editTextNbrarticle2.getText().toString().trim().isEmpty()) {
                Toast.makeText(ModifiercreditActivity.this, "renseigner le nombre et le prix du deuxieme article", Toast.LENGTH_SHORT).show();
            } else {
                    int sommearticle1 =Integer.parseInt(article1somme) ;
                    int nbrarticle1 = Integer.parseInt(article1qte);

                    String designation_article2 ;
                    String designationarticle2 ;
                    int sommearticle2 ;
                    int nbrarticle2 ;
                    String somme_article2 ;
                    String nbr_article2 ;

                    if (binding.editmodificationarticle2designation.getText().toString().trim().length() != 0 && binding.editmodificationarticle2somme.getText().toString().trim().equals("0") ||
                            binding.editmodificationarticle2designation.getText().toString().trim().length() != 0 & binding.editTextNbrarticle2.getText().toString().trim().equals("0")){
                        designation_article2 = "";
                        somme_article2 = "0";
                        nbr_article2 = "0";
                    }else if (binding.editmodificationarticle2designation.getText().toString().trim().isEmpty()){
                        designation_article2 = binding.editmodificationarticle2designation.getText().toString().trim();
                        somme_article2 = "0";
                        nbr_article2 = "0";
                    }else {
                        designation_article2 = binding.editmodificationarticle2designation.getText().toString().trim();;
                        somme_article2 = binding.editmodificationarticle2somme.getText().toString().trim();
                        nbr_article2 = binding.editTextNbrarticle2.getText().toString().trim();
                    }
                    designationarticle2 = designation_article2;
                    sommearticle2 = Integer.parseInt(somme_article2);
                    nbrarticle2 = Integer.parseInt(nbr_article2);
                    long datecredit = date_credit.getTime();

                    Articles c_article1 = new Articles(designationarticle1, sommearticle1,nbrarticle1);
                    Articles c_article2 =  new Articles(designationarticle2, sommearticle2,nbrarticle2);
                    int sommecredit = c_article1.getSomme() + c_article2.getSomme();

                    if (Integer.parseInt(versement) < sommecredit){
                        if(getIntent().getIntExtra("Tagx",1) == 1){
                            if ( credit != null ){
                                ClientModel client = credit.getClient();
                                long ancienne_sommecredit = credit.getSommecredit();
                                boolean success = creditcontrolleur.modifierCredit(credit.getId(), client,  c_article1, c_article2, versement, datecredit,ancienne_sommecredit);
                                if (success) {
                                    Intent intent = new Intent(ModifiercreditActivity.this, GestionActivity.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    Toast.makeText(this, "un probleme est survenu : modification avortée", Toast.LENGTH_SHORT).show();
                                }
                            }else {Toast.makeText(this, "credit inexistant", Toast.LENGTH_SHORT).show();}
                        }

                        if (getIntent().getIntExtra("Tagx",1) == 2){

                            ClientModel client = clientcontrolleur.getClient();
                            if ( client != null){
                                boolean success =  this.creditcontrolleur.ajouterCredit(client, c_article1, c_article2, versement, datecredit);
                                if (success) {
                                    Intent intent = new Intent(ModifiercreditActivity.this, GestionActivity.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    Toast.makeText(this, "un probleme est survenu 2 : modification avortée", Toast.LENGTH_SHORT).show();
                                }
                            }else {Toast.makeText(this, "client inexistant", Toast.LENGTH_SHORT).show();}

                        }

                    }else {Toast.makeText(this, "versement superieur au credit", Toast.LENGTH_SHORT).show();}



            }
        });


    }


}