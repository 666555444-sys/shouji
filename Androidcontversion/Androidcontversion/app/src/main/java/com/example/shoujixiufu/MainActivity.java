package com.example.shoujixiufu;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
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

import android.util.Log;
import android.os.Handler;
import android.view.animation.AnimationUtils;

public class MainActivity extends BaseActivity implements FeatureAdapter.OnFeatureClickListener, ServiceAdapter.OnServiceClickListener {

    private RecyclerView featuresRecyclerView;
    private RecyclerView servicesRecyclerView;
    private FeatureAdapter featureAdapter;
    private ServiceAdapter serviceAdapter;
    private Dialog premiumDialog;
    private NewsTicker newsTicker;
    
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
            
            // 初始化资讯栏
            View newsTickerView = findViewById(R.id.news_ticker);
            if (newsTickerView != null) {
                newsTicker = new NewsTicker(newsTickerView);
                newsTicker.setupNewsContent();
            }
            
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
                        Toast.makeText(this, R.string.permission_granted, Toast.LENGTH_SHORT).show();
                        if (pendingIntent != null) {
                            startActivity(pendingIntent);
                            pendingIntent = null;
                        }
                    } else {
                        // 显示权限被拒绝的对话框
                        showPermissionDeniedDialog();
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
        
        // 请求需要的权限，先显示权限说明
        if (!permissionsToRequest.isEmpty()) {
            showPermissionRationaleDialog(permissionsToRequest);
        }
    }
    
    // 显示权限说明对话框
    private void showPermissionRationaleDialog(List<String> permissions) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.permission_required);
        
        // 根据需要的权限，显示相应的说明
        StringBuilder message = new StringBuilder();
        message.append(getString(R.string.storage_permission_rationale)).append("\n\n");
        
        // 对于每种权限类型添加具体说明
        if (permissions.contains(Manifest.permission.READ_MEDIA_IMAGES)) {
            message.append("• ").append(getString(R.string.permission_media_images_rationale)).append("\n");
        }
        if (permissions.contains(Manifest.permission.READ_MEDIA_VIDEO)) {
            message.append("• ").append(getString(R.string.permission_media_video_rationale)).append("\n");
        }
        if (permissions.contains(Manifest.permission.READ_MEDIA_AUDIO)) {
            message.append("• ").append(getString(R.string.permission_media_audio_rationale)).append("\n");
        }
        if (permissions.contains(Manifest.permission.READ_EXTERNAL_STORAGE) || 
            permissions.contains(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            message.append("• ").append(getString(R.string.permission_storage_rationale)).append("\n");
        }
        
        builder.setMessage(message.toString());
        builder.setPositiveButton(R.string.grant, (dialog, which) -> {
            // 请求权限
            requestPermissionLauncher.launch(permissions.toArray(new String[0]));
        });
        builder.setNegativeButton(R.string.cancel, null);
        builder.setCancelable(false);
        builder.show();
    }
    
    // 显示权限被拒绝的对话框
    private void showPermissionDeniedDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.permission_denied);
        builder.setMessage(R.string.settings_permission_message);
        builder.setPositiveButton(R.string.go_to_settings, (dialog, which) -> {
            // 打开应用设置页面
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.setData(Uri.fromParts("package", getPackageName(), null));
            startActivity(intent);
        });
        builder.setNegativeButton(R.string.cancel, null);
        builder.show();
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
        // 图片缩放和图片裁剪功能直接使用，其他功能需要会员
        String title = feature.getTitle();
        if (title.equals(getString(R.string.image_scale)) || title.equals(getString(R.string.image_crop))) {
            // 先检查存储权限
            if (hasFileAccessPermissions()) {
                // 已经有权限，直接启动Activity
                if (title.equals(getString(R.string.image_scale))) {
                    Intent intent = new Intent(this, ImageScaleActivity.class);
                    startActivity(intent);
                } else if (title.equals(getString(R.string.image_crop))) {
                    Intent intent = new Intent(this, ImageCropActivity.class);
                    startActivity(intent);
                }
            } else {
                // 保存要启动的Activity信息，并请求权限
                if (title.equals(getString(R.string.image_scale))) {
                    pendingIntent = new Intent(this, ImageScaleActivity.class);
                } else {
                    pendingIntent = new Intent(this, ImageCropActivity.class);
                }
                // 请求存储权限
                requestStoragePermissions();
            }
        } else {
            // 其他功能需要会员
            showPremiumDialog();
        }
    }

    @Override
    public void onServiceClick(ServiceModel service) {
        try {
            String serviceTitle = service.getTitle();
            
            // 文件、视频、音频修复功能可直接使用，其他服务需要会员
            if (serviceTitle.equals(getString(R.string.file_repair)) || 
                serviceTitle.equals(getString(R.string.video_repair)) || 
                serviceTitle.equals(getString(R.string.audio_repair))) {
                
                // 先检查存储权限
                if (hasFileAccessPermissions()) {
                    // 已经有权限，直接启动Activity
                    Intent intent = new Intent(this, ScanActivity.class);
                    
                    if (serviceTitle.equals(getString(R.string.file_repair))) {
                        intent.putExtra(ScanActivity.EXTRA_SCAN_TYPE, ScanActivity.SCAN_TYPE_FILE);
                    } else if (serviceTitle.equals(getString(R.string.video_repair))) {
                        intent.putExtra(ScanActivity.EXTRA_SCAN_TYPE, ScanActivity.SCAN_TYPE_VIDEO);
                    } else if (serviceTitle.equals(getString(R.string.audio_repair))) {
                        intent.putExtra(ScanActivity.EXTRA_SCAN_TYPE, ScanActivity.SCAN_TYPE_AUDIO);
                    }
                    
                    startActivity(intent);
                } else {
                    // 保存要启动的Activity信息，并请求权限
                    pendingIntent = new Intent(this, ScanActivity.class);
                    
                    if (serviceTitle.equals(getString(R.string.file_repair))) {
                        pendingIntent.putExtra(ScanActivity.EXTRA_SCAN_TYPE, ScanActivity.SCAN_TYPE_FILE);
                    } else if (serviceTitle.equals(getString(R.string.video_repair))) {
                        pendingIntent.putExtra(ScanActivity.EXTRA_SCAN_TYPE, ScanActivity.SCAN_TYPE_VIDEO);
                    } else if (serviceTitle.equals(getString(R.string.audio_repair))) {
                        pendingIntent.putExtra(ScanActivity.EXTRA_SCAN_TYPE, ScanActivity.SCAN_TYPE_AUDIO);
                    }
                    
                    // 请求存储权限
                    requestStoragePermissions();
                }
            } else {
                // 其他服务需要会员
                showPremiumDialog();
            }
        } catch (Exception e) {
            Toast.makeText(this, "页面跳转失败，请稍后重试", Toast.LENGTH_SHORT).show();
        }
    }



    // 此方法已不再使用，所有功能都通过showPremiumDialog统一处理
    private void navigateToFeatureActivity(FeatureModel feature) {
        showPremiumDialog();
    }

    // 此方法已不再使用，所有服务都通过showPremiumDialog统一处理
    private void navigateToPaymentForService(ServiceModel service) {
        showPremiumDialog();
    }
    
    // 此方法已不再使用，所有服务都通过showPremiumDialog统一处理
    private void navigateToPayment(String serviceTitle, String servicePrice) {
        showPremiumDialog();
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
        premiumDialog.setContentView(R.layout.dialog_premium_new);
        premiumDialog.setCancelable(true);
        
        // 设置对话框宽度为屏幕宽度的90%
        if (premiumDialog.getWindow() != null) {
            WindowManager.LayoutParams layoutParams = premiumDialog.getWindow().getAttributes();
            layoutParams.width = (int)(getResources().getDisplayMetrics().widthPixels * 0.9);
            premiumDialog.getWindow().setAttributes(layoutParams);
            premiumDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        // 设置关闭按钮点击事件
        ImageButton closeButton = premiumDialog.findViewById(R.id.btn_close);
        if (closeButton != null) {
            closeButton.setOnClickListener(v -> {
                v.startAnimation(AnimationUtils.loadAnimation(this, R.anim.button_click));
                premiumDialog.dismiss();
            });
        }
        
        // 设置"立即开通"按钮点击事件
        Button activateButton = premiumDialog.findViewById(R.id.btn_activate);
        if (activateButton != null) {
            activateButton.setOnClickListener(v -> {
                v.startAnimation(AnimationUtils.loadAnimation(this, R.anim.button_click));
                premiumDialog.dismiss();
                // 直接跳转到服务详情页面
                navigateToServiceDetail();
            });
        }
        
        // 设置"暂不开通"按钮点击事件
        TextView cancelButton = premiumDialog.findViewById(R.id.btn_cancel);
        if (cancelButton != null) {
            cancelButton.setOnClickListener(v -> {
                v.startAnimation(AnimationUtils.loadAnimation(this, R.anim.button_click));
                premiumDialog.dismiss();
            });
        }
    }
    
    // 新增跳转到会员中心的方法 - 已不再使用
    private void navigateToVipMembership() {
        try {
            Intent intent = new Intent(this, VipMembershipActivityNew.class);
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, "打开会员中心失败，请稍后重试", Toast.LENGTH_SHORT).show();
        }
    }
    
    // 新增跳转到服务详情页面的方法
    private void navigateToServiceDetail() {
        try {
            // 默认服务标题和价格
            String serviceTitle = "手机数据恢复服务";
            String servicePrice = "¥159";
            
            Intent intent = new Intent(this, PaymentActivity.class);
            intent.putExtra("service_title", serviceTitle);
            intent.putExtra("service_price", servicePrice);
            
            // 添加前一个页面信息（如果需要）
            if (this instanceof BaseActivity) {
                intent = ((BaseActivity)this).addPreviousActivityInfo(intent, this.getClass().getName());
            }
            
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, "打开服务详情页面失败，请稍后重试", Toast.LENGTH_SHORT).show();
        }
    }

    private void showPremiumDialog() {
        if (premiumDialog != null && !premiumDialog.isShowing()) {
            // 设置动画效果
            if (premiumDialog.getWindow() != null) {
                premiumDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
            }
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
        
        // 免费功能
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