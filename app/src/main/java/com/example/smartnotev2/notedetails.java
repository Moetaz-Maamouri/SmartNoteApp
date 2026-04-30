package com.example.smartnotev2;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class notedetails extends AppCompatActivity {


    private TextView mtitleofnotedeatil ,mcontentofnotedetail;
    FloatingActionButton mgotoeditnote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_notedetails);

        mtitleofnotedeatil=findViewById(R.id.titleofnoteofdetails);
        mcontentofnotedetail=findViewById(R.id.contentofnoteofdetails);
        mgotoeditnote=findViewById(R.id.gotoeditnote);
        Toolbar toolbar=findViewById(R.id.toolbarofnotedetails);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Note Details");


        Intent data =getIntent();
        mtitleofnotedeatil.setText(data.getStringExtra("title"));
        mcontentofnotedetail.setText(data.getStringExtra("content"));

        mgotoeditnote.setOnClickListener( v -> {

            Intent intent=new Intent(v.getContext(), editnote.class);
            intent.putExtra("title",data.getStringExtra("title"));
            intent.putExtra("content",data.getStringExtra("content"));
            intent.putExtra("noteId",data.getStringExtra("noteId"));
            v.getContext().startActivity(intent);

        });




    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}