package com.example.nekochancoffee.Activities;

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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.nekochancoffee.ApiService;
import com.example.nekochancoffee.Model.Cat;
import com.example.nekochancoffee.R;

import com.example.nekochancoffee.network.RetrofitClient;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditCat extends AppCompatActivity {

    private static final int PICK_IMAGE = 1;
    private TextInputEditText txtCatName, txtCatPrice;
    private RadioGroup rgStatus;
    private RadioButton rdAtStore, rdAdopted;
    private Button btnEditCat;
    private ImageView imgCatImage;
    Uri imageUri;
    ApiService apiService  = RetrofitClient.getClient("https://dbd8-1-53-113-145.ngrok-free.app/").create(ApiService.class);

    private int catId; // Mã mèo cần chỉnh sửa

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_cat); // Đảm bảo tên file đúng

        // Ánh xạ view
        txtCatName = findViewById(R.id.txtCatName);
        txtCatPrice = findViewById(R.id.txtCatPrice);
        rgStatus = findViewById(R.id.rg_editmenu_TinhTrang);
        rdAtStore = findViewById(R.id.rd_editcat_Atstore);
        rdAdopted = findViewById(R.id.rd_editcat_Adopted);
        btnEditCat = findViewById(R.id.btnEditCat);
        imgCatImage = findViewById(R.id.imgCatImage);
        imgCatImage.setOnClickListener(v -> openGallery());
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar_editcat);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Trở về Activity trước đó
            }
        });
        // Nhận catId từ Intent

        catId = getIntent().getIntExtra("catId", -1);
        Log.d("EditCat", "Received catId: " + catId);

        // Lấy thông tin mèo từ API để điền vào các trường
        getCatDetails();

        btnEditCat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateCat();
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
            imgCatImage.setImageURI(imageUri);
        }
    }

    private void getCatDetails() {
//        ApiService apiService = RetrofitClient.getClient("https://e8da-58-186-28-106.ngrok-free.app/").create(ApiService.class);
        Call<Cat> call = apiService.getCatById(catId);

        call.enqueue(new Callback<Cat>() {
            @Override
            public void onResponse(Call<Cat> call, Response<Cat> response) {
                Log.d("EditCat", "Response code: " + response.code()); // Ghi nhận mã phản hồi
                Log.d("EditCat", "Response body: " + response.body()); // Ghi nhận nội dung phản hồi
                if (response.isSuccessful() && response.body() != null) {
                    Cat cat = response.body();
                    Log.d("EditCat", "Cat details: " + cat.getCatName());  // Log thông tin mèo
                    txtCatName.setText(cat.getCatName());
                    txtCatPrice.setText(String.valueOf(cat.getCatPrice()));

                    if ("At Store".equals(cat.getCatStatus())) {
                        rdAtStore.setChecked(true);
                    } else {
                        rdAdopted.setChecked(true);
                    }

                    if (cat.getCatImage() != null && !cat.getCatImage().isEmpty()) {
                        Bitmap bitmap = decodeBase64(cat.getCatImage());
                        imgCatImage.setImageBitmap(bitmap);
                    } else {
                        imgCatImage.setImageResource(R.drawable.t);  // Hình mặc định
                    }
                } else {

                    Log.d("EditCat", "Response error: " + response.code());
                    Toast.makeText(EditCat.this, "Không thể tải thông tin mèo", Toast.LENGTH_SHORT).show();
                }
            }


            @Override
            public void onFailure(Call<Cat> call, Throwable t) {
                Log.e("EditCat", "API error: " + t.getMessage());
                Toast.makeText(EditCat.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Hàm cập nhật thông tin mèo
    private void updateCat() {
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
//        ApiService apiService = RetrofitClient.getClient("https://e8da-58-186-28-106.ngrok-free.app/").create(ApiService.class);
        Call<Void> call = apiService.updateCat(catId, cat_name, cat_status, cat_price, cat_image);

        call.enqueue(new Callback<Void>() {

            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(EditCat.this, "Sửa mèo thành công!", Toast.LENGTH_SHORT).show();

                    finish(); // Quay về màn hình trước
                } else {
                    try {
                        // Lấy nội dung lỗi từ errorBody và chuyển thành chuỗi
                        String errorBody = response.errorBody().string();
                        Log.d("EditCat", "Error body: " + errorBody);
                        Toast.makeText(EditCat.this, "Sửa mèo thất bại! Lỗi: " + errorBody, Toast.LENGTH_LONG).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(EditCat.this, "Sửa mèo thất bại! Không thể đọc thông báo lỗi", Toast.LENGTH_SHORT).show();
                    }
                }
            }


            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(EditCat.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
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
    private Bitmap decodeBase64(String base64Str) {
        byte[] decodedBytes = Base64.decode(base64Str, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }

}
