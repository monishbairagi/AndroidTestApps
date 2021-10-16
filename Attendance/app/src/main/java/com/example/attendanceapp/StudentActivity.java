package com.example.attendanceapp;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.os.Build.VERSION.SDK_INT;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.InputType;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.opencsv.CSVReader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

public class StudentActivity extends AppCompatActivity {
    private static final int OPEN_CSV = 101;
    private static final int TAKE_USER_PERMISSION = 102;
    private Toolbar toolbar;
    private RecyclerView students_rv;
    private ArrayList<StudentItem> studentItems;
    private StudentAdapter studentAdapter;
    private DBHelper myDb;
    private String classId, subjectName, className;
    private MyCalendar calendar;
    private TextView total_tv, present_tv, absent_tv, remaining_tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student);

        studentItems = new ArrayList<>();
        calendar = new MyCalendar();
        myDb = new DBHelper(this);

        Intent intent = getIntent();
        classId = intent.getStringExtra("classId");
        subjectName = intent.getStringExtra("subjectName");
        className = intent.getStringExtra("className");

        total_tv = findViewById(R.id.total_student_tv);
        present_tv = findViewById(R.id.total_present_tv);
        absent_tv = findViewById(R.id.total_absent_tv);
        remaining_tv = findViewById(R.id.total_remaining_tv);

        setToolbar();

        studentItems.clear();
        studentItems.addAll(myDb.getAllStudent(classId));

        students_rv = findViewById(R.id.students_rv);
        students_rv.setHasFixedSize(true);

        RecyclerView.LayoutManager  layoutManager = new LinearLayoutManager(this);
        students_rv.setLayoutManager(layoutManager);

        studentAdapter = new StudentAdapter(this, studentItems);
        students_rv.setAdapter(studentAdapter);
        loadStudents(calendar.getDate());

        studentAdapter.setOnItemClickListener(p->{
            String status = studentItems.get(p).getStatus();
            if(status.equals("") || status.equals("A")) {
                status = "P";
            }else{
                status = "A";
            }
            studentItems.get(p).setStatus(status);
            studentAdapter.notifyItemChanged(p);
            updateStatistics();
        });
    }

    private void setToolbar() {
        toolbar = findViewById(R.id.toolbar);
        TextView title = findViewById(R.id.title_toolbar_tv);
        TextView sub_title = findViewById(R.id.subtitle_toolbar_tv);
        ImageButton arrow_back = findViewById(R.id.arrow_back_btn);
        ImageButton save = findViewById(R.id.save_btn);

        title.setSelected(true);
        sub_title.setSelected(true);
        title.setText(subjectName+" ("+className+")");
        sub_title.setText("Today");

        arrow_back.setOnClickListener(view -> onBackPressed());
        save.setOnClickListener(view -> saveAttendance());

        toolbar.inflateMenu(R.menu.student_menu);
        toolbar.setOnMenuItemClickListener(menuItem -> onMenuItemClick(menuItem));
    }

    private void loadStudents(String date) {
        for(StudentItem student : studentItems){
            String sid = String.valueOf(student.getStudent_id());
            String status = myDb.getStatus(sid,date);
            if (status != null && status.equals("A")){
                student.setStatus("A");
            }
            else if (status != null && status.equals("P")) {
                student.setStatus("P");
            } else {
                student.setStatus("");
            }
            updateStatistics();
        }
        studentAdapter.notifyDataSetChanged();
    }

    private void saveAttendance() {
        String total = String.valueOf(
                Integer.parseInt(total_tv.getText().toString().replaceAll("[^0-9]", ""))
        );
        if(!total.equals("0")) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setCancelable(false);

            String remaining = String.valueOf(
                    Integer.parseInt(remaining_tv.getText().toString().replaceAll("[^0-9]", ""))
            );
            builder.setTitle("Do you want to save?");
            if (!remaining.equals("0")) {
                String msg = "Remaining " + remaining + " students will be saved as absent";
                builder.setMessage(msg);
            }
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int idx) {
                    for (int i = 0; i < studentItems.size(); i++) {
                        StudentItem student = studentItems.get(i);
                        String sid = String.valueOf(student.getStudent_id());
                        String status = student.getStatus();
                        if (status != "P") status = "A";
                        if (myDb.getStatus(sid,calendar.getDate()) == null){
                            myDb.addAttendance(sid, classId, calendar.getDate(), status);
                        } else myDb.editAttendance(sid, calendar.getDate(), status);
                    }
                    finish();
                    dialogInterface.dismiss();
                }
            });
            builder.create().show();
        }
    }

    private boolean onMenuItemClick(MenuItem menuItem) {
        switch (menuItem.getItemId()){
            case R.id.import_csv_menu:
                readCSV();
                break;
            case R.id.add_student_menu:
                addStudent();
                break;
            case R.id.change_date_menu:
                changeDate();
                break;
        }
        return false;
    }

    private void readCSV() {
        if(!checkPermission()){
            requestPermission();
        }else{
            afterPermission();
        }
    }

    private void addStudent() {
        if (studentItems.size()>=150) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setCancelable(false);
            builder.setTitle("You cannot include more than 150 students in a class!");
            builder.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            builder.create().show();
        } else {
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            View v = LayoutInflater.from(this).inflate(R.layout.my_dialog, null);

            TextView title = v.findViewById(R.id.my_dialog_title_tv);
            EditText student_roll_et = v.findViewById(R.id.my_dialog_et_1);
            EditText student_name_et = v.findViewById(R.id.my_dialog_et_2);
            Button cancel = v.findViewById(R.id.cancel);
            Button ok = v.findViewById(R.id.ok);

            title.setText("Add New Student");
            student_roll_et.setInputType(InputType.TYPE_CLASS_NUMBER);
            student_roll_et.setHint("Enter Roll Number");
            student_name_et.setHint("Enter Student Name");

            dialog.setView(v);
            AlertDialog alertDialog = dialog.create();
            alertDialog.show();

            cancel.setOnClickListener(view -> alertDialog.dismiss());
            ok.setOnClickListener(view -> {

                String student_roll = student_roll_et.getText().toString();
                String student_name = student_name_et.getText().toString();
                if (student_roll.equals("")) {
                    student_roll_et.setError("Required");
                } else if (searchRoll(studentItems, student_roll)) {
                    student_roll_et.setError("Already exists");
                }
                if (student_name.equals("")) {
                    student_name_et.setError("Required");
                }
                if (!student_roll.equals("") && !student_name.equals("") && !searchRoll(studentItems, student_roll)) {
                    long studentId = myDb.addStudent(classId, student_roll, student_name);
                    studentItems.add(new StudentItem(studentId, Integer.parseInt(student_roll), student_name));
                    Collections.sort(studentItems, (StudentItem s1, StudentItem s2) -> s1.getRoll() - s2.getRoll());
                    studentAdapter.notifyDataSetChanged();
                    updateStatistics();
                    student_roll_et.setText(Integer.toString(Integer.parseInt(student_roll) + 1));
                    student_name_et.setText("");
                }
            });
        }
    }

    private void changeDate() {
        calendar.show(getSupportFragmentManager(),"");
        calendar.setOnCalendarOkClickListener((year, month, day) -> {
            calendar.setDate(year,month,day);
            TextView sub_title = findViewById(R.id.subtitle_toolbar_tv);
            if (calendar.getDate().equals(calendar.getDateToday())){
                sub_title.setText("Today");
            } else {
                sub_title.setText(calendar.getDate());
            }
            loadStudents(calendar.getDate());
        });
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case 0:
                updateStudent(item.getGroupId());
                break;
            case 1:
                deleteStudent(item.getGroupId());
                break;
        }
        return super.onContextItemSelected(item);
    }

    private void updateStudent(int groupId) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        View v = LayoutInflater.from(this).inflate(R.layout.my_dialog,null);

        TextView title = v.findViewById(R.id.my_dialog_title_tv);
        EditText student_roll_et = v.findViewById(R.id.my_dialog_et_1);
        EditText student_name_et = v.findViewById(R.id.my_dialog_et_2);
        Button cancel = v.findViewById(R.id.cancel);
        Button ok = v.findViewById(R.id.ok);

        title.setText("Edit Student");
        student_roll_et.setInputType(InputType.TYPE_CLASS_NUMBER);
        student_roll_et.setHint("Enter Roll Number");
        student_name_et.setHint("Enter Student Name");
        student_roll_et.setText(Integer.toString(studentItems.get(groupId).getRoll()));
        student_name_et.setText(studentItems.get(groupId).getName());

        student_roll_et.setEnabled(false); // roll cannot be changed
        student_roll_et.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.my_gray)); // changeing roll background

        student_name_et.requestFocus(); // request focus to student name

        dialog.setView(v);
        AlertDialog alertDialog = dialog.create();
        alertDialog.show();

        cancel.setOnClickListener(view -> alertDialog.dismiss());
        ok.setOnClickListener(view -> {

            String student_id = Long.toString(studentItems.get(groupId).getStudent_id());
            String student_name = student_name_et.getText().toString();

            if(student_name.equals("")){
                student_name_et.setError("Required");
            } else {

                myDb.editStudent(student_id,student_name);
                studentItems.get(groupId).setName(student_name);
                studentAdapter.notifyItemChanged(groupId);

                alertDialog.dismiss();
            }
        });
    }

    private void deleteStudent(int groupId) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        String student_roll = Integer.toString(studentItems.get(groupId).getRoll());
        String student_name = studentItems.get(groupId).getName();
        builder.setTitle("Do you want to delete "+student_name+" ( Roll: "+student_roll+" ) "+"?");
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                myDb.deleteStudent(Long.toString(studentItems.get(groupId).getStudent_id()));
                studentItems.remove(groupId);
                studentAdapter.notifyItemRemoved(groupId);
                updateStatistics();
                dialogInterface.dismiss();
            }
        });
        builder.create().show();
    }

    private void updateStatistics() {
        int total = studentItems.size(), present = 0, absent = 0, remaining = 0;
        for(StudentItem item : studentItems){
            if (item.getStatus().equals("A")) absent++;
            else if (item.getStatus().equals("P")) present++;
        }
        total_tv.setText("Total: "+String.valueOf(total));
        present_tv.setText("Present: "+String.valueOf(present));
        absent_tv.setText("Absent: "+String.valueOf(absent));
        remaining_tv.setText("Remaining: "+String.valueOf(total-present-absent));
    }

    private boolean searchRoll(ArrayList<StudentItem> studentItems, String student_roll) {
        int roll = Integer.parseInt(student_roll);
        int l = 0, r = studentItems.size() - 1;
        while (l <= r) {
            int m = l + (r - l) / 2;
            if (studentItems.get(m).getRoll() == roll) return true;
            if (studentItems.get(m).getRoll() < roll) l = m + 1;
            else r = m - 1;
        }
        return false;
    }
    private void afterPermission() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/comma-separated-values");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivityForResult(intent,OPEN_CSV);
    }

    private boolean checkPermission() {
        if (SDK_INT >= Build.VERSION_CODES.R) {
            return Environment.isExternalStorageManager();
        } else {
            int result = ContextCompat.checkSelfPermission(this, READ_EXTERNAL_STORAGE);
            int result1 = ContextCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE);
            return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED;
        }
    }
    private void requestPermission() {
        if (SDK_INT >= Build.VERSION_CODES.R) {
            // Android 10 and above
            try {
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                intent.setData(Uri.parse(String.format("package:%s",getApplicationContext().getPackageName())));
                startActivityForResult(intent, TAKE_USER_PERMISSION);
            } catch (Exception e) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                startActivityForResult(intent, TAKE_USER_PERMISSION);
            }
        } else {
            //below android 11
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE}, PackageManager.PERMISSION_GRANTED);
//            ActivityCompat.requestPermissions(this, new String[]{WRITE_EXTERNAL_STORAGE}, TAKE_USER_PERMISSION);
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        Uri pathHolder = null;
        if (data!=null) pathHolder = data.getData();
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == TAKE_USER_PERMISSION) {
            if (SDK_INT >= Build.VERSION_CODES.R) {
                if (Environment.isExternalStorageManager()) {
                    // perform action when allow permission success
                    readCSV();
                } else {
                    Toast.makeText(this, "Please allow permission for storage access!", Toast.LENGTH_SHORT).show();
                }
            }
        } else if (resultCode == RESULT_OK && requestCode == OPEN_CSV) {
            try {
                InputStream inputStream = getContentResolver().openInputStream(pathHolder);
                BufferedReader r = new BufferedReader(new InputStreamReader(inputStream));
                CSVReader reader = new CSVReader(r);
                List<String[]> names = reader.readAll();

                int student_roll;
                try {
                    student_roll = studentItems.get(studentItems.size() - 1).getRoll();
                } catch (Exception e){
                    student_roll = 0;
                }
                for (String[] row : names) {
                    for (String student_name : row) {
                        long studentId = myDb.addStudent(classId, String.valueOf(++student_roll), student_name);
                        studentItems.add(new StudentItem(studentId, student_roll, student_name));
                        Collections.sort(studentItems, (StudentItem s1, StudentItem s2) -> s1.getRoll() - s2.getRoll());
                        studentAdapter.notifyDataSetChanged();
                        updateStatistics();
                    }
                    if (studentItems.size()>=150) {
                        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                        builder.setCancelable(false);
                        builder.setTitle("You cannot include more than 150 students in a class!");
                        builder.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });
                        builder.create().show();
                        break;
                    }
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(this, "FileNotFoundException", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "IOException", Toast.LENGTH_SHORT).show();
            }
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case TAKE_USER_PERMISSION:
                if (grantResults.length > 0) {
                    boolean READ_EXTERNAL_STORAGE = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean WRITE_EXTERNAL_STORAGE = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                    if (READ_EXTERNAL_STORAGE && WRITE_EXTERNAL_STORAGE) {
                        // perform action when allow permission success
                        readCSV();
                    } else {
                        Toast.makeText(this, "Please allow permission for storage access!", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }
    }
}