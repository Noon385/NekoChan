package com.example.nekochancoffee.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nekochancoffee.Activities.EditUser;
import com.example.nekochancoffee.Activities.UserDetail;
import com.example.nekochancoffee.ApiService;
import com.example.nekochancoffee.Model.Cat;
import com.example.nekochancoffee.Model.User;
import com.example.nekochancoffee.R;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private List<User> userList;
    private Context context;
    private ApiService userApi;
    private  OnUserActionListener listener;

    public UserAdapter(List<User> userList, Context context, ApiService userApi, OnUserActionListener listener) {
        this.userList = userList;
        this.context = context;
        this.userApi = userApi;
        this.listener = listener;
    }


    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_item, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = userList.get(position);
        holder.tvUsername.setText("Tên đăng nhập: "+user.getUsername());
        holder.tvRole.setText("Chức vụ: "+user.getRole());
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, UserDetail.class);
            intent.putExtra("user", user); // Assuming User class implements Serializable or Parcelable
            context.startActivity(intent);
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
                            // Xử lý sửa mèo
                            if (listener != null) {
                                listener.onEditUser(user);
                            }
                            return true;

                        } else if (item.getItemId() == R.id.action_delete) {
                            // Xử lý xóa mèo
                            if (listener != null) {
                                listener.onDeleteUser(user);
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

//        // Nút xóa người dùng
//        holder.btnDelete.setOnClickListener(v -> {
//            deleteUser(user.getId(), position);
//            Toast.makeText(context, "ID là: " +user.getId(), Toast.LENGTH_SHORT).show();
//        });
//
//        // Nút cập nhật thông tin người dùng
//        holder.btnUpdate.setOnClickListener(v -> {
//            Intent intent = new Intent(context, EditUser.class);
//            intent.putExtra("user",user);
//            context.startActivity(intent);
////            updateUser(user);
//        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView tvUsername, tvRole;
//        Button btnDelete, btnUpdate;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUsername = itemView.findViewById(R.id.txtUsername);
            tvRole = itemView.findViewById(R.id.txtRole);
//            btnDelete = itemView.findViewById(R.id.btnDelete);
//            btnUpdate = itemView.findViewById(R.id.btnUpdate);
        }
    }

    // Thêm người dùng mới
    public void addUser(User user) {
        userApi.addUser(user).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    userList.add(response.body());
                    //notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {

            }
        });
    }

    // Cập nhật thông tin người dùng
    public void updateUser(User user) {
        userApi.updateUser(user.getId(), user).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    notifyDataSetChanged();

                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                // Xử lý khi yêu cầu thất bại
            }
        });
    }

    // Xóa người dùng
    public void deleteUser(int userId, int position) {
        // Gọi API để xóa người dùng
        userApi.deleteUser(userId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    // Xóa người dùng khỏi danh sách và cập nhật giao diện
                    userList.remove(position);
                    notifyItemRemoved(position);
                    Toast.makeText(context, "Người dùng đã được xóa thành công!", Toast.LENGTH_SHORT).show();
                } else {
                    // Hiển thị thông báo nếu yêu cầu không thành công
                    Toast.makeText(context, "Lỗi: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                // Thông báo lỗi khi yêu cầu thất bại
                Toast.makeText(context, "Lỗi khi xóa người dùng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    public interface OnUserActionListener {
        void onDeleteUser(User user);
        void onEditUser(User user);
    }

}
