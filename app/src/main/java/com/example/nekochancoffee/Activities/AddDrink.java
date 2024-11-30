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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.nekochancoffee.ApiService;
import com.example.nekochancoffee.Model.Category;
import com.example.nekochancoffee.Model.Customer;
import com.example.nekochancoffee.R;
import com.example.nekochancoffee.network.RetrofitClient;

import java.io.File;
import java.io.IOException;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddDrink extends AppCompatActivity {

    private static final int PICK_IMAGE = 1;
    private Uri imageUri;
    private EditText txtName, txtPrice;
    private RadioButton rdRemain, rdOutOfStock;
    private Button btnAddDrink;
    private ImageView imgDrink;
    private Spinner spinner;
    private ApiService apiService = RetrofitClient.getClient("https://1c38-58-186-29-70.ngrok-free.app/").create(ApiService.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_drink);

        txtName = findViewById(R.id.txtAddDrink_Name);
        txtPrice = findViewById(R.id.txtAddDrink_Price);
        rdRemain = findViewById(R.id.rdRemain);
        rdOutOfStock = findViewById(R.id.rdOutOfStock);
        btnAddDrink = findViewById(R.id.btnAddDrink);
        imgDrink = findViewById(R.id.img_adddrink_image);
        spinner = findViewById(R.id.spinnerCategory);

        imgDrink.setOnClickListener(v -> openGallery());
        btnAddDrink.setOnClickListener(v -> addDrink());

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar_adddrink);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Trở về Activity trước đó
            }
        });

        loadCategories();

    }
    private void loadCategories() {
        Call<List<Category>> call = apiService.getCategory();
        call.enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Category> categories = response.body();
                    ArrayAdapter<Category> adapter = new ArrayAdapter<>(AddDrink.this, R.layout.spinner_item, categories);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner.setAdapter(adapter);
                } else {
                    Toast.makeText(AddDrink.this, "Không tải được danh sách danh mục", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Category>> call, Throwable t) {
                Toast.makeText(AddDrink.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
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
            imgDrink.setImageURI(imageUri);
        }
    }

    private void addDrink() {
        String drinkName = txtName.getText().toString().trim();
        String drinkPrice = txtPrice.getText().toString().trim();
        String drinkStatus = rdRemain.isChecked() ? "Remain" : (rdOutOfStock.isChecked() ? "Out of Stock" : "");

        if (drinkName.isEmpty() || drinkPrice.isEmpty() || drinkStatus.isEmpty() || imageUri == null) {
            Toast.makeText(this, "Vui lòng nhập đủ thông tin và chọn ảnh!", Toast.LENGTH_SHORT).show();
            return;
        }

        Category selectedCategory = (Category) spinner.getSelectedItem();
        String categoryId = String.valueOf(selectedCategory.getCategory_id()); // Lấy category_id từ Spinner

        File imageFile = new File(getRealPathFromURI(imageUri));
        RequestBody requestFile = RequestBody.create(MediaType.parse("image/png"), imageFile);
        MultipartBody.Part drink_image = MultipartBody.Part.createFormData("drink_image", imageFile.getName(), requestFile);

        RequestBody drink_name = RequestBody.create(MediaType.parse("text/plain"), drinkName);
        RequestBody drink_price = RequestBody.create(MediaType.parse("text/plain"), drinkPrice);
        RequestBody drink_status = RequestBody.create(MediaType.parse("text/plain"), drinkStatus);
        RequestBody category_id = RequestBody.create(MediaType.parse("text/plain"), categoryId);

        Call<Void> call = apiService.addDrink(drink_name, drink_price, drink_status, category_id, drink_image);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(AddDrink.this, "Thêm đồ uống thành công", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    try {
                        String errorBody = response.errorBody().string();
                        Log.d("AddDrink", "Error body: " + errorBody);
                        Toast.makeText(AddDrink.this, "Lỗi: " + errorBody, Toast.LENGTH_LONG).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(AddDrink.this, "Không thể đọc thông báo lỗi", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(AddDrink.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
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
