package com.example.shoujixiufu;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class ContactServiceActivity extends AppCompatActivity {

    private LinearLayout faqHeader1, faqHeader2, faqHeader3, faqHeader4;
    private TextView faqToggle1, faqToggle2, faqToggle3, faqToggle4;
    private TextView faqAnswer1, faqAnswer2, faqAnswer3, faqAnswer4;
    private Spinner questionTypeSpinner;
    private Dialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_service);

        // Setup toolbar
        findViewById(R.id.btn_back).setOnClickListener(v -> onBackPressed());

        // Initialize views
        setupViews();
        setupQuestionTypeSpinner();
        setupFaqToggle();
        setupContactButtons();
    }

    private void setupViews() {
        // FAQ views
        faqHeader1 = findViewById(R.id.faq_header_1);
        faqHeader2 = findViewById(R.id.faq_header_2);
        faqHeader3 = findViewById(R.id.faq_header_3);
        faqHeader4 = findViewById(R.id.faq_header_4);

        faqToggle1 = findViewById(R.id.faq_toggle_1);
        faqToggle2 = findViewById(R.id.faq_toggle_2);
        faqToggle3 = findViewById(R.id.faq_toggle_3);
        faqToggle4 = findViewById(R.id.faq_toggle_4);

        faqAnswer1 = findViewById(R.id.faq_answer_1);
        faqAnswer2 = findViewById(R.id.faq_answer_2);
        faqAnswer3 = findViewById(R.id.faq_answer_3);
        faqAnswer4 = findViewById(R.id.faq_answer_4);

        // Question form views
        questionTypeSpinner = findViewById(R.id.spinner_question_type);
        Button submitButton = findViewById(R.id.btn_submit_question);
        submitButton.setOnClickListener(v -> submitQuestion());
    }

    private void setupQuestionTypeSpinner() {
        String[] questionTypes = new String[]{
                "请选择问题类型", 
                "数据修复问题", 
                "支付问题", 
                "账户问题", 
                "其他问题"
        };
        
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                questionTypes
        );
        
        questionTypeSpinner.setAdapter(adapter);
    }

    private void setupFaqToggle() {
        faqHeader1.setOnClickListener(v -> toggleFaq(faqToggle1, faqAnswer1));
        faqHeader2.setOnClickListener(v -> toggleFaq(faqToggle2, faqAnswer2));
        faqHeader3.setOnClickListener(v -> toggleFaq(faqToggle3, faqAnswer3));
        faqHeader4.setOnClickListener(v -> toggleFaq(faqToggle4, faqAnswer4));
    }

    private void setupContactButtons() {
        // Online support button
        Button btnConsult = findViewById(R.id.btn_consult);
        btnConsult.setOnClickListener(v -> showOnlineSupport());

        // Call button
        Button btnCall = findViewById(R.id.btn_call);
        btnCall.setOnClickListener(v -> callService());

        // Email button
        Button btnSendEmail = findViewById(R.id.btn_send_email);
        btnSendEmail.setOnClickListener(v -> sendEmail());
    }

    private void toggleFaq(TextView toggle, TextView answer) {
        // Reset all toggles and answers
        faqToggle1.setText("+");
        faqToggle2.setText("+");
        faqToggle3.setText("+");
        faqToggle4.setText("+");
        faqAnswer1.setVisibility(View.GONE);
        faqAnswer2.setVisibility(View.GONE);
        faqAnswer3.setVisibility(View.GONE);
        faqAnswer4.setVisibility(View.GONE);

        // Toggle the clicked answer
        if (answer.getVisibility() == View.GONE) {
            toggle.setText("-");
            answer.setVisibility(View.VISIBLE);
        } else {
            toggle.setText("+");
            answer.setVisibility(View.GONE);
        }
    }

    private void showOnlineSupport() {
        // Create and show loading dialog
        showLoadingDialog();
        
        // Simulate loading for 2 seconds
        new android.os.Handler().postDelayed(() -> {
            dismissLoadingDialog();
            Toast.makeText(ContactServiceActivity.this, "客服小美已接入，很高兴为您服务！", Toast.LENGTH_SHORT).show();
        }, 2000);
    }

    private void callService() {
        String phoneNumber = "400888xxxx";
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + phoneNumber));
        
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Toast.makeText(this, "无法拨打电话", Toast.LENGTH_SHORT).show();
        }
    }

    private void sendEmail() {
        String emailAddress = "support@datarecovery.com";
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:" + emailAddress));
        intent.putExtra(Intent.EXTRA_SUBJECT, "数据恢复问题咨询");
        
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Toast.makeText(this, "无法发送邮件", Toast.LENGTH_SHORT).show();
        }
    }

    private void submitQuestion() {
        // Check if question type is selected
        if (questionTypeSpinner.getSelectedItemPosition() == 0) {
            Toast.makeText(this, "请选择问题类型", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Show success message
        Toast.makeText(this, "您的问题已提交，我们会尽快回复您！", Toast.LENGTH_SHORT).show();
        
        // Reset form
        questionTypeSpinner.setSelection(0);
        findViewById(R.id.et_contact_info).requestFocus();
    }

    private void showLoadingDialog() {
        loadingDialog = new Dialog(this);
        loadingDialog.setContentView(R.layout.dialog_loading);
        loadingDialog.setCancelable(false);
        if (loadingDialog.getWindow() != null) {
            loadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        loadingDialog.show();
    }

    private void dismissLoadingDialog() {
        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.dismiss();
        }
    }
} 