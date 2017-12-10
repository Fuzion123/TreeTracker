package com.apps.frederik.treetracker.Model.Util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Frederik on 12/10/2017.
 */

public final class TimestampFakeGenerator {
    public static TimeStamp GenerateTimeStamp() throws ParseException {
        //Date dateLowerRange = new Date();
        //Date dateUpperRange = new Date();

        String dateInString = "01/01/2017/13:45:30";

        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy/hh:mm:ss");
        Date parsedDate = formatter.parse(dateInString);

        return new TimeStamp(parsedDate);
    }
}
