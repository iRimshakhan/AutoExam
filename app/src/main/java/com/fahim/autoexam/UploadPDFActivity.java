package com.fahim.autoexam;

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

import com.fahim.autoexam.databinding.ActivityUploadPdfactivityBinding;
import com.tom_roush.pdfbox.android.PDFBoxResourceLoader;
import com.tom_roush.pdfbox.pdmodel.PDDocument;
import com.tom_roush.pdfbox.text.PDFTextStripper;

import java.io.InputStream;

public class UploadPDFActivity extends AppCompatActivity {
    ActivityUploadPdfactivityBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PDFBoxResourceLoader.init(getApplicationContext());
        EdgeToEdge.enable(this);
        binding = ActivityUploadPdfactivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        binding.backArrow.setOnClickListener(view -> {
            startActivity(new Intent(UploadPDFActivity.this, ChooseSyllabusActivity.class));
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

        binding.generateButton.setOnClickListener(view -> {
            startActivity(new Intent(UploadPDFActivity.this, GeneratingActivity.class));
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
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
        }*/
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
    */

}