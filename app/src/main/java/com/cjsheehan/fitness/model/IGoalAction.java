package com.cjsheehan.fitness.model;

/**
 * Created by Chris on 10/03/2016.
 */
public interface IGoalAction {

    void addGoal(Goal goal);

    void removeGoal(Goal goal);

    void updateGoal(Goal goal);
}
