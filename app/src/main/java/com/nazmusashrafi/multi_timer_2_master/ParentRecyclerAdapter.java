package com.nazmusashrafi.multi_timer_2_master;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

class ParentRecyclerAdapter extends RecyclerView.Adapter<ParentRecyclerAdapter.MyViewHolder> {

    ArrayList<String> parentArrayList; // this is the multitimers title arraylist from db
    ArrayList<Long> parentArrayListTotTime; // this is the multitimers total time arraylist from db
    Context context;

    ArrayList<String> daysArrayList = new ArrayList<>(); // this is the singletime title array list

    //arraylists and arrays
    ArrayList<MultiTimer> multiTimerArrayList = new ArrayList<>();
    ArrayList<ArrayList<SingleTimer>> singleTimerArrayList = new ArrayList<>();

    //Firebase variables
    private DatabaseReference reference;
    private DatabaseReference referenceMultiTimer;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private String onlineUserID;
    //-----


    public ParentRecyclerAdapter(ArrayList<String> parentArrayList,ArrayList<Long> parentArrayListTotTime, Context context) {
        this.parentArrayList = parentArrayList;
        this.parentArrayListTotTime = parentArrayListTotTime;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.parent_rowlayout,parent,false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        //Firebase declarations
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        onlineUserID = mUser.getUid();

        // Multitimer name and totaltime
        holder.ItemName.setText(parentArrayList.get(position));

        String totalhm = String.format("%02d hr : %02d min", TimeUnit.MILLISECONDS.toHours(parentArrayListTotTime.get(position)),
                TimeUnit.MILLISECONDS.toMinutes(parentArrayListTotTime.get(position)) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(parentArrayListTotTime.get(position))));


        holder.itemTotalTime.setText("Total time : " + totalhm );

        //-------------------

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false);
        holder.ChildRV.setLayoutManager(layoutManager);
        holder.ChildRV.setHasFixedSize(true);

        //REFERENCE---------
        reference = FirebaseDatabase.getInstance().getReference().child("Users").child(onlineUserID).child("multitimer arraylist");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //gets custom array list from db
                GenericTypeIndicator<ArrayList<MultiTimer>> t = new GenericTypeIndicator<ArrayList<MultiTimer>>() {};

                ArrayList<MultiTimer> yourMultitimerArray = dataSnapshot.getValue(t);

                if(yourMultitimerArray!=null){


                    //---- ---- ---

                    multiTimerArrayList.addAll(yourMultitimerArray);

                    for(int i=0;i<yourMultitimerArray.size();i++){

                        singleTimerArrayList.add(yourMultitimerArray.get(i).getSingleTimerArrayList());

                        for(int j=0;j<singleTimerArrayList.get(i).size();j++){
                            System.out.println(singleTimerArrayList.get(i).get(j).getTitle());
                        }

                    }


//                    itemsArrayList.addAll(multiTimerTitleArrayList);
//                    itemsTimeArrayList.addAll(multiTimerTotalTimeArrayList);
//                    //send itemstmearraylist to parent recycler view
//
//                    System.out.println(multiTimerTotalTimeArrayList.toString());
//
//
//                    //
//
//                    adapter = new ParentRecyclerAdapter(itemsArrayList,itemsTimeArrayList,getActivity());
//
//
//                    recyclerView.setAdapter(adapter);
//
//                    adapter.notifyDataSetChanged();
//
//
//                    loading.setVisibility(View.INVISIBLE);




                }


            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        //--------------

        daysArrayList.clear();
        String[] days ={"cook n djnjnd jnjnd njnjnd njnjnjd jnjnjd njnj","clean","cook","clean","cook","clean","cook","clean"}; // singletimer titles

        for(int i=0;i<days.length;i++){
            daysArrayList.add(days[i]);
        }

        ChildRecyclerAdapter childRecyclerAdapter = new ChildRecyclerAdapter(daysArrayList);
        holder.ChildRV.setAdapter(childRecyclerAdapter);
        childRecyclerAdapter.notifyDataSetChanged();

    }

    @Override
    public int getItemCount() {
        return parentArrayList.size();

    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView ItemName;
        TextView itemTotalTime;
        RecyclerView ChildRV;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            ItemName = itemView.findViewById(R.id.stepname);
            itemTotalTime = itemView.findViewById(R.id.steptotaltime);
            ChildRV = itemView.findViewById(R.id.ChildRV);
        }
    }
}
