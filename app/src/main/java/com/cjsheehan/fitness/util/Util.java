package com.cjsheehan.fitness.util;

import android.app.Activity;
import android.content.Intent;

import com.cjsheehan.fitness.R;
import com.cjsheehan.fitness.model.ActiveState;
import com.cjsheehan.fitness.model.Goal;
import com.cjsheehan.fitness.model.Unit;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Util {

    public static final String DATE_FORMAT = "dd MMMM yyyy";
    public static DateFormat DATE_FORMATTER = new SimpleDateFormat(DATE_FORMAT, Locale.ENGLISH);
    private final static int DAY_IN_MS = 86400000;


    private static final String ACTIVE = "active";
    private static final String INACTIVE = "inactive";

    public static String formatTo2dp(double number) {
        DecimalFormat to2dp = new DecimalFormat("#.##");
        return to2dp.format(number);
    }

    public static String formatTo0dp(double number) {
        DecimalFormat to0dp = new DecimalFormat("#");
        return to0dp.format(number);
    }

    public static String format(int number) {
        return formatTo0dp(number);
    }

    public static String format(double number) {
        return formatTo2dp(number);
    }

    public static void restartApplication(final Activity activity) {
        Intent intent = new Intent(activity, com.cjsheehan.fitness.activity.MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("Exiting application", true);
        activity.startActivity(intent);
    }

    public static int toInt(double number) {
        return (int) Math.round(number);
    }

    public static String toString(double number) {
        return formatTo2dp(number);
    }

    public static String toString(int number) {
        return formatTo0dp(number);
    }

    public static ActiveState toState(String state) {
        if(state.equalsIgnoreCase(ACTIVE))
            return ActiveState.ACTIVE;
        else
            return ActiveState.INACTIVE;
    }

    public static String toString(ActiveState state) {
        if(state == ActiveState.ACTIVE)
            return ACTIVE;
        else
            return INACTIVE;
    }

    public static Goal getDefaultGoal(String date) {
        return new Goal("Default", date, 0, 0, 10000, Unit.STEP, ActiveState.ACTIVE);
    }

    public static List<String> getDates(String toDate, int numDays) {
        Calendar cal = Calendar.getInstance();
        Date fmtToDate = null;

        try {
            fmtToDate = DATE_FORMATTER.parse(toDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        cal.setTime(fmtToDate);
        cal.add(Calendar.DATE, (-1 * numDays));
        String fromDate = DATE_FORMATTER.format(cal.getTime());
        List<String> dates = getDates(fromDate, toDate);
        return dates;
    }

    public static List<String> getDates(String fromDate, String toDate) {
        List<String> dates = new ArrayList<>();
        Date frmtDateFrom = null;
        try {
            frmtDateFrom = DATE_FORMATTER.parse(fromDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Date frmtDateTo = null;
        try {
            frmtDateFrom = DATE_FORMATTER.parse(toDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if(frmtDateFrom != null && frmtDateTo != null) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(frmtDateFrom);
            while (cal.getTime().before(frmtDateTo)) {
                cal.add(Calendar.DATE, 1);
                String strDate = DATE_FORMATTER.format(cal.getTime());
                dates.add(strDate);
                System.out.println(cal.getTime());
            }
        }
        return dates;
    }



    //
    //
    //public static long getPastDate(String dateNow, int daysPast) {
    //    return 1;
    //}


}
