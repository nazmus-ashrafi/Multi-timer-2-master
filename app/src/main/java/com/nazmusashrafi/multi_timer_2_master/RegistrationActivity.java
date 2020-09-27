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
import com.google.firebase.database.FirebaseDatabase;

public class RegistrationActivity extends AppCompatActivity {


    private EditText RegEmail, RegPwd;
    private Button RegBtn;
    private TextView RegQn;

    private FirebaseAuth mAuth;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_registration);

        mAuth = FirebaseAuth.getInstance();

        RegEmail = findViewById(R.id.editTextTextEmailAddress);
        RegPwd = findViewById(R.id.editTextTextPassword);
        RegBtn = findViewById(R.id.buttonRegister);
        RegQn = findViewById(R.id.textViewAlreadyHaveAnAccount);
        progressBar = findViewById(R.id.progressBarRegistration);

        progressBar.setVisibility(View.GONE);

        RegQn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegistrationActivity.this,LoginActivity.class);
                startActivity(intent);
            }
        });

        RegBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = RegEmail.getText().toString().trim();
                final String password = RegPwd.getText().toString().trim();

                if(TextUtils.isEmpty(email)){
                    RegEmail.setError("Email is required");
                    RegEmail.requestFocus();
                    return;
                }

                else if(TextUtils.isEmpty(password)){
                    RegPwd.setError("Password is required");
                    RegPwd.requestFocus();
                    return;
                }

                else if (TextUtils.isEmpty(email) && TextUtils.isEmpty(password) ){
                    RegEmail.setError("Email is required");
                    RegPwd.setError("Password is required");
                    RegEmail.requestFocus();

                }

                if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    RegEmail.setError("Please provide valid email");
                    RegEmail.requestFocus();
                    return;

                }

                if(password.length()<6){
                    RegPwd.setError("Password should be atleast 6 characters long");
                    RegPwd.requestFocus();
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);



                mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(task.isSuccessful()){
                            User user = new User(email,password);

                            FirebaseDatabase.getInstance().getReference("Users")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if(task.isSuccessful()){


                                        Toast.makeText(RegistrationActivity.this,"You have been registered successfully",Toast.LENGTH_LONG).show();
                                        progressBar.setVisibility(View.GONE);

                                        //redirect to login layout

                                    }else{

                                        Toast.makeText(RegistrationActivity.this,"Failed to register, try again",Toast.LENGTH_LONG).show();
                                        progressBar.setVisibility(View.GONE);

                                    }

                                }
                            });
                        }else{
                            try {
                                Toast.makeText(RegistrationActivity.this,"Failed to register, try again",Toast.LENGTH_LONG).show();
                                progressBar.setVisibility(View.GONE);

                            }catch (Exception e){
                                System.out.println("error");

                            }

                        }





                    }
                });





            }
        });



    }
}