package com.example.braille_sync.Helper;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class NetworkHelper {


    public static boolean isConnectedToInternet(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        Network network = connectivityManager.getActiveNetwork();
        if (network != null) {
            NetworkCapabilities networkCapabilities = connectivityManager.getNetworkCapabilities(network);
            boolean hasInternet = networkCapabilities != null &&
                    networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                    networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED);
            Log.d("NetworkCheck", "isConnectedToInternet: " + hasInternet);
            return hasInternet;
        }
        Log.d("NetworkCheck", "isConnectedToInternet: false (network is null)");
        return false;
    }

    public static boolean isConnectedToLocalNetwork(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        Network network = connectivityManager.getActiveNetwork();
        if (network != null) {
            NetworkCapabilities networkCapabilities = connectivityManager.getNetworkCapabilities(network);
            boolean isWiFi = networkCapabilities != null && networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI);
            Log.d("NetworkCheck", "isConnectedToLocalNetwork: " + isWiFi);
            return isWiFi;
        }
        Log.d("NetworkCheck", "isConnectedToLocalNetwork: false (network is null)");
        return false;
    }

    public interface Callback {
        void onSuccess(String result);
        void onFailure(String errorMessage);
    }

    public static void sendGetRequest(String urlString, Callback callback) {
        new Thread(() -> {
            String result = fetchResult(urlString);
            new Handler(Looper.getMainLooper()).post(() -> {
                if (result != null) {
                    callback.onSuccess(result);
                } else {
                    callback.onFailure("Failed to get response");
                }
            });
        }).start();
    }

    private static String fetchResult(String urlString) {
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
                Log.e("TAG", "Server returned non-OK status: " + urlConnection.getResponseCode());
                return null;
            }
        } catch (Exception e) {
            Log.e("TAG", "Error in fetchResult", e);
            return null;
        }
    }
}
