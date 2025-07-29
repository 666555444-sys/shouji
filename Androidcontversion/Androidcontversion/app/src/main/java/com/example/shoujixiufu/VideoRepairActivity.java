package com.example.shoujixiufu;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class VideoRepairActivity extends AppCompatActivity {

    private ImageButton btnBack;
    private TextView tvFilterTime, tvFilterSize;
    private CardView cardScanProgress;
    private Button btnRecover, btnBottomAction;
    private RecyclerView videoList;
    private TextView tvProgressText;

    private List<VideoItem> videos = new ArrayList<>();
    private boolean sortByTimeDesc = true; // 默认按时间降序
    private boolean sortBySizeDesc = false;
    private int scanPercentage = 10;
    private int filesFound = 5;
    private Handler handler = new Handler(Looper.getMainLooper());
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_repair);
        
        // 获取从ScanActivity传递过来的数据
        if (getIntent().hasExtra("scan_percentage")) {
            scanPercentage = getIntent().getIntExtra("scan_percentage", 10);
        }
        if (getIntent().hasExtra("files_found")) {
            filesFound = getIntent().getIntExtra("files_found", 5);
        }

        initViews();
        setupListeners();
        loadVideos();
    }
    
    private void initViews() {
        // 工具栏
        btnBack = findViewById(R.id.btn_back);

        // 过滤器
        tvFilterTime = findViewById(R.id.filter_time);
        tvFilterSize = findViewById(R.id.filter_size);

        // 卡片和容器
        cardScanProgress = findViewById(R.id.card_scan_progress);

        // 按钮
        btnRecover = findViewById(R.id.btn_recover);
        btnBottomAction = findViewById(R.id.btn_bottom_action);

        // 视频列表
        videoList = findViewById(R.id.video_list);
        videoList.setLayoutManager(new LinearLayoutManager(this));

        // 进度文本
        tvProgressText = findViewById(R.id.tv_progress_text);
        tvProgressText.setText(getString(R.string.scanned_progress).replace("10%", scanPercentage + "%"));
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> showExitConfirmDialog());

        // 过滤器监听器
        tvFilterTime.setOnClickListener(v -> {
            sortByTimeDesc = !sortByTimeDesc;
            sortBySizeDesc = false;
            updateFilterUI();
            sortAndUpdateVideoList();
        });

        tvFilterSize.setOnClickListener(v -> {
            sortBySizeDesc = !sortBySizeDesc;
            sortByTimeDesc = false;
            updateFilterUI();
            sortAndUpdateVideoList();
        });

        // 按钮监听器
        btnRecover.setOnClickListener(v -> navigateToPayment());
        btnBottomAction.setOnClickListener(v -> navigateToPayment());
    }

    private void updateFilterUI() {
        // 更新时间过滤器UI
        tvFilterTime.setBackgroundResource(sortByTimeDesc || sortByTimeDesc == false && sortBySizeDesc == false
                ? R.drawable.bg_filter_active
                : R.drawable.bg_filter_inactive);
        tvFilterTime.setTextColor(getResources().getColor(
                sortByTimeDesc || sortByTimeDesc == false && sortBySizeDesc == false
                        ? R.color.primary_color
                        : R.color.text_secondary));

        // 更新大小过滤器UI
        tvFilterSize.setBackgroundResource(sortBySizeDesc
                ? R.drawable.bg_filter_active
                : R.drawable.bg_filter_inactive);
        tvFilterSize.setTextColor(getResources().getColor(
                sortBySizeDesc
                        ? R.color.primary_color
                        : R.color.text_secondary));
    }

    private void loadVideos() {
        new Thread(() -> {
            try {
                // 加载本地视频文件
                List<VideoItem> localVideos = getAllLocalVideos();
                if (localVideos.isEmpty()) {
                    localVideos = getDefaultVideoItems();
                }
                
                // 保存视频列表
                this.videos.clear();
                this.videos.addAll(localVideos);
                
                // 排序并更新UI
                handler.post(() -> {
                    sortAndUpdateVideoList();
                    
                    // 显示视频列表
                    videoList.setVisibility(View.VISIBLE);
                    btnBottomAction.setVisibility(View.VISIBLE);
                });
            } catch (Exception e) {
                e.printStackTrace();
                handler.post(() -> {
                    // 显示默认结果
                    this.videos.clear();
                    this.videos.addAll(getDefaultVideoItems());
                    sortAndUpdateVideoList();
                });
            }
        }).start();
    }

    private void sortAndUpdateVideoList() {
        if (sortByTimeDesc) {
            // 按时间降序排序
            Collections.sort(videos, new Comparator<VideoItem>() {
                @Override
                public int compare(VideoItem o1, VideoItem o2) {
                    // 将"今天"、"昨天"等转换为可比较的值
                    String date1 = o1.getDate();
                    String date2 = o2.getDate();
                    
                    if (date1.equals("今天") && !date2.equals("今天")) return -1;
                    if (!date1.equals("今天") && date2.equals("今天")) return 1;
                    if (date1.equals("昨天") && !date2.equals("昨天")) return -1;
                    if (!date1.equals("昨天") && date2.equals("昨天")) return 1;
                    
                    // 其他情况按字母顺序排序（MM-dd格式，越大越新）
                    return date2.compareTo(date1);
                }
            });
        } else if (sortBySizeDesc) {
            // 按大小降序排序
            Collections.sort(videos, new Comparator<VideoItem>() {
                @Override
                public int compare(VideoItem o1, VideoItem o2) {
                    // 将"1.2MB"、"650KB"等转换为可比较的值
                    return convertSizeToBytes(o2.getSize()) - convertSizeToBytes(o1.getSize());
                }
            });
        }
        
        // 更新视频列表
        VideoAdapter adapter = new VideoAdapter(videos);
        videoList.setAdapter(adapter);
    }
    
    // 将文件大小字符串转换为字节大小以便比较
    private int convertSizeToBytes(String sizeStr) {
        try {
            sizeStr = sizeStr.trim().toUpperCase();
            float size = Float.parseFloat(sizeStr.replaceAll("[^\\d.]", ""));
            
            if (sizeStr.contains("KB")) {
                return (int)(size * 1024);
            } else if (sizeStr.contains("MB")) {
                return (int)(size * 1024 * 1024);
            } else if (sizeStr.contains("GB")) {
                return (int)(size * 1024 * 1024 * 1024);
            } else {
                return (int)size; // 字节
            }
        } catch (Exception e) {
            return 0;
        }
    }

    private List<VideoItem> getAllLocalVideos() {
        List<VideoItem> allVideos = new ArrayList<>();
        
        try {
            // 使用MediaStore获取视频文件
            ContentResolver contentResolver = getContentResolver();
            Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
            
            String[] projection = {
                MediaStore.Video.Media.TITLE,
                MediaStore.Video.Media.SIZE,
                MediaStore.Video.Media.DATE_MODIFIED,
                MediaStore.Video.Media.DURATION,
                MediaStore.Video.Media.RESOLUTION
            };
            
            Cursor cursor = contentResolver.query(uri, projection, null, null, null);
            if (cursor != null) {
                while (cursor.moveToNext() && allVideos.size() < 30) { // 限制最多显示30个视频文件
                    try {
                        String title = cursor.getString(0);
                        long size = cursor.getLong(1);
                        long date = cursor.getLong(2) * 1000; // 转换为毫秒
                        long duration = cursor.getLong(3);
                        String resolution = cursor.getString(4);
                        
                        VideoItem item = new VideoItem(
                                title,
                                formatFileSize(size),
                                formatDate(date),
                                formatDuration(duration),
                                resolution == null || resolution.isEmpty() ? "未知分辨率" : resolution,
                                false
                        );
                        allVideos.add(item);
                    } catch (Exception e) {
                        // 跳过有问题的文件
                        e.printStackTrace();
                    }
                }
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return allVideos;
    }

    // 格式化文件大小
    private String formatFileSize(long size) {
        if (size <= 0)
            return "0 B";
        
        final String[] units = new String[] { "B", "KB", "MB", "GB", "TB" };
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }
    
    // 格式化日期
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
    
    // 格式化时长
    private String formatDuration(long duration) {
        long seconds = duration / 1000;
        long minutes = seconds / 60;
        seconds = seconds % 60;
        return String.format(Locale.getDefault(), "%d:%02d", minutes, seconds);
    }

    // 获取默认的视频列表
    private List<VideoItem> getDefaultVideoItems() {
        List<VideoItem> items = new ArrayList<>();
        
        // 添加一些样本视频文件
        items.add(new VideoItem("家庭聚会视频", "85.7MB", "今天", "3:20", "1280x720", false));
        items.add(new VideoItem("旅游风景视频", "125.4MB", "今天", "5:45", "1920x1080", false));
        items.add(new VideoItem("宝宝成长记录", "45.2MB", "昨天", "2:30", "1280x720", false));
        items.add(new VideoItem("会议录像", "72.8MB", "昨天", "15:20", "640x480", false));
        items.add(new VideoItem("视频教程", "108.5MB", "04-12", "10:15", "1280x720", false));
        items.add(new VideoItem("生日派对", "65.3MB", "04-10", "4:50", "1920x1080", false));
        items.add(new VideoItem("风景延时摄影", "95.8MB", "04-08", "3:35", "3840x2160", false));
        items.add(new VideoItem("微视频记录", "12.7MB", "04-05", "0:45", "640x480", false));
        
        return items;
    }
    
    private void showExitConfirmDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.exit_service_title)
              .setMessage(R.string.exit_service_message)
              .setNegativeButton(R.string.continue_service, (dialog, which) -> dialog.dismiss())
              .setPositiveButton(R.string.confirm_exit, (dialog, which) -> {
                  // 返回到MainActivity
                  Intent intent = new Intent(this, MainActivity.class);
                  intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                  startActivity(intent);
                  finish();
              })
              .setCancelable(false)
              .show();
    }

    private void navigateToPayment() {
        Intent intent = new Intent(this, PaymentActivity.class);
        intent.putExtra("returnTo", "video_repair");
        startActivity(intent);
    }
    
    @Override
    public void onBackPressed() {
        showExitConfirmDialog();
    }

    // 视频项类
    public static class VideoItem {
        private String title;
        private String size;
        private String date;
        private String duration;
        private String resolution;
        private boolean isChecked;

        public VideoItem(String title, String size, String date, String duration, String resolution, boolean isChecked) {
            this.title = title;
            this.size = size;
            this.date = date;
            this.duration = duration;
            this.resolution = resolution;
            this.isChecked = isChecked;
        }

        public String getTitle() {
            return title;
        }

        public String getSize() {
            return size;
        }

        public String getDate() {
            return date;
        }

        public String getDuration() {
            return duration;
        }

        public String getResolution() {
            return resolution;
        }

        public boolean isChecked() {
            return isChecked;
        }

        public void setChecked(boolean checked) {
            isChecked = checked;
        }
    }

    // 适配器类
    public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoViewHolder> {
        private List<VideoItem> videos;

        public VideoAdapter(List<VideoItem> videos) {
            this.videos = videos;
        }

        @Override
        public VideoViewHolder onCreateViewHolder(android.view.ViewGroup parent, int viewType) {
            View view = getLayoutInflater().inflate(R.layout.item_video, parent, false);
            return new VideoViewHolder(view);
        }

        @Override
        public void onBindViewHolder(VideoViewHolder holder, int position) {
            VideoItem video = videos.get(position);
            
            holder.tvTitle.setText(video.getTitle());
            holder.tvDate.setText(video.getDate());
            holder.tvSize.setText(video.getSize());
            holder.tvDuration.setText(video.getDuration());
            holder.tvResolution.setText(video.getResolution());
            holder.checkbox.setChecked(video.isChecked());
            
            holder.checkbox.setOnClickListener(v -> {
                boolean isChecked = holder.checkbox.isChecked();
                video.setChecked(isChecked);
            });
            
            holder.itemView.setOnClickListener(v -> {
                boolean isChecked = !video.isChecked();
                video.setChecked(isChecked);
                holder.checkbox.setChecked(isChecked);
            });
        }

        @Override
        public int getItemCount() {
            return videos.size();
        }

        class VideoViewHolder extends RecyclerView.ViewHolder {
            TextView tvTitle, tvDate, tvSize, tvDuration, tvResolution;
            CheckBox checkbox;

            public VideoViewHolder(View itemView) {
                super(itemView);
                tvTitle = itemView.findViewById(R.id.tv_video_title);
                tvDate = itemView.findViewById(R.id.tv_video_date);
                tvSize = itemView.findViewById(R.id.tv_video_size);
                tvDuration = itemView.findViewById(R.id.tv_video_duration);
                tvResolution = itemView.findViewById(R.id.tv_video_resolution);
                checkbox = itemView.findViewById(R.id.checkbox_video);
            }
        }
    }
} 