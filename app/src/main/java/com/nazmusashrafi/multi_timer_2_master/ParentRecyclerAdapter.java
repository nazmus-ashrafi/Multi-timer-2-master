package com.nazmusashrafi.multi_timer_2_master;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Parcelable;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

class ParentRecyclerAdapter extends RecyclerView.Adapter<ParentRecyclerAdapter.MyViewHolder> {



    ArrayList<String> parentArrayList; // this is the multitimers title arraylist from db
    ArrayList<Long> parentArrayListTotTime; // this is the multitimers total time arraylist from db
    ArrayList<Long> parentArrayListColor; // this is the multitimers color arraylist from db


    ArrayList<String> daysArrayList = new ArrayList<>(); // this is the singletimer title array list
    ArrayList<Integer> stepNoArrayList = new ArrayList<>(); // this is the singletimer step no. array list
    ArrayList<Long> stepTimeArrayList = new ArrayList<>(); // this is the singletimer time array list

    //arraylists and arrays
    ArrayList<MultiTimer> multiTimerArrayList = new ArrayList<>();
    ArrayList<MultiTimer> yourMultitimerArrayDel= new ArrayList<>();
    ArrayList<MultiTimer> multiTimerArrayListDel= new ArrayList<>();
    ArrayList<ArrayList<SingleTimer>> singleTimerArrayList = new ArrayList<>();

    //Firebase variables
    private DatabaseReference reference;
    private DatabaseReference referenceMultiTimer;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private String onlineUserID;
    //-----

    int isLoaded =0;
    int count =0;
    String id;


    private Context context;


    public ParentRecyclerAdapter(ArrayList<String> parentArrayList,ArrayList<Long> parentArrayListTotTime,ArrayList<Long> parentArrayListColor, Context context) {
        this.parentArrayList = parentArrayList;
        this.parentArrayListTotTime = parentArrayListTotTime;
        this.parentArrayListColor = parentArrayListColor;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.parent_rowlayout,parent,false);

        return new MyViewHolder(view);


    }


    @SuppressLint("ResourceAsColor")
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {

        //Firebase declarations
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        onlineUserID = mUser.getUid();

        // Set Multitimer name, total time and color
        holder.ItemName.setText(parentArrayList.get(position)); //name

        String totalhm = String.format("%02d hr : %02d min", TimeUnit.MILLISECONDS.toHours(parentArrayListTotTime.get(position)),
                TimeUnit.MILLISECONDS.toMinutes(parentArrayListTotTime.get(position)) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(parentArrayListTotTime.get(position))));

        holder.itemTotalTime.setText("Total time : " + totalhm ); //total time


        if(parentArrayListColor.get(position) == 2131034166 ) { //color
            holder.multitimerCard.setBackgroundColor(Color.parseColor("#D8EDEC")); //teal

        }else if(parentArrayListColor.get(position) == 2131034158 ){
            holder.multitimerCard.setBackgroundColor(Color.parseColor("#C1D5F2")); //blue

        }else if(parentArrayListColor.get(position) == 2131034159 ){
            holder.multitimerCard.setBackgroundColor(Color.parseColor("#D5CDEB")); //grape

        }else if(parentArrayListColor.get(position) == 2131034164 ){
            holder.multitimerCard.setBackgroundColor(Color.parseColor("#E7D0EB")); //purple

        }else if(parentArrayListColor.get(position) == 2131034162 ){
            holder.multitimerCard.setBackgroundColor(Color.parseColor("#E3E4EA")); //indigo

        }else if(parentArrayListColor.get(position) == 2131034167 ){
            holder.multitimerCard.setBackgroundColor(Color.parseColor("#EDEACD")); //yellow

        }else if(parentArrayListColor.get(position) == 2131034160 ){
            holder.multitimerCard.setBackgroundColor(Color.parseColor("#CDF5CE")); //green

        }else if(parentArrayListColor.get(position) == 2131034163 ){
            holder.multitimerCard.setBackgroundColor(Color.parseColor("#EBCDD2")); //pink

        }else if(parentArrayListColor.get(position) == 2131034165 ){
            holder.multitimerCard.setBackgroundColor(Color.parseColor("#CAE6FC")); //sky blue

        }else if(parentArrayListColor.get(position) == 2131034156 ){
            holder.multitimerCard.setBackgroundColor(Color.parseColor("#C1D5F2")); //blue

        }


        //-------------------


        //n                               HERE ----------------------- layoutmanager
        ////customLinearlayoutManager


        GridLayoutManager mGridLayoutManager;
        mGridLayoutManager = new GridLayoutManager(context, 2);



//            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false);

        RecyclerView.LayoutManager layoutManager = mGridLayoutManager;



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

                    if(count==0){
                        multiTimerArrayListDel.addAll(multiTimerArrayList);
                        count++;
                    }

                    for(int i=0;i<yourMultitimerArray.size();i++){

                        singleTimerArrayList.add(yourMultitimerArray.get(i).getSingleTimerArrayList());

//                        for(int j=0;j<singleTimerArrayList.get(i).size();j++){
//                            System.out.println(singleTimerArrayList.get(i).get(j).getTitle());
//
////                            notifyDataSetChanged(); //bug fix for horizontal RV not showing up in vertical RV
//                        }

                    }


                }


                    if(isLoaded==0){

                        notifyDataSetChanged(); //bug fix for horizontal RV not showing up in vertical RV
                        isLoaded=1;
                    }


                //load button
                //delete button


            }



            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }


        });



        //--------------------

        //load button

        holder.loadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("briiiiiiiiiiiiis");

                Intent intent;
                intent = new Intent(holder.itemView.getContext(), LoadBuildScreenActivity.class);

                intent.putExtra("SINGLETIMER_ARRAY",  multiTimerArrayList.get(position).getSingleTimerArrayList());
                intent.putExtra("MULTITIMER_ID",  multiTimerArrayList.get(position).getId());
                intent.putExtra("MULTITIMER_INDEX",  Integer.toString(position));


                context.startActivity(intent);

//                System.out.println(multiTimerArrayList.get(position).getId());



            }
        });



        //--------------


        daysArrayList.clear(); //step title arraylist
        stepNoArrayList.clear(); //step number arraylist
        stepTimeArrayList.clear(); //step time arraylist


        //adds singletimer info to multitimers

        for(int i=0;i<multiTimerArrayList.size();i++){

            if(!(position >= multiTimerArrayList.size())){ //

                if(multiTimerArrayList.get(position).equals(multiTimerArrayList.get(i))){


                    singleTimerArrayList.add(multiTimerArrayList.get(i).getSingleTimerArrayList());

                    for(int j=0;j<singleTimerArrayList.get(i).size();j++){


                        daysArrayList.add(singleTimerArrayList.get(i).get(j).getTitle()); //step title
                        stepNoArrayList.add(singleTimerArrayList.get(i).get(j).getStepNumber()); //step number
                        stepTimeArrayList.add(singleTimerArrayList.get(i).get(j).getTime()); //step time


                    }


                }

            }


        }

        //                  HERE------------------- adapter


                final ChildRecyclerAdapter childRecyclerAdapter = new ChildRecyclerAdapter(daysArrayList,stepNoArrayList,stepTimeArrayList);


                holder.ChildRV.setAdapter(childRecyclerAdapter);


                childRecyclerAdapter.notifyDataSetChanged();





        //delete button

        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // are you sure you want to delete
                final String[] colors = {"Yes", "No"};

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
//                builder.setTitle("Are you sure you want to delete this Multitimer?");

                builder.setItems(colors, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // the user clicked on colors[which]

                        if (which==0){
                            System.out.println("Delete it");

                            //update multitimer arraylist

                            //REFERENCE---------
                            reference = FirebaseDatabase.getInstance().getReference().child("Users").child(onlineUserID).child("multitimer arraylist");

                            multiTimerArrayListDel.remove(position);

                            reference.setValue(multiTimerArrayListDel);

                            notifyItemRemoved(position);

                            //---

                            //delete from multitimers and multitimerstemporary by id

                            System.out.println(multiTimerArrayList.get(position).getId());

                            DatabaseReference referenceMul = FirebaseDatabase.getInstance().getReference().child("Users").child(onlineUserID).child("multitimers").child(multiTimerArrayList.get(position).getId());
                            DatabaseReference referenceMulTemp = FirebaseDatabase.getInstance().getReference().child("Users").child(onlineUserID).child("multitimers temporary").child(multiTimerArrayList.get(position).getId());

                            if(referenceMul!=null){
                                referenceMul.setValue(null);
                            }

                            if(referenceMulTemp!=null){
                                referenceMulTemp.setValue(null);
                            }

                            //-------


                            //
                            Intent intent;
                            intent = new Intent(holder.itemView.getContext(), LoggedInTotalDashboardActivity.class);

                            context.startActivity(intent);

                            Toast.makeText(holder.itemView.getContext(),"Multitimer deleted, create a new one",Toast.LENGTH_LONG).show();




//                            //REFERENCE---------
//                            reference = FirebaseDatabase.getInstance().getReference().child("Users").child(onlineUserID).child("multitimer arraylist");
//
//                            reference.addValueEventListener(new ValueEventListener() {
//
//
//                                @Override
//                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//
//                                    //gets custom array list from db
//                                    GenericTypeIndicator<ArrayList<MultiTimer>> t = new GenericTypeIndicator<ArrayList<MultiTimer>>() {};
//
//                                    yourMultitimerArrayDel = dataSnapshot.getValue(t);
//                                    multiTimerArrayListDel.addAll(yourMultitimerArrayDel);
//
//                                    System.out.println("multiTimerArrayListDel size  " + multiTimerArrayListDel.size());
//                                    multiTimerArrayListDel.remove(position);
//
//                                    reference.setValue(multiTimerArrayListDel);
//
//                                }
//
//                                @Override
//                                public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                                }
//                            });



//                            if(count==0){
//                                multiTimerArrayListDel.addAll(multiTimerArrayList);
//                                count++;
//
//                                System.out.println("Count  "+ count); // 1
//                                System.out.println("Size  "+ multiTimerArrayListDel.size()); // 8
//                            }
//
//                            multiTimerArrayListDel.remove(position);
//                            reference.setValue(multiTimerArrayListDel);


                        }else{
                            System.out.println("No");
                            dialog.cancel();

                        }

                    }
                });

                builder.setTitle( Html.fromHtml("<font color='#63c1e8'>Are you sure you want to delete this Multitimer?</font>"));
                builder.show();


                //

//                Intent intent;
//                intent = new Intent(holder.itemView.getContext(), DeleteActivity.class);
//
//                intent.putExtra("MULTITIMER_ID",  multiTimerArrayList.get(position).getId());
//                intent.putExtra("MULTITIMER_POSITION",  Integer.toString(position));
//
//                context.startActivity(intent);


            }



        });


    }


    @Override
    public int getItemCount() {
        return parentArrayList.size();

    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView ItemName;
        TextView itemTotalTime;
        Button loadButton;
        Button deleteButton;
        LinearLayout multitimerCard;

        RecyclerView ChildRV;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            ItemName = itemView.findViewById(R.id.stepname);
            itemTotalTime = itemView.findViewById(R.id.steptotaltime);
            loadButton = itemView.findViewById(R.id.loadBtn);
            deleteButton = itemView.findViewById(R.id.deleteBtn);
            multitimerCard = itemView.findViewById(R.id.multitimercard);

            ChildRV = itemView.findViewById(R.id.ChildRV);


        }


    }


}
