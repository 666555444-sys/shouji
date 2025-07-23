package com.example.shoujixiufu;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// 导入扫描活动类
import com.example.shoujixiufu.FileScanActivity;
import com.example.shoujixiufu.VideoScanActivity;
import com.example.shoujixiufu.AudioScanActivity;
import com.example.shoujixiufu.ScanActivity;

public class MainActivity extends BaseActivity implements FeatureAdapter.OnFeatureClickListener, ServiceAdapter.OnServiceClickListener {

    private RecyclerView featuresRecyclerView;
    private RecyclerView servicesRecyclerView;
    private FeatureAdapter featureAdapter;
    private ServiceAdapter serviceAdapter;
    private Dialog premiumDialog;
    
    // 用于存储待启动的意图，在权限被授予后使用
    private Intent pendingIntent;
    
    // 权限请求结果处理器
    private ActivityResultLauncher<String[]> requestPermissionLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            // 初始化权限请求结果处理器
            initializePermissionLauncher();
            
            // 请求必要的存储权限
            requestStoragePermissions();
            
            // Setup features RecyclerView
            featuresRecyclerView = findViewById(R.id.features_grid);
            if (featuresRecyclerView != null) {
                featuresRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
                List<FeatureModel> features = getFeaturesList();
                featureAdapter = new FeatureAdapter(features, this);
                featuresRecyclerView.setAdapter(featureAdapter);
            }

            // Setup services RecyclerView
            servicesRecyclerView = findViewById(R.id.other_services_grid);
            if (servicesRecyclerView != null) {
                servicesRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
                List<ServiceModel> services = getServicesList();
                serviceAdapter = new ServiceAdapter(services, this);
                servicesRecyclerView.setAdapter(serviceAdapter);
            }

            // Setup bottom navigation
            setupBottomNavigation();
            
            // 设置首页选中状态的边框
            LinearLayout navHome = findViewById(R.id.nav_home);
            if (navHome != null) {
                navHome.setBackgroundResource(R.drawable.bottom_nav_item_background);
            }

            // Initialize premium dialog
            initPremiumDialog();
            
        } catch (Exception e) {
            Toast.makeText(this, "页面初始化失败，请重新启动应用", Toast.LENGTH_SHORT).show();
        }
    }
    
    // 初始化权限请求结果处理器
    private void initializePermissionLauncher() {
        requestPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestMultiplePermissions(),
                result -> {
                    boolean allGranted = true;
                    for (Boolean granted : result.values()) {
                        if (!granted) {
                            allGranted = false;
                            break;
                        }
                    }
                    
                    if (allGranted) {
                        // 如果所有权限都被授予，并且有等待执行的Intent，则启动它
                        if (pendingIntent != null) {
                            startActivity(pendingIntent);
                            pendingIntent = null;
                        }
                    } else {
                        Toast.makeText(this, "没有足够的权限，无法访问文件", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    
    // 请求存储权限
    private void requestStoragePermissions() {
        // 根据Android版本选择适当的权限
        List<String> permissions = new ArrayList<>();
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // Android 13及以上
            permissions.add(Manifest.permission.READ_MEDIA_IMAGES);
            permissions.add(Manifest.permission.READ_MEDIA_VIDEO);
            permissions.add(Manifest.permission.READ_MEDIA_AUDIO);
        } else { // Android 12及以下
            permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) { // Android 10及以下
                permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
        }
        
        // 检查哪些权限需要请求
        List<String> permissionsToRequest = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(permission);
            }
        }
        
        // 请求需要的权限
        if (!permissionsToRequest.isEmpty()) {
            requestPermissionLauncher.launch(permissionsToRequest.toArray(new String[0]));
        }
    }
    
    // 检查是否有必要的文件访问权限
    private boolean hasFileAccessPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_VIDEO) == PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_AUDIO) == PackageManager.PERMISSION_GRANTED;
        } else {
            return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        }
    }

    @Override
    public void onFeatureClick(FeatureModel feature) {
        // 针对图片裁剪和缩放功能，直接打开相应Activity
        if (feature.getTitle().equals("图片缩放") || feature.getTitle().equals("图片裁剪")) {
            navigateToFeatureActivity(feature);
        } else {
            // 其他功能仍需开通会员
            showPremiumDialog();
        }
    }

    @Override
    public void onServiceClick(ServiceModel service) {
        try {
            String serviceTitle = service.getTitle();
            Intent intent = null;
            
            // 根据不同的服务类型，准备相应的Intent
            if (serviceTitle.equals(getString(R.string.file_repair))) {
                intent = new Intent(this, ScanActivity.class);
                intent.putExtra(ScanActivity.EXTRA_SCAN_TYPE, ScanActivity.SCAN_TYPE_FILE);
            } else if (serviceTitle.equals(getString(R.string.video_repair))) {
                intent = new Intent(this, ScanActivity.class);
                intent.putExtra(ScanActivity.EXTRA_SCAN_TYPE, ScanActivity.SCAN_TYPE_VIDEO);
            } else if (serviceTitle.equals(getString(R.string.audio_repair))) {
                intent = new Intent(this, ScanActivity.class);
                intent.putExtra(ScanActivity.EXTRA_SCAN_TYPE, ScanActivity.SCAN_TYPE_AUDIO);
            } else {
                // 其他服务仍需要开通会员
        showPremiumDialog();
                return;
            }
            
            // 检查权限，如有必要则请求权限
            if (intent != null) {
                if (hasFileAccessPermissions()) {
                    // 有权限，直接启动Activity
                    startActivity(intent);
                } else {
                    // 没有权限，保存Intent并请求权限
                    pendingIntent = intent;
                    requestStoragePermissions();
                }
            }
        } catch (Exception e) {
            Toast.makeText(this, "页面跳转失败，请稍后重试", Toast.LENGTH_SHORT).show();
        }
    }

    private void showPremiumOrPaymentDialog(FeatureModel feature) {
        try {
            if (feature.getBadgeText().equals(getString(R.string.trial))) {
                // Trial features - show payment options
                navigateToPayment(feature.getTitle(), "¥29.99");
            } else {
                // Paid features - show premium dialog
                if (premiumDialog != null && !premiumDialog.isShowing()) {
                    premiumDialog.show();
                    
                    // Add "Pay Now" button to premium dialog
                    Button activateButton = premiumDialog.findViewById(R.id.btn_activate);
                    if (activateButton != null) {
                        activateButton.setOnClickListener(v -> {
                            premiumDialog.dismiss();
                            navigateToPayment(feature.getTitle(), "¥39.99");
                        });
                    }
                }
            }
        } catch (Exception e) {
            Toast.makeText(this, "显示支付选项失败，请稍后重试", Toast.LENGTH_SHORT).show();
        }
    }

    private void navigateToFeatureActivity(FeatureModel feature) {
        try {
            Intent intent = null;
            
            switch (feature.getTitle()) {
                case "图片缩放":
                    intent = new Intent(this, ImageScaleActivity.class);
                    break;
                case "图片裁剪":
                    intent = new Intent(this, ImageCropActivity.class);
                    break;
                default:
                    Toast.makeText(this, "功能即将推出", Toast.LENGTH_SHORT).show();
                    return;
            }
            
            if (intent != null) {
                startActivity(intent);
            }
        } catch (Exception e) {
            Toast.makeText(this, "打开功能失败，请稍后重试", Toast.LENGTH_SHORT).show();
        }
    }

    private void navigateToPaymentForService(ServiceModel service) {
        try {
            String serviceTitle = service.getTitle();
            String servicePrice = "¥49.99"; // Default price
            
            // Different services can have different prices
            if (serviceTitle.equals(getString(R.string.engineer_remote_help))) {
                servicePrice = "¥99.99";
            } else if (serviceTitle.equals(getString(R.string.file_repair)) ||
                       serviceTitle.equals(getString(R.string.video_repair)) ||
                       serviceTitle.equals(getString(R.string.audio_repair))) {
                servicePrice = "¥39.90";
            }
            
            navigateToPayment(serviceTitle, servicePrice);
        } catch (Exception e) {
            Toast.makeText(this, "打开支付页面失败，请稍后重试", Toast.LENGTH_SHORT).show();
        }
    }
    
    private void navigateToPayment(String serviceTitle, String servicePrice) {
        try {
            Intent intent = new Intent(MainActivity.this, PaymentActivity.class);
            intent.putExtra("service_title", serviceTitle);
            intent.putExtra("service_price", servicePrice);
            
            // 添加前一个页面信息
            intent = addPreviousActivityInfo(intent, MainActivity.this.getClass().getName());
            
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, "页面跳转失败，请稍后再试", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupBottomNavigation() {
        try {
            LinearLayout navHome = findViewById(R.id.nav_home);
            LinearLayout navCases = findViewById(R.id.nav_cases);
            LinearLayout navOrder = findViewById(R.id.nav_order);
            LinearLayout navProfile = findViewById(R.id.nav_profile);

            // 使用BottomNavHelper设置底部导航
            BottomNavHelper.setupBottomNavigation(this, navHome, navCases, navOrder, navProfile, "home");
        } catch (Exception e) {
            Toast.makeText(this, "初始化底部导航失败", Toast.LENGTH_SHORT).show();
        }
    }

    private void initPremiumDialog() {
        premiumDialog = new Dialog(this);
        premiumDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        premiumDialog.setContentView(R.layout.dialog_premium);
        premiumDialog.setCancelable(true);

        ImageButton closeButton = premiumDialog.findViewById(R.id.btn_close);
        closeButton.setOnClickListener(v -> premiumDialog.dismiss());
        
        // 设置"立即开通"按钮点击事件
        Button activateButton = premiumDialog.findViewById(R.id.btn_activate);
        if (activateButton != null) {
            activateButton.setOnClickListener(v -> {
                premiumDialog.dismiss();
                // 跳转到会员中心页面
                navigateToVipMembership();
            });
        }
    }
    
    // 新增跳转到会员中心的方法
    private void navigateToVipMembership() {
        try {
            Intent intent = new Intent(this, VipMembershipActivity.class);
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, "打开会员中心失败，请稍后重试", Toast.LENGTH_SHORT).show();
        }
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
        
        // Free features - 现在也需要会员
        features.add(new FeatureModel(R.drawable.ic_image_scale, getString(R.string.image_scale), 
                getString(R.string.image_scale_desc), true, getString(R.string.paid)));
        
        features.add(new FeatureModel(R.drawable.ic_image_crop, getString(R.string.image_crop), 
                getString(R.string.image_crop_desc), true, getString(R.string.paid)));
        
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