package com.cjsheehan.fitness.activity.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DialogTitle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.cjsheehan.fitness.R;
import com.cjsheehan.fitness.adapter.GoalListAdapter;
import com.cjsheehan.fitness.db.DbStatus;
import com.cjsheehan.fitness.event.date.DateListener;
import com.cjsheehan.fitness.event.goal.GoalListener;
import com.cjsheehan.fitness.model.ActiveState;
import com.cjsheehan.fitness.model.Goal;
import com.cjsheehan.fitness.model.GoalData;
import com.cjsheehan.fitness.model.Unit;
import com.cjsheehan.fitness.model.UnitConverter;
import com.cjsheehan.fitness.util.GoalValidation;
import com.cjsheehan.fitness.util.GoalValidationCode;
import com.cjsheehan.fitness.util.Util;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import java.io.IOException;

public class GoalsFragment extends BaseFragment implements DateListener, GoalListener {


    private static final String TAG = "BaseGoalsFragment";
    // Vars for adding new goal
    private TextView _agTitleView;
    private EditText _agTargetView;
    String _agTitleStr;
    String _agTargetStr;
    private Unit _agUnit = Unit.STEP;

    // Vars for editing existing goal
    private TextView _ugTitleView;
    private EditText _ugTargetView;
    String _ugTitleStr;
    String _ugTargetStr;
    private Unit _ugUnit = Unit.STEP;
    private int _ugPos = -1;

    // Goal handlers
    private GoalData _goalData;
    private GoalListAdapter _goalListAdapter;
    private ListView _goalListView;

    Context _context;
    private SharedPreferences _sharedPreferences;
    private SharedPreferences.OnSharedPreferenceChangeListener _settingsListener;
    private TextView _dateTextView;
    Activity _activity;

    // Callback when view is updated
    GoalListener _cbkGoalListener;
    private String _date;

    View _view; // Cached in onCreateView()

    String _strLength;

    public void selectActiveGoal() {
        if (_goalData != null) {
            int activeIdx = _goalData.getActiveIdx();
            if (activeIdx >= 0) {
                _goalListView.performItemClick(_goalListView.getChildAt(activeIdx), activeIdx, _goalListAdapter.getItemId(activeIdx));
                //_goalListAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_goals, container, false);
        _view = view;
        _dateTextView = (TextView) view.findViewById(R.id.date_display);
        _date = getArguments().getString(getString(R.string.date_bundle_key));
        init(_view);
        onDateChanged(_date);
        setHasOptionsMenu(true);
        initStrideLength();
        return view;
    }

    @Override
    protected void init(View view) {
        _context = view.getContext();
        _sharedPreferences = PreferenceManager.getDefaultSharedPreferences(_context);
        _dateTextView = (TextView) view.findViewById(R.id.date_display);

        _goalListView = (ListView) _view.findViewById(R.id.goal_list);

        _goalListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                // Select
                Log.d(TAG, "Goal ListView click");
                int count = _goalListView.getCount();
                for (int i = 0; i < count; i++) {
                    if (position == i) {
                        setGoalActive(position);
                        //_goalListView.getChildAt(i).setBackgroundResource(R.color.colorAccentAlternate);
                    } else {
                        //_goalListView.getChildAt(i).setBackgroundResource(Color.TRANSPARENT);
                    }
                }
            }
        });

        _goalListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
                if (true == _sharedPreferences.getBoolean(getString(R.string.enable_edit_goal_key), false)) {
                    //editGoalDialog(position);
                    updateGoalDialog(position);
                } else {
                    Toast.makeText(_context, "To edit goals, please enable the feature in settings", Toast.LENGTH_SHORT).show();
                }
                return true;
            }
        });
        setupSharedPreferences();
    }

    private void initGoalsForDate(String date) throws IOException {
        // Get goal data for single date
        _goalData = new GoalData(_context, date);
        List<Goal> all = _goalData.getAll();
        _goalListAdapter = new GoalListAdapter(all, getContext(), false);
        _goalListView.setAdapter(_goalListAdapter);
        _goalListAdapter.notifyDataSetChanged();
        int childCount = _goalListView.getCount();
        updateActiveView();
        //fillGoalData();
    }

    private void updateActiveView() {
        if (_goalData.size() == 0) {
            _goalData.add(Util.getDefaultGoal(_date));
            updateActiveView();
        } else if (_goalData.size() == 1) {
            if (_goalData.countActive() == 0) {
                _goalData.setActive(0);
            }
            _goalListView.performItemClick(
                    _goalListView.getChildAt(0), 0, _goalListAdapter.getItemId(0));
            _goalListAdapter.notifyDataSetChanged();
            raiseActiveGoalChanged(_goalData.getActive());
        } else if (_goalData.size() > 1) {
            int numActive = _goalData.countActive();
            if (numActive == 0) {
                setGoalActive(0);
            } else if (numActive == 1) {
                int activeIdx = _goalData.getActiveIdx();
                setGoalActive(activeIdx);
            } else if (numActive > 1) {
                List<Goal> goals = _goalData.getAll();
                for (Goal g : goals) {
                    g.setActiveState(ActiveState.INACTIVE);
                }
                _goalData.setActive(0);

                if (_goalData.countActive() > 1) {
                    Toast.makeText(_context, "ERROR : too many active goals, contact admin", Toast.LENGTH_SHORT).show();
                }
            }
            //int activeIdx = _goalData.getActiveIdx();
            //if (activeIdx >= 0) {
            //    _goalListView.performItemClick(
            //            _goalListView.getChildAt(activeIdx), activeIdx, _goalListAdapter.getItemId(activeIdx));
            //    _goalListAdapter.notifyDataSetChanged();
            //    raiseActiveGoalChanged(_goalData.getActive());
            //} else {
            //    Toast.makeText(_context, "ERROR : problem with active goals, contact admin", Toast.LENGTH_SHORT).show();
            //}
        }
    }


    public void addGoalDialog() {
        LayoutInflater li = LayoutInflater.from(_context);
        View promptsView = li.inflate(R.layout.goal_alert_dialog, null);
        AlertDialog.Builder addGoalDialog = new AlertDialog.Builder(_context);
        addGoalDialog.setView(promptsView);

        // Initialise the dialog widgets
        DialogTitle newGoalDialogTitle = (DialogTitle) promptsView.findViewById(R.id.goal_alert_title);
        newGoalDialogTitle.setText("Add new goal");
        _agTitleView = (EditText) promptsView.findViewById(R.id.goal_alert_name);
        //_agTitleView.requestFocus();
        //InputMethodManager imm = (InputMethodManager) _context.getSystemService(Context.INPUT_METHOD_SERVICE);
        //imm.showSoftInput(_agTitleView, InputMethodManager.SHOW_IMPLICIT);

        _agTargetView = (EditText) promptsView.findViewById(R.id.goal_alert_target);
        _agTargetView.setRawInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);

        final Spinner unitSpinner = (Spinner) promptsView.findViewById(R.id.goal_unit_spinner);

        // Cache
        _agTitleStr = _agTitleView.getText().toString();
        _agTargetStr = _agTargetView.getText().toString();

        addGoalDialog.setNegativeButton(android.R.string.cancel, null);
        addGoalDialog.setPositiveButton("Add",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        addGoalFromDialog();
                    }
                });

        final AlertDialog alertDialog = addGoalDialog.create();

        unitSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                ((TextView) parent.getChildAt(0)).setTextColor(ContextCompat.getColor(_context, R.color.colorPrimary));
                _agUnit = UnitConverter.toUnit(parent.getItemAtPosition(position).toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(_context, "No unit selected", Toast.LENGTH_SHORT).show();
            }

        });

        alertDialog.show();
    }

    public GoalValidationCode isGoalValid(String title, String target) {
        GoalValidationCode validationCode = GoalValidation.checkInput(title, target);
        switch (validationCode) {
            case FAIL_TITLE_IS_EMPTY:
            case FAIL_TARGET_IS_EMPTY:
                Toast.makeText(_context, "All data must be entered", Toast.LENGTH_SHORT).show();
                break;
            case FAIL_TARGET_NAN:
                Toast.makeText(_context, "Target must be a number", Toast.LENGTH_SHORT).show();
                break;
            case FAIL_TARGET_LTE0:
                Toast.makeText(_context, "Target must be greater than 1", Toast.LENGTH_SHORT).show();
                break;
        }

        return validationCode;
    }

    public void updateGoalDialog(final int position) {
        if (_goalData.get(position).getActiveState() == ActiveState.ACTIVE) {
            Toast.makeText(_context, "Cannot edit active goal, please activate another goal first", Toast.LENGTH_SHORT).show();
            return;
        }

        Goal goalToEdit = _goalData.get(position);

        LayoutInflater li = LayoutInflater.from(_context);
        View promptsView = li.inflate(R.layout.goal_alert_dialog, null);
        AlertDialog.Builder updateGoalDialog = new AlertDialog.Builder(_context);
        updateGoalDialog.setView(promptsView);

        // Initialise the dialog widgets
        DialogTitle updateGoalDialogTitle = (DialogTitle) promptsView.findViewById(R.id.goal_alert_title);
        updateGoalDialogTitle.setText("Edit Goal");
        _ugTitleView = (EditText) promptsView.findViewById(R.id.goal_alert_name);
        _ugTitleView.setText("" + goalToEdit.getTitle());
        _ugTitleView.setEnabled(false);
        _ugTargetView = (EditText) promptsView.findViewById(R.id.goal_alert_target);
        _ugTargetView.setText("" + goalToEdit.getTarget());
        _ugTargetView.setRawInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        final Spinner unitSpinner = (Spinner) promptsView.findViewById(R.id.goal_unit_spinner);
        int unitPosition = getPositionFromUnit(goalToEdit.getUnit());
        unitSpinner.setSelection(unitPosition);

        // Cache data from view
        _ugTitleStr = _ugTitleView.getText().toString();
        _ugTargetStr = _ugTargetView.getText().toString();
        _ugUnit = goalToEdit.getUnit();

        updateGoalDialog.setNegativeButton(android.R.string.cancel, null);
        updateGoalDialog.setNeutralButton(getString(R.string.delete_label),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        removeGoal(position);
                    }
                });
        updateGoalDialog.setPositiveButton("Edit",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        if (updateGoalFromDialog(position)) {
                            Toast.makeText(_context, "Edited Goal", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        final AlertDialog alertDialog = updateGoalDialog.create();

        unitSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                ((TextView) parent.getChildAt(0)).setTextColor(ContextCompat.getColor(_context, R.color.colorPrimary));
                _ugUnit = UnitConverter.toUnit(parent.getItemAtPosition(position).toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(_context, "No unit selected", Toast.LENGTH_SHORT).show();
            }

        });

        // Showing Alert Message
        updateGoalDialog.show();
    }

    private int getPositionFromUnit(Unit unit) {
        String[] unit_selectable = getResources().getStringArray(R.array.units_array_values);
        String strUni = UnitConverter.toString(unit);
        int position = -1;
        for (int i = 0; i < unit_selectable.length; i++) {
            if (strUni.equals(unit_selectable[i])) {
                position = i;
                break;
            }
        }
        return position;
    }
    //
    //private Unit getUnitFromPosition(int position) {
    //    String[] unit_selectable = getResources().getStringArray(R.array.units_array_values);
    //
    //    Unit unit = null;
    //    for (int i = 0; i < unit_selectable.length ; i++) {
    //
    //        if(strUni.equals()) {
    //            unit = UnitConversion.toUnit(unit_selectable[i]);
    //            break;
    //        }
    //    }
    //    return position;
    //}

    //public boolean addGoal() {
    //    String strTitle = _agTitleView.getText().toString();
    //    String strTarget = _agTargetView.getText().toString();
    //
    //    // Only edit inactive, valid goals
    //    if (isGoalValid(strTitle, strTarget) == GoalValidationCode.OK) {
    //        Unit unit = _agUnit;
    //        ActiveState state = ActiveState.INACTIVE;
    //        double target = Double.parseDouble(strTarget);
    //        double progress = 0.0;
    //        Goal newGoal = new Goal(strTitle, 0, progress, target, unit, state);
    //        _goalData.add(newGoal);
    //        _goalListAdapter.notifyDataSetChanged();
    //        return true;
    //    }
    //    return false;
    //}


    private boolean updateGoalFromDialog(int position) {
        _ugTargetStr = _ugTargetView.getText().toString();

        // Only edit inactive, valid goals
        if (_goalData.get(position).getActiveState() == ActiveState.ACTIVE) {
            Toast.makeText(_context, "Cannot edit active goal, please activate another goal first", Toast.LENGTH_SHORT).show();
        } else if (isGoalValid(_ugTitleStr, _ugTargetStr) == GoalValidationCode.OK) {
            _ugPos = position;
            _goalData.get(position).setTitle(_ugTitleStr);
            _goalData.get(position).setTarget(Double.parseDouble(_ugTargetStr));
            _goalData.get(position).setUnit(_ugUnit);
            _goalListAdapter.notifyDataSetChanged();
            _ugPos = -1;
            return true;
        }
        return false;
    }

    public void addGoalFromDialog() {
        _agTitleStr = _agTitleView.getText().toString();
        _agTargetStr = _agTargetView.getText().toString();
        if (isGoalValid(_agTitleStr, _agTargetStr) == GoalValidationCode.OK) {
            addGoal(_agTitleStr, _date, Double.parseDouble(_agTargetStr), _agUnit);
            return;
        }

        _agTitleView.setText("");
        _agTargetView.setText("");
    }

    public void addGoal(String title, String date, double target, Unit unit) {
        Goal goal = new Goal(title, date, 0, 0, target, unit, ActiveState.INACTIVE);
        DbStatus status = _goalData.add(goal);
        if (DbStatus.OK == status) {
            updateActiveView();
            _goalListAdapter.notifyDataSetChanged();
            Toast.makeText(_context, "Added " + goal.getTitle(), Toast.LENGTH_SHORT).show();
        } else {
            handleDbStatus("ERROR : Failed to add goal", status);
        }

    }

    private void handleDbStatus(String msg, DbStatus status) {
        switch (status) {
            case FAIL_GOAL_EXISTS:
                msg += ", goal already exists";
                Log.d(TAG, msg);
                Toast.makeText(_context, msg, Toast.LENGTH_SHORT).show();
        }
    }

    public void testAddGoals() {
        if (isGoalValid(_agTitleStr, _agTargetStr) == GoalValidationCode.OK) {
            Goal goal = new Goal(_agTitleStr, _date, 0, 0, Double.parseDouble(_agTargetStr), _agUnit, ActiveState.INACTIVE);
            _goalData.add(goal);
            _goalListAdapter.notifyDataSetChanged();
            if (_goalData.size() == 1) {
                setGoalActive(0);
            }
            Toast.makeText(_context, "Added " + goal.getTitle(), Toast.LENGTH_SHORT).show();
            return;
        }

        _agTitleView.setText("");
        _agTargetView.setText("");
    }

    private void fillGoalData() {
        Goal[] goalData = new Goal[]{
                new Goal("High", _date, 0, 0, 20000, Unit.STEP, ActiveState.ACTIVE),
                new Goal("High", _date, 0, 0, 3, Unit.MILE, ActiveState.INACTIVE),
                //new Goal("Low", _dateFrom, 0, 0, 10.5, Unit.KILOMETRE, ActiveState.INACTIVE),
        };

        for (Goal g : goalData) {
            _agTitleStr = g.getTitle();
            _agTargetStr = Util.format(g.getTarget());
            _agUnit = g.getUnit();
            testAddGoals();
        }
    }

    public void removeGoal(int position) {
        if (_goalData.get(position).getActiveState() == ActiveState.ACTIVE) {
            Toast.makeText(_context, "Cannot remove active goal, please activate another goal first", Toast.LENGTH_SHORT).show();
        } else {
            _goalData.remove(position);
            _goalListAdapter.notifyDataSetChanged();
            Toast.makeText(_context, "Goal removed", Toast.LENGTH_SHORT).show();
        }
    }

    public void setGoalActive(int position) {
        if (_goalData.get(position).getActiveState() != ActiveState.ACTIVE) {
            transferProgress(_goalData.getActive(), _goalData.get(position));
            _goalData.setActive(position);
            _goalListAdapter.notifyDataSetChanged();
            raiseActiveGoalChanged(_goalData.getActive());
            Toast.makeText(_context, _goalData.getActive().getTitle() + " is now active", Toast.LENGTH_SHORT).show();
        } else {
            _goalListAdapter.notifyDataSetChanged();
            raiseActiveGoalChanged(_goalData.getActive());
        }
    }

    private void transferProgress(Goal source, Goal target) {
        if (source != null && target != null) {
            double sourceProgress = source.getProgress();
            Unit sourceUnit = source.getUnit();
            Unit targetUnit = target.getUnit();
            double targetProgress = UnitConverter.convert(sourceProgress, sourceUnit, targetUnit);
            source.setProgress(0);
            _goalData.update(source);
            target.setProgress(target.getProgress() + targetProgress);
            _goalData.update(target);
        }
    }

    private void initStrideLength() {
        _strLength = _sharedPreferences.getString(getResources().getString(R.string.user_stride_length_key), "1");
        double length = Double.parseDouble(_strLength);
        UnitConverter.setMetresPerStep(length);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_add_goal, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_add_goal:
                addGoalDialog();
                return true;

            case R.id.random_populate:
                populateDb();
                return true;

            case R.id.delete_history:
                deleteAllHistory();
                _date = Util.getDateToday();
                Toast.makeText(_context, "Deleted all history", Toast.LENGTH_SHORT).show();
                try {
                    initGoalsForDate(_date);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onDateChanged(String date) {
        _date = date;
        if (_dateTextView != null) {
            _dateTextView.setText(_date);
        }
        try {
            initGoalsForDate(_date);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void populateDb() {
        String endDate = Util.getDateToday();
        List<String> dates = Util.getDates(endDate, 50, Util.Order.FORWARD);
        List<Goal> newGoals = new ArrayList<>();
        for (String date : dates) {
            List<Goal> newGoalsForDate = Util.genGoals(date, Util.RNG.nextInt(4) + 1);
            newGoals.addAll(newGoalsForDate);
        }
        _goalData.populate(newGoals);

        try {
            initGoalsForDate(_date);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActiveGoalChanged(Goal goal) {
        // Not needed as active goals are changed locally and emitted to others
    }

    @Override
    public void onGoalProgressChanged(double progress) {
        if (_goalData != null) {
            int idx = _goalData.getActiveIdx();
            _goalData.updateProgress(progress, idx);
            _goalData.getActive().setProgress(progress);
            // Update view
            _goalListAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        _activity = getActivity();

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            _cbkGoalListener = (GoalListener) _activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(_activity.toString()
                    + " must implement OnActiveGoalChangedListener");
        }
    }

    protected void raiseActiveGoalChanged(Goal goal) {
        _cbkGoalListener.onActiveGoalChanged(goal); // callback main activity to disrtibute event
    }

    private void setupSharedPreferences() {
        _settingsListener = new OnSettingsChangedListener();
        _sharedPreferences.registerOnSharedPreferenceChangeListener(_settingsListener);
    }

    private void deleteAllHistory() {
        _goalData.deleteAllHistory();
    }

    private class OnSettingsChangedListener implements SharedPreferences.OnSharedPreferenceChangeListener {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences spref, String key) {
            if (key.equals(getString(R.string.user_stride_length_key))) {
                initStrideLength();
                Toast.makeText(_context, "Stride length set to " + _strLength, Toast.LENGTH_SHORT).show();

            }
        }
    }


}
