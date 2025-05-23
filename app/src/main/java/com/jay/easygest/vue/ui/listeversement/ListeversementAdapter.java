package com.jay.easygest.vue.ui.listeversement;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jay.easygest.R;
import com.jay.easygest.model.VersementsModel;
import com.jay.easygest.outils.MesOutils;
import com.jay.easygest.vue.AfficherCreditsClientActivity;
import com.jay.easygest.vue.GestionActivity;

import java.util.ArrayList;
import java.util.Date;

public class ListeversementAdapter extends BaseAdapter {
    private final ArrayList<VersementsModel> versements;
    private final LayoutInflater inflater;
    private final Context contexte;

    public ListeversementAdapter(ArrayList<VersementsModel> versements, Context contexte) {
        this.versements = versements;
        this.contexte = contexte;
        this.inflater = LayoutInflater.from(contexte);
    }

    @Override
    public int getCount() {
        return versements.size();
    }

    @Override
    public Object getItem(int position) {
        return versements.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView,  ViewGroup parent) {

        ViewHolder holder;
        if (convertView == null){
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.layout_liste_versement,null);
            holder.txtlisteversementdate = convertView.findViewById(R.id.txtlisteversementdate);
            holder.txtlisteversementsomme = convertView.findViewById(R.id.txtlisteversementsomme);
            holder.txtlisteversementnum = convertView.findViewById(R.id.txtlisteversementnum);
            holder.llayouteversement = convertView.findViewById(R.id.llayouteversement);
            convertView.setTag(holder);
        }else {

            holder = (ViewHolder) convertView.getTag();
        }
        try {

            holder.txtlisteversementdate.setText(MesOutils.convertDateToString(new Date(versements.get(position).getDateversement()))  );
            holder.txtlisteversementsomme.setText(String.valueOf(versements.get(position).getSommeverse()));
            holder.txtlisteversementnum.setText(String.valueOf(versements.get(position).getCredit().getNumerocredit()));
            holder.llayouteversement.setTag(position);

        }catch (Exception e){
//        do nothing
        }


        holder.llayouteversement.setOnClickListener(v -> {
            try{
                int position1 = (int) v.getTag();
                String activity = v.getContext().getClass().getName();
                if (activity.contains("GestionActivity")){
                    ((GestionActivity)contexte).redirectToAfficheversementActivity(versements.get(position1),position1,this.getCount());
                }else {((AfficherCreditsClientActivity)contexte).redirectToAfficheversementActivity(versements.get(position1),position1,this.getCount());}
            }catch (Exception e){
                //do nothing
            }


        });

        return convertView;
    }

    private static class ViewHolder{

        TextView txtlisteversementdate;
        TextView txtlisteversementnum;
        TextView txtlisteversementsomme;
        LinearLayout llayouteversement;

    }
}
