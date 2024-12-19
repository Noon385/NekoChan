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

public class AddUser extends AppCompatActivity implements UserAdapter.OnUserActionListener {

    private TextInputEditText txtUsername, txtPassword;
    private RadioButton rdManager, rdStaff;
    private Button btnAddUser;
    private UserAdapter adapter;
    private List<User> userList = new ArrayList<>();
    private ApiService apiService = RetrofitClient.getClient("https://ea17-1-53-235-143.ngrok-free.app/").create(ApiService.class);

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
        toolbar.setNavigationOnClickListener(v -> finish());


        adapter = new UserAdapter(userList, this, apiService, this);

        btnAddUser.setOnClickListener(v -> addUser());
    }

    private void addUser() {
        String username = txtUsername.getText().toString().trim();
        String password = txtPassword.getText().toString().trim();


        String role;
        if (rdManager.isChecked()) {
            role = "manager";
        } else if (rdStaff.isChecked()) {
            role = "staff";
        } else {
            Toast.makeText(this, "Vui lòng chọn quyền cho người dùng!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if username and password are not empty
        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
            return;
        }


        User newUser = new User();
        newUser.setUsername(username);
        newUser.setPassword(password);
        newUser.setRole(role);


        apiService.addUser(newUser).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    userList.add(response.body());
                    adapter.notifyDataSetChanged();
                    Toast.makeText(AddUser.this, "Người dùng đã được thêm thành công!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(AddUser.this, "Thêm người dùng không thành công!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(AddUser.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onEditUser(User user) {

    }

    @Override
    public void onDeleteUser(User user) {

    }
}
