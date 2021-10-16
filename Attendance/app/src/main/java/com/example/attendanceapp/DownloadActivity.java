package com.example.attendanceapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class DownloadActivity extends AppCompatActivity {

    private LinearLayout homeActivity;
    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private DBHelper myDb;
    private ArrayList<ClassItem> classItems;
    private RVAdapterForDownload rvAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);

        classItems = new ArrayList<>();
        myDb = new DBHelper(this);
        toolbar = findViewById(R.id.toolbar);
        homeActivity = findViewById(R.id.home_activity);

        recyclerView = findViewById(R.id.classes_rv_da);
        recyclerView.setHasFixedSize(true);
        classItems.addAll(myDb.getAllClass());
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        rvAdapter = new RVAdapterForDownload(this,classItems);
        recyclerView.setAdapter(rvAdapter);

        setToolbar();

        rvAdapter.setOnItemClickListener(position -> {
            Intent i = new Intent(this,AttendanceRecordActivity.class);
            i.putExtra("classId",Long.toString(classItems.get(position).getClass_id()));
            i.putExtra("subjectName",classItems.get(position).getSubject_name());
            i.putExtra("className",classItems.get(position).getClass_name());
            startActivity(i);
        });


        homeActivity.setOnClickListener(view -> {
            this.startActivity(new Intent(this, MainActivity.class));
            this.overridePendingTransition(0, 0);
            finish();
        });
    }
    private void setToolbar() {
        TextView title = findViewById(R.id.title_toolbar_tv);
        TextView sub_title = findViewById(R.id.subtitle_toolbar_tv);
        ImageButton arrow_back = findViewById(R.id.arrow_back_btn);
        ImageButton save = findViewById(R.id.save_btn);

        title.setText("Downloads");
        sub_title.setVisibility(View.GONE);
        arrow_back.setVisibility(View.GONE);
        save.setVisibility(View.GONE);
    }
}