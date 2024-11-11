package com.example.nekochancoffee.Adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nekochancoffee.Activities.OrderDetail;
import com.example.nekochancoffee.Model.Order;
import com.example.nekochancoffee.R;

import java.util.List;

public class OrderDetailAdapter extends RecyclerView.Adapter<OrderDetailAdapter.OrderDetailViewHolder> {

    private List<Order> orderDetails;

    public OrderDetailAdapter(List<Order> orderDetails) {
        this.orderDetails = orderDetails;
    }

    @Override
    public OrderDetailViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_drink_item, parent, false);
        return new OrderDetailViewHolder(view);
    }


    @Override
    public void onBindViewHolder(OrderDetailViewHolder holder, int position) {
        Order orderDetail = orderDetails.get(position);
        holder.drinkName.setText(orderDetail.getDrink_name());
        holder.amount.setText(String.valueOf(orderDetail.getAmount()));
//        holder.price.setText(orderDetail.getDrink_price().toString().trim());
        if (orderDetail.getDrink_price() != null) {
            holder.price.setText(orderDetail.getDrink_price().toString()+" VND");
        } else {
            holder.price.setText("N/A");
        }

    }

    @Override
    public int getItemCount() {
        return orderDetails.size();
    }

    public static class OrderDetailViewHolder extends RecyclerView.ViewHolder {
        TextView drinkName, amount, price;

        public OrderDetailViewHolder(View itemView) {
            super(itemView);
            drinkName = itemView.findViewById(R.id.drinkName);
            amount = itemView.findViewById(R.id.drinkAmount);
            price = itemView.findViewById(R.id.drinkPrice);
        }
    }
}
