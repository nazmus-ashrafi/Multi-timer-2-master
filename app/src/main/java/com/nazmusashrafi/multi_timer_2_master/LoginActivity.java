package com.nazmusashrafi.multi_timer_2_master;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private EditText loginEmail, loginPwd;
    private Button loginBtn;
    private TextView loginQn;
    private TextView loginSkip;

    private FirebaseAuth mAuth;
    private ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginEmail = findViewById(R.id.editTextTextEmailAddress);
        loginPwd = findViewById(R.id.editTextTextPassword);
        loginBtn = (Button) findViewById(R.id.buttonLogin);
        loginQn = findViewById(R.id.textViewCreateNewAccount);
        loginSkip = findViewById(R.id.textViewSkip);
        progressBar = findViewById(R.id.progressBarLogin);
        mAuth = FirebaseAuth.getInstance();


        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userLogin();

            }
        });

        loginSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this,LoggedInTotalDashboardActivity.class);
                startActivity(intent);
            }
        });


        loginQn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this,RegistrationActivity.class);
                startActivity(intent);
            }
        });

        progressBar.setVisibility(View.GONE);

    }

    private void userLogin(){

        final String email = loginEmail.getText().toString().trim();
        final String password = loginPwd.getText().toString().trim();

        if(TextUtils.isEmpty(email)){
            loginEmail.setError("Email is required");
            loginEmail.requestFocus();
            return;
        }

        else if(TextUtils.isEmpty(password)){
            loginPwd.setError("Password is required");
            loginPwd.requestFocus();
            return;
        }

        else if (TextUtils.isEmpty(email) && TextUtils.isEmpty(password) ){
            loginEmail.setError("Email is required");
            loginPwd.setError("Password is required");
            loginEmail.requestFocus();

        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            loginEmail.setError("Please provide valid email");
            loginEmail.requestFocus();
            return;

        }

        if(password.length()<6){
            loginPwd.setError("Password should be atleast 6 characters long");
            loginPwd.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    //verify email
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();


                    if(user.isEmailVerified()){
                        //redirect to user profile
                        startActivity(new Intent(LoginActivity.this, LoggedInTotalDashboardActivity.class));

                    }else{
                        user.sendEmailVerification();
                        Toast.makeText(LoginActivity.this,"Check your email to verify your account",Toast.LENGTH_LONG).show();

                    }






                }else{
                    Toast.makeText(LoginActivity.this,"Failed to login! Please check your credentials",Toast.LENGTH_LONG).show();
                }
            }
        });

    }


}