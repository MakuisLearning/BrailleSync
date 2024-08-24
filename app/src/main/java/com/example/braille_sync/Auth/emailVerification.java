package com.example.braille_sync.Auth;
import android.content.Intent;

import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.braille_sync.R;

public class emailVerification extends AppCompatActivity {
    Button backBtn ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        String email = getIntent().getStringExtra("email");
        String password = getIntent().getStringExtra("password");
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_email_verification);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        backBtn = findViewById(R.id.goBack);


        backBtn.setOnClickListener(v -> {
            Intent intent = new Intent( this, Login.class);
            intent.putExtra("email", email);
            intent.putExtra("password", password);
            startActivity(intent);
            finish();
        });
    }




}