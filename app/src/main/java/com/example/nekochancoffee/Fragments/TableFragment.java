package com.example.nekochancoffee.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nekochancoffee.Activities.AddTable;
import com.example.nekochancoffee.Activities.EditTable;
import com.example.nekochancoffee.Activities.OrderDetail;
import com.example.nekochancoffee.Activities.TableDetail;
import com.example.nekochancoffee.Adapters.TableAdapter;
import com.example.nekochancoffee.Model.Order;
import com.example.nekochancoffee.Model.Payment;
import com.example.nekochancoffee.Model.Table;
import com.example.nekochancoffee.R;
import com.example.nekochancoffee.network.RetrofitClient;
import com.example.nekochancoffee.ApiService;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.JsonObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TableFragment extends Fragment {

    private RecyclerView recyclerViewTable;
    private FloatingActionButton btnAddTable;
    private TableAdapter tableAdapter;
    private WebView webViewPayment;
    private List<Table> tableList;
    ApiService apiService = RetrofitClient.getClient("https://56fc-118-69-96-226.ngrok-free.app/").create(ApiService.class);

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_table, container, false);
        recyclerViewTable = rootView.findViewById(R.id.recyclerViewTables);
        btnAddTable = rootView.findViewById(R.id.btnAddTable);
        webViewPayment = rootView.findViewById(R.id.webViewPayment);
        webViewPayment.getSettings().setJavaScriptEnabled(true);
        webViewPayment.setVisibility(View.GONE);
        tableList = new ArrayList<>();
        Table table = new Table();


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
            @Override
            public void onMomoPayment(Table table) {
            PaymentByMomo(table);
            }
            @Override
            public void onCashPayment(Table table) {
            PaymentByCash(table);
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
        getTables();


//        loadOrder(table_id);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        getTables();
    }

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

    private void deleteTable(int table_id) {
        Call<Void> call = apiService.deleteTable(table_id);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Table deleted", Toast.LENGTH_SHORT).show();
                    getTables();
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
//    private void loadOrder(int table_id) {
//        apiService.getOrderByTableId(table_id).enqueue(new Callback<List<Order>>() {
//            @Override
//            public void onResponse(Call<List<Order>> call, Response<List<Order>> response) {
//                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
//                    Order order = response.body().get(0);
////                    BigDecimal totalPrice = order.getTotal_price();
////                    int orderId = order.getOrder_id();
////                    int point = calculatePoints(totalPrice);
//                } else {
////                    Toast.makeText(getContext(), "Không có đơn hàng nào!", Toast.LENGTH_SHORT).show();
//                }
//            }
//
//            @Override
//            public void onFailure(Call<List<Order>> call, Throwable t) {
//                Toast.makeText(getContext(), "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        });
//    }

//    private void PaymentByCash() {
//
//        Order orderdetail = new Order();
//
//        Order order_status = new Order();
//        order_status.setOrder_status("yes");
//
//        BigDecimal totalPrice = orderdetail.getTotal_price();
//
//        int points = calculatePoints(totalPrice);
//        updateOrderStatus(orderdetail.getOrder_id(), order_status,points);
//
//        Table table = new Table();
//        table.setTable_status("no");
//        apiService.updateTableStatus(orderdetail.getTable_id(),table).enqueue(new Callback<Void>() {
//            @Override
//            public void onResponse(Call<Void> call, Response<Void> response) {
//                Toast.makeText(getContext(), "Cập nhật trạng thái bàn thành công", Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onFailure(Call<Void> call, Throwable t) {
//                Toast.makeText(getContext(), "Cập nhật trạng thái bàn thất bại", Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
private void PaymentByCash(Table table) {
    int table_id = table.getTable_id(); // Lấy ID bàn
    apiService.getOrderByTableId(table_id).enqueue(new Callback<List<Order>>() {
        @Override
        public void onResponse(Call<List<Order>> call, Response<List<Order>> response) {
            if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                Order order = response.body().get(0); // Lấy đơn hàng đầu tiên
                BigDecimal totalPrice = order.getTotal_price();
                int orderId = order.getOrder_id();

                // Tính điểm thưởng
                int points = calculatePoints(totalPrice);

                // Cập nhật trạng thái đơn hàng
                Order order_status = new Order();
                order_status.setOrder_status("yes");
                updateOrderStatus(orderId, order_status, points);

                // Cập nhật trạng thái bàn
                Table updatedTable = new Table();
                updatedTable.setTable_status("no");
                apiService.updateTableStatus(table_id, updatedTable).enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(getContext(), "Thanh toán thành công và cập nhật trạng thái bàn", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), "Cập nhật trạng thái bàn thất bại", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Toast.makeText(getContext(), "Lỗi khi cập nhật trạng thái bàn: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                // Không có đơn hàng nào cho bàn này
                Toast.makeText(getContext(), "Không có đơn hàng nào cho bàn này!", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onFailure(Call<List<Order>> call, Throwable t) {
            Toast.makeText(getContext(), "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
        }
    });
}

    private void PaymentByMomo(Table table) {
        int table_id = table.getTable_id(); // Lấy ID bàn
        apiService.getOrderByTableId(table_id).enqueue(new Callback<List<Order>>() {
            @Override
            public void onResponse(Call<List<Order>> call, Response<List<Order>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    Order order = response.body().get(0); // Lấy đơn hàng đầu tiên
                    BigDecimal totalPrice = order.getTotal_price();
                    int orderId = order.getOrder_id();

                    // Tính điểm thưởng
                    int points = calculatePoints(totalPrice);

                    // Gửi yêu cầu thanh toán MoMo
                    Payment payment = new Payment();
                    payment.setOrder_id(orderId);
                    payment.setTotal_price(totalPrice);

                    apiService.payment(payment).enqueue(new Callback<JsonObject>() {
                        @Override
                        public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                            if (response.isSuccessful()) {
                                JsonObject responseBody = response.body();
                                String payUrl = responseBody.get("payUrl").getAsString();
                                webViewPayment.setVisibility(View.VISIBLE);
                                webViewPayment.loadUrl(payUrl);

                                Order order_status = new Order();
                                order_status.setOrder_status("yes");
                                updateOrderStatus(orderId, order_status, points);

                                // Cập nhật trạng thái bàn
                                Table updatedTable = new Table();
                                updatedTable.setTable_status("no");
                                apiService.updateTableStatus(table_id, updatedTable).enqueue(new Callback<Void>() {
                                    @Override
                                    public void onResponse(Call<Void> call, Response<Void> response) {
                                        if (response.isSuccessful()) {
//                                            Toast.makeText(getContext(), "Thanh toán thành công và cập nhật trạng thái bàn", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(getContext(), "Cập nhật trạng thái bàn thất bại", Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<Void> call, Throwable t) {
                                        Toast.makeText(getContext(), "Lỗi khi cập nhật trạng thái bàn: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            } else {
                                Toast.makeText(getContext(), "Thanh toán thất bại", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<JsonObject> call, Throwable t) {
                            Toast.makeText(getContext(), "Lỗi kết nối khi thanh toán", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    // Không có đơn hàng nào cho bàn này
                    Toast.makeText(getContext(), "Không có đơn hàng nào cho bàn này!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Order>> call, Throwable t) {
                Toast.makeText(getContext(), "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateOrderStatus(int orderId, Order orderStatus, int points) {
        // Gửi yêu cầu cập nhật trạng thái đơn hàng
        apiService.updateOrderStatus(orderId, orderStatus).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Cập nhật trạng thái đơn hàng thành công", Toast.LENGTH_SHORT).show();


                    apiService.getOrderById(orderId).enqueue(new Callback<Order>() {
                        @Override
                        public void onResponse(Call<Order> call, Response<Order> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                int customerId = response.body().getCustomer_id();
                                updateCustomerPoints(customerId, points);
                            } else {
                                Toast.makeText(getContext(), "Không thể lấy thông tin khách hàng từ đơn hàng!", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<Order> call, Throwable t) {
                            Toast.makeText(getContext(), "Lỗi khi lấy thông tin đơn hàng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

                } else {
                    Toast.makeText(getContext(), "Cập nhật trạng thái đơn hàng thất bại", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(getContext(), "Lỗi kết nối khi cập nhật trạng thái đơn hàng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Hàm cập nhật điểm cho khách hàng
    private void updateCustomerPoints(int customerId, int points) {
        apiService.updateCustomerPoints(customerId, points).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
//                    Toast.makeText(getContext(), "Cập nhật điểm khách hàng thành công! Điểm: " + points, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Cập nhật điểm khách hàng thất bại: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(getContext(), "Lỗi kết nối khi cập nhật điểm khách hàng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private int calculatePoints(BigDecimal totalPrice) {
        BigDecimal points = totalPrice.divide(new BigDecimal("1000"), BigDecimal.ROUND_DOWN);
        return points.intValue();
    }

}
