package com.example.attendance;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.webkit.WebView;

public class AboutActivity extends AppCompatActivity {

    WebView about;
    final String name = "Monish Kumar Bairagi";
    final String clg = "HOOGHLY ENGINEERING AND TECHNOLOGY COLLEGE";
    final String stream = "Computer Science";
    final String email = "monishbairagi@gmail.com";
    final String desig = "App Developer";
    final String img_sec = "/drawable-v24/user.jpg";


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        about = findViewById(R.id.webview_about);

        about.loadDataWithBaseURL(null, getAboutHTML(name,desig,clg,stream,email,img_sec), "text/html", "utf-8", null);
        about.getSettings().setJavaScriptEnabled(true);
        about.getSettings().getDisplayZoomControls();
        about.getSettings().setLoadWithOverviewMode(true);


    }

    public String getAboutHTML(String name,String designation,String college,String stream,String email,String sec_image){
        return "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">\n" +
                "<style>\n" +
                "body {\n" +
                "  font-family: Arial, Helvetica, sans-serif;\n" +
                "  margin: 0;\n" +
                "}\n" +
                "\n" +
                "html {\n" +
                "  box-sizing: border-box;\n" +
                "}\n" +
                "\n" +
                "*, *:before, *:after {\n" +
                "  box-sizing: inherit;\n" +
                "}\n" +
                "\n" +
                ".column {\n" +
                "  float: left;\n" +
                "  width: 33.3%;\n" +
                "  margin-bottom: 16px;\n" +
                "  padding: 0 8px;\n" +
                "}\n" +
                "\n" +
                ".card {\n" +
                "  box-shadow: 0 4px 8px 0 rgba(0, 0, 0, 0.2);\n" +
                "  margin: 8px;\n" +
                "}\n" +
                "\n" +
                ".about-section {\n" +
                "  padding: 50px;\n" +
                "  text-align: center;\n" +
                "  background-color: #474e5d;\n" +
                "  color: white;\n" +
                "}\n" +
                "\n" +
                ".container {\n" +
                "  padding: 0 16px;\n" +
                "}\n" +
                "\n" +
                ".container::after, .row::after {\n" +
                "  content: \"\";\n" +
                "  clear: both;\n" +
                "  display: table;\n" +
                "}\n" +
                "\n" +
                ".title {\n" +
                "  color: grey;\n" +
                "}\n" +
                "\n" +
                ".button {\n" +
                "  border: none;\n" +
                "  outline: 0;\n" +
                "  display: inline-block;\n" +
                "  padding: 8px;\n" +
                "  color: white;\n" +
                "  background-color: #000;\n" +
                "  text-align: center;\n" +
                "  cursor: pointer;\n" +
                "  width: 100%;\n" +
                "}\n" +
                "\n" +
                ".button:hover {\n" +
                "  background-color: #555;\n" +
                "}\n" +
                "\n" +
                "@media screen and (max-width: 650px) {\n" +
                "  .column {\n" +
                "    width: 100%;\n" +
                "    display: block;\n" +
                "  }\n" +
                "}\n" +
                "</style>\n" +
                "</head>\n" +
                "<body>\n" +
                "\n" +
                "<div class=\"about-section\" style=\"text-align:center\">\n" +
                "  <h1>About Me</h1>\n" +
                "  <p></p>\n" +
                "  <p></p>\n" +
                "</div>\n" +
                "\n" +
                "<div class=\"row\" >\n" +
                "\n" +
                "  \n" +
                "  <div class=\"column\" >\n" +
                "    <div class=\"card\" >\n" +
                "    \n" +
//                "      <img src=\""+sec_image+"\" alt=\"User\" style=\"width:100%\">\n" +
                "      \n" +
                "      <div class=\"container\" style=\"text-align:center\">\n" +
                "        <h2>"+name+"</h2>\n" +
                "        <p class=\"title\">"+designation+"</p>\n" +
                "        <p>Hello,</p>\n" +
                "        <p>I am a "+stream+" Engineering Student of "+college+"</P>\n" +
                "        <p>"+email+"</p>\n" +
                "        <p><button class=\"button\">Contact</button></p>\n" +
                "      </div>\n" +
                "    </div>\n" +
                "  </div>\n" +
                "</div>\n" +
                "\n" +
                "</body>\n" +
                "</html>\n";
    }
}