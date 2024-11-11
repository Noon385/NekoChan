package com.example.nekochancoffee.Activities;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.nekochancoffee.ApiService;
import com.example.nekochancoffee.Model.Cat;
import com.example.nekochancoffee.Model.Customer;
import com.example.nekochancoffee.Model.Drink;
import com.example.nekochancoffee.Model.Order;
import com.example.nekochancoffee.Model.Table;
import com.example.nekochancoffee.R;
import com.example.nekochancoffee.network.RetrofitClient;
import com.google.android.material.textfield.TextInputEditText;

import java.math.BigDecimal;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddOrder extends AppCompatActivity {
    private ImageView imgDrink;
    private Spinner spinnerTable, spinnerCat, spinnerCustomer;
    private TextView txtDrinkName, txtStaff, txtTotal;
    private TextInputEditText txtAmount;
    private Button btnAddOrder;
    private SharedPreferences sharedPreferences;
    private Drink drink;
    private Table table;
    private ApiService apiService = RetrofitClient.getClient("https://4dfb-58-186-47-131.ngrok-free.app/").create(ApiService.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_order);

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar_addOrder);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Trở về Activity trước đó
            }
        });

        // Initialize views
        imgDrink = findViewById(R.id.imgDrink);
        spinnerTable = findViewById(R.id.spinnerTable);
        spinnerCat = findViewById(R.id.spinnerCat);
        spinnerCustomer = findViewById(R.id.spinnerCustomer);
        txtDrinkName = findViewById(R.id.txtDrinkName);
        txtStaff = findViewById(R.id.txtStaff);
        txtTotal = findViewById(R.id.txtTotal);
        txtAmount = findViewById(R.id.txtAmount);
        btnAddOrder = findViewById(R.id.btnAddOrder);

        drink = (Drink) getIntent().getSerializableExtra("drink");

        if (drink != null) {
            txtDrinkName.setText(drink.getDrink_name()); // Set drink name
            if (drink.getDrink_image() != null && !drink.getDrink_image().isEmpty()) {
                Bitmap bitmap = decodeBase64(drink.getDrink_image());
                imgDrink.setImageBitmap(bitmap); // Set drink image
            } else {
                imgDrink.setImageResource(R.drawable.t); // Default image
            }
        }

        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String username = sharedPreferences.getString("username", "");
        int userId = sharedPreferences.getInt("id", -1);
        Log.d("LoginActivity", "Stored userId: " + userId);
        txtStaff.setText("Nhân viên: " + username);

        loadCats();
        loadCustomers();
        loadTables();


//        int amount = Integer.parseInt(txtAmount.getText().toString());
//        BigDecimal total = BigDecimal.valueOf(drink.getDrink_price().doubleValue() * amount);
//        txtTotal.setText(total.toString());

        // Set button click listener to add the order
        btnAddOrder.setOnClickListener(v -> {
//            String catName = ((Cat) spinnerCat.getSelectedItem()).getCatName();
//            String tableName = ((Table) spinnerTable.getSelectedItem()).getTable_name();
//            String customerName = ((Customer) spinnerCustomer.getSelectedItem()).getCustomer_name();

            int catId = ((Cat) spinnerCat.getSelectedItem()).getCatId();
            int tableId = ((Table) spinnerTable.getSelectedItem()).getTable_id();
            int customerId = ((Customer) spinnerCustomer.getSelectedItem()).getCustomer_id();


            String amountText = txtAmount.getText().toString().trim();
            if (amountText.isEmpty()) {
                Toast.makeText(AddOrder.this, "amount null", Toast.LENGTH_SHORT).show();
                return;
            }

            int amount;
            try {
                amount = Integer.parseInt(amountText); // Parse the amount
            } catch (NumberFormatException e) {
                Toast.makeText(AddOrder.this, "Số lượng không hợp lệ", Toast.LENGTH_SHORT).show();
                return;  // Return early if the input is invalid
            }

            // Calculate the total
            BigDecimal total = BigDecimal.valueOf(drink.getDrink_price().doubleValue() * amount);
            txtTotal.setText(total.toString());

            // Create Order object with all the required details
            Order order = new Order();
            order.setTable_id(tableId);
            order.setCat_id(catId);
            order.setCustomer_id(customerId);
            order.setUser_id(userId);

            Order orderdetail =new Order();
            orderdetail.setDrink_id(drink.getDrink_id());
            orderdetail.setAmount(amount);
            orderdetail.setTotal(total);



//            order.setDrink_id(drink.getDrink_id());
//            order.setAmount(amount);
//            order.setTotal(total);

//            order.setTable_name(tableName);
//            order.setCat_name(catName);
//            order.setCustomer_name(customerName);
//            order.setUsername(username);
//            order.setDrink_name(drink.getDrink_name());

            // Make API call to add order
//            apiService.addOrder(order).enqueue(new Callback<Order>() {
//                @Override
//                public void onResponse(Call<Order> call, Response<Order> response) {
//                    if (response.isSuccessful()) {
//                        int orderId =response.body().getOrder_id();
//                        orderdetail.setOrder_id(orderId);
//                        Toast.makeText(AddOrder.this, "Order added successfully!", Toast.LENGTH_SHORT).show();
//                    } else {
//                        Toast.makeText(AddOrder.this, "Failed to add order"+userId, Toast.LENGTH_SHORT).show();
//                    }
//                }
//
//                @Override
//                public void onFailure(Call<Order> call, Throwable t) {
//                    Toast.makeText(AddOrder.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
//                }
//            });
            // Make API call to add order
            apiService.addOrder(order).enqueue(new Callback<OrderResponse>() {
                @Override
                public void onResponse(Call<OrderResponse> call, Response<OrderResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        int orderId = response.body().getorder_id();

                        // Kiểm tra nếu orderId hợp lệ
                        if (orderId != 0) {
                            Toast.makeText(AddOrder.this, "Order added successfully! Order ID: " + orderId, Toast.LENGTH_SHORT).show();

                            // Tiến hành thêm chi tiết đơn hàng
                            orderdetail.setOrder_id(orderId);
                            apiService.addOrderDetail(orderdetail).enqueue(new Callback<Order>() {
                                @Override
                                public void onResponse(Call<Order> call, Response<Order> response) {
                                    if (response.isSuccessful()) {
                                        Toast.makeText(AddOrder.this, "Order detail added successfully!", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(AddOrder.this, "Failed to add order detail", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onFailure(Call<Order> call, Throwable t) {
                                    Toast.makeText(AddOrder.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            Toast.makeText(AddOrder.this, "Failed to retrieve valid order ID", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(AddOrder.this, "Failed to add order", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<OrderResponse> call, Throwable t) {
                    Toast.makeText(AddOrder.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });




//            apiService.addOrderDetail(orderdetail).enqueue(new Callback<Order>() {
//                @Override
//                public void onResponse(Call<Order> call, Response<Order> response) {
//                    if (response.isSuccessful()) {
//                        Toast.makeText(AddOrder.this, "Order added successfully!", Toast.LENGTH_SHORT).show();
//                    } else {
//                        Toast.makeText(AddOrder.this, "Failed to add order"+userId, Toast.LENGTH_SHORT).show();
//                    }
//                }
//
//                @Override
//                public void onFailure(Call<Order> call, Throwable t) {
//                    Toast.makeText(AddOrder.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
//                }
//            });
//            finish();
        });
    }
    private Bitmap decodeBase64(String base64Str) {
        byte[] decodedBytes = Base64.decode(base64Str, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }
    private void loadCats() {
//        ApiService apiService = RetrofitClient.getClient("https://72ec-58-186-28-106.ngrok-free.app/").create(ApiService.class);
        apiService.getCatsAtStore().enqueue(new Callback<List<Cat>>() {
            @Override
            public void onResponse(Call<List<Cat>> call, Response<List<Cat>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Cat> catList = response.body();
                    ArrayAdapter<Cat> catAdapter = new ArrayAdapter<>(AddOrder.this,R.layout.spinner_item, catList);
                    catAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerCat.setAdapter(catAdapter);
                } else {
                    Toast.makeText(AddOrder.this, "Không có mèo khả dụng", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Cat>> call, Throwable t) {
                Toast.makeText(AddOrder.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Load danh sách khách hàng từ API vào Spinner
    private void loadCustomers() {
//        ApiService apiService = RetrofitClient.getClient("https://72ec-58-186-28-106.ngrok-free.app/").create(ApiService.class);
        apiService.getCustomers().enqueue(new Callback<List<Customer>>() {
            @Override
            public void onResponse(Call<List<Customer>> call, Response<List<Customer>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Customer> customerList = response.body();
                    ArrayAdapter<Customer> customerAdapter = new ArrayAdapter<>(AddOrder.this, R.layout.spinner_item, customerList);
                    customerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerCustomer.setAdapter(customerAdapter);
                } else {
                    Toast.makeText(AddOrder.this, "Không tải được danh sách khách hàng", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Customer>> call, Throwable t) {
                Toast.makeText(AddOrder.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void loadTables() {
//        ApiService apiService = RetrofitClient.getClient("https://72ec-58-186-28-106.ngrok-free.app/").create(ApiService.class);
        apiService.getTable().enqueue(new Callback<List<Table>>() {
            @Override
            public void onResponse(Call<List<Table>> call, Response<List<Table>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Table> tableList = response.body();
                    ArrayAdapter<Table> tableAdapter = new ArrayAdapter<>(AddOrder.this, R.layout.spinner_item, tableList);
                    tableAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerTable.setAdapter(tableAdapter);
                } else {
                    Toast.makeText(AddOrder.this, "Không tải được danh sách bàn", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Table>> call, Throwable t) {
                Toast.makeText(AddOrder.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    public static class OrderResponse {
        private int order_id;

        public int getorder_id() {
            return order_id;
        }

        public void setorder_id(int order_id) {
            this.order_id = order_id;
        }
    }
}
