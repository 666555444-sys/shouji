package com.example.shoujixiufu;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
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
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class FileScanActivity extends AppCompatActivity {

    private ImageButton btnBack;
    // 删除文件类型按钮
    private CardView cardScanProgress, cardFileSelector;
    private Button btnRecover, btnBrowseFile, btnStartScan, btnBottomAction;
    private TextView tvSelectedFiles, tvScanPercentage, tvScanFiles;
    private RecyclerView fileList;
    private FrameLayout scanningScreen;
    private ProgressBar scanProgressBar;
    private View scanCircleOuter, scanCircleMiddle, scanCircleInner;

    private ActivityResultLauncher<String[]> filePickerLauncher;
    private List<Uri> selectedFiles = new ArrayList<>();
    private Handler handler = new Handler(Looper.getMainLooper());
    private int filesFound = 0;
    private boolean scanCompleted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_scan);

        initViews();
        setupListeners();
        setupFilePicker();
        checkForDirectScan();
    }

    private void initViews() {
        // Toolbar
        btnBack = findViewById(R.id.btn_back);

        // 删除文件类型按钮 - 已删除这部分代码

        // 删除过滤器 - 已删除这部分代码

        // Cards and containers
        cardScanProgress = findViewById(R.id.card_scan_progress);
        cardFileSelector = findViewById(R.id.card_file_selector);

        // Buttons
        btnRecover = findViewById(R.id.btn_recover);
        btnBrowseFile = findViewById(R.id.btn_browse_file);
        btnStartScan = findViewById(R.id.btn_start_scan);
        btnBottomAction = findViewById(R.id.btn_bottom_action);

        // Text views
        tvSelectedFiles = findViewById(R.id.tv_selected_files);
        tvScanPercentage = findViewById(R.id.tv_scan_percentage);
        tvScanFiles = findViewById(R.id.tv_scan_files);

        // File list
        fileList = findViewById(R.id.file_list);
        fileList.setLayoutManager(new LinearLayoutManager(this));

        // Scanning screen
        scanningScreen = findViewById(R.id.scanning_screen);
        scanProgressBar = findViewById(R.id.scan_progress_bar);
        scanCircleOuter = findViewById(R.id.scan_circle_outer);
        scanCircleMiddle = findViewById(R.id.scan_circle_middle);
        scanCircleInner = findViewById(R.id.scan_circle_inner);
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> showExitConfirmDialog());

        btnBrowseFile.setOnClickListener(v -> openFilePicker());

        btnStartScan.setOnClickListener(v -> startScanning());

        btnRecover.setOnClickListener(v -> navigateToPayment());

        btnBottomAction.setOnClickListener(v -> navigateToPayment());

        // 删除文件类型按钮监听器

        // 删除过滤器监听器
    }

    // 删除setupFileTypeButtons方法

    // 删除setupFilterListeners方法

    private void setupFilePicker() {
        filePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.OpenMultipleDocuments(),
                uris -> {
                    if (uris != null && !uris.isEmpty()) {
                        selectedFiles.clear();
                        selectedFiles.addAll(uris);
                        
                        StringBuilder fileNames = new StringBuilder();
                        fileNames.append(getString(R.string.select_files)).append(":\n");
                        
                        for (Uri uri : uris) {
                            String fileName = getFileNameFromUri(uri);
                            fileNames.append("• ").append(fileName).append("\n");
                        }
                        
                        tvSelectedFiles.setText(fileNames.toString());
                        tvSelectedFiles.setVisibility(View.VISIBLE);
                        btnStartScan.setVisibility(View.VISIBLE);
                    }
                }
        );
    }

    private String getFileNameFromUri(Uri uri) {
        String path = uri.getPath();
        if (path != null) {
            String[] segments = path.split("/");
            if (segments.length > 0) {
                return segments[segments.length - 1];
            }
        }
        return "Unknown file";
    }

    private void openFilePicker() {
        filePickerLauncher.launch(new String[]{"*/*"});
    }

    private void checkForDirectScan() {
        boolean startScan = getIntent().getBooleanExtra("startScan", false);
        if (startScan) {
            cardFileSelector.setVisibility(View.GONE);
            startScanning();
        }
    }

    private void startScanning() {
        // Hide file selector
        cardFileSelector.setVisibility(View.GONE);
        
        // Show scanning screen
        scanningScreen.setVisibility(View.VISIBLE);
        
        // Start animation for scan circles
        startScanCircleAnimations();
        
        // 扫描本地文件
        scanLocalFiles();
    }

    // 修改后的扫描本地文件方法 - 不再区分目录
    private void scanLocalFiles() {
        new Thread(() -> {
            // 显示初始进度
            handler.post(() -> {
                scanProgressBar.setProgress(0);
                tvScanPercentage.setText("0%");
                tvScanFiles.setText(getString(R.string.scanning_files_desc));
            });
            
            // 获取要扫描的文件列表
            List<FileItem> localFiles = new ArrayList<>();
            
            try {
                // 模拟进度从0%开始，增加到10%后停止
                for(int progress = 0; progress <= 10; progress += 2) {
                    final int currentProgress = progress;
                    handler.post(() -> {
                        scanProgressBar.setProgress(currentProgress);
                        tvScanPercentage.setText(currentProgress + "%");
                        if(currentProgress > 5) {
                            filesFound = 5; // 模拟找到5个文件
                            tvScanFiles.setText(getString(R.string.found_files, filesFound));
                            tvScanFiles.setAlpha(1f);
                        }
                    });
                    
                    // 在5%时获取一些文件数据
                    if(progress == 5) {
                        // 获取外部存储中的部分文件 - 直接使用MediaStore获取
                        List<FileItem> files = getAllLocalFiles();
                        if(files.size() > 0) {
                            localFiles.addAll(files.subList(0, Math.min(5, files.size())));
                            filesFound = localFiles.size();
                        }
                    }
                    
                    Thread.sleep(250);
                }
                
                // 扫描到10%后就停止并显示结果
                final List<FileItem> finalFiles = localFiles.isEmpty() ? getDefaultFileItems() : localFiles;
                handler.post(() -> {
                    scanProgressBar.setProgress(10);
                    tvScanPercentage.setText("10%");
                    filesFound = finalFiles.size();
                    tvScanFiles.setText(getString(R.string.found_files, filesFound));
                    
                    // 扫描完成标志（虽然只扫描到10%）
                    scanCompleted = true;
                    
                    // 延迟500ms后隐藏扫描屏幕，显示结果
                    new Handler().postDelayed(() -> {
                        // 隐藏扫描屏幕
                        scanningScreen.setVisibility(View.GONE);
                        
                        // 显示文件列表
                        showScanResults(finalFiles);
                    }, 500);
                });
                
            } catch (Exception e) {
                e.printStackTrace();
                // 如果出错，回到UI线程显示默认结果
                handler.post(() -> {
                    scanCompleted = true;
                    scanningScreen.setVisibility(View.GONE);
                    showScanResults(getDefaultFileItems());
                });
            }
        }).start();
    }
    
    // 获取所有本地文件的新方法
    private List<FileItem> getAllLocalFiles() {
        List<FileItem> allFiles = new ArrayList<>();
        
        try {
            // 使用MediaStore获取所有文件类型
            ContentResolver contentResolver = getContentResolver();
            Uri uri = MediaStore.Files.getContentUri("external");
            
            String[] projection = {
                MediaStore.Files.FileColumns.DISPLAY_NAME,
                MediaStore.Files.FileColumns.SIZE,
                MediaStore.Files.FileColumns.DATE_MODIFIED,
                MediaStore.Files.FileColumns.MIME_TYPE
            };
            
            Cursor cursor = contentResolver.query(uri, projection, null, null, null);
            if (cursor != null) {
                while (cursor.moveToNext() && allFiles.size() < 50) { // 限制最多显示50个文件
                    try {
                        String name = cursor.getString(0);
                        long size = cursor.getLong(1);
                        long date = cursor.getLong(2) * 1000; // 转换为毫秒
                        String mimeType = cursor.getString(3);
                        
                        String icon = "F"; // 默认文件图标
                        if (mimeType != null) {
                            if (mimeType.contains("image/")) icon = "IMG";
                            else if (mimeType.contains("video/")) icon = "VID";
                            else if (mimeType.contains("audio/")) icon = "AUD";
                            else if (mimeType.contains("text/")) icon = "TXT";
                            else if (mimeType.contains("pdf")) icon = "PDF";
                        }
                        
                        FileItem item = new FileItem(
                                name,
                                formatFileSize(size),
                                formatDate(date),
                                icon,
                                R.drawable.bg_file_icon_doc
                        );
                        allFiles.add(item);
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
        
        // 如果找不到文件，返回默认样本
        if (allFiles.isEmpty()) {
            allFiles = getDefaultFileItems();
        }
        
        return allFiles;
    }
    
    // 删除getFilesFromDirectory方法，因为不再区分目录

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
    
    // 获取文件扩展名
    private String getFileExtension(String fileName) {
        if (fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0) {
            return fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
        } else {
            return "";
        }
    }
    
    // 简化的获取图标方法
    private String getFileIcon(String extension) {
        if (extension.isEmpty()) return "F";
        return extension.toUpperCase().substring(0, Math.min(extension.length(), 3));
    }
    
    // 获取图标背景颜色
    private int getFileIconBackground(String extension) {
        return R.drawable.bg_file_icon_doc; // 使用默认背景
    }

    // 显示扫描结果
    private void showScanResults(List<FileItem> files) {
        // 显示文件列表容器
        cardScanProgress.setVisibility(View.VISIBLE);
        fileList.setVisibility(View.VISIBLE);
        btnBottomAction.setVisibility(View.VISIBLE);
        
        // 设置已扫描10%的文本
        TextView progressText = findViewById(R.id.tv_progress_text);
        if (progressText != null) {
            progressText.setText(getString(R.string.scanned_progress).replace("10%", "10%"));
        }
        
        // 更新文件列表
        updateFileList(files);
    }

    // 简化的更新文件列表方法，不再进行过滤
    private void updateFileList(List<FileItem> items) {
        // 如果文件列表为空，使用默认样本数据
        if (items == null || items.isEmpty()) {
            items = getDefaultFileItems();
        }
        
        // 直接设置适配器，不再进行过滤
        FileAdapter adapter = new FileAdapter(this, items);
        fileList.setAdapter(adapter);
    }
    
    // 获取默认的文件列表
    private List<FileItem> getDefaultFileItems() {
        List<FileItem> items = new ArrayList<>();
        
        // 添加一些样本文件
        items.add(new FileItem("项目计划书.docx", "1.2MB", "昨天", "DOC", R.drawable.bg_file_icon_doc));
        items.add(new FileItem("财务报表.xlsx", "1.6MB", "昨天", "XLS", R.drawable.bg_file_icon_doc));
        items.add(new FileItem("产品发布会.pptx", "5.7MB", "昨天", "PPT", R.drawable.bg_file_icon_doc));
        items.add(new FileItem("合同文件.pdf", "2.4MB", "昨天", "PDF", R.drawable.bg_file_icon_doc));
        items.add(new FileItem("程序源代码.zip", "7.5MB", "昨天", "ZIP", R.drawable.bg_file_icon_doc));
        
        return items;
    }
    
    // 删除getSampleFilesForType方法
    
    // 删除getSelectedFileType方法

    private void startScanCircleAnimations() {
        // Outer circle animation
        ObjectAnimator outerRotation = ObjectAnimator.ofFloat(scanCircleOuter, "rotation", 0f, 360f);
        outerRotation.setDuration(2000);
        outerRotation.setRepeatCount(ValueAnimator.INFINITE);
        outerRotation.setInterpolator(new LinearInterpolator());
        outerRotation.start();

        // Middle circle animation (reverse direction)
        ObjectAnimator middleRotation = ObjectAnimator.ofFloat(scanCircleMiddle, "rotation", 0f, -360f);
        middleRotation.setDuration(1700);
        middleRotation.setRepeatCount(ValueAnimator.INFINITE);
        middleRotation.setInterpolator(new LinearInterpolator());
        middleRotation.start();

        // Inner circle animation
        ObjectAnimator innerRotation = ObjectAnimator.ofFloat(scanCircleInner, "rotation", 0f, 360f);
        innerRotation.setDuration(1400);
        innerRotation.setRepeatCount(ValueAnimator.INFINITE);
        innerRotation.setInterpolator(new LinearInterpolator());
        innerRotation.start();
    }

    private void showRecoveryDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.recovery_dialog_title)
                .setMessage(R.string.recovery_dialog_content)
                .setNegativeButton(R.string.cancel, (dialog, which) -> dialog.dismiss())
                .setPositiveButton(R.string.recover, (dialog, which) -> navigateToPayment())
                .setCancelable(false)
                .show();
    }

    private void showExitConfirmDialog() {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
        if (scanningScreen.getVisibility() == View.VISIBLE) {
            // 正在扫描中的退出提示
            builder.setTitle(R.string.cancel_scan_title)
                  .setMessage(R.string.cancel_scan_message)
                  .setNegativeButton(R.string.no, (dialog, which) -> dialog.dismiss())
                  .setPositiveButton(R.string.yes, (dialog, which) -> finish());
        } else {
            // 扫描完成后的退出提示
            builder.setTitle(R.string.exit_service_title)
                  .setMessage(R.string.exit_service_message)
                  .setNegativeButton(R.string.continue_service, (dialog, which) -> dialog.dismiss())
                  .setPositiveButton(R.string.confirm_exit, (dialog, which) -> finish());
        }
        builder.setCancelable(false).show();
    }

    private void navigateToPayment() {
        Intent intent = new Intent(this, PaymentActivity.class);
        intent.putExtra("returnTo", "file_scan");
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        showExitConfirmDialog();
    }

    // File item class for RecyclerView
    public static class FileItem {
        private String name;
        private String size;
        private String date;
        private String icon;
        private int iconBackground;
        private boolean isChecked;

        public FileItem(String name, String size, String date, String icon, int iconBackground) {
            this.name = name;
            this.size = size;
            this.date = date;
            this.icon = icon;
            this.iconBackground = iconBackground;
            this.isChecked = false;
        }

        public String getName() {
            return name;
        }

        public String getSize() {
            return size;
        }

        public String getDate() {
            return date;
        }

        public String getIcon() {
            return icon;
        }

        public int getIconBackground() {
            return iconBackground;
        }

        public boolean isChecked() {
            return isChecked;
        }

        public void setChecked(boolean checked) {
            isChecked = checked;
        }
    }

    // Adapter remains unchanged
    public class FileAdapter extends RecyclerView.Adapter<FileAdapter.FileViewHolder> {
        private List<FileItem> files;
        private android.content.Context context;

        public FileAdapter(android.content.Context context, List<FileItem> files) {
            this.context = context;
            this.files = files;
        }

        @Override
        public FileViewHolder onCreateViewHolder(android.view.ViewGroup parent, int viewType) {
            View view = getLayoutInflater().inflate(R.layout.item_file, parent, false);
            return new FileViewHolder(view);
        }

        @Override
        public void onBindViewHolder(FileViewHolder holder, int position) {
            FileItem file = files.get(position);
            
            holder.tvFileName.setText(file.getName());
            holder.tvFileDate.setText(file.getDate());
            holder.tvFileSize.setText(file.getSize());
            holder.tvFileIcon.setText(file.getIcon());
            holder.fileIconContainer.setBackgroundResource(file.getIconBackground());
            holder.checkbox.setChecked(file.isChecked());
            
            holder.checkbox.setOnClickListener(v -> {
                boolean isChecked = holder.checkbox.isChecked();
                file.setChecked(isChecked);
            });
            
            holder.itemView.setOnClickListener(v -> {
                boolean isChecked = !file.isChecked();
                file.setChecked(isChecked);
                holder.checkbox.setChecked(isChecked);
            });
        }

        @Override
        public int getItemCount() {
            return files.size();
        }

        class FileViewHolder extends RecyclerView.ViewHolder {
            TextView tvFileName, tvFileDate, tvFileSize, tvFileIcon;
            View fileIconContainer;
            CheckBox checkbox;

            public FileViewHolder(View itemView) {
                super(itemView);
                tvFileName = itemView.findViewById(R.id.tv_file_name);
                tvFileDate = itemView.findViewById(R.id.tv_file_date);
                tvFileSize = itemView.findViewById(R.id.tv_file_size);
                tvFileIcon = itemView.findViewById(R.id.tv_file_icon);
                fileIconContainer = itemView.findViewById(R.id.file_icon_container);
                checkbox = itemView.findViewById(R.id.checkbox_file);
            }
        }
    }
} 