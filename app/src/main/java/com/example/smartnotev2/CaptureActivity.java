package com.example.smartnotev2;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.HashMap;
import java.util.Map;


public class CaptureActivity extends AppCompatActivity {

    ImageView imagePreview;
    TextView textResult;
    FloatingActionButton btnCapture;
    Button saveBtn;
    FirebaseAuth mAuth;
    FirebaseUser firebaseUser;
    FirebaseFirestore firebaseFirestore;

    // 📸 Camera launcher
    private final ActivityResultLauncher<Intent> cameraLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Bundle extras = result.getData().getExtras();
                    Bitmap imageBitmap = (Bitmap) extras.get("data");
                    imagePreview.setImageBitmap(imageBitmap);
                    detectText(imageBitmap); // 🔥 OCR
                }
            });

    // 🔐 Permission launcher
    private final ActivityResultLauncher<String> permissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    openCamera();
                } else {
                    Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_capture);

        imagePreview = findViewById(R.id.imagePreview);
        textResult  = findViewById(R.id.textResult);
        btnCapture  = findViewById(R.id.btnCapture);
        saveBtn     = findViewById(R.id.btnSaveCapture);

        mAuth            = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseUser     = mAuth.getCurrentUser();

        btnCapture.setOnClickListener(v -> checkCameraPermission());

        // ✅ FIX 1 : listener défini UNE seule fois ici, pas dans detectText()
        saveBtn.setOnClickListener(v -> {
            String text = textResult.getText().toString().trim();
            if (text.isEmpty() || text.equals("No text found")) {
                Toast.makeText(this, "Aucun texte à sauvegarder", Toast.LENGTH_SHORT).show();
                return;
            }
            generateSummary(text); // lancement de l'appel API
        });
    }

    // 🔐 Check permission
    private void checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            openCamera();
        } else {
            permissionLauncher.launch(Manifest.permission.CAMERA);
        }
    }

    // 📷 Open camera
    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraLauncher.launch(intent);
    }

    // 🧠 OCR function
    private void detectText(Bitmap bitmap) {
        InputImage image = InputImage.fromBitmap(bitmap, 0);
        TextRecognizer recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);

        recognizer.process(image)
                .addOnSuccessListener(visionText -> {
                    String resultText = visionText.getText();
                    textResult.setText(resultText.isEmpty() ? "No text found" : resultText);
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "OCR Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
        // ✅ FIX 2 : saveBtn.setOnClickListener() retiré d'ici
    }

    // 🤖 Appel API + sauvegarde Firestore
    private void generateSummary(String text) {
        OpenAIApi api = RetrofitClient.getClient();
        OpenAIRequest request = new OpenAIRequest(text);

        String authHeader = "Bearer " + BuildConfig.GROQ_API_KEY;

        api.getSummary(authHeader, request).enqueue(new Callback<OpenAIResponse>() {
            @Override
            public void onResponse(Call<OpenAIResponse> call, Response<OpenAIResponse> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    try {
                        String err = response.errorBody() != null
                                ? response.errorBody().string() : "null";
                        Toast.makeText(CaptureActivity.this,
                                "Erreur " + response.code() + ": " + err,
                                Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Toast.makeText(CaptureActivity.this,
                                "Erreur " + response.code(),
                                Toast.LENGTH_LONG).show();
                    }
                    return;
                }

                String content = response.body().choices.get(0).message.content;

                String title   = "";
                String summary = "";

                if (content.contains("Title:") && content.contains("Summary:")) {
                    String[] parts = content.split("Summary:");
                    title   = parts[0].replace("Title:", "").trim();
                    summary = parts[1].trim();
                } else {
                    summary = content.trim();
                }

                textResult.setText("Title: " + title + "\n\nSummary: " + summary);

                if (firebaseUser != null) {
                    Map<String, Object> note = new HashMap<>();
                    note.put("title",     title);
                    note.put("content",   summary);
                    note.put("uid",       firebaseUser.getUid());
                    note.put("timestamp", System.currentTimeMillis());

                    firebaseFirestore.collection("notes")
                            .document(firebaseUser.getUid())   // 👈 user ID
                            .collection("mynotes")              // 👈 même que createnote
                            .document()
                            .set(note)
                            .addOnSuccessListener(doc ->
                                    Toast.makeText(CaptureActivity.this,
                                            "Note sauvegardée ✅", Toast.LENGTH_SHORT).show()
                            )
                            .addOnFailureListener(e ->
                                    Toast.makeText(CaptureActivity.this,
                                            "Erreur Firestore: " + e.getMessage(),
                                            Toast.LENGTH_SHORT).show()
                            );
                } else {
                    Toast.makeText(CaptureActivity.this,
                            "Utilisateur non connecté", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<OpenAIResponse> call, Throwable t) {
                Toast.makeText(CaptureActivity.this,
                        "Failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}