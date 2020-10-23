package com.nazmusashrafi.multi_timer_2_master;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity {

    EditText userEmail;
    Button resetPasswordButton;
    ProgressBar resetProgressBar;

    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        userEmail = findViewById(R.id.editTextTextEmailAddressReset);
        resetPasswordButton = findViewById(R.id.buttonResetPassword);
        resetProgressBar = findViewById(R.id.progressBarReset);

        //

        firebaseAuth = FirebaseAuth.getInstance();


        resetProgressBar.setVisibility(View.INVISIBLE);

        resetPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                resetProgressBar.setVisibility(View.VISIBLE);

                //
                firebaseAuth.sendPasswordResetEmail(userEmail.getText().toString().trim()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        resetProgressBar.setVisibility(View.GONE);
                        if(task.isSuccessful()){
                            //
                            Toast.makeText(ForgotPasswordActivity.this,"Password send to your email",Toast.LENGTH_LONG).show();

                            Intent intent = new Intent(ForgotPasswordActivity.this,LoginActivity.class);
                            startActivity(intent);
                        }else{

                            Toast.makeText(ForgotPasswordActivity.this,"You are not registered",Toast.LENGTH_LONG).show();
                            //task.getException().getMessage()

                            Intent intent = new Intent(ForgotPasswordActivity.this,RegistrationActivity.class);
                            startActivity(intent);

                        }
                    }
                });
            }
        });


    }
}