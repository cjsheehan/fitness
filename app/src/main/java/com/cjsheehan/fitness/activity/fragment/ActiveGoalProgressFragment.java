package com.cjsheehan.fitness.activity.fragment;

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
import com.cjsheehan.fitness.model.Goal;

public class ActiveGoalProgressFragment extends BaseFragment {
    private static final String TAG = "ActiveGoalProgressFragment";
    private FloatingActionButton _fab;
    private Context context;
    private RelativeLayout content;
    Context _context;
    private TextView _progressTextView;
    private TextView _targetTextView;
    private ProgressBar _progressBar;
    private Animation _simpleAnim;
    private SharedPreferences _sharedPreferences;
    int _currentProgress;
    int _currentTarget;

    enum ProgressChangeDirection { INCREMENT, DECREMENT };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_active_goal_progress, container, false);
        init(view);
        setHasOptionsMenu(true);
        return view;
    }

    @Override
    protected void init(View view) {
        _context = view.getContext();
        _sharedPreferences = PreferenceManager.getDefaultSharedPreferences(_context);

        _simpleAnim = AnimationUtils.loadAnimation(_context, R.animator.simple_animation);
        setupProgressIndicators(view);
        setupIncrButton(view);
        setupDecrButton(view);
        //initDateText();
        //goalRepository = new GoalRepository(view.getContext());
        //addGoalView = (AddGoalView) view.findViewById(R.id.add_goal_view);
        //
        //goalOfDayName = (TextView) view.findViewById(R.id.goal_of_day_name);
        //editGoalOfDayStepAmount = (EditText) view.findViewById(R.id.goal_of_day_step_amount);
        //goalOfDayStepGoal = (TextView) view.findViewById(R.id.goal_of_day_step_goal);
        //txtEditDate = (TextView) view.findViewById(R.id.edit_date);
        //// Clear focus from EditText
        //getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        //
        content = (RelativeLayout) view.findViewById(R.id.active_goal_progress_content);
        //actionButton = (FloatingActionButton) getActivity().findViewById(R.id.fab);
        //
        //initializeStepCountButtons(view);
        //
        //progressBar = (ProgressBar) view.findViewById(R.id.step_progress_bar);
        //progressBar.setProgress(0);
        //
        //toggleContent(editable);
        //checkForGoalOfDay();
        //
        //registerReceivers();
    }


    private void setupProgressIndicators(View view) {
        _progressTextView = (TextView) view.findViewById(R.id.active_goal_current_progress);
        _targetTextView = (TextView) view.findViewById(R.id.active_goal_target);
        _progressBar = (ProgressBar) view.findViewById(R.id.active_goal_progress_bar);

        setTarget(1000);
        addProgress(10);
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
        String current = _progressTextView.getText().toString();
        _currentProgress = Integer.parseInt(current);
        String incrementStr = _sharedPreferences.getString(getString(R.string.progress_increment_amount), "10");
        Integer incrementBy = Integer.parseInt(incrementStr);
        switch (direction) {
            case DECREMENT:
                _currentProgress -= incrementBy;
                break;
            case INCREMENT:
                _currentProgress += incrementBy;
                break;
        }

        if(_currentProgress <= 0)
            _currentProgress = 0;
        updateProgressView();
    }

    public void addProgress(Integer progress) {
        _currentProgress += progress;
        updateProgressView();
    }

    public void setTarget(Integer target) {
        _currentTarget = target;
        updateProgressView();
    }

    public void updateProgressView() {
        _progressTextView.setText(String.valueOf(_currentProgress));
        _targetTextView.setText(String.valueOf(_currentTarget));
        _progressBar.setMax(_currentTarget);
        _progressBar.setProgress(_currentProgress);
    }


    //@Override
    //protected void addGoalActionReceived(Goal goal) {
    //    Log.d(TAG, "Entered : addGoalActionReceived(Goal goal)");
    //}

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
                            addProgress(progressAdded);
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

}
