package com.cjsheehan.fitness.model;

import android.content.Context;
import android.util.Log;

import com.cjsheehan.fitness.db.DBHelper;
import com.cjsheehan.fitness.db.DbStatus;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class GoalData {
    private static final String TAG = "GoalData";
    private List<Goal> _goals;
    private Goal _active;
    // Db
    DBHelper _dbHelper;
    Context _context;

    public GoalData(Context context, String date) throws IOException {
        _context = context;
        _dbHelper = new DBHelper(_context);

        // Get all goals for date
        getAllByDate(date);
        _active = getActive();
}

    public int size() {
        return _goals.size();
    }

    public void setActive(int idx) {
        for (int i = 0; i < _goals.size() ; i++) {
            Goal g = _goals.get(i);
            if (i == idx) {
                g.setActiveState(ActiveState.ACTIVE);
            } else {
                g.setActiveState(ActiveState.INACTIVE);
            }
            _dbHelper.updateGoal(g);
        }
    }

    public Goal getActive() {
        _active = null;
        for (Goal g : _goals) {
            if (g.getActiveState() == ActiveState.ACTIVE) {
                return g;
            }
        }
        return null;
    }

    public int getActiveIdx() {
        for (int i = 0; i < _goals.size(); i++) {
            if (_goals.get(i).getActiveState() == ActiveState.ACTIVE)
                return i;
        }
        return -1;
    }

    public int countActive() {
        int count = 0;
        for (int i = 0; i < _goals.size(); i++) {
            if (_goals.get(i).getActiveState() == ActiveState.ACTIVE)
                count++;
        }
        return count;
    }

    public DbStatus add(Goal goal) {
        if(countActive() < 1) {
            goal.setActiveState(ActiveState.ACTIVE);
        }
        DbStatus dbstatus = _dbHelper.insertGoal(goal);
        if(dbstatus == DbStatus.OK)
            _goals.add(goal);

        return dbstatus;
    }

    public List<Goal> getAll() {
        return _goals;
    }

    public List<Goal> getAllByDate(String date) {
        _goals = _dbHelper.getGoalsByDate(date);
        int size = _goals.size();
        if(size == 1 && _goals.get(0).getActiveState() == ActiveState.INACTIVE) {
            setActive(0);
        }

        return _goals;
    }

    public List<Goal> setDate(String date) {
        _goals = getAllByDate(date);
        return _goals;
    }

    public Goal get(int idx) {
        if(idx >= 0)
           return _goals.get(idx);
        return null;
    }

    //
    //private List<Goal> fillGoalList() {
    //
    //    List<Goal> data = new ArrayList<>();
    //    String date = Calendar.getInstance().
    //
    //    data.add(new Goal("High", _date, 0, 7000, 10000, Unit.STEP, ActiveState.INACTIVE));
    //    data.add(new Goal("Medium", _date, 0, 500, 600.50, Unit.MILE, ActiveState.ACTIVE));
    //    data.add(new Goal("Low", _date, 0, 500, 99999, Unit.KILOMETRE, ActiveState.INACTIVE));
    //    return data;
    //}

    //public Goal read(Goal goal) {
    //    Log.d(TAG, "Entered : read(Goal goal)");
    //    // TODO : implement GoalData.read(Goal goal)
    //    int idx = _goals.indexOf(goal);
    //    if(idx >= 0)
    //        return _goals.get(idx);
    //}

    public void update(Goal goal) {
        int idx = _goals.indexOf(goal);
        if (idx >= 0) {
            _goals.set(idx, goal);
            //_dbHelper.updateGoal(goal);
        }
    }

    public void updateProgress(double progress, int idx) {
        if (idx >= 0) {
            _goals.get(idx).setProgress(progress);
            _dbHelper.updateGoal(_goals.get(idx));
        }
    }

    public void remove(Goal goal) {
        if (_goals.remove(goal)) {
            Log.d(TAG, "Deleted goal : " + goal.getTitle());
        } else {
            Log.d(TAG, "Cannot delete goal : " + goal.getTitle());
        }
    }

    public void remove(int idx) {
        String title = _goals.get(idx).getTitle();
        if (idx < _goals.size()) {
            Log.d(TAG, "Deleted goal : " + title);
            _goals.remove(idx);
        } else {
            Log.d(TAG, "Cannot delete goal : " + title);
        }
    }
}