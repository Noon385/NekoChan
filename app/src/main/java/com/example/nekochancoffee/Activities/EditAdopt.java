package com.example.nekochancoffee.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.nekochancoffee.ApiService;
import com.example.nekochancoffee.Model.Adopt;
import com.example.nekochancoffee.Model.Cat;
import com.example.nekochancoffee.Model.Customer;
import com.example.nekochancoffee.R;
import com.example.nekochancoffee.network.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditAdopt extends AppCompatActivity {

    private Spinner spinnerCat, spinnerCustomer;
    private Button btnEditAdopt;
    private Cat selectedCat;
    private Customer selectedCustomer;
    private Adopt adopt;
    ApiService apiService = RetrofitClient.getClient("https://bde3-42-119-80-131.ngrok-free.app/").create(ApiService.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_adopt);

        spinnerCat = findViewById(R.id.spinnerCat);
        spinnerCustomer = findViewById(R.id.spinnerCustomer);
        btnEditAdopt = findViewById(R.id.btnEditAdopt);

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar_editadopt);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Lấy thông tin nhận nuôi từ Intent
        adopt = (Adopt) getIntent().getSerializableExtra("adopt");

        loadCats();
        loadCustomers();

        btnEditAdopt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedCat = (Cat) spinnerCat.getSelectedItem();
                selectedCustomer = (Customer) spinnerCustomer.getSelectedItem();
                editAdopt(adopt.getAdopt_id(), selectedCat.getCatId(), selectedCustomer.getCustomer_id());
            }
        });
    }

    private void loadCats() {
        apiService.getCatsAtStore().enqueue(new Callback<List<Cat>>() {
            @Override
            public void onResponse(Call<List<Cat>> call, Response<List<Cat>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Cat> catList = response.body();
                    ArrayAdapter<Cat> catAdapter = new ArrayAdapter<>(EditAdopt.this, R.layout.spinner_item, catList);
                    catAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerCat.setAdapter(catAdapter);

                    // Chọn mèo hiện tại
                    for (int i = 0; i < catList.size(); i++) {
                        if (catList.get(i).getCatId() == adopt.getCat_id()) {
                            spinnerCat.setSelection(i);
                            break;
                        }
                    }
                } else {
                    Toast.makeText(EditAdopt.this, "Không có mèo khả dụng", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<List<Cat>> call, Throwable t) {
                Toast.makeText(EditAdopt.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadCustomers() {
        apiService.getCustomers().enqueue(new Callback<List<Customer>>() {
            @Override
            public void onResponse(Call<List<Customer>> call, Response<List<Customer>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Customer> customerList = response.body();
                    ArrayAdapter<Customer> customerAdapter = new ArrayAdapter<>(EditAdopt.this, R.layout.spinner_item, customerList);
                    customerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerCustomer.setAdapter(customerAdapter);

                    // Chọn khách hàng hiện tại
                    for (int i = 0; i < customerList.size(); i++) {
                        if (customerList.get(i).getCustomer_id() == adopt.getCustomer_id()) {
                            spinnerCustomer.setSelection(i);
                            break;
                        }
                    }
                } else {
                    Toast.makeText(EditAdopt.this, "Không tải được danh sách khách hàng", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Customer>> call, Throwable t) {
                Toast.makeText(EditAdopt.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void editAdopt(int adoptId, int catId, int customerId) {
        Call<Void> call = apiService.updateAdopt(adoptId, catId, customerId);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(EditAdopt.this, "Cập nhật thông tin nhận nuôi thành công!", Toast.LENGTH_SHORT).show();
                    finish(); // Đóng Activity
                } else {
                    // Nếu có lỗi, hiển thị thông báo cho người dùng
                    String errorMessage = "Không thể cập nhật thông tin nhận nuôi!";
                    Toast.makeText(EditAdopt.this, errorMessage, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(EditAdopt.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
