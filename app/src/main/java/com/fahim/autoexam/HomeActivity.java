package com.fahim.autoexam;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.navigation.NavigationView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.fahim.autoexam.databinding.ActivityHomeBinding;
import com.fahim.autoexam.db.AppDatabase;
import com.fahim.autoexam.db.ReportEntity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {
    private static final String TAG = "HomeActivity";
    ActivityHomeBinding binding;
    private ReportAdapter adapter;
    private List<ReportEntity> allReports = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Setup RecyclerView
        adapter = new ReportAdapter(this::openPdf, this::confirmDelete);
        binding.questionListView.setLayoutManager(new LinearLayoutManager(this));
        binding.questionListView.setAdapter(adapter);

        // Side menu opens drawer
        binding.sideMenu.setOnClickListener(view ->
                binding.drawerLayout.openDrawer(GravityCompat.START)
        );

        // Set user email in drawer header
        View headerView = binding.navigationView.getHeaderView(0);
        TextView navUserEmail = headerView.findViewById(R.id.nav_user_email);
        SharedPreferences prefs = getSharedPreferences("autoexam_prefs", MODE_PRIVATE);
        String email = prefs.getString("userEmail", "");
        navUserEmail.setText(email);

        // Navigation drawer item clicks
        binding.navigationView.setNavigationItemSelectedListener(item -> {
            binding.drawerLayout.closeDrawer(GravityCompat.START);
            int id = item.getItemId();

            if (id == R.id.nav_profile) {
                showProfileDialog();
            } else if (id == R.id.nav_settings) {
                Toast.makeText(this, "Settings coming soon", Toast.LENGTH_SHORT).show();
            } else if (id == R.id.nav_about) {
                showAboutDialog();
            } else if (id == R.id.nav_logout) {
                new MaterialAlertDialogBuilder(this)
                        .setTitle("Logout")
                        .setMessage("Are you sure you want to logout?")
                        .setNegativeButton("Cancel", null)
                        .setPositiveButton("Logout", (dialog, which) -> logout())
                        .show();
            }
            return true;
        });

        // FAB click
        binding.createFab.setOnClickListener(view ->
                startActivity(new Intent(HomeActivity.this, FormActivity.class))
        );

        // Search filter
        binding.searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterReports(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadReports();
    }

    @Override
    public void onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void loadReports() {
        new Thread(() -> {
            List<ReportEntity> reports = AppDatabase.getInstance(this).reportDao().getAllReports();
            runOnUiThread(() -> {
                allReports = reports;
                adapter.setReports(reports);
            });
        }).start();
    }

    private void filterReports(String query) {
        if (query.isEmpty()) {
            adapter.setReports(allReports);
            return;
        }
        String lowerQuery = query.toLowerCase();
        List<ReportEntity> filtered = new ArrayList<>();
        for (ReportEntity report : allReports) {
            if (report.paperName.toLowerCase().contains(lowerQuery)
                    || report.subjectName.toLowerCase().contains(lowerQuery)) {
                filtered.add(report);
            }
        }
        adapter.setReports(filtered);
    }

    private void confirmDelete(ReportEntity report) {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Delete")
                .setMessage("Are you sure you want to delete \"" + report.paperName + "\"?")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Delete", (dialog, which) -> deleteReport(report))
                .show();
    }

    private void deleteReport(ReportEntity report) {
        new Thread(() -> {
            File file = new File(report.filePath);
            if (file.exists()) {
                file.delete();
            }
            AppDatabase.getInstance(this).reportDao().delete(report);
            runOnUiThread(this::loadReports);
        }).start();
    }

    private void showProfileDialog() {
        SharedPreferences prefs = getSharedPreferences("autoexam_prefs", MODE_PRIVATE);
        String email = prefs.getString("userEmail", "N/A");
        new MaterialAlertDialogBuilder(this)
                .setTitle("Profile")
                .setMessage("Email: " + email)
                .setPositiveButton("OK", null)
                .show();
    }

    private void showAboutDialog() {
        new MaterialAlertDialogBuilder(this)
                .setTitle("About AutoExamGen")
                .setMessage("AutoExamGen v1.0\n\nAI-powered question paper generator.\nGenerate exam papers from your syllabus PDF using Gemini AI.")
                .setPositiveButton("OK", null)
                .show();
    }

    private void logout() {
        SharedPreferences prefs = getSharedPreferences("autoexam_prefs", MODE_PRIVATE);
        prefs.edit().clear().apply();
        Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void openPdf(ReportEntity report) {
        File file = new File(report.filePath);
        if (!file.exists()) {
            Toast.makeText(this, "PDF file not found", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            Uri pdfUri = FileProvider.getUriForFile(this,
                    getPackageName() + ".fileprovider", file);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(pdfUri, "application/pdf");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(intent);
        } catch (Exception e) {
            Log.e(TAG, "Error opening PDF", e);
            Toast.makeText(this, "No PDF viewer app found", Toast.LENGTH_SHORT).show();
        }
    }
}
