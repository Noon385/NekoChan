package com.example.nekochancoffee.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nekochancoffee.Adapters.OrderAdapter;
import com.example.nekochancoffee.Adapters.OrderDetailAdapter;
import com.example.nekochancoffee.ApiService;
import com.example.nekochancoffee.Model.Customer;
import com.example.nekochancoffee.Model.Order;
import com.example.nekochancoffee.Model.Payment;
import com.example.nekochancoffee.Model.Table;
import com.example.nekochancoffee.R;
import com.example.nekochancoffee.network.RetrofitClient;
import com.google.gson.JsonObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TableDetail extends AppCompatActivity {

    private TextView txtUsername,  orderId, tableName, catName, customerName, txtTotal, orderTime,customerPoint;
    private RecyclerView recyclerViewDrink;
    private OrderDetailAdapter adapter;
    private Button btnPaymentByCash, btnPaymentByMomo;
    private WebView webViewPayment;
    private ApiService apiService = RetrofitClient.getClient("https://3a18-42-119-149-86.ngrok-free.app/").create(ApiService.class);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_table_detail);
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar_table_detail);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        webViewPayment = findViewById(R.id.webViewPayment);
        webViewPayment.getSettings().setJavaScriptEnabled(true);
        webViewPayment.setVisibility(View.GONE);
        txtUsername = findViewById(R.id.txtUsername);
        orderId = findViewById(R.id.orderId);
        tableName = findViewById(R.id.tableName);
        catName = findViewById(R.id.catName);
        customerName = findViewById(R.id.customerName);
        customerPoint = findViewById(R.id.customerPoint);
        txtTotal = findViewById(R.id.txtTotal);
        orderTime  =findViewById(R.id.orderTime);
        recyclerViewDrink = findViewById(R.id.recyclerViewDrink);
//        btnPaymentByCash = findViewById(R.id.btnPaymentByCash);
//        btnPaymentByMomo = findViewById(R.id.btnPaymentByMomo);
//        btnAddOrderDetail = findViewById(R.id.btnAddOrderDetail);

        recyclerViewDrink.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerViewDrink.setAdapter(adapter);
        int table_id = getIntent().getIntExtra("tableId", -1);
        if (table_id != -1) {
            loadOrder(table_id);
        }

//        btnPaymentByCash.setOnClickListener(v -> {
//            PaymentByCash();
//
//        });
//        btnPaymentByMomo.setOnClickListener(v -> {
//            PaymentByMomo();
//
//        });

    }
//    private void PaymentByCash() {
//
//        Order orderdetail = (Order) getIntent().getSerializableExtra("order");
//        Order order_status = new Order();
//        order_status.setOrder_status("yes");
//
//        BigDecimal totalPrice = orderdetail.getTotal_price();
//        int points = calculatePoints(totalPrice); // Tính điểm khách hàng
//
//        // Cập nhật trạng thái đơn hàng
//        updateOrderStatus(orderdetail.getOrder_id(), order_status, points);
//        Table table = new Table();
//        table.setTable_status("no");
//        apiService.updateTableStatus(orderdetail.getTable_id(),table).enqueue(new Callback<Void>() {
//            @Override
//            public void onResponse(Call<Void> call, Response<Void> response) {
//                Toast.makeText(TableDetail.this, "Cập nhật trạng thái bàn thành công" + orderdetail.getTable_id(), Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onFailure(Call<Void> call, Throwable t) {
//                Toast.makeText(TableDetail.this, "Cập nhật trạng thái bàn thất bại", Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
//
//    private void PaymentByMomo() {
//
//        Order orderdetail = (Order) getIntent().getSerializableExtra("order");
//        BigDecimal totalPrice = orderdetail.getTotal_price();
//        Payment payment = new Payment();
//        payment.setOrder_id(orderdetail.getOrder_id());
//        payment.setTotal_price(totalPrice);
//
//        // Thực hiện thanh toán MoMo
//        apiService.payment(payment).enqueue(new Callback<JsonObject>() {  // Sửa thành JsonObject để nhận kết quả trả về
//            @Override
//            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
//                if (response.isSuccessful()) {
//                    // Lấy URL thanh toán từ JSON trả về
//                    JsonObject responseBody = response.body();
//                    String payUrl = responseBody.get("payUrl").getAsString();
//
//                    // Mở WebView để thanh toán
//                    webViewPayment.setVisibility(View.VISIBLE);
//                    webViewPayment.loadUrl(payUrl);
//
//                    // Cập nhật trạng thái đơn hàng sau khi thanh toán
//                    Order order_status = new Order();
//                    order_status.setOrder_status("yes");
//                    int points = calculatePoints(totalPrice);
//                    updateOrderStatus(orderdetail.getOrder_id(), order_status, points);
//                    Table table = new Table();
//                    table.setTable_status("no");
//                    apiService.updateTableStatus(order_status.getTable_id(),table).enqueue(new Callback<Void>() {
//                        @Override
//                        public void onResponse(Call<Void> call, Response<Void> response) {
//                            Toast.makeText(TableDetail.this, "Cập nhật trạng thái bàn thành công", Toast.LENGTH_SHORT).show();
//                        }
//
//                        @Override
//                        public void onFailure(Call<Void> call, Throwable t) {
//                            Toast.makeText(TableDetail.this, "Cập nhật trạng thái bàn thất bại", Toast.LENGTH_SHORT).show();
//                        }
//                    });
//                } else {
//                    Toast.makeText(TableDetail.this, "Thanh toán thất bại", Toast.LENGTH_SHORT).show();
//                }
//            }
//
//            @Override
//            public void onFailure(Call<JsonObject> call, Throwable t) {
//                Toast.makeText(TableDetail.this, "Lỗi kết nối khi thanh toán", Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
//
//    private void updateOrderStatus(int orderId, Order orderStatus, int points) {
//        Order orderdetail = (Order) getIntent().getSerializableExtra("order");
//        apiService.updateOrderStatus(orderId, orderStatus).enqueue(new Callback<Void>() {
//            @Override
//            public void onResponse(Call<Void> call, Response<Void> response) {
//                if (response.isSuccessful()) {
//                    Toast.makeText(TableDetail.this, "Cập nhật trạng thái đơn hàng thành công", Toast.LENGTH_SHORT).show();
//
//                    // Cập nhật điểm cho khách hàng
//                    apiService.updateCustomerPoints(orderdetail.getCustomer_id(), points).enqueue(new Callback<Void>() {
//                        @Override
//                        public void onResponse(Call<Void> call, Response<Void> response) {
//                            if (response.isSuccessful()) {
//                                Toast.makeText(TableDetail.this, "Cập nhật điểm khách hàng thành công! Điểm: " + points, Toast.LENGTH_SHORT).show();
//                            } else {
//                                Toast.makeText(TableDetail.this, "Cập nhật điểm khách hàng thất bại: " + response.code(), Toast.LENGTH_SHORT).show();
//                            }
//                        }
//
//                        @Override
//                        public void onFailure(Call<Void> call, Throwable t) {
//                            Toast.makeText(TableDetail.this, "Lỗi kết nối khi cập nhật điểm khách hàng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
//                        }
//                    });
//                } else {
//                    Toast.makeText(TableDetail.this, "Cập nhật trạng thái đơn hàng thất bại", Toast.LENGTH_SHORT).show();
//                }
//            }
//
//            @Override
//            public void onFailure(Call<Void> call, Throwable t) {
//                Toast.makeText(TableDetail.this, "Lỗi kết nối khi cập nhật trạng thái đơn hàng", Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
//
//    private int calculatePoints(BigDecimal totalPrice) {
//        BigDecimal points = totalPrice.divide(new BigDecimal("1000"), BigDecimal.ROUND_DOWN);
//        return points.intValue();
//    }
    private void loadOrder(int table_id) {
        apiService.getOrderByTableId(table_id).enqueue(new Callback<List<Order>>() {
            @Override
            public void onResponse(Call<List<Order>> call, Response<List<Order>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    Order order = response.body().get(0);
                    displayOrderDetails(order);
                    loadDrinkDetail(order);
                } else {
                    Toast.makeText(TableDetail.this, "Không có đơn hàng nào!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Order>> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void displayOrderDetails(Order order) {
        orderId.setText(String.valueOf(order.getOrder_id()));
        customerPoint.setText(String.valueOf(order.getCustomer_point()));
        tableName.setText(order.getTable_name());
        catName.setText(order.getCat_name());
        customerName.setText(order.getCustomer_name());
        txtTotal.setText("Tổng: "+String.valueOf(order.getTotal_price())+" VND");
        orderTime.setText(order.getOrder_time());
        txtUsername.setText("Nhân viên: "+order.getUsername());
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
                    Toast.makeText(TableDetail.this, "Không có dữ liệu chi tiết cho đơn hàng", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Order>> call, Throwable t) {
                // Xử lý lỗi khi không thể gọi API
                Toast.makeText(TableDetail.this, "Lỗi khi tải chi tiết đơn hàng", Toast.LENGTH_SHORT).show();
            }
        });
    }

}