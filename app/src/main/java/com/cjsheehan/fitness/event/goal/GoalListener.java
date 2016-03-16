package com.cjsheehan.fitness.event.goal;


import com.cjsheehan.fitness.model.Goal;

public interface GoalListener {
    void onActiveGoalChanged(Goal goal);
    void onGoalProgressChanged(double progress);
}
