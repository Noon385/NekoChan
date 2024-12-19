package com.example.nekochancoffee.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
    private final static int req =123;
    private ApiService apiService = RetrofitClient.getClient("https://3a18-42-119-149-86.ngrok-free.app/").create(ApiService.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_order);

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar_addOrder);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


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
            txtDrinkName.setText(drink.getDrink_name());
            if (drink.getDrink_image() != null && !drink.getDrink_image().isEmpty()) {
                Bitmap bitmap = decodeBase64(drink.getDrink_image());
                imgDrink.setImageBitmap(bitmap);
            } else {
                imgDrink.setImageResource(R.drawable.t);
            }
        }
        imgDrink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddOrder.this,CategoryActivity.class);
                intent.putExtra("req",req);
                startActivityForResult(intent,req);

            }
        });

        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String username = sharedPreferences.getString("username", "");
        int userId = sharedPreferences.getInt("id", -1);
        Log.d("LoginActivity", "Stored userId: " + userId);
        txtStaff.setText("Nhân viên: " + username);

        loadCats();
        loadCustomers();
        loadTables();

        txtAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateTotal();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });


        btnAddOrder.setOnClickListener(v -> {
            String amountText = txtAmount.getText().toString().trim();
            if (amountText.isEmpty()) {
                Toast.makeText(AddOrder.this, "amount null", Toast.LENGTH_SHORT).show();
                return;
            }
            int amount;
            try {
                amount = Integer.parseInt(amountText);
            } catch (NumberFormatException e) {
                Toast.makeText(AddOrder.this, "Số lượng không hợp lệ", Toast.LENGTH_SHORT).show();
                return;
            }

            BigDecimal total = BigDecimal.valueOf(drink.getDrink_price().doubleValue() * amount);
            txtTotal.setText(total.toString());

            addOrder(amount, total, userId);
            finish();
        });

    }
    private void updateTotal() {
        String amountText = txtAmount.getText().toString().trim();
        if (amountText.isEmpty()) {
            txtTotal.setText("0");
            return;
        }

        try {
            int amount = Integer.parseInt(amountText);
            BigDecimal price = drink != null ? drink.getDrink_price() : BigDecimal.ZERO;
            BigDecimal total = price.multiply(BigDecimal.valueOf(amount));
            txtTotal.setText(total.toString() +" VND");
        } catch (NumberFormatException e) {
            txtTotal.setText("0");
        }
    }
    private void addOrder(int amount, BigDecimal total, int userId) {
        int catId = ((Cat) spinnerCat.getSelectedItem()).getCatId();
        int tableId = ((Table) spinnerTable.getSelectedItem()).getTable_id();
        int customerId = ((Customer) spinnerCustomer.getSelectedItem()).getCustomer_id();

        Order order = new Order();
        order.setTable_id(tableId);
        order.setCat_id(catId);
        order.setCustomer_id(customerId);
        order.setUser_id(userId);

        Order orderDetail = new Order();
        orderDetail.setDrink_id(drink.getDrink_id());
        orderDetail.setAmount(amount);
        orderDetail.setTotal(total);

        apiService.addOrder(order).enqueue(new Callback<OrderResponse>() {
            @Override
            public void onResponse(Call<OrderResponse> call, Response<OrderResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    int orderId = response.body().getorder_id();
                    if (orderId != 0) {
                        Toast.makeText(AddOrder.this, "Order added successfully! Order ID: " + orderId, Toast.LENGTH_SHORT).show();

                        Table table = new Table();
                        table.setTable_status("yes");
                        apiService.updateTableStatus(order.getTable_id(), table).enqueue(new Callback<Void>() {
                            @Override
                            public void onResponse(Call<Void> call, Response<Void> response) {
                                Toast.makeText(AddOrder.this, "Cập nhật trạng thái bàn thành công", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onFailure(Call<Void> call, Throwable t) {
                                Toast.makeText(AddOrder.this, "Cập nhật trạng thái bàn thất bại", Toast.LENGTH_SHORT).show();
                            }
                        });

                        orderDetail.setOrder_id(orderId);
                        apiService.addOrderDetail(orderDetail).enqueue(new Callback<Order>() {
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
        apiService.getEmptyTable().enqueue(new Callback<List<Table>>() {
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
