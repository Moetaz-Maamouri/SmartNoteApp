package com.example.smartnotev2;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileActivity extends AppCompatActivity {

    TextView tvName, tvEmail, tvNoteCount;
    MaterialButton btnLogout;
    BottomNavigationView bottomNavigationView;

    FirebaseAuth mAuth;
    FirebaseUser firebaseUser;
    FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);

        tvName               = findViewById(R.id.tvName);
        tvEmail              = findViewById(R.id.tvEmail);
        tvNoteCount          = findViewById(R.id.tvNoteCount);
        btnLogout            = findViewById(R.id.btnLogout);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        mAuth             = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseUser      = mAuth.getCurrentUser();

        if (firebaseUser != null) {

            // 👤 Nom
            String name = firebaseUser.getDisplayName();
            tvName.setText(name != null && !name.isEmpty() ? name : "Utilisateur");

            // 📧 Email
            String email = firebaseUser.getEmail();
            tvEmail.setText(email != null ? email : "");

            // 📊 Nombre de notes
            firebaseFirestore
                    .collection("notes")
                    .document(firebaseUser.getUid())
                    .collection("mynotes")
                    .get()
                    .addOnSuccessListener(snapshot ->
                            tvNoteCount.setText(String.valueOf(snapshot.size()))
                    )
                    .addOnFailureListener(e ->
                            tvNoteCount.setText("—")
                    );

        } else {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }

        // 🚪 Déconnexion
        btnLogout.setOnClickListener(v -> {
            mAuth.signOut();
            Toast.makeText(this, "Déconnecté", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        // 🧭 Bottom navbar
        bottomNavigationView.setSelectedItemId(R.id.nav_profile);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                startActivity(new Intent(this, noteActivity.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (id == R.id.nav_capture) {
                startActivity(new Intent(this, CaptureActivity.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (id == R.id.nav_create) {
                startActivity(new Intent(this, createnote.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (id == R.id.nav_profile) {
                return true;
            }
            return false;
        });
    }
}