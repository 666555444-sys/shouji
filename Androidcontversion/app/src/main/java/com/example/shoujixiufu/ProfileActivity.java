package com.example.shoujixiufu;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ProfileActivity extends AppCompatActivity {

    private LinearLayout navHome, navCases, navOrder, navProfile;
    private LinearLayout profileInfo, orderHistory, settings, aboutUs, feedback, contactService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        initViews();
        setupBottomNavigation();
        setupProfileActions();
    }

    private void initViews() {
        // 初始化底部导航
        navHome = findViewById(R.id.nav_home);
        navCases = findViewById(R.id.nav_cases);
        navOrder = findViewById(R.id.nav_order);
        navProfile = findViewById(R.id.nav_profile);

        // 初始化个人中心功能项
        profileInfo = findViewById(R.id.profile_info);
        orderHistory = findViewById(R.id.order_history);
        settings = findViewById(R.id.settings);
        aboutUs = findViewById(R.id.about_us);
        feedback = findViewById(R.id.feedback);
        contactService = findViewById(R.id.contact_service);
    }

    private void setupBottomNavigation() {
        navHome.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish(); // 结束当前Activity
        });

        navCases.setOnClickListener(v -> {
            Intent intent = new Intent(this, CasesActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish(); // 结束当前Activity
        });

        navOrder.setOnClickListener(v -> {
            Intent intent = new Intent(this, OrderActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish(); // 结束当前Activity
        });

        navProfile.setOnClickListener(v -> {
            // 已经在个人中心页面
        });

        // 设置当前页面为选中状态
        updateNavigationState();
    }

    private void updateNavigationState() {
        // 重置所有导航项的状态
        resetNavigationState();
        
        // 设置个人中心页面为选中状态
        if (navProfile != null) {
            ImageView icon = navProfile.findViewById(R.id.nav_profile_icon);
            TextView text = navProfile.findViewById(R.id.nav_profile_text);
            if (icon != null) icon.setImageResource(R.drawable.ic_profile_selected);
            if (text != null) text.setTextColor(getResources().getColor(R.color.primary_color));
        }
    }

    private void resetNavigationState() {
        // 重置首页
        if (navHome != null) {
            ImageView icon = navHome.findViewById(R.id.nav_home_icon);
            TextView text = navHome.findViewById(R.id.nav_home_text);
            if (icon != null) icon.setImageResource(R.drawable.ic_home);
            if (text != null) text.setTextColor(getResources().getColor(R.color.text_secondary));
        }
        
        // 重置案例
        if (navCases != null) {
            ImageView icon = navCases.findViewById(R.id.nav_cases_icon);
            TextView text = navCases.findViewById(R.id.nav_cases_text);
            if (icon != null) icon.setImageResource(R.drawable.ic_cases);
            if (text != null) text.setTextColor(getResources().getColor(R.color.text_secondary));
        }
        
        // 重置订单
        if (navOrder != null) {
            ImageView icon = navOrder.findViewById(R.id.nav_order_icon);
            TextView text = navOrder.findViewById(R.id.nav_order_text);
            if (icon != null) icon.setImageResource(R.drawable.ic_order);
            if (text != null) text.setTextColor(getResources().getColor(R.color.text_secondary));
        }
    }

    private void setupProfileActions() {
        // 个人信息
        if (profileInfo != null) {
            profileInfo.setOnClickListener(v -> {
                // TODO: 跳转到个人信息编辑页面
                // Intent intent = new Intent(this, ProfileEditActivity.class);
                // startActivity(intent);
            });
        }

        // 订单历史
        if (orderHistory != null) {
            orderHistory.setOnClickListener(v -> {
                Intent intent = new Intent(this, OrderActivity.class);
                startActivity(intent);
            });
        }

        // 设置
        if (settings != null) {
            settings.setOnClickListener(v -> {
                // TODO: 跳转到设置页面
                // Intent intent = new Intent(this, SettingsActivity.class);
                // startActivity(intent);
            });
        }

        // 关于我们
        if (aboutUs != null) {
            aboutUs.setOnClickListener(v -> {
                Intent intent = new Intent(this, AboutUsActivity.class);
                startActivity(intent);
            });
        }

        // 意见反馈
        if (feedback != null) {
            feedback.setOnClickListener(v -> {
                Intent intent = new Intent(this, FeedbackActivity.class);
                startActivity(intent);
            });
        }

        // 联系客服
        if (contactService != null) {
            contactService.setOnClickListener(v -> {
                Intent intent = new Intent(this, ContactServiceActivity.class);
                startActivity(intent);
            });
        }
    }
} 