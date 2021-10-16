package com.example.attendanceapp;

public class ClassItem {
    private long class_id;
    private String subject_name, class_name;
    private long total_student, total_present;
    private boolean attendance_today;

    public ClassItem(long class_id, String subject_name, String class_name) {
        this.class_id = class_id;
        this.subject_name = subject_name;
        this.class_name = class_name;
        this.attendance_today = false;
        this.total_student = 0;
        this.total_present = 0;
    }

    public long getClass_id() {
        return class_id;
    }

    public String getSubject_name() {
        return subject_name;
    }

    public String getClass_name() {
        return class_name;
    }

    public void setSubject_name(String subject_name) {
        this.subject_name = subject_name;
    }

    public void setClass_name(String class_name) {
        this.class_name = class_name;
    }

    public long getTotal_student() { return total_student; }

    public void setTotal_student(long total_student) { this.total_student = total_student; }

    public long getTotal_present() { return total_present; }

    public void setTotal_present(long total_present) { this.total_present = total_present; }

    public boolean getAttendance_today() { return attendance_today; }

    public void setAttendance_today(boolean attendance_today) { this.attendance_today = attendance_today; }
}
