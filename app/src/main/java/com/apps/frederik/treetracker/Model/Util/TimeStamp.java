package com.apps.frederik.treetracker.Model.Util;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by Frederik on 12/9/2017.
 */

public class TimeStamp {
    private Calendar _calender;
    private final Date _date;

    public TimeStamp(Date date){
        _date = date;
        _calender = Calendar.getInstance();
        _calender.setTime(_date);
    }

    public int get_year() {
        return _calender.get(Calendar.YEAR);
    }

    public int get_month() {
        return _calender.get(Calendar.MONTH);
    }

    public int get_day() {
        return _calender.get(Calendar.DAY_OF_MONTH);
    }

    public int get_hour() {
        return _calender.get(Calendar.HOUR);
    }

    public int get_minute() {
        return _calender.get(Calendar.MINUTE);
    }

    public int get_second() {
        return _calender.get(Calendar.SECOND);
    }

    public int get_milliSecond() {return _calender.get(Calendar.MILLISECOND);}

}
