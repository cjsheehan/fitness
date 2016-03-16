package com.cjsheehan.fitness.activity.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cjsheehan.fitness.R;
import com.cjsheehan.fitness.event.goal.GoalListener;
import com.cjsheehan.fitness.model.Goal;
import com.cjsheehan.fitness.event.date.DateListener;
import com.cjsheehan.fitness.model.Unit;
import com.cjsheehan.fitness.util.Util;

public class ActiveGoalProgressFragment extends BaseFragment implements DateListener, GoalListener {
    private static final String TAG = "ActiveGoalProgressFragment";
    private FloatingActionButton _fab;
    private Context context;
    private RelativeLayout content;
    Context _context;
    private TextView _progressTextView;
    private TextView _targetTextView;
    private TextView _dateTextView;
    private ProgressBar _progressBar;
    private Animation _simpleAnim;
    private SharedPreferences _sharedPreferences;
    double _currentProgress;
    double _currentTarget;
    private Unit _currentUnit = Unit.STEP;
    Goal _activeGoal;
    Activity _activity;

    // MAIN ACTIVITY callback to distribute events
    GoalListener _cbkGoalListener;

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

        setupProgressIndicators(view);
        setupIncrButton(view);
        setupDecrButton(view);
    }

    private void setupProgressIndicators(View view) {
        _progressTextView = (TextView) view.findViewById(R.id.active_goal_current_progress);
        _targetTextView = (TextView) view.findViewById(R.id.active_goal_target);
        _progressBar = (ProgressBar) view.findViewById(R.id.active_goal_progress_bar);
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
            Integer incrementBy = Integer.parseInt(incrementStr);
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
            updateProgressView();
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


    public void updateProgressView() {
        String strProgress, strTarget = null;
        double progress = getProgress();
        double target = getTarget();

        if (_activeGoal.getUnit() == Unit.STEP) {
            strTarget = Util.formatTo0dp(target);
            if (progress <= 0) {
                strProgress = Util.formatTo0dp(0);
            } else {
                strProgress = Util.formatTo0dp(progress);
            }
        } else {
            strTarget = Util.formatTo2dp(target);
            if (progress <= 0) {
                strProgress = Util.formatTo2dp(0);
            } else {
                strProgress = Util.formatTo2dp(progress);
            }
        }

        setTargetTextView(strTarget);
        setProgressTextView(strProgress);

        int progressAsInt = Util.toInt(_activeGoal.getProgress());
        int targetAsInt = Util.toInt(_activeGoal.getTarget());
        _progressBar.setMax(targetAsInt);
        _progressBar.setProgress(progressAsInt);
    }

    private void setTargetTextView(String target) {
        _targetTextView.setText(target);
    }

    private void setProgressTextView(String progress) {
        _progressTextView.setText(progress);
    }

    @Override
    public void addGoal(Goal goal) {

    }

    @Override
    public void removeGoal(Goal goal) {

    }

    @Override
    public void updateGoal(Goal goal) {

    }

    public void editProgressDialog() {

        LinearLayout layout = new LinearLayout(_context);
        layout.setOrientation(LinearLayout.VERTICAL);

        // Progress to Target
        final EditText editProgressText = new EditText(_context);
        editProgressText.setText(_sharedPreferences.getString(getString(R.string.progress_increment_amount), "10"));
        editProgressText.setSelectAllOnFocus(true);
        editProgressText.setRawInputType(Configuration.KEYBOARD_QWERTY);
        layout.addView(editProgressText);

        // Alert
        final AlertDialog.Builder editProgressDialog = new AlertDialog.Builder(_context);

        // Icon
        editProgressDialog.setIcon(R.drawable.ic_icon_walk_green);

        // Setting Dialog Title
        editProgressDialog.setTitle("Add Goal Progress");

        editProgressDialog.setView(layout);
        editProgressDialog.create();

        editProgressDialog.setNegativeButton(android.R.string.cancel,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        InputMethodManager imm = (InputMethodManager) _context.getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(editProgressText.getWindowToken(), 0);
                        dialog.cancel();
                    }
                });

        editProgressDialog.setPositiveButton("Add",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        InputMethodManager imm = (InputMethodManager) _context.getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(editProgressText.getWindowToken(), 0);
                        String strProgressAdded = editProgressText.getText().toString();
                        try {
                            Integer progressAdded = Integer.parseInt(strProgressAdded);
                            incrementProgress(progressAdded);
                            Toast.makeText(_context, "Added " + progressAdded + " to progress", Toast.LENGTH_SHORT).show();
                        } catch (NumberFormatException e) {
                            Toast.makeText(_context, "Input a number please", Toast.LENGTH_SHORT).show();
                            editProgressDialog();
                        }
                    }
                });

        // Showing Alert Message
        editProgressDialog.show();
        editProgressText.requestFocus();
        InputMethodManager imm = (InputMethodManager) _context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_change_unit, menu);
        inflater.inflate(R.menu.menu_add_progress, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_add_progress:
                editProgressDialog();
                return true;

            case R.id.menu_change_unit:
                Toast.makeText(_context, "Change Unit", Toast.LENGTH_SHORT).show();
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
        updateProgressView();
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
        _cbkGoalListener.onGoalProgressChanged(progress); // callback main activity to disrtibute event
    }

}
