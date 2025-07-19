package com.example.shoujixiufu;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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

import java.util.ArrayList;
import java.util.List;

public class FileScanActivity extends AppCompatActivity {

    private ImageButton btnBack;
    private Button btnTypeWord, btnTypeExcel, btnTypePpt, btnTypePdf, btnTypeOthers;
    private TextView filterTime, filterSize;
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

        // File type buttons
        btnTypeWord = findViewById(R.id.btn_type_word);
        btnTypeExcel = findViewById(R.id.btn_type_excel);
        btnTypePpt = findViewById(R.id.btn_type_ppt);
        btnTypePdf = findViewById(R.id.btn_type_pdf);
        btnTypeOthers = findViewById(R.id.btn_type_others);

        // Filters
        filterTime = findViewById(R.id.filter_time);
        filterSize = findViewById(R.id.filter_size);

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

        // File type button listeners
        setupFileTypeButtons();

        // Filter listeners
        setupFilterListeners();
    }

    private void setupFileTypeButtons() {
        View.OnClickListener fileTypeClickListener = v -> {
            // Reset all buttons to inactive state
            btnTypeWord.setBackgroundResource(R.drawable.bg_file_type_inactive);
            btnTypeWord.setTextColor(getResources().getColor(R.color.text_secondary));
            btnTypeExcel.setBackgroundResource(R.drawable.bg_file_type_inactive);
            btnTypeExcel.setTextColor(getResources().getColor(R.color.text_secondary));
            btnTypePpt.setBackgroundResource(R.drawable.bg_file_type_inactive);
            btnTypePpt.setTextColor(getResources().getColor(R.color.text_secondary));
            btnTypePdf.setBackgroundResource(R.drawable.bg_file_type_inactive);
            btnTypePdf.setTextColor(getResources().getColor(R.color.text_secondary));
            btnTypeOthers.setBackgroundResource(R.drawable.bg_file_type_inactive);
            btnTypeOthers.setTextColor(getResources().getColor(R.color.text_secondary));

            // Set active state for clicked button
            v.setBackgroundResource(R.drawable.bg_file_type_active);
            ((Button) v).setTextColor(getResources().getColor(android.R.color.white));

            // Update file list based on selected type
            if (scanCompleted) {
                updateFileList();
            }
        };

        btnTypeWord.setOnClickListener(fileTypeClickListener);
        btnTypeExcel.setOnClickListener(fileTypeClickListener);
        btnTypePpt.setOnClickListener(fileTypeClickListener);
        btnTypePdf.setOnClickListener(fileTypeClickListener);
        btnTypeOthers.setOnClickListener(fileTypeClickListener);
    }

    private void setupFilterListeners() {
        View.OnClickListener filterClickListener = v -> {
            // Reset all filters to inactive state
            filterTime.setBackgroundResource(R.drawable.bg_filter_inactive);
            filterTime.setTextColor(getResources().getColor(R.color.text_secondary));
            filterSize.setBackgroundResource(R.drawable.bg_filter_inactive);
            filterSize.setTextColor(getResources().getColor(R.color.text_secondary));

            // Set active state for clicked filter
            v.setBackgroundResource(R.drawable.bg_filter_active);
            ((TextView) v).setTextColor(getResources().getColor(R.color.primary_color));

            // Update file list based on selected filter
            if (scanCompleted) {
                updateFileList();
            }
        };

        filterTime.setOnClickListener(filterClickListener);
        filterSize.setOnClickListener(filterClickListener);
    }

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
        String startScan = getIntent().getStringExtra("startScan");
        if ("true".equals(startScan)) {
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
        
        // Start progress animation
        startProgressAnimation();
    }

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

    private void startProgressAnimation() {
        final int targetProgress = 10; // Target progress percentage
        filesFound = selectedFiles.isEmpty() ? 3 : selectedFiles.size();
        
        new Thread(() -> {
            int progress = 0;
            
            // Simulate scanning progress
            while (progress < targetProgress) {
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                
                progress += 1;
                final int currentProgress = progress;
                
                handler.post(() -> {
                    // Update progress bar
                    scanProgressBar.setProgress(currentProgress);
                    tvScanPercentage.setText(currentProgress + "%");
                    
                    // Show found files with animation
                    if (currentProgress > 3 && tvScanFiles.getAlpha() == 0) {
                        tvScanFiles.setText(getString(R.string.found_files, filesFound));
                        tvScanFiles.animate().alpha(1f).setDuration(500).start();
                    }
                });
            }
            
            // Scanning completed
            handler.postDelayed(() -> {
                scanningScreen.setVisibility(View.GONE);
                showScanResults();
            }, 1000);
        }).start();
    }

    private void showScanResults() {
        // Show scan progress card
        cardScanProgress.setVisibility(View.VISIBLE);
        
        // Show file list
        updateFileList();
        fileList.setVisibility(View.VISIBLE);
        
        // Show bottom action button
        btnBottomAction.setVisibility(View.VISIBLE);
        
        // Mark scan as completed
        scanCompleted = true;
        
        // Show recovery dialog
        showRecoveryDialog();
    }

    private void updateFileList() {
        // Create sample file items based on selected type
        List<FileItem> items = new ArrayList<>();
        
        String selectedType = getSelectedFileType();
        
        // Add sample files based on type
        if ("word".equals(selectedType)) {
            items.add(new FileItem("项目计划书.docx", "1.2MB", "昨天", "W", R.drawable.bg_file_icon_doc));
            items.add(new FileItem("个人简历.doc", "680KB", "7-15", "W", R.drawable.bg_file_icon_doc));
            items.add(new FileItem("工作报告.docx", "2.3MB", "7-14", "W", R.drawable.bg_file_icon_doc));
        } else if ("excel".equals(selectedType)) {
            items.add(new FileItem("财务报表.xlsx", "1.6MB", "昨天", "E", R.drawable.bg_file_icon_doc));
            items.add(new FileItem("销售数据.xls", "4.2MB", "7-15", "E", R.drawable.bg_file_icon_doc));
            items.add(new FileItem("人员名单.xlsx", "820KB", "7-14", "E", R.drawable.bg_file_icon_doc));
        } else if ("ppt".equals(selectedType)) {
            items.add(new FileItem("产品发布会.pptx", "5.7MB", "昨天", "P", R.drawable.bg_file_icon_doc));
            items.add(new FileItem("季度总结.ppt", "3.8MB", "7-15", "P", R.drawable.bg_file_icon_doc));
            items.add(new FileItem("项目提案.pptx", "4.1MB", "7-14", "P", R.drawable.bg_file_icon_doc));
        } else if ("pdf".equals(selectedType)) {
            items.add(new FileItem("合同文件.pdf", "2.4MB", "昨天", "PDF", R.drawable.bg_file_icon_doc));
            items.add(new FileItem("技术手册.pdf", "5.1MB", "7-15", "PDF", R.drawable.bg_file_icon_doc));
            items.add(new FileItem("研究报告.pdf", "3.7MB", "7-14", "PDF", R.drawable.bg_file_icon_doc));
        } else {
            items.add(new FileItem("程序源代码.zip", "7.5MB", "昨天", "ZIP", R.drawable.bg_file_icon_doc));
            items.add(new FileItem("数据备份.bak", "12.3MB", "7-15", "BAK", R.drawable.bg_file_icon_doc));
            items.add(new FileItem("网站设计.psd", "18.5MB", "7-14", "PSD", R.drawable.bg_file_icon_doc));
        }
        
        // Set adapter
        FileAdapter adapter = new FileAdapter(this, items);
        fileList.setAdapter(adapter);
    }

    private String getSelectedFileType() {
        if (btnTypeWord.getTextColors().getDefaultColor() == getResources().getColor(android.R.color.white)) {
            return "word";
        } else if (btnTypeExcel.getTextColors().getDefaultColor() == getResources().getColor(android.R.color.white)) {
            return "excel";
        } else if (btnTypePpt.getTextColors().getDefaultColor() == getResources().getColor(android.R.color.white)) {
            return "ppt";
        } else if (btnTypePdf.getTextColors().getDefaultColor() == getResources().getColor(android.R.color.white)) {
            return "pdf";
        } else {
            return "other";
        }
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
        if (scanCompleted) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.scan_exit_confirm)
                    .setMessage(R.string.scan_exit_desc)
                    .setNegativeButton(R.string.cancel, (dialog, which) -> dialog.dismiss())
                    .setPositiveButton(R.string.confirm, (dialog, which) -> finish())
                    .show();
        } else {
            finish();
        }
    }

    private void navigateToPayment() {
        Intent intent = new Intent(this, MainActivity.class); // Replace with PaymentActivity if available
        intent.putExtra("returnTo", "file-scan");
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

    // File adapter for RecyclerView
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