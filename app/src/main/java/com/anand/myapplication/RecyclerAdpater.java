package com.anand.myapplication;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.common.util.concurrent.Striped;

import java.util.ArrayList;
import java.util.HashMap;

public class RecyclerAdpater  extends  RecyclerView.Adapter<RecyclerAdpater.ViewHolder> {

    private Context context;
    private ArrayList<HashMap<String,String>> map;

    public RecyclerAdpater(Context context, ArrayList<HashMap<String,String>> list) {
        this.map = list;
        this.context = context;

    }

    @Override
    public RecyclerAdpater.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycler_row, viewGroup, false);
        return new RecyclerAdpater.ViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onBindViewHolder(final RecyclerAdpater.ViewHolder viewHolder, int i) {
        HashMap<String,String> local=map.get(i);
//        map.get
        viewHolder.lat.setText(local.get("LAT"));
        viewHolder.lang.setText(local.get("LANG"));

    }

    @Override
    public int getItemCount() {
        return map.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        TextView lat,lang;
        public ViewHolder(View view)
        {
            super(view);
            lat=view.findViewById(R.id.lat_row);
            lang=view.findViewById(R.id.lang_row);
        }
    }
}
