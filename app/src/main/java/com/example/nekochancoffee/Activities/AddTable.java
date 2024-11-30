package com.example.nekochancoffee.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
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

public class AddTable extends AppCompatActivity {

    private TextInputEditText txtTableName;
    private Button btnAddTable;
    private ApiService apiService = RetrofitClient.getClient("https://1c38-58-186-29-70.ngrok-free.app/").create(ApiService.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_table);
        txtTableName = findViewById(R.id.txtTableName);
        btnAddTable = findViewById(R.id.btnAddTable);

        @SuppressLint({"MissingInflatedId", "LocalSuppress"})
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar_addtable);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish()); // Go back to the previous Activity

        btnAddTable.setOnClickListener(v -> addTable());
    }

    private void addTable() {
        String name = txtTableName.getText().toString().trim();

        if (name.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đủ thông tin!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create a Table object with the required data
        Table newTable = new Table();
        newTable.setTable_name(name);
        newTable.setTable_status("no"); // Default status

        Call<Void> call = apiService.addTable(newTable);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(AddTable.this, "Bàn đã được thêm thành công!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(AddTable.this, "Lỗi khi thêm bàn, vui lòng thử lại.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(AddTable.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
