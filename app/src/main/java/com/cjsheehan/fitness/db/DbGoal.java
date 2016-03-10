package com.cjsheehan.fitness.db;

import android.content.Context;

import com.cjsheehan.fitness.model.Goal;

import java.util.List;

public class DbGoal {
    private static final String TAG = "GoalDataLoader";

    //private IGoalRepository repository;

    public DbGoal(Context context) {
        //super(context);
        //this.repository = new GoalRepository(context);
    }

    protected List<Goal> readAll() {
        return null;
    }

    public void insert(Goal goal) {
    }

    public void delete(Goal goal) {
    }

    public void update(Goal goal) {
    }

}