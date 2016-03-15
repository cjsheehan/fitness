package com.cjsheehan.fitness.util;

import android.app.Activity;
import android.content.Intent;

import com.cjsheehan.fitness.R;

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

    public static void restartApplication(final Activity activity) {
        Intent intent = new Intent(activity, com.cjsheehan.fitness.activity.MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("Exiting application", true);
        activity.startActivity(intent);
    }


}
