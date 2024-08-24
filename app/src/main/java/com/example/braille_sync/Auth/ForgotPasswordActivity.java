package com.example.braille_sync.Auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.braille_sync.R;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity {

    Button back,reset;
    EditText email;
    FirebaseAuth fAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forgot_password);
        back = findViewById(R.id.back);
        reset = findViewById(R.id.resetBtn);
        email = findViewById(R.id.email);
        fAuth = FirebaseAuth.getInstance();


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        reset.setOnClickListener(v -> {
                    String userEmail = email.getText().toString();

                    if (TextUtils.isEmpty(userEmail) || !Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()) {
                        Toast.makeText(this, "Enter your Registered email address", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    fAuth.sendPasswordResetEmail(userEmail).addOnCompleteListener(task ->{
                        if (task.isSuccessful()) {
                            Toast.makeText(this, "Password reset email sent", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(this, Login.class);
                            startActivity(intent);
                            finish();
                        }
                    });

        });



        back.setOnClickListener(v -> {
            Intent intent = new Intent(this, Login.class);
            startActivity(intent);
            finish();
        });
    }


}