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
import java.util.Random;

public class Util {

    public static final String DATE_FORMAT = "dd MMMM yyyy";
    public static DateFormat DATE_FORMATTER = new SimpleDateFormat(DATE_FORMAT, Locale.ENGLISH);
    private final static int DAY_IN_MS = 86400000;
    public static Random RNG = new Random();
    private static final Unit[] unitArr = new Unit[]{Unit.STEP, Unit.YARD, Unit.METRE, Unit.KILOMETRE, Unit.MILE};
    private static Calendar CALENDAR = Calendar.getInstance();

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
        Date fmtToDate = null;

        try {
            fmtToDate = DATE_FORMATTER.parse(toDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        CALENDAR.setTime(fmtToDate);
        CALENDAR.add(Calendar.DATE, (-1 * numDays));
        String fromDate = DATE_FORMATTER.format(CALENDAR.getTime());
        List<String> dates = getDates(fromDate, toDate);
        return dates;
    }

    public static String getDateToday() {
        Calendar cal = Calendar.getInstance();
        return DATE_FORMATTER.format(cal.getTime());
    }

    public static List<String> getDates(String fromDate, String toDate) {
        List<String> dates = new ArrayList<>();
        Date fDateFrom = null;
        try {
            fDateFrom = DATE_FORMATTER.parse(fromDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Date fDateTo = null;
        try {
            fDateTo = DATE_FORMATTER.parse(toDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if(fDateFrom != null && fDateTo != null) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(fDateFrom);
            while (cal.getTime().before(fDateTo)) {
                cal.add(Calendar.DATE, 1);
                String strDate = DATE_FORMATTER.format(cal.getTime());
                dates.add(strDate);
            }
        }
        return dates;
    }

    private static final double MAX_TARGET = 1000;
    private static final double MAX_PROGRESS = MAX_TARGET * 1.3;

    public static List<Goal> genGoals(String date, int howMany) {
        List<Goal> goals = new ArrayList<>();
        RandomString rs = new RandomString(6);
        for (int i = 0; i < howMany; i++) {
            String title = rs.nextString(RNG);
            double target = MAX_TARGET * RNG.nextDouble();
            double progress = MAX_PROGRESS * RNG.nextDouble();
            Unit unit = unitArr[RNG.nextInt(unitArr.length)];
            ActiveState state = ActiveState.INACTIVE;
            if(i == 0)
                state = ActiveState.ACTIVE;
            else
                progress = 0.00;
            Goal g = new Goal(title, date, target, progress, unit, state);
            goals.add(g);
        }
        return goals;
    }





    //
    //
    //public static long getPastDate(String dateNow, int daysPast) {
    //    return 1;
    //}


}
