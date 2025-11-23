package com.example.dispenser.ui.history;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dispenser.R;
import com.example.dispenser.adapter.HistoryAdapter;
import com.example.dispenser.data.model.HistoryModel;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class HistoryActivity extends AppCompatActivity {
    private HistoryAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_history);
        getSupportActionBar().setTitle("History");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        // DATA DUMMY (ganti dengan Firebase nanti)

//        List<HistoryModel> data = new ArrayList<>();
//        data.add(new HistoryModel(
//                "1",                    // bottleCount
//                "DSP-001",              // dispenserId
//                "1000 ml",              // dispenserVolume
//                "2025-02-14 10:30",     // timestamp
//                "5 detik",              // timeUsed
//                "Original",             // variant
//                "30 ml"                 // volume
//        ));
//
//        data.add(new HistoryModel(
//                "2",
//                "DSP-001",
//                "970 ml",
//                "2025-02-14 11:00",
//                "4.2 detik",
//                "Sambal Mayo",
//                "30 ml"
//        ));
//
//        data.add(new HistoryModel(
//                "3",
//                "DSP-002",
//                "2000 ml",
//                "2025-02-14 12:45",
//                "5.5 detik",
//                "Roasted Sesame",
//                "35 ml"
//        ));



        // SETUP RECYCLER VIEW
        RecyclerView rv = findViewById(R.id.rvHistory);
        rv.setLayoutManager(new LinearLayoutManager(this));
        HistoryController controller = new HistoryController();
         controller.getUserHistory()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(list -> {
//                    adapter.submitList(list);
                    rv.setAdapter(new HistoryAdapter(list));


                }, error -> {
                    Log.e("History", error.getMessage());
                });
//        rv.setAdapter(new HistoryAdapter(data));

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed(); // kembali ke activity sebelumnya
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}