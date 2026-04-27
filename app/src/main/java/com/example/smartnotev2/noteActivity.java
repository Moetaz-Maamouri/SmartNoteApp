package com.example.smartnotev2;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import androidx.appcompat.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class noteActivity extends AppCompatActivity {

    FloatingActionButton mcreatenotesfab;
    private FirebaseAuth mAuth ;
    RecyclerView recyclerView;
    StaggeredGridLayoutManager staggeredGridLayoutManager;
    FirebaseUser firebaseUser;
    FirebaseFirestore firebaseFirestore;
    FirestoreRecyclerAdapter<FirebaseModel,NoteViewHolder> noteAdapter;
    Toolbar toolbar;
    FloatingActionButton scanFab ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_note);







        toolbar= findViewById(R.id.toolbar);
        mcreatenotesfab= findViewById(R.id.createnotefab);
        mAuth = FirebaseAuth.getInstance();
        firebaseUser =mAuth.getCurrentUser();
        firebaseFirestore =FirebaseFirestore.getInstance();
        scanFab = findViewById(R.id.scanFab);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("All Notes");

        mcreatenotesfab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(noteActivity.this, createnote.class);
                startActivity(intent);

            }
        });

        scanFab.setOnClickListener(v -> {
            Intent intent = new Intent(noteActivity.this, CaptureActivity.class);
            startActivity(intent);
        });


        Query query=firebaseFirestore.collection("notes").document(firebaseUser.getUid()).collection("mynotes").orderBy("title",Query.Direction.ASCENDING);
        FirestoreRecyclerOptions<FirebaseModel> allusernotes = new FirestoreRecyclerOptions.Builder<FirebaseModel>().setQuery(query,FirebaseModel.class).build();
        noteAdapter = new FirestoreRecyclerAdapter<FirebaseModel, NoteViewHolder>(allusernotes) {
            @Override
            protected void onBindViewHolder(@NonNull NoteViewHolder noteViewHolder, int i, @NonNull FirebaseModel firebaseModel) {

                ImageView popupbutton=noteViewHolder.itemView.findViewById(R.id.menupopbutton);

                int colorcode=getRandomColor();
                noteViewHolder.mnote.setBackgroundColor(noteViewHolder.itemView.getResources().getColor(colorcode,null));

                noteViewHolder.notetitle.setText(firebaseModel.getTitle());
                noteViewHolder.notecontent.setText(firebaseModel.getContent());


                String noteId=noteAdapter.getSnapshots().getSnapshot(i).getId();

                noteViewHolder.itemView.setOnClickListener(v ->{
                    Intent intent = new Intent(v.getContext(), notedetails.class);
                    intent.putExtra("title",firebaseModel.getTitle());
                    intent.putExtra("content",firebaseModel.getContent());
                    intent.putExtra("noteId",noteId);
                    v.getContext().startActivity(intent);
                    //Toast.makeText(noteActivity.this, "this is Clicked", Toast.LENGTH_SHORT).show();
                });

                popupbutton.setOnClickListener(v -> {
                    PopupMenu popupMenu = new PopupMenu(v.getContext(),v);
                    popupMenu.setGravity(Gravity.END);
                    popupMenu.getMenu().add("Edit").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(@NonNull MenuItem item) {

                            Intent intent=new Intent(v.getContext(),editnote.class);
                            intent.putExtra("title",firebaseModel.getTitle());
                            intent.putExtra("content",firebaseModel.getContent());
                            intent.putExtra("noteId",noteId);
                            v.getContext().startActivity(intent);
                            return false;
                        }
                    });

                    popupMenu.getMenu().add("Delete").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(@NonNull MenuItem item) {
                            DocumentReference documentReference =firebaseFirestore.collection("notes").document(firebaseUser.getUid()).collection("mynotes").document(noteId);
                            documentReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(v.getContext(),"This note is deleted",Toast.LENGTH_SHORT).show();

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(v.getContext(),"Failed to delete this note",Toast.LENGTH_SHORT).show();
                                }
                            });
                            return false;
                        }
                    });

                    popupMenu.show();

                });

            }

            @NonNull
            @Override
            public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notes_layout,parent,false);
                return new NoteViewHolder(view);
            }
        };


        recyclerView=findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(false);
        recyclerView.setItemAnimator(null);
        staggeredGridLayoutManager=new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(staggeredGridLayoutManager);
        recyclerView.setAdapter(noteAdapter);


    }


    public class NoteViewHolder extends  RecyclerView.ViewHolder{
        private TextView notetitle;
        private TextView notecontent;
        LinearLayout mnote;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            notetitle=itemView.findViewById(R.id.notetitle);
            notecontent=itemView.findViewById(R.id.notecontent);
            mnote=itemView.findViewById(R.id.note);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        MenuItem searchItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setQueryHint("Search by title...");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                loadNotes(query.trim());
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                loadNotes(newText.trim());
                return true;
            }
        });

        searchItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(@NonNull MenuItem item) { return true; }

            @Override
            public boolean onMenuItemActionCollapse(@NonNull MenuItem item) {
                loadNotes(""); // Reset to all notes when search is closed
                return true;
            }
        });
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.logout) {
            mAuth.signOut();

            Intent intent = new Intent(noteActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onStart() {
        super.onStart();
        noteAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(noteAdapter!=null){
            noteAdapter.stopListening();
        }
    }

    public int getRandomColor(){
        List<Integer> colorcode = new ArrayList<>();
        colorcode.add(R.color.note_blue);
        colorcode.add(R.color.note_orange);
        colorcode.add(R.color.note_purple);
        colorcode.add(R.color.note_pink);
        colorcode.add(R.color.note_green);
        colorcode.add(R.color.note_yellow);

        Random random=new Random();
        int number =random.nextInt(colorcode.size());

        return colorcode.get(number);
    }

    private void loadNotes(String searchText) {
        Query query;

        if (searchText == null || searchText.isEmpty()) {
            query = firebaseFirestore.collection("notes")
                    .document(firebaseUser.getUid())
                    .collection("mynotes")
                    .orderBy("title", Query.Direction.ASCENDING);
        } else {
            // Firestore prefix search: matches titles that START with searchText
            String end = searchText + "\uf8ff";
            query = firebaseFirestore.collection("notes")
                    .document(firebaseUser.getUid())
                    .collection("mynotes")
                    .orderBy("title")
                    .startAt(searchText)
                    .endAt(end);
        }

        FirestoreRecyclerOptions<FirebaseModel> options =
                new FirestoreRecyclerOptions.Builder<FirebaseModel>()
                        .setQuery(query, FirebaseModel.class)
                        .build();

        if (noteAdapter != null) {
            noteAdapter.stopListening();
        }

        noteAdapter.updateOptions(options);
        noteAdapter.startListening();
    }
}