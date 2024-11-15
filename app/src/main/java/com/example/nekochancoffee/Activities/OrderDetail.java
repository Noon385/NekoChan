package com.example.nekochancoffee.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
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
import com.example.nekochancoffee.Model.Payment;
import com.example.nekochancoffee.R;
import com.example.nekochancoffee.network.RetrofitClient;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.JsonObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderDetail extends AppCompatActivity {
    private TextView  txtUsername,  orderId, tableName, catName, customerName, txtTotal;
    private RecyclerView recyclerViewDrink;
    private OrderDetailAdapter adapter;
    private Button btnPaymentByCash, btnPaymentByMomo;
   private WebView webViewPayment;
    private Drink drink;

    private ApiService apiService = RetrofitClient.getClient("https://e45d-42-115-42-67.ngrok-free.app/").create(ApiService.class);


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

        webViewPayment = findViewById(R.id.webViewPayment);
        webViewPayment.getSettings().setJavaScriptEnabled(true);
        webViewPayment.setVisibility(View.GONE);
        txtUsername = findViewById(R.id.txtUsername);
        orderId = findViewById(R.id.orderId);
        tableName = findViewById(R.id.tableName);
        catName = findViewById(R.id.catName);
        customerName = findViewById(R.id.customerName);
        txtTotal = findViewById(R.id.txtTotal);
        recyclerViewDrink = findViewById(R.id.recyclerViewDrink);
        btnPaymentByCash = findViewById(R.id.btnPaymentByCash);
        btnPaymentByMomo = findViewById(R.id.btnPaymentByMomo);
//        btnAddOrderDetail = findViewById(R.id.btnAddOrderDetail);
         Order orderdetail = (Order) getIntent().getSerializableExtra("order");
        recyclerViewDrink.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerViewDrink.setAdapter(adapter);

        if (orderdetail != null) {
            loadOrderDetail(orderdetail);
            loadDrinkDetail(orderdetail);
        }
        btnPaymentByCash.setOnClickListener(v -> {
            PaymentByCash();

        });
        btnPaymentByMomo.setOnClickListener(v -> {
            PaymentByMomo();



        });


    }
    private void loadOrderDetail(Order order) {
        // Populate TextViews with order data
        orderId.setText(String.valueOf(order.getOrder_id()));
        tableName.setText(order.getTable_name());
        catName.setText(order.getCat_name());
        customerName.setText(order.getCustomer_name());
        txtUsername.setText("Nhân viên: "+order.getUsername());
        txtTotal.setText("Tổng: " + order.getTotal_price() + " VND");

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
    private void PaymentByCash() {

        Order orderdetail = (Order) getIntent().getSerializableExtra("order");
        Order order_status = new Order();
        order_status.setOrder_status("yes");

        BigDecimal totalPrice = orderdetail.getTotal_price();
        int points = calculatePoints(totalPrice); // Tính điểm khách hàng

        // Cập nhật trạng thái đơn hàng
        updateOrderStatus(orderdetail.getOrder_id(), order_status, points);
    }

    private void PaymentByMomo() {

        Order orderdetail = (Order) getIntent().getSerializableExtra("order");
        BigDecimal totalPrice = orderdetail.getTotal_price();
        Payment payment = new Payment();
        payment.setOrder_id(orderdetail.getOrder_id());
        payment.setTotal_price(totalPrice);

        // Thực hiện thanh toán MoMo
        apiService.payment(payment).enqueue(new Callback<JsonObject>() {  // Sửa thành JsonObject để nhận kết quả trả về
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    // Lấy URL thanh toán từ JSON trả về
                    JsonObject responseBody = response.body();
                    String payUrl = responseBody.get("payUrl").getAsString();

                    // Mở WebView để thanh toán
                    webViewPayment.setVisibility(View.VISIBLE);
                    webViewPayment.loadUrl(payUrl);

                    // Cập nhật trạng thái đơn hàng sau khi thanh toán
                    Order order_status = new Order();
                    order_status.setOrder_status("yes");
                    int points = calculatePoints(totalPrice);
                    updateOrderStatus(orderdetail.getOrder_id(), order_status, points);
                } else {
                    Toast.makeText(OrderDetail.this, "Thanh toán thất bại", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(OrderDetail.this, "Lỗi kết nối khi thanh toán", Toast.LENGTH_SHORT).show();
            }
        });
    }


    // Phương thức cập nhật trạng thái đơn hàng và điểm khách hàng
    private void updateOrderStatus(int orderId, Order orderStatus, int points) {
        Order orderdetail = (Order) getIntent().getSerializableExtra("order");
        apiService.updateOrderStatus(orderId, orderStatus).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(OrderDetail.this, "Cập nhật trạng thái đơn hàng thành công", Toast.LENGTH_SHORT).show();

                    // Cập nhật điểm cho khách hàng
                    Customer customer = new Customer();
                    customer.setCustomer_point(String.valueOf(points));
                    apiService.updateCustomer(orderdetail.getCustomer_id(), customer).enqueue(new Callback<Customer>() {
                        @Override
                        public void onResponse(Call<Customer> call, Response<Customer> response) {
                            if (response.isSuccessful()) {
                                Toast.makeText(OrderDetail.this, "Cập nhật điểm khách hàng thành công!", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(OrderDetail.this, "Cập nhật điểm khách hàng thất bại", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<Customer> call, Throwable t) {
                            Toast.makeText(OrderDetail.this, "Lỗi khi cập nhật điểm khách hàng", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(OrderDetail.this, "Cập nhật trạng thái đơn hàng thất bại", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(OrderDetail.this, "Lỗi kết nối khi cập nhật trạng thái đơn hàng", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Phương thức tính điểm từ tổng giá trị
    private int calculatePoints(BigDecimal totalPrice) {
        BigDecimal points = totalPrice.divide(new BigDecimal("1000"), BigDecimal.ROUND_DOWN);
        return points.intValue();
    }

}