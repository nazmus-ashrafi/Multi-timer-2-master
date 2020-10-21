package com.nazmusashrafi.multi_timer_2_master;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.Adapter;
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
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class DeleteActivity extends AppCompatActivity {

    RecyclerView rvTimers;
    AdapterTimers mAdapter;
    public static final String TIMERARRAYLIST = "com.nazmus.multi_timer2.TIMERARRAYLIST";

    MultiTimer multiTimer = new MultiTimer();

    //Firebase variables
    private DatabaseReference reference;
    private DatabaseReference referenceMultiTimer;
    private DatabaseReference referenceMultiTimerAddAtEnd;
    private DatabaseReference referenceTemporaryMultiTimer;

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private String onlineUserID;
    //-----

    long totalTime;
    int stepNumber = 0;
    int position;
    long displayTimeAtTop;
    int counter=0;

    ArrayList<SingleTimer> singleTimer = new ArrayList<>();
    ArrayList<MultiTimer> multiTimerArrayListToBeSaved = new ArrayList<>();
    Context context;
    String id;
    String index;
    String idGotten;

    long t2Hour,t2Minute;
//    private TextView tvTimerView; //display

    private String m_Text = "";

    int editCounter=0;
    boolean multitimersTemporaryPresent = false;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete);


        //Buttons and text view declaration
        Button addTimer = (Button) findViewById(R.id.btReset);
        Button startBtn = findViewById(R.id.btnstarttimer);
        TextView emptyView = (TextView) findViewById(R.id.empty_view);
        Button saveBtn = findViewById(R.id.btSave);


        //Firebase declarations
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        onlineUserID = mUser.getUid();

        //REFERENCE---------
        reference = FirebaseDatabase.getInstance().getReference().child("Users").child(onlineUserID).child("multitimers");


        //getting ifo from parentRecyclerAdapter
        ArrayList<SingleTimer> singletimerarraylist =(ArrayList<SingleTimer>) getIntent().getSerializableExtra("SINGLETIMER_ARRAY");
        id =getIntent().getStringExtra("MULTITIMER_ID");
        index =getIntent().getStringExtra("MULTITIMER_POSITION");

        idGotten = id;


        System.out.println(index);

        referenceMultiTimer = FirebaseDatabase.getInstance().getReference().child("Users").child(onlineUserID).child("multitimer arraylist");



        //-----

//        //making a backup reference
//        //REFERENCE---------
//        referenceMultiTimer = FirebaseDatabase.getInstance().getReference().child("Users").child(onlineUserID).child("multitimers arraylist").child(index);
//
//        //----------
//
//        if(singletimerarraylist!=null){
//            System.out.println("bagga " + singletimerarraylist.get(0).getTitle());
//
//            singleTimer.addAll(singletimerarraylist);
//        }

        //recycler view animation
        recyclerViewAnimation();


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

//        //end of recyclerview animation
//
        // remove button functionality

        Button deleteButton = (Button) findViewById(R.id.deleteBtn);

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                referenceMultiTimer.child(index).setValue(null);

                Intent intent;
                intent = new Intent(getApplicationContext(), LoggedInTotalDashboardActivity.class);

               startActivity(intent);
            }
        });
//
//        // update button functionality
//
//        Button editButton = (Button) findViewById(R.id.editBtn);
//
//        editButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                //open alertdialog populated with previous values
//                editTask(position);
//
//                //push to db with updated values
//
//            }
//        });

    }
}