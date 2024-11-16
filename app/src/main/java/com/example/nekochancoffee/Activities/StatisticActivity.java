package com.example.nekochancoffee.Activities;

import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.nekochancoffee.ApiService;
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
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StatisticActivity extends AppCompatActivity {

    ApiService apiService  = RetrofitClient.getClient("https://3d81-2001-ee0-51b2-2550-541a-a894-eb1-5c57.ngrok-free.app/").create(ApiService.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistic);
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar_Statistic);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        BarChart chart =findViewById(R.id.chart);
        setupChart(chart);
        fetchRevenueData(chart);
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

    private void fetchRevenueData(BarChart chart) {
        Call<List<Revenue>> call = apiService.getRevenue(); // Giả sử API trả về danh sách doanh thu
        call.enqueue(new Callback<List<Revenue>>() {
            @Override
            public void onResponse(Call<List<Revenue>> call, Response<List<Revenue>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Revenue> revenueList = response.body();
                    updateChart(chart, revenueList);
                }
            }

            @Override
            public void onFailure(Call<List<Revenue>> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private void updateChart(BarChart chart, List<Revenue> revenueList) {
        ArrayList<BarEntry> entries = new ArrayList<>();
        ArrayList<String> labels = new ArrayList<>();

        // Định dạng ngày tháng (ví dụ: "06/11/2024")
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()); // Định dạng đầu vào từ API
        SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

        for (int i = 0; i < revenueList.size(); i++) {
            Revenue revenue = revenueList.get(i);
            float revenueAmount = revenue.getTotal_revenue().floatValue();
            entries.add(new BarEntry(i, revenueAmount));

            // Chuyển đổi và định dạng ngày
            try {
                Date date = inputFormat.parse(revenue.getOrder_date());
                String formattedDate = outputFormat.format(date);
                labels.add(formattedDate); // Thêm ngày đã định dạng vào danh sách labels
            } catch (Exception e) {
                e.printStackTrace();
                labels.add(revenue.getOrder_date()); // Nếu có lỗi, dùng giá trị gốc
            }
        }

        BarDataSet dataSet = new BarDataSet(entries, "Doanh thu");
        dataSet.setColor(getResources().getColor(R.color.teal));
        BarData data = new BarData(dataSet);
        data.setBarWidth(0.9f);

        chart.setData(data);
        chart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels)); // Hiển thị ngày tháng trên trục X
        chart.invalidate(); // Làm mới biểu đồ
    }
}