package com.example.shoujixiufu;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ScanActivity extends AppCompatActivity {

    // 扫描类型常量
    public static final String EXTRA_SCAN_TYPE = "scan_type";
    public static final String SCAN_TYPE_FILE = "file";
    public static final String SCAN_TYPE_VIDEO = "video";
    public static final String SCAN_TYPE_AUDIO = "audio";
    
    private ImageButton btnBack;
    private FrameLayout scanningScreen;
    private ProgressBar scanProgressBar;
    private View scanCircleOuter, scanCircleMiddle, scanCircleInner;
    private TextView tvScanPercentage, tvScanFiles, tvScanningTitle, tvScanningDesc;
    private String scanType;
    private Handler handler = new Handler(Looper.getMainLooper());
    private boolean scanCompleted = false;
    private int filesFound = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        // 获取扫描类型
        scanType = getIntent().getStringExtra(EXTRA_SCAN_TYPE);
        if (scanType == null) {
            scanType = SCAN_TYPE_FILE; // 默认为文件扫描
        }

        initViews();
        setupListeners();
        updateUIBasedOnScanType();
        startScanning();
    }
    
    private void initViews() {
        btnBack = findViewById(R.id.btn_back);
        scanningScreen = findViewById(R.id.scanning_screen);
        scanProgressBar = findViewById(R.id.scan_progress_bar);
        scanCircleOuter = findViewById(R.id.scan_circle_outer);
        scanCircleMiddle = findViewById(R.id.scan_circle_middle);
        scanCircleInner = findViewById(R.id.scan_circle_inner);
        tvScanPercentage = findViewById(R.id.tv_scan_percentage);
        tvScanFiles = findViewById(R.id.tv_scan_files);
        tvScanningTitle = findViewById(R.id.scanning_title);
        tvScanningDesc = findViewById(R.id.scanning_desc);
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> showExitConfirmDialog());
    }

    private void updateUIBasedOnScanType() {
        TextView scanIcon = findViewById(R.id.scan_icon);
        
        switch (scanType) {
            case SCAN_TYPE_VIDEO:
                tvScanningTitle.setText(R.string.scanning_videos);
                scanIcon.setText("🎬");
                break;
            case SCAN_TYPE_AUDIO:
                tvScanningTitle.setText(R.string.scanning_audio);
                scanIcon.setText("🎵");
                break;
            case SCAN_TYPE_FILE:
            default:
                tvScanningTitle.setText(R.string.scanning_files);
                scanIcon.setText("📄");
                break;
        }
    }

    private void startScanning() {
        // 显示扫描屏幕
        scanningScreen.setVisibility(View.VISIBLE);
        
        // 开始动画
        startScanCircleAnimations();
        
        // 开始扫描过程
        simulateScanningProcess();
    }

    private void simulateScanningProcess() {
        new Thread(() -> {
            // 显示初始进度
            handler.post(() -> {
                scanProgressBar.setProgress(0);
                tvScanPercentage.setText("0%");
                tvScanFiles.setAlpha(0f);
            });
            
            try {
                // 根据扫描类型设置目标进度
                int targetProgress;
                switch (scanType) {
                    case SCAN_TYPE_VIDEO:
                        targetProgress = 9; // 视频扫描到9%
                        break;
                    case SCAN_TYPE_AUDIO:
                        targetProgress = 10; // 音频扫描到10%
                        break;
                    case SCAN_TYPE_FILE:
                    default:
                        targetProgress = 8; // 文件扫描到8%
                        break;
                }
                
                // 模拟进度从0%开始，增加到目标进度后停止
                for (int progress = 0; progress <= targetProgress; progress++) {
                    final int currentProgress = progress;
                    handler.post(() -> {
                        scanProgressBar.setProgress(currentProgress);
                        tvScanPercentage.setText(currentProgress + "%");
                        
                        // 在5%时模拟找到一些文件
                        if (currentProgress >= 5) {
                            filesFound = 5 + currentProgress - 5; // 5-10个文件
                            tvScanFiles.setText(getString(R.string.found_files, filesFound));
                            tvScanFiles.setAlpha(1f);
                        }
                    });
                    
                    // 每个百分比停留500毫秒
                    Thread.sleep(500);
                }
                
                // 扫描到目标进度后显示结果并导航到相应页面
                handler.post(() -> {
                    // 标记扫描完成
                    scanCompleted = true;
                    
                    // 延迟一秒后跳转到相应修复页面
                    new Handler().postDelayed(this::navigateToRepairScreen, 1000);
                });
                
            } catch (Exception e) {
                e.printStackTrace();
                handler.post(() -> {
                    scanCompleted = true;
                    navigateToRepairScreen();
                });
            }
        }).start();
    }

    private void navigateToRepairScreen() {
        Intent intent = null;
        
        switch (scanType) {
            case SCAN_TYPE_VIDEO:
                intent = new Intent(this, VideoRepairActivity.class);
                intent.putExtra("scan_percentage", 9);
                break;
            case SCAN_TYPE_AUDIO:
                intent = new Intent(this, AudioRepairActivity.class);
                intent.putExtra("scan_percentage", 10);
                break;
            case SCAN_TYPE_FILE:
            default:
                intent = new Intent(this, FileRepairActivity.class);
                intent.putExtra("scan_percentage", 8);
                break;
        }
        
        // 传递扫描完成的百分比和找到的文件数量
        intent.putExtra("files_found", filesFound);
        
        // 清除任务栈上的所有Activity，然后启动新Activity
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        
        startActivity(intent);
        finish(); // 结束当前扫描页面
    }

    private void startScanCircleAnimations() {
        // 外圆动画
        ObjectAnimator outerRotation = ObjectAnimator.ofFloat(scanCircleOuter, "rotation", 0f, 360f);
        outerRotation.setDuration(2000);
        outerRotation.setRepeatCount(ValueAnimator.INFINITE);
        outerRotation.setInterpolator(new LinearInterpolator());
        outerRotation.start();

        // 中圆动画（反方向）
        ObjectAnimator middleRotation = ObjectAnimator.ofFloat(scanCircleMiddle, "rotation", 0f, -360f);
        middleRotation.setDuration(1700);
        middleRotation.setRepeatCount(ValueAnimator.INFINITE);
        middleRotation.setInterpolator(new LinearInterpolator());
        middleRotation.start();

        // 内圆动画
        ObjectAnimator innerRotation = ObjectAnimator.ofFloat(scanCircleInner, "rotation", 0f, 360f);
        innerRotation.setDuration(1400);
        innerRotation.setRepeatCount(ValueAnimator.INFINITE);
        innerRotation.setInterpolator(new LinearInterpolator());
        innerRotation.start();
    }

    private void showExitConfirmDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        
        builder.setTitle(R.string.cancel_scan_title)
               .setMessage(R.string.cancel_scan_message)
               .setNegativeButton(R.string.no, (dialog, which) -> dialog.dismiss())
               .setPositiveButton(R.string.yes, (dialog, which) -> finish())
               .setCancelable(false)
               .show();
    }
    
    @Override
    public void onBackPressed() {
        showExitConfirmDialog();
    }
} 