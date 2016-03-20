package com.cjsheehan.fitness.view;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.cjsheehan.fitness.R;
import com.cjsheehan.fitness.model.Goal;
import com.cjsheehan.fitness.model.UnitConverter;
import com.cjsheehan.fitness.util.Util;

public class GoalView {
    private final Context context;
    private TextView title;
    private TextView target;
    private TextView progress;
    private TextView unit;
    private ProgressBar targetProgress;

    public GoalView(Context context) {
        this.context = context;
    }

    public void update(Goal goal) {
        double progress = goal.getProgress();
        double target = goal.getTarget();
        String strTarget = Util.format(target);
        String strProgress = Util.format(progress);
        String strUnit = UnitConverter.toString(goal.getUnit());


        if(this.title != null)
            this.title.setText(goal.getTitle());

        if(this.target != null)
             this.target.setText(strTarget);

        if(this.progress != null)
            this.progress.setText(strProgress);

        if(this.targetProgress != null)
            setProgressBarView(progress, target);

        if(this.unit != null)
             this.unit.setText(strUnit);
    }

    public void setProgressBarView(double progress, double target) {
        int progressAsInt = Util.toInt(progress);
        int targetAsInt = Util.toInt(target);
        if(progressAsInt < targetAsInt) {
            this.targetProgress.setProgressDrawable(this.context.getDrawable(R.drawable.progress_bar_circular));
        } else {
            this.targetProgress.setProgressDrawable(this.context.getDrawable(R.drawable.progress_bar_circular_complete));
        }

        this.targetProgress.setMax(0);
        this.targetProgress.setProgress(0);
        this.targetProgress.setMax(targetAsInt);
        this.targetProgress.setProgress(progressAsInt);
    }

    public void setProgress(TextView progress) {
        this.progress = progress;
    }

    public void setTarget(TextView target) {
        this.target = target;
    }

    public void setTargetProgress(ProgressBar targetProgress) {

        this.targetProgress = targetProgress;
    }

    public void setTitle(TextView title) {
        this.title = title;
    }

    public void setUnit(TextView unit) {
        this.unit = unit;
    }
}