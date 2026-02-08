/*package com.fahim.autoexam;

import static android.content.ContentValues.TAG;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.fahim.autoexam.databinding.ActivityUploadPdfBinding;
import com.tom_roush.pdfbox.android.PDFBoxResourceLoader;
import com.tom_roush.pdfbox.pdmodel.PDDocument;
import com.tom_roush.pdfbox.text.PDFTextStripper;

import java.io.InputStream;

public class UploadPDFActivity extends AppCompatActivity {
    ActivityUploadPdfBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PDFBoxResourceLoader.init(getApplicationContext());
        EdgeToEdge.enable(this);
        binding = ActivityUploadPdfBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        binding.backArrow.setOnClickListener(view -> {
            startActivity(new Intent(UploadPDFActivity.this, ChooseSyllabusActivity.class));
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        });

        binding.generateButton.setOnClickListener(view -> {
            startActivity(new Intent(UploadPDFActivity.this, GeneratingActivity.class));
        });


        Intent intent = getIntent();
        String pdfUriString = intent.getStringExtra("pdfUri");
        String pdfName = intent.getStringExtra("pdfName");

        /*if (pdfUriString != null) {
            Uri pdfUri = Uri.parse(pdfUriString);
            binding.uploadedFileName.setText(pdfName != null ? pdfName : "Unknown File");

            //String content = readPDFContent(pdfUri);
            // binding.pdfContentText.setText(content);
        } else {
            Toast.makeText(this, "No PDF file found!", Toast.LENGTH_SHORT).show();
        }
        binding.uploadedFileName.setText(pdfName);
        binding.pdfContentText.setText(readPDFContent(Uri.parse(pdfUriString)));

        if (pdfUriString != null) {
            Uri pdfUri = Uri.parse(pdfUriString);
            readPDFContent(pdfUri);
        } else {
            Toast.makeText(this, "No PDF file found!", Toast.LENGTH_SHORT).show();
        }
    }



    public String readPDFContent(Uri uri) {
        try {
            ContentResolver contentResolver = getContentResolver();
            InputStream inputStream = contentResolver.openInputStream(uri);

            PDDocument document = PDDocument.load(inputStream);
            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(document);
            document.close();

            Log.d(TAG, "PDF Content: " + text);
            return text;
        } catch (Exception e) {
            e.printStackTrace();
            return "Failed to read PDF content";
        }
    }

/*binding.generateButton.setOnClickListener(view -> {
        if (pdfContent.isEmpty()) {
            Toast.makeText(this, "Please upload a valid PDF first!", Toast.LENGTH_SHORT).show();
            return;
        }

        binding.pdfContentText.setText("Generating questions...");
        GeminiAIHelper ai = new GeminiAIHelper();

        ai.generateQuestions(pdfContent, "MCQs", "Information Technology", new GeminiAIHelper.Callback() {
            @Override
            public void onResult(String output) {
                new Handler(Looper.getMainLooper()).post(() ->
                        binding.pdfContentText.setText(output)
                );
            }

            @Override
            public void onError(Exception e) {
                new Handler(Looper.getMainLooper()).post(() ->
                        Toast.makeText(getApplicationContext(),
                                "Error: " + e.getMessage(), Toast.LENGTH_LONG).show()
                );
            }
        });
    });



    /*private void readPDFContent(Uri uri) {
        try {
            ParcelFileDescriptor fileDescriptor = getContentResolver().openFileDescriptor(uri, "r");
            PDDocument document = PDDocument.load(fileDescriptor.getFileDescriptor());
            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(document);
            document.close();

            Log.d("PDF_CONTENT", text);
            binding.pdfContentTextView.setText(text); // Optional: show in TextView

        } catch (Exception e) {
            e.printStackTrace();
    }


}*/

/*package com.fahim.autoexam;

import static android.content.ContentValues.TAG;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.fahim.autoexam.databinding.ActivityUploadPdfBinding;
import com.tom_roush.pdfbox.android.PDFBoxResourceLoader;
import com.tom_roush.pdfbox.pdmodel.PDDocument;
import com.tom_roush.pdfbox.text.PDFTextStripper;

import java.io.InputStream;

public class UploadPDFActivity extends AppCompatActivity {
    ActivityUploadPdfBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PDFBoxResourceLoader.init(getApplicationContext());
        EdgeToEdge.enable(this);

        binding = ActivityUploadPdfBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Handle window insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Back button
        binding.backArrow.setOnClickListener(view -> {
            startActivity(new Intent(UploadPDFActivity.this, ChooseSyllabusActivity.class));
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        });

        // Get PDF details from previous screen
        Intent intent = getIntent();
        String pdfUriString = intent.getStringExtra("pdfUri");
        String pdfName = intent.getStringExtra("pdfName");

        if (pdfUriString != null) {
            Uri pdfUri = Uri.parse(pdfUriString);

            // Show uploaded file name
            binding.uploadedFileName.setText(pdfName != null ? pdfName : "Unknown File");

            // Read PDF content
            String pdfContent = readPDFContent(pdfUri);
            binding.pdfContentText.setText(pdfContent);

        } else {
            Toast.makeText(this, "No PDF file found!", Toast.LENGTH_SHORT).show();
        }

        // Generate button → Next page (for now static navigation)
        binding.generateButton.setOnClickListener(view -> {
            startActivity(new Intent(UploadPDFActivity.this, GeneratingActivity.class));
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        });
    }

    // Function to extract text from PDF
    public String readPDFContent(Uri uri) {
        try {
            ContentResolver contentResolver = getContentResolver();
            InputStream inputStream = contentResolver.openInputStream(uri);

            PDDocument document = PDDocument.load(inputStream);
            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(document);
            document.close();

            Log.d(TAG, "PDF Content: " + text);
            return text.trim().isEmpty() ? "No readable text found in the PDF." : text;
        } catch (Exception e) {
            e.printStackTrace();
            return "Failed to read PDF content.";
        }
    }
}
*/

package com.fahim.autoexam;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.fahim.autoexam.databinding.ActivityUploadPdfBinding;
import com.tom_roush.pdfbox.android.PDFBoxResourceLoader;
import com.tom_roush.pdfbox.pdmodel.PDDocument;
import com.tom_roush.pdfbox.text.PDFTextStripper;

import java.io.InputStream;

public class UploadPDFActivity extends AppCompatActivity {
    private static final String TAG = "UploadPDFActivity";
    ActivityUploadPdfBinding binding;
    private QuestionPaperData paperData;
    private String pdfContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PDFBoxResourceLoader.init(getApplicationContext());
        EdgeToEdge.enable(this);

        binding = ActivityUploadPdfBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Handle window insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Get data from previous activities
        Intent intent = getIntent();
        paperData = (QuestionPaperData) intent.getSerializableExtra("paperData");
        String pdfUriString = intent.getStringExtra("pdfUri");
        String pdfName = intent.getStringExtra("pdfName");

        // Back button
        binding.backArrow.setOnClickListener(view -> {
            finish();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        });

        if (pdfUriString != null) {
            Uri pdfUri = Uri.parse(pdfUriString);

            // Show uploaded file name
            binding.uploadedFileName.setText(pdfName != null ? pdfName : "Unknown File");

            // Read PDF content on background thread to avoid blocking UI
            binding.pdfContentText.setText("Reading PDF...");
            binding.generateButton.setEnabled(false);

            new Thread(() -> {
                String content = readPDFContent(pdfUri);
                runOnUiThread(() -> {
                    pdfContent = content;
                    if (pdfContent != null && !pdfContent.isEmpty()) {
                        String displayText = pdfContent.length() > 500
                                ? pdfContent.substring(0, 500) + "..."
                                : pdfContent;
                        binding.pdfContentText.setText(displayText);

                        // Save to paperData
                        paperData.setPdfContent(pdfContent);
                        paperData.setPdfName(pdfName);
                    } else {
                        binding.pdfContentText.setText("No readable text found in PDF");
                    }
                    binding.generateButton.setEnabled(true);
                });
            }).start();

        } else {
            Toast.makeText(this, "No PDF file found!", Toast.LENGTH_SHORT).show();
        }

        // Generate button → Start AI generation
        binding.generateButton.setOnClickListener(view -> {
            if (pdfContent == null || pdfContent.trim().isEmpty()) {
                Toast.makeText(this, "Please upload a valid PDF with text content!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Navigate to GeneratingActivity with all data
            Intent generateIntent = new Intent(UploadPDFActivity.this, GeneratingActivity.class);
            generateIntent.putExtra("paperData", paperData);
            startActivity(generateIntent);
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        });
    }

    // Function to extract text from PDF
    public String readPDFContent(Uri uri) {
        try {
            ContentResolver contentResolver = getContentResolver();
            InputStream inputStream = contentResolver.openInputStream(uri);

            PDDocument document = PDDocument.load(inputStream);
            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(document);
            document.close();

            Log.d(TAG, "PDF Content extracted: " + text.length() + " characters");
            return text.trim().isEmpty() ? null : text;
        } catch (Exception e) {
            Log.e(TAG, "Error reading PDF", e);
            e.printStackTrace();
            return null;
        }
    }
}