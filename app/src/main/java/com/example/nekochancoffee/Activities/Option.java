package com.example.nekochancoffee.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.nekochancoffee.Model.User;
import com.example.nekochancoffee.R;

public class Option extends AppCompatActivity {

    private CardView cardStaff, cardCategory, cardProduct, cardCustomer, cardLogout, cardOrder,cardAdopt, cardStatistic;
    TextView txtUsername;
    private SharedPreferences sharedPreferences;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_option);
        cardStaff = findViewById(R.id.cardStaff);
        cardCategory = findViewById(R.id.cardCategory);
        cardProduct = findViewById(R.id.cardProduct);
        cardCustomer = findViewById(R.id.cardCustomer);
        cardLogout = findViewById(R.id.cardLogout);
        cardOrder = findViewById(R.id.cardOrder);
        cardAdopt = findViewById(R.id.cardAdopt);
        cardStatistic = findViewById(R.id.cardStatistic);
        txtUsername = findViewById(R.id.txtUsername);

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar_option);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Trở về Activity trước đó
            }
        });

        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String role = sharedPreferences.getString("role", "staff");
        String username = sharedPreferences.getString("username","");
        txtUsername.setText("Xin chào "+username.toString());

        // Kiểm tra vai trò của người dùng
        if (role.equals("manager")) {
            // Hiển thị các chức năng cho quản lý
            showManagerFeatures();
        } else if (role.equals("staff")) {
            // Hiển thị các chức năng cho nhân viên
            showStaffFeatures();
        }

        cardStaff.setOnClickListener(v -> {
            Intent intent = new Intent(Option.this, UserActivity.class);
            startActivity(intent);
        });

        cardCategory.setOnClickListener(v -> {
            Intent intent = new Intent(Option.this, CategoryActivity.class);
            startActivity(intent);
        });

        cardProduct.setOnClickListener(v -> {
            Intent intent = new Intent(Option.this, DrinkActivity.class);
            startActivity(intent);
        });

        cardCustomer.setOnClickListener(v -> {
            Intent intent = new Intent(Option.this, CustomerActivity.class);
            startActivity(intent);
        });

        cardOrder.setOnClickListener(v -> {
            Intent intent = new Intent(Option.this, OrderActivity.class);
            startActivity(intent);
        });
        cardAdopt.setOnClickListener(v -> {
            Intent intent = new Intent(Option.this, AdoptActivity.class);
            startActivity(intent);
        });

        cardStatistic.setOnClickListener(v -> {
            Intent intent = new Intent(Option.this, StatisticActivity.class);
            startActivity(intent);
        });
        cardLogout.setOnClickListener(v -> {

            Intent intent = new Intent(Option.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Xóa ngăn xếp và mở LoginActivity
            startActivity(intent);
            finish();
        });
    }
    private void showManagerFeatures() {
        cardStaff.setVisibility(View.VISIBLE);
        cardCategory.setVisibility(View.VISIBLE);
        cardProduct.setVisibility(View.VISIBLE);
        cardCustomer.setVisibility(View.VISIBLE);
        cardLogout.setVisibility(View.VISIBLE);
        cardOrder.setVisibility(View.VISIBLE);
        cardAdopt.setVisibility(View.VISIBLE);
        cardStatistic.setVisibility(View.VISIBLE);
    }


    private void showStaffFeatures() {
        cardStaff.setVisibility(View.GONE);
        cardCategory.setVisibility(View.VISIBLE);
        cardProduct.setVisibility(View.VISIBLE);
        cardCustomer.setVisibility(View.VISIBLE);
        cardOrder.setVisibility(View.VISIBLE);
        cardLogout.setVisibility(View.VISIBLE);
        cardAdopt.setVisibility(View.VISIBLE);
        cardStatistic.setVisibility(View.GONE);
    }

}