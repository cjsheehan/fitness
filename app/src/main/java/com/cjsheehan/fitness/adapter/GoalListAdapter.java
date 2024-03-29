package com.cjsheehan.fitness.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import com.cjsheehan.fitness.R;
import com.cjsheehan.fitness.model.ActiveState;
import com.cjsheehan.fitness.model.Goal;
import com.cjsheehan.fitness.view.GoalView;

public class GoalListAdapter extends ArrayAdapter<Goal> {

    private final static String TAG = "GoalListAdapter";
    private List<Goal> goals;
    protected Context context;
    protected LayoutInflater _inflater;
    Goal _activeGoal;
    GoalView _goalView;
    boolean _showDate;

    public GoalListAdapter(List<Goal> goals, Context context, boolean showDate) {
        super(context, R.layout.goal_list_item);
        this.goals = goals;
        this.context = context;
        _showDate = showDate;
    }


    @Override
    public View getView(int position, View view, ViewGroup parent) {
        _goalView = new GoalView(getContext());
        if (view == null) {
            // This a new view we inflate the new layout
            _inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = _inflater.inflate(R.layout.goal_progress_list_item, parent, false);
            // Now we can fill the layout with the right values
            _goalView.setTitle((TextView) view.findViewById(R.id.goal_title));
            if(_showDate)
                _goalView.setDate((TextView) view.findViewById(R.id.goal_date));
            _goalView.setProgress((TextView) view.findViewById(R.id.goal_progress));
            _goalView.setTarget((TextView) view.findViewById(R.id.goal_target));
            _goalView.setTargetProgress((ProgressBar) view.findViewById(R.id.goal_progress_bar));
            _goalView.setUnit((TextView) view.findViewById(R.id.goal_unit_text));
            view.setTag(_goalView);
        }
        else {
            _goalView = (GoalView) view.getTag();
        }

        Goal active = goals.get(position);

        if (active.getActiveState() == ActiveState.ACTIVE) {
            view.setBackgroundResource(R.color.colorAccentAlternate);
        } else {
            view.setBackgroundResource(Color.TRANSPARENT);
        }

        _goalView.update(active);
        return view;
    }

    @Override
    public int getCount() {
        return goals.size();
    }

    @Override
    public Goal getItem(int position) {
        return goals.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void setGoals(List<Goal> goals) {
        this.goals = goals;
        notifyDataSetChanged();
    }

    public void reset() {
        goals = new ArrayList<>();
    }
}
