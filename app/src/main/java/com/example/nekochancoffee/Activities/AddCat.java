package com.example.nekochancoffee.Activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.nekochancoffee.ApiService;
import com.example.nekochancoffee.R;
import com.example.nekochancoffee.Adapters.CatAdapter;
import com.example.nekochancoffee.network.RetrofitClient;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddCat extends AppCompatActivity {

    private static final int PICK_IMAGE = 1;
    private CatAdapter catAdapter;
    EditText txtCatName , txtCatPrice;
    RadioButton rdAtStore, rdAdopted;
    Button btnAddCat;
    ImageView imgCat;
    Uri imageUri;
    ApiService apiService = RetrofitClient.getClient("https://e4aa-115-75-32-98.ngrok-free.app/").create(ApiService.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cat_add_layout);

        txtCatName = findViewById(R.id.txtCatName);
        txtCatPrice = findViewById(R.id.txtCatPrice);
        rdAtStore = findViewById(R.id.rd_addcat_Atstore);
        rdAdopted = findViewById(R.id.rd_addcat_Adopted);
        btnAddCat = findViewById(R.id.btnAddCat);
        imgCat = findViewById(R.id.imgCat);

        imgCat.setOnClickListener(v -> openGallery());

        btnAddCat.setOnClickListener(v -> addCat());

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar_addcat);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Trở về Activity trước đó
            }
        });
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
            imgCat.setImageURI(imageUri);
        }
    }

    private void addCat() {
        String catName = txtCatName.getText().toString().trim();
        String catPrice = txtCatPrice.getText().toString().trim();
        String catStatus = rdAtStore.isChecked() ? "At Store" : (rdAdopted.isChecked() ? "Adopted" : "");

        if (catName.isEmpty() || catStatus.isEmpty() || imageUri == null || catPrice.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập tên mèo, chọn tình trạng và ảnh!", Toast.LENGTH_SHORT).show();
            return;
        }

        File imageFile = new File(getRealPathFromURI(imageUri));

        RequestBody requestFile = RequestBody.create(MediaType.parse("image/png"), imageFile);
        MultipartBody.Part cat_image = MultipartBody.Part.createFormData("cat_image", imageFile.getName(), requestFile);

        // Tạo RequestBody cho các trường khác
        RequestBody cat_name = RequestBody.create(MediaType.parse("text/plain"), catName);
        RequestBody cat_status = RequestBody.create(MediaType.parse("text/plain"), catStatus);
        RequestBody cat_price = RequestBody.create(MediaType.parse("text/plain"), catPrice);

        // Gọi API

        Call<Void> call = apiService.addCat(cat_name, cat_status, cat_price, cat_image);

        call.enqueue(new Callback<Void>() {

            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(AddCat.this, "Thêm mèo thành công!", Toast.LENGTH_SHORT).show();
                    catAdapter.notifyDataSetChanged();
                    //finish(); // Quay về màn hình trước
                } else {
                    try {
                        // Lấy nội dung lỗi từ errorBody và chuyển thành chuỗi
                        String errorBody = response.errorBody().string();
                        Log.d("AddCat", "Error body: " + errorBody);
                        Toast.makeText(AddCat.this, "Thêm mèo thất bại! Lỗi: " + errorBody, Toast.LENGTH_LONG).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(AddCat.this, "Thêm mèo thất bại! Không thể đọc thông báo lỗi", Toast.LENGTH_SHORT).show();
                    }
                }
            }


            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(AddCat.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
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
