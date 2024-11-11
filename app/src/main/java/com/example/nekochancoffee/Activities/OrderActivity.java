package com.example.nekochancoffee.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderActivity extends AppCompatActivity {

    private RecyclerView recyclerViewOrder;
    private OrderAdapter orderAdapter;
    private List<Order> orderList;
    private Drink drink;
    ApiService apiService = RetrofitClient.getClient("https://4dfb-58-186-47-131.ngrok-free.app/").create(ApiService.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        // Lấy đối tượng Drink từ Intent
        drink = (Drink) getIntent().getSerializableExtra("drink");

        // Thiết lập Toolbar
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar_order);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        recyclerViewOrder = findViewById(R.id.recyclerViewOrder);
        recyclerViewOrder.setLayoutManager(new GridLayoutManager(this, 2));

        // Tải dữ liệu đơn hàng
        loadOrder();
    }

    private void loadOrder() {
        apiService.getOrders().enqueue(new Callback<List<Order>>() {
            @Override
            public void onResponse(Call<List<Order>> call, Response<List<Order>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    orderList = response.body();

                    // Sử dụng đúng thứ tự tham số khi khởi tạo OrderAdapter
                    orderAdapter = new OrderAdapter(OrderActivity.this, orderList, new OrderAdapter.OnOrderActionListener() {
                        @Override
                        public void onDeleteOrder(Order order) {
                            // Gọi API xóa đơn hàng
//                            apiService.deleteOrder(order.getOrder_id()).enqueue(new Callback<Void>() {
//                                @Override
//                                public void onResponse(Call<Void> call, Response<Void> response) {
//                                    if (response.isSuccessful()) {
//                                        Toast.makeText(OrderActivity.this, "Xóa đơn hàng thành công", Toast.LENGTH_SHORT).show();
//                                        loadOrder(); // Tải lại danh sách đơn hàng sau khi xóa
//                                    } else {
//                                        Toast.makeText(OrderActivity.this, "Không thể xóa đơn hàng", Toast.LENGTH_SHORT).show();
//                                    }
//                                }
//
//                                @Override
//                                public void onFailure(Call<Void> call, Throwable t) {
//                                    Toast.makeText(OrderActivity.this, "Lỗi kết nối!", Toast.LENGTH_SHORT).show();
//                                }
//                            });
                        }

                        @Override
                        public void onEditOrder(Order order) {
                            // Xử lý chỉnh sửa đơn hàng
                        }

                        @Override
                        public void onDetailOrder(Order order) {
                            // Chuyển đến màn hình chi tiết đơn hàng
                            Intent intent = new Intent(OrderActivity.this, OrderDetail.class);
                            intent.putExtra("order", order);
                            startActivity(intent);
                        }
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
