package com.example.nekochancoffee.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nekochancoffee.Activities.AddOrderDetail;
import com.example.nekochancoffee.Activities.OrderDetail;
import com.example.nekochancoffee.Model.Drink;
import com.example.nekochancoffee.Model.Order;
import com.example.nekochancoffee.R;

import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    private Context context;
    private List<Order> orderList;
    private OnOrderActionListener listener;
    private Drink drink;

    public OrderAdapter(Context context, List<Order> orderList, OnOrderActionListener listener, Drink drink) {
        this.context = context;
        this.orderList = orderList;
        this.listener = listener;
        this.drink = drink;
    }
    public OrderAdapter(Context context, List<Order> orderList) {
        this.context = context;
        this.orderList = orderList;

    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.order_item, parent, false);
        return new OrderViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orderList.get(position);

        // Thiết lập các thông tin đơn hàng
        holder.txtOrderId.setText("Order ID: " + order.getOrder_id());
        holder.txtTableName.setText("Bàn số: " + order.getTable_name());
        holder.txtCustomerName.setText("Khách hàng: " + order.getCustomer_name());
        if(order.getTotal_price() == null){
            holder.txtTotal.setText("Tổng: 0 VND");
        }else holder.txtTotal.setText("Tổng: " + order.getTotal_price() + " VND");
        holder.txtOrderStatus.setText("yes".equals(order.getOrder_status()) ? "Đã thanh toán" : "Chưa thanh toán");



        holder.itemView.setOnLongClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(context, holder.itemView);
            popupMenu.getMenuInflater().inflate(R.menu.menu_cat_action_delete_edit, popupMenu.getMenu());

            popupMenu.setOnMenuItemClickListener(item -> {
                if (listener == null) return false;

                if (item.getItemId() == R.id.action_edit) {
                    listener.onEditOrder(order);
                    return true;
                } else if (item.getItemId() == R.id.action_delete) {
                    listener.onDeleteOrder(order);
                    return true;
                }
//                else if (item.getItemId() == R.id.action_detail) {
//                    listener.onDetailOrder(order);
//                    return true;
//                }
                else {
                    return false;
                }
            });
            popupMenu.show();
            return true;
        });

        holder.itemView.setOnClickListener(v -> {
            if(drink != null){
                Intent intent = new Intent(context, AddOrderDetail.class);
                intent.putExtra("order", order);
                intent.putExtra("drink",drink);
                context.startActivity(intent);
            }else {
                Intent intent = new Intent(context, OrderDetail.class);
                intent.putExtra("order", order);
                context.startActivity(intent);
            }

        });
    }


    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {

        private TextView txtOrderId, txtTableName, txtCustomerName, txtTotal, txtOrderStatus;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            txtOrderId = itemView.findViewById(R.id.txtOrderId);
            txtTableName = itemView.findViewById(R.id.txtTableName);
            txtCustomerName = itemView.findViewById(R.id.txtCustomerName);
            txtTotal = itemView.findViewById(R.id.txtTotal);
            txtOrderStatus = itemView.findViewById(R.id.txtOrderStatus);

        }
    }

    public interface OnOrderActionListener {
        void onDeleteOrder(Order order);
        void onEditOrder(Order order);
//        void onDetailOrder(Order order);
    }

    public void updateOrderList(List<Order> newOrderList) {
        this.orderList = newOrderList;
        notifyDataSetChanged();
    }
}
