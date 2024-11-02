package com.example.nekochancoffee.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import com.example.nekochancoffee.ApiService;
import com.example.nekochancoffee.Model.Table;
import com.example.nekochancoffee.R;
import com.example.nekochancoffee.network.RetrofitClient;
import com.google.android.material.textfield.TextInputEditText;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditTable extends AppCompatActivity {
    private TextInputEditText txtTableName;
    private Button btnEditTable;
    private int tableId;
    private Table table;
    private ApiService apiService = RetrofitClient.getClient("https://b319-2402-800-360e-5fad-bcbf-1b4b-9e52-88d8.ngrok-free.app/").create(ApiService.class);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_table);

        @SuppressLint({"MissingInflatedId", "LocalSuppress"})
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar_edittable);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());
        txtTableName = findViewById(R.id.txtTableName);
        btnEditTable = findViewById(R.id.btnAddTable);

        tableId = getIntent().getIntExtra("table_id", -1);
        loadTable();
        btnEditTable.setOnClickListener(v -> editTable());

    }

    private void loadTable() {
        apiService.getTableById(tableId).enqueue(new Callback<Table>() {
            @Override
            public void onResponse(Call<Table> call, Response<Table> response) {
                if (response.isSuccessful() && response.body() != null) {
                    table = response.body();
                    txtTableName.setText(table.getTable_name());
                } else {
                    Toast.makeText(EditTable.this, "Error loading table data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Table> call, Throwable t) {
                Toast.makeText(EditTable.this, "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void editTable() {
        String newName = txtTableName.getText().toString().trim();
        if (newName.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đủ thông tin!", Toast.LENGTH_SHORT).show();
            return;
        }
        table.setTable_name(newName);

        apiService.updateTable(tableId, table).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(EditTable.this, "Cập nhật bàn thành công!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(EditTable.this, "Lỗi khi cập nhật bàn", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(EditTable.this, "Lỗi mạng!", Toast.LENGTH_SHORT).show();
            }
        });
    }

}