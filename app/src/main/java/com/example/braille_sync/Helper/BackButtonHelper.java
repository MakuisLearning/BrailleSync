package com.example.braille_sync.Helper;
;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

public abstract class BackButtonHelper {
    public static void addBackButtonCallback(AppCompatActivity activity) {
        activity.getOnBackPressedDispatcher().addCallback(activity, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (activity.getSupportFragmentManager().getBackStackEntryCount() == 0) {
                    activity.finish();
                } else {
                    activity.getSupportFragmentManager().popBackStack();
                }
            }
        });
    }
}
