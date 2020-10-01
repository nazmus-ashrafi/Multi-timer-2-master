package com.nazmusashrafi.multi_timer_2_master;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import android.app.TimePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.Layout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class BuildScreenActivity extends AppCompatActivity {


    RecyclerView rvTimers;
    AdapterTimers mAdapter;
    public static final String TIMERARRAYLIST = "com.nazmus.multi_timer2.TIMERARRAYLIST";

    MultiTimer multiTimer = new MultiTimer();

    //Firebase variables
    private DatabaseReference reference;
    private DatabaseReference referenceMultiTimer;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private String onlineUserID;
    //-----

    long totalTime;
    int stepNumber = 0;
    int position;
    long displayTimeAtTop;

    ArrayList<SingleTimer> singleTimer = new ArrayList<>();
    Context context;
    String id;

    long t2Hour,t2Minute;
//    private TextView tvTimerView; //display


    public void AdapterTimers(ArrayList<SingleTimer> singleTimer, Context context) {

        this.singleTimer = singleTimer;
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_build_screen);

//        Toolbar toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);


        //Buttons and text view declaration
        Button addTimer = (Button) findViewById(R.id.btAdd);
        Button startBtn = findViewById(R.id.btnstarttimer);
        TextView emptyView = (TextView) findViewById(R.id.empty_view);
        Button saveBtn = findViewById(R.id.btSave);


        //Firebase declarations
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        onlineUserID = mUser.getUid();
        reference = FirebaseDatabase.getInstance().getReference().child("Users").child(onlineUserID);

        //recycler view animation
        recyclerViewAnimation();

        //Add timer button response
        addTimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addTask();
            }

        });

        //Start button response
        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("go to run page");
            }
        });

        //Save button response
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("Save timer, go to run page");
            }
        });

        //---




    }

    private void recyclerViewAnimation() {
        //start of recycler view animation

        rvTimers = findViewById(R.id.rvTimers);
        final RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this,RecyclerView.HORIZONTAL,false);
        rvTimers.setLayoutManager(layoutManager);


//        if (singleTimer.isEmpty()) {
//            rvTimers.setVisibility(View.GONE);
//            emptyView.setVisibility(View.VISIBLE);
//        }else {
//            rvTimers.setVisibility(View.VISIBLE);
//            emptyView.setVisibility(View.GONE);
//        }

        //Adapter
        mAdapter = new AdapterTimers(singleTimer,id,position,totalTime,this);
        rvTimers.setAdapter(mAdapter);


        //--
        rvTimers.setPadding(0,0,0,0);

        final SnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(rvTimers);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                RecyclerView.ViewHolder viewHolder = rvTimers.findViewHolderForAdapterPosition(0);

                if(viewHolder!=null){
                    ScrollView rl1 =  viewHolder.itemView.findViewById(R.id.rl1);
                    rl1.animate().setDuration(350).scaleX(0.8f).scaleY(0.8f).setInterpolator(new AccelerateInterpolator()).start();
                }

            }
        },100);


        // card zoom in on scroll animation
        rvTimers.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                View v = snapHelper.findSnapView(layoutManager);

                if(!singleTimer.isEmpty()){
                    int pos = layoutManager.getPosition(v);
                    position = pos;
//                    System.out.println(position);

                    RecyclerView.ViewHolder viewHolder = rvTimers.findViewHolderForAdapterPosition(pos);
                    ScrollView rl1 = viewHolder.itemView.findViewById(R.id.rl1);

                    if(newState == RecyclerView.SCROLL_STATE_IDLE){
                        rl1.animate().setDuration(350).scaleX(0.8f).scaleY(0.8f).setInterpolator(new AccelerateInterpolator()).start();
                    }else{
                        rl1.animate().setDuration(350).scaleX(0.6f).scaleY(0.6f).setInterpolator(new AccelerateInterpolator()).start();
                    }

                }

            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }



        });

        //end of recyclerview animation

        // remove button functionality

        Button removeButton = (Button) findViewById(R.id.removeBtn);


        removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println(singleTimer.size() );

                if(singleTimer.size()==1){

                    singleTimer.clear();
                    referenceMultiTimer.setValue(null);
                    mAdapter.notifyDataSetChanged();
//                    referenceMultiTimer = FirebaseDatabase.getInstance().getReference().child("Users").child(onlineUserID).child(id);
//                    referenceMultiTimer.child("singleTimerArrayList").child(Integer.toString(position)).setValue(null);
                    //
                    totalTime=0;
                    stepNumber=0;

                }else{

//                    singleTimer.remove(position);
//                    referenceMultiTimer.child("singleTimerArrayList").child(Integer.toString(position)).setValue(null);
//                    mAdapter.notifyItemChanged(position);

                    //do what happens if you delete step 1 when having 2 steps
                    //change the whole multitimer, ie remake the multi-timer and update it


                    //
                    //fix bugs in stepnumbers - better yet try to make stepnumber = index number + 1

                    totalTime = totalTime - singleTimer.get(position).getTime();
                    singleTimer.remove(position);

                    for(int i = 0;i<singleTimer.size();i++){
                        singleTimer.get(i).setStepNumber(i+1);
                    }

                    multiTimer.setSingleTimerArrayList(singleTimer);
                    multiTimer.setTitle("");
                    multiTimer.setTotalSteps(singleTimer.size());
                    multiTimer.setTotalTime(totalTime);
                    multiTimer.setId(id);
                    referenceMultiTimer.setValue(multiTimer);
                    mAdapter.notifyDataSetChanged();

                }

                TextView totalTimeView = findViewById(R.id.totalTimeView);

                displayTimeAtTop = totalTime;

                String totalhm = String.format("%02d hr : %02d min", TimeUnit.MILLISECONDS.toHours(displayTimeAtTop),
                        TimeUnit.MILLISECONDS.toMinutes(displayTimeAtTop) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(displayTimeAtTop)));

                totalTimeView.setText("Total time: " + totalhm);

            }
        });

    }


    private void addTask(){

        AlertDialog.Builder myDialog = new AlertDialog.Builder(this,R.style.CustomDialog);


        LayoutInflater inflater = LayoutInflater.from(this);

        View myView = inflater.inflate(R.layout.activity_timer_inputs,null);
        myDialog.setView(myView);

        final AlertDialog dialog = myDialog.create();
        dialog.setCancelable(false);
        dialog.show();

        //timer input declarations
        final EditText title = myView.findViewById(R.id.tvMainTitle);
        final TextView step = myView.findViewById(R.id.tvMainTitleStep);
        final EditText desc = myView.findViewById(R.id.tvDesc);
        Button confirm = myView.findViewById(R.id.btConfirm);
        Button cancel = myView.findViewById(R.id.btCancel);
        Button duration = myView.findViewById(R.id.btDuration);
        final TextView tvTimerView = myView.findViewById(R.id.tvTimerView);
        final ImageView inputViewColor = myView.findViewById(R.id.imgArticle);
        //----

        //give a random color to the card

        Random rand = new Random();
        int n = rand.nextInt(10); //bound :10 is (0 to 9)
        int color=0;

        if(n==0){
            color = R.color.cardColorBlueL;
        }else if(n==1){
            color= R.color.cardColorCreamL;
        }else if(n==2){
            color = R.color.cardColorIndigoL;
        }else if(n==3){
            color = R.color.cardColorSkyBlueL;
        }else if(n==4){
            color = R.color.cardColorGreenL;
        }else if(n==5){
            color =  R.color.cardColorYellowL;
        }else if(n==6){
            color =  R.color.cardColorPurpleL;
        }else if(n==7){
            color =  R.color.cardColorPinkL;
        }else if(n==8){
            color =  R.color.cardColorGrapeL;
        }else if(n==9){
            color =  R.color.cardColorTealL;
        }

        inputViewColor.setBackgroundColor(ContextCompat.getColor(this, color));

        final int cardFinalColor = color;


        //

        stepNumber= singleTimer.size()+1;
        step.setText("Step "+stepNumber);


        //cancel button
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stepNumber = singleTimer.size()+1;
                dialog.dismiss();
            }
        });

        //duration button
        duration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Initialize time picker dialog
                TimePickerDialog timePickerDialog = new TimePickerDialog(
                        BuildScreenActivity.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                //Initialize hour and minute
                                t2Hour = hourOfDay;
                                t2Minute = minute;
                                //Store hours and minutes in string
                                String time = t2Hour + ":" + t2Minute;
                                //Initialize 24 hours time format
                                SimpleDateFormat f24Hours = new SimpleDateFormat(
                                        "HH:mm"
                                );
                                try {
                                    Date date = f24Hours.parse(time);
                                    //initialize 12 hour time format
                                    SimpleDateFormat f12Hours = new SimpleDateFormat(
                                            "HH:mm" //"hh:mm"
                                    );
                                    //Set selected time on textview
                                    long millis = t2Hour*3600*1000 + t2Minute*60*1000;
                                    String hm = String.format("%02d hr : %02d min", TimeUnit.MILLISECONDS.toHours(millis),
                                            TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)));
                                    tvTimerView.setText(hm); // format 00 hr : 00 min


                                }catch(ParseException e){
                                    e.printStackTrace();
                                }


                            }
                        },12,0,true

                );
                timePickerDialog.setTitle("Enter hours and minutes");


                timePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable((Color.TRANSPARENT)));
//                timePickerDialog.updateTime((int)t2Hour,(int)t2Minute);
                timePickerDialog.updateTime(0,0);

                timePickerDialog.show();


            }


        });


        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mTitle = title.getText().toString().trim();
                String mDesc = desc.getText().toString().trim();

                id = reference.push().getKey();


                TextView totalTimeView = findViewById(R.id.totalTimeView);


                //Cannot leave empty
                if(TextUtils.isEmpty(mTitle)){
                    title.setError("Title required");
                    return;
                }


//                System.out.println(tvTimerView.getText().toString());

                if((t2Hour*3600*1000 + t2Minute*60*1000)==0){
//                    tvTimerView.setError("Duration cannot be 0");

                        Toast.makeText(BuildScreenActivity.this,"Set a duration",Toast.LENGTH_LONG).show();
                        return;

                }
                //-----

                //Total time calculation and display on top of screen


                totalTime = t2Hour*3600*1000 + t2Minute*60*1000 + totalTime;
                displayTimeAtTop = totalTime;

                String totalhm = String.format("%02d hr : %02d min", TimeUnit.MILLISECONDS.toHours(displayTimeAtTop),
                        TimeUnit.MILLISECONDS.toMinutes(displayTimeAtTop) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(displayTimeAtTop)));

                totalTimeView.setText("Total time: " + totalhm);

                //----


                    //Population
                    singleTimer.add(new SingleTimer(stepNumber,mTitle,mDesc,t2Hour*3600*1000 + t2Minute*60*1000,cardFinalColor));

                    multiTimer.setSingleTimerArrayList(singleTimer);
                    multiTimer.setTitle("");
                    multiTimer.setTotalSteps(singleTimer.size());
                    multiTimer.setTotalTime(totalTime);
                    multiTimer.setId(id);
                    String idBackup = id;

                    mAdapter.notifyItemChanged(3);

                    //-----

                //create and update multi-timer (db operations)
                if(multiTimer.getSingleTimerArrayList().size()==1){
                    //create multitimer
                    reference.child(id).setValue(multiTimer).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(BuildScreenActivity.this,"Multitimer updated",Toast.LENGTH_LONG).show();

                            }

                            referenceMultiTimer = FirebaseDatabase.getInstance().getReference().child("Users").child(onlineUserID).child(multiTimer.getId());

                            //optional check
                            referenceMultiTimer.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if((ArrayList<SingleTimer>) dataSnapshot.child("singleTimerArrayList").getValue()!=null){
                                        ArrayList<SingleTimer> retrivedArray = (ArrayList<SingleTimer>) dataSnapshot.child("singleTimerArrayList").getValue();

//                                        System.out.println(retrivedArray.toString());
                                    }


                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });

                        }
                    });


                }else{
                    //update multitimer
                    referenceMultiTimer.setValue(multiTimer).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(BuildScreenActivity.this,"Multitimer updated",Toast.LENGTH_LONG).show();

                                //optional check
                                referenceMultiTimer.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if((ArrayList<SingleTimer>) dataSnapshot.child("singleTimerArrayList").getValue()!=null){
                                            ArrayList<SingleTimer> retrivedArray = (ArrayList<SingleTimer>) dataSnapshot.child("singleTimerArrayList").getValue();

//                                            System.out.println(retrivedArray.toString());
                                        }

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                            }

                        }
                    });


                }

                //-------


                //MISC

                dialog.dismiss();

                t2Hour=0;
                t2Minute=0;

                //recycler view scrolls to present step

                rvTimers.post(new Runnable() {
                    @Override
                    public void run() {
                        // Call smooth scroll
                        rvTimers.smoothScrollToPosition(mAdapter.getItemCount() +1 );
                    }
                });

                //----

                //reset adapter to send id

                System.out.println(position);

                mAdapter = new AdapterTimers(singleTimer,id,position,totalTime,getApplicationContext());
                rvTimers.setAdapter(mAdapter);


                //----




            }

        });





    }

//



}