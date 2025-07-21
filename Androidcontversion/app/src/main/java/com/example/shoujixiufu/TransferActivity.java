package com.example.shoujixiufu;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class TransferActivity extends AppCompatActivity {

    private ImageButton backBtn, refreshBtn;
    private View sendOption, receiveOption;
    private TextView navBack, navHome, navTasks;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer);
        
        // 初始化视图
        initViews();
        
        // 设置点击监听器
        setupClickListeners();
    }
    
    private void initViews() {
        backBtn = findViewById(R.id.backBtn);
        refreshBtn = findViewById(R.id.refreshBtn);
        sendOption = findViewById(R.id.sendOption);
        receiveOption = findViewById(R.id.receiveOption);
        navBack = findViewById(R.id.navBack);
        navHome = findViewById(R.id.navHome);
        navTasks = findViewById(R.id.navTasks);
    }
    
    private void setupClickListeners() {
        // 返回按钮
        backBtn.setOnClickListener(v -> {
            finish();
        });
        
        // 刷新按钮
        refreshBtn.setOnClickListener(v -> {
            refreshPage();
        });
        
        // 发送选项
        sendOption.setOnClickListener(v -> {
            showSendOptions();
        });
        
        // 接收选项
        receiveOption.setOnClickListener(v -> {
            showReceiveOptions();
        });
        
        // 底部导航栏
        navBack.setOnClickListener(v -> {
            finish();
        });
        
        navHome.setOnClickListener(v -> {
            goToMainActivity();
        });
        
        navTasks.setOnClickListener(v -> {
            showTasksMessage();
        });
    }
    
    private void refreshPage() {
        // 模拟刷新页面
        Toast.makeText(this, R.string.refreshing, Toast.LENGTH_SHORT).show();
        // 实际应用中可以重新加载数据或其他刷新操作
    }
    
    private void showSendOptions() {
        // 在实际应用中，这里可能会跳转到文件选择页面
        // 这里简单用弹窗模拟
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.select_send_method);
        builder.setMessage(R.string.select_file_type);
        builder.setPositiveButton(R.string.select_file, (dialog, which) -> {
            // 跳转到文件选择页面
            Toast.makeText(this, R.string.going_to_file_selection, Toast.LENGTH_SHORT).show();
            // 在实际应用中，这里可以跳转到文件选择页面：
            // Intent intent = new Intent(this, FileSendActivity.class);
            // startActivity(intent);
        });
        builder.setNegativeButton(R.string.cancel_action, null);
        builder.show();
    }
    
    private void showReceiveOptions() {
        // 在实际应用中，这里可能会启动接收模式
        // 这里简单用弹窗模拟
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.ready_receive_files);
        builder.setMessage(R.string.device_preparing);
        builder.setPositiveButton(R.string.start_receiving, (dialog, which) -> {
            // 启动接收模式
            Toast.makeText(this, R.string.receiving_ready, Toast.LENGTH_SHORT).show();
        });
        builder.setNegativeButton(R.string.cancel_action, null);
        builder.show();
    }
    
    private void goToMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
    
    private void showTasksMessage() {
        Toast.makeText(this, R.string.task_management, Toast.LENGTH_SHORT).show();
    }
} 