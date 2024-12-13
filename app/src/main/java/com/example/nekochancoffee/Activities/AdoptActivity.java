package com.example.nekochancoffee.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.nekochancoffee.Adapters.AdoptAdapter;
import com.example.nekochancoffee.ApiService;
import com.example.nekochancoffee.Model.Adopt;
import com.example.nekochancoffee.R;
import com.example.nekochancoffee.network.RetrofitClient;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdoptActivity extends AppCompatActivity {

    private RecyclerView recyclerViewAdopt;
    private AdoptAdapter adoptAdapter;
    private FloatingActionButton btnAddAdopt;
    ApiService apiService  = RetrofitClient.getClient("https://bde3-42-119-80-131.ngrok-free.app/").create(ApiService.class);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adopt);

        recyclerViewAdopt = findViewById(R.id.recyclerViewAdopt);
        recyclerViewAdopt.setLayoutManager(new LinearLayoutManager(this));
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar_adopt);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdoptActivity.this, Option.class));
                finish();
            }
        });
        recyclerViewAdopt =findViewById(R.id.recyclerViewAdopt);
        btnAddAdopt = findViewById(R.id.btnAddAdopt);

        // Load adopted cats
        loadAdoptData();
        btnAddAdopt.setOnClickListener(v -> {
            // Điều hướng đến Activity thêm người dùng (nếu có)
            startActivity(new Intent(AdoptActivity.this,AddAdopt.class));
        });
    }

    private void loadAdoptData() {
//        ApiService apiService  = RetrofitClient.getClient("https://72ec-58-186-28-106.ngrok-free.app/ ").create(ApiService.class);
        Call<List<Adopt>> call = apiService.getAdopts();

        call.enqueue(new Callback<List<Adopt>>() {
            @Override
            public void onResponse(Call<List<Adopt>> call, Response<List<Adopt>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Adopt> adoptList = response.body();
                    adoptAdapter = new AdoptAdapter(AdoptActivity.this, adoptList,apiService);
                    recyclerViewAdopt.setAdapter(adoptAdapter);
                } else {
                    Toast.makeText(AdoptActivity.this, "Không thể tải danh sách body", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Adopt>> call, Throwable t) {
                Log.e("AdoptActivity", "Failed to fetch data", t);
            }
        });
    }
}