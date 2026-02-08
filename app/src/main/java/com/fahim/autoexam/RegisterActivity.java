package com.fahim.autoexam;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.fahim.autoexam.databinding.ActivityRegisterBinding;
import com.fahim.autoexam.db.AppDatabase;
import com.fahim.autoexam.db.UserEntity;

import java.security.MessageDigest;

public class RegisterActivity extends AppCompatActivity {
    ActivityRegisterBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        binding.createButton.setOnClickListener(view -> registerUser());
    }

    private void registerUser() {
        String email = binding.editEmailAddress.getText().toString().trim();
        String password = binding.editPassword.getText().toString().trim();
        String confirmPassword = binding.editConfirmPassword.getText().toString().trim();

        // Validation
        if (email.isEmpty()) {
            binding.editEmailAddress.setError("Email is required");
            binding.editEmailAddress.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.editEmailAddress.setError("Enter a valid email");
            binding.editEmailAddress.requestFocus();
            return;
        }
        if (password.isEmpty()) {
            binding.editPassword.setError("Password is required");
            binding.editPassword.requestFocus();
            return;
        }
        if (password.length() < 6) {
            binding.editPassword.setError("Password must be at least 6 characters");
            binding.editPassword.requestFocus();
            return;
        }
        if (!password.equals(confirmPassword)) {
            binding.editConfirmPassword.setError("Passwords do not match");
            binding.editConfirmPassword.requestFocus();
            return;
        }

        String hashedPassword = hashPassword(password);

        new Thread(() -> {
            UserEntity existing = AppDatabase.getInstance(this).userDao().getUserByEmail(email);
            if (existing != null) {
                runOnUiThread(() -> Toast.makeText(this, "Email already registered", Toast.LENGTH_SHORT).show());
                return;
            }

            UserEntity user = new UserEntity();
            user.email = email;
            user.password = hashedPassword;
            AppDatabase.getInstance(this).userDao().insert(user);

            runOnUiThread(() -> {
                Toast.makeText(this, "Registration successful!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                finish();
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
