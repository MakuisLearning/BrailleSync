package com.example.braille_sync.Main;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.example.braille_sync.Fragments.AboutFragment;
import com.example.braille_sync.Fragments.ConnectWifiFragment;
import com.example.braille_sync.Fragments.HistoryFragment;
import com.example.braille_sync.Fragments.HomeFragment;
import com.example.braille_sync.index;
import com.example.braille_sync.R;
import com.google.android.material.navigation.NavigationView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


public class Home extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    ImageView setting;
    FirebaseAuth fAuth;
    DrawerLayout drawer;
    NavigationView navigationView;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    TextView usernameTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_landing);

        fAuth = FirebaseAuth.getInstance();
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        setting = findViewById(R.id.settings);
        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        usernameTV = headerView.findViewById(R.id.username);
        getUserData(user.getUid(), usernameTV);


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_home);
        }

        setting.setOnClickListener(v -> toggle());
    }

    public void toggle() {

        if (drawer.isDrawerOpen(GravityCompat.END)) {
            drawer.closeDrawer(GravityCompat.END);
        } else {
            RotateAnimation rotate = new RotateAnimation(0f, 360f,
                    Animation.RELATIVE_TO_SELF, 0.5f,
                    Animation.RELATIVE_TO_SELF, 0.5f);
            rotate.setDuration(500);
            rotate.setFillAfter(true);
            setting.startAnimation(rotate);
            drawer.openDrawer(GravityCompat.END);
        }

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        Fragment selectedFragment = null;

        if (itemId == R.id.nav_home) {
            selectedFragment = new HomeFragment();
        } else if (itemId == R.id.nav_history) {
            selectedFragment = new HistoryFragment();
        } else if (itemId == R.id.nav_about) {
            selectedFragment = new AboutFragment();
        } else if (itemId == R.id.nav_connect) {
            selectedFragment = new ConnectWifiFragment();
        }
        else if (itemId == R.id.nav_logout) {
            SignOut();
            DrawerLayout drawer = findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.END);
            return true;
        }

        if (selectedFragment != null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.END);
        return true;
    }



    public void SignOut() {
        SharedPreferences sharedPreferences = getSharedPreferences("userLoginCredential", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        fAuth.signOut();
        Intent intent = new Intent( this, index.class);
        startActivity(intent);
        finish();
    }

    public void getUserData(String uid, TextView usernameTextView) {
        db.collection("users").document(uid).get()
                .addOnCompleteListener(task -> {
                    DocumentSnapshot document = task.getResult();
                    if (task.isSuccessful()) {
                        if(document.exists()){
                            String username = document.getString("username");
                            usernameTextView.setText(username);
                        }

                    } else {
                        System.out.println("Error getting documents: " + task.getException());
                    }
                });
    }
}