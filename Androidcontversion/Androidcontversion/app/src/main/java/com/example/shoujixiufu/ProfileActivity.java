package com.example.shoujixiufu;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

// 导入需要的活动类
import com.example.shoujixiufu.PrivacyPolicyActivity;
import com.example.shoujixiufu.ServiceAgreementActivity;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // 删除返回按钮相关代码

        // 编辑资料按钮
        /*
        Button btnEditProfile = findViewById(R.id.btn_edit_profile);
        btnEditProfile.setOnClickListener(v -> {
            Toast.makeText(this, "编辑资料功能正在开发中", Toast.LENGTH_SHORT).show();
        });
        */

        // 返回主页按钮
        Button btnBackToHome = findViewById(R.id.btn_back_to_home);
        btnBackToHome.setOnClickListener(v -> {
            // 跳转到MainActivity
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });

        // 设置页面功能模块的点击事件
        setupFunctionModules();
        
        // 设置底部导航栏
        setupBottomNavigation();
    }

    // 设置底部导航栏
    private void setupBottomNavigation() {
        LinearLayout navHome = findViewById(R.id.nav_home);
        LinearLayout navCases = findViewById(R.id.nav_cases);
        LinearLayout navOrder = findViewById(R.id.nav_order);
        LinearLayout navProfile = findViewById(R.id.nav_profile);
        
        // 设置当前选中状态
        // 个人中心图标已在XML中设置为选中状态
        
        // 首页点击事件
        navHome.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });

        // 案例点击事件
        navCases.setOnClickListener(v -> {
            Intent intent = new Intent(this, CasesActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });

        // 订单点击事件
        navOrder.setOnClickListener(v -> {
            Intent intent = new Intent(this, OrderActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });

        // 个人中心点击事件 - 已在当前页面，不需要操作
        navProfile.setOnClickListener(v -> {
            // 已在个人中心页面，不做任何操作
        });
    }

    // 设置各功能模块的点击事件
    private void setupFunctionModules() {
        // 隐私政策
        LinearLayout layoutPrivacyPolicy = findViewById(R.id.layout_privacy_policy);
        layoutPrivacyPolicy.setOnClickListener(v -> {
            Intent intent = new Intent(this, PrivacyPolicyActivity.class);
                startActivity(intent);
            });

        // 服务协议
        LinearLayout layoutServiceAgreement = findViewById(R.id.layout_service_agreement);
        layoutServiceAgreement.setOnClickListener(v -> {
            Intent intent = new Intent(this, ServiceAgreementActivity.class);
                startActivity(intent);
            });

        // 语言设置
        LinearLayout layoutLanguage = findViewById(R.id.layout_language);
        layoutLanguage.setOnClickListener(v -> {
            Toast.makeText(this, "目前仅支持简体中文", Toast.LENGTH_SHORT).show();
        });

        // 意见反馈
        LinearLayout layoutFeedback = findViewById(R.id.layout_feedback);
        layoutFeedback.setOnClickListener(v -> {
                Intent intent = new Intent(this, FeedbackActivity.class);
                startActivity(intent);
            });

        // 联系客服
        LinearLayout layoutContactService = findViewById(R.id.layout_contact_service);
        layoutContactService.setOnClickListener(v -> {
                Intent intent = new Intent(this, ContactServiceActivity.class);
                startActivity(intent);
            });

        // 关于我们
        LinearLayout layoutAboutUs = findViewById(R.id.layout_about_us);
        layoutAboutUs.setOnClickListener(v -> {
            Intent intent = new Intent(this, AboutUsActivity.class);
            startActivity(intent);
        });
    }
} 