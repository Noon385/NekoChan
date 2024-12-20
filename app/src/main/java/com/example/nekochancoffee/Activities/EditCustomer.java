package com.example.nekochancoffee.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.nekochancoffee.Adapters.CustomerAdapter;
import com.example.nekochancoffee.ApiService;
import com.example.nekochancoffee.Model.Customer;
import com.example.nekochancoffee.Model.User;
import com.example.nekochancoffee.R;
import com.example.nekochancoffee.network.RetrofitClient;
import com.google.android.material.textfield.TextInputEditText;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditCustomer extends AppCompatActivity {
    private TextInputEditText txtName, txtPhone,txtPoint;
    private Button btnEditCustomer;

    private Customer customer;
    private CustomerAdapter adapter;
    ApiService apiService  = RetrofitClient.getClient("https://56fc-118-69-96-226.ngrok-free.app/").create(ApiService.class);

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_customer);

        txtName = findViewById(R.id.txtName);
        txtPhone = findViewById(R.id.txtPhone);
        txtPoint  = findViewById(R.id.txtPoint);
        btnEditCustomer = findViewById(R.id.btnEditCustomer);

//        ApiService apiService = RetrofitClient.getClient("https://e8da-58-186-28-106.ngrok-free.app/").create(ApiService.class);
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar_editcustomer);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Trở về Activity trước đó
            }
        });

        customer = (Customer) getIntent().getSerializableExtra("customer");

        if (customer != null) {
            txtName.setText(customer.getCustomer_name());
            txtPhone.setText(customer.getCustomer_phone());
            txtPoint.setText(customer.getCustomer_point());

        }


        btnEditCustomer.setOnClickListener(v -> {
            updateCustomer();
            //adapter.notifyDataSetChanged();

        });
    }
    private void updateCustomer() {

        String name = txtName.getText().toString().trim();
        String phone = txtPhone.getText().toString().trim();
        String point = txtPoint.getText().toString().trim();


        if (name.isEmpty() || phone.isEmpty() || point.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        customer.setCustomer_name(name);
        customer.setCustomer_phone(phone);
        customer.setCustomer_point(point);
//        ApiService apiService = RetrofitClient.getClient("https://e8da-58-186-28-106.ngrok-free.app/").create(ApiService.class);


        apiService.updateCustomer(customer.getCustomer_id(), customer).enqueue(new Callback<Customer>() {
            @Override
            public void onResponse(Call<Customer> call, Response<Customer> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(EditCustomer.this, "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
                    adapter.notifyDataSetChanged();
                    finish();
                } else {
                    Toast.makeText(EditCustomer.this, "Cập nhật thất bại: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Customer> call, Throwable t) {
                Toast.makeText(EditCustomer.this, "Lỗi khi cập nhật: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}