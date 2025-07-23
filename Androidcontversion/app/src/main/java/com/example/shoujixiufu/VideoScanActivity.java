package com.example.shoujixiufu;

import android.animation.ObjectAnimator;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
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

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Collections;

public class VideoScanActivity extends AppCompatActivity {

    private ImageButton backBtn;
    private TextView menuBtn;
    // 删除时间、大小过滤器
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
        
        // 隐藏右上角目录按钮
        if (menuBtn != null) {
            menuBtn.setVisibility(View.GONE);
        }
        
        // 使用正确的ID
        scanProgressBar = findViewById(R.id.scanProgressBar);
        progressText = findViewById(R.id.progressText);
        recoverBtn = findViewById(R.id.recoverBtn);
        bottomActionBtn = findViewById(R.id.bottomActionBtn);
        
        // 视频列表
        videoList = findViewById(R.id.videoList);
        
        // 扫描界面
        scanningScreen = findViewById(R.id.scanningScreen);
        scanProgressBar2 = findViewById(R.id.scanProgressBar2);
        scanPercentage = findViewById(R.id.scanPercentage);
        scanFiles = findViewById(R.id.scanFiles);
        
        // 扫描圆圈
        outerCircle = findViewById(R.id.outerCircle);
        middleCircle = findViewById(R.id.middleCircle);
        innerCircle = findViewById(R.id.innerCircle);

        // 设置时间过滤器点击事件
        TextView filterTime = findViewById(R.id.filterTime);
        if (filterTime != null) {
        filterTime.setOnClickListener(v -> {
                // 按时间排序视频
                if (videoAdapter != null && videoItems != null) {
                    List<VideoItem> sortedByTime = new ArrayList<>(videoItems);
                    Collections.sort(sortedByTime, (v1, v2) -> v2.date.compareTo(v1.date));
                    videoAdapter = new VideoAdapter(sortedByTime);
                    videoList.setAdapter(videoAdapter);
                    
                    // 更新过滤器样式
            filterTime.setBackgroundResource(R.drawable.bg_filter_selected);
            filterTime.setTextColor(getResources().getColor(R.color.blue_primary));
            
                    TextView filterSize = findViewById(R.id.filterSize);
                    if (filterSize != null) {
            filterSize.setBackground(null);
            filterSize.setTextColor(getResources().getColor(R.color.text_secondary));
                    }
                }
        });
        }
        
        // 设置大小过滤器点击事件
        TextView filterSize = findViewById(R.id.filterSize);
        if (filterSize != null) {
        filterSize.setOnClickListener(v -> {
                // 按大小排序视频
                if (videoAdapter != null && videoItems != null) {
                    List<VideoItem> sortedBySize = new ArrayList<>(videoItems);
                    Collections.sort(sortedBySize, (v1, v2) -> {
                        try {
                            // 从 "100MB" 格式解析
                            String s1 = v1.size.replaceAll("[^0-9.]", "");
                            String s2 = v2.size.replaceAll("[^0-9.]", "");
                            float f1 = Float.parseFloat(s1);
                            float f2 = Float.parseFloat(s2);
                            
                            // 考虑单位
                            if (v1.size.contains("GB")) f1 *= 1024;
                            if (v2.size.contains("GB")) f2 *= 1024;
                            
                            return Float.compare(f2, f1);
                        } catch (Exception e) {
                            return 0;
                        }
                    });
                    videoAdapter = new VideoAdapter(sortedBySize);
                    videoList.setAdapter(videoAdapter);
                    
                    // 更新过滤器样式
            filterSize.setBackgroundResource(R.drawable.bg_filter_selected);
            filterSize.setTextColor(getResources().getColor(R.color.blue_primary));
            
                    TextView timeFilter = findViewById(R.id.filterTime);
                    if (timeFilter != null) {
                        timeFilter.setBackground(null);
                        timeFilter.setTextColor(getResources().getColor(R.color.text_secondary));
                    }
                }
            });
        }
    }
    
    private void setupClickListeners() {
        // Back button
        backBtn.setOnClickListener(v -> showExitConfirmDialog());
        
        // Menu button
        menuBtn.setOnClickListener(v -> {
            // Show menu options if needed
        });
        
        // 删除过滤器点击监听器
        
        // Recover button
        recoverBtn.setOnClickListener(v -> showRecoverDialog());
        
        // Bottom action button
        bottomActionBtn.setOnClickListener(v -> showRecoverDialog());
    }
    
    private void createVideoData() {
        // 创建默认视频数据样本
        videoItems = createDefaultVideoData();
    }
    
    private void setupVideoList() {
        videoAdapter = new VideoAdapter(videoItems);
        videoList.setAdapter(videoAdapter);
    }
    
    private void startScanning() {
        isScanning = true;
        
        // 显示扫描界面
        scanningScreen.setVisibility(View.VISIBLE);
        
        // 开始旋转动画
        startCircleAnimations();
        
        // 扫描本地视频文件
        scanLocalVideos();
    }
    
    // 扫描本地视频文件 - 不再区分目录
    private void scanLocalVideos() {
        new Thread(() -> {
            try {
                // 显示初始进度
                runOnUiThread(() -> {
                    scanProgressBar2.setProgress(0);
                    scanPercentage.setText("0%");
                    scanFiles.setText(getString(R.string.scanning_video_desc));
                    scanFiles.setAlpha(1f);
                });
                
                // 先创建视频列表，在扫描过程中填充
                List<VideoItem> videos = new ArrayList<>();
                
                // 模拟进度从0%开始，只增加到10%
                for(int progress = 0; progress <= 10; progress += 2) {
                    final int currentProgress = progress;
                    runOnUiThread(() -> {
                        scanProgressBar2.setProgress(currentProgress);
                        scanPercentage.setText(currentProgress + "%");
                        
                        // 当进度达到一定程度，更新找到的视频数量
                        if(currentProgress > 5) {
                            filesFound = videos.size() > 0 ? videos.size() : 3;
                            scanFiles.setText(getString(R.string.files_found, filesFound));
                        }
                    });
                    
                    // 如果进度到达5%，开始获取视频
                    if(progress == 5) {
                        // 获取部分本地视频
                        List<VideoItem> localVideos = getLocalVideos();
                        if (localVideos.size() > 0) {
                            videos.addAll(localVideos.subList(0, Math.min(5, localVideos.size())));
                            filesFound = videos.size();
                        }
                    }
                    
                    Thread.sleep(250);
                }
                
                // 扫描到10%时停止并显示结果
                final List<VideoItem> finalVideos = videos.isEmpty() ? createDefaultVideoData() : videos;
                
                runOnUiThread(() -> {
                    scanProgressBar2.setProgress(10);
                    scanPercentage.setText("10%");
                    filesFound = finalVideos.size();
                    scanFiles.setText(getString(R.string.files_found, filesFound));
                    
                    // 延迟500ms后隐藏扫描屏幕，显示结果
                    new Handler(Looper.getMainLooper()).postDelayed(() -> {
                        // 隐藏扫描屏幕
                        Animation fadeOut = new AlphaAnimation(1f, 0f);
                        fadeOut.setDuration(300);
                        fadeOut.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {}
                            
                            @Override
                            public void onAnimationEnd(Animation animation) {
                                // 扫描完成，隐藏扫描屏幕
                                scanningScreen.setVisibility(View.GONE);
                                stopCircleAnimations();
                                
                                // 显示扫描结果
                                showScanResults(finalVideos);
                            }
                            
            @Override
                            public void onAnimationRepeat(Animation animation) {}
                        });
                        scanningScreen.startAnimation(fadeOut);
                    }, 500);
                });
                
            } catch (Exception e) {
                e.printStackTrace();
                
                // 如果发生错误，显示默认数据
                runOnUiThread(() -> {
                    scanningScreen.setVisibility(View.GONE);
                    stopCircleAnimations();
                    showScanResults(createDefaultVideoData());
                });
            }
        }).start();
    }
    
    // 修改获取本地视频方法 - 不再区分目录
    private List<VideoItem> getLocalVideos() {
        List<VideoItem> videoList = new ArrayList<>();
        
        try {
            // 查询系统媒体库中的视频文件
            ContentResolver contentResolver = getContentResolver();
            Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
            
            // 创建与Android版本兼容的投影
            List<String> projectionList = new ArrayList<>();
            projectionList.add(MediaStore.Video.Media.DISPLAY_NAME);
            projectionList.add(MediaStore.Video.Media.SIZE);
            projectionList.add(MediaStore.Video.Media.DATE_MODIFIED);
            projectionList.add(MediaStore.Video.Media.DURATION);
            
            // 有条件地添加RESOLUTION (API 29+ 才有)
            boolean hasResolution = false;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                try {
                    // 反射检查字段是否存在
                    MediaStore.Video.Media.class.getField("RESOLUTION");
                    projectionList.add(MediaStore.Video.Media.RESOLUTION);
                    hasResolution = true;
                } catch (NoSuchFieldException e) {
                    // 字段不存在，不添加到投影中
                }
            }
            
            String[] projection = projectionList.toArray(new String[0]);
            
            Cursor cursor = contentResolver.query(uri, projection, null, null, null);
            if (cursor != null) {
                // 获取列索引，以防字段顺序不一致
                int nameIndex = cursor.getColumnIndex(MediaStore.Video.Media.DISPLAY_NAME);
                int sizeIndex = cursor.getColumnIndex(MediaStore.Video.Media.SIZE);
                int dateIndex = cursor.getColumnIndex(MediaStore.Video.Media.DATE_MODIFIED);
                int durationIndex = cursor.getColumnIndex(MediaStore.Video.Media.DURATION);
                int resolutionIndex = hasResolution ? 
                        cursor.getColumnIndex(MediaStore.Video.Media.RESOLUTION) : -1;
                
                while (cursor.moveToNext() && videoList.size() < 20) { // 限制最多20个视频
                    String name = nameIndex >= 0 ? cursor.getString(nameIndex) : "未知";
                    long size = sizeIndex >= 0 ? cursor.getLong(sizeIndex) : 0;
                    long date = dateIndex >= 0 ? cursor.getLong(dateIndex) * 1000 : 0; // 转换为毫秒
                    long duration = durationIndex >= 0 ? cursor.getLong(durationIndex) : 0;
                    
                    // 安全获取分辨率
                    String resolution = "未知";
                    if (resolutionIndex >= 0) {
                        String resValue = cursor.getString(resolutionIndex);
                        resolution = (resValue != null) ? resValue : "未知";
                    }
                    
                    VideoItem video = new VideoItem(
                            name,
                            formatDuration(duration),
                            formatSize(size),
                            formatDate(date),
                            resolution
                    );
                    videoList.add(video);
                }
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            videoList = createDefaultVideoData();
        }
        
        // 如果找不到视频，使用默认数据
        if (videoList.isEmpty()) {
            videoList = createDefaultVideoData();
        }
        
        return videoList;
    }
    
    // 删除从目录获取视频文件的方法，因为我们直接使用MediaStore

    private String formatSize(long size) {
        if (size <= 0) return "0B";
        final String[] units = new String[]{"B", "KB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + units[digitGroups];
    }
    
    private String formatDuration(long milliseconds) {
        if (milliseconds <= 0) return "0:00";
        
        long seconds = milliseconds / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        
        seconds %= 60;
        minutes %= 60;
        
        if (hours > 0) {
            return String.format(Locale.getDefault(), "%d:%02d:%02d", hours, minutes, seconds);
        } else {
            return String.format(Locale.getDefault(), "%d:%02d", minutes, seconds);
        }
    }
    
    private String formatDate(long timeMillis) {
        Date date = new Date(timeMillis);
        Calendar calendar = Calendar.getInstance();
        Calendar today = Calendar.getInstance();
        calendar.setTime(date);
        
        if (calendar.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
                calendar.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR)) {
            return "今天";
        } else if (calendar.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
                calendar.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR) - 1) {
            return "昨天";
        } else {
            SimpleDateFormat sdf = new SimpleDateFormat("MM-dd", Locale.getDefault());
            return sdf.format(date);
        }
    }
    
    private List<VideoItem> createDefaultVideoData() {
        List<VideoItem> videos = new ArrayList<>();
        videos.add(new VideoItem("家庭旅行视频.mp4", "3:42", "32MB", "昨天", "1080p"));
        videos.add(new VideoItem("朋友聚会.mp4", "2:18", "24MB", "7-15", "720p"));
        videos.add(new VideoItem("生日派对.mov", "5:07", "45MB", "7-14", "1080p"));
        return videos;
    }
    
    private void showScanResults(List<VideoItem> videos) {
        // 显示视频列表
        videoItems = videos;
        videoAdapter = new VideoAdapter(videos);
        videoList.setAdapter(videoAdapter);
        videoList.setLayoutManager(new LinearLayoutManager(this));
        
        // 显示进度条和底部按钮
        scanProgressBar.setVisibility(View.VISIBLE);
        bottomActionBtn.setVisibility(View.VISIBLE);
        
        // 设置进度文本显示为10%
        if (progressText != null) {
            progressText.setText(getString(R.string.scanned_percent, 10));
        }
        
        // 设置按钮点击事件
        recoverBtn.setOnClickListener(v -> showRecoverDialog());
        bottomActionBtn.setOnClickListener(v -> showRecoverDialog());
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
    
    // 删除updateScanProgress方法
    // 删除completeScan方法，因为scanLocalVideos已经处理了扫描完成逻辑
    
    // 退出确认对话框
    private void showExitConfirmDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        
        if (scanningScreen.getVisibility() == View.VISIBLE) {
            // 正在扫描中的退出提示
            builder.setTitle(getString(R.string.cancel_scan_title))
                    .setMessage(getString(R.string.cancel_scan_message))
                    .setPositiveButton(getString(R.string.yes), (dialog, which) -> finish())
                    .setNegativeButton(getString(R.string.no), (dialog, which) -> dialog.dismiss());
        } else {
            // 扫描完成后的退出提示
            builder.setTitle(R.string.exit_service_title)
                    .setMessage(R.string.exit_service_message)
                    .setPositiveButton(R.string.confirm_exit, (dialog, which) -> finish())
                    .setNegativeButton(R.string.continue_service, (dialog, which) -> dialog.dismiss());
        }
        builder.setCancelable(false).show();
    }
    
    // 恢复对话框
    private void showRecoverDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.recovery_dialog_title)
                .setMessage(R.string.recovery_dialog_content)
                .setNegativeButton(R.string.cancel, (dialog, which) -> dialog.dismiss())
                .setPositiveButton(R.string.go_to_recover, (dialog, which) -> goToPayment())
                .setCancelable(false)
                .show();
    }
    
    private void goToPayment() {
        Intent intent = new Intent(this, PaymentActivity.class);
        intent.putExtra("returnTo", "video_scan");
        startActivity(intent);
    }
    
    @Override
    public void onBackPressed() {
            showExitConfirmDialog();
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        
        // Clean up timer if still running
        // Removed scanTimer as it's no longer used
    }
    
    // 视频数据模型
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
    
    // 保持适配器实现不变
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
                videoName = itemView.findViewById(R.id.tv_video_title);
                videoDuration = itemView.findViewById(R.id.tv_video_duration);
                videoDate = itemView.findViewById(R.id.tv_video_date);
                videoSize = itemView.findViewById(R.id.tv_video_size);
                videoResolution = itemView.findViewById(R.id.tv_video_resolution);
                videoCheckbox = itemView.findViewById(R.id.checkbox_video);
            }
        }
    }
} 