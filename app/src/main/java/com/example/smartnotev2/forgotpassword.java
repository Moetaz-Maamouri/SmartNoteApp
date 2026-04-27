package com.example.smartnotev2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class forgotpassword extends AppCompatActivity {
    private EditText mforgot_password;
    private Button mpasswordrecoverbutton;
    private TextView mgobacktologin;
    private FirebaseAuth mAuth ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forgotpassword);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        mforgot_password = findViewById(R.id.forgot_password);
        mpasswordrecoverbutton = findViewById(R.id.passwordrecoverbutton);
        mgobacktologin = findViewById(R.id.gobacktologin);
        mAuth = FirebaseAuth.getInstance();

        mgobacktologin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(forgotpassword.this,MainActivity.class);
                startActivity(intent);
            }
        });

        mpasswordrecoverbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mail = mforgot_password.getText().toString().trim();
                if(mail.isEmpty()){
                    Toast.makeText(getApplicationContext(),"Enter your email first",Toast.LENGTH_LONG).show();
                }
                else{
                    mAuth.sendPasswordResetEmail(mail).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(getApplicationContext(),"Email Sent, You can recover your password using email",Toast.LENGTH_SHORT).show();
                                finish();
                                Intent intent = new Intent(forgotpassword.this, MainActivity.class);
                                startActivity(intent);
                            }

                            else {
                                Toast.makeText(getApplicationContext(),"Email is Wrong or Account Not Exist",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }

            }
        });

    }
}