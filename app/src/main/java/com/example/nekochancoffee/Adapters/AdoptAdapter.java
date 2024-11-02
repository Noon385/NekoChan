package com.example.nekochancoffee.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nekochancoffee.Activities.AdoptDetail;
import com.example.nekochancoffee.Activities.EditAdopt;
import com.example.nekochancoffee.Activities.EditUser;
import com.example.nekochancoffee.ApiService;
import com.example.nekochancoffee.Model.Adopt;
import com.example.nekochancoffee.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdoptAdapter extends RecyclerView.Adapter<AdoptAdapter.AdoptViewHolder> {

    private Context context;
    private List<Adopt> adoptList;
    private ApiService apiService;

    public AdoptAdapter(Context context, List<Adopt> adoptList,ApiService apiService) {
        this.context = context;
        this.adoptList = adoptList;
        this.apiService =apiService;
    }

    @NonNull
    @Override
    public AdoptViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adopt_item, parent, false);
        return new AdoptViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdoptViewHolder holder, int position) {
        Adopt adopt = adoptList.get(position);

        if (adopt.getCat_image() != null && !adopt.getCat_image().isEmpty()) {
            // Chuyển đổi base64 thành Bitmap
            Bitmap bitmap = decodeBase64(adopt.getCat_image());
            holder.imgCat.setImageBitmap(bitmap); // Set hình ảnh cho ImageView
        } else {
            // Nếu không có ảnh, hiển thị hình mặc định
            holder.imgCat.setImageResource(R.drawable.t);
        }
//        Picasso.get().load(adopt.getCat_image()).into(holder.imgCat);

        // Set other data
        holder.txtCatName.setText(adopt.getCat_name());
        holder.txtCatStatus.setText("Status: " + adopt.getCat_status());
        holder.txtCustomerName.setText("Owner: " + adopt.getCustomer_name());
        holder.txtAdoptTime.setText("Adopted on: " + adopt.getAdopt_time());

        holder.itemView.setOnClickListener(v -> {
            // Mở chi tiết thông tin về nhận nuôi
            Intent intent = new Intent(context, AdoptDetail.class);
            intent.putExtra("adopt", adopt);
            context.startActivity(intent);
        });
        holder.itemView.setOnLongClickListener(v -> {
            showPopupMenu(holder.itemView, position, adopt);
            return true;
        });

//        holder.btnDelete.setOnClickListener(v -> {
//            deleteAdopt(adopt.getAdopt_id(),position);
//
//        });
//        holder.btnUpdate.setOnClickListener(v -> {
//            Intent intent = new Intent(context, EditAdopt.class);
//            intent.putExtra("adopt",adopt);
//            context.startActivity(intent);
//        });
    }
    private void showPopupMenu(View view, int position, Adopt adopt) {
        PopupMenu popupMenu = new PopupMenu(context, view);
        popupMenu.getMenuInflater().inflate(R.menu.menu_cat_action_delete_edit, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();
            if (id == R.id.action_delete) {
                deleteAdopt(adopt.getAdopt_id(), position);
                return true;
            } else if (id == R.id.action_edit) {
                Intent intent = new Intent(context, EditAdopt.class);
                intent.putExtra("adopt", adopt);
                context.startActivity(intent);
                return true;
            }
            return false;
        });
        popupMenu.show();
    }

    public void deleteAdopt(int adoptId, int position) {

        apiService.deleteAdopt(adoptId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    adoptList.remove(position);
                    notifyItemRemoved(position);
                    Toast.makeText(context, "Đơn nhận nuôi đã được xóa thành công!", Toast.LENGTH_SHORT).show();
                } else {
                    // Hiển thị thông báo nếu yêu cầu không thành công
                    Toast.makeText(context, "Lỗi: " + adoptId+response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                // Thông báo lỗi khi yêu cầu thất bại
                Toast.makeText(context, "Lỗi khi xóa danh sách nhận nuôi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private Bitmap decodeBase64(String base64Str) {
        byte[] decodedBytes = Base64.decode(base64Str, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }

    @Override
    public int getItemCount() {
        return adoptList.size();
    }

    public static class AdoptViewHolder extends RecyclerView.ViewHolder {
        ImageView imgCat;
        TextView txtCatName, txtCatStatus, txtCustomerName, txtAdoptTime;
//        Button btnUpdate,btnDelete;

        public AdoptViewHolder(@NonNull View itemView) {
            super(itemView);

            imgCat = itemView.findViewById(R.id.imgcat);
            txtCatName = itemView.findViewById(R.id.txtCatName);
            txtCatStatus = itemView.findViewById(R.id.txtCatStatus);
            txtCustomerName = itemView.findViewById(R.id.txtCustomerName);
            txtAdoptTime = itemView.findViewById(R.id.txtTime);
//            btnUpdate = itemView.findViewById(R.id.btnUpdate);
//            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
