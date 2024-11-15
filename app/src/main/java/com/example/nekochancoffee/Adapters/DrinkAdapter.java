package com.example.nekochancoffee.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nekochancoffee.Activities.AddOrder;
import com.example.nekochancoffee.Activities.OrderActivity;
import com.example.nekochancoffee.Model.Drink;
import com.example.nekochancoffee.Model.Order;
import com.example.nekochancoffee.R;

import java.util.List;

public class DrinkAdapter extends RecyclerView.Adapter<DrinkAdapter.DrinkViewHolder> {

    private Context context;
    private List<Drink> drinkList;

    private OnDrinkActionListener listener;
    public DrinkAdapter(Context context, List<Drink> drinkList, OnDrinkActionListener listener) {
        this.context = context;
        this.drinkList = drinkList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public DrinkViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.drink_item, parent, false);
        return new DrinkViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DrinkViewHolder holder, int position) {
        Drink drink = drinkList.get(position);

        holder.txtDrinkName.setText(drink.getDrink_name());
        if ("Remain".equals(drink.getDrink_status())) {
            holder.txtDrinkStatus.setText("Tình trạng: Còn món");
        } else if ("OutOfStock".equals(drink.getDrink_status())) {
            holder.txtDrinkStatus.setText("Tình trạng: Hết món");
        }
        holder.txtDrinkPrice.setText(drink.getDrink_price().toString() +" VND");

        if (drink.getDrink_image() != null && !drink.getDrink_image().isEmpty()) {
            // Chuyển đổi base64 thành Bitmap
            Bitmap bitmap = decodeBase64(drink.getDrink_image());
            holder.imgDrink.setImageBitmap(bitmap); // Set hình ảnh cho ImageView
        } else {
            // Nếu không có ảnh, hiển thị hình mặc định
            holder.imgDrink.setImageResource(R.drawable.t);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    Intent intent = new Intent(context, OrderActivity.class);
                    intent.putExtra("drink",drink);
                    context.startActivity(intent);


            }

        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                PopupMenu popupMenu = new PopupMenu(context, holder.itemView);
                popupMenu.getMenuInflater().inflate(R.menu.menu_cat_action_delete_edit, popupMenu.getMenu());

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if (item.getItemId() == R.id.action_edit) {
                            if (listener != null) {
                                listener.onEditDrink(drink);
                            }
                            return true;
                        } else if (item.getItemId() == R.id.action_delete) {
                            if (listener != null) {
                                listener.onDeleteDrink(drink);
                            }
                            return true;
                        } else {
                            return false;
                        }
                    }
                });
                popupMenu.show();
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return drinkList.size();
    }

    public static class DrinkViewHolder extends RecyclerView.ViewHolder {
        ImageView imgDrink;
        TextView txtDrinkName, txtDrinkStatus, txtDrinkPrice;

        public DrinkViewHolder(@NonNull View itemView) {
            super(itemView);
            imgDrink = itemView.findViewById(R.id.imgDrink);
            txtDrinkName = itemView.findViewById(R.id.txtDrinkName);
            txtDrinkStatus = itemView.findViewById(R.id.txtDrinkStatus);
            txtDrinkPrice = itemView.findViewById(R.id.txtDrinkPrice);
        }
    }
    public interface OnDrinkActionListener {
        void onDeleteDrink(Drink drink);
        void onEditDrink(Drink drink);
    }
    private Bitmap decodeBase64(String base64Str) {
        byte[] decodedBytes = Base64.decode(base64Str, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }
}
