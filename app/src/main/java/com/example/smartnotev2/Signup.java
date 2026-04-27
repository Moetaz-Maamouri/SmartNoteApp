package com.example.smartnotev2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Signup extends AppCompatActivity {

    private EditText msignupemail,msignuppassword;
    private RelativeLayout msignup;
    private TextView mgotologin;
    private FirebaseAuth mAuth ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signup);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        msignupemail = findViewById(R.id.signup_email);
        msignuppassword = findViewById(R.id.signup_password);
        msignup = findViewById(R.id.signup);
        mgotologin = findViewById(R.id.gotologin);
        mAuth = FirebaseAuth.getInstance();

        mgotologin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Signup.this,MainActivity.class);
                startActivity(intent);
            }
        });

        msignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email=msignupemail.getText().toString().trim();
                String password=msignuppassword.getText().toString().trim();

                if(email.isEmpty() || password.isEmpty()){
                    Toast.makeText(getApplicationContext(),"All Fiels Are Required",Toast.LENGTH_SHORT).show();
                }
                else if(password.length()<=7){
                    Toast.makeText(getApplicationContext(),"Password Should Greater than 8 Characters",Toast.LENGTH_SHORT).show();
                }
                else {

                    mAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Toast.makeText(getApplicationContext(),"Registration success",Toast.LENGTH_SHORT).show();
                                    sendEmailVerification();
                                }
                                else{
                                    Toast.makeText(getApplicationContext(),"Registration failed",Toast.LENGTH_SHORT).show();
                                }
                            });

// Login


                }
            }
        });
    }

    private void sendEmailVerification(){
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        if(firebaseUser!=null){
            firebaseUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Toast.makeText(getApplicationContext(),"Verification Email is Sent, Verify and Log In Again",Toast.LENGTH_SHORT).show();
                    mAuth.signOut();
                    finish();
                    Intent intent=new Intent(Signup.this,MainActivity.class);
                    startActivity(intent);

                }
            });
        }

        else {
            Toast.makeText(getApplicationContext(),"Failed To Send Verification Email",Toast.LENGTH_SHORT).show();
        }

    }
}