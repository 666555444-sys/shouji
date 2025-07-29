package com.example.shoujixiufu;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class FeedbackActivity extends AppCompatActivity {

    private EditText etContactInfo;
    private EditText etIssueDescription;
    private Button btnSubmit;
    private ImageButton btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        initViews();
        setupListeners();
    }

    private void initViews() {
        etContactInfo = findViewById(R.id.et_contact_info);
        etIssueDescription = findViewById(R.id.et_issue_description);
        btnSubmit = findViewById(R.id.btn_submit);
        btnBack = findViewById(R.id.btn_back);
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> navigateBack());
        
        btnSubmit.setOnClickListener(v -> {
            if (validateInputs()) {
                submitFeedback();
            }
        });
    }

    private boolean validateInputs() {
        String contactInfo = etContactInfo.getText().toString().trim();
        String issueDescription = etIssueDescription.getText().toString().trim();

        if (TextUtils.isEmpty(contactInfo)) {
            Toast.makeText(this, "请填写联系方式", Toast.LENGTH_SHORT).show();
            etContactInfo.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(issueDescription)) {
            Toast.makeText(this, "请填写问题描述", Toast.LENGTH_SHORT).show();
            etIssueDescription.requestFocus();
            return false;
        }

        return true;
    }

    private void submitFeedback() {
        // In a real app, you would send this data to a server
        // For this example, we just show a success message and navigate back
        
        Toast.makeText(this, "反馈提交成功！", Toast.LENGTH_SHORT).show();
        
        // Return to profile page
        navigateToProfile();
    }

    private void navigateBack() {
        onBackPressed();
    }

    private void navigateToProfile() {
        Intent intent = new Intent(this, MainActivity.class); // Replace with ProfileActivity if available
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }
} 