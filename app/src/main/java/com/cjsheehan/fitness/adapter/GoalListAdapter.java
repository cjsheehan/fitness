package com.cjsheehan.fitness.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import com.cjsheehan.fitness.R;
import com.cjsheehan.fitness.model.Goal;
import com.cjsheehan.fitness.model.Unit;
import com.cjsheehan.fitness.util.Util;

public class GoalListAdapter extends ArrayAdapter<Goal> {

    private final static String TAG = "GoalListAdapter";
    private List<Goal> goals;
    protected Context context;
    protected LayoutInflater _inflater;

    public GoalListAdapter(List<Goal> goals, Context context) {
        super(context, R.layout.goal_list_item);
        this.goals = goals;
        this.context = context;
    }


    @Override
    public View getView(int position, View view, ViewGroup parent) {
        GoalHolder holder = new GoalHolder();
        if (view == null) {
            // This a new view we inflate the new layout
            _inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = _inflater.inflate(R.layout.goal_progress_list_item, parent, false);
            // Now we can fill the layout with the right values
            holder.titleTV = (TextView) view.findViewById(R.id.goal_title);
            holder.progressTV = (TextView) view.findViewById(R.id.goal_progress);
            holder.targetTV = (TextView) view.findViewById(R.id.goal_target);
            holder.goalProgressBar = (ProgressBar) view.findViewById(R.id.goal_progress_bar);
            holder.unitTV = (TextView) view.findViewById(R.id.goal_unit_text);
            view.setTag(holder);
        }
        else {
            holder = (GoalHolder) view.getTag();
        }

        Goal goal = goals.get(position);
        holder.titleTV.setText(goal.getTitle());
        holder.targetTV.setText(Integer.toString(goal.getTarget()));
        holder.progressTV.setText(Integer.toString(goal.getProgress()));
        holder.unitTV.setText("MLS");
        holder.goalProgressBar.setProgress(goal.getProgress()/ goal.getTarget());
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

    private static class GoalHolder {
        public TextView titleTV;
        public TextView targetTV;
        public TextView progressTV;
        public TextView unitTV;
        public ProgressBar goalProgressBar;
    }

}
