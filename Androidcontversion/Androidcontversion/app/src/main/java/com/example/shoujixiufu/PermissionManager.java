package com.example.shoujixiufu;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * 权限管理器
 * 用于简化和统一应用的权限请求流程，支持顺序请求多个权限
 */
public class PermissionManager {
    private static final String TAG = "PermissionManager";
    
    private final AppCompatActivity activity;
    private final Queue<String> permissionQueue = new LinkedList<>();
    private final ActivityResultLauncher<String> requestPermissionLauncher;
    private PermissionCallback currentCallback;
    
    /**
     * 权限请求回调接口
     */
    public interface PermissionCallback {
        void onPermissionGranted();
        void onPermissionDenied();
    }
    
    /**
     * 构造方法
     * @param activity 活动上下文
     */
    public PermissionManager(AppCompatActivity activity) {
        this.activity = activity;
        
        // 注册权限请求启动器
        this.requestPermissionLauncher = activity.registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        if (currentCallback != null) {
                            currentCallback.onPermissionGranted();
                        }
                        // 处理下一个权限
                        processNextPermission();
                    } else {
                        if (currentCallback != null) {
                            currentCallback.onPermissionDenied();
                        }
                        // 权限被拒绝，显示拒绝对话框
                        showPermissionDeniedDialog();
                    }
                });
    }
    
    /**
     * 请求存储权限
     * @param callback 权限回调
     */
    public void requestStoragePermissions(PermissionCallback callback) {
        // 清空队列
        permissionQueue.clear();
        
        // 根据Android版本选择适当的权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // Android 13及以上
            permissionQueue.add(Manifest.permission.READ_MEDIA_IMAGES);
            permissionQueue.add(Manifest.permission.READ_MEDIA_VIDEO);
            permissionQueue.add(Manifest.permission.READ_MEDIA_AUDIO);
        } else { // Android 12及以下
            permissionQueue.add(Manifest.permission.READ_EXTERNAL_STORAGE);
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) { // Android 10及以下
                permissionQueue.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
        }
        
        // 设置回调
        this.currentCallback = callback;
        
        // 开始处理权限队列
        processNextPermission();
    }
    
    /**
     * 请求相机权限
     * @param callback 权限回调
     */
    public void requestCameraPermission(PermissionCallback callback) {
        // 清空队列
        permissionQueue.clear();
        
        // 添加相机权限
        permissionQueue.add(Manifest.permission.CAMERA);
        
        // 设置回调
        this.currentCallback = callback;
        
        // 开始处理权限队列
        processNextPermission();
    }
    
    /**
     * 处理队列中的下一个权限
     */
    private void processNextPermission() {
        if (permissionQueue.isEmpty()) {
            Log.d(TAG, "所有权限处理完毕");
            if (currentCallback != null) {
                currentCallback.onPermissionGranted();
            }
            return;
        }
        
        String permission = permissionQueue.peek();
        if (hasPermission(permission)) {
            // 如果已经有权限，从队列中移除并处理下一个
            permissionQueue.poll();
            processNextPermission();
        } else {
            // 显示权限说明并请求权限
            showPermissionRationaleDialog(permission);
        }
    }
    
    /**
     * 检查是否已经拥有指定权限
     * @param permission 权限
     * @return 是否拥有权限
     */
    public boolean hasPermission(String permission) {
        return ContextCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED;
    }
    
    /**
     * 显示权限说明对话框
     * @param permission 权限
     */
    private void showPermissionRationaleDialog(String permission) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("需要访问权限");
        
        // 根据权限类型显示不同的说明
        String rationale = getPermissionRationale(permission);
        
        builder.setMessage(rationale);
        builder.setPositiveButton("授予权限", (dialog, which) -> {
            // 请求权限
            requestPermissionLauncher.launch(permission);
            // 从队列中移除当前处理的权限
            permissionQueue.poll();
        });
        builder.setNegativeButton("取消", (dialog, which) -> {
            // 用户拒绝请求权限，调用回调
            if (currentCallback != null) {
                currentCallback.onPermissionDenied();
            }
            // 清空队列
            permissionQueue.clear();
        });
        builder.setCancelable(false);
        builder.show();
    }
    
    /**
     * 根据权限类型获取对应的说明文本
     * @param permission 权限
     * @return 说明文本
     */
    @NonNull
    private String getPermissionRationale(String permission) {
        switch (permission) {
            case Manifest.permission.READ_MEDIA_IMAGES:
                return activity.getString(R.string.permission_media_images_rationale);
            case Manifest.permission.READ_MEDIA_VIDEO:
                return activity.getString(R.string.permission_media_video_rationale);
            case Manifest.permission.READ_MEDIA_AUDIO:
                return activity.getString(R.string.permission_media_audio_rationale);
            case Manifest.permission.READ_EXTERNAL_STORAGE:
            case Manifest.permission.WRITE_EXTERNAL_STORAGE:
                return activity.getString(R.string.permission_storage_rationale);
            case Manifest.permission.CAMERA:
                return "需要访问相机权限才能拍摄照片";
            default:
                return activity.getString(R.string.storage_permission_rationale);
        }
    }
    
    /**
     * 显示权限被拒绝的对话框
     */
    private void showPermissionDeniedDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("权限被拒绝");
        builder.setMessage("您需要在应用设置中手动授予权限才能继续使用此功能");
        builder.setPositiveButton("前往设置", (dialog, which) -> {
            // 打开应用设置页面
            openAppSettings();
        });
        builder.setNegativeButton("取消", (dialog, which) -> {
            // 用户拒绝前往设置，处理队列中的下一个权限
            processNextPermission();
        });
        builder.show();
    }
    
    /**
     * 打开应用设置页面
     */
    private void openAppSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.fromParts("package", activity.getPackageName(), null));
        activity.startActivity(intent);
    }
} 