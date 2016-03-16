package com.cjsheehan.fitness.view;

import android.widget.ProgressBar;
import android.widget.TextView;

import com.cjsheehan.fitness.model.Goal;
import com.cjsheehan.fitness.util.Util;

public class GoalView {
    private TextView title;
    private TextView target;
    private TextView progress;
    private TextView unit;
    private ProgressBar targetProgress;

    public void updateView(Goal goal) {
        double progress = goal.getProgress();
        double target = goal.getTarget();
        String strTarget = Util.format(target);
        String strProgress = Util.format(progress);
        this.title.setText(goal.getTitle());
        this.target.setText(strTarget);
        this.progress.setText(strProgress);
        setProgressBarView(target, progress);
    }

    public void setProgressBarView(double progress, double target) {
        int progressAsInt = Util.toInt(progress);
        int targetAsInt = Util.toInt(target);
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