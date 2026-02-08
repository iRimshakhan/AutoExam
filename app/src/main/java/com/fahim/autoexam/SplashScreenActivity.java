package com.fahim.autoexam;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.fahim.autoexam.databinding.ActivityMainBinding;

public class SplashScreenActivity extends AppCompatActivity {
    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Animate progress from 0 to 100 over 3 seconds
        ObjectAnimator progressAnimator = ObjectAnimator.ofInt(binding.progressBar, "progress", 0, 100);
        progressAnimator.setDuration(3000); // 3 seconds
        progressAnimator.start();

        new Handler().postDelayed(() -> {
            SharedPreferences prefs = getSharedPreferences("autoexam_prefs", MODE_PRIVATE);
            boolean isLoggedIn = prefs.getBoolean("isLoggedIn", false);

            if (isLoggedIn) {
                startActivity(new Intent(SplashScreenActivity.this, HomeActivity.class));
            } else {
                startActivity(new Intent(SplashScreenActivity.this, LoginActivity.class));
            }
            finish();
        }, 3000);
    }
}
