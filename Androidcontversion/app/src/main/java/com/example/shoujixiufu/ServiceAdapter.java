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

public class ServiceAdapter extends RecyclerView.Adapter<ServiceAdapter.ServiceViewHolder> {

    private List<ServiceModel> services;
    private OnServiceClickListener listener;

    public interface OnServiceClickListener {
        void onServiceClick();
    }

    public ServiceAdapter(List<ServiceModel> services, OnServiceClickListener listener) {
        this.services = services;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ServiceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_service_card, parent, false);
        return new ServiceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ServiceViewHolder holder, int position) {
        ServiceModel service = services.get(position);
        
        holder.icon.setImageResource(service.getIconResId());
        holder.title.setText(service.getTitle());
        
        holder.card.setOnClickListener(v -> listener.onServiceClick());
    }

    @Override
    public int getItemCount() {
        return services.size();
    }

    static class ServiceViewHolder extends RecyclerView.ViewHolder {
        CardView card;
        ImageView icon;
        TextView title;

        public ServiceViewHolder(@NonNull View itemView) {
            super(itemView);
            card = itemView.findViewById(R.id.service_card);
            icon = itemView.findViewById(R.id.service_icon);
            title = itemView.findViewById(R.id.service_title);
        }
    }
} 