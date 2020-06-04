package com.example.attendance;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintManager;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.ajts.androidmads.library.SQLiteToExcel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import static com.example.attendance.MainActivity.Date;
import static com.example.attendance.ViewActivity.giveHTML;

public class FirstActivity extends AppCompatActivity {

    SetupHelper db_sh;
    FloatingActionButton add_btn;
    ListView lv;

    static DataBaseHelper mydb;
    boolean re;
    static int c;
    static Cursor cr;
    static AdapterView.AdapterContextMenuInfo info;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_about,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.menu_about:
                // Intent to AboutActivity
                startActivity(new Intent(FirstActivity.this,AboutActivity.class));
                break;
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);

        mydb = new DataBaseHelper(FirstActivity.this, 1);

        add_btn = findViewById(R.id.floating_add_button);
        lv = findViewById(R.id.listView);


        add_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(FirstActivity.this, SetupActivity.class));
                finish();
            }
        });


        registerForContextMenu(lv);
        lv.deferNotifyDataSetChanged();
        lv.refreshDrawableState();
        lv.invalidateViews();

        int i = 0;
        db_sh = new SetupHelper(this);
        ArrayList<String> arrayList = new ArrayList<>();
        Cursor data = db_sh.getListContent();
        if (data.getCount() == 0)
            Toast.makeText(FirstActivity.this, "Empty", Toast.LENGTH_SHORT).show();
        else {
            while (data.moveToNext()) {
                String s = Integer.toString(++i) + ") " + data.getString(1) + " " + data.getString(2) + " (Subject - " + data.getString(3).replace("_"," ") + ")";
                arrayList.add(s);
                ListAdapter listAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, arrayList);
                lv.setAdapter(listAdapter);
            }
        }
        lv.refreshDrawableState();


        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int p, long id) {
                Cursor data = db_sh.getListContent();
                data.moveToPosition(p);
                final String dept = data.getString(1);
                final String year = data.getString(2);
                final String subj = data.getString(3);
                final int total = Integer.parseInt(data.getString(4));


                /********************************************************************/
                boolean checker;
                try {
                    checker = mydb.checkAgain(dept + "_" + year + "_" + subj);
                }catch (Exception e){
                    checker = false;
                }
                if(checker){
                    AlertDialog.Builder builder = new AlertDialog.Builder(FirstActivity.this);
                    builder.setTitle("⚠ Alert!");
                    builder.setMessage("Do you want to take Attendance again ?");
                    builder.setCancelable(false);
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            try {
                                mydb.createUserTable(dept + "_" + year + "_" + subj, total);
                                Intent i = new Intent(FirstActivity.this, MainActivity.class);
                                i.putExtra("Dept", dept);
                                i.putExtra("Year", year+"_"+subj);
                                i.putExtra("Total", total);
                                c = 1;
                                startActivity(i);
                            } catch (Exception e) {
                                Intent i = new Intent(FirstActivity.this, MainActivity.class);
                                i.putExtra("Dept", dept);
                                i.putExtra("Year", year+"_"+subj);
                                i.putExtra("Total", total);
                                c = 1;
                                startActivity(i);
                            }

                        }
                    });
                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) { dialog.cancel(); }
                    });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }else {
                    try {
                        mydb.createUserTable(dept + "_" + year + "_" + subj, total);
                        Intent i = new Intent(FirstActivity.this, MainActivity.class);
                        i.putExtra("Dept", dept);
                        i.putExtra("Year", year+"_"+subj);
                        i.putExtra("Total", total);
                        c = 1;
                        startActivity(i);
                    } catch (Exception e) {
                        Intent i = new Intent(FirstActivity.this, MainActivity.class);
                        i.putExtra("Dept", dept);
                        i.putExtra("Year", year+"_"+subj);
                        i.putExtra("Total", total);
                        c = 1;
                        startActivity(i);
                    }
                }
                /**********************************************************************/


            }
        });
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.menu_main, menu);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_view:
                AdapterView.AdapterContextMenuInfo info_v = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
                Cursor c_v = db_sh.getListContent();
                c_v.moveToPosition(info_v.position);
                Intent i = new Intent(FirstActivity.this, ViewActivity.class);
                i.putExtra("Dept",c_v.getString(1));
                i.putExtra("Year",c_v.getString(2)+"_"+c_v.getString(3));
                i.putExtra("Total",c_v.getInt(4));
                startActivity(i);
                break;
            case R.id.action_update:
                AdapterView.AdapterContextMenuInfo info_u = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
                Cursor c_u = db_sh.getListContent();
                c_u.moveToPosition(info_u.position);
                Intent j = new Intent(FirstActivity.this, UpdateActivity.class);
                j.putExtra("Dept",c_u.getString(1));
                j.putExtra("Year",c_u.getString(2)+"_"+c_u.getString(3));
                j.putExtra("Total",c_u.getInt(4));
                startActivity(j);
                finish();
                break;
            case R.id.action_export_excel:
                Toast.makeText(this, "Exporting to EXCEL", Toast.LENGTH_SHORT).show();
                AdapterView.AdapterContextMenuInfo info_x = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
                Cursor c_x = db_sh.getListContent();
                c_x.moveToPosition(info_x.position);

                Cursor k = mydb.getAllData(c_x.getString(1)+"_"+c_x.getString(2)+"_"+c_x.getString(3));
                k.moveToFirst(); String beg = k.getString(0);
                k.moveToLast(); String end = k.getString(0);

                c_x.moveToPosition(info_x.position);
                if(isStoragePermissionGranted()) {
                    boolean r = ExportToEXCEL(c_x.getString(1),c_x.getString(2)+"_"+c_x.getString(3),beg,end);
                    if (r)
                        Toast.makeText(FirstActivity.this, "Exported to INTERNAL/Attendance Data/" + c_x.getString(1) + "_" + c_x.getString(2) +"_"+c_x.getString(3) + "("+ dateModifier(beg) +" - "+ dateModifier(end) +").xls", Toast.LENGTH_LONG).show();
                    else
                        Toast.makeText(FirstActivity.this, "Error", Toast.LENGTH_SHORT).show();
                }
                else
                    Toast.makeText(FirstActivity.this, "Permission Granted\nNow try again to Export.", Toast.LENGTH_LONG).show();
                break;
            case R.id.action_delete:
                info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
                cr = db_sh.getListContent();
                cr.moveToPosition(info.position);

                AlertDialog.Builder builder = new AlertDialog.Builder(FirstActivity.this);
                builder.setTitle("⚠ Warning!");
                builder.setMessage("Do you want to Delete " + cr.getString(1) + " " + cr.getString(2)+" "+cr.getString(3).replace("_"," ") + "?");
                builder.setCancelable(false);
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        cr.moveToPosition(info.position);
                        try {
                            mydb.deleteAllData(cr.getString(1) + "_" + cr.getString(2)+"_"+cr.getString(3));
                            db_sh.delData(cr.getString(0));
                            startActivity(new Intent(FirstActivity.this, FirstActivity.class));
                            finish();
                            Toast.makeText(FirstActivity.this, "Deleted", Toast.LENGTH_SHORT).show();
                        }catch (Exception e){}

                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) { dialog.cancel(); }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
                break;
            default:
                return super.onContextItemSelected(item);
        }
        return super.onContextItemSelected(item);
    }

    public boolean ExportToEXCEL(String dept,String year,String beg,String end){
        String directory_path = Environment.getExternalStorageDirectory().getPath() + "/Attendance Data/";
        File file = new File(directory_path);
        if (!file.exists())
            file.mkdirs();

        SQLiteToExcel sqliteToExcel = new SQLiteToExcel(getApplicationContext(), "attendance.db", directory_path);
        sqliteToExcel.exportSingleTable(dept+"_"+year, dept+"_"+year+"("+ dateModifier(beg) +" - "+ dateModifier(end) +").xls", new SQLiteToExcel.ExportListener() {
            @Override
            public void onStart() { re = true; }
            @Override
            public void onCompleted(String filePath) { re = true; }
            @Override
            public void onError(Exception e) { re = false; }
        });
        return  re;
    }

    public String dateModifier(String d){
        return d.replace("/",".");
    }

    public  boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            return true;
        }
    }

//    public void deleteTable()
//    {
//        AlertDialog.Builder builder = new AlertDialog.Builder(FirstActivity.this);
//        builder.setMessage("Do you want to Delete ?");
//        builder.setTitle("Alert !");
//        builder.setCancelable(false);
//        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                c.moveToPosition(info.position);
//                                try {
//                                    mydb.deleteAllData(c.getString(1) + "_" + c.getString(2)+"_"+c.getString(3));
//                                }catch (Exception e){}
//                                db_sh.delData(c.getString(0));
//                                startActivity(new Intent(FirstActivity.this, FirstActivity.class));
//                                finish();
//                                Toast.makeText(this, "Deleted", Toast.LENGTH_SHORT).show();
//                            }
//                        });
//        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) { dialog.cancel(); }
//                        });
//        AlertDialog alertDialog = builder.create();
//        alertDialog.show();
//    }
}
