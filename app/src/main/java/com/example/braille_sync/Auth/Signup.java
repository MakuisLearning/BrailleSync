package com.example.braille_sync.Auth;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.braille_sync.index;
import com.example.braille_sync.R;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


public class Signup extends AppCompatActivity {
    EditText username, password, email, confirmPassword;
    Button signupBtn;
    ProgressBar loaders;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signup);

        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        email = findViewById(R.id.email);
        signupBtn = findViewById(R.id.signupBtn);
        confirmPassword = findViewById(R.id.confirmPassword);
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        loaders = findViewById(R.id.loaders);


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        signupBtn.setOnClickListener(v -> {
            String email = Signup.this.email.getText().toString();
            String password = Signup.this.password.getText().toString();
            String username = Signup.this.username.getText().toString();
            String confirmPassword = Signup.this.confirmPassword.getText().toString();

            if (email.trim().isEmpty() || password.trim().isEmpty() || username.trim().isEmpty()) {
                Toast.makeText(Signup.this, "Please fill all the fields", Toast.LENGTH_SHORT).show();
                return;
            }
            if (password.length() < 8) {
                Toast.makeText(Signup.this, "Password must be at least 8 characters long", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!password.equals(confirmPassword)) {
                Toast.makeText(Signup.this, "Password does not match", Toast.LENGTH_SHORT).show();
                return;
            }

            loaders.setVisibility(View.VISIBLE);

            // Register user
            fAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                loaders.setVisibility(View.INVISIBLE);

                if (task.isSuccessful()) {
                    Objects.requireNonNull(fAuth.getCurrentUser()).sendEmailVerification().addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()) {
                            Toast.makeText(Signup.this, "Please verify your Email", Toast.LENGTH_SHORT).show();
                            String userID = fAuth.getCurrentUser().getUid();
                            DocumentReference db = fStore.collection("users").document(Objects.requireNonNull(userID));
                            Map<String, Object> user = new HashMap<>();
                            user.put("username", username);
                            user.put("email", email);
                            db.set(user).addOnSuccessListener(aVoid ->
                                            Log.d("TAG", "DocumentSnapshot successfully written!"));

                            Intent intent = new Intent(Signup.this, emailVerification.class);
                            intent.putExtra("email", email);
                            intent.putExtra("password", password);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(Signup.this, "Error: " + Objects.requireNonNull(task1.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(Signup.this, "Error: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }



    public void back(View v) {
        Intent intent = new Intent( this, index.class);
        startActivity(intent);
        finish();
    }


}