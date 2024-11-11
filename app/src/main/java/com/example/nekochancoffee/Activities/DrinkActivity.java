package com.example.nekochancoffee.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nekochancoffee.Adapters.DrinkAdapter;
import com.example.nekochancoffee.ApiService;

import com.example.nekochancoffee.Model.Drink;
import com.example.nekochancoffee.R;
import com.example.nekochancoffee.network.RetrofitClient;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DrinkActivity extends AppCompatActivity {

    private RecyclerView  recyclerView;
    private FloatingActionButton btnAddDrink;
    private ApiService apiService = RetrofitClient.getClient("https://4dfb-58-186-47-131.ngrok-free.app/").create(ApiService.class);

    private List<Drink>drinkList =new ArrayList<>();
    private DrinkAdapter adapter ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drink);

        recyclerView = findViewById(R.id.recyclerViewDrink);
        btnAddDrink = findViewById(R.id.btnAddDrink);

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar_drink);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btnAddDrink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DrinkActivity.this, AddDrink.class));
            }
        });
        adapter = new DrinkAdapter(this, drinkList, new DrinkAdapter.OnDrinkActionListener() {
            @Override
            public void onDeleteDrink(Drink drink) {
                apiService.deleteDrink(drink.getDrink_id()).enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(DrinkActivity.this, "Xóa món thành công", Toast.LENGTH_SHORT).show();
                            loadDrinkList();  // Reload the drink list after deletion
                        } else {
                            Toast.makeText(DrinkActivity.this, "Không thể xóa món", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Toast.makeText(DrinkActivity.this, "Lỗi kết nối!", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onEditDrink(Drink drink) {
                Intent intent = new Intent(DrinkActivity.this, EditDrink.class);
                intent.putExtra("drink_id", drink.getDrink_id());
                Toast.makeText(DrinkActivity.this, "Drink ID: " + drink.getDrink_id(), Toast.LENGTH_LONG).show();
                startActivity(intent);
            }
        });

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        int categoryId = getIntent().getIntExtra("category_id",-1);
        if (categoryId != -1) {
            loadDrinkListByCategoryId(categoryId);
            Toast.makeText(DrinkActivity.this, "Danh sách món theo id "+categoryId, Toast.LENGTH_SHORT).show();
        } else {
            loadDrinkList();
            Toast.makeText(DrinkActivity.this, "Danh sách món", Toast.LENGTH_SHORT).show();
        }

    }

    private void loadDrinkList() {
        apiService.getDrink().enqueue(new Callback<List<Drink>>() {
            @Override
            public void onResponse(Call<List<Drink>> call, Response<List<Drink>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    drinkList.clear();
                    drinkList.addAll(response.body());
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(DrinkActivity.this, "Không thể tải danh sách món", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Drink>> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Lỗi kết nối!", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void loadDrinkListByCategoryId(int categoryId) {
        apiService.getDrinksByCategoryId(categoryId).enqueue(new Callback<List<Drink>>() {
            @Override
            public void onResponse(Call<List<Drink>> call, Response<List<Drink>> response) {
                if (response.isSuccessful() && response.body() != null) {

                    drinkList.clear();
                    drinkList.addAll(response.body());
                    adapter.notifyDataSetChanged();
                    Toast.makeText(DrinkActivity.this, "Drink ID: " + response.body().get(0).getDrink_id(), Toast.LENGTH_LONG).show();

                } else {
                    Toast.makeText(DrinkActivity.this, "Không thể tải danh sách món", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Drink>> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Lỗi kết nối!", Toast.LENGTH_SHORT).show();
            }
        });
    }

}