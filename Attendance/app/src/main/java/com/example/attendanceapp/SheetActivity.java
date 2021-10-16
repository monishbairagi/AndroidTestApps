package com.example.attendanceapp;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.os.Build.VERSION.SDK_INT;
import static com.example.attendanceapp.DateYearConversion.getDayInMonth;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.Settings;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;

public class SheetActivity extends AppCompatActivity{
    private String[] sid_array,roll_array,name_array;
    private String month_year, class_id, class_name,subject_name;

    private DBHelper myDb;

    private File filePath;
    private HSSFWorkbook hssfWorkbook;

    private static final int TAKE_USER_PERMISSION = 102;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sheet);

        myDb = new DBHelper(this);
        hssfWorkbook = new HSSFWorkbook();
        loadIntentInputs(); // this should be at top
        setToolbar();

        new MyWorker().execute();
    }

    private class MyWorker extends AsyncTask {
        private ProgressDialog progressDialog;

        @ Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(SheetActivity.this) ;
            progressDialog.setCancelable(false) ;
            progressDialog.setTitle("Retrieving data"); ;
            progressDialog.setMessage("Please wait...\nIt could take some time."); ;
            progressDialog.setIndeterminate(true);
            progressDialog.show() ;
        }

        @Override
        protected Object doInBackground(Object[] objects) {
            showAttendanceTable();
            return null;
        }

        @ Override
        protected void onPostExecute(Object object) {
            if(progressDialog != null && progressDialog.isShowing()){
                progressDialog.dismiss() ;
            }
        }
    }

    private void loadIntentInputs() {
        Intent intent = getIntent();
        month_year = intent.getStringExtra("month_year");
        class_id = intent.getStringExtra("class_id");
        class_name = intent.getStringExtra("class_name");
        subject_name = intent.getStringExtra("subject_name");
        sid_array = intent.getStringArrayExtra("sid_array");
        roll_array = intent.getStringArrayExtra("roll_array");
        name_array = intent.getStringArrayExtra("name_array");
    }

    private void showAttendanceTable() {
        TableLayout view = new TableLayout(this);
        int day_in_month = getDayInMonth(month_year);
        int row_size = sid_array.length+1;
        TableRow[] rows = new TableRow[row_size];
        TextView[] rolls_tv = new TextView[row_size];
        TextView[] names_tv = new TextView[row_size];
        TextView[][] status_tv = new TextView[row_size][day_in_month+1];
        for (int i = 0; i < row_size; i++) {
            rolls_tv[i] = new TextView(this);
            names_tv[i] = new TextView(this);
            for (int j = 1; j <= day_in_month; j++) {
                status_tv[i][j] = new TextView(this);
            }
        }

        // for excel file
        HSSFSheet hssfSheet = null;
        try {
            hssfSheet = hssfWorkbook.createSheet(month_year);
        } catch (IllegalArgumentException e){
            e.printStackTrace();
        }
        HSSFRow hssfRow;

        // setting 1st row
        rolls_tv[0].setText("Roll");
        names_tv[0].setText("Name");

        // setting excel file
        hssfRow = hssfSheet.createRow(0);
        hssfRow.createCell(0).setCellValue("Roll");
        hssfRow.createCell(1).setCellValue("Name");

        rolls_tv[0].setTextSize(1,22);
        names_tv[0].setTextSize(1,22);

        rolls_tv[0].setTypeface(rolls_tv[0].getTypeface(), Typeface.BOLD);
        names_tv[0].setTypeface(names_tv[0].getTypeface(), Typeface.BOLD);
        rolls_tv[0].setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        names_tv[0].setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        rolls_tv[0].setTextColor(Color.parseColor("#FF000000"));
        names_tv[0].setTextColor(Color.parseColor("#FF000000"));

        for (int i = 1; i <=day_in_month ; i++) {
            String date = String.valueOf(i);
            if(i<10) date = "0"+date;
            status_tv[0][i].setText(date);

            // setting excel file
            hssfRow.createCell(i+1).setCellValue(date);

            status_tv[0][i].setTextSize(1,22);
            status_tv[0][i].setTypeface(status_tv[0][i].getTypeface(), Typeface.BOLD);
            status_tv[0][i].setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            status_tv[0][i].setTextColor(Color.parseColor("#FF000000"));
        }

        // setting rows after 1st row
        for (int i = 1; i < row_size ; i++) {
            rolls_tv[i].setText(roll_array[i-1]);
            names_tv[i].setText(name_array[i-1]);

            // setting excel file
            hssfRow = hssfSheet.createRow(i);
            hssfRow.createCell(0).setCellValue(roll_array[i-1]);
            hssfRow.createCell(1).setCellValue(name_array[i-1]);

            rolls_tv[i].setTextSize(1,20);
            names_tv[i].setTextSize(1,20);
            rolls_tv[i].setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            names_tv[i].setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            rolls_tv[i].setTextColor(Color.parseColor("#FF000000"));
            names_tv[i].setTextColor(Color.parseColor("#FF000000"));

            int count=0;
            for (int j = 1; j <=day_in_month ; j++) {
                String day = String.valueOf(j);
                String month = month_year.substring(0,3);
                String year = month_year.substring(4,8);
                String status = myDb.getStatus(sid_array[i-1],day+" "+month+" "+year);
                if (status!=null && status.equals("A")) {
                    status_tv[i][j].setText(status);
                    status_tv[i][j].setBackgroundColor(Color.parseColor("#EF9A9A"));

                    // setting excel file
                    hssfRow.createCell(j+1).setCellValue(status);

                } else if (status!=null && status.equals("P")) {
                    status_tv[i][j].setText(String.valueOf(++count));
                    status_tv[i][j].setBackgroundColor(Color.parseColor("#A5D6A7"));

                    // setting excel file
                    hssfRow.createCell(j+1).setCellValue(String.valueOf(count));
                }

                status_tv[i][j].setTextSize(1,20);
                status_tv[i][j].setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                status_tv[i][j].setTextColor(Color.parseColor("#FF000000"));
            }
        }
        for (int i = 0; i < row_size; i++) {
            rows[i] = new TableRow(this);

            rolls_tv[i].setPadding(20,12,20,12);
            names_tv[i].setPadding(20,12,20,12);

            if (i==0) {
                rows[i].setBackgroundColor(Color.parseColor("#FFD400"));
            } else if(i%2==0) {
                rows[i].setBackgroundColor(Color.parseColor("#EEEEEE"));
            } else {
                rows[i].setBackgroundColor(Color.parseColor("#E4E4E4"));
            }
            rows[i].addView(rolls_tv[i]);
            rows[i].addView(names_tv[i]);

            for (int j = 1; j <=day_in_month ; j++) {
                status_tv[i][j].setPadding(20,12,20,12);
                rows[i].addView(status_tv[i][j]);
            }
            view.addView(rows[i]);
        }
        runOnUiThread(new Thread(new Runnable() {
            @Override
            public void run() {
                view.setShowDividers(TableLayout.SHOW_DIVIDER_MIDDLE);
                TableLayout tableLayout = findViewById(R.id.table_layout);
                tableLayout.addView(view);
            }
        }));
    }

    private void setToolbar() {
        TextView title = findViewById(R.id.title_toolbar_tv);
        TextView sub_title = findViewById(R.id.subtitle_toolbar_tv);
        ImageButton arrow_back = findViewById(R.id.arrow_back_btn);
        ImageButton download = findViewById(R.id.save_btn);
        download.setImageDrawable(getResources().getDrawable(R.drawable.ic_download));
        download.setVisibility(View.VISIBLE);

        title.setSelected(true);
        sub_title.setSelected(true);
        title.setText(subject_name+" ("+class_name+")");
        sub_title.setText(month_year+" Attendance Sheet");

        arrow_back.setOnClickListener(view -> onBackPressed());
        download.setOnClickListener(view -> downloadExcel());
    }

    private void downloadExcel() {
        if(!checkPermission()){
            requestPermission();
        } else {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setCancelable(false);
            builder.setTitle("Do you want to save data in excel format?");
            builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    String folderPath = Environment.getExternalStorageDirectory() + File.separator + "Attendance App Data";
                    File folder = new File(folderPath);
                    boolean success = true;
                    if (!folder.exists()) {
                        success = folder.mkdirs();
                    } if (success) {
                        String fileName = month_year.replace(" ", "_") + "_" + class_name.replace(" ", "_") + "_" + subject_name.replace(" ", "_") + ".xls";
                        filePath = new File(folderPath + "/" + fileName);
                        try {
                            if (!filePath.exists()) filePath.createNewFile();
                            FileOutputStream fileOutputStream = new FileOutputStream(filePath);
                            hssfWorkbook.write(fileOutputStream);
                            if (fileOutputStream != null) {
                                fileOutputStream.flush();
                                fileOutputStream.close();
                            }
                            Toast.makeText(SheetActivity.this, "Saved: 'Internal/Attendance App Data/"+fileName+"'", Toast.LENGTH_LONG).show();
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                            Toast.makeText(SheetActivity.this, "FileNotFoundException", Toast.LENGTH_SHORT).show();
                        } catch (IOException e) {
                            e.printStackTrace();
                            Toast.makeText(SheetActivity.this, "IOException", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(SheetActivity.this, "Error: Folder cannot be saved", Toast.LENGTH_SHORT).show();
                    }
                    dialogInterface.dismiss();
                }
            });
            builder.create().show();
        }
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
}