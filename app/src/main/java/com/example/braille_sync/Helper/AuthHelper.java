package com.example.braille_sync.Helper;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AuthHelper {
    private final FirebaseAuth fAuth;
    private final SharedPreferences sharedPreferences;

    public AuthHelper(Context context) {
        fAuth = FirebaseAuth.getInstance();
        sharedPreferences = context.getSharedPreferences("userLoginCredential", Context.MODE_PRIVATE);
    }

    public boolean isUserSignedIn() {
        FirebaseUser currentUser = fAuth.getCurrentUser();
        return currentUser != null;
    }

    public void signInUserWithStoredCredentials(OnAuthCompleteListener listener) {
        String email = sharedPreferences.getString("email", null);
        String password = sharedPreferences.getString("password", null);

        if (email != null && password != null) {
            fAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            listener.onSuccess();
                        } else {
                            listener.onFailure("Authentication Failed.");
                        }
                    });
        } else {
            listener.onFailure("No stored credentials found.");
        }
    }

    public void signInUser(String email, String password, OnAuthCompleteListener listener) {
        fAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("email", email);
                        editor.putString("password", password);
                        editor.apply();
                        listener.onSuccess();
                    } else {
                        listener.onFailure("Authentication Failed.");
                    }
                });
    }

    public interface OnAuthCompleteListener {
        void onSuccess();
        void onFailure(String errorMessage);
    }
}
