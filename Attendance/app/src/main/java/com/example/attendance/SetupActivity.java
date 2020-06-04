package com.example.attendance;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class SetupActivity extends AppCompatActivity {

    Spinner dept_sp,year_sp;
    EditText subject,total;
    Button setup;

    SetupHelper db_sh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        dept_sp = findViewById(R.id.spinner_dept);
        year_sp = findViewById(R.id.spinner_yr);
        subject = findViewById(R.id.editText_subject);
        total = findViewById(R.id.editText_total);
        setup = findViewById(R.id.Button_create_setup);

        db_sh = new SetupHelper(this);

        String[] dept = {"CSE","ECE","ME","EE","CE"};
        String[] year = {"1ST","2ND","3RD","4TH"};
        ArrayAdapter<String> adapter_dept = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,dept);
        ArrayAdapter<String> adapter_year = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,year);

        dept_sp.setAdapter(adapter_dept);
        year_sp.setAdapter(adapter_year);

        setup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String d = dept_sp.getSelectedItem().toString();
                String y = year_sp.getSelectedItem().toString();
                String s = subject.getText().toString();
                s = s.replace(" ","_");
                String t = total.getText().toString();

                if(t.isEmpty()||s.isEmpty()) {
                    if (t.isEmpty())
                        total.setError("Required");
                    if (s.isEmpty())
                        subject.setError("Required");
                }
                else {
                    boolean r = db_sh.addData(d,y,s,Integer.parseInt(t));
                    if(r)
                        Toast.makeText(SetupActivity.this,  "Data Inserted", Toast.LENGTH_SHORT).show();
                    else
                        Toast.makeText(SetupActivity.this, "Data Not Inserted", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(SetupActivity.this, FirstActivity.class);
                    startActivity(i);
                    finish();
                }
            }
        });
    }

}