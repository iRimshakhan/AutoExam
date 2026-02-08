/*package com.fahim.autoexam;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.fahim.autoexam.databinding.ActivityFormBinding;

import java.util.Calendar;

public class FormActivity extends AppCompatActivity {
    ActivityFormBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityFormBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        String[] questionTypes = {
                "Select question type",
                "MCQ",
                "Short Answer",
                "Long Answer"
        };
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, questionTypes);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerQuestionType.setAdapter(adapter);
        binding.spinnerQuestionType.setSelection(0);

        binding.spinnerQuestionType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    // Placeholder selected - ignore
                } else {
                    String selected = parent.getItemAtPosition(position).toString();
                    Toast.makeText(FormActivity.this, "Selected: " + selected, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        binding.EditDate.setOnClickListener(view -> {
            final Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(FormActivity.this, (view1, year1, monthOfYear, dayOfMonth) -> {
                        // Set selected date to EditText
                        String selectedDate = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year1;
                        binding.EditDate.setText(selectedDate);
                    }, year, month, day);
            datePickerDialog.show();
        });

        binding.NextButton.setOnClickListener(view -> {
            if (binding.spinnerQuestionType.getSelectedItemPosition() == 0) {
                Toast.makeText(FormActivity.this, "Select question type", Toast.LENGTH_SHORT).show();
            } else {
                startActivity(new Intent(FormActivity.this, ChooseSyllabusActivity.class));
            }
        });

        binding.backArrow.setOnClickListener(view -> {
            startActivity(new Intent(FormActivity.this, HomeActivity.class));
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);

        });

    }
}

*/

package com.fahim.autoexam;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.fahim.autoexam.databinding.ActivityFormBinding;

import java.util.Calendar;

public class FormActivity extends AppCompatActivity {
    ActivityFormBinding binding;
    private QuestionPaperData paperData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityFormBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        paperData = new QuestionPaperData();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        String[] questionTypes = {
                "Select question type",
                "MCQ",
                "Short Answer",
                "Long Answer"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, questionTypes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerQuestionType.setAdapter(adapter);
        binding.spinnerQuestionType.setSelection(0);

        binding.spinnerQuestionType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    String selected = parent.getItemAtPosition(position).toString();
                    paperData.setQuestionType(selected);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        binding.EditDate.setOnClickListener(view -> {
            final Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(FormActivity.this,
                    (view1, year1, monthOfYear, dayOfMonth) -> {
                        String selectedDate = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year1;
                        binding.EditDate.setText(selectedDate);
                        paperData.setDate(selectedDate);
                    }, year, month, day);
            datePickerDialog.show();
        });

        binding.NextButton.setOnClickListener(view -> {
            if (validateInputs()) {
                // Save all form data
                paperData.setClassName(binding.ClassText.getText().toString().trim());
                paperData.setSubjectName(binding.EditNameOfSub.getText().toString().trim());
                paperData.setQuestionPaperName(binding.EditNameOfQP.getText().toString().trim());
                paperData.setNoOfQuestionsPerUnit(binding.NoOfQuesUnit.getText().toString().trim());
                paperData.setCollegeName(binding.EditNameOfClg.getText().toString().trim());
                paperData.setMarks(binding.EditMarks.getText().toString().trim());
                paperData.setDuration(binding.EditDuration.getText().toString().trim());

                // Pass data to next activity
                Intent intent = new Intent(FormActivity.this, ChooseSyllabusActivity.class);
                intent.putExtra("paperData", paperData);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });

        binding.backArrow.setOnClickListener(view -> {
            startActivity(new Intent(FormActivity.this, HomeActivity.class));
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        });
    }

    private boolean validateInputs() {
        if (binding.ClassText.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Please enter class", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (binding.EditNameOfSub.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Please enter subject name", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (binding.EditNameOfQP.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Please enter question paper name", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (binding.spinnerQuestionType.getSelectedItemPosition() == 0) {
            Toast.makeText(this, "Please select question type", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (binding.NoOfQuesUnit.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Please enter number of questions", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (binding.EditMarks.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Please enter total marks", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (binding.EditDuration.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Please enter duration", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}