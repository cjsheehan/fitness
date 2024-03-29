package com.cjsheehan.fitness.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.cjsheehan.fitness.model.ActiveState;
import com.cjsheehan.fitness.model.Goal;
import com.cjsheehan.fitness.model.Unit;
import com.cjsheehan.fitness.model.UnitConverter;
import com.cjsheehan.fitness.util.Util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class DBHelper extends SQLiteOpenHelper{
    private static final int DATABASE_VERSION= 1;
    private static final String DATABASE_NAME = "myKeepFit.db3";

    // Refer to Goal class : com.cjsheehan.fitness.model
    public static final String TABLE_GOALS = "goals";
    public static final String COLUMN_TITLE = "name";
    public static final String COLUMN_TARGET = "steps";
    public static final String COLUMN_PROGRESS = "progress";
    public static final String COLUMN_UNIT= "unit";
    public static final String COLUMN_ACTIVESTATE = "activestate";
    public static final String COLUMN_DATE= "date";
    public static final String CREATE_GOALS_TABLE =
        "CREATE TABLE IF NOT EXISTS " + TABLE_GOALS + "(" +
        COLUMN_TITLE  + " TEXT NOT NULL, " +
        COLUMN_DATE + " TEXT NOT NULL, " +
        COLUMN_TARGET + " REAL, " +
        COLUMN_PROGRESS + " REAL, " +
        COLUMN_UNIT + " TEXT, " +
        COLUMN_ACTIVESTATE + " TEXT, " +
        "PRIMARY KEY( " + COLUMN_TITLE + ", " + COLUMN_DATE + ")" +
            ");";

    private static final String DROP_GOALS_TABLE = "DROP TABLE IF EXISTS " + TABLE_GOALS;


    public DBHelper(Context context) throws IOException {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL(CREATE_GOALS_TABLE);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void onStart() {

    }

    //Add new goal to db
    public DbStatus insertGoal(Goal goal) {
        // Check if goal exists
        if(isExisting(goal))
            return DbStatus.FAIL_GOAL_EXISTS;


        String strUnit =  UnitConverter.toString(goal.getUnit());
        String strState = Util.toString(goal.getActiveState());

        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, goal.getTitle());
        values.put(COLUMN_DATE, goal.getDate());
        values.put(COLUMN_TARGET, goal.getTarget());
        values.put(COLUMN_PROGRESS, goal.getProgress());
        values.put(COLUMN_UNIT, strUnit);
        values.put(COLUMN_ACTIVESTATE, strState);
        SQLiteDatabase db = getWritableDatabase();

        db.insert(TABLE_GOALS, null, values);
        db.close();
        return DbStatus.OK;
    }

    public boolean isExisting(Goal goal) {
        String title = goal.getTitle();
        String date = goal.getDate();
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_GOALS + " WHERE " +
                COLUMN_DATE + "=\"" + date + "\" " + " AND " +
                COLUMN_TITLE + "=\"" + title + "\";";
        Cursor c = db.rawQuery(query, null);
        boolean isExisting = false;
        if(c.getCount() > 0) {
            isExisting = true;
        }
        db.close();
        return isExisting;
    }

    //Add new goal to db
    public DbStatus updateGoal(Goal goal) {
        if(!isExisting(goal))
            return DbStatus.FAIL_GOAL_NOT_EXIST;

        String strUnit =  UnitConverter.toString(goal.getUnit());
        String strState = goal.getActiveState().toString();

        // Retrieve goal with matching date and name
        SQLiteDatabase db = getWritableDatabase();
        String date = goal.getDate();
        String title = goal.getTitle();

        ContentValues values = new ContentValues();
        values.put(COLUMN_TARGET, goal.getTarget());
        values.put(COLUMN_PROGRESS, goal.getProgress());
        values.put(COLUMN_UNIT, strUnit);
        values.put(COLUMN_ACTIVESTATE, strState);

        String whereClause =  COLUMN_DATE + " = ? AND " + COLUMN_TITLE + " = ?";
        String[] whereArgs = new String[] {goal.getDate(), goal.getTitle()};
        int num_rows = db.update(TABLE_GOALS, values, whereClause, whereArgs);


        String query = "SELECT * FROM " + TABLE_GOALS + " WHERE " +
                COLUMN_DATE + "=\"" + date + "\" " + " AND " +
                COLUMN_TITLE + "=\"" + title + "\";";
        Cursor c = db.rawQuery(query, null);
        if(c != null && c.getCount() > 0) {
            c.moveToFirst();
            while(!c.isAfterLast()) {
                String row_title = c.getString(c.getColumnIndex(COLUMN_TITLE));
                String row_date = c.getString(c.getColumnIndex(COLUMN_DATE));
                double row_target = c.getDouble(c.getColumnIndex(COLUMN_TARGET));
                double row_progress = c.getDouble(c.getColumnIndex(COLUMN_PROGRESS));
                String row_unit = c.getString(c.getColumnIndex(COLUMN_UNIT));
                String row_active_state = c.getString(c.getColumnIndex(COLUMN_ACTIVESTATE));

                ActiveState state = Util.toState(row_active_state);
                Unit unit = UnitConverter.toUnit(row_unit);
                c.moveToNext();
            }
        }

        db.close();

        if(num_rows == 1) {
            return DbStatus.OK;
        } else if (num_rows == 0){
            return DbStatus.FAIL_GOAL_NOT_EXIST;
        } else if(num_rows > 1) {
            return DbStatus.FAIL_MORE_THAN_ONE_GOAL;
        }

        return DbStatus.FAIL_UNKNOWN;
    }

    public List<Goal> getGoalsByDate(String date) {
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_GOALS + " WHERE " + COLUMN_DATE + "=\"" + date + "\";";
        Cursor c = db.rawQuery(query, null);
        List<Goal> goals = new ArrayList<>();
        int count = c.getCount();

        if(c != null && c.getCount() > 0) {
            c.moveToFirst();
            while(!c.isAfterLast()) {
                String row_title = c.getString(c.getColumnIndex(COLUMN_TITLE));
                String row_date = c.getString(c.getColumnIndex(COLUMN_DATE));
                double row_target = c.getDouble(c.getColumnIndex(COLUMN_TARGET));
                double row_progress = c.getDouble(c.getColumnIndex(COLUMN_PROGRESS));
                String row_unit = c.getString(c.getColumnIndex(COLUMN_UNIT));
                String row_active_state = c.getString(c.getColumnIndex(COLUMN_ACTIVESTATE));

                ActiveState state = Util.toState(row_active_state);
                Unit unit = UnitConverter.toUnit(row_unit);

                try {
                    Goal goal = new Goal(row_title, row_date, 0, row_progress, row_target, unit, state);
                    goals.add(goal);
                } catch (Exception e) {
                    e.printStackTrace();

                }
                c.moveToNext();
            }
        }
        db.close();
        return goals;
    }

    public List<Goal> getGoals(List<String> dates) {
        SQLiteDatabase db = getReadableDatabase();

        List<Goal> goals = new ArrayList<>();
        for(String date : dates) {
            String query = "SELECT * FROM " + TABLE_GOALS + " WHERE " +
                    COLUMN_DATE + "=\"" + date + "\" " + " AND " +
                    COLUMN_ACTIVESTATE + "=\"" + "ACTIVE" + "\";";

            Cursor c = db.rawQuery(query, null);
            int count = c.getCount();

            if (c != null && c.getCount() > 0) {
                c.moveToFirst();
                while (!c.isAfterLast()) {
                    String row_title = c.getString(c.getColumnIndex(COLUMN_TITLE));
                    String row_date = c.getString(c.getColumnIndex(COLUMN_DATE));
                    double row_target = c.getDouble(c.getColumnIndex(COLUMN_TARGET));
                    double row_progress = c.getDouble(c.getColumnIndex(COLUMN_PROGRESS));
                    String row_unit = c.getString(c.getColumnIndex(COLUMN_UNIT));
                    String row_active_state = c.getString(c.getColumnIndex(COLUMN_ACTIVESTATE));

                    ActiveState state = Util.toState(row_active_state);
                    Unit unit = UnitConverter.toUnit(row_unit);

                    try {
                        Goal goal = new Goal(row_title, row_date, 0, row_progress, row_target, unit, state);
                        goals.add(goal);
                    } catch (Exception e) {
                        e.printStackTrace();

                    }
                    c.moveToNext();
                }
            }
        }
        db.close();
        return goals;
    }

    public Goal getActiveGoal(String date) {
        SQLiteDatabase db = getReadableDatabase();
        Goal goal = null;

        String query = "SELECT * FROM " + TABLE_GOALS + " WHERE " +
                COLUMN_DATE + "=\"" + date + "\" " + " AND " +
                COLUMN_ACTIVESTATE + "=\"" + "active" + "\";";

        Cursor c = db.rawQuery(query, null);
        int count = c.getCount();

        if (c != null && c.getCount() == 1) {
            c.moveToFirst();
            while (!c.isAfterLast()) {
                String row_title = c.getString(c.getColumnIndex(COLUMN_TITLE));
                String row_date = c.getString(c.getColumnIndex(COLUMN_DATE));
                double row_target = c.getDouble(c.getColumnIndex(COLUMN_TARGET));
                double row_progress = c.getDouble(c.getColumnIndex(COLUMN_PROGRESS));
                String row_unit = c.getString(c.getColumnIndex(COLUMN_UNIT));
                String row_active_state = c.getString(c.getColumnIndex(COLUMN_ACTIVESTATE));

                ActiveState state = Util.toState(row_active_state);
                Unit unit = UnitConverter.toUnit(row_unit);

                try {
                    goal = new Goal(row_title, row_date, 0, row_progress, row_target, unit, state);
                } catch (Exception e) {
                    e.printStackTrace();

                }
                c.moveToNext();
            }
        }
        db.close();
        return goal;
    }

    public List<Goal> getGoals(String date) {
        SQLiteDatabase db = getReadableDatabase();
        List<Goal> goals = new ArrayList<>();


        String query = "SELECT * FROM " + TABLE_GOALS + " WHERE " +
                COLUMN_DATE + "=\"" + date + "\";";

        Cursor c = db.rawQuery(query, null);
        int count = c.getCount();

        if (c != null && c.getCount() == 1) {
            c.moveToFirst();
            while (!c.isAfterLast()) {
                Goal goal = null;
                String row_title = c.getString(c.getColumnIndex(COLUMN_TITLE));
                String row_date = c.getString(c.getColumnIndex(COLUMN_DATE));
                double row_target = c.getDouble(c.getColumnIndex(COLUMN_TARGET));
                double row_progress = c.getDouble(c.getColumnIndex(COLUMN_PROGRESS));
                String row_unit = c.getString(c.getColumnIndex(COLUMN_UNIT));
                String row_active_state = c.getString(c.getColumnIndex(COLUMN_ACTIVESTATE));

                ActiveState state = Util.toState(row_active_state);
                Unit unit = UnitConverter.toUnit(row_unit);

                try {
                    goal = new Goal(row_title, row_date, 0, row_progress, row_target, unit, state);
                    goals.add(goal);
                } catch (Exception e) {
                    e.printStackTrace();

                }
                c.moveToNext();
            }
        }
        db.close();
        return goals;
    }


    public Map<String, List<Goal>> getAllGoals() {
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_GOALS + "\";";
        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();

        Map<String, List<Goal>> goalsByDate = new HashMap<>();
        List<Goal> goals = new ArrayList<>();
        while (!c.isAfterLast()) {
            c.moveToFirst();
            String row_title = c.getString(c.getColumnIndex(COLUMN_TITLE));
            String row_date = c.getString(c.getColumnIndex(COLUMN_DATE));
            double row_target = c.getDouble(c.getColumnIndex(COLUMN_TARGET));
            double row_progress = c.getDouble(c.getColumnIndex(COLUMN_PROGRESS));
            String row_unit = c.getString(c.getColumnIndex(COLUMN_UNIT));
            String row_active_state = c.getString(c.getColumnIndex(COLUMN_ACTIVESTATE));

            ActiveState state = Util.toState(row_active_state);
            Unit unit = UnitConverter.toUnit(row_unit);

            try {
                Goal goal = new Goal(row_title, row_date, 0, row_progress, row_target, unit, state);
                if(!goalsByDate.containsKey(row_date)) {
                    goalsByDate.put(row_date, new ArrayList<Goal>());
                }
                goalsByDate.get(row_date).add(goal);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        db.close();
        return goalsByDate;
    }

    public void removeAll() {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(DROP_GOALS_TABLE);
        db.execSQL(CREATE_GOALS_TABLE);
        db.close();
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        removeAll();
        onCreate(db);

    }

}
