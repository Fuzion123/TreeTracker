package com.apps.frederik.treetracker.Model.Util;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by Frederik on 12/9/2017.
 */

public class TimeStampHelper {
    private static Calendar _calender = Calendar.getInstance();

    public static int get_year(String date) {
        _calender.setTime(new Date(date));
        return _calender.get(Calendar.YEAR);
    }

    public static int get_month(String date) {
        _calender.setTime(new Date(date));
        return _calender.get(Calendar.MONTH);
    }

    public static int get_day(String date) {
        _calender.setTime(new Date(date));
        return _calender.get(Calendar.DAY_OF_MONTH);
    }

    public static int get_hour(String date) {
        _calender.setTime(new Date(date));
        return _calender.get(Calendar.HOUR);
    }

    public static int get_minute(String date) {
        _calender.setTime(new Date(date));
        return _calender.get(Calendar.MINUTE);
    }

    public static int get_second(String date) {
        _calender.setTime(new Date(date));
        return _calender.get(Calendar.SECOND);
    }

    public static int get_milliSecond(String date) {
        _calender.setTime(new Date(date));
        return _calender.get(Calendar.MILLISECOND);
    }

    public static Date get_dataTime(String date){
        _calender.setTime(new Date(date));
        return _calender.getTime();
    }
}
