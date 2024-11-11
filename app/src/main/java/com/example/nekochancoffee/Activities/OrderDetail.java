package com.example.nekochancoffee.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nekochancoffee.Adapters.OrderDetailAdapter;
import com.example.nekochancoffee.ApiService;
import com.example.nekochancoffee.Model.Adopt;
import com.example.nekochancoffee.Model.Customer;
import com.example.nekochancoffee.Model.Drink;
import com.example.nekochancoffee.Model.Order;
import com.example.nekochancoffee.R;
import com.example.nekochancoffee.network.RetrofitClient;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderDetail extends AppCompatActivity {
    private TextView  txtUsername,  orderId, tableName, catName, customerName, txtTotal;
    private RecyclerView recyclerViewDrink;
    private OrderDetailAdapter adapter;
    private FloatingActionButton btnAddOrderDetail;
    private Drink drink;
    private ApiService apiService = RetrofitClient.getClient("https://4dfb-58-186-47-131.ngrok-free.app/").create(ApiService.class);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);
        RecyclerView recyclerView = findViewById(R.id.recyclerViewDrink);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar_order_detail);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        txtUsername = findViewById(R.id.txtUsername);
        orderId = findViewById(R.id.orderId);
        tableName = findViewById(R.id.tableName);
        catName = findViewById(R.id.catName);
        customerName = findViewById(R.id.customerName);
        txtTotal = findViewById(R.id.txtTotal);
        recyclerViewDrink = findViewById(R.id.recyclerViewDrink);
        btnAddOrderDetail = findViewById(R.id.btnAddOrderDetail);

        recyclerViewDrink.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerViewDrink.setAdapter(adapter);
        Order orderdetail = (Order) getIntent().getSerializableExtra("order");
        if (orderdetail != null) {
            loadOrderDetail(orderdetail);
            loadDrinkDetail(orderdetail);

        }
        btnAddOrderDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(OrderDetail.this,AddOrderDetail.class);
                intent.putExtra("order",orderdetail);
                startActivity(intent);
            }
        });


    }
    private void loadOrderDetail(Order order) {
        // Populate TextViews with order data
        orderId.setText(String.valueOf(order.getOrder_id()));
        tableName.setText(order.getTable_name());
        catName.setText(order.getCat_name());
        customerName.setText(order.getCustomer_name());
        txtUsername.setText("Nhân viên: "+order.getUsername());
        txtTotal.setText("Tổng: " + order.getTotal() + " VND");

    }
    private void loadDrinkDetail(Order order){

        Call<List<Order>> call = apiService.getOrderDetailById(order.getOrder_id());
        call.enqueue(new Callback<List<Order>>() {
            @Override
            public void onResponse(Call<List<Order>> call, Response<List<Order>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Order> orderDetails = response.body();

                    // Thiết lập adapter với dữ liệu lấy được từ API
                    adapter = new OrderDetailAdapter(orderDetails);
                    recyclerViewDrink.setAdapter(adapter);
                } else {
                    // Xử lý lỗi khi API trả về nhưng không có dữ liệu
                    Toast.makeText(OrderDetail.this, "Không có dữ liệu chi tiết cho đơn hàng", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Order>> call, Throwable t) {
                // Xử lý lỗi khi không thể gọi API
                Toast.makeText(OrderDetail.this, "Lỗi khi tải chi tiết đơn hàng", Toast.LENGTH_SHORT).show();
            }
        });
    }

}