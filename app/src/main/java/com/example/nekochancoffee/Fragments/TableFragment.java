package com.example.nekochancoffee.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nekochancoffee.Activities.AddTable;
import com.example.nekochancoffee.Activities.EditTable;
import com.example.nekochancoffee.Adapters.TableAdapter;
import com.example.nekochancoffee.Model.Table;
import com.example.nekochancoffee.R;
import com.example.nekochancoffee.network.RetrofitClient;
import com.example.nekochancoffee.ApiService;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TableFragment extends Fragment {

    private RecyclerView recyclerViewTable;
    private FloatingActionButton btnAddTable;
    private TableAdapter tableAdapter;
    private List<Table> tableList;
    ApiService apiService = RetrofitClient.getClient("https://1c38-58-186-29-70.ngrok-free.app/").create(ApiService.class);

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_table, container, false);

        recyclerViewTable = rootView.findViewById(R.id.recyclerViewTables);
        btnAddTable = rootView.findViewById(R.id.btnAddTable);

        // Initialize the table list
        tableList = new ArrayList<>();

        // Set up RecyclerView with the adapter
        tableAdapter = new TableAdapter(getContext(), tableList, new TableAdapter.OnTableActionListener() {
            @Override
            public void onDeleteTable(Table table) {
                deleteTable(table.getTable_id());
                Toast.makeText(getContext(), "Table ID: " + table.getTable_id(), Toast.LENGTH_LONG).show();
            }
            @Override
            public void onEditTable(Table table) {
                Intent intent = new Intent(getActivity(), EditTable.class);
                intent.putExtra("table_id", table.getTable_id());
                Toast.makeText(getContext(), "Table ID: " + table.getTable_id(), Toast.LENGTH_LONG).show();
                startActivity(intent);
            }
        });

        btnAddTable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), AddTable.class));
            }
        });

        recyclerViewTable.setLayoutManager(new GridLayoutManager(getContext(),2));
        recyclerViewTable.setAdapter(tableAdapter);

        // Fetch the list of tables from the API
        getTables();

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        getTables();
    }

    // Method to fetch tables from the API
    private void getTables() {
        Call<List<Table>> call = apiService.getTable();
        call.enqueue(new Callback<List<Table>>() {
            @Override
            public void onResponse(Call<List<Table>> call, Response<List<Table>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    tableList.clear();
                    tableList.addAll(response.body());
                    tableAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(getContext(), "Unable to load table list", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Table>> call, Throwable t) {
                Toast.makeText(getContext(), "Connection error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Method to delete a table
    private void deleteTable(int table_id) {
        Call<Void> call = apiService.deleteTable(table_id);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Table deleted", Toast.LENGTH_SHORT).show();
                    getTables(); // Reload table list
                } else {
                    Toast.makeText(getContext(), "Failed to delete table", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(getContext(), "Connection error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
