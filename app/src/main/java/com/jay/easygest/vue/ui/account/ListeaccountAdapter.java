package com.jay.easygest.vue.ui.account;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jay.easygest.R;
import com.jay.easygest.model.AccountModel;
import com.jay.easygest.outils.MesOutils;
import com.jay.easygest.vue.AfficherCreditsClientActivity;

import java.util.ArrayList;
import java.util.Date;


public class ListeaccountAdapter extends BaseAdapter {

    private final Context contexte;
    private final LayoutInflater inflater;
    private ArrayList<AccountModel> accounts;

    public ListeaccountAdapter(Context contexte, ArrayList<AccountModel> accounts) {
        this.contexte = contexte;
        this.accounts = accounts;
        this.inflater = LayoutInflater.from(contexte);
    }

    @Override
    public int getCount() {
        return accounts.size();
    }

    @Override
    public Object getItem(int position) {
        return accounts.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        AccountViewHolder holder;

        if (view == null){

            holder = new AccountViewHolder();
            view = inflater.inflate(R.layout.layout_liste_accounts,null);

            holder.layoutaccountadapter = view.findViewById(R.id.layoutaccountadapter);
            holder.textViewaccountdate = view.findViewById(R.id.textViewaccountdate);
            holder.textViewaccountsomme = view.findViewById(R.id.textViewaccountsomme);
            holder.textViewaccountreste = view.findViewById(R.id.textViewaccountreste);

            view.setTag(holder);
        }else {
            holder = (AccountViewHolder) view.getTag();
        }
        String content = "account"+"\n"+ "du"+"\n"+ MesOutils.convertDateToString(new Date(accounts.get(position).getDateaccount())) ;
        holder.textViewaccountdate.setText(content);
        holder.textViewaccountsomme.setText(String.valueOf(accounts.get(position).getSommeaccount()));
        holder.textViewaccountreste.setText(String.valueOf(accounts.get(position).getReste()));


        holder.layoutaccountadapter.setOnClickListener(v->{

//            int pos = (int)v.getTag();
            ((AfficherCreditsClientActivity)contexte).redirectToAfficherAccountActivity(accounts.get(position));

        });

        return view;
    }

    private static class AccountViewHolder{
        TextView textViewaccountdate;
        TextView textViewaccountsomme;
        TextView textViewaccountreste;
        LinearLayout layoutaccountadapter;

    }
}
