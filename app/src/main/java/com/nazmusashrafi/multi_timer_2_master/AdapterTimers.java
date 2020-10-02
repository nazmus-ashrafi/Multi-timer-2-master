package com.nazmusashrafi.multi_timer_2_master;

import android.content.Context;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.lang.ref.Reference;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class AdapterTimers extends RecyclerView.Adapter<AdapterTimers.ViewHolder> {


    ArrayList<SingleTimer>  singleTimer;
    Context context;
    String id;
    long totalTime;
    MultiTimer multiTimer = new MultiTimer();

    //Firebase variables
    private DatabaseReference reference;
    private DatabaseReference referenceMultiTimer;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private String onlineUserID;
    private int position;
    //-----


    public AdapterTimers(ArrayList<SingleTimer> singleTimer,String id,int position,long totalTime, Context context) {

        this.singleTimer = singleTimer;
        this.context = context;
        this.id =id;
        this.totalTime = totalTime;
        this.position = position;

    }



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(context).inflate(R.layout.view_timers, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

        //Firebase declarations
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        onlineUserID = mUser.getUid();
        reference = FirebaseDatabase.getInstance().getReference().child("Users").child(onlineUserID);


        long millis = singleTimer.get(i).getTime();
//        String hms = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis),
//                TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
//                TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
        String hm = String.format("%02d hr : %02d min", TimeUnit.MILLISECONDS.toHours(millis),
                TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)));

        //viewholders - populate the cards from input
        viewHolder.tvStepNumber.setText("Step "+Integer.toString(singleTimer.get(i).getStepNumber()));
        viewHolder.tvTitleStep.setText(singleTimer.get(i).getTitle());
        viewHolder.tvDesc.setText(singleTimer.get(i).getDescription());
        viewHolder.imgArticle.setBackgroundColor(ContextCompat.getColor(this.context, singleTimer.get(i).getColor()));

        viewHolder.tvDuration.setText(hm); //reformat to 00:00

        // Remove button functionality
        final int j = i;
        viewHolder.btRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                System.out.println(id);
                singleTimer.remove(j);


                //get firebase
                //Population

                multiTimer.setSingleTimerArrayList(singleTimer);
                multiTimer.setTitle("");
                multiTimer.setTotalSteps(singleTimer.size());
                multiTimer.setTotalTime(totalTime);
                multiTimer.setId(id);

                referenceMultiTimer = FirebaseDatabase.getInstance().getReference().child("Users").child(onlineUserID).child(id);
//                referenceMultiTimer.child("singleTimerArrayList").child(Integer.toString(j)).setValue(null);
                System.out.println(position);

                //onScroll listener
                notifyDataSetChanged();

            }
        });
    }

    @Override
    public int getItemCount() {
        return singleTimer.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
//        public ImageView imgArticle;
        public TextView tvTitleStep,tvDesc,tvDuration,tvStepNumber;
        public ImageView imgArticle;
        public Button btRemove,btEdit;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            //textviews and imageviews
            tvStepNumber = itemView.findViewById(R.id.tvMainTitleStep);
            tvTitleStep = itemView.findViewById(R.id.tvMainTitle);
            tvDesc      = itemView.findViewById(R.id.tvDesc);
            tvDuration = itemView.findViewById(R.id.tvTimerView);
            imgArticle = itemView.findViewById(R.id.imgArticle);

            //buttons
            btRemove  = itemView.findViewById(R.id.btRemove);
            btEdit = itemView.findViewById(R.id.btEdit);

        }



    }




}
