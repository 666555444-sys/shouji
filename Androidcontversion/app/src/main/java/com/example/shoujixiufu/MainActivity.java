package com.example.shoujixiufu;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView featuresRecyclerView;
    private RecyclerView servicesRecyclerView;
    private FeatureAdapter featureAdapter;
    private ServiceAdapter serviceAdapter;
    private Dialog premiumDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Setup features RecyclerView
        featuresRecyclerView = findViewById(R.id.features_grid);
        featuresRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        featureAdapter = new FeatureAdapter(getFeaturesList(), this::showPremiumDialog);
        featuresRecyclerView.setAdapter(featureAdapter);

        // Setup services RecyclerView
        servicesRecyclerView = findViewById(R.id.other_services_grid);
        servicesRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        serviceAdapter = new ServiceAdapter(getServicesList(), this::showPremiumDialog);
        servicesRecyclerView.setAdapter(serviceAdapter);

        // Setup bottom navigation
        setupBottomNavigation();

        // Initialize premium dialog
        initPremiumDialog();
        
        // 添加查看Logo按钮
        Button viewLogoBtn = findViewById(R.id.view_logo_btn);
        if (viewLogoBtn != null) {
            viewLogoBtn.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, LogoViewerActivity.class);
                startActivity(intent);
            });
        }
    }

    private void setupBottomNavigation() {
        LinearLayout navHome = findViewById(R.id.nav_home);
        LinearLayout navCases = findViewById(R.id.nav_cases);
        LinearLayout navOrder = findViewById(R.id.nav_order);
        LinearLayout navProfile = findViewById(R.id.nav_profile);

        navHome.setOnClickListener(v -> {
            // Already on home screen
        });

        navCases.setOnClickListener(v -> {
            Intent intent = new Intent(this, CasesActivity.class);
            startActivity(intent);
        });

        navOrder.setOnClickListener(v -> {
            Intent intent = new Intent(this, OrderActivity.class);
            startActivity(intent);
        });

        navProfile.setOnClickListener(v -> {
            Intent intent = new Intent(this, ProfileActivity.class);
            startActivity(intent);
        });
    }

    private void initPremiumDialog() {
        premiumDialog = new Dialog(this);
        premiumDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        premiumDialog.setContentView(R.layout.dialog_premium);
        premiumDialog.setCancelable(true);

        ImageButton closeButton = premiumDialog.findViewById(R.id.btn_close);
        closeButton.setOnClickListener(v -> premiumDialog.dismiss());
    }

    private void showPremiumDialog() {
        if (premiumDialog != null && !premiumDialog.isShowing()) {
            premiumDialog.show();
        }
    }

    private List<FeatureModel> getFeaturesList() {
        List<FeatureModel> features = new ArrayList<>();
        
        // Premium features
        features.add(new FeatureModel(R.drawable.ic_wechat_message, getString(R.string.wechat_message_repair), 
                getString(R.string.wechat_message_repair_desc), true, getString(R.string.trial)));
        
        features.add(new FeatureModel(R.drawable.ic_wechat_friends, getString(R.string.wechat_friend_repair), 
                getString(R.string.wechat_friend_repair_desc), true, getString(R.string.trial)));
        
        features.add(new FeatureModel(R.drawable.ic_image_repair, getString(R.string.image_repair), 
                getString(R.string.image_repair_desc), true, getString(R.string.trial)));
        
        // Free features
        features.add(new FeatureModel(R.drawable.ic_image_scale, getString(R.string.image_scale), 
                getString(R.string.image_scale_desc), false, getString(R.string.free)));
        
        features.add(new FeatureModel(R.drawable.ic_image_crop, getString(R.string.image_crop), 
                getString(R.string.image_crop_desc), false, getString(R.string.free)));
        
        // More premium features
        features.add(new FeatureModel(R.drawable.ic_file_transfer, getString(R.string.file_transfer), 
                getString(R.string.file_transfer_desc), true, getString(R.string.paid)));
        
        features.add(new FeatureModel(R.drawable.ic_call_log, getString(R.string.call_log_repair), 
                getString(R.string.call_log_repair_desc), true, getString(R.string.trial)));
        
        features.add(new FeatureModel(R.drawable.ic_sms, getString(R.string.sms_repair), 
                getString(R.string.sms_repair_desc), true, getString(R.string.trial)));
        
        return features;
    }

    private List<ServiceModel> getServicesList() {
        List<ServiceModel> services = new ArrayList<>();
        
        services.add(new ServiceModel(R.drawable.ic_file, getString(R.string.file_repair)));
        services.add(new ServiceModel(R.drawable.ic_video, getString(R.string.video_repair)));
        services.add(new ServiceModel(R.drawable.ic_audio, getString(R.string.audio_repair)));
        services.add(new ServiceModel(R.drawable.ic_image_clear, getString(R.string.image_clear)));
        services.add(new ServiceModel(R.drawable.ic_qq, getString(R.string.qq_data_repair)));
        services.add(new ServiceModel(R.drawable.ic_mobile, getString(R.string.image_clear)));
        services.add(new ServiceModel(R.drawable.ic_wechat_bill, getString(R.string.wechat_bill_repair)));
        services.add(new ServiceModel(R.drawable.ic_tiktok, getString(R.string.tiktok_data_repair)));
        services.add(new ServiceModel(R.drawable.ic_engineer, getString(R.string.engineer_remote_help)));
        
        return services;
    }
} 