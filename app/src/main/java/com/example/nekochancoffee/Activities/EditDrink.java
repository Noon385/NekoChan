package com.example.nekochancoffee.Activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.nekochancoffee.ApiService;
import com.example.nekochancoffee.Model.Category;
import com.example.nekochancoffee.Model.Drink;
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

public class EditDrink extends AppCompatActivity {
    private static final int PICK_IMAGE = 1;
    private Uri imageUri;
    private EditText txtName, txtPrice;
    private RadioButton rdRemain, rdOutOfStock;
    private Button btnAddDrink;
    private ImageView imgDrink;
    private Spinner spinner;
    private Drink drink;
    private int drinkId;
    private ApiService apiService = RetrofitClient.getClient("https://9729-118-68-211-167.ngrok-free.app/").create(ApiService.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_drink);

        txtName = findViewById(R.id.txtEditDrink_Name);
        txtPrice = findViewById(R.id.txtEditDrink_Price);
        rdRemain = findViewById(R.id.rdRemain);
        rdOutOfStock = findViewById(R.id.rdOutOfStock);
        btnAddDrink = findViewById(R.id.btnEditDrink);
        imgDrink = findViewById(R.id.img_editdrink_image);
        spinner = findViewById(R.id.spinnerCategory);

        imgDrink.setOnClickListener(v -> openGallery());
        btnAddDrink.setOnClickListener(v -> editDrink());

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar_editdrink);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Trở về Activity trước đó
            }
        });

        drinkId = getIntent().getIntExtra("drinkId", -1);
        getDrinkDetails();

        loadCategories();
    }

    private void getDrinkDetails() {
        Call<Drink> call = apiService.getDrinkById(drinkId);
        call.enqueue(new Callback<Drink>() {
            @Override
            public void onResponse(Call<Drink> call, Response<Drink> response) {
                if (response.isSuccessful() && response.body() != null) {
                    drink = response.body();
                    txtName.setText(drink.getDrink_name());
                    txtPrice.setText(String.valueOf(drink.getDrink_price()));
                    if ("Remain".equals(drink.getDrink_status())) {
                        rdRemain.setChecked(true);
                    } else {
                        rdOutOfStock.setChecked(true);
                    }
                    if (drink.getDrink_image() != null && !drink.getDrink_image().isEmpty()) {
                        Bitmap bitmap = decodeBase64(drink.getDrink_image());
                        imgDrink.setImageBitmap(bitmap);
                    } else {
                        imgDrink.setImageResource(R.drawable.t);
                    }
                } else {

                    Log.d("EditDrink", "Response error: " + response.code());
                    Toast.makeText(EditDrink.this, "Không thể tải thông tin món", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Drink> call, Throwable t) {
                Log.e("EditCategory", "API error: " + t.getMessage());
                Toast.makeText(EditDrink.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadCategories() {
        Call<List<Category>> call = apiService.getCategory();
        call.enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Category> categories = response.body();
                    ArrayAdapter<Category> adapter = new ArrayAdapter<>(EditDrink.this, R.layout.spinner_item, categories);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner.setAdapter(adapter);
                } else {
                    Toast.makeText(EditDrink.this, "Không tải được danh sách danh mục", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Category>> call, Throwable t) {
                Toast.makeText(EditDrink.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE);
    }

    private Bitmap decodeBase64(String base64Str) {
        byte[] decodedBytes = Base64.decode(base64Str, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            imgDrink.setImageURI(imageUri);
        }
    }

    private String getRealPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        if (cursor != null) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        }
        return null;
    }
    private void editDrink() {
        String drinkName = txtName.getText().toString().trim();
        String drinkPrice = txtPrice.getText().toString().trim();
        String drinkStatus = rdRemain.isChecked() ? "Remain" : (rdOutOfStock.isChecked() ? "OutOfStock" : "");

        if (drinkName.isEmpty() || drinkPrice.isEmpty() || drinkStatus.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đủ thông tin!", Toast.LENGTH_SHORT).show();
            return;
        }

        Category selectedCategory = (Category) spinner.getSelectedItem();
        String categoryId = String.valueOf(selectedCategory.getCategory_id());

        MultipartBody.Part drink_image = null;
        if (imageUri != null) {
            File imageFile = new File(getRealPathFromURI(imageUri));
            RequestBody requestFile = RequestBody.create(MediaType.parse("image/png"), imageFile);
            drink_image = MultipartBody.Part.createFormData("drink_image", imageFile.getName(), requestFile);
        }

        //RequestBody drink_id = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(drink.getDrink_id()));
        RequestBody drink_name = RequestBody.create(MediaType.parse("text/plain"), drinkName);
        RequestBody drink_price = RequestBody.create(MediaType.parse("text/plain"), drinkPrice);
        RequestBody drink_status = RequestBody.create(MediaType.parse("text/plain"), drinkStatus);
        RequestBody category_id = RequestBody.create(MediaType.parse("text/plain"), categoryId);

        Call<Void> call = apiService.updateDrink(drink.getDrink_id(), drink_name, drink_price, drink_status, category_id, drink_image);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(EditDrink.this, "Cập nhật đồ uống thành công", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    try {
                        String errorBody = response.errorBody().string();
                        Log.d("EditDrink", "Error body: " + errorBody);
                        Toast.makeText(EditDrink.this, "Lỗi: " + errorBody, Toast.LENGTH_LONG).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(EditDrink.this, "Không thể đọc thông báo lỗi", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(EditDrink.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}