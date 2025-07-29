package com.example.shoujixiufu;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SupervisionComplaintActivity extends AppCompatActivity {
    
    private Spinner complaintTypeSpinner;
    private EditText contactInput;
    private EditText orderNumberInput;
    private EditText complaintContentInput;
    private CheckBox privacyCheckbox;
    private Button submitBtn;
    private ImageButton backBtn;
    
    private String selectedComplaintType = "";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_supervision_complaint);
        
        // 初始化视图
        initViews();
        
        // 设置投诉类型下拉菜单
        setupComplaintTypeSpinner();
        
        // 设置点击事件
        setupClickListeners();
    }
    
    private void initViews() {
        complaintTypeSpinner = findViewById(R.id.complaintTypeSpinner);
        contactInput = findViewById(R.id.contactInput);
        orderNumberInput = findViewById(R.id.orderNumberInput);
        complaintContentInput = findViewById(R.id.complaintContentInput);
        privacyCheckbox = findViewById(R.id.privacyCheckbox);
        submitBtn = findViewById(R.id.submitBtn);
        backBtn = findViewById(R.id.backBtn);
    }
    
    private void setupComplaintTypeSpinner() {
        // 创建投诉类型数组
        String[] complaintTypes = {
            getString(R.string.select_complaint_type),
            getString(R.string.service_issue),
            getString(R.string.product_quality),
            getString(R.string.fee_dispute),
            getString(R.string.staff_attitude),
            getString(R.string.other_issues)
        };
        
        // 创建适配器
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
            this, 
            android.R.layout.simple_spinner_dropdown_item,
            complaintTypes
        );
        
        // 设置适配器
        complaintTypeSpinner.setAdapter(adapter);
        
        // 设置选择监听器
        complaintTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // 保存选择的投诉类型，忽略默认提示项
                if (position > 0) {
                    selectedComplaintType = complaintTypes[position];
                } else {
                    selectedComplaintType = "";
                }
            }
            
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedComplaintType = "";
            }
        });
    }
    
    private void setupClickListeners() {
        // 返回按钮点击事件
        backBtn.setOnClickListener(v -> {
            finish();
        });
        
        // 提交按钮点击事件
        submitBtn.setOnClickListener(v -> {
            submitComplaint();
        });
    }
    
    private void submitComplaint() {
        // 获取输入值
        String contactInfo = contactInput.getText().toString().trim();
        String complaintContent = complaintContentInput.getText().toString().trim();
        boolean isAgreedPrivacy = privacyCheckbox.isChecked();
        
        // 验证输入
        if (TextUtils.isEmpty(selectedComplaintType)) {
            Toast.makeText(this, R.string.please_select_type, Toast.LENGTH_SHORT).show();
            return;
        }
        
        if (TextUtils.isEmpty(contactInfo)) {
            Toast.makeText(this, R.string.please_enter_contact, Toast.LENGTH_SHORT).show();
            return;
        }
        
        if (TextUtils.isEmpty(complaintContent)) {
            Toast.makeText(this, R.string.please_enter_content, Toast.LENGTH_SHORT).show();
            return;
        }
        
        if (!isAgreedPrivacy) {
            Toast.makeText(this, R.string.please_agree_privacy, Toast.LENGTH_SHORT).show();
            return;
        }
        
        // 显示提交成功提示
        Toast.makeText(this, R.string.complaint_submitted, Toast.LENGTH_SHORT).show();
        
        // 提交成功后返回到个人页面
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);
        finish();
    }
} 