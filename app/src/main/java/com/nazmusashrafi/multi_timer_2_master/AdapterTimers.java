package com.nazmusashrafi.multi_timer_2_master;

import android.content.Context;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;

import java.util.ArrayList;

public class AdapterTimers extends RecyclerView.Adapter<AdapterTimers.ViewHolder> {


    ArrayList<SingleTimer>  singleTimer;
    Context context;


    public AdapterTimers(ArrayList<SingleTimer> singleTimer, Context context) {

        this.singleTimer = singleTimer;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(context).inflate(R.layout.view_timers, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.tvTitleStep.setText(singleTimer.get(i).getTitle());
        viewHolder.tvDesc.setText(singleTimer.get(i).getDescription());
    }

    @Override
    public int getItemCount() {
        return singleTimer.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
//        public ImageView imgArticle;
        public TextView tvTitleStep,tvDesc;
        public Button btReadMore;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

//            imgArticle  = itemView.findViewById(R.id.imgArticle);
            tvTitleStep = itemView.findViewById(R.id.tvMainTitleStep);
            tvDesc      = itemView.findViewById(R.id.tvDesc);
            btReadMore  = itemView.findViewById(R.id.btRemove);
        }
    }


}
