package com.example.nekochancoffee.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.nekochancoffee.Adapters.UserAdapter;
import com.example.nekochancoffee.ApiService;
import com.example.nekochancoffee.Model.User;
import com.example.nekochancoffee.R;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nekochancoffee.network.RetrofitClient;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserActivity extends AppCompatActivity implements UserAdapter.OnUserActionListener {

    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    private List<User> userList;
    private FloatingActionButton fabAddUser;
    private ApiService apiService = RetrofitClient.getClient("https://4dfb-58-186-47-131.ngrok-free.app/").create(ApiService.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        recyclerView = findViewById(R.id.recyclerViewUsers);
        fabAddUser = findViewById(R.id.fabAddUser);

        // Configure RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Fetch user list from the API
        getAllUsers();

        // Add user button
        fabAddUser.setOnClickListener(v -> {
            startActivity(new Intent(UserActivity.this, AddUser.class));
        });

        // Set up toolbar
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar_user);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());
    }


    private void getAllUsers() {
        apiService.getAllUsers().enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    userList = response.body();
                    userAdapter = new UserAdapter(userList, UserActivity.this, apiService, UserActivity.this);
                    recyclerView.setAdapter(userAdapter);
                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                // Handle failure to retrieve data
            }
        });
    }

    @Override
    public void onEditUser(User user) {
        Intent intent = new Intent(this, EditUser.class);
        intent.putExtra("user", user);
        startActivity(intent);
    }

    @Override
    public void onDeleteUser(User user) {
        int position = userList.indexOf(user);
        if (position != -1) {
            userAdapter.deleteUser(user.getId(), position);
        }
    }
}
