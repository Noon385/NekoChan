package com.example.nekochancoffee.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nekochancoffee.Adapters.AdoptAdapter;
import com.example.nekochancoffee.ApiService;
import com.example.nekochancoffee.Model.Adopt;
import com.example.nekochancoffee.Model.Customer;
import com.example.nekochancoffee.Model.User;
import com.example.nekochancoffee.R;
import com.example.nekochancoffee.network.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CustomerDetail extends AppCompatActivity {

    private ImageView imgCustomer;
    private TextView txName, txtPhone, txtPoint;
    private RecyclerView recyclerViewAdopt, recyclerViewOrder;
    private AdoptAdapter adoptAdapter;
    ApiService apiService  = RetrofitClient.getClient("https://1988-118-69-116-208.ngrok-free.app/").create(ApiService.class);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_detail);
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar_customer_detail);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        imgCustomer = findViewById(R.id.imgCustomer);
        txName = findViewById(R.id.txName);
        txtPhone = findViewById(R.id.txtPhone);
        txtPoint = findViewById(R.id.txtPoint);
        recyclerViewAdopt = findViewById(R.id.recyclerViewAdopt);
        recyclerViewOrder = findViewById(R.id.recyclerViewOrder);

        recyclerViewAdopt.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerViewOrder.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        Customer customer = (Customer) getIntent().getSerializableExtra("customer");
        if (customer != null) {
            loadCustomerData(customer);
            loadAdopt(customer);
        }


    }
    private void loadCustomerData(Customer customer) {

        txName.setText( "Họ và tên: "+customer.getCustomer_name());
        txtPhone.setText( "Số điện thoại: "+customer.getCustomer_phone());
        txtPoint.setText( "Điểm số: "+customer.getCustomer_point());
        imgCustomer.setImageResource(R.drawable.ic_person);
    }
    private void loadAdopt(Customer customer){
        Call<List<Adopt>> call = apiService.getAdoptByCustomerId(customer.getCustomer_id());

        call.enqueue(new Callback<List<Adopt>>() {
            @Override
            public void onResponse(Call<List<Adopt>> call, Response<List<Adopt>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Adopt> adoptList = response.body();
                    adoptAdapter = new AdoptAdapter(CustomerDetail.this, adoptList,apiService);
                    recyclerViewAdopt.setAdapter(adoptAdapter);
                } else {
                    Toast.makeText(CustomerDetail.this, "Không thể tải danh sách body", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Adopt>> call, Throwable t) {
                Log.e("CustomerDetail", "Failed to fetch data", t);
            }
        });

    }

}