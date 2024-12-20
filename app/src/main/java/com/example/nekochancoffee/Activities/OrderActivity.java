package com.example.nekochancoffee.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.nekochancoffee.Adapters.OrderAdapter;
import com.example.nekochancoffee.ApiService;
import com.example.nekochancoffee.Model.Drink;
import com.example.nekochancoffee.Model.Order;
import com.example.nekochancoffee.R;
import com.example.nekochancoffee.network.RetrofitClient;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderActivity extends AppCompatActivity {

    private RecyclerView recyclerViewOrder;
    private FloatingActionButton btnAddOrder;
    private OrderAdapter orderAdapter;
    private List<Order> orderList;
    private Drink drink;
    ApiService apiService = RetrofitClient.getClient("https://56fc-118-69-96-226.ngrok-free.app/").create(ApiService.class);

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        drink = (Drink) getIntent().getSerializableExtra("drink");

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar_order);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                startActivity(new Intent(OrderActivity.this, Option.class));
                finish();
            }
        });

        recyclerViewOrder = findViewById(R.id.recyclerViewOrder);
        btnAddOrder = findViewById(R.id.btnAddNewOrder);
        btnAddOrder.setOnClickListener(v -> {
            Intent intent = new Intent(OrderActivity.this,AddOrder.class);
            intent.putExtra("drink",drink);
            startActivity(intent);
        });
        recyclerViewOrder.setLayoutManager(new GridLayoutManager(this, 2));
        loadOrder();
    }

    private void loadOrder() {
        apiService.getOrders().enqueue(new Callback<List<Order>>() {
            @Override
            public void onResponse(Call<List<Order>> call, Response<List<Order>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    orderList = response.body();
                    orderAdapter = new OrderAdapter(OrderActivity.this, orderList, new OrderAdapter.OnOrderActionListener() {
                        @Override
                        public void onDeleteOrder(Order order) {
                            apiService.deleteOrder(order.getOrder_id()).enqueue(new Callback<Void>() {
                                @Override
                                public void onResponse(Call<Void> call, Response<Void> response) {
                                    if (response.isSuccessful()) {
                                        Toast.makeText(OrderActivity.this, "Xóa đơn hàng thành công", Toast.LENGTH_SHORT).show();
                                        loadOrder();
                                    } else {
                                        Toast.makeText(OrderActivity.this, "Không thể xóa đơn hàng", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onFailure(Call<Void> call, Throwable t) {
                                    Toast.makeText(OrderActivity.this, "Lỗi kết nối!", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                        @Override
                        public void onEditOrder(Order order) {
                            // Xử lý chỉnh sửa đơn hàng
                        }

//                        @Override
//                        public void onDetailOrder(Order order) {
//
//                            Intent intent = new Intent(OrderActivity.this, OrderDetail.class);
//                            intent.putExtra("order", order);
//                            startActivity(intent);
//                        }
                    }, drink); 

                    recyclerViewOrder.setAdapter(orderAdapter);
                } else {
                    Toast.makeText(OrderActivity.this, "Không có đơn hàng nào", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Order>> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Lỗi kết nối!", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
