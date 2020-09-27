package com.nazmusashrafi.multi_timer_2_master;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import android.app.TimePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.Layout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
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

public class BuildScreenActivity extends AppCompatActivity {

    RecyclerView rvTimers;
    AdapterTimers mAdapter;
    public static final String TIMERARRAYLIST = "com.nazmus.multi_timer2.TIMERARRAYLIST";

    MultiTimer multiTimer = new MultiTimer();

    private DatabaseReference reference;
    private DatabaseReference referenceMultiTimer;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private String onlineUserID;
    long totalTime;

    ArrayList<SingleTimer> singleTimer = new ArrayList<>();
    Context context;

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



        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        onlineUserID = mUser.getUid();
        reference = FirebaseDatabase.getInstance().getReference().child("Users").child(onlineUserID);


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



        mAdapter = new AdapterTimers(singleTimer,this);
        rvTimers.setAdapter(mAdapter);
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

        rvTimers.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                View v = snapHelper.findSnapView(layoutManager);
                int pos = layoutManager.getPosition(v);

                RecyclerView.ViewHolder viewHolder = rvTimers.findViewHolderForAdapterPosition(pos);
                ScrollView rl1 = viewHolder.itemView.findViewById(R.id.rl1);

                if(newState == RecyclerView.SCROLL_STATE_IDLE){
                    rl1.animate().setDuration(350).scaleX(0.8f).scaleY(0.8f).setInterpolator(new AccelerateInterpolator()).start();
                }else{
                    rl1.animate().setDuration(350).scaleX(0.6f).scaleY(0.6f).setInterpolator(new AccelerateInterpolator()).start();
                }


            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });

        //end of recyclerview animation

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
        final EditText desc = myView.findViewById(R.id.tvDesc);
//        final int totalTimeSingleTimer = myView.findViewById(R.id.)
        Button confirm = myView.findViewById(R.id.btConfirm);
        Button cancel = myView.findViewById(R.id.btCancel);
        Button duration = myView.findViewById(R.id.btDuration);
        final TextView tvTimerView = myView.findViewById(R.id.tvTimerView);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mTitle = title.getText().toString().trim();
                String mDesc = desc.getText().toString().trim();


                String id = reference.push().getKey();
                String date = DateFormat.getDateInstance().format(new Date());

                if(TextUtils.isEmpty(mTitle)){
                    title.setError("Title required");
                    return;
                }


                    singleTimer.add(new SingleTimer(2,mTitle,mDesc,200,"Yellow"));

                    multiTimer.setSingleTimerArrayList(singleTimer);
                    multiTimer.setTitle("");
                    multiTimer.setTotalSteps(singleTimer.size());
                    multiTimer.setTotalTime(3000);
                    multiTimer.setId(id);
                    String idBackup = id;


                    mAdapter.notifyItemChanged(3);


                if(multiTimer.getSingleTimerArrayList().size()==1){
                    //create multitimer
                    reference.child(id).setValue(multiTimer).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(BuildScreenActivity.this,"Multitimer updated",Toast.LENGTH_LONG).show();

                            }


                            referenceMultiTimer = FirebaseDatabase.getInstance().getReference().child("Users").child(onlineUserID).child(multiTimer.getId());

                            referenceMultiTimer.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if((ArrayList<SingleTimer>) dataSnapshot.child("singleTimerArrayList").getValue()!=null){
                                        ArrayList<SingleTimer> retrivedArray = (ArrayList<SingleTimer>) dataSnapshot.child("singleTimerArrayList").getValue();

                                        System.out.println(retrivedArray.toString());
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

                                referenceMultiTimer.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if((ArrayList<SingleTimer>) dataSnapshot.child("singleTimerArrayList").getValue()!=null){
                                            ArrayList<SingleTimer> retrivedArray = (ArrayList<SingleTimer>) dataSnapshot.child("singleTimerArrayList").getValue();

                                            System.out.println(retrivedArray.toString());
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

                //



                dialog.dismiss();




            }

        });

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
                                        tvTimerView.setText(f12Hours.format(date));


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

//                System.out.println(t2Hour+":"+t2Minute);
                    addCountDownText();



            }



            private void addCountDownText(){

                //

                totalTime = t2Hour*3600*1000 + t2Minute*60*1000 + totalTime; // in miliseconds

                long seconds = totalTime / 1000;
                long minutes = seconds / 60;
                long hours = minutes / 60;
                String timeDisplay = String.format(Locale.getDefault(),"%02d:%02d:%02d",hours % 24,minutes % 60,seconds % 60);

                //countdowntxt.setText(timeDisplay);

                System.out.println(totalTime);


            }


        });



    }






}