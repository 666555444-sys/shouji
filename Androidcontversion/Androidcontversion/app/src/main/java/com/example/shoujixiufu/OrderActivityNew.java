package com.example.shoujixiufu;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

public class OrderActivityNew extends AppCompatActivity {

    private LinearLayout navHome, navCases, navOrder, navProfile;
    private CardView packagePermanent, packageAnnual;
    private ImageView checkPermanent, checkAnnual;
    private Button btnPayNow;
    private TextView recentPurchase;

    // 服务相关信息
    private String selectedService = "图片清除(高级版)";
    private String selectedPrice = "¥158";
    private String selectedOriginalPrice = "¥198";
    private String selectedDuration = "长期有效";
    private boolean isPermanent = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_new);

        initViews();
        setupBottomNavigation();
        setupListeners();
        updateRecentPurchase();
    }

    private void initViews() {
        // 初始化底部导航
        navHome = findViewById(R.id.nav_home);
        navCases = findViewById(R.id.nav_cases);
        navOrder = findViewById(R.id.nav_order);
        navProfile = findViewById(R.id.nav_profile);

        // 初始化套餐选择
        packagePermanent = findViewById(R.id.package_permanent);
        packageAnnual = findViewById(R.id.package_annual);
        checkPermanent = findViewById(R.id.check_permanent);
        checkAnnual = findViewById(R.id.check_annual);

        // 初始化支付按钮
        btnPayNow = findViewById(R.id.btn_pay_now);

        // 初始化最近购买通知
        recentPurchase = findViewById(R.id.recent_purchase);
    }

    private void setupBottomNavigation() {
        // 使用底部导航帮助类设置底部导航
        BottomNavHelper.setupBottomNavigation(
                this, 
                navHome, 
                navCases, 
                navOrder, 
                navProfile, 
                "order"
        );
    }

    private void setupListeners() {
        // 设置套餐选择点击事件
        packagePermanent.setOnClickListener(v -> selectPackage(true));
        packageAnnual.setOnClickListener(v -> selectPackage(false));

        // 设置支付按钮点击事件
        btnPayNow.setOnClickListener(v -> showPaymentDialog());
    }

    // 选择套餐
    private void selectPackage(boolean isPermanent) {
        this.isPermanent = isPermanent;
        
        if (isPermanent) {
            checkPermanent.setVisibility(View.VISIBLE);
            checkAnnual.setVisibility(View.GONE);
            packagePermanent.setCardElevation(8);
            packageAnnual.setCardElevation(2);
            selectedService = "图片清除(高级版)";
            selectedPrice = "¥158";
            selectedOriginalPrice = "¥198";
            selectedDuration = "长期有效";
        } else {
            checkPermanent.setVisibility(View.GONE);
            checkAnnual.setVisibility(View.VISIBLE);
            packagePermanent.setCardElevation(2);
            packageAnnual.setCardElevation(8);
            selectedService = "图片清除(一年)";
            selectedPrice = "¥78";
            selectedOriginalPrice = "¥98";
            selectedDuration = "1年有效";
        }
    }

    // 显示支付确认对话框
    private void showPaymentDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("确认支付");
        builder.setMessage("服务：" + selectedService + "\n价格：" + selectedPrice);
        builder.setPositiveButton("确认支付", (dialog, which) -> {
            processPayment();
        });
        builder.setNegativeButton("取消", null);
        builder.show();
    }

    // 处理支付
    private void processPayment() {
        // 显示处理中提示
        Toast.makeText(this, "正在处理支付...", Toast.LENGTH_SHORT).show();
        
        // 模拟支付过程
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            // 跳转到支付成功页面
            Intent intent = new Intent(OrderActivityNew.this, PaymentSuccessActivity.class);
            intent.putExtra("service_title", selectedService);
            intent.putExtra("service_price", selectedPrice);
            intent.putExtra("service_duration", selectedDuration);
            startActivity(intent);
        }, 1000);
    }

    // 更新最近购买通知
    private void updateRecentPurchase() {
        Random random = new Random();
        int phoneNumber = 130 + random.nextInt(70) * 10000 + random.nextInt(10000);
        String maskedPhone = String.valueOf(phoneNumber).substring(0, 3) + "****" + String.valueOf(phoneNumber).substring(7, 11);
        
        // 生成1-30分钟前的随机时间
        int minutesAgo = random.nextInt(30) + 1;
        
        // 根据当前时间计算购买时间
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        Date now = new Date();
        
        // 随机选择一个服务
        String[] services = {"图片清除(高级版)", "微信消息修复", "文件传输", "数据恢复"};
        String randomService = services[random.nextInt(services.length)];
        
        // 更新UI
        recentPurchase.setText("👤 " + maskedPhone + " 购买了" + randomService + " " + minutesAgo + "分钟前");
    }

    // 返回按钮点击事件
    public void onBackPressed(View view) {
        finish();
    }

    @Override
    public void onBackPressed() {
        finish();
    }
} 