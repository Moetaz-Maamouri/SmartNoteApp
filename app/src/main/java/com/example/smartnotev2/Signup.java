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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class Signup extends AppCompatActivity {

    private EditText msignupemail, msignuppassword, msignupfullname;
    private RelativeLayout msignup;
    private TextView mgotologin;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signup);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        msignupemail    = findViewById(R.id.signup_email);
        msignuppassword = findViewById(R.id.signup_password);
        msignupfullname = findViewById(R.id.signup_fullname);
        msignup         = findViewById(R.id.signup);
        mgotologin      = findViewById(R.id.gotologin);
        mAuth           = FirebaseAuth.getInstance();

        mgotologin.setOnClickListener(v -> {
            startActivity(new Intent(Signup.this, MainActivity.class));
        });

        msignup.setOnClickListener(v -> {
            String email    = msignupemail.getText().toString().trim();
            String password = msignuppassword.getText().toString().trim();
            String fullname = msignupfullname.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty() || fullname.isEmpty()) {
                Toast.makeText(this, "All Fields Are Required", Toast.LENGTH_SHORT).show();
            } else if (password.length() <= 7) {
                Toast.makeText(this, "Password Should Be Greater Than 8 Characters", Toast.LENGTH_SHORT).show();
            } else {
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {

                                FirebaseUser firebaseUser = mAuth.getCurrentUser();

                                if (firebaseUser != null) {

                                    // ✅ Sauvegarde le nom dans Firebase Auth
                                    UserProfileChangeRequest profileUpdate =
                                            new UserProfileChangeRequest.Builder()
                                                    .setDisplayName(fullname)
                                                    .build();

                                    firebaseUser.updateProfile(profileUpdate)
                                            .addOnCompleteListener(profileTask -> {
                                                if (profileTask.isSuccessful()) {
                                                    Toast.makeText(this,
                                                            "Registration success",
                                                            Toast.LENGTH_SHORT).show();
                                                }
                                                // ✅ Envoie email de vérification
                                                sendEmailVerification();
                                            });
                                }

                            } else {
                                Toast.makeText(this,
                                        "Registration failed: " + task.getException().getMessage(),
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }

    private void sendEmailVerification() {
        FirebaseUser firebaseUser = mAuth.getCurrentUser();

        if (firebaseUser != null) {
            firebaseUser.sendEmailVerification()
                    .addOnCompleteListener(task -> {
                        Toast.makeText(this,
                                "Verification Email Sent, Verify and Log In Again",
                                Toast.LENGTH_SHORT).show();
                        mAuth.signOut();
                        finish();
                        startActivity(new Intent(Signup.this, MainActivity.class));
                    });
        } else {
            Toast.makeText(this,
                    "Failed To Send Verification Email",
                    Toast.LENGTH_SHORT).show();
        }
    }
}