package com.nazmusashrafi.multi_timer_2_master;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.ismaeldivita.chipnavigation.ChipNavigationBar;

public class LoggedInTotalDashboardActivity extends AppCompatActivity {

    ChipNavigationBar chipNavigationBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logged_in_dashboard);

        chipNavigationBar = findViewById(R.id.bottom_nav_menu);
        chipNavigationBar.setItemSelected(R.id.bottom_nav_dashboard,true);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new LoggedInDashboardFragment()).commit();
        bottomMenu();


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
}