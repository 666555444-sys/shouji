package com.example.shoujixiufu;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class FileListAdapter extends RecyclerView.Adapter<FileListAdapter.FileViewHolder> {

    private final List<FileSendActivity.FileItem> fileItems;
    private final FileSendActivity.FileListAdapterCallback callback;

    public FileListAdapter(List<FileSendActivity.FileItem> fileItems, FileSendActivity.FileListAdapterCallback callback) {
        this.fileItems = fileItems;
        this.callback = callback;
    }

    @NonNull
    @Override
    public FileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_file_send, parent, false);
        return new FileViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FileViewHolder holder, int position) {
        FileSendActivity.FileItem item = fileItems.get(position);
        
        holder.fileNameText.setText(item.getName());
        holder.fileSizeText.setText(item.getSize());
        holder.fileIconImage.setImageResource(item.getIconResId());
        
        holder.removeButton.setOnClickListener(v -> {
            if (callback != null) {
                callback.onRemoveFile(holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return fileItems.size();
    }

    static class FileViewHolder extends RecyclerView.ViewHolder {
        ImageView fileIconImage;
        TextView fileNameText;
        TextView fileSizeText;
        ImageButton removeButton;

        FileViewHolder(View itemView) {
            super(itemView);
            fileIconImage = itemView.findViewById(R.id.file_icon);
            fileNameText = itemView.findViewById(R.id.file_name);
            fileSizeText = itemView.findViewById(R.id.file_size);
            removeButton = itemView.findViewById(R.id.remove_button);
        }
    }
} 