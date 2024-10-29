package com.example.nekochancoffee.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.nekochancoffee.Fragments.HomeFragment;
import com.example.nekochancoffee.Fragments.TableFragment;
import com.example.nekochancoffee.Model.User;
import com.example.nekochancoffee.R;
import com.example.nekochancoffee.Fragments.CatFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        User user = (User) getIntent().getSerializableExtra("username");


        // BottomNavigationView
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Xử lý sự kiện khi người dùng chọn mục
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;

                // Kiểm tra và chọn fragment tương ứng
                if (item.getItemId() == R.id.nav_home) {
                    selectedFragment = new HomeFragment();
                } else if (item.getItemId() == R.id.nav_cat) {
                    selectedFragment = new CatFragment();
                } else if (item.getItemId() == R.id.nav_table) {
                    selectedFragment = new TableFragment();
                }else if (item.getItemId() == R.id.nav_more) {
                    startActivity(new Intent(MainActivity.this, Option.class));


                }


                // Thay đổi fragment khi có mục được chọn
                if (selectedFragment != null) {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, selectedFragment)
                            .commit();
                }

                return true;
            }
        });

        // Mặc định hiển thị HomeFragment khi mở ứng dụng
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new HomeFragment())
                    .commit();
        }
    }
}
