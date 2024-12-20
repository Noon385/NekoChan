package com.example.nekochancoffee.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nekochancoffee.Activities.CustomerDetail;
import com.example.nekochancoffee.Activities.EditCustomer;
import com.example.nekochancoffee.Activities.EditUser;
import com.example.nekochancoffee.Activities.UserDetail;
import com.example.nekochancoffee.ApiService;
import com.example.nekochancoffee.Model.Customer;
import com.example.nekochancoffee.R;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CustomerAdapter extends RecyclerView.Adapter<CustomerAdapter.CustomerViewHolder> {

    private List<Customer> customerList;
    private Context context;
    private ApiService customerApi;

    public CustomerAdapter(List<Customer> customerList, Context context, ApiService customerApi) {
        this.customerList = customerList;
        this.context = context;
        this.customerApi = customerApi;
    }

    @NonNull
    @Override
    public CustomerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.customer_item, parent, false);
        return new CustomerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomerViewHolder holder, int position) {
        Customer customer = customerList.get(position);
        holder.tvName.setText("Họ và tên: " + customer.getCustomer_name());
        holder.tvPhone.setText("Số điện thoại: " + customer.getCustomer_phone());
        holder.tvPoint.setText("Điểm số: " + (customer.getCustomer_point() != null ? String.valueOf(customer.getCustomer_point()) : "0"));
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, CustomerDetail.class);
            intent.putExtra("customer", customer);
            context.startActivity(intent);
        });

        holder.itemView.setOnLongClickListener(v -> {
            showPopupMenu(holder.itemView, position, customer);
            return true;
        });
    }


    @Override
    public int getItemCount() {
        return customerList.size();
    }

    public class CustomerViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvPhone, tvPoint;
        Button btnMore;

        public CustomerViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.txtName);
            tvPhone = itemView.findViewById(R.id.txtPhone);
            tvPoint = itemView.findViewById(R.id.txtPoint);
//            btnMore = itemView.findViewById(R.id.btnMore);
        }
    }


    private void showPopupMenu(View view, int position, Customer customer) {
        PopupMenu popupMenu = new PopupMenu(context, view);
        MenuInflater inflater = popupMenu.getMenuInflater();
        inflater.inflate(R.menu.menu_cat_action_delete_edit, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();

            if (id == R.id.action_delete) {
                deleteCustomer(position, customer.getCustomer_id());
                return true;
            } else if (id == R.id.action_edit) {
                Intent intent = new Intent(context, EditCustomer.class);
                intent.putExtra("customer",customer);
                context.startActivity(intent);
                return true;
            } else {
                return false;
            }
        });
        popupMenu.show();
    }


    public void addCustomer(Customer customer) {
        customerApi.addCustomer(customer).enqueue(new Callback<Customer>() {
            @Override
            public void onResponse(Call<Customer> call, Response<Customer> response) {
                if (response.isSuccessful()) {
                    customerList.add(response.body());

                } else {
                    Toast.makeText(context, "Lỗi khi thêm khách hàng!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Customer> call, Throwable t) {
                Toast.makeText(context, "Lỗi kết nối khi thêm khách hàng!", Toast.LENGTH_SHORT).show();
            }
        });
    }


    public void editCustomer(Customer customer) {
        customerApi.updateCustomer(customer.getCustomer_id(), customer).enqueue(new Callback<Customer>() {
            @Override
            public void onResponse(Call<Customer> call, Response<Customer> response) {
                if (response.isSuccessful()) {
                    notifyDataSetChanged();
                    Toast.makeText(context, "Cập nhật thông tin thành công!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Lỗi khi cập nhật thông tin!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Customer> call, Throwable t) {
                Toast.makeText(context, "Lỗi kết nối khi cập nhật thông tin!", Toast.LENGTH_SHORT).show();
            }
        });
    }


    public void deleteCustomer(int position, int customerId) {
        customerApi.deleteCustomer(customerId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    customerList.remove(position);
                    notifyItemRemoved(position);
                    Toast.makeText(context, "Xóa khách hàng thành công!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Lỗi khi xóa khách hàng!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(context, "Lỗi kết nối khi xóa khách hàng!", Toast.LENGTH_SHORT).show();
            }
        });
    }


    public void viewCustomerCats(Customer customer) {
        Toast.makeText(context, "Xem danh sách mèo của: " + customer.getCustomer_name(), Toast.LENGTH_SHORT).show();
    }
}
