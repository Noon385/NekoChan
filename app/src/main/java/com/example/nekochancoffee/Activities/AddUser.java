package com.example.nekochancoffee.Activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.nekochancoffee.Model.User;
import com.example.nekochancoffee.Adapters.UserAdapter;
import com.example.nekochancoffee.ApiService;
import com.example.nekochancoffee.R;
import com.example.nekochancoffee.network.RetrofitClient;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddUser extends AppCompatActivity {

    private TextInputEditText txtUsername, txtPassword;
    private RadioButton rdManager, rdStaff;
    private Button btnAddUser;
    private UserAdapter adapter;
    private List<User> userList = new ArrayList<>(); // Danh sách người dùng
    ApiService apiService = RetrofitClient.getClient("https://e4aa-115-75-32-98.ngrok-free.app/").create(ApiService.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user);

        txtUsername = findViewById(R.id.txtUsername);
        txtPassword = findViewById(R.id.txtPassword);
        rdStaff = findViewById(R.id.rdStaff);
        rdManager = findViewById(R.id.rdManager);
        btnAddUser = findViewById(R.id.btnAddUser);

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar_adduser);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Trở về Activity trước đó
            }
        });

        // Khởi tạo adapter với danh sách người dùng và API

        adapter = new UserAdapter(userList, this, apiService);

        btnAddUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addUser();
            }
        });
    }

    private void addUser() {
        String username = txtUsername.getText().toString().trim();
        String password = txtPassword.getText().toString().trim();

        // Kiểm tra xem người dùng đã chọn quyền hay chưa
        String role;
        if (rdManager.isChecked()) {
            role = "manager"; // Quyền quản lý
        } else if (rdStaff.isChecked()) {
            role = "staff"; // Quyền nhân viên
        } else {
            Toast.makeText(this, "Vui lòng chọn quyền cho người dùng!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Kiểm tra xem username và password có trống hay không
        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Tạo đối tượng User
        User newUser = new User();
        newUser.setUsername(username);
        newUser.setPassword(password);
        newUser.setRole(role);

        // Gọi hàm thêm người dùng từ adapter
        adapter.addUser(newUser);
        adapter.notifyDataSetChanged();

        // Đóng activity sau khi thêm người dùng
       finish(); // Quay về màn hình trước
    }
}
