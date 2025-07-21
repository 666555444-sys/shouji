package com.example.shoujixiufu;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    private List<Order> orderList;
    private Context context;
    private OrderItemClickListener listener;

    public OrderAdapter(List<Order> orderList, Context context) {
        this.orderList = orderList;
        this.context = context;
    }

    public void setOrderItemClickListener(OrderItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orderList.get(position);
        
        holder.orderTitle.setText(order.getTitle());
        holder.orderStatus.setText(order.getStatus());
        holder.orderNumber.setText(order.getOrderNumber());
        holder.orderTime.setText(order.getOrderTime());
        holder.orderAmount.setText("¥" + order.getAmount());
        
        // 根据订单状态设置不同的颜色
        switch (order.getStatus()) {
            case "处理中":
                holder.orderStatus.setTextColor(context.getResources().getColor(R.color.primary_color));
                break;
            case "已完成":
                holder.orderStatus.setTextColor(context.getResources().getColor(R.color.success_color));
                break;
            case "已取消":
                holder.orderStatus.setTextColor(context.getResources().getColor(R.color.text_tertiary));
                break;
            case "待支付":
                holder.orderStatus.setTextColor(context.getResources().getColor(R.color.warning_color));
                break;
        }
        
        // 设置按钮点击事件
        holder.btnOrderDetail.setOnClickListener(v -> {
            if (listener != null) {
                listener.onOrderDetailClick(order);
            }
        });
        
        holder.btnContactService.setOnClickListener(v -> {
            if (listener != null) {
                listener.onContactServiceClick(order);
            }
        });
    }

    @Override
    public int getItemCount() {
        return orderList == null ? 0 : orderList.size();
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView orderTitle, orderStatus, orderNumber, orderTime, orderAmount;
        Button btnOrderDetail, btnContactService;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            orderTitle = itemView.findViewById(R.id.order_title);
            orderStatus = itemView.findViewById(R.id.order_status);
            orderNumber = itemView.findViewById(R.id.order_number);
            orderTime = itemView.findViewById(R.id.order_time);
            orderAmount = itemView.findViewById(R.id.order_amount);
            btnOrderDetail = itemView.findViewById(R.id.btn_order_detail);
            btnContactService = itemView.findViewById(R.id.btn_contact_service);
        }
    }

    public interface OrderItemClickListener {
        void onOrderDetailClick(Order order);
        void onContactServiceClick(Order order);
    }
} 