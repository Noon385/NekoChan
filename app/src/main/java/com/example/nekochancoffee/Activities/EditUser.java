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
    private User user;
    ApiService apiService  = RetrofitClient.getClient("https://bde3-42-119-80-131.ngrok-free.app/").create(ApiService.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user);


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
                finish();
            }
        });


//        apiService = RetrofitClient.getClient("https://e8da-58-186-28-106.ngrok-free.app/").create(ApiService.class);


        user = (User) getIntent().getSerializableExtra("user");
        if (user != null) {
            txtUsername.setText(user.getUsername());
            txtPassword.setText(user.getPassword());

            if (user.getRole().equalsIgnoreCase("Manager")) {
                rdManager.setChecked(true);
            } else {
                rdStaff.setChecked(true);
            }
        }

        btnEditUser.setOnClickListener(v -> {
            updateUser();
        });
    }

    private void updateUser() {
        String username = txtUsername.getText().toString().trim();
        String password = txtPassword.getText().toString().trim();
        String role = rdManager.isChecked() ? "Manager" : "Staff";
        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        user.setUsername(username);
        user.setPassword(password);
        user.setRole(role);

        apiService.updateUser(user.getId(), user).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(EditUser.this, "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
                    adapter.notifyDataSetChanged();
                    finish();
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
