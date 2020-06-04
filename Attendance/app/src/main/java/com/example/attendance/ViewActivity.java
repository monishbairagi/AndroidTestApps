package com.example.attendance;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintManager;
import android.webkit.WebView;
import android.widget.Toast;

import static com.example.attendance.FirstActivity.mydb;

public class ViewActivity extends AppCompatActivity {

    String dept;
    String year;
    int total;

    Cursor c;

    WebView wv;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);

        wv = findViewById(R.id.webview);

        dept = getIntent().getExtras().getString("Dept");
        year = getIntent().getExtras().getString("Year");
        total = getIntent().getExtras().getInt("Total");

        try {
            c = mydb.getAllData(dept + "_" + year);
            wv.loadDataWithBaseURL(null, giveHTML(dept, year, total), "text/html", "utf-8", null);
            c.close();
        } catch (Exception e) {
            Toast.makeText(this, "No Data Exists", Toast.LENGTH_SHORT).show();
        }



    }

    public static String giveHTML(String dept,String year,int max){
        Cursor c = mydb.getAllData(dept+"_"+year);

        return  "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "<style>\n" +
                "table {\n" +
                "  font-family: arial, sans-serif;\n" +
                "  border-collapse: collapse;\n" +
                "  width: 50%;\n" +
                "}\n" +
                "\n" +
                "td, th {\n" +
                "  border: 1px solid #dddddd;\n" +
                "  text-align: center;\n" +
                "  padding: 8px;\n" +
                "}\n" +
                "\n" +
                "tr:nth-child(even) {\n" +
                "  background-color: #dddddd;\n" +
                "}\n" +
                "</style>\n" +
                "</head>\n" +
                "<body>\n" +
                "\n" +
                "<h2>"+(dept+"_"+year).replace("_"," ")+"</h2>\n" +
                "\n" +
                "\n" +
                "<table>\n" +
                "  <tr>\n" +
                "    <th>Roll\\Date</th>\n"+GiveAllDates(c) +
                "  </tr>\n" +
                GiveAllRoll(c,max) +
                "</table>\n" +
                "\n" +
                "</body>\n" +
                "</html>\n";
    }

    static String GiveAllDates(Cursor c){
        String d="";
        if(c!=null && c.moveToFirst()){
            do{
                d = d + "<th>"+c.getString(0)+"</th>";
            }while (c.moveToNext());
        }
        return d;
    }

    static String GiveRoll(Cursor c,int roll){
        String d="";
        if(c!=null && c.moveToFirst()){
            c.moveToFirst();
            if(c.getColumnName(roll).equals("Total"))
                d = d + "<th>"+c.getColumnName(roll)+" ="+"</th>";
            else
                d = d + "<th>"+c.getColumnName(roll).substring(5)+"</th>";
            c.moveToFirst();
            do{
                if(c.getColumnName(roll).equals("Total"))
                    d = d + "<th>"+c.getString(roll)+"</th>";
                else
                    d = d + "<td>"+c.getString(roll)+"</td>";
            }while (c.moveToNext());
        }
        return d;
    }

    static String GiveAllRoll(Cursor c, int max){
        String r = "";
        if(c!=null && c.moveToFirst()) {
            for (int i = 1; i <= max+1; i++) {
                r = r + "<tr>" + GiveRoll(c, i) + "</tr>";
            }
        }
        return r;
    }
}


