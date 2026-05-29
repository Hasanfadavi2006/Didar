package com.didar.qcreport;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.didar.qcreport.adapter.ActivitiesAdapter;
import com.didar.qcreport.api.DidarApiClient;
import com.didar.qcreport.model.ActivityItem;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private TextView tvStatus;
    private Button btnRefresh;
    private SwipeRefreshLayout swipeRefresh;

    private final DidarApiClient apiClient = new DidarApiClient();
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView  = findViewById(R.id.recycler_view);
        progressBar   = findViewById(R.id.progress_bar);
        tvStatus      = findViewById(R.id.tv_status);
        btnRefresh    = findViewById(R.id.btn_refresh);
        swipeRefresh  = findViewById(R.id.swipe_refresh);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        btnRefresh.setOnClickListener(v -> loadActivities());
        swipeRefresh.setOnRefreshListener(this::loadActivities);

        loadActivities();
    }

    private void loadActivities() {
        setLoading(true);
        tvStatus.setText("در حال دریافت فعالیت‌های برگشت از QC ...");

        executor.execute(() -> {
            try {
                List<JSONObject> raw = apiClient.fetchQCActivities();
                List<ActivityItem> items = new ArrayList<>();
                for (JSONObject obj : raw) {
                    items.add(new ActivityItem(obj));
                }

                mainHandler.post(() -> {
                    setLoading(false);
                    if (items.isEmpty()) {
                        tvStatus.setText("هیچ فعالیتی یافت نشد.");
                    } else {
                        tvStatus.setText("تعداد کل: " + items.size() + " فعالیت");
                        recyclerView.setAdapter(new ActivitiesAdapter(items));
                    }
                });

            } catch (Exception e) {
                mainHandler.post(() -> {
                    setLoading(false);
                    tvStatus.setText("خطا: " + e.getMessage());
                    Toast.makeText(this, "خطا در اتصال به دیدار", Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    private void setLoading(boolean loading) {
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        btnRefresh.setEnabled(!loading);
        swipeRefresh.setRefreshing(false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executor.shutdown();
    }
}
