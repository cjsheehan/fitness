package com.cjsheehan.fitness.util;

import android.app.Activity;
import android.content.Intent;

import com.cjsheehan.fitness.R;
import com.cjsheehan.fitness.model.Unit;

import java.text.DecimalFormat;

public class Util {
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


    public static String unitToString(Unit unit) {
        String strUnit = null;
        switch (unit) {
            case METRE:
                strUnit = "m";
            break;
            case KILOMETRE:
                strUnit = "km";
            break;
            case MILE:
                strUnit ="mls";
            break;
            case YARD:
                strUnit ="yds";
            break;
            case STEP:
                strUnit ="step";
            break;
        }
        return strUnit;
    }
}
