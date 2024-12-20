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

import com.example.nekochancoffee.Activities.AddAdopt;
import com.example.nekochancoffee.Activities.AddTable;
import com.example.nekochancoffee.Model.Cat;
import com.example.nekochancoffee.R;

import com.squareup.picasso.Picasso;

import java.util.List;

public class CatAdapter extends RecyclerView.Adapter<CatAdapter.CatViewHolder> {

    private Context context;
    private List<Cat> catList;
    private OnCatActionListener listener;

    public CatAdapter(Context context, List<Cat> catList, OnCatActionListener listener) {
        this.context = context;
        this.catList = catList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cat_item, parent, false);
        return new CatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CatViewHolder holder, int position) {
        Cat cat = catList.get(position);

        // Set tên và trạng thái của mèo
        holder.tvCatName.setText(cat.getCatName());
        holder.tvCatStatus.setText(cat.getCatStatus());
//        holder.tvCatPrice.setText(cat.getCatPrice() + " VND");
        holder.tvCatPrice.setVisibility(View.GONE);




        if (cat.getCatImage() != null && !cat.getCatImage().isEmpty()) {
            Bitmap bitmap = decodeBase64(cat.getCatImage());
            holder.imgCatImage.setImageBitmap(bitmap);
        } else {
            holder.imgCatImage.setImageResource(R.drawable.t);
        }

        if(cat.getCatStatus().toLowerCase().equals("at store")){
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent =new Intent(context, AddAdopt.class);
                    context.startActivity(intent);
                }
            });
        }


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
                                listener.onEditCat(cat);
                            }
                            return true;

                        } else if (item.getItemId() == R.id.action_delete) {
                            if (listener != null) {
                                listener.onDeleteCat(cat);
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
        return catList.size();
    }

    public static class CatViewHolder extends RecyclerView.ViewHolder {
        TextView tvCatName, tvCatStatus, tvCatPrice;
        ImageView imgCatImage;

        public CatViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCatName = itemView.findViewById(R.id.tvCatName);
            tvCatStatus = itemView.findViewById(R.id.tvCatStatus);
            tvCatPrice = itemView.findViewById(R.id.tvCatPrice);
            imgCatImage = itemView.findViewById(R.id.imgCatImage);
        }
    }


    public interface OnCatActionListener {
        void onDeleteCat(Cat cat);
        void onEditCat(Cat cat);
    }
    private Bitmap decodeBase64(String base64Str) {
        byte[] decodedBytes = Base64.decode(base64Str, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }

}

