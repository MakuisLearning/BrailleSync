package com.example.braille_sync;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.os.Handler;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;

public class Splash extends AppCompatActivity {
    FirebaseAuth fAuth;
    String email, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash);

        fAuth = FirebaseAuth.getInstance();

        new Handler().postDelayed(this::navigateToLandingView, 3000);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }


    private void navigateToLandingView() {
        if (fAuth.getCurrentUser() != null) {
//            Intent intent = new Intent(Splash.this, Home.class);
//            startActivity(intent);
//            finish();
            Intent intent = new Intent(Splash.this, sample.class);
            startActivity(intent);
            finish();
        } else {
            SharedPreferences sharedPreferences = getSharedPreferences("userLoginCredential", MODE_PRIVATE);
            email = sharedPreferences.getString("email", null);
            password = sharedPreferences.getString("password", null);

            if (email != null && password != null) {
                fAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
//                                Intent intent = new Intent(Splash.this, Home.class);
//                                startActivity(intent);
                                Intent intent = new Intent(Splash.this, sample.class);
                                startActivity(intent);
                            } else {
                                Intent intent = new Intent(Splash.this, index.class);
                                startActivity(intent);
                            }
                            finish();
                        });
            } else {
                Intent intent = new Intent(Splash.this, index.class);
                startActivity(intent);
                finish();
            }
        }
    }
}