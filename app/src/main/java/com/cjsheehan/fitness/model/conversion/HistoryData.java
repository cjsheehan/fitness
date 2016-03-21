package com.cjsheehan.fitness.model.conversion;

import android.content.Context;
import android.widget.ListView;

import com.cjsheehan.fitness.adapter.GoalListAdapter;
import com.cjsheehan.fitness.db.DBHelper;
import com.cjsheehan.fitness.model.Goal;
import com.cjsheehan.fitness.model.GoalData;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HistoryData {

    private static final String TAG = "HistoryData";
    private List<Goal> _goals;
    // Db
    DBHelper _dbHelper;
    Context _context;


    public HistoryData(Context context, List<String> dates) throws IOException {
        _context = context;
        _dbHelper = new DBHelper(_context);
        getAllByDate(dates);
    }

    public List<Goal> getAllByDate(List<String> dates) {
        _goals = new ArrayList<>();
        for(String date : dates) {
            Goal g = _dbHelper.getActiveGoal(date);
            if(g != null)
                _goals.add(g);
        }

        return _goals;
    }

    public List<Goal> getAll() {
        return _goals;
    }


    public List<Goal> getAllInRange(String dateFrom, String dateTo) {
        return _goals;
    }
}
