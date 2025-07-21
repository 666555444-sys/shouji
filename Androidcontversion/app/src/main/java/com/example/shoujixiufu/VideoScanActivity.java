package com.example.shoujixiufu;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class VideoScanActivity extends AppCompatActivity {

    private ImageButton backBtn;
    private TextView menuBtn;
    private TextView filterTime, filterSize;
    private View scanProgressBar;
    private TextView progressText;
    private Button recoverBtn, bottomActionBtn;
    private RecyclerView videoList;
    
    // 扫描界面元素
    private View scanningScreen;
    private View outerCircle, middleCircle, innerCircle;
    private ProgressBar scanProgressBar2;
    private TextView scanPercentage, scanFiles;
    
    // 扫描状态和数据
    private boolean isScanning = false;
    private int progress = 0;
    private int filesFound = 0;
    private Timer scanTimer;
    
    // 视频数据
    private List<VideoItem> videoItems;
    private VideoAdapter videoAdapter;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_scan);
        
        // 初始化视图
        initViews();
        
        // 设置点击事件
        setupClickListeners();
        
        // 创建视频数据
        createVideoData();
        
        // 设置视频列表
        setupVideoList();
        
        // 检查是否直接开始扫描
        boolean startScan = getIntent().getBooleanExtra("startScan", false);
        if (startScan) {
            startScanning();
        } else {
            // 检查是否已完成过扫描(可以使用SharedPreferences来保持状态)
            // 为了演示，我们假设尚未扫描过
            startScanning();
        }
    }
    
    private void initViews() {
        backBtn = findViewById(R.id.backBtn);
        menuBtn = findViewById(R.id.menuBtn);
        filterTime = findViewById(R.id.filterTime);
        filterSize = findViewById(R.id.filterSize);
        scanProgressBar = findViewById(R.id.scanProgressBar);
        progressText = findViewById(R.id.progressText);
        recoverBtn = findViewById(R.id.recoverBtn);
        bottomActionBtn = findViewById(R.id.bottomActionBtn);
        videoList = findViewById(R.id.videoList);
        
        // 扫描界面元素
        scanningScreen = findViewById(R.id.scanningScreen);
        outerCircle = findViewById(R.id.outerCircle);
        middleCircle = findViewById(R.id.middleCircle);
        innerCircle = findViewById(R.id.innerCircle);
        scanProgressBar2 = findViewById(R.id.scanProgressBar2);
        scanPercentage = findViewById(R.id.scanPercentage);
        scanFiles = findViewById(R.id.scanFiles);
    }
    
    private void setupClickListeners() {
        // 返回按钮
        backBtn.setOnClickListener(v -> {
            showExitConfirmDialog();
        });
        
        // 菜单按钮
        menuBtn.setOnClickListener(v -> {
            // 这里可以实现菜单功能
        });
        
        // 筛选按钮
        filterTime.setOnClickListener(v -> {
            // 更新筛选状态
            filterTime.setBackgroundResource(R.drawable.bg_filter_selected);
            filterTime.setTextColor(getResources().getColor(R.color.blue_primary));
            
            filterSize.setBackground(null);
            filterSize.setTextColor(getResources().getColor(R.color.text_secondary));
            
            // 可以在这里实现按时间排序
        });
        
        filterSize.setOnClickListener(v -> {
            // 更新筛选状态
            filterSize.setBackgroundResource(R.drawable.bg_filter_selected);
            filterSize.setTextColor(getResources().getColor(R.color.blue_primary));
            
            filterTime.setBackground(null);
            filterTime.setTextColor(getResources().getColor(R.color.text_secondary));
            
            // 可以在这里实现按大小排序
        });
        
        // 恢复按钮
        recoverBtn.setOnClickListener(v -> {
            showRecoverDialog();
        });
        
        // 底部按钮
        bottomActionBtn.setOnClickListener(v -> {
            showRecoverDialog();
        });
    }
    
    private void createVideoData() {
        videoItems = new ArrayList<>();
        
        // 模拟视频数据
        videoItems.add(new VideoItem("家庭旅行视频.mp4", "3:42", "32MB", "昨天", "1080p"));
        videoItems.add(new VideoItem("朋友聚会.mp4", "2:18", "24MB", "7-15", "720p"));
        videoItems.add(new VideoItem("生日派对.mov", "5:07", "45MB", "7-14", "1080p"));
        
        // 可以添加更多视频数据
    }
    
    private void setupVideoList() {
        videoAdapter = new VideoAdapter(videoItems);
        videoList.setLayoutManager(new LinearLayoutManager(this));
        videoList.setAdapter(videoAdapter);
    }
    
    private void startScanning() {
        isScanning = true;
        
        // 显示扫描界面
        scanningScreen.setVisibility(View.VISIBLE);
        
        // 开始旋转动画
        startCircleAnimations();
        
        // 使用定时器模拟扫描进度
        scanTimer = new Timer();
        scanTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(() -> updateScanProgress());
            }
        }, 800, 300); // 延迟800毫秒后，每300毫秒更新一次
    }
    
    private void startCircleAnimations() {
        // 外圆顺时针旋转
        RotateAnimation outerAnim = new RotateAnimation(0f, 360f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        outerAnim.setDuration(2000);
        outerAnim.setRepeatCount(Animation.INFINITE);
        outerAnim.setInterpolator(new LinearInterpolator());
        outerCircle.startAnimation(outerAnim);
        
        // 中圆逆时针旋转
        RotateAnimation middleAnim = new RotateAnimation(0f, -360f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        middleAnim.setDuration(1700);
        middleAnim.setRepeatCount(Animation.INFINITE);
        middleAnim.setInterpolator(new LinearInterpolator());
        middleCircle.startAnimation(middleAnim);
        
        // 内圆顺时针旋转
        RotateAnimation innerAnim = new RotateAnimation(0f, 360f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        innerAnim.setDuration(1400);
        innerAnim.setRepeatCount(Animation.INFINITE);
        innerAnim.setInterpolator(new LinearInterpolator());
        innerCircle.startAnimation(innerAnim);
    }
    
    private void stopCircleAnimations() {
        outerCircle.clearAnimation();
        middleCircle.clearAnimation();
        innerCircle.clearAnimation();
    }
    
    private void updateScanProgress() {
        // 增加进度
        progress += Math.random() * 0.7;
        
        // 进度最多到8%
        int targetProgress = 8;
        if (progress > targetProgress) {
            progress = targetProgress;
        }
        
        // 更新进度条
        scanProgressBar2.setProgress((int) progress);
        scanPercentage.setText((int) progress + "%");
        
        // 模拟发现文件
        if (Math.random() > 0.7) {
            filesFound += Math.floor(Math.random() * 3) + 1;
            scanFiles.setText(getString(R.string.files_found, filesFound));
            
            if (scanFiles.getAlpha() < 1) {
                // 显示文件计数
                ObjectAnimator.ofFloat(scanFiles, "alpha", 0f, 1f)
                        .setDuration(500)
                        .start();
            }
        }
        
        // 达到目标进度后完成扫描
        if (progress >= targetProgress) {
            completeScan();
        }
    }
    
    private void completeScan() {
        if (scanTimer != null) {
            scanTimer.cancel();
            scanTimer = null;
        }
        
        // 延迟一会儿以显示最终进度
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            // 隐藏扫描界面
            Animation fadeOut = new AlphaAnimation(1f, 0f);
            fadeOut.setDuration(500);
            fadeOut.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {}
                
                @Override
                public void onAnimationEnd(Animation animation) {
                    scanningScreen.setVisibility(View.GONE);
                    stopCircleAnimations();
                    showScanResults();
                }
                
                @Override
                public void onAnimationRepeat(Animation animation) {}
            });
            scanningScreen.startAnimation(fadeOut);
        }, 1000);
    }
    
    private void showScanResults() {
        // 显示进度条
        scanProgressBar.setVisibility(View.VISIBLE);
        
        // 设置进度文本
        progressText.setText(getString(R.string.scanned_percent, 8));
        
        // 显示底部按钮
        bottomActionBtn.setVisibility(View.VISIBLE);
        
        // 自动显示弹窗(第一次进入时)
        // 使用Handler延迟一点时间以避免UI卡顿
        new Handler(Looper.getMainLooper()).postDelayed(this::showRecoverDialog, 500);
    }
    
    private void showExitConfirmDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.are_you_sure_return);
        builder.setMessage(R.string.rescan_return_note);
        builder.setPositiveButton(R.string.confirm_action, (dialog, which) -> {
            finish();
        });
        builder.setNegativeButton(R.string.cancel_action, null);
        builder.show();
    }
    
    private void showRecoverDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.scanned_percent, 8));
        builder.setMessage("目前只扫描到8%，您如需要开放「深层扫描」查看全部数据，请点击「去恢复」按钮。");
        builder.setPositiveButton(R.string.recover_immediately, (dialog, which) -> {
            goToPayment();
        });
        builder.setNegativeButton(R.string.cancel_action, null);
        builder.show();
    }
    
    private void goToPayment() {
        // 跳转到支付页面
        Intent intent = new Intent(this, PaymentActivity.class);
        intent.putExtra("returnTo", "video-scan.html");
        startActivity(intent);
    }
    
    @Override
    public void onBackPressed() {
        if (isScanning) {
            showExitConfirmDialog();
        } else {
            super.onBackPressed();
        }
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (scanTimer != null) {
            scanTimer.cancel();
            scanTimer = null;
        }
    }
    
    // 视频项模型
    private static class VideoItem {
        private String name;
        private String duration;
        private String size;
        private String date;
        private String resolution;
        private boolean isSelected = false;
        
        public VideoItem(String name, String duration, String size, String date, String resolution) {
            this.name = name;
            this.duration = duration;
            this.size = size;
            this.date = date;
            this.resolution = resolution;
        }
    }
    
    // 视频列表适配器
    private class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoViewHolder> {
        
        private List<VideoItem> videoItems;
        
        public VideoAdapter(List<VideoItem> videoItems) {
            this.videoItems = videoItems;
        }
        
        @NonNull
        @Override
        public VideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_video, parent, false);
            return new VideoViewHolder(view);
        }
        
        @Override
        public void onBindViewHolder(@NonNull VideoViewHolder holder, int position) {
            VideoItem item = videoItems.get(position);
            
            holder.videoName.setText(item.name);
            holder.videoDuration.setText(item.duration);
            holder.videoDate.setText(item.date);
            holder.videoSize.setText(item.size);
            holder.videoResolution.setText(item.resolution);
            
            // 设置点击事件
            holder.itemView.setOnClickListener(v -> {
                // 可以在这里实现点击视频项的处理
            });
            
            // 设置选择框状态
            holder.videoCheckbox.setSelected(item.isSelected);
            holder.videoCheckbox.setOnClickListener(v -> {
                item.isSelected = !item.isSelected;
                holder.videoCheckbox.setSelected(item.isSelected);
            });
            
            // 使用动画效果
            setFadeAnimation(holder.itemView, position);
        }
        
        @Override
        public int getItemCount() {
            return videoItems.size();
        }
        
        // 为列表项添加淡入动画
        private void setFadeAnimation(View view, int position) {
            Animation animation = new AlphaAnimation(0, 1);
            animation.setDuration(500);
            animation.setStartOffset(position * 100); // 设置延迟
            animation.setInterpolator(new DecelerateInterpolator());
            view.startAnimation(animation);
        }
        
        // 视图持有者
        class VideoViewHolder extends RecyclerView.ViewHolder {
            TextView videoName;
            TextView videoDuration;
            TextView videoDate;
            TextView videoSize;
            TextView videoResolution;
            View videoCheckbox;
            
            public VideoViewHolder(@NonNull View itemView) {
                super(itemView);
                videoName = itemView.findViewById(R.id.videoName);
                videoDuration = itemView.findViewById(R.id.videoDuration);
                videoDate = itemView.findViewById(R.id.videoDate);
                videoSize = itemView.findViewById(R.id.videoSize);
                videoResolution = itemView.findViewById(R.id.videoResolution);
                videoCheckbox = itemView.findViewById(R.id.videoCheckbox);
            }
        }
    }
} 