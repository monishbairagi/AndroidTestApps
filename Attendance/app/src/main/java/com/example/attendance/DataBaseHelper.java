package com.example.attendance;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DataBaseHelper extends SQLiteOpenHelper {
    int max;
    int total_present = 0;

    String TABLE_NAME="dummy";

    public DataBaseHelper(Context context, int total) {
        super(context, "attendance.db", null, 1);
        max=total;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String s = " (Date TEXT PRIMARY KEY,";
        for(int i=1;i<=max;i++){
            if(i!=max)
                s =s+ " Roll_"+Integer.toString(i)+" TEXT, ";
            else {
                s = s + " Roll_" + Integer.toString(i) + " TEXT, ";
                s = s + "Total INTEGER)";
            }
        }
        db.execSQL("create table "+TABLE_NAME+s);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        onCreate(db);
    }

    public void createUserTable( String user,int max) {
        final SQLiteDatabase db = getWritableDatabase();

        String s = " (Date TEXT PRIMARY KEY,";
        for(int i=1;i<=max;i++){
            if(i!=max)
                s =s+ " Roll_"+Integer.toString(i)+" TEXT, ";
            else {
                s = s + " Roll_" + Integer.toString(i) + " TEXT, ";
                s = s + "Total INTEGER)";
            }
        }
        db.execSQL("create table "+user+s);
        db.close();
    }

    public boolean presentData(String table_name,String roll){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        Cursor p = db.rawQuery("select * from " + table_name,null);

        contentValues.put("Date",MainActivity.Date());
        db.insert(table_name,null,contentValues);

        try{
            int val=0;
            p.moveToLast();
            p.moveToPrevious();
            if(p.getInt(Integer.parseInt(roll)) != 0) {
                val = p.getInt(Integer.parseInt(roll));
                contentValues.put("Roll_" + roll, val + 1);
            }
            else{
                while(p.moveToPrevious()){
                    val = p.getInt(Integer.parseInt(roll));
                    if(val!=0)
                        break;
                }
                contentValues.put("Roll_" + roll, val + 1);
            }
        }
        catch (Exception e){
            contentValues.put("Roll_"+roll,1);
        }

        int r = db.update(table_name,contentValues,"Date=?",new String[]{MainActivity.Date()});
        if(r > 0) {
            contentValues.put("Total",getTotalPresent(table_name,Integer.parseInt(roll)));
            int q = db.update(table_name,contentValues,"Date=?",new String[]{MainActivity.Date()});
            if(q>0)
                return true;
            else
                return false;
        }
        else
            return false;
    }

    public boolean absentData(String table_name,String roll){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put("Date",MainActivity.Date());
        db.insert(table_name,null,contentValues);

        contentValues.put("Roll_"+roll,"A");

        int r = db.update(table_name,contentValues,"Date=?",new String[]{MainActivity.Date()});
        if(r > 0){
            contentValues.put("Total",getTotalPresent(table_name,Integer.parseInt(roll)));
            int q = db.update(table_name,contentValues,"Date=?",new String[]{MainActivity.Date()});
            if(q>0)
                return true;
            else
                return false;
        }
        else
            return false;
    }

    public boolean presentDataUpdate(String table_name,String roll){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        try {
            Cursor p = db.rawQuery("select * from " + table_name, null);

            contentValues.put("Date", MainActivity.Date());
            db.insert(table_name, null, contentValues);

            p.moveToLast();
        }
        catch (Exception e){
            return false;
        }

        try{
            Cursor p = db.rawQuery("select * from " + table_name, null);
            int val=0;
            p.moveToLast();
            p.moveToPrevious();
            if(p.getInt(Integer.parseInt(roll)) != 0) {
                val = p.getInt(Integer.parseInt(roll));
                contentValues.put("Roll_" + roll, val + 1);
            }
            else{
                while(p.moveToPrevious()){
                    val = p.getInt(Integer.parseInt(roll));
                    if(val!=0)
                        break;
                }

                contentValues.put("Roll_" + roll, val + 1);
            }
        }
        catch (Exception e){
            contentValues.put("Roll_"+roll,1);
        }

        int r = db.update(table_name,contentValues,"Date=?",new String[]{MainActivity.Date()});
        if(r > 0){
            contentValues.put("Total",getTotalPresentAfterUpdate(table_name));
            int q = db.update(table_name,contentValues,"Date=?",new String[]{MainActivity.Date()});
            if(q>0)
                return true;
            else
                return false;
        }
        else
            return false;
    }

    public boolean absentDataUpdate(String table_name,String roll){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        try {
            Cursor c = getAllData(table_name);
            c.moveToLast();

            contentValues.put("Date", MainActivity.Date());
            db.insert(table_name, null, contentValues);

            contentValues.put("Roll_" + roll, "A");
        }
        catch (Exception e){
            return false;
        }

        int r = db.update(table_name,contentValues,"Date=?",new String[]{MainActivity.Date()});
        if(r > 0){
            contentValues.put("Total",getTotalPresentAfterUpdate(table_name));
            int q = db.update(table_name,contentValues,"Date=?",new String[]{MainActivity.Date()});
            if(q>0)
                return true;
            else
                return false;
        }
        else
            return false;
    }

    public Cursor getAllData(String table_name){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from " + table_name,null);
        return res;
    }

    public void deleteAllData(String table_name){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS "+ table_name);
    }

    public int getTotalPresent(String table_name,int total){
        int r = 0;
        Cursor c = getAllData(table_name);
        c.moveToLast();
        for(int i=1;i<=total;i++) {
            if (c.getString(i).equals("A"))
                ++r;
        }
        return c.getColumnCount()-r-2;
    }

    public int getTotalPresentAfterUpdate(String table_name){
        int r = 0;
        Cursor c = getAllData(table_name);
        c.moveToLast();
        for(int i=1;i<=c.getColumnCount()-2;i++) {
            if (c.getString(i).equals("A"))
                ++r;
        }
        return c.getColumnCount()-r-2;
    }

    public boolean checkAgain(String table_name){
        Cursor c = getAllData(table_name);
        c.moveToLast();
        if (MainActivity.Date().equals(c.getString(0)) && !c.isNull(c.getColumnCount()-2))
            return true;
        else
            return false;
    }
}