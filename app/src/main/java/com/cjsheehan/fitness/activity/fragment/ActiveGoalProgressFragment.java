package com.cjsheehan.fitness.activity.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cjsheehan.fitness.R;
import com.cjsheehan.fitness.model.Goal;

public class ActiveGoalProgressFragment extends BaseFragment {
    private static final String TAG = "ActiveGoalProgressFragment";
    private Context context;
    private RelativeLayout content;
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
        return view;
    }

    @Override
    protected void init(View view) {
        context = view.getContext();
        _sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        _simpleAnim = AnimationUtils.loadAnimation(context, R.animator.simple_animation);
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
        setProgress(10);
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

    public void setProgress(Integer progress) {
        _currentProgress = progress;
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


}
