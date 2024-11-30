package com.example.nekochancoffee.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nekochancoffee.Activities.AddCat;
import com.example.nekochancoffee.Activities.EditCat;
import com.example.nekochancoffee.Activities.MainActivity;
import com.example.nekochancoffee.Adapters.CatAdapter;
import com.example.nekochancoffee.Model.Cat;
import com.example.nekochancoffee.R;
import com.example.nekochancoffee.network.RetrofitClient;
import com.example.nekochancoffee.ApiService;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CatFragment extends Fragment {

    private RecyclerView recyclerViewCats;
    FloatingActionButton btnAddcat;
    private CatAdapter catAdapter;
    private List<Cat> catList;
    ApiService apiService  = RetrofitClient.getClient("https://5725-58-186-29-70.ngrok-free.app/ ").create(ApiService.class);

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_cat, container, false);

        recyclerViewCats = rootView.findViewById(R.id.recyclerViewCats);
        btnAddcat = rootView.findViewById(R.id.btnAddCat);

        // Khởi tạo danh sách mèo
        catList = new ArrayList<>();

        // Thiết lập RecyclerView
        catAdapter = new CatAdapter(getContext(), catList, new CatAdapter.OnCatActionListener() {
            @Override
            public void onDeleteCat(Cat cat) {
                deleteCat(cat.getCatId());

            }

            @Override
            public void onEditCat(Cat cat) {
                Intent intent =new Intent(getActivity(), EditCat.class);
                intent.putExtra("catId",cat.getCatId());
                Toast.makeText(getContext(), "Cat ID: " + cat.getCatId(), Toast.LENGTH_LONG).show();
                startActivity(intent);
            }
        });

        btnAddcat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(),AddCat.class));
            }
        });

        recyclerViewCats.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewCats.setAdapter(catAdapter);

        // Lấy danh sách mèo từ API
        getCats();

        return rootView;
    }
    @Override
    public void onResume() {
        super.onResume();
        getCats();
    }

    // Hàm lấy danh sách mèo từ API
    private void getCats() {
//        ApiService apiService = RetrofitClient.getClient("https://200f-1-52-23-183.ngrok-free.app/").create(ApiService.class);

        Call<List<Cat>> call = apiService.getCats();
        call.enqueue(new Callback<List<Cat>>() {
            @Override
            public void onResponse(Call<List<Cat>> call, Response<List<Cat>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    catList.clear();
                    catList.addAll(response.body());
                    catAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(getContext(), "Không thể tải danh sách mèo", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Cat>> call, Throwable t) {
                Toast.makeText(getContext(), "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void deleteCat(int catId) {
//        ApiService apiService = RetrofitClient.getClient("https://72ec-58-186-28-106.ngrok-free.app/").create(ApiService.class);
        Call<Void> call = apiService.deleteCat(catId);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Mèo đã bị xóa", Toast.LENGTH_SHORT).show();
                    getCats(); // Tải lại danh sách mèo
                } else {
                    Toast.makeText(getContext(), "Xóa mèo thất bại" , Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(getContext(), "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
