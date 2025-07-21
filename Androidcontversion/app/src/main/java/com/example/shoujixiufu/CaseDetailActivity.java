package com.example.shoujixiufu;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class CaseDetailActivity extends AppCompatActivity {

    private TextView caseTitle;
    private TextView caseDate;
    private TextView caseDescription;
    private ImageView caseImage;
    private ImageButton backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_case_detail);

        // 初始化视图
        initViews();
        
        // 从Intent获取数据
        String id = getIntent().getStringExtra("case_id");
        String title = getIntent().getStringExtra("case_title");
        String date = getIntent().getStringExtra("case_date");
        String description = getIntent().getStringExtra("case_description");
        String imageUrl = getIntent().getStringExtra("case_image");
        
        // 显示案例数据
        displayCaseData(id, title, date, description, imageUrl);
        
        // 设置返回按钮点击事件
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void initViews() {
        caseTitle = findViewById(R.id.case_title);
        caseDate = findViewById(R.id.case_date);
        caseDescription = findViewById(R.id.case_description);
        caseImage = findViewById(R.id.case_image);
        backButton = findViewById(R.id.back_btn);
    }

    private void displayCaseData(String id, String title, String date, String description, String imageUrl) {
        // 设置标题和日期
        if (title != null && !title.isEmpty()) {
            caseTitle.setText(title);
        }
        
        if (date != null && !date.isEmpty()) {
            caseDate.setText(date);
        }
        
        // 处理描述中的换行符
        if (description != null && !description.isEmpty()) {
            if (description.contains("\n\n")) {
                String[] paragraphs = description.split("\n\n");
                StringBuilder formattedDescription = new StringBuilder();
                for (String paragraph : paragraphs) {
                    formattedDescription.append(paragraph).append("\n\n");
                }
                caseDescription.setText(formattedDescription.toString().trim());
            } else {
                caseDescription.setText(description);
            }
        }
        
        // 根据案例ID或imageUrl设置图片
        int imageResource = R.drawable.ic_file; // 默认图片
        
        if (id != null) {
            switch (id) {
                case "case1":
                    imageResource = R.drawable.ic_image;
                    break;
                case "case2":
                    imageResource = R.drawable.ic_sd_card;
                    break;
                case "case3":
                    imageResource = R.drawable.ic_wechat;
                    break;
                case "case4":
                    imageResource = R.drawable.ic_audio;
                    break;
            }
        } else if (imageUrl != null) {
            // 如果有图片URL，根据URL设置图片
            switch (imageUrl) {
                case "image_photo":
                    imageResource = R.drawable.ic_image;
                    break;
                case "image_sdcard":
                    imageResource = R.drawable.ic_sd_card;
                    break;
                case "image_wechat":
                    imageResource = R.drawable.ic_wechat;
                    break;
                case "image_audio":
                    imageResource = R.drawable.ic_audio;
                    break;
            }
        }
        
        // 设置图片资源
        caseImage.setImageResource(imageResource);
        caseImage.setVisibility(View.VISIBLE);
    }
} 