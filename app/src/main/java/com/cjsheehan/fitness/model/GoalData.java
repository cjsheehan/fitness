package com.cjsheehan.fitness.model;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;


public class GoalData {

    private static final String TAG = "GoalData";
    private List<Goal> _goals;

    public GoalData(Context context) {
        //super(context);
        //this.repository = new DbGoal(context);
        // TODO : db access
        _goals = new ArrayList<>();
        _goals = fillGoalList();
    }


    public void add(Goal goal) {
        Log.d(TAG, "Entered : create(Goal goal()");
        // TODO : implement GoalData.create(Goal goal)
        _goals.add(goal);
    }

    public List<Goal> getAll() {
        Log.d(TAG, "Entered : read()");
        // TODO : implement GoalData.read()
        //int idx = _goals.indexOf(goal);
        //if(idx >= 0)
        //    _goals.get(idx) = goal;
        return _goals;

    }

    private List<Goal> fillGoalList() {

        List<Goal> data = new ArrayList<>();

        data.add(new Goal("High", 0, 10000, GoalState.INACTIVE));
        data.add(new Goal("Medium", 1, 5000, GoalState.ACTIVE));
        data.add(new Goal("Low", 2, 1000, GoalState.INACTIVE));
        return data;
    }

    //public Goal read(Goal goal) {
    //    Log.d(TAG, "Entered : read(Goal goal)");
    //    // TODO : implement GoalData.read(Goal goal)
    //    int idx = _goals.indexOf(goal);
    //    if(idx >= 0)
    //        return _goals.get(idx);
    //}

    public void update(Goal goal) {
        Log.d(TAG, "Entered : update(Goal goal)");
        // TODO : implement GoalData.update(Goal goal)

        int idx = _goals.indexOf(goal);
        if (idx >= 0)
            _goals.set(idx, goal);
    }


    public void remove(Goal goal) {
        Log.d(TAG, "Entered : delete(Goal goal)");
        // TODO : implement GoalData.delete(Goal goal)
        if (_goals.remove(goal)) {
            Log.d(TAG, "Deleted goal : " + goal.getTitle());
        } else {
            Log.d(TAG, "Cannot delete goal : " + goal.getTitle());
        }
    }
}