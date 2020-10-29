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
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.text.Editable;
import android.text.Html;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.ContextThemeWrapper;
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
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
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
    ArrayList<MultiTimer> multiTimerArrayListToBeSaved = new ArrayList<>();
    Context context;
    String id;

    long t2Hour,t2Minute;
//    private TextView tvTimerView; //display

    private String m_Text = "";


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
        final Button addTimer = (Button) findViewById(R.id.btReset);
        Button startBtn = findViewById(R.id.btnstarttimer);
        TextView emptyView = (TextView) findViewById(R.id.empty_view);
        Button saveBtn = findViewById(R.id.btSave);


        //Firebase declarations
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        onlineUserID = mUser.getUid();

        //REFERENCE---------
        reference = FirebaseDatabase.getInstance().getReference().child("Users").child(onlineUserID).child("multitimers");


            id = reference.push().getKey();



        //recycler view animation
        recyclerViewAnimation();


        //Add timer button response
        addTimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //
                addTask();

                //

                addTimer.setEnabled(false);


                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        // This method will be executed once the timer is over
                        addTimer.setEnabled(true);


                    }
                },1000);// set time as per your requirement



            }

        });

        //Start button response
        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(singleTimer.size()>1){
                    alertDialogForTitle();

                }else{
                    Toast.makeText(BuildScreenActivity.this,"Must have at-least 2 timers",Toast.LENGTH_LONG).show();
                }

            }
        });

        //Save button response
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("Save timer, go to run page");

                //REFERENCE---------
                DatabaseReference referenceMultiTimerArraySave = FirebaseDatabase.getInstance().getReference().child("Users").child(onlineUserID).child("multitimer arraylist");

                referenceMultiTimerArraySave.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        //gets custom array list from db
                        GenericTypeIndicator<ArrayList<MultiTimer>> t = new GenericTypeIndicator<ArrayList<MultiTimer>>() {};

                        ArrayList<MultiTimer> yourMultitimerArray = dataSnapshot.getValue(t);

                        if(yourMultitimerArray!=null){

                            System.out.println(yourMultitimerArray.size());

                            //---- ---- ---

                            multiTimerArrayListToBeSaved.addAll(yourMultitimerArray);

                        }



                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
//
//
//                //------


                multiTimerArrayListToBeSaved.add(multiTimer);

                //ask for title and redirect to save page
                //AlertDialog

                AlertDialog.Builder builder = new AlertDialog.Builder(BuildScreenActivity.this);
//                builder.setTitle("Set a title for your multi-timer");

                builder.setTitle( Html.fromHtml("<font color='#63c1e8'>Set a title for your multi-timer</font>"));


// Set up the input
                final EditText input = new EditText(BuildScreenActivity.this);
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(input);

// Set up the buttons
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //REFERENCE---------
                        DatabaseReference referenceMultiTimerArraySave = FirebaseDatabase.getInstance().getReference().child("Users").child(onlineUserID);

                        //filling in the title of the multi-timer
                        m_Text = input.getText().toString();
                        multiTimer.setTitle(m_Text);
                        reference.child(id).setValue(multiTimer);
                        //----

                        //get the multi-timer array from db


                        //update it and push back to db
                        referenceMultiTimerArraySave.child("multitimer arraylist").setValue(multiTimerArrayListToBeSaved).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    Toast.makeText(BuildScreenActivity.this,"Multi-timer saved in saved timers",Toast.LENGTH_LONG).show();

                                }

                            }
                        });

                        //goto run page

                        Intent intent;
                        intent = new Intent(BuildScreenActivity.this, LoggedInTotalDashboardActivity.class);
                        System.out.println(id);
                        intent.putExtra("id", id);
//                            intent.putExtra("view",layoutManager)
                        startActivity(intent);




                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();



            }
        });

        //---


    }

    private void alertDialogForTitle() {
        //AlertDialog

//        AlertDialog.Builder builder = new AlertDialog.Builder(BuildScreenActivity.this);
//        builder.setTitle("Set a title for your multi-timer");
//
//
//// Set up the input
//        final EditText input = new EditText(BuildScreenActivity.this);
//// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
//        input.setInputType(InputType.TYPE_CLASS_TEXT);
//        builder.setView(input);
//
//// Set up the buttons
//        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//
//                //filling in the title of the multi-timer
//                m_Text = input.getText().toString();
//                multiTimer.setTitle(m_Text);
//                reference.child(id).setValue(multiTimer);
//                //----
//
//                Intent intent;
//                intent = new Intent(BuildScreenActivity.this, RunPageActivity.class);
//                System.out.println(id);
//                intent.putExtra("id", id);
////                            intent.putExtra("view",layoutManager)
//                startActivity(intent);
//
//
//
//
//            }
//        });
//        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.cancel();
//            }
//        });
//
//        builder.show();


        //------------------------------------------------------------------- ----   ----------------

        reference.child(id).setValue(multiTimer);
        //----

        Intent intent;
        intent = new Intent(BuildScreenActivity.this, RunPageActivity.class);
        System.out.println(id);
        intent.putExtra("id", id);
        intent.putExtra("frombuildscreenactivity", true); //

//                            intent.putExtra("view",layoutManager)
        startActivity(intent);


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
                removeTask();
            }
        });

        // update button functionality

        Button editButton = (Button) findViewById(R.id.editBtn);

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //open alertdialog populated with previous values
                editTask(position);

                //push to db with updated values

            }
        });

    }

    private void removeTask() {
        System.out.println(singleTimer.size() );

        if(singleTimer.size()==1){

            singleTimer.clear();
            referenceMultiTimer.setValue(null).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        Toast.makeText(BuildScreenActivity.this,"Multi-timer updated",Toast.LENGTH_LONG).show();

                    }

                }
            });
            mAdapter.notifyDataSetChanged();
//                    referenceMultiTimer = FirebaseDatabase.getInstance().getReference().child("Users").child(onlineUserID).child(id);
//                    referenceMultiTimer.child("singleTimerArrayList").child(Integer.toString(position)).setValue(null);
            //
            totalTime=0;
            stepNumber=0;

            t2Minute=0;
            t2Hour=0;

        }else if(singleTimer.size()>1){

//                    singleTimer.remove(position);
//                    referenceMultiTimer.child("singleTimerArrayList").child(Integer.toString(position)).setValue(null);
//                    mAdapter.notifyItemChanged(position);

            totalTime = totalTime - singleTimer.get(position).getTime();
            singleTimer.remove(position);

            for(int i = 0;i<singleTimer.size();i++){
                singleTimer.get(i).setStepNumber(i+1);
            }

            multiTimer.setSingleTimerArrayList(singleTimer);
            multiTimer.setTitle("");
            multiTimer.setTotalSteps(singleTimer.size());

            long updatedTotalTime = 0;

            for(int i = 0; i<singleTimer.size();i++){
                updatedTotalTime = updatedTotalTime + singleTimer.get(i).getTime();
            }

            multiTimer.setTotalTime(updatedTotalTime);


//            if(singleTimer.size()==1){
//                multiTimer.setId(id);
//            }


            referenceMultiTimer.setValue(multiTimer).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        Toast.makeText(BuildScreenActivity.this,"Multi-timer updated",Toast.LENGTH_LONG).show();

                    }


                }
            });;
            mAdapter.notifyDataSetChanged();


        }else{
            Toast.makeText(BuildScreenActivity.this,"Nothing to remove",Toast.LENGTH_LONG).show();
        }

//                TextView totalTimeView = findViewById(R.id.totalTimeView);
//
//                displayTimeAtTop = totalTime;
//
//                String totalhm = String.format("%02d hr : %02d min", TimeUnit.MILLISECONDS.toHours(displayTimeAtTop),
//                        TimeUnit.MILLISECONDS.toMinutes(displayTimeAtTop) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(displayTimeAtTop)));
//
//                totalTimeView.setText("Total time: " + totalhm);

        long updatedTotalTime = 0;

        for(int i = 0; i<singleTimer.size();i++){
            updatedTotalTime = updatedTotalTime + singleTimer.get(i).getTime();
        }

        multiTimer.setTotalTime(updatedTotalTime);

        //top total time display update
        TextView totalTimeView = findViewById(R.id.totalTimeView);

        String updatedtotalhm = String.format("%02d hr : %02d min", TimeUnit.MILLISECONDS.toHours(updatedTotalTime),
                TimeUnit.MILLISECONDS.toMinutes(updatedTotalTime) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(updatedTotalTime)));

        totalTimeView.setText("Total time: " + updatedtotalhm);
        //--

        //recycler view scrolls to step before deleted step

        rvTimers.post(new Runnable() {
            @Override
            public void run() {
                // Call smooth scroll
                rvTimers.smoothScrollToPosition(position);
            }
        });

        //Bug - make rv scroll from right to left


        //----
    }

    private void editTask(final int position) {

        if(singleTimer.size()<1){
            Toast.makeText(BuildScreenActivity.this,"Nothing to edit",Toast.LENGTH_LONG).show();
        }else {

            final AlertDialog.Builder myDialog = new AlertDialog.Builder(this, R.style.CustomDialog);


            LayoutInflater inflater = LayoutInflater.from(this);

            View myView = inflater.inflate(R.layout.activity_timer_inputs, null);
            myDialog.setView(myView);

            final AlertDialog dialog = myDialog.create();
            dialog.setCancelable(false);
            dialog.show();

            //timer input declarations
            final EditText title = myView.findViewById(R.id.tvMainTitle);
            final TextView step = myView.findViewById(R.id.tvMainTitleStep);
            final EditText desc = myView.findViewById(R.id.tvDesc);
            final Button confirm = myView.findViewById(R.id.btConfirm);
            Button cancel = myView.findViewById(R.id.btCancel);
            Button duration = myView.findViewById(R.id.btDuration);
            final TextView tvTimerView = myView.findViewById(R.id.tvTimerView);
            final ImageView inputViewColor = myView.findViewById(R.id.imgArticle);

            final TextView titleWordCount = myView.findViewById(R.id.titleWordCount);
            final TextView descWordCount = myView.findViewById(R.id.titleDescCount);
            //----

            //word counter
            wordCounter(title, desc, titleWordCount, descWordCount);
            //

            String retrivedTitle;
            final String retrivedId;
            final long retrivedTime;


            // get values from db and populate textviews
            step.setText("Step " + Integer.toString(position + 1));

//        reference = FirebaseDatabase.getInstance().getReference().child("Users").child(onlineUserID);


            referenceMultiTimer.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

//                referenceMultiTimer = FirebaseDatabase.getInstance().getReference().child("Users").child(onlineUserID).child(multiTimer.getId());

                    if ((String) dataSnapshot.child("singleTimerArrayList").child(Integer.toString(position)).child("title").getValue() != null) {
//                    ArrayList<SingleTimer> retrivedArray = (ArrayList<SingleTimer>) dataSnapshot.child("singleTimerArrayList").getValue();

                        //populate inputs with retrieved data
                        String retrivedDesc = (String) dataSnapshot.child("singleTimerArrayList").child(Integer.toString(position)).child("description").getValue();
                        String retrivedTitle = (String) dataSnapshot.child("singleTimerArrayList").child(Integer.toString(position)).child("title").getValue();
                        Long retrivedTime = (Long) dataSnapshot.child("singleTimerArrayList").child(Integer.toString(position)).child("time").getValue();
                        String totalTimeDisplay = String.format("%02d hr : %02d min", TimeUnit.MILLISECONDS.toHours(retrivedTime),
                                TimeUnit.MILLISECONDS.toMinutes(retrivedTime) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(retrivedTime)));
                        long cardColor = (long) dataSnapshot.child("singleTimerArrayList").child(Integer.toString(position)).child("color").getValue();

                        title.setText(retrivedTitle);
                        desc.setText(retrivedDesc);
                        tvTimerView.setText(totalTimeDisplay);
                        int colorInteger = (int) cardColor;

                        inputViewColor.setBackgroundColor(ContextCompat.getColor(myDialog.getContext(), colorInteger));

                        t2Minute = ((retrivedTime / (1000*60)) % 60);
                        t2Hour   = ((retrivedTime / (1000*60*60)) % 24);

                    }

                }


                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            duration.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    timePicker(tvTimerView);
                }
            });


            //push updated data to db once confirm is hit

//                reference.child(id).setValue(multiTimer)
            confirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                    updateAfterEdit(position, title, desc, dialog);

                }
            });

            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });



        }



    }

    private void updateAfterEdit(int position, EditText title, EditText desc, AlertDialog dialog) {

        if((t2Hour*3600*1000 + t2Minute*60*1000)==0){
//                    tvTimerView.setError("Duration cannot be 0");

            Toast.makeText(BuildScreenActivity.this,"Set a duration",Toast.LENGTH_LONG).show();
            return;

        }


        singleTimer.get(position).setTitle(title.getText().toString());
        singleTimer.get(position).setDescription(desc.getText().toString());
        singleTimer.get(position).setTime(t2Hour * 3600 * 1000 + t2Minute * 60 * 1000);


        long updatedTotalTime = 0;

        for (int i = 0; i < singleTimer.size(); i++) {
            updatedTotalTime = updatedTotalTime + singleTimer.get(i).getTime();
        }

        multiTimer.setTotalTime(updatedTotalTime);

        //top total time display update
        TextView totalTimeView = findViewById(R.id.totalTimeView);

        String updatedtotalhm = String.format("%02d hr : %02d min", TimeUnit.MILLISECONDS.toHours(updatedTotalTime),
                TimeUnit.MILLISECONDS.toMinutes(updatedTotalTime) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(updatedTotalTime)));

        totalTimeView.setText("Total time: " + updatedtotalhm);
        //--

        multiTimer.setSingleTimerArrayList(singleTimer);
        referenceMultiTimer.setValue(multiTimer).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(BuildScreenActivity.this,"Multi-timer updated",Toast.LENGTH_LONG).show();

                }

            }
        });

        mAdapter.notifyDataSetChanged();
        dialog.dismiss();

    }

    private void addTask(){

        if(singleTimer.size()==0 ){
            addAtEnd();
        }else{
            if((position+1)==singleTimer.size()){
                addAtEnd();


            }else{

                System.out.println(singleTimer.size());

                String[] colors = {"Add at the end", "Add after step "+ (position+1)};


                //


                //

                AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.AlertDialogCustom));
                builder.setTitle("Add timer");
                builder.setItems(colors, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // the user clicked on colors[which]

                        if (which==0){
                            addAtEnd();
                        }else{
                            addAfterCurrentStep();

                        }

                    }
                });
                builder.show();

                //



            }


        }


    }

    private void addAfterCurrentStep() {
        AlertDialog.Builder myDialog = new AlertDialog.Builder(BuildScreenActivity.this,R.style.CustomDialog);


        LayoutInflater inflater = LayoutInflater.from(BuildScreenActivity.this);

        View myView = inflater.inflate(R.layout.activity_timer_inputs,null);
        myDialog.setView(myView);

        final AlertDialog dialogAddAtEnd = myDialog.create();
        dialogAddAtEnd.setCancelable(false);
        dialogAddAtEnd.show();

        //timer input declarations
        final EditText title = myView.findViewById(R.id.tvMainTitle);
        final TextView step = myView.findViewById(R.id.tvMainTitleStep);
        final EditText desc = myView.findViewById(R.id.tvDesc);
        Button confirm = myView.findViewById(R.id.btConfirm);
        Button cancel = myView.findViewById(R.id.btCancel);
        Button duration = myView.findViewById(R.id.btDuration);
        final TextView tvTimerView = myView.findViewById(R.id.tvTimerView);
        final ImageView inputViewColor = myView.findViewById(R.id.imgArticle);

        final TextView titleWordCount = myView.findViewById(R.id.titleWordCount);
        final TextView descWordCount = myView.findViewById(R.id.titleDescCount);
        //----

        //word counter
        wordCounter(title, desc, titleWordCount, descWordCount);

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

        inputViewColor.setBackgroundColor(ContextCompat.getColor(BuildScreenActivity.this, color));

        final int cardFinalColor = color;


        //

        stepNumber= (position + 1) + 1;
        step.setText("Step " + stepNumber);

        //addTask(stepNumber)


        //cancel button
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stepNumber = singleTimer.size()+1;
                dialogAddAtEnd.dismiss();
            }
        });

        //duration button
        duration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timePicker(tvTimerView);

            }


        });


        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ArrayList<SingleTimer> first = new ArrayList<SingleTimer>();
                ArrayList<SingleTimer> second = new ArrayList<SingleTimer>();


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


                //Population

                //Add after current step manipulations------------------------

                //singleTimer.size() = 3
                //i = 2
                //position = 1

                //2,1


//                singleTimer.add(new SingleTimer('s',mTitle,mDesc,2,2));


                //breaking the original arraylist and reconstruction with new element ----------

                System.out.println("Sizzze   " + singleTimer.size());


                for(int i =0;i<=position;i++){
                    first.add(singleTimer.get(i));
                }

                for(int i=position+1;i<singleTimer.size();i++){
                    singleTimer.get(i).setStepNumber(singleTimer.get(i).getStepNumber()+1);
                    second.add(singleTimer.get(i));
                }

                System.out.println("Sizzze first   " + first.size());
                System.out.println("Sizzze second   " + second.size());

                System.out.println(first.toString());
                System.out.println(second.toString());

                singleTimer.clear();
                singleTimer.addAll(first);
                singleTimer.add(new SingleTimer(stepNumber,mTitle,mDesc,t2Hour*3600*1000 + t2Minute*60*1000,cardFinalColor));
                singleTimer.addAll(second);


                //-------------------



//                for(int k=singleTimer.size()-1;k >= position;k--){
//
//                    singleTimer.add(new SingleTimer('6',mTitle,mDesc,2,cardFinalColor));
//                    System.out.println(k); //2
//                    System.out.println(position); //1
//
//                    singleTimer.get(k+1).setStepNumber(singleTimer.get(k+1).getStepNumber()+1);
//                    //3,2
//                    singleTimer.set((k+1),singleTimer.get(k));
//
////                    singleTimer.remove(singleTimer.size()-1);
//
////                    singleTimer.toArray()[k+1] = singleTimer.toArray()[k];
//                }
//
//                singleTimer.remove(singleTimer.size()-1);
//
//                //-----------------------------
//
//
//                singleTimer.set((position+1),new SingleTimer(stepNumber,mTitle,mDesc,t2Hour*3600*1000 + t2Minute*60*1000,cardFinalColor));

                long updatedTotalTime=0;

                for(int i = 0; i<singleTimer.size();i++){
                    updatedTotalTime = updatedTotalTime + singleTimer.get(i).getTime();
                }

                multiTimer.setSingleTimerArrayList(singleTimer);
                multiTimer.setTitle("");
                multiTimer.setTotalSteps(singleTimer.size());
                multiTimer.setTotalTime(updatedTotalTime);


                if(singleTimer.size()==1){
                    multiTimer.setId(id);
                    String idBackup = id;

                }


//                    mAdapter.notifyItemChanged(3);
                mAdapter.notifyDataSetChanged();

                //-----

                //Total time calculation and display on top of screen


                String totalhm = String.format("%02d hr : %02d min", TimeUnit.MILLISECONDS.toHours(updatedTotalTime),
                        TimeUnit.MILLISECONDS.toMinutes(updatedTotalTime) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(updatedTotalTime)));

                totalTimeView.setText("Total time: " + totalhm);

                //---

                //create and update multi-timer (db operations)
                if(multiTimer.getSingleTimerArrayList().size()==1){
                    //create multitimer
                    reference.child(id).setValue(multiTimer).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(BuildScreenActivity.this,"Multi-timer updated",Toast.LENGTH_LONG).show();

                            }

                            //REFERENCE---------
                            referenceMultiTimer = FirebaseDatabase.getInstance().getReference().child("Users").child(onlineUserID).child("multitimers").child(multiTimer.getId());

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

                    //REFERENCE---------
                    referenceMultiTimer = FirebaseDatabase.getInstance().getReference().child("Users").child(onlineUserID).child("multitimers").child(multiTimer.getId());


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

                dialogAddAtEnd.dismiss();

                t2Hour=0;
                t2Minute=0;

                //recycler view scrolls to present step

                rvTimers.post(new Runnable() {
                    @Override
                    public void run() {
                        // Call smooth scroll
                        rvTimers.smoothScrollToPosition(position+1);
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

    private void addAtEnd() {
        AlertDialog.Builder myDialog = new AlertDialog.Builder(BuildScreenActivity.this,R.style.CustomDialog);

        LayoutInflater inflater = LayoutInflater.from(BuildScreenActivity.this);

        View myView = inflater.inflate(R.layout.activity_timer_inputs,null);
        myDialog.setView(myView);

        final AlertDialog dialogAddAtEnd = myDialog.create();
        dialogAddAtEnd.setCancelable(false);
        dialogAddAtEnd.show();

        //timer input declarations
        final EditText title = myView.findViewById(R.id.tvMainTitle);
        final TextView step = myView.findViewById(R.id.tvMainTitleStep);
        final EditText desc = myView.findViewById(R.id.tvDesc);
        Button confirm = myView.findViewById(R.id.btConfirm);
        Button cancel = myView.findViewById(R.id.btCancel);
        Button duration = myView.findViewById(R.id.btDuration);
        final TextView tvTimerView = myView.findViewById(R.id.tvTimerView);
        final ImageView inputViewColor = myView.findViewById(R.id.imgArticle);

        final TextView titleWordCount = myView.findViewById(R.id.titleWordCount);
        final TextView descWordCount = myView.findViewById(R.id.titleDescCount);
        //----

        //word counter
        wordCounter(title, desc, titleWordCount, descWordCount);


        //do same for LoadBuildScreenActivity

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

        inputViewColor.setBackgroundColor(ContextCompat.getColor(BuildScreenActivity.this, color));

        final int cardFinalColor = color;


        //

        stepNumber= singleTimer.size()+1;
        step.setText("Step "+stepNumber);


        //cancel button
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stepNumber = singleTimer.size()+1;
                dialogAddAtEnd.dismiss();
            }
        });

        //duration button
        duration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timePicker(tvTimerView);


            }


        });


        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mTitle = title.getText().toString().trim();
                String mDesc = desc.getText().toString().trim();

//                id = reference.push().getKey();
                System.out.println("this is id" + id);

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



                //Population
                singleTimer.add(new SingleTimer(stepNumber,mTitle,mDesc,t2Hour*3600*1000 + t2Minute*60*1000,cardFinalColor));

                long updatedTotalTime=0;

                for(int i = 0; i<singleTimer.size();i++){
                    updatedTotalTime = updatedTotalTime + singleTimer.get(i).getTime();
                }

                multiTimer.setSingleTimerArrayList(singleTimer);
                multiTimer.setTitle("");
                multiTimer.setTotalSteps(singleTimer.size());
                multiTimer.setTotalTime(updatedTotalTime);



                if(singleTimer.size()==1){
                    multiTimer.setId(id);
                    String idBackup = id;

                }


//                    mAdapter.notifyItemChanged(3);
                mAdapter.notifyDataSetChanged();

                //-----

                //Total time calculation and display on top of screen


                String totalhm = String.format("%02d hr : %02d min", TimeUnit.MILLISECONDS.toHours(updatedTotalTime),
                        TimeUnit.MILLISECONDS.toMinutes(updatedTotalTime) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(updatedTotalTime)));

                totalTimeView.setText("Total time: " + totalhm);

                //---

                //create and update multi-timer (db operations)
                if(multiTimer.getSingleTimerArrayList().size()==1){
                    //create multitimer
                    reference.child(id).setValue(multiTimer).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(BuildScreenActivity.this,"Multi-timer updated",Toast.LENGTH_LONG).show();

                            }

                            //REFERENCE---------
                            referenceMultiTimer = FirebaseDatabase.getInstance().getReference().child("Users").child(onlineUserID).child("multitimers").child(multiTimer.getId());

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
                    //REFERENCE---------
                    referenceMultiTimer = FirebaseDatabase.getInstance().getReference().child("Users").child(onlineUserID).child("multitimers").child(multiTimer.getId());

                    //update multitimer
                    referenceMultiTimer.setValue(multiTimer).addOnCompleteListener(new OnCompleteListener<Void>() { //BUGGY
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

                dialogAddAtEnd.dismiss();

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

    private void wordCounter(EditText title, EditText desc, final TextView titleWordCount, final TextView descWordCount) {
        //word counter viewer title
        title.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                titleWordCount.setText(30 - s.toString().length() + "/30");

            }
        });


        //word counter viewer desc
        desc.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                descWordCount.setText(110 - s.toString().length() + "/110");

            }
        });
    }

    private void timePicker(final TextView tvTimerView) {
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

    @Override
    public void onBackPressed() {
        System.out.println("the frekin id is "+ id);

        DatabaseReference referenceMultitimersBackPressed = FirebaseDatabase.getInstance().getReference().child("Users").child(onlineUserID).child("multitimers").child(id);

        referenceMultitimersBackPressed.setValue(null);

        //send to dashboard
        Intent intent = new Intent(BuildScreenActivity.this,LoggedInTotalDashboardActivity.class);
        startActivity(intent);


        Toast.makeText(BuildScreenActivity.this,"Multi-timer builder cancelled",Toast.LENGTH_LONG).show();
    }

//


}