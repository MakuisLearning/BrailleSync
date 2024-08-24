package com.example.braille_sync.Auth;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.braille_sync.Main.Home;
import com.example.braille_sync.index;
import com.example.braille_sync.R;
import com.example.braille_sync.sample;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class Login extends AppCompatActivity {
    EditText email, password;
    Button login;
    TextView reset;
    ProgressBar loaders;
    FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        fAuth = FirebaseAuth.getInstance();
        login = findViewById(R.id.loginBtn);
        email = findViewById(R.id.email);
        reset = findViewById(R.id.resetBtn);
        password = findViewById(R.id.password);
        loaders = findViewById(R.id.process);
        String emailInput = getIntent().getStringExtra("email");
        String passwordInput = getIntent().getStringExtra("password");


        if (emailInput != null && passwordInput != null){
            email.setText(emailInput);
            password.setText(passwordInput);
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        login.setOnClickListener(v -> {
            if (email.getText().toString().trim().isEmpty() || password.getText().toString().trim().isEmpty()) {
                Toast.makeText(Login.this, "Please fill all the fields", Toast.LENGTH_SHORT).show();
                return;
            }
            loaders.setVisibility(View.VISIBLE);
            String email = Login.this.email.getText().toString();
            String password = Login.this.password.getText().toString();

            fAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                loaders.setVisibility(View.INVISIBLE);
                if (task.isSuccessful()) {
                    if (Objects.requireNonNull(fAuth.getCurrentUser()).isEmailVerified()) {
                        SharedPreferences sharedPreferences = getSharedPreferences("userLoginCredential", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("email", email);
                        editor.putString("password", password);
                        editor.apply();

//                        Intent intent = new Intent(Login.this, Home.class);
                        Intent intent = new Intent(Login.this, sample.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(Login.this, "Please verify your Email", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Toast.makeText(Login.this, "Error: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        });

        reset.setOnClickListener(v -> {
            Intent intent = new Intent(this, ForgotPasswordActivity.class);
            startActivity(intent);
            finish();
        });

    }

    public void back(View v) {
        Intent intent = new Intent( this, index.class);
        startActivity(intent);
        finish();
    }
}