package com.nazmusashrafi.multi_timer_2_master;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class LoggedInSavedTimersFragment extends Fragment {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    RecyclerView.Adapter adapter;

    ArrayList<String> itemsArrayList = new ArrayList<>();
    ArrayList<Long> itemsTimeArrayList = new ArrayList<>();
    ArrayList<Long> itemsColorArrayList = new ArrayList<>();


    //Firebase variables
    private DatabaseReference reference;
    private DatabaseReference referenceMultiTimer;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private String onlineUserID;
    //-----

    //arraylists and arrays
    ArrayList<MultiTimer> multiTimerArrayList = new ArrayList<>();
    ArrayList<String> multiTimerTitleArrayList = new ArrayList<>();
    ArrayList<Long> multiTimerTotalTimeArrayList = new ArrayList<>();
    ArrayList<Long> multiTimerColorArrayList = new ArrayList<>();


    boolean noInfo = true;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_logged_in_saved_timers, container, false);


    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Firebase declarations
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        if(mUser!=null){
            onlineUserID = mUser.getUid();
        }


        recyclerView = (RecyclerView) view.findViewById(R.id.ParentRv);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);


        //show loading
        final ProgressBar loading = view.findViewById(R.id.progressBar);

        recyclerView.setAdapter(adapter);

        if(adapter==null ){
            System.out.println("loading...");

            loading.setVisibility(View.VISIBLE);


        }


        //

        //REFERENCE---------
        reference = FirebaseDatabase.getInstance().getReference().child("Users").child(onlineUserID).child("multitimer arraylist");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //make loader invisible if noInfo

                if(dataSnapshot.hasChildren()){
                    noInfo = false;
                }

                if(noInfo){
                    loading.setVisibility(View.INVISIBLE);
                }

                //------

                //gets custom array list from db
                GenericTypeIndicator<ArrayList<MultiTimer>> t = new GenericTypeIndicator<ArrayList<MultiTimer>>() {};

                ArrayList<MultiTimer> yourMultitimerArray = dataSnapshot.getValue(t);

                if(yourMultitimerArray!=null){

                    //---- ---- ---

                    multiTimerArrayList.addAll(yourMultitimerArray);

                    for(int i=0;i<yourMultitimerArray.size();i++){

                            multiTimerTitleArrayList.add(yourMultitimerArray.get(i).getTitle());
                            multiTimerTotalTimeArrayList.add(yourMultitimerArray.get(i).getTotalTime());
                            multiTimerColorArrayList.add((long) yourMultitimerArray.get(i).getSingleTimerArrayList().get(0).getColor());


                    }

                    itemsArrayList.addAll(multiTimerTitleArrayList); //multitimer title
                    itemsTimeArrayList.addAll(multiTimerTotalTimeArrayList); //multitimer total time
                    itemsColorArrayList.addAll(multiTimerColorArrayList); //multitimer color
                    //send itemstmearraylist to parent recycler view


                    //

                    adapter = new ParentRecyclerAdapter(itemsArrayList,itemsTimeArrayList,itemsColorArrayList,getActivity());


                        recyclerView.setAdapter(adapter);

                        adapter.notifyDataSetChanged();


                    loading.setVisibility(View.INVISIBLE);


                   // move the parent recycler view

//                    recyclerView.smoothScrollToPosition(itemsArrayList.size());
//
//                    new java.util.Timer().schedule(
//                            new java.util.TimerTask() {
//                                @Override
//                                public void run() {
//
//                                    recyclerView.smoothScrollToPosition(0);
//
//                                }
//                            },
//                            2000
//                    );



                }


            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });





//        for(int i=0;i<multiTimerArrayList.size();i++){
//            multiTimerTitleArrayList.add(multiTimerArrayList.get(i).getTitle());
//
//
//        }
//
//        System.out.println("to" + multiTimerTitleArrayList.size());
//
//        itemsArrayList.addAll(multiTimerTitleArrayList);

//        String[] items = {"Multitimer 1","Multitimer 2","Multitimer 3"}; // list of multitimer titles


//        for(int i=0;i<items.length;i++){
//            itemsArrayList.add(items[i]);
//        }





    }


}