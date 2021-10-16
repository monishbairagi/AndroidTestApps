package com.example.attendanceapp;

import java.util.Calendar;

public class DateYearConversion {
    public static String getStrDateToNumStr(String str_date){
        // month_year -> 'mmm yyyy' -> 'mm yyyy'
        String month = str_date.substring(0,3);
        String year = str_date.substring(4,8);
        // assigning number for sort date
        String month_to_num=null;
        if (month.equals("Jan")) month_to_num="01";
        else if (month.equals("Feb")) month_to_num="02";
        else if (month.equals("Mar")) month_to_num="03";
        else if (month.equals("Apr")) month_to_num="04";
        else if (month.equals("May")) month_to_num="05";
        else if (month.equals("Jun")) month_to_num="06";
        else if (month.equals("Jul")) month_to_num="07";
        else if (month.equals("Aug")) month_to_num="08";
        else if (month.equals("Sep")) month_to_num="09";
        else if (month.equals("Oct")) month_to_num="10";
        else if (month.equals("Nov")) month_to_num="11";
        else if (month.equals("Dec")) month_to_num="12";
        return month_to_num+year;
    }

    public static String getNumStrToStrDate(String str_date){
        // month_year -> 'mm yyyy' -> 'mmm yyyy'
        String month = str_date.substring(0,2);
        String year = str_date.substring(2,6);
        String num_to_month=null;
        // retrieving month from number
        if (month.equals("01")) num_to_month="Jan";
        else if (month.equals("02")) num_to_month="Feb";
        else if (month.equals("03")) num_to_month="Mar";
        else if (month.equals("04")) num_to_month="Apr";
        else if (month.equals("05")) num_to_month="May";
        else if (month.equals("06")) num_to_month="Jun";
        else if (month.equals("07")) num_to_month="Jul";
        else if (month.equals("08")) num_to_month="Aug";
        else if (month.equals("09")) num_to_month="Sep";
        else if (month.equals("10")) num_to_month="Oct";
        else if (month.equals("11")) num_to_month="Nov";
        else if (month.equals("12")) num_to_month="Dec";
        return num_to_month+" "+year;
    }

    public static int getDayInMonth(String month_year) {
        // month_year -> 'mm yyyy'
        month_year = getStrDateToNumStr(month_year);
        String month = month_year.substring(0,2);
        String year = month_year.substring(2,6);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MONTH, Integer.parseInt(month));
        calendar.set(Calendar.YEAR, Integer.parseInt(year));
        return calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
    }
}
