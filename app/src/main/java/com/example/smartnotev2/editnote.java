package com.example.smartnotev2;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class editnote extends AppCompatActivity {

    Intent data;
    EditText medittitleofnote,meditcontentofnote;
    FloatingActionButton msveeditnote;
    FirebaseAuth mAuth ;
    FirebaseUser firebaseUser;
    FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_editnote);
        meditcontentofnote=findViewById(R.id.editcontentofnote);
        medittitleofnote=findViewById(R.id.edittitleofnote);
        msveeditnote=findViewById(R.id.saveeditnote);
        Toolbar toolbar=findViewById(R.id.toolbarofeditnote);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        data=getIntent();
        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore =FirebaseFirestore.getInstance();
        firebaseUser = mAuth.getCurrentUser();

        msveeditnote.setOnClickListener( v -> {
            //Toast.makeText(getApplicationContext(),"edit successfully",Toast.LENGTH_SHORT).show();
            String newtitle=medittitleofnote.getText().toString();
            String newcontent=meditcontentofnote.getText().toString();
            if(newtitle.isEmpty() || newcontent.isEmpty()){
                Toast.makeText(getApplicationContext(), "All field required", Toast.LENGTH_SHORT).show();
                return;
            }
            else{
                DocumentReference documentReference=firebaseFirestore.collection("notes").document(firebaseUser.getUid()).collection("mynotes").document(data.getStringExtra("noteId"));
                Map<String,Object> note=new HashMap<>();
                note.put("title",newtitle);
                note.put("content",newcontent);
                documentReference.set(note).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(getApplicationContext(), "Note is updated", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "Failed to update note", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });


        String notetitle=data.getStringExtra("title");
        String notecontent=data.getStringExtra("content");
        medittitleofnote.setText(notetitle);
        meditcontentofnote.setText(notecontent);
        String noteId=data.getStringExtra("noteId");

    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}