package com.example.nekochancoffee.Activities;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.nekochancoffee.ApiService;
import com.example.nekochancoffee.R;
import com.example.nekochancoffee.Model.LoginResponse;
import com.example.nekochancoffee.network.RetrofitClient;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
//
//public class LoginActivity extends AppCompatActivity {
//
//    private EditText txtUsername, txtPassword;
//    private Button btnLogin;
//    private ApiService apiService;
//    private SharedPreferences sharedPreferences;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_login);
//
//        // Liên kết các thành phần UI
//        txtUsername = findViewById(R.id.txtUsername);
//        txtPassword = findViewById(R.id.txtPassword);
//        btnLogin = findViewById(R.id.btnLogin);
//
//        // Khởi tạo Retrofit với đường dẫn API của bạn
//        apiService = RetrofitClient.getClient("https://675e-42-115-92-23.ngrok-free.app/").create(ApiService.class);
//        // Xử lý khi nhấn nút đăng nhập
//        btnLogin.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String username = txtUsername.getText().toString();
//                String password = txtPassword.getText().toString();
//
//                // Kiểm tra thông tin đầu vào
//                if (TextUtils.isEmpty(username)) {
//                    txtUsername.setError("Vui lòng nhập username");
//                } else if (TextUtils.isEmpty(password)) {
//                    txtPassword.setError("Vui lòng nhập mật khẩu");
//                } else {
//                   login(username, password);
//
////                        Intent intent=new Intent(LoginActivity.this,MainActivity.class);
////                        startActivity(intent);
//
//
//
//                }
//            }
//        });
//    }
//
//    private void login(String username, String password) {
//        Call<LoginResponse> call = apiService.login(username, password);
//        call.enqueue(new Callback<LoginResponse>() {
//            @Override
//            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
//                if (response.isSuccessful() && response.body() != null) {
//                    LoginResponse loginResponse = response.body();
//                    if (loginResponse.isSuccess()) {
//                        // Đăng nhập thành công
//
//                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
//
//                        startActivity(intent);
//                        finish();
//                    } else {
//                        Toast.makeText(LoginActivity.this, loginResponse.getMessage(), Toast.LENGTH_SHORT).show();
//                    }
//                } else {
//                    // Đọc phản hồi dưới dạng chuỗi nếu không phải JSON
//                    try {
//                        String errorBody = response.errorBody().string();
//                        Log.d("LoginActivity", "Error body: " + errorBody);
//                        Toast.makeText(LoginActivity.this, "Lỗi: " + errorBody, Toast.LENGTH_SHORT).show();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                    Log.d("LoginActivity", "Response code: " + response.code());
//                }
//            }
//
//            @Override
//            public void onFailure(Call<LoginResponse> call, Throwable t) {
//                Toast.makeText(LoginActivity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
//                Log.e("LoginActivity", "Lỗi khi gọi API đăng nhập: ", t);
//            }
//        });
//    }
//
//}
public class LoginActivity extends AppCompatActivity {

    private EditText usernameEditText, passwordEditText;
    private Button loginButton;
    private SharedPreferences sharedPreferences;
    ApiService apiService  = RetrofitClient.getClient("https://bde3-42-119-80-131.ngrok-free.app/").create(ApiService.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usernameEditText = findViewById(R.id.txtUsername);
        passwordEditText = findViewById(R.id.txtPassword);
        loginButton = findViewById(R.id.btnLogin);

        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);

        loginButton.setOnClickListener(v -> {
            String username = usernameEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();
//            Toast.makeText(LoginActivity.this, ""+username+password, Toast.LENGTH_SHORT).show();
            login(username, password);

//            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
//            startActivity(intent);
//            finish();
        });
    }

    private void login(String username, String password) {
        Call<LoginResponse> call = apiService.login(username, password);
        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("LoginActivity", "Response ID: " + response.body().getId());
                    if (response.body().isSuccess()) {
                        String role = response.body().getRole();
                        int id = response.body().getId();
//                        Toast.makeText(LoginActivity.this, id, Toast.LENGTH_SHORT).show();
                        // Lưu vai trò vào SharedPreference
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("role", role);
                        editor.putString("username",username);
                        editor.putInt("id",id);
                        editor.apply();

                        // Điều hướng tới màn hình chính
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(LoginActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "Đăng nhập thất bại!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
