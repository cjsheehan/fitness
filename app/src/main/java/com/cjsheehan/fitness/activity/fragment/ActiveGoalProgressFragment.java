package com.cjsheehan.fitness.activity.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.cjsheehan.fitness.R;
import com.cjsheehan.fitness.event.date.DateListener;
import com.cjsheehan.fitness.event.goal.GoalListener;
import com.cjsheehan.fitness.model.Goal;
import com.cjsheehan.fitness.model.Unit;
import com.cjsheehan.fitness.model.UnitConverter;
import com.cjsheehan.fitness.util.Util;
import com.cjsheehan.fitness.view.GoalView;

public class ActiveGoalProgressFragment extends BaseFragment implements DateListener, GoalListener {
    private static final String TAG = "ActiveGoalProgressFragment";
    private FloatingActionButton _fab;
    private RelativeLayout content;
    Context _context;
    private TextView _dateTextView;
    private ProgressBar _progressBar;
    private Animation _simpleAnim;
    private SharedPreferences _sharedPreferences;
    Goal _activeGoal;
    Activity _activity;

    // MAIN ACTIVITY callback to distribute events
    GoalListener _cbkGoalListener;
    GoalView _goalView;

    // Add progress via dialog
    private double _progressDistance;
    private Unit _progressUnit;

    enum ProgressChangeDirection { INCREMENT, DECREMENT };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_active_goal_progress, container, false);
        init(view);

        String date = getArguments().getString(getString(R.string.date_bundle_key));
        _dateTextView = (TextView) view.findViewById(R.id.date_display);
        onDateChanged(date);
        setHasOptionsMenu(true);
        return view;
    }


    @Override
    protected void init(View view) {
        _context = view.getContext();
        _sharedPreferences = PreferenceManager.getDefaultSharedPreferences(_context);
        content = (RelativeLayout) view.findViewById(R.id.active_goal_progress_content2);
        _simpleAnim = AnimationUtils.loadAnimation(_context, R.animator.simple_animation);
        _goalView = new GoalView(_context);
        setupGoalView(_goalView, view);
        //setupProgressIndicators(view);
        setupIncrButton(view);
        setupDecrButton(view);
    }

    private void setupGoalView(GoalView gv, View view) {
        gv.setProgress((TextView) view.findViewById(R.id.active_goal_current_progress));
        gv.setTarget((TextView) view.findViewById(R.id.active_goal_target));
        gv.setTargetProgress((ProgressBar) view.findViewById(R.id.active_goal_progress_bar));
        gv.setUnit((TextView) view.findViewById(R.id.active_goal_unit_text));
        gv.setTitle((TextView) view.findViewById(R.id.active_goal_title));
    }

    private void setupIncrButton(View view) {
        ImageView imageViewIncr = (ImageView) view.findViewById(R.id.active_goal_progress_incr);
        imageViewIncr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.startAnimation(_simpleAnim);
                incrementProgress(ProgressChangeDirection.INCREMENT);
            }
        });
    }

    private void setupDecrButton(View view) {
        ImageView imageViewDecr = (ImageView) view.findViewById(R.id.active_goal_progress_decr);
        imageViewDecr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.startAnimation(_simpleAnim);
                incrementProgress(ProgressChangeDirection.DECREMENT);
            }
        });
    }

    public void incrementProgress(ProgressChangeDirection direction) {
        try {
            String incrementStr = _sharedPreferences.getString(getString(R.string.progress_increment_amount), "10");
            Double incrementBy = Double.parseDouble(incrementStr);
            switch (direction) {
                case DECREMENT:
                    incrementProgress(-1 * incrementBy);
                    break;
                case INCREMENT:
                    incrementProgress(incrementBy);
                    break;
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    public void incrementProgress(double progress) {
        setProgress(getProgress() + progress);
    }

    public void setProgress(double progress) {
        if(_activeGoal != null) {
            _activeGoal.setProgress(progress);
            raiseOnGoalProgressChanged(getProgress()); // callback activity
            updateGoalView();
        }
    }

    private double getProgress() {
        if(_activeGoal != null) {
            return _activeGoal.getProgress();
        }
        return 0;
    }

    private double getTarget() {
        if(_activeGoal != null) {
            return _activeGoal.getTarget();
        }
        return 0;
    }

    public void updateGoalView() {
        if(_activeGoal != null && _goalView != null) {
            _goalView.update(_activeGoal);
        }
    }

    public void editProgressDialog() {
        LayoutInflater li = LayoutInflater.from(_context);
        View promptsView = li.inflate(R.layout.edit_progress_dialog, null);
        AlertDialog.Builder editProgressDialog = new AlertDialog.Builder(_context);
        editProgressDialog.setView(promptsView);

        // Progress to Target

        // Title
        EditText addProgressTitleView = (EditText) promptsView.findViewById(R.id.edit_progress_dialog_name);
        addProgressTitleView.setText("" + _activeGoal.getTitle());
        addProgressTitleView.setEnabled(false);

        // Progress
        final EditText editProgressView = (EditText) promptsView.findViewById(R.id.edit_progress_dialog_progress);
        editProgressView.setText(_sharedPreferences.getString(getString(R.string.progress_increment_amount), "10"));
        editProgressView.setSelectAllOnFocus(true);
        editProgressView.setRawInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);

        // Unit
        final Spinner unitSpinner= (Spinner) promptsView.findViewById(R.id.edit_progress_unit_spinner);
        int unitPosition = getPositionFromUnit(_activeGoal.getUnit());
        unitSpinner.setSelection(unitPosition);
        _progressUnit = _activeGoal.getUnit();

        unitSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                ((TextView) parent.getChildAt(0)).setTextColor(ContextCompat.getColor(_context, R.color.colorPrimary));
                _progressUnit = UnitConverter.toUnit(parent.getItemAtPosition(position).toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(_context, "No unit selected", Toast.LENGTH_SHORT).show();
            }

        });



        editProgressDialog.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                InputMethodManager imm = (InputMethodManager) _context.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(editProgressView.getWindowToken(), 0);
                dialog.cancel();
            }

        });

        editProgressDialog.setPositiveButton("Add",new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                InputMethodManager imm = (InputMethodManager) _context.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(editProgressView.getWindowToken(), 0);
                String strProgressAdded = editProgressView.getText().toString();
                try {
                    _progressDistance = Double.parseDouble(strProgressAdded);
                    if(_progressUnit != _activeGoal.getUnit()) {
                        _progressDistance = UnitConverter.convert(_progressDistance, _progressUnit, _activeGoal.getUnit());
                    }

                    incrementProgress(_progressDistance);
                    Toast.makeText(_context, "Added " + Util.formatTo2dp(_progressDistance) + " to progress", Toast.LENGTH_SHORT).show();
                } catch (NumberFormatException e) {
                    Toast.makeText(_context, "Input a number please", Toast.LENGTH_SHORT).show();
                    editProgressDialog();
                }
            }

        });

        editProgressDialog.create();

        // Showing Alert Message
        editProgressDialog.show();

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_add_progress, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_add_progress:
                editProgressDialog();
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    public void onDateChanged(String date) {
        if(_dateTextView != null)
            _dateTextView.setText(date);
    }

    @Override
    public void onActiveGoalChanged(Goal goal) {
        _activeGoal = goal;
        updateGoalView();
    }

    @Override
    public void onGoalProgressChanged(double progress) {
        // Progress updates occur here, no need to handle
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

    protected void raiseOnGoalProgressChanged(double progress) {
        _cbkGoalListener.onGoalProgressChanged(progress); // callback main activity to distribute event
    }

    private int getPositionFromUnit(Unit unit) {
        String[] unit_selectable = getResources().getStringArray(R.array.units_array_values);
        String strUni = UnitConverter.toString(unit);
        int position = -1;
        for (int i = 0; i < unit_selectable.length ; i++) {
            if(strUni.equals(unit_selectable[i])) {
                position = i;
                break;
            }
        }
        return position;
    }

}
