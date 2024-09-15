package com.jay.easygest.vue.ui.versementacc;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jay.easygest.R;
import com.jay.easygest.model.VersementsaccModel;
import com.jay.easygest.outils.MesOutils;
import com.jay.easygest.vue.AfficherCreditsClientActivity;
import java.util.ArrayList;
import java.util.Date;

public class VersementaccAdapter extends BaseAdapter{

    private ArrayList<VersementsaccModel> versements;
    private final LayoutInflater inflater;
    private final Context contexte;


    public VersementaccAdapter(ArrayList<VersementsaccModel> versements, Context contexte) {
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
    public View getView(int position, View view, ViewGroup viewGroup) {

        VersementaccHolder holder ;

        if (view == null){
            holder = new VersementaccHolder();
            view = inflater.inflate(R.layout.layout_liste_versementacc,null);
            holder.listevsmtaccdate = view.findViewById(R.id.listevsmtaccdate);
            holder.listevsmtaccsomme = view.findViewById(R.id.listevsmtaccsomme);
            holder.listevsmtaccnacc = view.findViewById(R.id.listevsmtaccnacc);
            holder.llayoutlistevsmtacc = view.findViewById(R.id.llayoutlistevsmtacc);
            view.setTag(holder);
        }else {

            holder = (VersementaccHolder) view.getTag();
        }

        try {

            holder.listevsmtaccdate.setText(MesOutils.convertDateToString(new Date(versements.get(position).getDateversement())));
            holder.listevsmtaccsomme.setText(String.valueOf(versements.get(position).getSommeverse()));
            holder.listevsmtaccnacc.setText(String.valueOf(versements.get(position).getAccount().getNumeroaccount()));
            holder.llayoutlistevsmtacc.setTag(position);

        }catch (Exception e){
//        do nothing
        }

        holder.llayoutlistevsmtacc.setOnClickListener(v -> {

            try{
                int position1 = (int) v.getTag();
                ((AfficherCreditsClientActivity)contexte).redirectToAfficheversementaccActivity(versements.get(position1),position1,this.getCount());
            } catch (Exception e){}
        });

        return view;
    }

    private static class VersementaccHolder{
        TextView listevsmtaccdate;
        TextView listevsmtaccsomme;
        TextView listevsmtaccnacc;
        LinearLayout llayoutlistevsmtacc;
    }
}
