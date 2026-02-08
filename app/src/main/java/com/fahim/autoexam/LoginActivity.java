package com.fahim.autoexam;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.fahim.autoexam.databinding.ActivityLoginBinding;
import com.fahim.autoexam.db.AppDatabase;
import com.fahim.autoexam.db.UserEntity;

import java.security.MessageDigest;

public class LoginActivity extends AppCompatActivity {
    ActivityLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        binding.loginButton.setOnClickListener(view -> loginUser());

        binding.registerButton.setOnClickListener(view ->
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class))
        );
    }

    private void loginUser() {
        String email = binding.editEmailAddress.getText().toString().trim();
        String password = binding.editPassword.getText().toString().trim();

        if (email.isEmpty()) {
            binding.editEmailAddress.setError("Email is required");
            binding.editEmailAddress.requestFocus();
            return;
        }
        if (password.isEmpty()) {
            binding.editPassword.setError("Password is required");
            binding.editPassword.requestFocus();
            return;
        }

        String hashedPassword = hashPassword(password);

        new Thread(() -> {
            UserEntity user = AppDatabase.getInstance(this).userDao().login(email, hashedPassword);
            runOnUiThread(() -> {
                if (user != null) {
                    // Save session
                    SharedPreferences prefs = getSharedPreferences("autoexam_prefs", MODE_PRIVATE);
                    prefs.edit()
                            .putBoolean("isLoggedIn", true)
                            .putString("userEmail", email)
                            .apply();

                    startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                    finish();
                } else {
                    Toast.makeText(this, "Invalid email or password", Toast.LENGTH_SHORT).show();
                }
            });
        }).start();
    }

    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            return password;
        }
    }
}
