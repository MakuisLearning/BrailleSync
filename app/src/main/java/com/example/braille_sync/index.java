package com.example.braille_sync;

import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.braille_sync.Auth.Login;
import com.example.braille_sync.Helper.BackButtonHelper;
import com.example.braille_sync.Helper.NetworkHelper;
import com.example.braille_sync.Main.arduinoWifi;

public class index extends AppCompatActivity {
    ConnectivityChangeReceiver connectivityChangeReceiver;
    Boolean isConnected, isOnInternet;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        connectivityChangeReceiver = new ConnectivityChangeReceiver();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        BackButtonHelper.addBackButtonCallback(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        handleNavigation();
        registerReceiver(connectivityChangeReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    @Override
    protected void onResume() {
        super.onResume();
        handleNavigation();
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(connectivityChangeReceiver);
    }

    private void handleNavigation() {
        if (NetworkHelper.isConnectedToLocalNetwork(this)) {
            isConnected = NetworkHelper.isConnectedToLocalNetwork(this);
            isOnInternet = NetworkHelper.isConnectedToInternet(this);
        }
        else {
            isConnected = NetworkHelper.isConnectedToLocalNetwork(this);
            promptUserToConnectToWiFi();
        }
    }

    public void redirect(View v) {
        Intent intent = null;
        if (v.getId() == R.id.modifyWifi) {
            String urlString = "http://192.168.4.1/Connect";
            NetworkHelper.sendGetRequest(urlString, new NetworkHelper.Callback() {
                @Override
                public void onSuccess(String result) {
                    Toast.makeText(index.this, result, Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(index.this, arduinoWifi.class);
                    startActivity(intent);
                    finish();
                }

                @Override
                public void onFailure(String errorMessage) {
                    Toast.makeText(index.this, errorMessage, Toast.LENGTH_SHORT).show();
                }
            });
        } else if (v.getId() == R.id.login) {
            intent = new Intent(this, Login.class);
        }
        if( intent != null){
            startActivity(intent);
            finish();
        }
    }


    private void promptUserToConnectToWiFi() {
        new AlertDialog.Builder(this)
                .setTitle("WiFi Connection Needed")
                .setMessage("Please connect to WiFi to continue.")
                .setPositiveButton("Go to WiFi Settings", (dialog, which) -> {
                    try {
                        Intent intent = new Intent(Intent.ACTION_MAIN);
                        intent.setClassName("com.android.settings", "com.android.settings.wifi.WifiSettings");
                        startActivity(intent);
                    } catch (ActivityNotFoundException e) {

                        Toast.makeText(index.this, "WiFi settings not found on this device.", Toast.LENGTH_SHORT).show();
                        promptUserToConnectToWiFi();
                    }
                })
                .setNegativeButton("cancel", (dialog, which) -> handleNavigation())
                .show();
    }

    private class ConnectivityChangeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            handleNavigation();
        }
    }

}
