package com.example.shoujixiufu;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class FeatureAdapter extends RecyclerView.Adapter<FeatureAdapter.FeatureViewHolder> {

    private List<FeatureModel> features;
    private OnFeatureClickListener listener;

    public interface OnFeatureClickListener {
        void onFeatureClick(FeatureModel feature);
    }

    public FeatureAdapter(List<FeatureModel> features, OnFeatureClickListener listener) {
        this.features = features;
        this.listener = listener;
    }

    @NonNull
    @Override
    public FeatureViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_feature_card, parent, false);
        return new FeatureViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FeatureViewHolder holder, int position) {
        FeatureModel feature = features.get(position);
        
        holder.icon.setImageResource(feature.getIconResId());
        holder.title.setText(feature.getTitle());
        holder.description.setText(feature.getDescription());
        holder.badge.setText(feature.getBadgeText());
        
        // 检查是否为图片缩放或裁剪功能，并设置为黄色
        if (feature.getTitle().equals("图片缩放") || feature.getTitle().equals("图片裁剪")) {
            holder.card.setBackgroundResource(R.drawable.bg_card_free);
            holder.badge.setBackgroundResource(R.drawable.badge_free);
            holder.title.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.text_primary));
            holder.description.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.text_primary));
        }
        // 其他高级功能设置为蓝色
        else if (feature.isPremium()) {
            holder.card.setBackgroundResource(R.drawable.bg_card_premium);
            holder.badge.setBackgroundResource(R.drawable.badge_premium);
            holder.title.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.white));
            holder.description.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.white));
        }
        // 免费功能
        else {
            holder.card.setBackgroundResource(R.drawable.bg_card_free);
            holder.badge.setBackgroundResource(R.drawable.badge_free);
            holder.title.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.text_primary));
            holder.description.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.text_primary));
        }
        
        holder.card.setOnClickListener(v -> {
            if (listener != null) {
                listener.onFeatureClick(feature);
            }
        });
    }

    @Override
    public int getItemCount() {
        return features.size();
    }

    static class FeatureViewHolder extends RecyclerView.ViewHolder {
        CardView card;
        ImageView icon;
        TextView title;
        TextView description;
        TextView badge;

        public FeatureViewHolder(@NonNull View itemView) {
            super(itemView);
            card = itemView.findViewById(R.id.feature_card);
            icon = itemView.findViewById(R.id.feature_icon);
            title = itemView.findViewById(R.id.feature_title);
            description = itemView.findViewById(R.id.feature_desc);
            badge = itemView.findViewById(R.id.feature_badge);
        }
    }
} 