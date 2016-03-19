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
import com.cjsheehan.fitness.model.UnitConversion;
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


    public DBHelper(Context context) throws IOException {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        String table_query = "CREATE TABLE IF NOT EXISTS " + TABLE_GOALS + "(" +
                COLUMN_TITLE  + " TEXT NOT NULL, " +
                COLUMN_DATE + " TEXT NOT NULL, " +
                COLUMN_TARGET + " REAL, " +
                COLUMN_PROGRESS + " REAL, " +
                COLUMN_UNIT + " TEXT, " +
                COLUMN_ACTIVESTATE + " TEXT, " +
                "PRIMARY KEY( " + COLUMN_TITLE + ", " + COLUMN_DATE + ")" +
                ");";

        try {
            db.execSQL(table_query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //Add new goal to db
    public void insertGoal(Goal goal) {
        String strUnit =  UnitConversion.toString(goal.getUnit());
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
    }

    //Add new goal to db
    public void updateGoal(Goal goal) {
        String strUnit =  UnitConversion.toString(goal.getUnit());
        String strState = goal.getActiveState().toString();

        // Retrieve goal with matching date and name
        SQLiteDatabase db = getWritableDatabase();
        String date = goal.getDate();
        String title = goal.getTitle();
        String query = "SELECT * FROM " + TABLE_GOALS + " WHERE " +
                COLUMN_DATE + "=\"" + date +
                COLUMN_TITLE + "=\"" + title + "\";" ;
        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();

        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, goal.getTitle());
        values.put(COLUMN_DATE, goal.getDate());
        values.put(COLUMN_TARGET, goal.getTarget());
        values.put(COLUMN_PROGRESS, goal.getProgress());
        values.put(COLUMN_UNIT, strUnit);
        values.put(COLUMN_ACTIVESTATE, strState);

        db.insert(TABLE_GOALS, null, values);
        db.close();
    }

    public List<Goal> getGoalsByDate(String date) {
        SQLiteDatabase db = getWritableDatabase();
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
                Unit unit = UnitConversion.toUnit(row_unit);

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

    public Map<String, List<Goal>> getAllGoals() {
        SQLiteDatabase db = getWritableDatabase();
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
            Unit unit = UnitConversion.toUnit(row_unit);

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
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_GOALS + "\";");
        db.close();
        onCreate(db);
    }



//    //Deletes a goal from the db
//    public void deleteGoal(String goalName){
//        SQLiteDatabase db = getWritableDatabase();
//        db.execSQL("DELETE FROM " + TABLE_GOALS + " WHERE " + COLUMN_TITLE + "=\"" + goalName + "\";");
//        db.close();
//    }
//

    //
    //public List<Goal> GetAllGoals(){
    //
    //    List<Goal> goals = new ArrayList<>();
    //
    //    SQLiteDatabase db = getWritableDatabase();
    //    String query = "SELECT * FROM " + TABLE_GOALS;
    //
    //    Cursor c = db.rawQuery(query, null);
    //    c.moveToFirst();
    //
    //    while(!c.isAfterLast()){
    //        String name = c.getString(c.getColumnIndex(COLUMN_TITLE ));
    //        double steps = c.getDouble(c.getColumnIndex(COLUMN_STEPS));
    //        boolean isHistorical =  (1==c.getInt(c.getColumnIndex(COLUMN_HISTORICAL))) ? true : false;
    //        int unit = c.getInt(c.getColumnIndex(COLUMN_UNIT));
    //        goals.add(new Goal(name,steps, isHistorical, Unit.getById(unit) ));
    //        c.moveToNext();
    //    }
    //    db.close();
    //    return goals;
    //}
    //
    //public List<Goal> getCurrentGoals(){
    //
    //    List<Goal> goals = new ArrayList<>();
    //
    //    SQLiteDatabase db = getWritableDatabase();
    //    String query = "SELECT * FROM " + TABLE_GOALS + " WHERE " + COLUMN_HISTORICAL + "=\" 0 \";";
    //
    //    Cursor c = db.rawQuery(query, null);
    //    c.moveToFirst();
    //
    //    while(!c.isAfterLast()){
    //        String name = c.getString(c.getColumnIndex(COLUMN_TITLE ));
    //        double steps = c.getDouble(c.getColumnIndex(COLUMN_STEPS));
    //        boolean isHistorical =  (1==c.getInt(c.getColumnIndex(COLUMN_HISTORICAL))) ? true : false;
    //        int unit = c.getInt(c.getColumnIndex(COLUMN_UNIT));
    //        goals.add(new Goal(name,steps, isHistorical, Unit.getById(unit) ));
    //        c.moveToNext();
    //    }
    //    db.close();
    //    return goals;
    //}

    //public Goal getGoalByName(String name) {
    //
    //
    //    SQLiteDatabase db = getWritableDatabase();
    //    String query = "SELECT * FROM " + TABLE_GOALS + " WHERE " + COLUMN_TITLE  + "=\"" + name + "\";";
    //
    //    Cursor c = db.rawQuery(query, null);
    //    c.moveToFirst();
    //
    //    name = c.getString(c.getColumnIndex(COLUMN_TITLE ));
    //    double steps = c.getDouble(c.getColumnIndex(COLUMN_STEPS));
    //    boolean isHistorical = (1 == c.getInt(c.getColumnIndex(COLUMN_HISTORICAL))) ? true : false;
    //    int unit = c.getInt(c.getColumnIndex(COLUMN_UNIT));
    //    db.close();
    //
    //    return new Goal(name, steps, isHistorical, Unit.getById(unit));
    //
    //}
    //



    //Adds a new goal to the db
    //public void insertUpdateRecord(Record record){
    //    ContentValues values = new ContentValues();
    //    values.put(COLUMN_DATE, record.getDate().getTime());
    //    values.put(COLUMN_PROGRESS, record.getStepsCompleted());
    //    values.put(COLUMN_ACTIVESTATE, record.getActiveGoal().getName());
    //
    //    SQLiteDatabase db = getWritableDatabase();
    //    db.replace(TABLE_DAYS, null, values);
    //    db.close();
    //}
    //




    //public List<Record> getAllRecords() {
    //    SQLiteDatabase db = getWritableDatabase();
    //    String query = "SELECT * FROM " + TABLE_DAYS + " ORDER BY date(" + COLUMN_DATE+ ") DESC;";
    //    List<Record> records = new ArrayList<>();
    //
    //    Cursor c = db.rawQuery(query, null);
    //
    //    if(c!=null && c.getCount()>0) {
    //        c.moveToFirst();
    //
    //        while(!c.isAfterLast()){
    //            long dateInMsec = c.getLong(c.getColumnIndex(COLUMN_DATE));
    //            Date date = convertToDate(dateInMsec);
    //            int steps = c.getInt(c.getColumnIndex(COLUMN_PROGRESS));
    //            String activeGoalName = c.getString(c.getColumnIndex(COLUMN_ACTIVESTATE));
    //
    //            Goal goal = getGoalByName(activeGoalName);
    //
    //            records.add(new Record(date,steps,goal));
    //            c.moveToNext();
    //        }
    //    }
    //    db.close();
    //    return records;
    //}

    //private Date convertToDate(long dateInMsec)  {
    //    DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
    //    Date date = new Date(dateInMsec);
    //    try {
    //        date = dateFormat.parse(dateFormat.format(date));
    //    } catch (ParseException e) {
    //        e.printStackTrace();
    //    }
    //    return date;
    //}

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        removeAll();
        onCreate(db);

    }

}
