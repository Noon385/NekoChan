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

import com.example.nekochancoffee.Activities.DrinkActivity;
import com.example.nekochancoffee.Model.Category;
import com.example.nekochancoffee.Model.Drink;
import com.example.nekochancoffee.R;

import java.util.List;
public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {
    private Context context;
    private List<Category> categoryList;
    private OnCategoryActionListener listener;

    public CategoryAdapter(Context context, List<Category> categoryList, OnCategoryActionListener listener) {
        this.context = context;
        this.categoryList = categoryList;
        this.listener = listener;
    }
    public CategoryAdapter(Context context, List<Category> categoryList) {
        this.context = context;
        this.categoryList = categoryList;

    }

    public static class CategoryViewHolder extends RecyclerView.ViewHolder {
        public TextView categoryName;
        public ImageView categoryImage;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryName = itemView.findViewById(R.id.txtCategoryName);
            categoryImage = itemView.findViewById(R.id.imgCategoryImage);
        }
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the item layout for each category
        View view = LayoutInflater.from(context).inflate(R.layout.category_item, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        Category category = categoryList.get(position);
        holder.categoryName.setText(category.getCategory_name());
        if (category.getCategory_image() != null && !category.getCategory_image().isEmpty()) {
            Bitmap bitmap = decodeBase64(category.getCategory_image());
            holder.categoryImage.setImageBitmap(bitmap);
        } else {
            holder.categoryImage.setImageResource(R.drawable.t);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DrinkActivity.class);
                intent.putExtra("category_id", category.getCategory_id());
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
                                listener.onEditCategory(category);
                            }
                            return true;
                        } else if (item.getItemId() == R.id.action_delete) {
                            if (listener != null) {
                                listener.onDeleteCategory(category);
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
        return categoryList.size();
    }
    public void updateCategories(List<Category> newCategories) {
        this.categoryList = newCategories;
        notifyDataSetChanged();
    }

    public interface OnCategoryActionListener {
        void onDeleteCategory(Category category);
        void onEditCategory(Category category);
    }

    private Bitmap decodeBase64(String base64Str) {
        byte[] decodedBytes = Base64.decode(base64Str, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }
}

