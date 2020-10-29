package com.nazmusashrafi.multi_timer_2_master;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.ContextThemeWrapper;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;

public class LoggedInTotalDashboardActivity extends AppCompatActivity {

    //Firebase variables
    private DatabaseReference reference;
    private DatabaseReference referenceMultiTimer;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private String onlineUserID;
    //-----

    ChipNavigationBar chipNavigationBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logged_in_dashboard);

        chipNavigationBar = findViewById(R.id.bottom_nav_menu);
        chipNavigationBar.setItemSelected(R.id.bottom_nav_dashboard,true);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new LoggedInDashboardFragment()).commit();
        bottomMenu();

        //Firebase declarations
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        onlineUserID = mUser.getUid();


    }

    private void bottomMenu() {

        chipNavigationBar.setOnItemSelectedListener(new ChipNavigationBar.OnItemSelectedListener() {
            @Override
            public void onItemSelected(int i) {
                Fragment fragment = null;
                switch (i) {
                    case R.id.bottom_nav_dashboard:
                        fragment = new LoggedInDashboardFragment();
                        break;
                    case R.id.bottom_nav_save:
                        fragment = new LoggedInSavedTimersFragment();
                        break;
                    case R.id.bottom_nav_settings:
                        fragment = new LoggedInSettingsFragment();
                        break;
                }
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,fragment).commit();
            }
        });




    }

    @Override
    public void onBackPressed() {

        // TODO Auto-generated method stub


        new AlertDialog.Builder(LoggedInTotalDashboardActivity.this)
                .setTitle("Close app")
                .setMessage("Do you really want to exit?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        // sign out user and delete user data if uid is anym
                        if(mUser.isAnonymous()){

                            mAuth.signOut();

                            reference = FirebaseDatabase.getInstance().getReference().child("Users").child(onlineUserID);
                            reference.removeValue();
                        }

                        //---


                        ActivityCompat.finishAffinity(LoggedInTotalDashboardActivity.this);
                        finish();

                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
//                .setIcon(android.R.drawable.ic_dialog_alert)

        .setTitle( Html.fromHtml("<font color='#63c1e8'>Close app</font>"))
                .show();




    }
}