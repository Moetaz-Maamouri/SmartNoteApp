package com.example.smartnotev2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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

public class MainActivity extends AppCompatActivity {

    private EditText mloginemail,mloginpassword;
    private RelativeLayout mlogin,mgotosignup;
    private TextView mforgotpassword;

   private FirebaseAuth mAuth ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);


        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        mloginemail = findViewById(R.id.login_email);
        mloginpassword = findViewById(R.id.login_password);
        mlogin = findViewById(R.id.login);
        mgotosignup = findViewById(R.id.gotosignup);
        mforgotpassword = findViewById(R.id.gotoforgotpassword);
        mAuth = FirebaseAuth.getInstance();

        FirebaseUser firebaseUser = mAuth.getCurrentUser();

        if(firebaseUser!=null){
            finish();
            startActivity(new Intent(MainActivity.this, noteActivity.class));
        }


        mgotosignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Signup.class);
                startActivity(intent);
            }
        });

        mforgotpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, forgotpassword.class);
                startActivity(intent);
            }
        });

        mlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = mloginemail.getText().toString().trim();
                String password = mloginpassword.getText().toString().trim();

                if(email.isEmpty() || password.isEmpty()){
                    Toast.makeText(getApplicationContext(),"All Fiels Are Required",Toast.LENGTH_SHORT).show();
                }
                else {
                    mAuth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    checkemailverfivation();
                                }
                                else{
                                    Toast.makeText(getApplicationContext(),"Account Doesn't Exsit",Toast.LENGTH_SHORT).show();

                                }
                            });


                }
            }
        });


    }

    private void checkemailverfivation(){
        FirebaseUser firebaseUser=mAuth.getCurrentUser();
        if(firebaseUser.isEmailVerified()==true){
            Toast.makeText(getApplicationContext(),"Logged In", Toast.LENGTH_SHORT).show();
            finish();
            Intent intent = new Intent(MainActivity.this,noteActivity.class);
            startActivity(intent);

        }
            else{
                Toast.makeText(getApplicationContext(),"Verify your email first", Toast.LENGTH_SHORT).show();
                mAuth.signOut();
            }



    }



}