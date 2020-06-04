package com.example.attendance;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import static com.example.attendance.FirstActivity.mydb;
import static com.example.attendance.MainActivity.Date;

public class UpdateActivity extends AppCompatActivity {

    TextView date,stream,total;
    Button pre,abs;
    EditText roll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);

        date = findViewById(R.id.TextView_date_u);
        stream = findViewById(R.id.TextView_stream_u);
        total = findViewById(R.id.TextView_total_u);
        pre = findViewById(R.id.Button_present_u);
        abs = findViewById(R.id.Button_apsend_u);
        roll = findViewById(R.id.EditText_roll_u);

        date.setText(Date());
        stream.setText(getIntent().getExtras().getString("Dept")+" "+getIntent().getExtras().getString("Year").substring(0,3)+"\n"+(getIntent().getExtras().getString("Year").substring(4)).replace("_"," "));
        total.setText("Total: "+Integer.toString(getIntent().getExtras().getInt("Total")));

        pre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(roll.getText().toString().isEmpty())
                    roll.setError("Required");
                else if(Integer.parseInt(roll.getText().toString()) > getIntent().getExtras().getInt("Total"))
                    roll.setError("Must be less than "+getIntent().getExtras().getInt("Total"));
                else{
                    boolean r = mydb.presentDataUpdate(getIntent().getExtras().getString("Dept")+"_"+getIntent().getExtras().getString("Year"),roll.getText().toString());
                    if(r)
                        Toast.makeText(UpdateActivity.this,"Roll "+roll.getText().toString()+" is Present now",Toast.LENGTH_SHORT).show();
                    else
                        Toast.makeText(UpdateActivity.this,"Error",Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(UpdateActivity.this,FirstActivity.class));
                    finish();
                }
            }
        });

        abs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(roll.getText().toString().isEmpty())
                    roll.setError("Required");
                else if(Integer.parseInt(roll.getText().toString()) > getIntent().getExtras().getInt("Total"))
                    roll.setError("Must be less than "+getIntent().getExtras().getInt("Total"));
                else{
                    boolean r = mydb.absentDataUpdate(getIntent().getExtras().getString("Dept")+"_"+getIntent().getExtras().getString("Year"),roll.getText().toString());
                    if(r)
                        Toast.makeText(UpdateActivity.this,"Roll "+roll.getText().toString()+" is Absent now",Toast.LENGTH_SHORT).show();
                    else
                        Toast.makeText(UpdateActivity.this,"Error",Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(UpdateActivity.this,FirstActivity.class));
                    finish();
                }
            }
        });

    }
}
