package com.example.nekochancoffee.Fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nekochancoffee.Activities.AddCat;
import com.example.nekochancoffee.Activities.CategoryActivity;
import com.example.nekochancoffee.Activities.EditCat;
import com.example.nekochancoffee.Adapters.CatAdapter;
import com.example.nekochancoffee.Adapters.CategoryAdapter;
import com.example.nekochancoffee.ApiService;
import com.example.nekochancoffee.Model.Cat;
import com.example.nekochancoffee.Model.Category;
import com.example.nekochancoffee.R;
import com.example.nekochancoffee.network.RetrofitClient;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class HomeFragment extends Fragment {

    private TextView txtCategory,txtOrder;
    private RecyclerView recyclerViewCategory,recyclerViewOrder;
    private CategoryAdapter categoryAdapter;

    private List<Category> categoryList;
    ApiService apiService  = RetrofitClient.getClient("https://4dfb-58-186-47-131.ngrok-free.app/ ").create(ApiService.class);

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerViewCategory = rootView.findViewById(R.id.recyclerViewCategory);
        recyclerViewOrder = rootView.findViewById(R.id.recyclerViewOrder);
        txtCategory = rootView.findViewById(R.id.txtCategory);
        txtOrder = rootView.findViewById(R.id.txtOrder);

        categoryList = new ArrayList<>();
        categoryAdapter = new CategoryAdapter(getContext(), categoryList);

        txtCategory.setOnClickListener(v -> startActivity(new Intent(getContext(), CategoryActivity.class)));


        recyclerViewCategory.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false));
        recyclerViewCategory.setAdapter(categoryAdapter);

        getCategory();

        return rootView;
    }
    @Override
    public void onResume() {
        super.onResume();
        getCategory();
    }


    private void getCategory() {
//        ApiService apiService = RetrofitClient.getClient("https://200f-1-52-23-183.ngrok-free.app/").create(ApiService.class);

        Call<List<Category>> call = apiService.getCategory();
        call.enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    categoryList.clear();
                    categoryList.addAll(response.body());
                    categoryAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(getContext(), "Không thể tải danh sách loại món", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Category>> call, Throwable t) {
                Toast.makeText(getContext(), "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}