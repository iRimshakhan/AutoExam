package com.fahim.autoexam;

import android.animation.ObjectAnimator;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.pdf.PdfRenderer;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.fahim.autoexam.databinding.ActivityUploadPdfBinding;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;
import com.tom_roush.pdfbox.android.PDFBoxResourceLoader;
import com.tom_roush.pdfbox.pdmodel.PDDocument;
import com.tom_roush.pdfbox.text.PDFTextStripper;

import java.io.InputStream;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

public class UploadPDFActivity extends AppCompatActivity {
    private static final String TAG = "UploadPDFActivity";
    ActivityUploadPdfBinding binding;
    private ObjectAnimator progressAnimator;
    private QuestionPaperData paperData;
    private String syllabusContent;
    private String patternContent;
    private final AtomicInteger filesRead = new AtomicInteger(0);

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

        Intent intent = getIntent();
        paperData = (QuestionPaperData) intent.getSerializableExtra("paperData");
        String syllabusUriString = intent.getStringExtra("syllabusUri");
        String syllabusName = intent.getStringExtra("syllabusName");
        String patternUriString = intent.getStringExtra("patternUri");
        String patternName = intent.getStringExtra("patternName");

        binding.progressBar.setProgress(0);
        binding.progressBar.setMax(100);

        progressAnimator = ObjectAnimator.ofInt(binding.progressBar, "progress", 0, 50);
        progressAnimator.setDuration(3000); // 3 seconds to reach 80%
        progressAnimator.start();

        binding.backArrow.setOnClickListener(view -> {
            finish();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        });

        binding.syllabusFileName.setText(syllabusName != null ? syllabusName : "Unknown File");
        binding.patternFileName.setText(patternName != null ? patternName : "Unknown File");
        binding.pdfContentText.setText("Reading files...");
        binding.generateButton.setEnabled(false);

        if (syllabusUriString != null && patternUriString != null) {
            Uri syllabusUri = Uri.parse(syllabusUriString);
            Uri patternUri = Uri.parse(patternUriString);

            readPdfWithOcrFallback(syllabusUri, content -> {
                syllabusContent = content;
                runOnUiThread(this::onFileRead);
            });

            readPdfWithOcrFallback(patternUri, content -> {
                patternContent = content;
                runOnUiThread(this::onFileRead);
            });
        } else {
            Toast.makeText(this, "Missing PDF files!", Toast.LENGTH_SHORT).show();
        }

        binding.generateButton.setOnClickListener(view -> {
            if (syllabusContent == null || syllabusContent.trim().isEmpty()) {
                Toast.makeText(this, "Could not read syllabus PDF!", Toast.LENGTH_SHORT).show();
                return;
            }
            if (patternContent == null || patternContent.trim().isEmpty()) {
                Toast.makeText(this, "Could not read pattern paper PDF!", Toast.LENGTH_SHORT).show();
                return;
            }

            paperData.setPdfContent(syllabusContent);
            paperData.setPdfName(syllabusName);
            paperData.setPatternPdfContent(patternContent);
            paperData.setPatternPdfName(patternName);

            Intent generateIntent = new Intent(UploadPDFActivity.this, GeneratingActivity.class);
            generateIntent.putExtra("paperData", paperData);
            startActivity(generateIntent);
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        });
    }

    private void onFileRead() {
        int count = filesRead.incrementAndGet();

        // Smoothly animate to the next milestone (50% or 100%)
        int targetProgress = count * 50;

        ObjectAnimator.ofInt(binding.progressBar, "progress", binding.progressBar.getProgress(), targetProgress)
                .setDuration(500) // Small duration just for the "slide" effect
                .start();

        if (count == 1) {
            binding.pdfContentText.setText("Processed 1/2 files...");
        }

        if (count >= 2) {
            binding.pdfContentText.setText("Both files ready!");
            binding.generateButton.setEnabled(true);
            binding.imageView.setImageResource(R.drawable.file_check_green);
        }
    }

    private void readPdfWithOcrFallback(Uri uri, Consumer<String> callback) {
        new Thread(() -> {
            String pdfBoxText = readPDFContent(uri);

            if (pdfBoxText != null && pdfBoxText.length() >= 50) {
                Log.d(TAG, "PDFBox extraction successful: " + pdfBoxText.length() + " chars");
                callback.accept(pdfBoxText);
                return;
            }

            Log.d(TAG, "PDFBox returned insufficient text, falling back to OCR");
            runOnUiThread(() -> {
                // Boost the bar slightly to show OCR started
                int current = binding.progressBar.getProgress();
                ObjectAnimator.ofInt(binding.progressBar, "progress", current, current + 10)
                        .setDuration(1000)
                        .start();
            });
            try {
                ParcelFileDescriptor pfd = getContentResolver().openFileDescriptor(uri, "r");
                if (pfd == null) {
                    callback.accept(null);
                    return;
                }

                PdfRenderer renderer = new PdfRenderer(pfd);
                int pageCount = renderer.getPageCount();
                StringBuilder ocrText = new StringBuilder();
                TextRecognizer recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);

                for (int i = 0; i < pageCount; i++) {
                    final int pageIndex = i;
                    PdfRenderer.Page page = renderer.openPage(pageIndex);

                    int width = (int) (page.getWidth() * 200.0 / 72.0);
                    int height = (int) (page.getHeight() * 200.0 / 72.0);
                    Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                    page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
                    page.close();

                    CountDownLatch latch = new CountDownLatch(1);
                    InputImage image = InputImage.fromBitmap(bitmap, 0);
                    final String[] pageText = {""};

                    recognizer.process(image)
                            .addOnSuccessListener(result -> {
                                pageText[0] = result.getText();
                                latch.countDown();
                            })
                            .addOnFailureListener(e -> {
                                Log.e(TAG, "OCR failed on page " + pageIndex, e);
                                latch.countDown();
                            });

                    latch.await();
                    bitmap.recycle();

                    if (!pageText[0].isEmpty()) {
                        ocrText.append(pageText[0]).append("\n");
                    }
                }

                renderer.close();
                pfd.close();
                recognizer.close();

                String result = ocrText.toString().trim();
                Log.d(TAG, "OCR extraction complete: " + result.length() + " chars");
                callback.accept(result.isEmpty() ? null : result);

            } catch (Exception e) {
                Log.e(TAG, "OCR fallback failed", e);
                callback.accept(pdfBoxText);
            }
        }).start();
    }

    private String readPDFContent(Uri uri) {
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
            return null;
        }
    }
}
