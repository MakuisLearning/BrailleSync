package com.example.braille_sync.Main;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.braille_sync.R;
import com.example.braille_sync.index;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class arduinoWifi extends AppCompatActivity {

    EditText SSID, PASS;
    Button connect;
    private static final String TAG = "arduinoWifi";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_arduino_wifi);

        SSID = findViewById(R.id.SSID);
        PASS = findViewById(R.id.password);
        connect = findViewById(R.id.connectbtn);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        connect.setOnClickListener(v -> {
            String ssid = SSID.getText().toString();
            String password = PASS.getText().toString();
            sendGetRequest(ssid, password);
        });
    }

    private void sendGetRequest(String ssid, String password) {
        new Thread(() -> {
            String urlString = "http://192.168.4.1/config?ssid=" + ssid + "&pass=" + password;
            String result = fetchResult(urlString);

            new Handler(Looper.getMainLooper()).post(() -> {
                if (result != null && result.equals("success")) {
                    new AlertDialog.Builder(this)
                            .setTitle("Connection Successful")
                            .setCancelable(false)
                            .setMessage("ESP has successfully connected. Redirecting now...")
                            .setPositiveButton("Proceed", (dialog, which) -> {
                                Intent intent = new Intent(this, index.class);
                                startActivity(intent);
                                finish();
                            })
                            .show();
                } else {
                    Toast.makeText(arduinoWifi.this, "Failed to Connect", Toast.LENGTH_LONG).show();
                }
            });

        }).start();
    }

    private String fetchResult(String urlString) {
        try {
            URL url = new URL(urlString);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    response.append(line);
                }
                bufferedReader.close();
                return response.toString();
            } else {
                Log.e(TAG, "Server returned non-OK status: " + urlConnection.getResponseCode());
                return null;
            }
        } catch (Exception e) {
            Log.e(TAG, "Error in fetchResult", e);
            return null;
        }
    }
}
