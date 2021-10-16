package com.example.attendanceapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;

public class AttendanceRecordActivity extends AppCompatActivity{
    private ListView listView;
    private ArrayAdapter adapter;
    private ArrayList<String> months;
    private String classId, subjectName, className;
    private DBHelper myDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance_record);

        myDb = new DBHelper(this);

        Intent i = getIntent();
        classId = i.getStringExtra("classId");
        subjectName = i.getStringExtra("subjectName");
        className = i.getStringExtra("className");

        listView = findViewById(R.id.attendance_record_lv);

        months = new ArrayList<>();
        months.clear();
        months.addAll(new DBHelper(this).getMonths(classId));

        adapter = new ArrayAdapter<>(this, R.layout.month_item,R.id.month_tv,months);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener((parent,view,position,id)-> openSheetActivity(position));

        setToolbar();
    }

    private void openSheetActivity(int position) {
        ArrayList<StudentItem> studentItems = new ArrayList<>();
        studentItems.addAll(myDb.getAllStudent(classId));
        String sid_array[] = new String[studentItems.size()];
        String roll_array[] = new String[studentItems.size()];
        String name_array[] = new String[studentItems.size()];
        for(int i=0;i<studentItems.size();i++){
            sid_array[i] = String.valueOf(studentItems.get(i).getStudent_id());
            roll_array[i] = String.valueOf(studentItems.get(i).getRoll());
            name_array[i] = studentItems.get(i).getName();
        }
        Intent intent = new Intent(this,SheetActivity.class);
        intent.putExtra("month_year",months.get(position));
        intent.putExtra("class_id",classId);
        intent.putExtra("class_name",className);
        intent.putExtra("subject_name",subjectName);
        intent.putExtra("sid_array",sid_array);
        intent.putExtra("roll_array",roll_array);
        intent.putExtra("name_array",name_array);
        startActivity(intent);
    }


    private void setToolbar() {
        TextView title = findViewById(R.id.title_toolbar_tv);
        TextView sub_title = findViewById(R.id.subtitle_toolbar_tv);
        ImageButton arrow_back = findViewById(R.id.arrow_back_btn);
        ImageButton save = findViewById(R.id.save_btn);

        title.setSelected(true);
        sub_title.setSelected(true);
        title.setText(subjectName+" ("+className+")");
        sub_title.setText("Monthly Attendance Record");
        save.setVisibility(View.GONE);

        arrow_back.setOnClickListener(view -> onBackPressed());
    }
}