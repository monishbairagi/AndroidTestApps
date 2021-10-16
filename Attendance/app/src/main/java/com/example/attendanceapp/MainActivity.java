package com.example.attendanceapp;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.os.Build.VERSION.SDK_INT;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private FloatingActionButton add_sub;
    private RecyclerView classes_rv;
    private ArrayList<ClassItem> classItems;
    private RVAdapter rvAdapter;
    private Toolbar toolbar;
    private DBHelper myDb;
    private LinearLayout downloadActivity;

    private static final int TAKE_USER_PERMISSION = 102;

    @Override
    protected void onResume() {
        super.onResume();
        classItems.clear();
        classItems.addAll(myDb.getAllClass());
        rvAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setToolbar();

        classItems = new ArrayList<>();
        myDb = new DBHelper(this);

        downloadActivity = findViewById(R.id.download_activity);
        downloadActivity.setOnClickListener(view -> {
            this.startActivity(new Intent(this, DownloadActivity.class));
            this.overridePendingTransition(0, 0);
            finish();
        });

        add_sub = findViewById(R.id.fab_add);
        add_sub.setOnClickListener(view -> addClass());

        classes_rv = findViewById(R.id.classes_rv);
        classes_rv.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        classes_rv.setLayoutManager(layoutManager);

        rvAdapter = new RVAdapter(this, classItems);
        classes_rv.setAdapter(rvAdapter);

        rvAdapter.setOnItemClickListener(position -> {
            Intent i = new Intent(this,StudentActivity.class);
            i.putExtra("classId",Long.toString(classItems.get(position).getClass_id()));
            i.putExtra("subjectName",classItems.get(position).getSubject_name());
            i.putExtra("className",classItems.get(position).getClass_name());
            startActivity(i);
        });
    }

    private void setToolbar() {
        toolbar = findViewById(R.id.toolbar);
        TextView title = findViewById(R.id.title_toolbar_tv);
        TextView sub_title = findViewById(R.id.subtitle_toolbar_tv);
        ImageButton arrow_back = findViewById(R.id.arrow_back_btn);
        ImageButton save = findViewById(R.id.save_btn);

        title.setText("Home");
        sub_title.setVisibility(View.GONE);
        arrow_back.setVisibility(View.GONE);
        save.setVisibility(View.GONE);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case 0:
                updateClass(item.getGroupId());
                break;
            case 1:
                deleteClass(item.getGroupId());
                break;
        }
        return super.onContextItemSelected(item);
    }

    private void addClass() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        View v = LayoutInflater.from(this).inflate(R.layout.my_dialog,null);

        TextView title = v.findViewById(R.id.my_dialog_title_tv);
        EditText subject_name_et = v.findViewById(R.id.my_dialog_et_1);
        EditText class_name_et = v.findViewById(R.id.my_dialog_et_2);
        Button cancel = v.findViewById(R.id.cancel);
        Button ok = v.findViewById(R.id.ok);

        title.setText("Add New Class");
        subject_name_et.setHint("Enter Subject Name");
        class_name_et.setHint("Enter Class Name");

        dialog.setView(v);
        AlertDialog alertDialog = dialog.create();
        alertDialog.show();

        cancel.setOnClickListener(view -> alertDialog.dismiss());
        ok.setOnClickListener(view -> {

            String subject_name = subject_name_et.getText().toString();
            String class_name = class_name_et.getText().toString();

            if(subject_name.equals("")){
                subject_name_et.setError("Required");
            }if(class_name.equals("")){
                class_name_et.setError("Required");
            }if(!subject_name.equals("") && !class_name.equals("")) {
                long class_id = myDb.addClass(subject_name, class_name);
                classItems.add(new ClassItem(class_id, subject_name, class_name));

                rvAdapter.notifyDataSetChanged();
                alertDialog.dismiss();
            }
        });
    }

    private void deleteClass(int groupId) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        String subject = classItems.get(groupId).getSubject_name();
        builder.setTitle("Do you want to delete "+subject+" class?");
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                myDb.deleteClass(Long.toString(classItems.get(groupId).getClass_id()));
                classItems.remove(groupId);
                rvAdapter.notifyItemRemoved(groupId);
                dialogInterface.dismiss();
            }
        });
        builder.create().show();
    }

    private void updateClass(int groupId) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        View v = LayoutInflater.from(this).inflate(R.layout.my_dialog,null);

        EditText subject_name_et = v.findViewById(R.id.my_dialog_et_1);
        EditText class_name_et = v.findViewById(R.id.my_dialog_et_2);
        TextView title = v.findViewById(R.id.my_dialog_title_tv);
        Button cancel = v.findViewById(R.id.cancel);
        Button ok = v.findViewById(R.id.ok);

        subject_name_et.setText(classItems.get(groupId).getSubject_name());
        class_name_et.setText(classItems.get(groupId).getClass_name());

        title.setText("Edit Class");
        subject_name_et.setHint("Enter Subject Name");
        class_name_et.setHint("Enter Class Name");

        dialog.setView(v);
        AlertDialog alertDialog = dialog.create();
        alertDialog.show();

        cancel.setOnClickListener(view -> alertDialog.dismiss());
        ok.setOnClickListener(view -> {

            String cid = Long.toString(classItems.get(groupId).getClass_id());
            String subject_name = subject_name_et.getText().toString();
            String class_name = class_name_et.getText().toString();

            if(subject_name.equals("")){
                subject_name_et.setError("Required");
            }if(class_name.equals("")){
                class_name_et.setError("Required");
            }if(!subject_name.equals("") && !class_name.equals("")) {
                myDb.editClass(cid, subject_name, class_name);

                classItems.get(groupId).setSubject_name(subject_name);
                classItems.get(groupId).setClass_name(class_name);

                rvAdapter.notifyItemChanged(groupId);

                alertDialog.dismiss();
            }
        });
    }
}