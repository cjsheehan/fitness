package com.cjsheehan.fitness.activity.fragment;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.cjsheehan.fitness.R;
import com.cjsheehan.fitness.adapter.GoalListAdapter;
import com.cjsheehan.fitness.db.DbGoal;
import com.cjsheehan.fitness.model.Goal;
import com.cjsheehan.fitness.model.GoalData;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GoalsFragment extends BaseFragment {
    private static final String TAG = "BaseGoalsFragment";
    private final int LOADER_ID = new Random().nextInt(1000);

    private Context context;
    private GoalData _goalData;
    private GoalListAdapter _goalListAdapter;
    private ListView _goalListView;
    private TextView txtMessage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_goals, container, false);
        init(view);
        return view;
    }

    @Override
    protected void init(View view) {
        context = view.getContext();

        // TODO : Need to get goals from repo
        //goalRepository = new GoalRepository(context);
        //txtMessage = (TextView) view.findViewById(R.id.goals_message);

        initGoalList(view);
        //initLoader();
        registerReceivers();
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

    private void initGoalList(View view) {
        _goalData = new GoalData(getContext());
        _goalListAdapter = new GoalListAdapter(_goalData.getAll(), getContext());
        _goalListView = (ListView) view.findViewById(R.id.goal_list);

        _goalListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                // Select

                Log.d(TAG, "Goal ListView click");
                for (int i = 0; i < _goalListView.getChildCount(); i++) {
                    if(position == i) {
                        _goalListView.getChildAt(i).setBackgroundResource(R.color.teal100);
                    }else{
                        _goalListView.getChildAt(i).setBackgroundResource(Color.TRANSPARENT);
                    }
                }
                Goal goal = _goalListAdapter.getItem(position);
                Toast.makeText(getContext(), "Selected goal : " + goal.getTitle(), Toast.LENGTH_SHORT).show();
            }
        });
        _goalListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
                // TODO : RESTRICT editing of active goals
                // Edit
                Log.d(TAG, "Goal ListView longclick");
                Goal goal = _goalListAdapter.getItem(position);
                Toast.makeText(getContext(), "Edited goal : " + goal.getTitle(), Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        _goalListView.setAdapter(_goalListAdapter);
    }

    //private void initLoader() {
    //    Log.d(TAG, "### Calling initLoader! ###");
    //    if (getActivity().getSupportLoaderManager().getLoader(LOADER_ID) == null)
    //        Log.d(TAG, "### Initializing a new Loader... ###");
    //    else
    //        Log.d(TAG, "### Reconnecting with existing Loader (id " + LOADER_ID + ")... ###");
    //    getLoaderManager().initLoader(LOADER_ID, null, this).forceLoad();
    //}

    //@Override
    //public Loader<List<Goal>> onCreateLoader(int id, Bundle args) {
    //    Log.d(TAG, "Enetered :  onCreateLoader(int id, Bundle args). Create Loader with id : " + id);
    //    _dbGoal = new DbGoal(context);
    //    _goalListAdapter.setLoader(loader);
    //    return loader;
    //}
    //
    //@Override
    //public void onLoadFinished(Loader<List<Goal>> loader, List<Goal> data) {
    //    _goalListAdapter.setGroups(data);
    //    if (data.isEmpty())
    //        txtMessage.setVisibility(View.VISIBLE);
    //    else
    //        txtMessage.setVisibility(View.GONE);
    //}

    //@Override
    //public void onLoaderReset(Loader<List<Goal>> loader) {
    //    _goalListAdapter.setGroups(new ArrayList<Goal>());
    //}

}
