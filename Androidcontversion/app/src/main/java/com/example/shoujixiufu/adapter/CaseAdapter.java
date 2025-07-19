package com.example.shoujixiufu.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shoujixiufu.R;
import com.example.shoujixiufu.model.CaseModel;

import java.util.List;

public class CaseAdapter extends RecyclerView.Adapter<CaseAdapter.CaseViewHolder> {

    private final List<CaseModel> caseList;
    private final Context context;
    private final OnCaseClickListener onCaseClickListener;

    public interface OnCaseClickListener {
        void onCaseClick(CaseModel caseModel);
    }

    public CaseAdapter(Context context, List<CaseModel> caseList, OnCaseClickListener listener) {
        this.context = context;
        this.caseList = caseList;
        this.onCaseClickListener = listener;
    }

    @NonNull
    @Override
    public CaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_case, parent, false);
        return new CaseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CaseViewHolder holder, int position) {
        CaseModel caseModel = caseList.get(position);
        
        holder.caseTitle.setText(caseModel.getTitle());
        holder.caseDate.setText(caseModel.getDate());
        
        // 在实际应用中，这里应该使用Glide或Picasso等库加载图片
        // 这里简单处理，根据不同的案例ID设置不同的占位图
        switch (caseModel.getId()) {
            case "case1":
                holder.caseImage.setImageResource(R.drawable.ic_image);
                break;
            case "case2":
                holder.caseImage.setImageResource(R.drawable.ic_sd_card);
                break;
            case "case3":
                holder.caseImage.setImageResource(R.drawable.ic_wechat);
                break;
            case "case4":
                holder.caseImage.setImageResource(R.drawable.ic_audio);
                break;
            default:
                holder.caseImage.setImageResource(R.drawable.ic_file);
                break;
        }
        
        holder.itemView.setOnClickListener(v -> {
            if (onCaseClickListener != null) {
                onCaseClickListener.onCaseClick(caseModel);
            }
        });
    }

    @Override
    public int getItemCount() {
        return caseList != null ? caseList.size() : 0;
    }

    static class CaseViewHolder extends RecyclerView.ViewHolder {
        TextView caseTitle;
        TextView caseDate;
        ImageView caseImage;

        public CaseViewHolder(@NonNull View itemView) {
            super(itemView);
            caseTitle = itemView.findViewById(R.id.case_title);
            caseDate = itemView.findViewById(R.id.case_date);
            caseImage = itemView.findViewById(R.id.case_image);
        }
    }
} 