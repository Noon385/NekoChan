package com.example.nekochancoffee.Activities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.nekochancoffee.ApiService;
import com.example.nekochancoffee.Model.Drink;
import com.example.nekochancoffee.Model.Order;

import com.example.nekochancoffee.R;
import com.example.nekochancoffee.network.RetrofitClient;

import java.math.BigDecimal;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddOrderDetail extends AppCompatActivity {
    private ImageView imgDrink;
    private TextView txtDrinkName, txtAmount, txtTotal, txtOrderId;
    private Button btnAddOrder;
    private Order order;
    private Drink drink;
    private ApiService apiService = RetrofitClient.getClient("https://3a18-42-119-149-86.ngrok-free.app/").create(ApiService.class);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_order_detail);

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar_addOrder_detail);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        imgDrink = findViewById(R.id.imgDrink);
        txtDrinkName = findViewById(R.id.txtDrinkName);
        txtAmount = findViewById(R.id.txtAmount);
        txtTotal  = findViewById(R.id.txtTotal);
        btnAddOrder = findViewById(R.id.btnAddOrder);
        txtOrderId = findViewById(R.id.txtOrderId);


        order = (Order) getIntent().getSerializableExtra("order");
        drink = (Drink) getIntent().getSerializableExtra("drink");

        txtOrderId.setText("Order ID : " + order.getOrder_id());
        txtDrinkName.setText(drink.getDrink_name());


        if (drink.getDrink_image() != null && !drink.getDrink_image().isEmpty()) {
            Bitmap bitmap = decodeBase64(drink.getDrink_image());
            imgDrink.setImageBitmap(bitmap);
        } else {
            imgDrink.setImageResource(R.drawable.ic_food);
        }

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
            addOrderDetail();
            finish();
        });

    }

    private Bitmap decodeBase64(String base64Str) {
        byte[] decodedBytes = Base64.decode(base64Str, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
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

    private void addOrderDetail() {
        String amountStr = txtAmount.getText().toString();
        if (amountStr.isEmpty() || Integer.parseInt(amountStr) <= 0) {
            Toast.makeText(this, "Vui lòng nhập số lượng", Toast.LENGTH_SHORT).show();
            return;
        }

        int amount = Integer.parseInt(amountStr);
        BigDecimal total = BigDecimal.valueOf(drink.getDrink_price().doubleValue() * amount);


        Order orderDetail = new Order();
        orderDetail.setOrder_id(order.getOrder_id());
        orderDetail.setDrink_id(drink.getDrink_id());
        orderDetail.setAmount(amount);
        orderDetail.setTotal(total);



        apiService.addOrderDetail(orderDetail).enqueue(new Callback<Order>() {
            @Override
            public void onResponse(Call<Order> call, Response<Order> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(AddOrderDetail.this, "Order detail added successfully!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(AddOrderDetail.this, ""+orderDetail.getTotal(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Order> call, Throwable t) {
                Toast.makeText(AddOrderDetail.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
