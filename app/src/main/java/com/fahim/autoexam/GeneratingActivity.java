/*package com.fahim.autoexam;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.fahim.autoexam.databinding.ActivityGeneratingBinding;

public class GeneratingActivity extends AppCompatActivity {
    ActivityGeneratingBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        binding = ActivityGeneratingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ObjectAnimator progressAnimator = ObjectAnimator.ofInt(binding.progressBar, "progress", 0, 100);
        progressAnimator.setDuration(3000); // 3 seconds
        progressAnimator.start();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(GeneratingActivity.this, ViewPDFActivity.class));
                finish();
            }
        }, 3000);
    }
}

 */

package com.fahim.autoexam;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.fahim.autoexam.databinding.ActivityGeneratingBinding;

public class GeneratingActivity extends AppCompatActivity {
    private static final String TAG = "GeneratingActivity";
    ActivityGeneratingBinding binding;
    private QuestionPaperData paperData;
    private GeminiAIHelper geminiHelper;
    private boolean isGenerating = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        binding = ActivityGeneratingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Get data
        paperData = (QuestionPaperData) getIntent().getSerializableExtra("paperData");

        if (paperData == null || paperData.getPdfContent() == null) {
            Toast.makeText(this, "Error: Missing data!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Handle back press using OnBackPressedDispatcher (Modern way)
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (isGenerating) {
                    Toast.makeText(GeneratingActivity.this,
                            "Please wait while questions are being generated...",
                            Toast.LENGTH_SHORT).show();
                } else {
                    finish();
                }
            }
        });

        // Initialize AI Helper
        geminiHelper = new GeminiAIHelper();

        // Start generation animation and process
        startGenerationProcess();
    }

    private void startGenerationProcess() {
        isGenerating = true;

        // Animate the steps
        animateSteps();

        // Call Gemini AI
        geminiHelper.generateQuestions(paperData, paperData.getPdfContent(),
                new GeminiAIHelper.OnQuestionGeneratedListener() {
                    @Override
                    public void onSuccess(String generatedQuestions) {
                        Log.d(TAG, "Questions generated successfully");
                        isGenerating = false;

                        // Navigate to ViewPDFActivity with generated questions
                        Intent intent = new Intent(GeneratingActivity.this, ViewPDFActivity.class);
                        intent.putExtra("paperData", paperData);
                        intent.putExtra("generatedQuestions", generatedQuestions);
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onError(String errorMessage) {
                        Log.e(TAG, "Error generating questions: " + errorMessage);
                        isGenerating = false;

                        runOnUiThread(() -> {
                            Toast.makeText(GeneratingActivity.this,
                                    "Error: " + errorMessage, Toast.LENGTH_LONG).show();
                            finish();
                        });
                    }

                    @Override
                    public void onProgress(String message) {
                        Log.d(TAG, "Progress: " + message);
                        runOnUiThread(() -> {
                            binding.text1.setText(message);
                        });
                    }
                });
    }

    private void animateSteps() {
        // Step 1: Analyzing syllabus - show immediately
        binding.text3.setAlpha(1.0f);

        // Step 2: Creating questions - show after 2 seconds
        new Handler().postDelayed(() -> {
            if (isGenerating) {
                binding.text4.setAlpha(1.0f);
            }
        }, 2000);

        // Step 3: Formatting - show after 4 seconds
        new Handler().postDelayed(() -> {
            if (isGenerating) {
                binding.text5.setAlpha(1.0f);
            }
        }, 4000);
    }
}