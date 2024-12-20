package com.example.nekochancoffee.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.nekochancoffee.Adapters.CustomerAdapter;
import com.example.nekochancoffee.Adapters.UserAdapter;
import com.example.nekochancoffee.ApiService;
import com.example.nekochancoffee.Model.Customer;
import com.example.nekochancoffee.Model.User;
import com.example.nekochancoffee.R;
import com.example.nekochancoffee.network.RetrofitClient;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

public class AddCustomer extends AppCompatActivity {

    private TextInputEditText txtName, txtPhone;
    private Button btnAddCustomer;
    CustomerAdapter adapter;
    private List<Customer> customerList = new ArrayList<>();
    ApiService apiService = RetrofitClient.getClient("https://56fc-118-69-96-226.ngrok-free.app/").create(ApiService.class);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_customer);

        txtName = findViewById(R.id.txtName);
        txtPhone = findViewById(R.id.txtPhone);
        btnAddCustomer = findViewById(R.id.btnAddCustomer);


        adapter = new CustomerAdapter(customerList, AddCustomer.this, apiService);

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar_addcustomer);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Trở về Activity trước đó
            }
        });

        btnAddCustomer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                addCustomer();
//                adapter.notifyDataSetChanged();
//                finish();


            }
        });
    }

    private void addCustomer() {
        String customerName = txtName.getText().toString().trim();
        String customerPhone = txtPhone.getText().toString().trim();

        if (customerName.isEmpty() || customerPhone.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
            return;
        }

        Customer newCustomer = new Customer();
        newCustomer.setCustomer_name(customerName);
        newCustomer.setCustomer_phone(customerPhone);


        adapter.addCustomer(newCustomer);
        adapter.notifyDataSetChanged();


        finish();
    }
}