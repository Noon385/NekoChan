package com.example.nekochancoffee.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.nekochancoffee.ApiService;
import com.example.nekochancoffee.Model.Cat;
import com.example.nekochancoffee.Model.Customer;
import com.example.nekochancoffee.R;
import com.example.nekochancoffee.network.RetrofitClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddAdopt extends AppCompatActivity {
    private Spinner spinnerCat, spinnerCustomer;
    private Button btnAddAdopt;
    private Cat selectedCat;
    private Customer selectedCustomer;
    ApiService apiService = RetrofitClient.getClient("https://1c38-58-186-29-70.ngrok-free.app/").create(ApiService.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_adopt);

        spinnerCat = findViewById(R.id.spinnerCat);
        spinnerCustomer = findViewById(R.id.spinnerCustomer);
        btnAddAdopt = findViewById(R.id.btnAddAdopt);

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar_addadopt);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Trở về Activity trước đó
            }
        });

        loadCats();
        loadCustomers();

        // Xử lý khi bấm nút Thêm nhận nuôi
        btnAddAdopt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 selectedCat = (Cat) spinnerCat.getSelectedItem();
                 selectedCustomer = (Customer) spinnerCustomer.getSelectedItem();
                addAdopt(selectedCat.getCatId(), selectedCustomer.getCustomer_id());
            }
        });
    }

    // Load danh sách mèo từ API vào Spinner
    private void loadCats() {
//        ApiService apiService = RetrofitClient.getClient("https://72ec-58-186-28-106.ngrok-free.app/").create(ApiService.class);
        apiService.getCatsAtStore().enqueue(new Callback<List<Cat>>() {
            @Override
            public void onResponse(Call<List<Cat>> call, Response<List<Cat>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Cat> catList = response.body();
                    ArrayAdapter<Cat> catAdapter = new ArrayAdapter<>(AddAdopt.this,R.layout.spinner_item, catList);
                    catAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerCat.setAdapter(catAdapter);
                } else {
                    Toast.makeText(AddAdopt.this, "Không có mèo khả dụng", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Cat>> call, Throwable t) {
                Toast.makeText(AddAdopt.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
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
                    ArrayAdapter<Customer> customerAdapter = new ArrayAdapter<>(AddAdopt.this, R.layout.spinner_item, customerList);
                    customerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerCustomer.setAdapter(customerAdapter);
                } else {
                    Toast.makeText(AddAdopt.this, "Không tải được danh sách khách hàng", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Customer>> call, Throwable t) {
                Toast.makeText(AddAdopt.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    // Gọi API để thêm thông tin nhận nuôi
    private void addAdopt(int catId, int customerId) {
//        ApiService apiService = RetrofitClient.getClient("https://72ec-58-186-28-106.ngrok-free.app/").create(ApiService.class);
        Call<Void> call = apiService.addAdopt(catId, customerId);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(AddAdopt.this, "Thêm nhận nuôi thành công!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    String errorMessage = "Không thể thêm nhận nuôi cho mèo: " + selectedCat.getCatName() +
                            ", khách hàng: " + selectedCustomer.getCustomer_name();
                    Toast.makeText(AddAdopt.this, errorMessage, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(AddAdopt.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}