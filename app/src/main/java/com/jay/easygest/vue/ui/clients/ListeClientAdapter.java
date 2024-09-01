package com.jay.easygest.vue.ui.clients;

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
import com.jay.easygest.controleur.Clientcontrolleur;
import com.jay.easygest.model.ClientModel;
import com.jay.easygest.vue.GestionActivity;

import java.util.ArrayList;

public class ListeClientAdapter extends BaseAdapter {
    private final ArrayList<ClientModel> clients;
    private final LayoutInflater inflater;
    private final Context contexte;

    public ListeClientAdapter( Context contexte,ArrayList<ClientModel> clients) {
        this.clients = clients;
        this.contexte = contexte;
        this.inflater = LayoutInflater.from(contexte);
    }

    @Override
    public int getCount() {
        return clients.size();
    }

    @Override
    public Object getItem(int position) {
        return clients.get(position);
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
            convertView = inflater.inflate(R.layout.layout_client,null);

            holder.txtlstcodeclient = convertView.findViewById(R.id.txtlstcodeclient);
            holder.txtlstnom = convertView.findViewById(R.id.txtlstnom);
            holder.txtlstprenoms = convertView.findViewById(R.id.txtlstprenoms);
            holder.btnlstnewcredit = convertView.findViewById(R.id.btnlstnewcredit);
            holder.btnlstsupprimer = convertView.findViewById(R.id.btnlstsupprimer);

            convertView.setTag(holder);
        }else {
            holder = (ListeClientAdapter.ViewHolder) convertView.getTag();
        }
        try {
            SpannableString content = new SpannableString(clients.get(position).getCodeclient());
            content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
            holder.txtlstcodeclient.setText(content);
        }catch (Exception ex){
//            do nothing
        }


        holder.txtlstnom.setText(String.valueOf(clients.get(position).getNom()));
        holder.txtlstprenoms.setText(String.valueOf(clients.get(position).getPrenoms()));
        holder.btnlstnewcredit.setTag(position);
        holder.btnlstsupprimer.setTag(position);
        holder.txtlstcodeclient.setTag(position);

        holder.btnlstnewcredit.setOnClickListener(v -> {
            int position3 = (int)v.getTag();
            Clientcontrolleur clientcontrolleur = Clientcontrolleur.getClientcontrolleurInstance(contexte);
            clientcontrolleur.setClient(clients.get(position3));
            ((GestionActivity)contexte).redirectToNouveauCreditActivity(clients.get(position3));
        });

        holder.btnlstsupprimer.setOnClickListener(v -> {
            int position2 = (int)v.getTag();
            ((GestionActivity)contexte).supprimerClient(clients.get(position2));

        });

        holder.txtlstcodeclient.setOnClickListener(v -> {
            int position1 = (int)v.getTag();
            ((GestionActivity)contexte).redirectToAfficherClientActivity(clients.get(position1));

        });

        return convertView;

    }

    private static class ViewHolder{
        TextView txtlstcodeclient;
        TextView txtlstnom;
        TextView txtlstprenoms;
        ImageButton btnlstnewcredit;
        ImageButton btnlstsupprimer;
    }
}
