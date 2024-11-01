package com.example.nekochancoffee.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.nekochancoffee.Adapters.UserAdapter;
import com.example.nekochancoffee.ApiService;
import com.example.nekochancoffee.Model.User;
import com.example.nekochancoffee.R;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nekochancoffee.network.RetrofitClient;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    private List<User> userList;
//    private ApiService userApi;
    private FloatingActionButton fabAddUser;
    ApiService apiService  = RetrofitClient.getClient("https://dbd8-1-53-113-145.ngrok-free.app/").create(ApiService.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        recyclerView = findViewById(R.id.recyclerViewUsers);
        fabAddUser = findViewById(R.id.fabAddUser);


//        apiService = RetrofitClient.getClient("https://e8da-58-186-28-106.ngrok-free.app/").create(ApiService.class);

        // Cấu hình RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Gọi API để lấy danh sách người dùng
        getAllUsers();

        // Nút thêm người dùng
        fabAddUser.setOnClickListener(v -> {
            // Điều hướng đến Activity thêm người dùng (nếu có)
            startActivity(new Intent(UserActivity.this,AddUser.class));
        });

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar_user);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Trở về Activity trước đó
            }
        });
    }

    // Hàm lấy danh sách người dùng từ API
    private void getAllUsers() {
        apiService.getAllUsers().enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if (response.isSuccessful()) {
                    userList = response.body();
                    // Gán danh sách người dùng vào adapter và hiển thị lên RecyclerView
                    userAdapter = new UserAdapter(userList, UserActivity.this, apiService);
                    recyclerView.setAdapter(userAdapter);

                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                // Xử lý lỗi khi không lấy được dữ liệu
            }
        });
    }
}