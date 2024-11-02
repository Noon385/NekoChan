package com.example.nekochancoffee.Activities;

import static java.security.AccessController.getContext;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nekochancoffee.Adapters.CategoryAdapter;
import com.example.nekochancoffee.ApiService;
import com.example.nekochancoffee.Model.Category;
import com.example.nekochancoffee.R;
import com.example.nekochancoffee.network.RetrofitClient;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CategoryActivity extends AppCompatActivity {

    private FloatingActionButton btnAddCategory;
    private RecyclerView recyclerViewCategory;
    private CategoryAdapter adapter;
    private List<Category> categoryList = new ArrayList<>();
    private ApiService apiService = RetrofitClient.getClient("https://b319-2402-800-360e-5fad-bcbf-1b4b-9e52-88d8.ngrok-free.app/").create(ApiService.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar_category);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        recyclerViewCategory = findViewById(R.id.recyclerViewCategory);
        btnAddCategory = findViewById(R.id.btnAddCategory);

        adapter = new CategoryAdapter(this, categoryList, new CategoryAdapter.OnCategoryActionListener() {
            @Override
            public void onDeleteCategory(Category category) {
                deleteCategory(category.getCategory_id());
            }

            @Override
            public void onEditCategory(Category category) {
                Intent intent = new Intent(CategoryActivity.this, EditCategory.class);
                intent.putExtra("categoryId", category.getCategory_id());
                Toast.makeText(CategoryActivity.this, "Category ID: " + category.getCategory_id(), Toast.LENGTH_LONG).show();
                startActivity(intent);



            }
        });


        recyclerViewCategory.setAdapter(adapter);
        recyclerViewCategory.setLayoutManager(new GridLayoutManager(this,3));

        // Tải danh sách ban đầu
        loadCategoryList();

        // Xử lý khi nhấn nút thêm loại món
        btnAddCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(CategoryActivity.this,AddCategory.class));
            }
        });
    }

    private void loadCategoryList() {
        apiService.getCategory().enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    categoryList.clear();
                    categoryList.addAll(response.body());
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(CategoryActivity.this, "Không thể tải danh sách loại món", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Category>> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Lỗi kết nối!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteCategory(int categoryId) {
        Call<Void> call = apiService.deleteCategory(categoryId);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(CategoryActivity.this, "Loại món đã bị xóa", Toast.LENGTH_SHORT).show();
                    // Tải lại danh sách loại món sau khi xóa thành công
                    loadCategoryList();
                } else {
                    Toast.makeText(CategoryActivity.this, "Xóa loại món thất bại", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(CategoryActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
