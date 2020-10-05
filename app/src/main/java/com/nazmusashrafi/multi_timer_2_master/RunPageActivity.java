package com.nazmusashrafi.multi_timer_2_master;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
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

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class RunPageActivity extends AppCompatActivity {


    //Firebase variables
    private DatabaseReference reference;
    private DatabaseReference referenceMultiTimer;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private String onlineUserID;
    //-----

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //Firebase declarations
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        onlineUserID = mUser.getUid();

        Bundle bundle = getIntent().getExtras();
        String id = bundle.getString("id");

        System.out.println(id);

        //REFERENCE---------
        reference = FirebaseDatabase.getInstance().getReference().child("Users").child(onlineUserID).child("multitimers").child(id);


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_run_page);

        recyclerViewAnimation();


//        referenceMultiTimer.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                if((ArrayList<SingleTimer>) dataSnapshot.child("singleTimerArrayList").getValue()!=null){
//                    ArrayList<SingleTimer> retrivedArray = (ArrayList<SingleTimer>) dataSnapshot.child("singleTimerArrayList").getValue();
//
//                                        System.out.println(retrivedArray.toString());
//                }
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
    }


    private void recyclerViewAnimation() {


    }
}