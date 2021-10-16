package com.example.attendanceapp;

import static com.example.attendanceapp.DateYearConversion.getNumStrToStrDate;
import static com.example.attendanceapp.DateYearConversion.getStrDateToNumStr;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

public class DBHelper extends SQLiteOpenHelper {
    private static final int VERSION = 1;

    // TODO: class table
    public static String CLASS_TABLE = "CLASS_TABLE";
    public static String CID = "_CID";
    public static String SUBJECT_NAME = "SUBJECT_NAME";
    public static String CLASS_NAME = "CLASS_NAME";
    private static String CREATE_CLASS_TABLE =
            "CREATE TABLE "+CLASS_TABLE+"(" +
                    CID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                    SUBJECT_NAME + " TEXT NOT NULL, " +
                    CLASS_NAME + " TEXT NOT NULL, " +
                    "UNIQUE (" + CLASS_NAME + "," + SUBJECT_NAME + ")" +
                    ")";
    private static String DROP_CLASS_TABLE = "DROP TABLE IF EXISTS " + CLASS_TABLE;
    private static String SELECT_ALL_CLASS_TABLE = "SELECT * FROM " + CLASS_TABLE;

    // TODO: student table
    public static String STUDENT_TABLE = "STUDENT_TABLE";
    public static String SID = "_SID";
    public static String ROLL = "ROLL";
    public static String STUDENT_NAME = "STUDENT_NAME";
    // CID AS A FOREIGN KEY
    private static String CREATE_STUDENT_TABLE =
            "CREATE TABLE "+STUDENT_TABLE+"(" +
                    SID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                    ROLL + " INTEGER NOT NULL, " +
                    STUDENT_NAME + " TEXT NOT NULL, " +
                    CID + " INTEGER NOT NULL, " +
                    "FOREIGN KEY (" + CID + ") REFERENCES " + CLASS_TABLE + "(" + CID + ") ON DELETE CASCADE" +
                    ")";
    private static String DROP_STUDENT_TABLE = "DROP TABLE IF EXISTS " + STUDENT_TABLE;
    private static String SELECT_ALL_STUDENT_TABLE = "SELECT * FROM " + STUDENT_TABLE;

    // TODO: status table
    public static String ATTENDANCE_TABLE = "ATTENDANCE_TABLE";
    public static String AID = "_AID";
    public static String DATE = "DATE";
    public static String STATUS = "STATUS";
    // SID AS A FOREIGN KEY
    private static String CREATE_ATTENDANCE_TABLE =
            "CREATE TABLE "+ATTENDANCE_TABLE+"(" +
                    AID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                    DATE + " TEXT NOT NULL, " +
                    STATUS + " TEXT NOT NULL, " +
                    SID + " INTEGER NOT NULL, " +
                    CID + " INTEGER NOT NULL, " +
                    "UNIQUE (" + SID + "," + DATE + ")," +
                    "FOREIGN KEY (" + SID + ") REFERENCES " + STUDENT_TABLE + "(" + SID + ") ON DELETE CASCADE," +
                    "FOREIGN KEY (" + CID + ") REFERENCES " + CLASS_TABLE + "(" + CID + ") ON DELETE CASCADE" +
                    ")";
    private static String DROP_ATTENDANCE_TABLE = "DROP TABLE IF EXISTS " + ATTENDANCE_TABLE;
    private static String SELECT_ALL_ATTENDANCE_TABLE = "SELECT * FROM " + ATTENDANCE_TABLE;

    public DBHelper(@Nullable Context context) {
        super(context,"attendance.db",null,VERSION);
    }
    @Override
    public void onOpen(SQLiteDatabase db){
        super.onOpen(db);
        db.execSQL("PRAGMA foreign_keys=ON");
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_CLASS_TABLE);
        sqLiteDatabase.execSQL(CREATE_STUDENT_TABLE);
        sqLiteDatabase.execSQL(CREATE_ATTENDANCE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        try {
            sqLiteDatabase.execSQL(DROP_CLASS_TABLE);
            sqLiteDatabase.execSQL(DROP_STUDENT_TABLE);
            sqLiteDatabase.execSQL(DROP_ATTENDANCE_TABLE);
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    // TODO: class table operations
    public long addClass(String subject_name, String class_name){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(SUBJECT_NAME,subject_name);
        cv.put(CLASS_NAME,class_name);
        long result = db.insert(CLASS_TABLE,null,cv);
        db.close();
        return result;
    }
    public int deleteClass(String cid){
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(CLASS_TABLE, CID+"=?",new String[]{cid});
        db.close();
        return result;
    }
    public int editClass(String cid, String subject_name, String class_name){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(SUBJECT_NAME,subject_name);
        cv.put(CLASS_NAME,class_name);
        int result = db.update(CLASS_TABLE,cv,CID+"=?",new String[]{cid});
        db.close();
        return result;
    }
    public ArrayList<ClassItem> getAllClass(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor =  db.rawQuery(SELECT_ALL_CLASS_TABLE,null);
        ArrayList<ClassItem> classItems = new ArrayList<>();
        while (cursor.moveToNext()){
            int class_id = Integer.parseInt(cursor.getString((int)cursor.getColumnIndex(CID)));
            String subject_name = cursor.getString((int)cursor.getColumnIndex(SUBJECT_NAME));
            String class_name = cursor.getString((int)cursor.getColumnIndex(CLASS_NAME));

            ClassItem classItem = new ClassItem(class_id,subject_name,class_name);
            classItem.setAttendance_today(isTodayAttendance(String.valueOf(class_id)));
            classItem.setTotal_student(getTotalStudentToday(String.valueOf(class_id)));
            classItem.setTotal_present(getTotalPresentToday(String.valueOf(class_id)));

            classItems.add(classItem);
        }
        db.close();
        return classItems;
    }

    // TODO: student table operations
    public long addStudent(String cid, String roll, String student_name){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(CID,cid);
        cv.put(ROLL,roll);
        cv.put(STUDENT_NAME,student_name);
        long result = db.insert(STUDENT_TABLE,null,cv);
        db.close();
        return result;
    }
    public int deleteStudent(String sid){
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(STUDENT_TABLE, SID+"=?",new String[]{sid});
        db.close();
        return result;
    }
    public int editStudent(String sid, String student_name){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(SID,sid);
        cv.put(STUDENT_NAME,student_name);
        int result = db.update(STUDENT_TABLE,cv,SID+"=?",new String[]{sid});
        db.close();
        return result;
    }
    public ArrayList<StudentItem> getAllStudent(String cid){
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<StudentItem> studentItems = new ArrayList<>();
        Cursor cursor = db.rawQuery(
                SELECT_ALL_STUDENT_TABLE+" " + "WHERE " + CID + "=" + cid + " ORDER BY " + ROLL + " ASC ",
                null);
        while (cursor.moveToNext()){
            long student_id = Long.parseLong(cursor.getString((int)cursor.getColumnIndex(SID)));
            int student_roll = Integer.parseInt(cursor.getString((int)cursor.getColumnIndex(ROLL)));
            String student_name = cursor.getString((int)cursor.getColumnIndex(STUDENT_NAME));
            studentItems.add(new StudentItem(student_id, student_roll,student_name));
        }
        db.close();
        return studentItems;
    }

    // TODO: attendance table operations
    public long addAttendance(String sid, String cid, String date, String status){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(SID,sid);
        cv.put(CID,cid);
        cv.put(DATE,date);
        cv.put(STATUS,status);
        long result = db.insert(ATTENDANCE_TABLE,null,cv);
        db.close();
        return result;
    }
    public int editAttendance(String sid, String date, String status){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(STATUS,status);
        int result = db.update(ATTENDANCE_TABLE,cv,SID+"="+sid+" AND "+DATE+"='"+date+"'",null);
        db.close();
        return result;
    }
    public String getStatus(String sid, String date){
        SQLiteDatabase db = this.getReadableDatabase();
        String status = null;
        Cursor cursor = db.query(ATTENDANCE_TABLE,null,SID+"="+sid+" AND "+DATE+"='"+date+"'",null,null,null,null);
        if (cursor.moveToFirst()){
            status = cursor.getString((int)cursor.getColumnIndex(STATUS));
        }
        db.close();
        return status;
    }

    // TODO: other required function (reading database)
    private boolean isTodayAttendance(String cid) {
        SQLiteDatabase db = this.getReadableDatabase();
        String date = new MyCalendar().getDateToday();
        Cursor cursor = db.rawQuery("SELECT DISTINCT "+DATE+" FROM "+ATTENDANCE_TABLE+" WHERE "+CID+" = "+cid+" AND "+DATE+"='"+date+"'",null);
        if(cursor.moveToFirst()){
            return true;
        }
        db.close();
        return false;
    }
    private long getTotalStudentToday(String cid){
        long count = 0;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT DISTINCT "+SID+" FROM "+ATTENDANCE_TABLE+" WHERE "+CID+" = "+cid,null);
        while (cursor.moveToNext()){
            count++;
        }
        db.close();
        return count;
    }
    private long getTotalPresentToday(String cid){
        long count = 0;
        SQLiteDatabase db = this.getReadableDatabase();
        String date = new MyCalendar().getDateToday();
        Cursor cursor = db.rawQuery("SELECT DISTINCT "+SID+" FROM "+ATTENDANCE_TABLE+" WHERE "+CID+"="+cid+" AND "+STATUS+"='P' AND "+DATE+"='"+date+"'",null);
        while (cursor.moveToNext()){
            count++;
        }
        db.close();
        return count;
    }
    public ArrayList<String> getMonths(String cid){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT DISTINCT SUBSTR("+DATE+", LENGTH("+DATE+") - 7, LENGTH("+DATE+")) FROM "+ATTENDANCE_TABLE+" WHERE "+CID+" = "+cid,null);

        ArrayList<String> tempList = new ArrayList<>();
        while (cursor.moveToNext()){
            tempList.add(getStrDateToNumStr(cursor.getString(0)));
        }

        // sorting full_date_in_num_str
        Collections.sort(tempList);

        ArrayList<String> result = new ArrayList<>();
        for(String date : tempList){
            result.add(getNumStrToStrDate(date));
        }
        db.close();
        return result;
    }
}
