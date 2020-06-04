package com.example.attendance;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

import static com.example.attendance.FirstActivity.c;
import static com.example.attendance.FirstActivity.mydb;

public class MainActivity extends AppCompatActivity {

    int max;
    String dept,year;

    private Button pr_btn,ab_btn;
    private TextView roll_tv,total_tv,stream_tv,date_tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        max = getIntent().getExtras().getInt("Total");
        dept = getIntent().getExtras().getString("Dept");
        year = getIntent().getExtras().getString("Year");

        date_tv = findViewById(R.id.TextView_date);
        total_tv = findViewById(R.id.TextView_total);
        stream_tv = findViewById(R.id.TextView_stream);
        roll_tv = findViewById(R.id.TextView_roll);
        pr_btn = findViewById(R.id.Button_present);
        ab_btn = findViewById(R.id.Button_apsend);

        date_tv.setText(Date());
        stream_tv.setText(dept+" "+year.substring(0,3)+"\n"+year.substring(4).replace("_"," "));
        total_tv.setText("Total: "+max);
        roll_tv.setText(Integer.toString(c));

        pr_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(c<=max){
                    //code here for present
                    mydb.presentData(dept+"_"+year,Integer.toString(c));
                    c++;
                    if(c==max+1){
                        Intent i =new Intent(MainActivity.this,ViewActivity.class);
                        i.putExtra("Year",year);
                        i.putExtra("Dept",dept);
                        i.putExtra("Total",max);
                        startActivity(i);
                        finish();
                    }
                    else {
                        roll_tv.setText(Integer.toString(c));
                    }
                }
            }
        });

        ab_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(c<=max){
                    mydb.absentData(dept+"_"+year,Integer.toString(c));
                    c++;
                    if(c==max+1){
                        Intent i =new Intent(MainActivity.this,ViewActivity.class);
                        i.putExtra("Year",year);
                        i.putExtra("Dept",dept);
                        i.putExtra("Total",max);
                        startActivity(i);
                        finish();
                    }
                    else
                        roll_tv.setText(Integer.toString(c));
                }
            }
        });
    }


    public static String Date(){
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String curdate = sdf.format(new Date());
        return curdate;
    }
}
