package com.example.attendanceapp;

public class StudentItem {
    private long student_id;
    private int roll;
    private String name, status;

    public StudentItem(long student_id, int roll, String name) {
        this.student_id = student_id;
        this.roll = roll;
        this.name = name;
        this.status = "";
    }

    public long getStudent_id() { return student_id; }

    public int getRoll() {
        return roll;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
