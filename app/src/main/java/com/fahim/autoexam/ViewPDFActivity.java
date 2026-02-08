/*package com.fahim.autoexam;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.fahim.autoexam.databinding.ActivityViewPdfBinding;

public class ViewPDFActivity extends AppCompatActivity {
    ActivityViewPdfBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityViewPdfBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        binding.backArrow.setOnClickListener(view -> {
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            finish();
        });
    }
}*/

package com.fahim.autoexam;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.fahim.autoexam.databinding.ActivityViewPdfBinding;
import com.fahim.autoexam.db.AppDatabase;
import com.fahim.autoexam.db.ReportEntity;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ViewPDFActivity extends AppCompatActivity {
    private static final String TAG = "ViewPDFActivity";
    ActivityViewPdfBinding binding;
    private QuestionPaperData paperData;
    private String generatedQuestions;
    private File generatedPdfFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityViewPdfBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Get data
        paperData = (QuestionPaperData) getIntent().getSerializableExtra("paperData");
        generatedQuestions = getIntent().getStringExtra("generatedQuestions");

        if (generatedQuestions == null || generatedQuestions.isEmpty()) {
            Toast.makeText(this, "No questions generated!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        Log.d(TAG, "Generated Questions:\n" + generatedQuestions);

        // Set preview info from paperData
        String fileName = paperData.getSubjectName().replaceAll(" ", "_") + "_QuestionPaper.pdf";
        binding.nestedLayout.title.setText(fileName);
        binding.nestedLayout.paperName.setText(paperData.getQuestionPaperName());

        // Preview card click - opens PDF if downloaded
        binding.nestedLayout.previewCard.setOnClickListener(view -> {
            if (generatedPdfFile != null && generatedPdfFile.exists()) {
                openPdf();
            } else {
                Toast.makeText(this, "Please download the PDF first", Toast.LENGTH_SHORT).show();
            }
        });

        // Back button
        binding.backArrow.setOnClickListener(view -> {
            finish();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        });

        // Download PDF button
        binding.DownloadButton.setOnClickListener(view -> {
            generateAndSavePDF();
        });

        // Share button
        binding.shareButton.setOnClickListener(view -> {
            shareQuestionPaper();
        });

        // Retry button
        binding.retryButton.setOnClickListener(view -> {
            // Go back to FormActivity to start over
            Intent intent = new Intent(ViewPDFActivity.this, FormActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        });
    }

    private void generateAndSavePDF() {
        try {
            int pageWidth = 595;  // A4 width in points
            int pageHeight = 842; // A4 height in points
            int marginLeft = 50;
            int marginRight = 50;
            int marginTop = 50;
            int marginBottom = 60;
            int textWidth = pageWidth - marginLeft - marginRight;
            int maxY = pageHeight - marginBottom;

            PdfDocument document = new PdfDocument();
            PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create();
            PdfDocument.Page page = document.startPage(pageInfo);
            Canvas canvas = page.getCanvas();
            int y = marginTop;

            // Draw header
            TextPaint headerPaint = new TextPaint();
            headerPaint.setTextSize(18);
            headerPaint.setFakeBoldText(true);
            headerPaint.setAntiAlias(true);

            StaticLayout headerLayout = StaticLayout.Builder
                    .obtain(paperData.getCollegeName(), 0, paperData.getCollegeName().length(), headerPaint, textWidth)
                    .setAlignment(Layout.Alignment.ALIGN_CENTER)
                    .build();
            canvas.save();
            canvas.translate(marginLeft, y);
            headerLayout.draw(canvas);
            canvas.restore();
            y += headerLayout.getHeight() + 16;

            // Draw metadata
            TextPaint metaPaint = new TextPaint();
            metaPaint.setTextSize(12);
            metaPaint.setAntiAlias(true);

            // Draw questions with text wrapping
            TextPaint questionPaint = new TextPaint();
            questionPaint.setTextSize(11);
            questionPaint.setAntiAlias(true);

            String[] lines = generatedQuestions.split("\n");
            for (String line : lines) {
                if (line.trim().isEmpty()) {
                    y += 8; // blank line spacing
                    continue;
                }

                StaticLayout lineLayout = StaticLayout.Builder
                        .obtain(line, 0, line.length(), questionPaint, textWidth)
                        .setAlignment(Layout.Alignment.ALIGN_NORMAL)
                        .setLineSpacing(2f, 1f)
                        .build();

                int lineHeight = lineLayout.getHeight();

                // Check if we need a new page
                if (y + lineHeight > maxY) {
                    document.finishPage(page);
                    page = document.startPage(pageInfo);
                    canvas = page.getCanvas();
                    y = marginTop;
                }

                canvas.save();
                canvas.translate(marginLeft, y);
                lineLayout.draw(canvas);
                canvas.restore();
                y += lineHeight + 4;
            }

            document.finishPage(page);

            // Save PDF
            String fileName = paperData.getSubjectName().replaceAll(" ", "_") + "_QuestionPaper.pdf";
            File pdfDir = new File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "AutoExamGen");
            if (!pdfDir.exists()) {
                pdfDir.mkdirs();
            }

            generatedPdfFile = new File(pdfDir, fileName);
            FileOutputStream fos = new FileOutputStream(generatedPdfFile);
            document.writeTo(fos);
            document.close();
            fos.close();

            // Update preview card with file info
            long fileSizeKB = generatedPdfFile.length() / 1024;
            binding.nestedLayout.date.setText(fileSizeKB + "KB");

            // Save report to Room DB
            saveReportToDb();

            Toast.makeText(this, "PDF saved successfully!", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            Log.e(TAG, "Error generating PDF", e);
            Toast.makeText(this, "Error creating PDF: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void saveReportToDb() {
        new Thread(() -> {
            ReportEntity report = new ReportEntity();
            report.paperName = paperData.getQuestionPaperName();
            report.subjectName = paperData.getSubjectName();
            report.filePath = generatedPdfFile.getAbsolutePath();
            report.date = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
            AppDatabase.getInstance(this).reportDao().insert(report);
        }).start();
    }

    private void openPdf() {
        try {
            Uri pdfUri = FileProvider.getUriForFile(this,
                    getPackageName() + ".fileprovider", generatedPdfFile);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(pdfUri, "application/pdf");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(intent);
        } catch (Exception e) {
            Log.e(TAG, "Error opening PDF", e);
            Toast.makeText(this, "No PDF viewer app found", Toast.LENGTH_SHORT).show();
        }
    }

    private void shareQuestionPaper() {
        if (generatedPdfFile == null) {
            // Generate PDF first if not already generated
            generateAndSavePDF();
        }

        if (generatedPdfFile != null && generatedPdfFile.exists()) {
            try {
                Uri pdfUri = FileProvider.getUriForFile(this,
                        getPackageName() + ".fileprovider", generatedPdfFile);

                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("application/pdf");
                shareIntent.putExtra(Intent.EXTRA_STREAM, pdfUri);
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, paperData.getQuestionPaperName());
                shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                startActivity(Intent.createChooser(shareIntent, "Share Question Paper"));
            } catch (Exception e) {
                Log.e(TAG, "Error sharing PDF", e);
                Toast.makeText(this, "Error sharing: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        } else {
            // Share as text if PDF not available
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, paperData.getQuestionPaperName());
            shareIntent.putExtra(Intent.EXTRA_TEXT, generatedQuestions);
            startActivity(Intent.createChooser(shareIntent, "Share via"));
        }
    }
}