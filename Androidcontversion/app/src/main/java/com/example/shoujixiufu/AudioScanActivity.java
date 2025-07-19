package com.example.shoujixiufu;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class AudioScanActivity extends AppCompatActivity {

    private CardView cardScanProgress;
    private CardView cardAudioSelector;
    private RecyclerView audioList;
    private Button btnBottomAction;
    private FrameLayout scanningScreen;
    private ProgressBar scanProgressBar;
    private TextView tvScanPercentage;
    private TextView tvScanFiles;
    private TextView tvSelectedAudios;
    private Button btnBrowseAudio;
    private Button btnStartScan;
    private View scanCircleOuter;
    private View scanCircleMiddle;
    private View scanCircleInner;

    private boolean scanCompleted = false;
    private boolean modalShown = false;

    private ActivityResultLauncher<String[]> audioPickerLauncher;
    private List<Uri> selectedAudioFiles = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_scan);

        // Initialize views
        initViews();
        // Set click listeners
        setClickListeners();
        // Initialize the audio picker launcher
        initAudioPicker();

        // Check for automatic scan start
        if (getIntent().getBooleanExtra("startScan", false)) {
            startScanning();
        }
    }

    private void initViews() {
        // Header views
        ImageButton btnBack = findViewById(R.id.btn_back);
        ImageButton btnMenu = findViewById(R.id.btn_menu);
        TextView tvTitle = findViewById(R.id.tv_title);

        // Filter views
        TextView filterTime = findViewById(R.id.filter_time);
        TextView filterSize = findViewById(R.id.filter_size);

        // Progress views
        cardScanProgress = findViewById(R.id.card_scan_progress);
        Button btnRecover = findViewById(R.id.btn_recover);
        TextView tvProgressText = findViewById(R.id.tv_progress_text);

        // Audio selector views
        cardAudioSelector = findViewById(R.id.card_audio_selector);
        btnBrowseAudio = findViewById(R.id.btn_browse_audio);
        tvSelectedAudios = findViewById(R.id.tv_selected_audios);
        btnStartScan = findViewById(R.id.btn_start_scan);

        // Audio list view
        audioList = findViewById(R.id.audio_list);
        audioList.setLayoutManager(new LinearLayoutManager(this));

        // Bottom action button
        btnBottomAction = findViewById(R.id.btn_bottom_action);

        // Scanning screen views
        scanningScreen = findViewById(R.id.scanning_screen);
        scanProgressBar = findViewById(R.id.scan_progress_bar);
        tvScanPercentage = findViewById(R.id.tv_scan_percentage);
        tvScanFiles = findViewById(R.id.tv_scan_files);

        // Scan animation views
        scanCircleOuter = findViewById(R.id.scan_circle_outer);
        scanCircleMiddle = findViewById(R.id.scan_circle_middle);
        scanCircleInner = findViewById(R.id.scan_circle_inner);
    }

    private void setClickListeners() {
        // Back button click
        findViewById(R.id.btn_back).setOnClickListener(v -> showExitConfirmDialog());

        // Menu button click
        findViewById(R.id.btn_menu).setOnClickListener(v -> {
            Toast.makeText(AudioScanActivity.this, "菜单功能开发中", Toast.LENGTH_SHORT).show();
        });

        // Filter clicks
        findViewById(R.id.filter_time).setOnClickListener(v -> {
            findViewById(R.id.filter_time).setBackgroundResource(R.drawable.bg_filter_active);
            findViewById(R.id.filter_time).setSelected(true);
            findViewById(R.id.filter_size).setBackgroundResource(R.drawable.bg_filter_inactive);
            findViewById(R.id.filter_size).setSelected(false);
        });

        findViewById(R.id.filter_size).setOnClickListener(v -> {
            findViewById(R.id.filter_size).setBackgroundResource(R.drawable.bg_filter_active);
            findViewById(R.id.filter_size).setSelected(true);
            findViewById(R.id.filter_time).setBackgroundResource(R.drawable.bg_filter_inactive);
            findViewById(R.id.filter_time).setSelected(false);
        });

        // Recover button click
      //  findViewById(R.id.btn_recover).setOnClickListener(v -> showRecoveryDialog());

        // Browse audio button click
        btnBrowseAudio.setOnClickListener(v -> audioPickerLauncher.launch(new String[]{"audio/*"}));

        // Start scan button click
        btnStartScan.setOnClickListener(v -> {
            cardAudioSelector.setVisibility(View.GONE);
            startScanning();
        });

        // Bottom action button click
    //    btnBottomAction.setOnClickListener(v -> showRecoveryDialog());
    }

    private void initAudioPicker() {
        audioPickerLauncher = registerForActivityResult(
                new ActivityResultContracts.OpenMultipleDocuments(),
                uris -> {
                    if (uris != null && !uris.isEmpty()) {
                        selectedAudioFiles.clear();
                        selectedAudioFiles.addAll(uris);

                        StringBuilder fileNames = new StringBuilder();
                        fileNames.append("<b>已选择音频:</b><br>");
                        for (Uri uri : uris) {
                            String fileName = getFileNameFromUri(uri);
                            fileNames.append(fileName).append("<br>");
                        }

                        tvSelectedAudios.setVisibility(View.VISIBLE);
                        tvSelectedAudios.setText(android.text.Html.fromHtml(fileNames.toString()));
                        btnStartScan.setVisibility(View.VISIBLE);
                    }
                });
    }

    private String getFileNameFromUri(Uri uri) {
        String fileName = uri.getLastPathSegment();
        return fileName != null ? fileName : "未知文件";
    }

    private void showExitConfirmDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.scan_exit_confirm)
                .setMessage(R.string.scan_exit_desc)
                .setNegativeButton(R.string.cancel, (dialog, which) -> dialog.dismiss())
                .setPositiveButton(R.string.confirm, (dialog, which) -> {
                    dialog.dismiss();
                    finish();
                })
                .show();
    }

//    private void showRecoveryDialog() {
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setTitle(R.string.recovery_dialog_title)
//                .setMessage(R.string.recovery_dialog_content)
//                .setNegativeButton(R.string.cancel, (dialog, which) -> dialog.dismiss())
//                .setPositiveButton(R.string.recover, (dialog, which) -> {
//                    // Navigate to payment page
//                    Intent intent = new Intent(AudioScanActivity.this, PaymentActivity.class);
//                    intent.putExtra("returnTo", "audio_scan");
//                    startActivity(intent);
//                })
//                .show();
//    }

    private void startScanning() {
        scanningScreen.setVisibility(View.VISIBLE);
        cardAudioSelector.setVisibility(View.GONE);

        // Start scan animation
        startScanAnimation();

        // Simulate scanning progress
        scanProgressBar.setProgress(0);
        simulateScanProgress();
    }

    private void startScanAnimation() {
        // Outer circle animation
        RotateAnimation rotateOuter = new RotateAnimation(0, 360,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        rotateOuter.setDuration(2000);
        rotateOuter.setRepeatCount(Animation.INFINITE);
        rotateOuter.setRepeatMode(Animation.RESTART);
        scanCircleOuter.startAnimation(rotateOuter);

        // Middle circle animation (reverse direction)
        RotateAnimation rotateMiddle = new RotateAnimation(0, -360,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        rotateMiddle.setDuration(1700);
        rotateMiddle.setRepeatCount(Animation.INFINITE);
        rotateMiddle.setRepeatMode(Animation.RESTART);
        scanCircleMiddle.startAnimation(rotateMiddle);

        // Inner circle animation
        RotateAnimation rotateInner = new RotateAnimation(0, 360,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        rotateInner.setDuration(1400);
        rotateInner.setRepeatCount(Animation.INFINITE);
        rotateInner.setRepeatMode(Animation.RESTART);
        scanCircleInner.startAnimation(rotateInner);
    }

    private void simulateScanProgress() {
        final int targetProgress = 10; // Target progress is 10%
        final int duration = 5000; // 5 seconds for the entire animation
        final int interval = 200; // Update every 200ms
        final float progressStep = (float) targetProgress * interval / duration;
        
        final Handler handler = new Handler();
        final Runnable runnable = new Runnable() {
            float progress = 0;
            int filesFound = 0;

            @Override
            public void run() {
                progress += progressStep;
                filesFound += (int) (Math.random() * 2);
                
                if (progress > targetProgress) {
                    progress = targetProgress;
                }
                
                scanProgressBar.setProgress((int) progress);
                tvScanPercentage.setText((int) progress + "%");
                tvScanFiles.setText(getString(R.string.found_files, filesFound));
                
                // Make scan files text visible
                if (tvScanFiles.getAlpha() < 1) {
                    tvScanFiles.setAlpha(Math.min(1, tvScanFiles.getAlpha() + 0.1f));
                }
                
                if (progress < targetProgress) {
                    handler.postDelayed(this, interval);
                } else {
                    // Finish scanning after delay
                    new Handler().postDelayed(() -> finishScanning(filesFound), 1000);
                }
            }
        };
        
        handler.post(runnable);
    }

    private void finishScanning(int filesFound) {
        // Hide scanning screen
        scanningScreen.setVisibility(View.GONE);
        
        // Show scan results
        cardScanProgress.setVisibility(View.VISIBLE);
        audioList.setVisibility(View.VISIBLE);
        btnBottomAction.setVisibility(View.VISIBLE);
        
        // Create and set adapter for audio files
        createAudioItems(filesFound);
        
        // Mark scan as completed
        scanCompleted = true;
        
        // Show recovery dialog if not shown before
        if (!modalShown) {
         //   new Handler().postDelayed(this::showRecoveryDialog, 500);
            modalShown = true;
        }
    }

    private void createAudioItems(int count) {
        // Create a list of sample audio items
        List<AudioItem> audioItems = new ArrayList<>();
        
        // Add sample items (use real items if available from selectedAudioFiles)
        if (selectedAudioFiles.size() > 0) {
            for (Uri uri : selectedAudioFiles) {
                String name = getFileNameFromUri(uri);
                String duration = formatDuration((int) (Math.random() * 300));
                String size = formatSize((long) (Math.random() * 10 * 1024 * 1024));
                String date = "今天";
                audioItems.add(new AudioItem(name, duration, size, date));
            }
        } else {
            // Use predefined sample data if no files were selected
            String[] sampleNames = {
                    "微信语音_20250716.mp3",
                    "录音备忘_会议内容.m4a",
                    "语音笔记_项目计划.mp3"
            };
            
            String[] sampleDates = {"昨天", "7-15", "7-14"};
            
            // Add up to 3 sample items
            for (int i = 0; i < Math.min(3, count); i++) {
                String name = sampleNames[i % sampleNames.length];
                String duration = formatDuration((int) (Math.random() * 300));
                String size = formatSize((long) (Math.random() * 10 * 1024 * 1024));
                String date = sampleDates[i % sampleDates.length];
                audioItems.add(new AudioItem(name, duration, size, date));
            }
        }
        
        // Set adapter to RecyclerView
        AudioAdapter adapter = new AudioAdapter(audioItems);
        audioList.setAdapter(adapter);
    }

    private String formatDuration(int seconds) {
        int minutes = seconds / 60;
        int remainingSeconds = seconds % 60;
        return minutes + ":" + String.format("%02d", remainingSeconds);
    }

    private String formatSize(long bytes) {
        if (bytes < 1024) {
            return bytes + "B";
        } else if (bytes < 1024 * 1024) {
            return (bytes / 1024) + "KB";
        } else {
            return String.format("%.1f", bytes / (1024.0 * 1024.0)) + "MB";
        }
    }

    // Simple data class for audio items
    public static class AudioItem {
        String name;
        String duration;
        String size;
        String date;

        AudioItem(String name, String duration, String size, String date) {
            this.name = name;
            this.duration = duration;
            this.size = size;
            this.date = date;
        }
    }

    // Adapter for the audio list
    private class AudioAdapter extends RecyclerView.Adapter<AudioAdapter.AudioViewHolder> {
        private final List<AudioItem> audioItems;

        AudioAdapter(List<AudioItem> audioItems) {
            this.audioItems = audioItems;
        }

        @Override
        public AudioViewHolder onCreateViewHolder(android.view.ViewGroup parent, int viewType) {
            View view = getLayoutInflater().inflate(R.layout.item_audio, parent, false);
            return new AudioViewHolder(view);
        }

        @Override
        public void onBindViewHolder(AudioViewHolder holder, int position) {
            AudioItem item = audioItems.get(position);
            holder.name.setText(item.name);
            holder.duration.setText(item.duration);
            holder.size.setText(item.size);
            holder.date.setText(item.date);
            
            // Set checkbox click listener
            holder.checkbox.setOnClickListener(v -> {
                // Toggle checked state
                holder.checkbox.setChecked(!holder.checkbox.isChecked());
            });
        }

        @Override
        public int getItemCount() {
            return audioItems.size();
        }

        class AudioViewHolder extends RecyclerView.ViewHolder {
            TextView name;
            TextView duration;
            TextView size;
            TextView date;
            CheckBox checkbox;

            AudioViewHolder(View itemView) {
                super(itemView);
                name = itemView.findViewById(R.id.tv_audio_name);
                duration = itemView.findViewById(R.id.tv_audio_duration);
                size = itemView.findViewById(R.id.tv_audio_size);
                date = itemView.findViewById(R.id.tv_audio_date);
                checkbox = itemView.findViewById(R.id.checkbox_container);
            }
        }
    }
} 