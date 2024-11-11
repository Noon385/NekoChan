package com.example.nekochancoffee.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.nekochancoffee.Adapters.CustomerAdapter;
import com.example.nekochancoffee.ApiService;
import com.example.nekochancoffee.Model.Cat;
import com.example.nekochancoffee.Model.Customer;
import com.example.nekochancoffee.R;
import com.example.nekochancoffee.network.RetrofitClient;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CustomerActivity extends AppCompatActivity {
    private FloatingActionButton btnAddCustomer;
    private RecyclerView recyclerViewCustomers;
//    private ApiService apiService;

    ApiService apiService  = RetrofitClient.getClient("https://4dfb-58-186-47-131.ngrok-free.app/").create(ApiService.class);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer);
        btnAddCustomer =findViewById(R.id.btnAddCustomer);
        recyclerViewCustomers = findViewById(R.id.recyclerViewCustomers);

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar_customer);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

//        apiService  = RetrofitClient.getClient("https://72ec-58-186-28-106.ngrok-free.app/").create(ApiService.class);

        apiService.getCustomers().enqueue(new Callback<List<Customer>>() {
            @Override
            public void onResponse(Call<List<Customer>> call, Response<List<Customer>> response) {
                if (response.isSuccessful()) {
                    List<Customer> customers = response.body();
                    if (customers != null && !customers.isEmpty()) {
                        CustomerAdapter adapter = new CustomerAdapter(customers, CustomerActivity.this, apiService);
                        recyclerViewCustomers.setLayoutManager(new LinearLayoutManager(CustomerActivity.this));
                        recyclerViewCustomers.setAdapter(adapter);
                        adapter.notifyDataSetChanged();

                    } else {
                        Toast.makeText(getApplicationContext(), "Không tìm thấy khách hàng", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Log.e("Lỗi API", "Response Code: " + response.code());
                    Toast.makeText(getApplicationContext(), "Lỗi khi tải danh sách khách hàng", Toast.LENGTH_SHORT).show();
                }
            }


            @Override
            public void onFailure(Call<List<Customer>> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Lỗi kết nối!", Toast.LENGTH_SHORT).show();
            }
        });

        btnAddCustomer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CustomerActivity.this,AddCustomer.class));
            }
        });




    }
//
//    private void getCustomers() {
//        ApiService apiService = RetrofitClient.getClient("https://e5db-118-69-116-141.ngrok-free.app/").create(ApiService.class);
//
//        Call<List<Customer>> call = apiService.getCustomers();
//        call.enqueue(new Callback<List<Customer>>() {
//            @Override
//            public void onResponse(Call<List<Customer>> call, Response<List<Customer>> response) {
//                if (response.isSuccessful() && response.body() != null) {
//                    List<Customer> customers = response.body();
//                    customers.clear();
//                    customers.addAll(response.body());
//                    adapter.notifyDataSetChanged();
//                } else {
//                    Toast.makeText(CustomerActivity.this, "Không thể tải danh sách khách hàng", Toast.LENGTH_SHORT).show();
//                }
//            }
//
//            @Override
//            public void onFailure(Call<List<Customer>> call, Throwable t) {
//                Toast.makeText(CustomerActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//
//
//        });
//    }
}