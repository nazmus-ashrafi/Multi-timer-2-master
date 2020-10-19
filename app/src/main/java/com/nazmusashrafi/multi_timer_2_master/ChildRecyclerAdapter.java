package com.nazmusashrafi.multi_timer_2_master;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

class ChildRecyclerAdapter extends RecyclerView.Adapter<ChildRecyclerAdapter.MyViewHolder> {

    ArrayList<String> arrayList; // singletimer titles from the singletimer arraylist
    ArrayList<Integer> stepNumbersArrayList; // singletimer step numbers from the singletimer arraylist
    ArrayList<Long> stepTimeArrayList; // singletimer time from the singletimer arraylist


    public ChildRecyclerAdapter(ArrayList<String> arrayList,ArrayList<Integer> stepNumbersArrayList,ArrayList<Long> stepTimeArrayList) {
        this.arrayList = arrayList;
        this.stepNumbersArrayList = stepNumbersArrayList;
        this.stepTimeArrayList = stepTimeArrayList;


    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.child_rowlayout,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.Days.setText(arrayList.get(position)); //step title
        holder.StepNumber.setText("Step " + stepNumbersArrayList.get(position).toString()); //step number

        String totalhm = String.format("%02d hr : %02d min", TimeUnit.MILLISECONDS.toHours(stepTimeArrayList.get(position)),
                TimeUnit.MILLISECONDS.toMinutes(stepTimeArrayList.get(position)) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(stepTimeArrayList.get(position))));

        holder.StepTime.setText(totalhm); //step time

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        TextView Days; //step titles
        TextView StepNumber; //step number
        TextView StepTime; //step time


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            Days = itemView.findViewById(R.id.steptitle);
            StepNumber = itemView.findViewById(R.id.stepnum);
            StepTime = itemView.findViewById(R.id.steptime);

        }
    }
}
