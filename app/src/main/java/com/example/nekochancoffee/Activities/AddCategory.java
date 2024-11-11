package com.example.nekochancoffee.Activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.nekochancoffee.Adapters.CategoryAdapter;
import com.example.nekochancoffee.ApiService;
import com.example.nekochancoffee.R;
import com.example.nekochancoffee.network.RetrofitClient;
import com.google.android.material.textfield.TextInputEditText;

import java.io.File;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddCategory extends AppCompatActivity {
    private static final int PICK_IMAGE = 1;
    private TextInputEditText txtCategoryName;
    private ImageView imgCategory;
    private Button btnAddCategory;
    private Uri imageUri;
    private CategoryAdapter categoryAdapter;

    ApiService apiService = RetrofitClient.getClient("https://4dfb-58-186-47-131.ngrok-free.app/").create(ApiService.class);

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_category);

        txtCategoryName = findViewById(R.id.txt_addcategory_name);
        imgCategory = findViewById(R.id.img_addcategory_image);
        btnAddCategory = findViewById(R.id.btn_addcategory);

        imgCategory.setOnClickListener(v -> openGallery());

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar_addcategory);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Trở về Activity trước đó
            }
        });

        btnAddCategory.setOnClickListener(v -> addCategory());

    }
    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            imgCategory.setImageURI(imageUri);
        }
    }
    private void addCategory() {
        String categoryName = txtCategoryName.getText().toString().trim();

        if (categoryName.isEmpty() ||  imageUri == null ) {
            Toast.makeText(this, "Vui lòng nhập tên và hình ảnh !", Toast.LENGTH_SHORT).show();
            return;
        }

        File imageFile = new File(getRealPathFromURI(imageUri));

        RequestBody requestFile = RequestBody.create(MediaType.parse("image/png"), imageFile);
        MultipartBody.Part category_image = MultipartBody.Part.createFormData("category_image", imageFile.getName(), requestFile);

        RequestBody category_name = RequestBody.create(MediaType.parse("text/plain"), categoryName);


        Call<Void> call = apiService.addCategory(category_name,category_image);

        call.enqueue(new Callback<Void>() {

            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(AddCategory.this, "Thêm loại món thành công!", Toast.LENGTH_SHORT).show();
                    categoryAdapter.notifyDataSetChanged();
                    //finish(); // Quay về màn hình trước
                } else {
                    try {
                        // Lấy nội dung lỗi từ errorBody và chuyển thành chuỗi
                        String errorBody = response.errorBody().string();
                        Log.d("AddCategory", "Error body: " + errorBody);
                        Toast.makeText(AddCategory.this, "Thêm loại món thất bại! Lỗi: " + errorBody, Toast.LENGTH_LONG).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(AddCategory.this, "Thêm loại món thất bại! Không thể đọc thông báo lỗi", Toast.LENGTH_SHORT).show();
                    }
                }
            }


            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(AddCategory.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private String getRealPathFromURI(Uri contentUri) {
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        if (cursor != null) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        }
        return null;
    }

}