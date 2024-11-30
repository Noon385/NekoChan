package com.example.nekochancoffee.Activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.nekochancoffee.ApiService;
import com.example.nekochancoffee.Model.Cat;
import com.example.nekochancoffee.Model.Category;
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

public class EditCategory extends AppCompatActivity {
    private TextInputEditText txtCategoryName;
    private static final int PICK_IMAGE = 1;
    private ImageView imgCategory;
    private Button btnAddCategory;
    private Uri imageUri;
    private int categoryId;
    ApiService apiService  = RetrofitClient.getClient("https://1c38-58-186-29-70.ngrok-free.app/").create(ApiService.class);
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editcategory);
        txtCategoryName = findViewById(R.id.txt_editcategory_name);
        imgCategory = findViewById(R.id.img_editcategory_image);
        btnAddCategory = findViewById(R.id.btn_editcategory);

        categoryId = getIntent().getIntExtra("categoryId", -1);
        Log.d("EditCategory", "Received categoryId: " + categoryId);

        getCategoryDetails();
        imgCategory.setOnClickListener(v -> openGallery());

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar_editcategory);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Trở về Activity trước đó
            }
        });

        btnAddCategory.setOnClickListener(v -> editCategory());
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
    private Bitmap decodeBase64(String base64Str) {
        byte[] decodedBytes = Base64.decode(base64Str, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }
    private void getCategoryDetails() {
        Call<Category> call = apiService.getCategoryById(categoryId);
        call.enqueue(new Callback<Category>() {
            @Override
            public void onResponse(Call<Category> call, Response<Category> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Category category = response.body();
                    Log.d("EditCategory", "Category details: " + category.getCategory_name());
                    txtCategoryName.setText(category.getCategory_name());
                    if (category.getCategory_image() != null && !category.getCategory_image().isEmpty()) {
                        Bitmap bitmap = decodeBase64(category.getCategory_image());
                        imgCategory.setImageBitmap(bitmap);
                    } else {
                        imgCategory.setImageResource(R.drawable.t);  // Hình mặc định
                    }
                } else {

                    Log.d("EditCat", "Response error: " + response.code());
                    Toast.makeText(EditCategory.this, "Không thể tải thông tin loại món", Toast.LENGTH_SHORT).show();
                }
            }


            @Override
            public void onFailure(Call<Category> call, Throwable t) {
                Log.e("EditCategory", "API error: " + t.getMessage());
                Toast.makeText(EditCategory.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void editCategory() {
        String categoryName = txtCategoryName.getText().toString().trim();


        if (categoryName.isEmpty()|| imageUri == null ) {
            Toast.makeText(this, "Vui lòng nhập tên , ảnh!", Toast.LENGTH_SHORT).show();
            return;
        }

        File imageFile = new File(getRealPathFromURI(imageUri));

        RequestBody requestFile = RequestBody.create(MediaType.parse("image/png"), imageFile);
        MultipartBody.Part category_image = MultipartBody.Part.createFormData("category_image", imageFile.getName(), requestFile);
        RequestBody category_name = RequestBody.create(MediaType.parse("text/plain"), categoryName);

        Call<Void> call = apiService.updateCategory(categoryId, category_name, category_image);
        call.enqueue(new Callback<Void>() {

            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(EditCategory.this, "Sửa loại món thành công!", Toast.LENGTH_SHORT).show();

                    finish(); // Quay về màn hình trước
                } else {
                    try {
                        // Lấy nội dung lỗi từ errorBody và chuyển thành chuỗi
                        String errorBody = response.errorBody().string();
                        Log.d("EditCat", "Error body: " + errorBody);
                        Toast.makeText(EditCategory.this, "Sửa mèo thất bại! Lỗi: " + errorBody, Toast.LENGTH_LONG).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(EditCategory.this, "Sửa mèo thất bại! Không thể đọc thông báo lỗi", Toast.LENGTH_SHORT).show();
                    }
                }
            }


            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(EditCategory.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}