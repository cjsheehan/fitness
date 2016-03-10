package com.cjsheehan.fitness.activity.fragment;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.cjsheehan.fitness.R;
import com.cjsheehan.fitness.model.Goal;

public class ActiveGoalProgressFragment extends BaseFragment {
    private static final String TAG = "ActiveGoalProgressFragment";
    private Context context;
    private RelativeLayout content;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_active_goal_progress, container, false);
        init(view);
        return view;
    }

    @Override
    protected void init(View view) {
        context = view.getContext();
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
