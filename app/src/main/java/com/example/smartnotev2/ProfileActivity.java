package com.example.smartnotev2;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);

        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigationView);
        bottomNav.setSelectedItemId(R.id.nav_capture); // change selon l'activity


        bottomNav.setOnItemSelectedListener(item -> {
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