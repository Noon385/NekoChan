package com.example.nekochancoffee.Activities;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.os.Environment;
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
import com.example.nekochancoffee.Model.Table;
import com.example.nekochancoffee.R;
import com.example.nekochancoffee.network.RetrofitClient;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.JsonObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderDetail extends AppCompatActivity {
    private TextView  txtUsername,  orderId, tableName, catName, customerName, txtTotal, orderTime,customerPoint;
    private RecyclerView recyclerViewDrink;
    private OrderDetailAdapter adapter;
    private Button btnPaymentByCash, btnPaymentByMomo,btnExport;
   private WebView webViewPayment;
    private Drink drink;
    private List<Order> orderDetails;

    private ApiService apiService = RetrofitClient.getClient("https://56fc-118-69-96-226.ngrok-free.app/").create(ApiService.class);


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
        customerPoint = findViewById(R.id.customerPoint);
        txtTotal = findViewById(R.id.txtTotal);
        orderTime  =findViewById(R.id.orderTime);
        recyclerViewDrink = findViewById(R.id.recyclerViewDrink);
        btnPaymentByCash = findViewById(R.id.btnPaymentByCash);
        btnPaymentByMomo = findViewById(R.id.btnPaymentByMomo);
        btnExport = findViewById(R.id.btnExport);

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
        btnExport.setOnClickListener(v -> {
            Order order = (Order) getIntent().getSerializableExtra("order");
            if (order != null) {
                generatePdf(order);
            } else {
                Toast.makeText(OrderDetail.this, "Không có thông tin hóa đơn để in", Toast.LENGTH_SHORT).show();
            }
        });



    }
    private void loadOrderDetail(Order order) {
        // Populate TextViews with order data
        orderId.setText(String.valueOf(order.getOrder_id()));
        tableName.setText(order.getTable_name());
        catName.setText(order.getCat_name());
        customerName.setText(order.getCustomer_name());
        customerPoint.setText(String.valueOf(order.getCustomer_point()));
        txtUsername.setText("Nhân viên: "+order.getUsername());
        txtTotal.setText("Tổng: " + order.getTotal_price() + " VND");
        orderTime.setText(order.getOrder_time().toString());


        if ("yes".equals(order.getOrder_status())) {
            btnPaymentByCash.setVisibility(View.GONE);
            btnPaymentByMomo.setVisibility(View.GONE);
        }

    }
    private void loadDrinkDetail(Order order){

        Call<List<Order>> call = apiService.getOrderDetailById(order.getOrder_id());
        call.enqueue(new Callback<List<Order>>() {
            @Override
            public void onResponse(Call<List<Order>> call, Response<List<Order>> response) {
                if (response.isSuccessful() && response.body() != null) {
                     orderDetails = response.body();

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

        int points = calculatePoints(totalPrice);
        updateOrderStatus(orderdetail.getOrder_id(), order_status,points);

        Table table = new Table();
        table.setTable_status("no");
        apiService.updateTableStatus(orderdetail.getTable_id(),table).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Toast.makeText(OrderDetail.this, "Cập nhật trạng thái bàn thành công", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(OrderDetail.this, "Cập nhật trạng thái bàn thất bại", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void PaymentByMomo() {
        Order orderdetail = (Order) getIntent().getSerializableExtra("order");
        if (orderdetail == null) {
            Toast.makeText(this, "Không có chi tiết đơn hàng ", Toast.LENGTH_SHORT).show();
            return;
        }
        BigDecimal totalPrice = orderdetail.getTotal_price();

        Payment payment = new Payment();
        payment.setOrder_id(orderdetail.getOrder_id());
        payment.setTotal_price(totalPrice);


        apiService.payment(payment).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {

                    JsonObject responseBody = response.body();
                    String payUrl = responseBody.get("payUrl").getAsString();


                    webViewPayment.setVisibility(View.VISIBLE);
                    webViewPayment.loadUrl(payUrl);

                    Order order_status = new Order();
                    order_status.setOrder_status("yes");
                    int points = calculatePoints(totalPrice);
                    updateOrderStatus(orderdetail.getOrder_id(), order_status, points);

                    Table table = new Table();
                    table.setTable_status("no");
                    apiService.updateTableStatus(orderdetail.getTable_id(),table).enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            Toast.makeText(OrderDetail.this, "Cập nhật trạng thái bàn thành công", Toast.LENGTH_SHORT).show();
                        }
                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            Toast.makeText(OrderDetail.this, "Cập nhật trạng thái bàn thất bại", Toast.LENGTH_SHORT).show();
                        }
                    });
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
    private void updateOrderStatus(int orderId, Order orderStatus, int points) {
        Order orderdetail = (Order) getIntent().getSerializableExtra("order");
        apiService.updateOrderStatus(orderId, orderStatus).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(OrderDetail.this, "Cập nhật trạng thái đơn hàng thành công", Toast.LENGTH_SHORT).show();

                    // Cập nhật điểm cho khách hàng
                    apiService.updateCustomerPoints(orderdetail.getCustomer_id(), points).enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            if (response.isSuccessful()) {
                                Toast.makeText(OrderDetail.this, "Cập nhật điểm khách hàng thành công! Điểm: " + points, Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(OrderDetail.this, "Cập nhật điểm khách hàng thất bại: " + response.code(), Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            Toast.makeText(OrderDetail.this, "Lỗi kết nối khi cập nhật điểm khách hàng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
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
    private int calculatePoints(BigDecimal totalPrice) {
        BigDecimal points = totalPrice.divide(new BigDecimal("1000"), BigDecimal.ROUND_DOWN);
        return points.intValue();
    }
    
    private void generatePdf(Order order) {
        // Khởi tạo PdfDocument
        PdfDocument pdfDocument = new PdfDocument();
        Paint paint = new Paint();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(600, 850, 1).create();
        PdfDocument.Page page = pdfDocument.startPage(pageInfo);

        Canvas canvas = page.getCanvas();
        //ội dung
        int x = 10, y = 50;
        paint.setTextSize(14);

        paint.setTextAlign(Paint.Align.LEFT);
        canvas.drawText("CÀ PHÊ MÈO NEKOCHAN", x, y, paint);

        paint.setTextAlign(Paint.Align.RIGHT);
        canvas.drawText("Nhân viên: " + order.getUsername(), pageInfo.getPageWidth() - x, y, paint);


        paint.setTextAlign(Paint.Align.LEFT);

        y += 30;
        paint.setTextSize(18);
        canvas.drawText("HÓA ĐƠN THANH TOÁN", 595 / 2 - 100, y, paint);

        y += 30;
        paint.setTextSize(14);
        canvas.drawText("Mã hóa đơn: " + order.getOrder_id(), x, y, paint);
        y += 20;
        canvas.drawText("Tên bàn: " + order.getTable_name(), x, y, paint);
        y += 20;
        canvas.drawText("Tên thú cưng: " + order.getCat_name(), x, y, paint);
        y += 20;
        canvas.drawText("Tên khách hàng: " + order.getCustomer_name(), x, y, paint);
        y += 20;
        canvas.drawText("Điểm số: " + order.getCustomer_point(), x, y, paint);
        y += 20;
        canvas.drawText("Thời gian: " + order.getOrder_time(), x, y, paint);
        y += 20;

        DecimalFormat decimalFormat = new DecimalFormat("#,##0 VND");

        y += 30;
        canvas.drawLine(x, y, x + 550, y, paint);
        y += 20;

        paint.setTextSize(14);
        canvas.drawText("Tên đồ uống", x, y, paint);
        canvas.drawText("Số lượng", x + 250, y, paint);
        canvas.drawText("Giá", x + 400, y, paint);

        y += 20;
        canvas.drawLine(x, y, x + 550, y, paint);
        y += 20;

        // Chi tiết đồ uống
        if (orderDetails != null && !orderDetails.isEmpty()) {
            paint.setTextSize(12);
            for (Order detail : orderDetails) {
                canvas.drawText(detail.getDrink_name(), x, y, paint);
                canvas.drawText(String.valueOf(detail.getAmount()), x + 250, y, paint);
                canvas.drawText(decimalFormat.format(detail.getDrink_price()), x + 400, y, paint);
                y += 20;
            }
        } else {
            canvas.drawText("Không có chi tiết đồ uống.", x, y, paint);
            y += 20;
        }

        y += 10;
        canvas.drawLine(x, y, x + 550, y, paint);
        y += 30;

        paint.setTextSize(16);
        canvas.drawText("Tổng tiền: " + decimalFormat.format(order.getTotal_price()), 595 / 2 - 100, y, paint);


        pdfDocument.finishPage(page);
        String directoryPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS) + "/HoaDonPDF";
        File directory = new File(directoryPath);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        File file = new File(directory, "HoaDon_" + order.getOrder_id() + ".pdf");

        try {
            pdfDocument.writeTo(new FileOutputStream(file));
            Toast.makeText(this, "Hóa đơn đã được lưu tại: " + file.getAbsolutePath(), Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Lỗi khi lưu hóa đơn", Toast.LENGTH_SHORT).show();
        }

        pdfDocument.close();
    }



}