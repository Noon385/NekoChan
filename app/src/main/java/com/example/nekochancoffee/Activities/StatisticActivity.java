
package com.example.nekochancoffee.Activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.nekochancoffee.ApiService;
import com.example.nekochancoffee.Model.Bestseller;
import com.example.nekochancoffee.Model.Drink;
import com.example.nekochancoffee.Model.Order;
import com.example.nekochancoffee.Model.Revenue;
import com.example.nekochancoffee.R;
import com.example.nekochancoffee.network.RetrofitClient;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StatisticActivity extends AppCompatActivity {

    ApiService apiService = RetrofitClient.getClient("https://bde3-42-119-80-131.ngrok-free.app/").create(ApiService.class);
    private EditText startDate, endDate;
    private Button btnFilter;
    private Spinner spinnerStatistic;
    private BarChart chart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistic);

        // Toolbar setup
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar_Statistic);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(StatisticActivity.this, Option.class));
                finish();
            }
        });

        // Initialize views
        startDate = findViewById(R.id.startDate);
        endDate = findViewById(R.id.endDate);
        btnFilter = findViewById(R.id.btnFilter);
        spinnerStatistic = findViewById(R.id.spinnerStatistic);
        chart = findViewById(R.id.chart);

        // Setup chart
        setupChart(chart);

        // Set DatePicker for EditTexts
        setupDatePicker(startDate);
        setupDatePicker(endDate);

        List<String> statisticOptions = new ArrayList<>();
        statisticOptions.add("Doanh thu");
        statisticOptions.add("Món bán chạy");
        statisticOptions.add("Mèo được ưa thích");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_item, statisticOptions);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // Áp dụng layout sẵn có cho danh sách dropdown
        spinnerStatistic.setAdapter(adapter);

        // Button filter logic
        spinnerStatistic.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedOption = parent.getItemAtPosition(position).toString();

                // Xử lý logic dựa trên lựa chọn
                btnFilter.setOnClickListener(v -> {
                    String start = startDate.getText().toString();
                    String end = endDate.getText().toString();

                    if (start.isEmpty() || end.isEmpty()) {
                        Toast.makeText(StatisticActivity.this, "Vui lòng chọn khoảng thời gian", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (selectedOption.equals("Doanh thu")) {
                        fetchRevenueData(chart, start, end);
                    } else if (selectedOption.equals("Món bán chạy")) {
                        fetchBestsellerData(chart, start, end);
                    } else if (selectedOption.equals("Mèo được ưa thích")) {
                    fetchBestcatsData(chart, start, end);
                }
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        fetchRevenueData(chart, null, null);
    }

    private void setupChart(BarChart chart) {
        chart.getAxisRight().setDrawLabels(false);
        chart.getDescription().setEnabled(false);
        chart.setFitBars(true);
        chart.setBackgroundColor(getResources().getColor(R.color.cream));

        // Customize X-axis
        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setGranularityEnabled(true);
    }

    private void setupDatePicker(EditText editText) {
        editText.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            DatePickerDialog datePickerDialog = new DatePickerDialog(StatisticActivity.this,
                    (view, year, month, dayOfMonth) -> {
                        calendar.set(year, month, dayOfMonth);
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                        editText.setText(sdf.format(calendar.getTime()));
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.show();
        });
    }

    //revenue
    private void fetchRevenueData(BarChart chart, String startDate, String endDate) {
        Call<List<Revenue>> call = apiService.getRevenueByDateRange(startDate, endDate);
        call.enqueue(new Callback<List<Revenue>>() {
            @Override
            public void onResponse(Call<List<Revenue>> call, Response<List<Revenue>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Revenue> revenueList = response.body();
                    if (revenueList.isEmpty()) {
                        Toast.makeText(StatisticActivity.this, "Không có dữ liệu trong khoảng thời gian này", Toast.LENGTH_SHORT).show();
                    } else {
                        updateChart(chart, revenueList);
                    }
                } else {
                    Toast.makeText(StatisticActivity.this, "Lỗi kết nối, vui lòng thử lại", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Revenue>> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(StatisticActivity.this, "Lỗi khi lấy dữ liệu", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateChart(BarChart chart, List<Revenue> revenueList) {
        ArrayList<BarEntry> entries = new ArrayList<>();
        ArrayList<String> labels = new ArrayList<>();

        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()); // Định dạng ngày từ API (yyyy-MM-dd)
        SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()); // Định dạng ngày muốn hiển thị (dd/MM/yyyy)

        for (int i = 0; i < revenueList.size(); i++) {
            Revenue revenue = revenueList.get(i);
            float revenueAmount = revenue.getTotal_revenue().floatValue();
            entries.add(new BarEntry(i, revenueAmount));

            try {
                // Chuyển đổi ngày từ API sang định dạng mới
                Date date = inputFormat.parse(revenue.getOrder_date()); // Lấy ngày từ API
                String formattedDate = outputFormat.format(date); // Định dạng lại ngày
                labels.add(formattedDate);
            } catch (Exception e) {
                e.printStackTrace();
                labels.add(revenue.getOrder_date()); // Nếu có lỗi, giữ nguyên ngày từ API
            }
        }

        // Tạo dữ liệu cho biểu đồ
        BarDataSet dataSet = new BarDataSet(entries, "Doanh thu");
        dataSet.setColor(getResources().getColor(R.color.teal));
        BarData data = new BarData(dataSet);
        data.setBarWidth(0.9f);

        // Cập nhật dữ liệu cho biểu đồ
        chart.setData(data);
        chart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels)); // Hiển thị nhãn với định dạng ngày mới
        chart.invalidate(); // Vẽ lại biểu đồ
    }

    // bestseller
    private void fetchBestsellerData(BarChart chart, String startDate, String endDate) {
        Call<List<Order>> call = apiService.getBestsellersByDateRange(startDate, endDate);
        call.enqueue(new Callback<List<Order>>() {
            @Override
            public void onResponse(Call<List<Order>> call, Response<List<Order>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Order> bestsellerList = response.body();
                    if (bestsellerList.isEmpty()) {
                        Toast.makeText(StatisticActivity.this, "Không có dữ liệu món bán chạy", Toast.LENGTH_SHORT).show();
                    } else {
                        updateBestsellerChart(chart, bestsellerList);
                    }
                } else {
                    Toast.makeText(StatisticActivity.this, "Lỗi kết nối, vui lòng thử lại", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Order>> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(StatisticActivity.this, "Lỗi khi lấy dữ liệu", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateBestsellerChart(BarChart chart, List<Order> bestsellerList) {
        ArrayList<BarEntry> entries = new ArrayList<>();
        ArrayList<String> labels = new ArrayList<>();

        for (int i = 0; i < bestsellerList.size(); i++) {
            Order bestseller = bestsellerList.get(i);

            entries.add(new BarEntry(i, bestseller.getTotal_amount()));

            labels.add(bestseller.getDrink_name());
        }
        Log.d("BestsellerData", bestsellerList.toString());
        BarDataSet dataSet = new BarDataSet(entries, "Món bán chạy");
        dataSet.setColor(getResources().getColor(R.color.teal));
        BarData data = new BarData(dataSet);
        data.setBarWidth(0.9f);

        chart.setData(data);
        chart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels));
        chart.invalidate();
    }

    // bestcats
    private void fetchBestcatsData(BarChart chart, String startDate, String endDate) {
        Call<List<Order>> call = apiService.getBestcatsByDateRange(startDate, endDate);
        call.enqueue(new Callback<List<Order>>() {
            @Override
            public void onResponse(Call<List<Order>> call, Response<List<Order>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Order> bestcatsList = response.body();
                    if (bestcatsList.isEmpty()) {
                        Toast.makeText(StatisticActivity.this, "Không có dữ liệu mèo", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.d("RawAPIResponse", response.body().toString());
                        updateBestcatsChart(chart, bestcatsList);
                    }
                } else {
                    Toast.makeText(StatisticActivity.this, "Lỗi kết nối, vui lòng thử lại", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Order>> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(StatisticActivity.this, "Lỗi khi lấy dữ liệu", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateBestcatsChart(BarChart chart, List<Order> bestcatsList) {
        ArrayList<BarEntry> entries = new ArrayList<>();
        ArrayList<String> labels = new ArrayList<>();

        // Logging to ensure data is correctly received
        Log.d("BestcatsData", "Received data size: " + bestcatsList.size());

        for (int i = 0; i < bestcatsList.size(); i++) {
            Order bestcat = bestcatsList.get(i);
            String catName = bestcat.getCat_name() != null ? bestcat.getCat_name() : "No Name";
            int catAmount = bestcat.getCat_amount();
            Log.d("BestcatsData", "Cat: " + bestcat.getCat_name() + ", Amount: " + bestcat.getCat_amount());



            entries.add(new BarEntry(i, catAmount));  // Using the amount for the Y value
            labels.add(catName);  // Using the cat name for the X label
        }

        // Create the BarDataSet and set chart data
        BarDataSet dataSet = new BarDataSet(entries, "Mèo được ưa thích");
        dataSet.setColor(getResources().getColor(R.color.teal));  // Set the bar color
        BarData data = new BarData(dataSet);
        data.setBarWidth(0.9f);  // Set bar width for better spacing

        // Set the data to the chart
        chart.setData(data);

        // Set the labels for the X axis
        chart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels));
        chart.invalidate();  // Refresh the chart to display the new data
    }




}

