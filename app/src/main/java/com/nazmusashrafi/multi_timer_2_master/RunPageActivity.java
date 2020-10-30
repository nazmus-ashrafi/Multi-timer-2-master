package com.nazmusashrafi.multi_timer_2_master;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.Objects;

public class RunPageActivity extends AppCompatActivity {

    //Universal declarations
    RecyclerView rvTimers;
    AdapterTimers mAdapter;

    long totalTime = 0;
    int stepNumber = 0;
    int position;
    ArrayList<SingleTimer> singleTimer = new ArrayList<>();
    ArrayList<SingleTimer> singleTimerBackup = new ArrayList<>();
    Context context;
    String idUni;
    String indexUni;
    long t2Hour,t2Minute;
    MultiTimer multiTimer = new MultiTimer();
    String id;
    Integer one;
    Integer two;

    Boolean frombuildscreenactivity;


    //timer universal vars
    private Button stopButton;
    private TextView countdownText;
    private Button countdownButton;
    private Button resetMultiTimerButton;
    private Button skipButton;
    private  Button resetStepButton;
    private CountDownTimer countDownTimer;
    private ProgressBar timerProgress;
    private  TextView ringStepNumber;
    private long totalStepTime = 0;
    private boolean timerRunning;
    private long timeLeft;
    private Dialog customDialog;
    private String currentSound;
    private TextView swipeToShowCardText;

    private boolean timerStarted = false;

    //Firebase variables
    private DatabaseReference reference;
    private DatabaseReference referenceMultiTimer;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private String onlineUserID;
    //-----

    int backPressCount = 0;

    DatabaseReference referenceMultitimerArraylistBackPress;
    DatabaseReference referenceMultitimersBackPress;
    DatabaseReference referenceMultitimersTempBackPress;


    //variables & declarations
    PieChart pieChart;
    BuildScreenActivity buildPage = new BuildScreenActivity();

    // Uni vars
    ArrayList<Long> timesUni = new ArrayList<>();
    ArrayList<Long> colorUni = new ArrayList<>();
    ArrayList<Long> stepNumberUni = new ArrayList<>();

    long timeAtPaused = 0;

    int counter =0;
    long millis =0;

    //sounds
    MediaPlayer bellSound;
    MediaPlayer chilledSound;
    MediaPlayer softSound;
    MediaPlayer gearSound;
    MediaPlayer batmanSound;



    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        System.out.println("WE ARE IN RUNPAGE ACTIVITY");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_run_page);


        //sounds
        bellSound = MediaPlayer.create(this, R.raw.bell);
        chilledSound = MediaPlayer.create(this, R.raw.chilled);
        softSound = MediaPlayer.create(this, R.raw.soft);
        gearSound = MediaPlayer.create(this, R.raw.gear);
        batmanSound = MediaPlayer.create(this, R.raw.batman);


        //Buttons and text view declaration
        Button addTimer = (Button) findViewById(R.id.btReset);

        //Firebase initialize variables
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        onlineUserID = mUser.getUid();

        //get id and index from build page
        Bundle bundle = getIntent().getExtras();

        String id = bundle.getString("id");
        String index = bundle.getString("index");
        frombuildscreenactivity = bundle.getBoolean("frombuildscreenactivity"); //

        System.out.println("Id at runpage(momo):  "+id);
        idUni = id;
        indexUni = index;

        //initialize timer components
        countdownText = findViewById(R.id.countdown_text);
        countdownButton = findViewById(R.id.btnPlayTimer); //play/pause button
        timerProgress = findViewById(R.id.timerProgress);
        ringStepNumber = findViewById(R.id.countdown_steps);
        swipeToShowCardText = findViewById(R.id.swipe_to_show_cards_text);

        skipButton = findViewById(R.id.btSkip);
        stopButton = findViewById(R.id.btStop);
        resetStepButton = findViewById(R.id.resetStepBtn);
        resetMultiTimerButton = findViewById(R.id.btReset);

        customDialog = new Dialog(this);


        //REFERENCE---------
        reference = FirebaseDatabase.getInstance().getReference().child("Users").child(onlineUserID).child("multitimers").child(id);

        //        recycler view animation
        recyclerViewAnimation();

        //get current sound
        FirebaseDatabase.getInstance().getReference().child("Users").child(onlineUserID).child("sound").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue()!=null){

                    currentSound = dataSnapshot.getValue().toString();

                }else{
                    currentSound = "Bell";
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //-------------


        reference.addValueEventListener(new ValueEventListener() {   //TOTAL TIME BUG
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child("totalTime").getValue()!=null){

                    timeLeft = (long) dataSnapshot.child("totalTime").getValue();  //bug
                    //maybe send to save page after save , and not go to run - in loadbuildscreen and  buildscreen

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //play/pause button
        countdownButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(timerRunning){

//                    timeAtPaused =  timeAtPaused + (100-((timeLeft*100)/timesUni.get(counter)));
//
////                    100-((timeLeft*100)/timeLeftLong))


                    pauseTimer();

                }else{
                    if(!timerStarted){

                        timerStarted= true;
                        startTimer(timesUni.get(0));  //bug, sometimes gets no data set from loadbuildscreen page, when larger timer

//                    startTimer();

                    }else{

                        startTimer(millis);

                    }


                }
            }
        });

        //skip button
        skipButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View view) {

                if(timesUni.size()==counter+1){
                    Toast.makeText(RunPageActivity.this,"Step "+(stepNumberUni.get(counter))+" skipped",Toast.LENGTH_LONG).show();

                    countDownTimer.cancel();
                    timerRunning = false;
                    timerProgress.setVisibility(View.INVISIBLE);
                    countdownButton.setText("Done");
                    countdownButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            System.out.println("Back to homepage");
                        }
                    });

                    //stuff that happens after multi-timer finishes
                    resetStepButton.setVisibility(View.INVISIBLE);
                    skipButton.setVisibility(View.INVISIBLE);
                    Toast.makeText(RunPageActivity.this,"Activity completed",Toast.LENGTH_LONG).show();

                    openCustomDialog();

//
                }else{

                    if(countDownTimer!=null){

                        countDownTimer.cancel();
                        timerRunning = false;
                        countdownButton.setText("Start");

                        //change step number, ring color, recycler view, toast
                        //change ring color
                        colorSetter(counter+1);

                        //change step number in ring
                        ringStepNumber.setText("Step " + stepNumberUni.get(counter+1).toString());

                        //move recycler view to next position
                        rvTimers.smoothScrollToPosition(Math.toIntExact(stepNumberUni.get(counter)));

                        //toast
                        Toast.makeText(RunPageActivity.this,"Step "+(stepNumberUni.get(counter))+" skipped",Toast.LENGTH_LONG).show();

                        counter++;

                        //----------

                        startTimer(timesUni.get(counter));

                    }else{
                        Toast.makeText(RunPageActivity.this,"Can't skip if not started",Toast.LENGTH_LONG).show();
                    }

                }

            }
        });

        //reset step

        resetStepButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(timesUni.size()==counter+1){
                    Toast.makeText(RunPageActivity.this,"Step "+(stepNumberUni.get(counter))+" reset",Toast.LENGTH_LONG).show();

                    countDownTimer.cancel();
                    timerRunning = false;
                    startTimer(timesUni.get(counter));

                }else{
                    if(countDownTimer!=null){

                        countDownTimer.cancel();
                        timerRunning = false;
                        countdownButton.setText("Start");

                        //toast
                        Toast.makeText(RunPageActivity.this,"Step "+(stepNumberUni.get(counter))+" reset",Toast.LENGTH_LONG).show();

                        //----------

                        startTimer(timesUni.get(counter));

                    }else{
                        Toast.makeText(RunPageActivity.this,"Can't reset if not started",Toast.LENGTH_LONG).show();
                    }


                }

            }
        });

        //reset full timer

        resetMultiTimerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //popup are you sure

//                if(timesUni.size()==counter+1){
//                    Toast.makeText(RunPageActivity.this,"Step "+(stepNumberUni.get(counter))+" reset",Toast.LENGTH_LONG).show();
//
//                    countDownTimer.cancel();
//                    timerRunning = false;
//
//                    //
//                    counter=0;
//                    startTimer(timesUni.get(0));
//
//
//
//                }else{
//                    if(countDownTimer!=null){
//
//                        countDownTimer.cancel();
//                        timerRunning = false;
//                        countdownButton.setText("Start");
//
//                        //toast
//                        Toast.makeText(RunPageActivity.this,"Step "+(stepNumberUni.get(counter))+" reset",Toast.LENGTH_LONG).show();
//
//                        //----------
//
//                        //
//                        counter=0;
//                        startTimer(timesUni.get(0));
//
//                    }else{
//                        Toast.makeText(RunPageActivity.this,"Can't reset if not started",Toast.LENGTH_LONG).show();
//                    }
//
//
//                }

                //ask if sure or not

                Intent intent = new Intent(getApplicationContext(), RunPageActivity.class);
            intent.putExtra("id", idUni);
            startActivity(intent);

            }
        });


        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        //



        //db ops
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if((ArrayList<SingleTimer>) dataSnapshot.child("singleTimerArrayList").getValue()!=null){
                    ArrayList<SingleTimer> retrivedArray = (ArrayList<SingleTimer>) dataSnapshot.child("singleTimerArrayList").getValue();

                    ArrayList<Long> times = new ArrayList<>();
                    ArrayList<String> titles = new ArrayList<>();
                    ArrayList<String> des = new ArrayList<>();
                    ArrayList<Long> stepNumber = new ArrayList<>();
                    ArrayList<Long> color = new ArrayList<>();


                    for(int i=0;i<retrivedArray.size();i++){
                        times.add((Long) dataSnapshot.child("singleTimerArrayList").child(Integer.toString(i)).child("time").getValue());
                        titles.add((String) dataSnapshot.child("singleTimerArrayList").child(Integer.toString(i)).child("title").getValue());
                        des.add((String) dataSnapshot.child("singleTimerArrayList").child(Integer.toString(i)).child("description").getValue());
                        stepNumber.add((Long) dataSnapshot.child("singleTimerArrayList").child(Integer.toString(i)).child("stepNumber").getValue());
                        color.add((Long) dataSnapshot.child("singleTimerArrayList").child(Integer.toString(i)).child("color").getValue());

                        singleTimer.add(new SingleTimer(stepNumber.get(i).intValue(),titles.get(i),des.get(i),times.get(i),color.get(i).intValue()));
                        singleTimerBackup.add(new SingleTimer(stepNumber.get(i).intValue(),titles.get(i),des.get(i),times.get(i),color.get(i).intValue()));

                    }

                    //displays step 1 time initially in the circle-------

                    for(int j=0;j<times.size();j++){
                        totalTime = totalTime + times.get(j);
                        //this is the total time of all the timers
                    }

                    long seconds = times.get(0) / 1000;
                    long minutes = seconds / 60;
                    long hours = minutes / 60;

                    String timeLeftFormatted = String.format(Locale.getDefault(),"%02d:%02d:%02d",hours % 24,minutes % 60,seconds % 60);

                    countdownText.setText(timeLeftFormatted);

                    //-----

                    //fill uni vars
                    timesUni.addAll(times);
                    colorUni.addAll(color);
                    stepNumberUni.addAll(stepNumber);

                    //set color for ring initially
                    colorSetter(0);



                    //------- -- ----


//                    System.out.println(singleTimer.toString());
//
//                                        System.out.println(retrivedArray.toString());

                    //Pie chart
                    pieChartInitiater(times,titles,color);



                    //

//                    Map<String, SingleTimer> td = new HashMap<String, SingleTimer>();
//                    for (DataSnapshot jobSnapshot: dataSnapshot.getChildren()) {
//                        SingleTimer job = jobSnapshot.getValue(SingleTimer.class);
//                        td.put(jobSnapshot.getKey(), job);
//                    }
//
//                    ArrayList<SingleTimer> values = new ArrayList<>(td.values());
//                    List<String> keys = new ArrayList<String>(td.keySet());
//
//                    for (SingleTimer job: values) {
//                        Log.d("firebase", job.getTitle());
//                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }


    private void colorSetter(int number) {
        if(colorUni.get(number) == 2131034166 ) {
            timerProgress.setProgressDrawable(getDrawable(R.drawable.circleteal));
        }else if(colorUni.get(number) == 2131034158 ){
            timerProgress.setProgressDrawable(getDrawable(R.drawable.circle)); //cream
        }else if(colorUni.get(number) == 2131034159 ){
            timerProgress.setProgressDrawable(getDrawable(R.drawable.circlegrape));
        }else if(colorUni.get(number) == 2131034164 ){
            timerProgress.setProgressDrawable(getDrawable(R.drawable.circlepurple)); //
        }else if(colorUni.get(number) == 2131034162 ){
            timerProgress.setProgressDrawable(getDrawable(R.drawable.circleindigo));
        }else if(colorUni.get(number) == 2131034167 ){
            timerProgress.setProgressDrawable(getDrawable(R.drawable.circleyellow));
        }else if(colorUni.get(number) == 2131034160 ){
            timerProgress.setProgressDrawable(getDrawable(R.drawable.circlegreen));
        }else if(colorUni.get(number) == 2131034163 ){
            timerProgress.setProgressDrawable(getDrawable(R.drawable.circlepink));
        }else if(colorUni.get(number) == 2131034165 ){
            timerProgress.setProgressDrawable(getDrawable(R.drawable.circleskyblue));
        }else if(colorUni.get(number) == 2131034156 ){
            timerProgress.setProgressDrawable(getDrawable(R.drawable.circleblue));
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void recyclerViewAnimation() {
        //start of recycler view animation

        rvTimers = findViewById(R.id.rvTimers);
        final RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this,RecyclerView.HORIZONTAL,false);
        rvTimers.setLayoutManager(layoutManager);
        //if layout manager is null push to savepage


//        if (singleTimer.isEmpty()) {
//            rvTimers.setVisibility(View.GONE);
//            emptyView.setVisibility(View.VISIBLE);
//        }else {
//            rvTimers.setVisibility(View.VISIBLE);
//            emptyView.setVisibility(View.GONE);
//        }

        //Adapter
        mAdapter = new AdapterTimers(singleTimer,idUni,position,totalTime,this);
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


        //send to save and run from there
        //-------------

        //bug fix for null v

        rvTimers.post(new Runnable() {
            @Override
            public void run() {
                rvTimers.scrollToPosition(0);
                // Here adapter.getItemCount()== child count

            }
        });

        // SWIPE HERE TO SHOW CHART

        pieChart = (PieChart) findViewById(R.id.pieChart);
        pieChart.setNoDataText("Swipe here to show chart");
        Paint p = pieChart.getPaint(Chart.PAINT_INFO);
        p.setColor(ContextCompat.getColor(this,
                R.color.footerBlue));

        p.setTextSize(35);
//        p.setTypeface(...);

        //---- ---------- --------

        // SWIPE HERE TO SHOW CARDS

        if(singleTimer.isEmpty()){
            swipeToShowCardText.setVisibility(View.VISIBLE);
        }


        // card zoom in on scroll animation
        rvTimers.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                View v = snapHelper.findSnapView(layoutManager);

                swipeToShowCardText.setVisibility(View.INVISIBLE);

//                if(v==null){
////            finish();
////            startActivity(getIntent());
//
//                    System.out.println("boohoo");
//
////            Intent intent = new Intent(this, RunPageActivity.class);
////            intent.putExtra("id", idUni);
////            startActivity(intent);
//
//                    mAdapter.notifyDataSetChanged();
//
//                }

                if(!singleTimer.isEmpty() ) {

                    if (v != null) {


                        System.out.println(" v not null");
                        int pos = layoutManager.getPosition(v); // BUG ??
                        position = pos;
//                    System.out.println(position);

                        RecyclerView.ViewHolder viewHolder = rvTimers.findViewHolderForAdapterPosition(pos);
                        ScrollView rl1 = viewHolder.itemView.findViewById(R.id.rl1);

                        if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                            rl1.animate().setDuration(350).scaleX(0.8f).scaleY(0.8f).setInterpolator(new AccelerateInterpolator()).start();
                        } else {
                            rl1.animate().setDuration(350).scaleX(0.6f).scaleY(0.6f).setInterpolator(new AccelerateInterpolator()).start();
                        }
                    } else {
                        System.out.println("v is null");

                        //reset step, reset, save page --

                        //
//                        Intent intent;
//                        intent = new Intent(RunPageActivity.this, RunPageActivity.class);
//                        System.out.println(idUni);
//                        intent.putExtra("id", idUni);
////                            intent.putExtra("view",layoutManager)
//                        startActivity(intent);

                        mAdapter.notifyDataSetChanged();


                    }
                }


            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

            }


        });

        //end of recyclerview animation



    }

//    private void startTimer() {
//
//
//        countDownTimer = new CountDownTimer(timeLeft,1) { //1000
//            long timeBuildup=timesUni.get(0);
//
//
//            @Override
//            public void onTick(long millisUntilFinished) {
//                timeLeft = millisUntilFinished;
//                updateCountDownText();
//
//
//                //per second checkups -- get time array, get color array...
//
////                System.out.println("time left " +timeLeft);
////                System.out.println(totalTime-timeBuildup);
////                System.out.println(totalTime-(timeBuildup-300));
//
//
//                for(int i=0; i<timesUni.size();i++){
//                    if(timeLeft>=totalTime-timeBuildup && timeLeft<=totalTime-(timeBuildup-500) ){ //30
//                            counter++;
//                            System.out.println("Bing");
//
//                            if(counter>0){
//                                timeBuildup = timeBuildup+timesUni.get(i);
//                                System.out.println("Bing o");
//
//                                System.out.println("time left " +timeLeft);
//                                System.out.println(totalTime-timeBuildup);
//                                System.out.println(totalTime-(timeBuildup-500));
//                            }
//
//                    }
//
//                }
//
//            }
//
//            @Override
//            public void onFinish() {
//                System.out.println("Bing Bing");
//
//                timerRunning = false;
//                countdownButton.setText("Start");
////                resetMultiTimerButton.setVisibility((View.VISIBLE));
//                totalStepTime = 0;
//
//            }
//
//
//        }.start();
//
//        timerRunning = true;
//        countdownButton.setText("Pause");
////        resetMultiTimerButton.setVisibility(View.INVISIBLE);
//
//
//    }


//    private void startTimer(long timeLeftLong) {
//
//            timeLeft= timeLeftLong;
//
//            countDownTimer = new CountDownTimer(timeLeft,1000) { //1000
//
//                @Override
//                public void onTick(long millisUntilFinished) {
//
//                    timeLeft = millisUntilFinished;
//                    updateCountDownText(); //change this
//
////                  System.out.println("Bing");
//                }
//
//                @Override
//                public void onFinish() {
//                    counter++;
//
//                    if(counter<timesUni.size()){
//                        System.out.println("Bing");
//                        //bell, move recyclerview ,change ring color,change (step 1) text in ring
//                        // redo lower button placement
//
//                        startTimer(timesUni.get(counter)); // recursion
//
//                    }
//
//                    if(counter == timesUni.size()){
//                        System.out.println("Bing");
//
//                        //
//                        timerRunning = false;
//                        countdownButton.setText("Start");
////                        resetMultiTimerButton.setVisibility((View.VISIBLE));
//                    }
//
//
//                }
//
//
//            }.start();
//
//            timerRunning = true;
//            countdownButton.setText("Pause");
////            resetMultiTimerButton.setVisibility(View.INVISIBLE);
//
//
//    }

    private void startTimer(final long timeLeftLong) {

        timeLeft= timeLeftLong;

        timerProgress.setVisibility(View.VISIBLE);

        countDownTimer = new CountDownTimer(timeLeft,1000) { //1000

            @Override
            public void onTick(long millisUntilFinished) {

                timeLeft = millisUntilFinished;
                millis = millisUntilFinished;
                updateCountDownText(); //change this

//                  System.out.println("Bing");

                //progress circle
//                timerProgress.setProgress((int) ((100-(timeAtPaused+(timeLeft*100)/timeLeftLong)))); //100-80=20



                timerProgress.setProgress((int) ((100-((timeLeft*100)/timeLeftLong)))); //works


            }

            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onFinish() {

                counter++;

                timerProgress.setVisibility(View.INVISIBLE);

                if(counter<timesUni.size()){

                    // sounds
                    System.out.println("Bing");

                    System.out.println("current sound is: "+ currentSound);

                    if(currentSound.equals("Batman")){
                        batmanSound.start();
                    }else if(currentSound.equals("Chilled")){
                        chilledSound.start();
                    }else if(currentSound.equals("Soft")){
                        softSound.start();
                    }else if(currentSound.equals("Gear")){
                        gearSound.start();
                    }else if(currentSound.equals("Bell")){
                        bellSound.start();
                    }

                    //---


                    //bell, move recyclerview ,change ring color,change (step 1) text in ring
                    // redo lower button placement

                    //change ring color
                    colorSetter(counter);

                    //change step number in ring
                    ringStepNumber.setText("Step " + stepNumberUni.get(counter).toString());

                    //move recycler view
                    rvTimers.smoothScrollToPosition(Math.toIntExact(stepNumberUni.get(counter)-1));


                    //
                    startTimer(timesUni.get(counter)); // recursion to start next step

                }

                if(counter == timesUni.size()){
                    System.out.println("Bing Ping");
                    // sounds

                    System.out.println("current sound is: "+ currentSound);

                    if(currentSound.equals("Batman")){
                        batmanSound.start();
                    }else if(currentSound.equals("Chilled")){
                        chilledSound.start();
                    }else if(currentSound.equals("Soft")){
                        softSound.start();
                    }else if(currentSound.equals("Gear")){
                        gearSound.start();
                    }else if(currentSound.equals("Bell")){
                        bellSound.start();
                    }

                    //---

                    //
                    timerProgress.setProgress(100);

                    //
                    timerRunning = false;
                    countdownButton.setText("Done");
                    countdownButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            System.out.println("Back to homepage");
                        }
                    });

                    //stuff that happens after multitimer completed
                    resetStepButton.setVisibility(View.INVISIBLE);
                    skipButton.setVisibility(View.INVISIBLE);
                    Toast.makeText(RunPageActivity.this,"Activity completed",Toast.LENGTH_LONG).show();

                    openCustomDialog(); // you need to dispose of this

                    //

                    //increase card height programatically?? or a popup dialog??

                    //---
//                        resetMultiTimerButton.setVisibility((View.VISIBLE));
                }


            }


        }.start();

        timerRunning = true;
        countdownButton.setText("Pause");
//            resetMultiTimerButton.setVisibility(View.INVISIBLE);


    }

    private void updateCountDownText(){
        // timeLeft is in miliseconds

        long seconds = timeLeft / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;


        String timeLeftFormatted = String.format(Locale.getDefault(),"%02d:%02d:%02d",hours % 24,minutes % 60,seconds % 60);


        countdownText.setText(timeLeftFormatted); //display


    }

    private void pauseTimer(){

        timerProgress.setVisibility(View.INVISIBLE);


        countDownTimer.cancel();
        timerRunning = false;
        countdownButton.setText("Start");
//        resetbtn.setVisibility(View.VISIBLE);


    }

//    private void updateprogressBar(){
//        timerProgress.setProgress(timeLeft);
//    }

//    private void resetTimer(){
//        countDownTimer.cancel();
//        timerRunning = false;
//        countdownButton.setText("Start");
////        resetbtn.setVisibility(View.VISIBLE);
//
//
//    }


    private void pieChartInitiater(ArrayList<Long> retrivedTimes,ArrayList<String> retrivedTitles,ArrayList<Long> retrivedColors) {

        pieChart = (PieChart) findViewById(R.id.pieChart);


        pieChart.setEntryLabelColor(Color.BLACK);
        pieChart.getLegend().setEnabled(false);   // Hide the legend

        pieChart.getDescription().setEnabled(false);

        pieChart.setExtraOffsets(5,10,5,5);

        pieChart.setDragDecelerationFrictionCoef(0.95f);

        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColor(Color.WHITE);
        pieChart.setTransparentCircleRadius(61f);

        ArrayList<PieEntry> yValues = new ArrayList<>();

        for(int i=0;i<retrivedTimes.size();i++){

            yValues.add(new PieEntry(retrivedTimes.get(i),retrivedTitles.get(i)));
        }

        PieDataSet dataSet = new PieDataSet(yValues,"");
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);

        //pie chart colors

        ArrayList<Integer> arrayListColors;
        int arrayColors[]= new int[retrivedColors.size()];

        for(int i=0;i<retrivedTimes.size();i++){
            arrayColors[i] = retrivedColors.get(i).intValue();
        }

        System.out.println(Arrays.toString(arrayColors));

//        dataSet.setColors(new int[] {2131034167, R.color.cardColorCreamH, R.color.cardColorGrapeL, R.color.cardColor1 }, this);

        System.out.println("this is the arrayColors.length : "+ arrayColors.length);

        if((arrayColors.length==singleTimer.size())){

            if(singleTimer.size()==1){
                dataSet.setColors(new int[] {arrayColors[0]}, this);
            }

            else if(singleTimer.size()==2){
                dataSet.setColors(new int[] {arrayColors[0],arrayColors[1]}, this);
            }else if(singleTimer.size()==3){
                dataSet.setColors(new int[] {arrayColors[0],arrayColors[1],arrayColors[2]}, this);
            }else if(singleTimer.size()==4){
                dataSet.setColors(new int[] {arrayColors[0],arrayColors[1],arrayColors[2],arrayColors[3]}, this);
            }else if(singleTimer.size()==5){
                dataSet.setColors(new int[] {arrayColors[0],arrayColors[1],arrayColors[2],arrayColors[3],arrayColors[4]}, this);
            }else if(singleTimer.size()==6){
                dataSet.setColors(new int[] {arrayColors[0],arrayColors[1],arrayColors[2],arrayColors[3],
                        arrayColors[4],arrayColors[5]}, this);
            }else if(singleTimer.size()==7){
                dataSet.setColors(new int[] {arrayColors[0],arrayColors[1],arrayColors[2],arrayColors[3],
                        arrayColors[4],arrayColors[5],arrayColors[6]}, this);
            }else if(singleTimer.size()==8){
                dataSet.setColors(new int[] {arrayColors[0],arrayColors[1],arrayColors[2],arrayColors[3],
                        arrayColors[4],arrayColors[5],arrayColors[6],arrayColors[7]}, this);
            }else if(singleTimer.size()==9){
                dataSet.setColors(new int[] {arrayColors[0],arrayColors[1],arrayColors[2],arrayColors[3],
                        arrayColors[4],arrayColors[5],arrayColors[6],arrayColors[7],arrayColors[8]}, this);
            }else if(singleTimer.size()==10){
                dataSet.setColors(new int[] {arrayColors[0],arrayColors[1],arrayColors[2],arrayColors[3],
                        arrayColors[4],arrayColors[5],arrayColors[6],arrayColors[7],arrayColors[8],arrayColors[9]}, this);
            }else if(singleTimer.size()==11){
                dataSet.setColors(new int[] {arrayColors[0],arrayColors[1],arrayColors[2],arrayColors[3],
                        arrayColors[4],arrayColors[5],arrayColors[6],arrayColors[7],arrayColors[8],arrayColors[9]
                        ,arrayColors[10]}, this);
            }else if(singleTimer.size()==12){
                dataSet.setColors(new int[] {arrayColors[0],arrayColors[1],arrayColors[2],arrayColors[3],
                        arrayColors[4],arrayColors[5],arrayColors[6],arrayColors[7],arrayColors[8],arrayColors[9]
                        ,arrayColors[10],arrayColors[11]}, this);
            }else if(singleTimer.size()==13){
                dataSet.setColors(new int[] {arrayColors[0],arrayColors[1],arrayColors[2],arrayColors[3],
                        arrayColors[4],arrayColors[5],arrayColors[6],arrayColors[7],arrayColors[8],arrayColors[9]
                        ,arrayColors[10],arrayColors[11],arrayColors[12]}, this);
            }else if(singleTimer.size()==14){
                dataSet.setColors(new int[] {arrayColors[0],arrayColors[1],arrayColors[2],arrayColors[3],
                        arrayColors[4],arrayColors[5],arrayColors[6],arrayColors[7],arrayColors[8],arrayColors[9]
                        ,arrayColors[10],arrayColors[11],arrayColors[12],arrayColors[13]}, this);
            }else if(singleTimer.size()==15){
                dataSet.setColors(new int[] {arrayColors[0],arrayColors[1],arrayColors[2],arrayColors[3],
                        arrayColors[4],arrayColors[5],arrayColors[6],arrayColors[7],arrayColors[8],arrayColors[9]
                        ,arrayColors[10],arrayColors[11],arrayColors[12],arrayColors[13],arrayColors[14]}, this);
            }else if(singleTimer.size()==16){
                dataSet.setColors(new int[] {arrayColors[0],arrayColors[1],arrayColors[2],arrayColors[3],
                        arrayColors[4],arrayColors[5],arrayColors[6],arrayColors[7],arrayColors[8],arrayColors[9]
                        ,arrayColors[10],arrayColors[11],arrayColors[12],arrayColors[13],arrayColors[14],arrayColors[15]}, this);
            }else if(singleTimer.size()==17){
                dataSet.setColors(new int[] {arrayColors[0],arrayColors[1],arrayColors[2],arrayColors[3],
                        arrayColors[4],arrayColors[5],arrayColors[6],arrayColors[7],arrayColors[8],arrayColors[9]
                        ,arrayColors[10],arrayColors[11],arrayColors[12],arrayColors[13],arrayColors[14],arrayColors[15]
                        ,arrayColors[16]}, this);
            }else if(singleTimer.size()==18){
                dataSet.setColors(new int[] {arrayColors[0],arrayColors[1],arrayColors[2],arrayColors[3],
                        arrayColors[4],arrayColors[5],arrayColors[6],arrayColors[7],arrayColors[8],arrayColors[9]
                        ,arrayColors[10],arrayColors[11],arrayColors[12],arrayColors[13],arrayColors[14],arrayColors[15]
                        ,arrayColors[16],arrayColors[17]}, this);
            }else if(singleTimer.size()==19){
                dataSet.setColors(new int[] {arrayColors[0],arrayColors[1],arrayColors[2],arrayColors[3],
                        arrayColors[4],arrayColors[5],arrayColors[6],arrayColors[7],arrayColors[8],arrayColors[9]
                        ,arrayColors[10],arrayColors[11],arrayColors[12],arrayColors[13],arrayColors[14],arrayColors[15]
                        ,arrayColors[16],arrayColors[17],arrayColors[18]}, this);
            }else if(singleTimer.size()==20){
                dataSet.setColors(new int[] {arrayColors[0],arrayColors[1],arrayColors[2],arrayColors[3],
                        arrayColors[4],arrayColors[5],arrayColors[6],arrayColors[7],arrayColors[8],arrayColors[9]
                        ,arrayColors[10],arrayColors[11],arrayColors[12],arrayColors[13],arrayColors[14],arrayColors[15]
                        ,arrayColors[16],arrayColors[17],arrayColors[18],arrayColors[19]}, this);
            }else if(singleTimer.size()==21){
                dataSet.setColors(new int[] {arrayColors[0],arrayColors[1],arrayColors[2],arrayColors[3],
                        arrayColors[4],arrayColors[5],arrayColors[6],arrayColors[7],arrayColors[8],arrayColors[9]
                        ,arrayColors[10],arrayColors[11],arrayColors[12],arrayColors[13],arrayColors[14],arrayColors[15]
                        ,arrayColors[16],arrayColors[17],arrayColors[18],arrayColors[19],arrayColors[20]}, this);
            }else if(singleTimer.size()==22){
                dataSet.setColors(new int[] {arrayColors[0],arrayColors[1],arrayColors[2],arrayColors[3],
                        arrayColors[4],arrayColors[5],arrayColors[6],arrayColors[7],arrayColors[8],arrayColors[9]
                        ,arrayColors[10],arrayColors[11],arrayColors[12],arrayColors[13],arrayColors[14],arrayColors[15]
                        ,arrayColors[16],arrayColors[17],arrayColors[18],arrayColors[19],arrayColors[20],
                        arrayColors[21]}, this);
            }else if(singleTimer.size()==23){
                dataSet.setColors(new int[] {arrayColors[0],arrayColors[1],arrayColors[2],arrayColors[3],
                        arrayColors[4],arrayColors[5],arrayColors[6],arrayColors[7],arrayColors[8],arrayColors[9]
                        ,arrayColors[10],arrayColors[11],arrayColors[12],arrayColors[13],arrayColors[14],arrayColors[15]
                        ,arrayColors[16],arrayColors[17],arrayColors[18],arrayColors[19],arrayColors[20]
                        ,arrayColors[21],arrayColors[22]}, this);
            }else if(singleTimer.size()==24){
                dataSet.setColors(new int[] {arrayColors[0],arrayColors[1],arrayColors[2],arrayColors[3],
                        arrayColors[4],arrayColors[5],arrayColors[6],arrayColors[7],arrayColors[8],arrayColors[9]
                        ,arrayColors[10],arrayColors[11],arrayColors[12],arrayColors[13],arrayColors[14],arrayColors[15]
                        ,arrayColors[16],arrayColors[17],arrayColors[18],arrayColors[19],arrayColors[20]
                        ,arrayColors[21],arrayColors[22],arrayColors[23]}, this);
            }else if(singleTimer.size()==25){
                dataSet.setColors(new int[] {arrayColors[0],arrayColors[1],arrayColors[2],arrayColors[3],
                        arrayColors[4],arrayColors[5],arrayColors[6],arrayColors[7],arrayColors[8],arrayColors[9]
                        ,arrayColors[10],arrayColors[11],arrayColors[12],arrayColors[13],arrayColors[14],arrayColors[15]
                        ,arrayColors[16],arrayColors[17],arrayColors[18],arrayColors[19],arrayColors[20]
                        ,arrayColors[21],arrayColors[22],arrayColors[23],arrayColors[24]}, this);
            }else if(singleTimer.size()==26){
                dataSet.setColors(new int[] {arrayColors[0],arrayColors[1],arrayColors[2],arrayColors[3],
                        arrayColors[4],arrayColors[5],arrayColors[6],arrayColors[7],arrayColors[8],arrayColors[9]
                        ,arrayColors[10],arrayColors[11],arrayColors[12],arrayColors[13],arrayColors[14],arrayColors[15]
                        ,arrayColors[16],arrayColors[17],arrayColors[18],arrayColors[19],arrayColors[20]
                        ,arrayColors[21],arrayColors[22],arrayColors[23],arrayColors[24],arrayColors[25]}, this);
            }else if(singleTimer.size()==27){
                dataSet.setColors(new int[] {arrayColors[0],arrayColors[1],arrayColors[2],arrayColors[3],
                        arrayColors[4],arrayColors[5],arrayColors[6],arrayColors[7],arrayColors[8],arrayColors[9]
                        ,arrayColors[10],arrayColors[11],arrayColors[12],arrayColors[13],arrayColors[14],arrayColors[15]
                        ,arrayColors[16],arrayColors[17],arrayColors[18],arrayColors[19],arrayColors[20]
                        ,arrayColors[21],arrayColors[22],arrayColors[23],arrayColors[24],arrayColors[25]
                        ,arrayColors[26]}, this);
            }else if(singleTimer.size()==28){
                dataSet.setColors(new int[] {arrayColors[0],arrayColors[1],arrayColors[2],arrayColors[3],
                        arrayColors[4],arrayColors[5],arrayColors[6],arrayColors[7],arrayColors[8],arrayColors[9]
                        ,arrayColors[10],arrayColors[11],arrayColors[12],arrayColors[13],arrayColors[14],arrayColors[15]
                        ,arrayColors[16],arrayColors[17],arrayColors[18],arrayColors[19],arrayColors[20]
                        ,arrayColors[21],arrayColors[22],arrayColors[23],arrayColors[24],arrayColors[25]
                        ,arrayColors[26],arrayColors[27]}, this);
            }else if(singleTimer.size()==29){
                dataSet.setColors(new int[] {arrayColors[0],arrayColors[1],arrayColors[2],arrayColors[3],
                        arrayColors[4],arrayColors[5],arrayColors[6],arrayColors[7],arrayColors[8],arrayColors[9]
                        ,arrayColors[10],arrayColors[11],arrayColors[12],arrayColors[13],arrayColors[14],arrayColors[15]
                        ,arrayColors[16],arrayColors[17],arrayColors[18],arrayColors[19],arrayColors[20]
                        ,arrayColors[21],arrayColors[22],arrayColors[23],arrayColors[24],arrayColors[25]
                        ,arrayColors[26],arrayColors[27],arrayColors[28]}, this);
            }else if(singleTimer.size()==30){
                dataSet.setColors(new int[] {arrayColors[0],arrayColors[1],arrayColors[2],arrayColors[3],
                        arrayColors[4],arrayColors[5],arrayColors[6],arrayColors[7],arrayColors[8],arrayColors[9]
                        ,arrayColors[10],arrayColors[11],arrayColors[12],arrayColors[13],arrayColors[14],arrayColors[15]
                        ,arrayColors[16],arrayColors[17],arrayColors[18],arrayColors[19],arrayColors[20]
                        ,arrayColors[21],arrayColors[22],arrayColors[23],arrayColors[24],arrayColors[25]
                        ,arrayColors[26],arrayColors[27],arrayColors[28],arrayColors[29]}, this);
            }else if(singleTimer.size()==31){
                dataSet.setColors(new int[] {arrayColors[0],arrayColors[1],arrayColors[2],arrayColors[3],
                        arrayColors[4],arrayColors[5],arrayColors[6],arrayColors[7],arrayColors[8],arrayColors[9]
                        ,arrayColors[10],arrayColors[11],arrayColors[12],arrayColors[13],arrayColors[14],arrayColors[15]
                        ,arrayColors[16],arrayColors[17],arrayColors[18],arrayColors[19],arrayColors[20]
                        ,arrayColors[21],arrayColors[22],arrayColors[23],arrayColors[24],arrayColors[25]
                        ,arrayColors[26],arrayColors[27],arrayColors[28],arrayColors[29],arrayColors[30]}, this);
            }else if(singleTimer.size()==32){
                dataSet.setColors(new int[] {arrayColors[0],arrayColors[1],arrayColors[2],arrayColors[3],
                        arrayColors[4],arrayColors[5],arrayColors[6],arrayColors[7],arrayColors[8],arrayColors[9]
                        ,arrayColors[10],arrayColors[11],arrayColors[12],arrayColors[13],arrayColors[14],arrayColors[15]
                        ,arrayColors[16],arrayColors[17],arrayColors[18],arrayColors[19],arrayColors[20]
                        ,arrayColors[21],arrayColors[22],arrayColors[23],arrayColors[24],arrayColors[25]
                        ,arrayColors[26],arrayColors[27],arrayColors[28],arrayColors[29],arrayColors[30],arrayColors[31]}, this);
            }

        }


//        dataSet.setColors(ColorTemplate.VORDIPLOM_COLORS);

        //maybe just go with monocolor or a theme


        //----------------------


        PieData data = new PieData(dataSet);
        data.setValueTextSize(10f);
        data.setValueTextColor(Color.BLACK);

        //show percent sign
        data.setValueFormatter(new PercentFormatter(pieChart));
        pieChart.setUsePercentValues(true);
        //----

        pieChart.setData(data);
        pieChart.setRotation(position);
        pieChart.setRotationX(10);

    }

    private void openCustomDialog(){
        customDialog.setContentView(R.layout.custom_dialog_finish);
        customDialog.getWindow().setBackgroundDrawable(new ColorDrawable((Color.TRANSPARENT)));

        Button btnReset = customDialog.findViewById(R.id.btReset);
        Button btnDone = customDialog.findViewById(R.id.btDone);

        if(!isFinishing()){
            customDialog.show();
        }


        customDialog.setCanceledOnTouchOutside(false);

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), RunPageActivity.class);
                intent.putExtra("id", idUni);
                startActivity(intent);

            }
        });

        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("Go to dashboard");

                Intent intent = new Intent(getApplicationContext(), LoggedInTotalDashboardActivity.class);
                startActivity(intent);


                // ?
                onBackPressed();


            }
        });

    }

    //take care of back press on dashboard page ---


    @Override
    public void onBackPressed() {

        System.out.println("runpageactivity bacpressed in runpageactivity");


            //destroy multitimer temporary on back button press

            //destroy from multitimers if singleTimerArrayList is not equal in multitimer arraylist

        if(backPressCount==0){
            DatabaseReference referenceBackPress = FirebaseDatabase.getInstance().getReference().child("Users").child(onlineUserID);


            referenceBackPress.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.getValue()!=null){

                        if(dataSnapshot.hasChild("multitimer arraylist")){

                            if(indexUni!=null){

                                referenceMultitimerArraylistBackPress = FirebaseDatabase.getInstance().getReference().child("Users").child(onlineUserID).child("multitimer arraylist").child(indexUni);
                                referenceMultitimersBackPress = FirebaseDatabase.getInstance().getReference().child("Users").child(onlineUserID).child("multitimers").child(idUni);
                                 referenceMultitimersTempBackPress = FirebaseDatabase.getInstance().getReference().child("Users").child(onlineUserID).child("multitimers temporary").child(idUni);


                                referenceMultitimerArraylistBackPress.child("singleTimerArrayList").addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull final DataSnapshot dataSnapshot2) {
                                        if(dataSnapshot2.getValue()!=null){

                                            System.out.println("Size 1:   " + dataSnapshot2.getValue().toString().length());

                                            one = dataSnapshot2.getValue().toString().length();


                                            //


                                            referenceMultitimersBackPress.child("singleTimerArrayList").addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                    if(dataSnapshot.getValue()!=null){

                                                        System.out.println("Size 2:   " + dataSnapshot.getValue().toString().length());

                                                        two = dataSnapshot.getValue().toString().length();

                                                        //

                                                        if(one==two){
                                                            System.out.println("keep entry in multitimers");
                                                            System.out.println("EQUAL in RunPageActivity ");



                                                        }else{

                                                            System.out.println("replace entry in multitimers with the one from multitimer arraylist");
                                                            System.out.println("NOT EQUAL in RunPageActivity");

                                                            //also delete multitimer temporary
                                                            referenceMultitimersTempBackPress.setValue(null);



                                                            if(backPressCount==0){
                                                                referenceMultitimersBackPress.child("singleTimerArrayList").setValue(dataSnapshot2.getValue());


                                                                backPressCount=1;

                                                            }

//                                                            referenceMultitimersBackPress.removeEventListener(this);

                                                            //maybe also delete multimers temporary


                                                        }

                                                    }

                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                }
                                            });


                                        }

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });



                            }



                        }else{
                            two =0;
                        }



                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            //-------------------

            //when backpressed and timer not loaded by freshly made  -- BUGGY


//            referenceMultitimersBackPress = FirebaseDatabase.getInstance().getReference().child("Users").child(onlineUserID).child("multitimers").child(idUni);
//
//
//            if(referenceMultitimerArraylistBackPress!=null){
//                referenceMultitimerArraylistBackPress.child("id").addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                        if(!dataSnapshot.getValue().equals(idUni)){
//                            System.out.println(idUni);
//                            System.out.println(dataSnapshot.getValue());
//                            System.out.println("hiiiiaty");
//
//
//                            FirebaseDatabase.getInstance().getReference().child("Users").child(onlineUserID).child("multitimers").child(idUni).setValue(null);
//                        }
//
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                    }
//                });
//
//            }else{
//                System.out.println("referenceMultitimerArraylistBackPress.child(id)==null");
//
//                System.out.println("no hiiiiaty");
//                FirebaseDatabase.getInstance().getReference().child("Users").child(onlineUserID).child("multitimers").child(idUni).setValue(null);
//
//            }

            if(frombuildscreenactivity==true){
                FirebaseDatabase.getInstance().getReference().child("Users").child(onlineUserID).child("multitimers").child(idUni).setValue(null);

            }


            //-------


            Toast.makeText(RunPageActivity.this,"Multi-timer ended",Toast.LENGTH_LONG).show();
            if(countDownTimer!=null){
                countDownTimer.cancel();
                timerRunning = false;

                finish();

            }


            Intent intent = new Intent(getApplicationContext(), LoggedInTotalDashboardActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            startActivity(intent);

            // destroy activity on back button press ?

            finish();

            //

        }



    }





}

