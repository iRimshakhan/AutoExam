/*package com.fahim.autoexam;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.OpenableColumns;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.fahim.autoexam.databinding.ActivityChooseSyllabusBinding;
import com.tom_roush.pdfbox.android.PDFBoxResourceLoader;
import com.tom_roush.pdfbox.pdmodel.PDDocument;
import com.tom_roush.pdfbox.text.PDFTextStripper;

import java.io.InputStream;

public class ChooseSyllabusActivity extends AppCompatActivity {
    private static final String TAG = ChooseSyllabusActivity.class.getName();
    ActivityChooseSyllabusBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.NoAnimationTheme);
        super.onCreate(savedInstanceState);
        PDFBoxResourceLoader.init(getApplicationContext());
        binding = ActivityChooseSyllabusBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        EdgeToEdge.enable(this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        binding.backArrow.setOnClickListener(view -> {
            startActivity(new Intent(ChooseSyllabusActivity.this, FormActivity.class));
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });
        binding.chooseFile.setOnClickListener(view -> {

            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("application/pdf");
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            startActivityForResult(Intent.createChooser(intent, "Select PDF"), 1);
//            startActivity(new Intent(ChooseSyllabusActivity.this, UploadPDFActivity.class));
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            Uri pdfUri = data.getData();
            Log.e(TAG, "uri: "+pdfUri.toString());
            readPDFContent(pdfUri);  // You'll create this function
            String pdfName = getFileNameFromUri(pdfUri);
            Intent intent = new Intent(ChooseSyllabusActivity.this, UploadPDFActivity.class);
            //overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            intent.putExtra("pdfUri", pdfUri.toString()); // This is correct
            intent.putExtra("pdfName", pdfName);          // This too
            startActivity(intent);

        }
    }

    private String getFileNameFromUri(Uri uri) {
        String result = null;
        if ("content".equals(uri.getScheme())) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            if (cursor != null) {
                try {
                    if (cursor.moveToFirst()) {
                        int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                        if (nameIndex != -1) {
                            result = cursor.getString(nameIndex);
                        }
                    }
                } finally {
                    cursor.close();
                }
            }
        }
        if (result == null) {
            result = uri.getLastPathSegment();
        }
        return result;
    }



    public void readPDFContent(Uri uri) {
        try {
            ContentResolver contentResolver = getContentResolver();
            InputStream inputStream = contentResolver.openInputStream(uri);
            PDDocument document = PDDocument.load(inputStream);
            // ParcelFileDescriptor fileDescriptor = getContentResolver().openFileDescriptor(uri, "r");
            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(document);
            document.close();
            Log.d("PDF_CONTENT", text);  // Or display in a TextView
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
*/
package com.fahim.autoexam;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.fahim.autoexam.databinding.ActivityChooseSyllabusBinding;
import com.tom_roush.pdfbox.pdmodel.PDDocument;
import com.tom_roush.pdfbox.text.PDFTextStripper;

import java.io.InputStream;

public class ChooseSyllabusActivity extends AppCompatActivity {
    private static final String TAG = ChooseSyllabusActivity.class.getName();
    ActivityChooseSyllabusBinding binding;
    ActivityResultLauncher<Intent> launcher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult o) {
                        Intent data = o.getData();
                        if (o.getResultCode() == RESULT_OK) {
                            Uri pdfUri = data.getData();
                            Log.e(TAG, "PDF URI: " + pdfUri);
//                            readPDFContent(pdfUri);
                            String pdfName = getFileNameFromUri(pdfUri);

                            Intent intent = new Intent(ChooseSyllabusActivity.this, UploadPDFActivity.class);
                            intent.putExtra("pdfUri", pdfUri.toString());
                            intent.putExtra("pdfName", pdfName);
                            startActivity(intent);
                        }
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
            startActivity(new Intent(this, FormActivity.class));
        });

        binding.chooseFile.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("application/pdf");
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            launcher.launch(Intent.createChooser(intent,"PDF"));
//            startActivityForResult(Intent.createChooser(intent, "Select PDF"), 1);
        });
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