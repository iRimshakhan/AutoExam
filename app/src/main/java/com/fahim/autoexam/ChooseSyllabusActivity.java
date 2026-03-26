package com.fahim.autoexam;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.fahim.autoexam.databinding.ActivityChooseSyllabusBinding;

public class ChooseSyllabusActivity extends AppCompatActivity {
    private static final String TAG = "ChooseSyllabusActivity";
    ActivityChooseSyllabusBinding binding;
    private QuestionPaperData paperData;

    private Uri syllabusUri;
    private String syllabusName;
    private Uri patternUri;
    private String patternName;

    private ActivityResultLauncher<Intent> syllabusLauncher;
    private ActivityResultLauncher<Intent> patternLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        paperData = (QuestionPaperData) getIntent().getSerializableExtra("paperData");

        syllabusLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        syllabusUri = result.getData().getData();
                        syllabusName = getFileNameFromUri(syllabusUri);
                        binding.syllabusFileName.setText(syllabusName);
                        updateNextButton();
                    }
                });

        patternLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        patternUri = result.getData().getData();
                        patternName = getFileNameFromUri(patternUri);
                        binding.patternFileName.setText(patternName);
                        updateNextButton();
                    }
                });

        binding = ActivityChooseSyllabusBinding.inflate(getLayoutInflater());
        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        binding.backArrow.setOnClickListener(view -> {
            finish();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        });

        binding.chooseSyllabusFile.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("application/pdf");
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            syllabusLauncher.launch(Intent.createChooser(intent, "Select Syllabus PDF"));
        });

        binding.choosePatternFile.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("application/pdf");
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            patternLauncher.launch(Intent.createChooser(intent, "Select Pattern Paper PDF"));
        });

        binding.nextButton.setEnabled(false);
        binding.nextButton.setOnClickListener(view -> {
            Intent intent = new Intent(ChooseSyllabusActivity.this, UploadPDFActivity.class);
            intent.putExtra("syllabusUri", syllabusUri.toString());
            intent.putExtra("syllabusName", syllabusName);
            intent.putExtra("patternUri", patternUri.toString());
            intent.putExtra("patternName", patternName);
            intent.putExtra("paperData", paperData);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        });
    }

    private void updateNextButton() {
        binding.nextButton.setEnabled(syllabusUri != null && patternUri != null);
    }

    private String getFileNameFromUri(Uri uri) {
        String result = null;
        if ("content".equals(uri.getScheme())) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if (nameIndex >= 0) {
                        result = cursor.getString(nameIndex);
                    }
                }
            } finally {
                if (cursor != null) cursor.close();
            }
        }
        if (result == null) {
            result = uri.getLastPathSegment();
        }
        return result;
    }
}
