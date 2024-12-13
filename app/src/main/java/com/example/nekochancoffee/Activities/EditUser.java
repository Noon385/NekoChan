package com.example.nekochancoffee.Activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.nekochancoffee.Adapters.UserAdapter;
import com.example.nekochancoffee.ApiService;
import com.example.nekochancoffee.Model.User;
import com.example.nekochancoffee.R;
import com.example.nekochancoffee.network.RetrofitClient;
import com.google.android.material.textfield.TextInputEditText;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditUser extends AppCompatActivity {

    private TextInputEditText txtUsername, txtPassword;
    private RadioGroup rgRole;
    private RadioButton rdManager, rdStaff;
    private Button btnEditUser;
//    private ApiService apiService;
    UserAdapter adapter;
    private User user; // Người dùng cần chỉnh sửa
    ApiService apiService  = RetrofitClient.getClient("https://bde3-42-119-80-131.ngrok-free.app/").create(ApiService.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user);

        // Khởi tạo các thành phần giao diện
        txtUsername = findViewById(R.id.txtUsername);
        txtPassword = findViewById(R.id.txtPassword);
        rgRole = findViewById(R.id.rg_addstaff_Quyen);
        rdManager = findViewById(R.id.rdManager);
        rdStaff = findViewById(R.id.rdStaff);
        btnEditUser = findViewById(R.id.btnEditUser);
        //Toolbar toolbar = findViewById(R.id.toolbar_edituser);
        //setSupportActionBar(toolbar);
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar_edituser);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Trở về Activity trước đó
            }
        });

        // Khởi tạo ApiService
//        apiService = RetrofitClient.getClient("https://e8da-58-186-28-106.ngrok-free.app/").create(ApiService.class);

        // Nhận dữ liệu người dùng từ intent
        user = (User) getIntent().getSerializableExtra("user");

        if (user != null) {
            // Hiển thị dữ liệu người dùng vào các trường
            txtUsername.setText(user.getUsername());
            txtPassword.setText(user.getPassword());

            if (user.getRole().equalsIgnoreCase("Manager")) {
                rdManager.setChecked(true);
            } else {
                rdStaff.setChecked(true);
            }
        }

        // Xử lý sự kiện khi nhấn nút sửa người dùng
        btnEditUser.setOnClickListener(v -> {
            updateUser();
        });
    }

    private void updateUser() {
        // Lấy giá trị từ các trường nhập liệu
        String username = txtUsername.getText().toString().trim();
        String password = txtPassword.getText().toString().trim();
        String role = rdManager.isChecked() ? "Manager" : "Staff";

        // Kiểm tra dữ liệu hợp lệ
        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        // Cập nhật thông tin người dùng
        user.setUsername(username);
        user.setPassword(password);
        user.setRole(role);

        // Gọi API để cập nhật người dùng
        apiService.updateUser(user.getId(), user).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(EditUser.this, "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
                    adapter.notifyDataSetChanged();
                    finish(); // Đóng activity sau khi cập nhật thành công
                } else {
                    Toast.makeText(EditUser.this, "Cập nhật thất bại: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(EditUser.this, "Lỗi khi cập nhật: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
