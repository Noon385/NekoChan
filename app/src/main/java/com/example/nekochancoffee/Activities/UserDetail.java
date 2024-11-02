package com.example.nekochancoffee.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.nekochancoffee.Model.User;
import com.example.nekochancoffee.R;

public class UserDetail extends AppCompatActivity {
    TextView txtUsername, txtPassword,txtRole;
    ImageView imgUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_detail);
        txtUsername = findViewById(R.id.txtUsername);
        txtPassword = findViewById(R.id.txtPassword);
        txtRole = findViewById(R.id.txtRole);
        imgUser = findViewById(R.id.imgUser);
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar_user_detail);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());
        User user = (User) getIntent().getSerializableExtra("user");
        if (user != null) {
            loadUserDetails(user);
        }



    }
    private void loadUserDetails(User user) {
        txtUsername.setText("Tên đăng nhập: " + user.getUsername());
        txtPassword.setText("Mật khẩu: "+user.getPassword());
        txtRole.setText("Chức vụ: " + user.getRole());

        if(user.getRole().toLowerCase().equals("staff")){
            imgUser.setImageResource(R.drawable.ic_staff);
        }else imgUser.setImageResource(R.drawable.ic_manager);
    }
}