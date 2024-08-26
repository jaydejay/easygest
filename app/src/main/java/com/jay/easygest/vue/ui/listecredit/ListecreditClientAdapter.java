package com.jay.easygest.vue.ui.listecredit;

import android.content.Context;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.jay.easygest.R;
import com.jay.easygest.model.CreditModel;
import com.jay.easygest.outils.MesOutils;
import com.jay.easygest.vue.AfficherCreditsClientActivity;
import com.jay.easygest.vue.AfficherclientActivity;
import com.jay.easygest.vue.GestionActivity;

import java.util.ArrayList;
import java.util.Date;

public class ListecreditClientAdapter extends BaseAdapter {
    private final ArrayList<CreditModel> credits;
    private final LayoutInflater inflater;
    private final Context context;

    public ListecreditClientAdapter(Context context, ArrayList<CreditModel> credits) {
        this.credits = credits;
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return credits.size();
    }

    @Override
    public Object getItem(int position) {
        return credits.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder ;

        if (convertView == null){
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.layout_liste_credits_client,null);

            holder.txtlisteviewdate = convertView.findViewById(R.id.txtlisteviewdate);
            holder.txtlisteviewsommecredit = convertView.findViewById(R.id.txtlisteviewsommecredit);
            holder.txtlisteviewreste = convertView.findViewById(R.id.txtlisteviewreste);
            holder.btnlisteviewmodif = convertView.findViewById(R.id.btnlisteviewmodif);
            holder.btnlisteviewannuller = convertView.findViewById(R.id.btnlisteviewannuller);


            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }
            if (credits.get(position).getReste() == 0){

                holder.btnlisteviewannuller.setVisibility(convertView.GONE);
                holder.btnlisteviewmodif.setVisibility(convertView.GONE);
            }

            String content = "credit"+"\n"+" du "+"\n"+ MesOutils.convertDateToString(new Date(credits.get(position).getDatecredit())) ;
            holder.txtlisteviewdate.setText(content);
            holder.txtlisteviewsommecredit.setText(String.valueOf(credits.get(position).getSommecredit()));
            holder.txtlisteviewreste.setText(String.valueOf(credits.get(position).getReste()));

            holder.btnlisteviewmodif.setTag(position);
            holder.btnlisteviewannuller.setTag(position);

        holder.btnlisteviewmodif.setOnClickListener(v -> {
            int position1 = (int)v.getTag();
             String activity = v.getContext().getClass().getName();

            if (activity.contains("GestionActivity")){
                ((GestionActivity)context).redirectToModifiercreditActivity(credits.get(position1));
            }else {((AfficherCreditsClientActivity)context).redirectToModifiercreditActivity(credits.get(position1));}
        });

        holder.btnlisteviewannuller.setOnClickListener(v -> {
            int position12 = (int)v.getTag();
            String activity = v.getContext().getClass().getName();
            if (activity.contains("GestionActivity")){
                ((GestionActivity)context).annullerCredit(credits.get(position12));
            }else {((AfficherCreditsClientActivity)context).annullerCredit(credits.get(position12));}

        });

//        holder.txtlisteviewcodeclient.setOnClickListener(v -> {
//            int position3 = (int)v.getTag();
//            String activity = v.getContext().getClass().getName();
//            if (activity.contains("GestionActivity")){
//                ((GestionActivity)context).redirectToAfficherCreditActivity(credits.get(position3));
//            }else {((AfficherclientActivity)context).redirectToAfficherCreditActivity(credits.get(position3));}
//
//        });


        return convertView;
    }

    private static class ViewHolder{
        TextView txtlisteviewdate;
        TextView txtlisteviewsommecredit;
        TextView txtlisteviewreste;
        ImageButton btnlisteviewmodif;
        ImageButton btnlisteviewannuller;

    }
}
