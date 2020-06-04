package com.example.attendance;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SetupHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "Setup_data.db";
    public static final String TABLE_NAME = "data_table";
    public static final String DEPT = "Dept";
    public static final String YEAR = "Year";
    public static final String SUBJECT = "Subject";
    public static final String MAX = "MAX_STUDENT";

    public SetupHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String s = "(ID INTEGER PRIMARY KEY AUTOINCREMENT, "+DEPT+" TEXT, "+YEAR+" TEXT, "+SUBJECT+" TEXT, "+MAX+" INTEGER"+")";
        db.execSQL("create table "+TABLE_NAME+s);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        onCreate(db);
    }
    public boolean addData(String dept,String year,String subject,int total_student){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(DEPT,dept);
        contentValues.put(YEAR,year);
        contentValues.put(SUBJECT,subject);
        contentValues.put(MAX,total_student);
        long r = db.insert(TABLE_NAME,null,contentValues);
        if(r == -1)
            return false;
        else
            return true;
    }

    public boolean delData(String key){
        SQLiteDatabase db = this.getWritableDatabase();
        int r =  db.delete(TABLE_NAME, "ID = ?",new String[] {key});
        if(r == 0)
            return false;
        else
            return true;
    }

    public Cursor getListContent(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from " + TABLE_NAME,null);
        return res;
    }



}
