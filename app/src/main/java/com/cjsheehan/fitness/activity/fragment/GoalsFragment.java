package com.cjsheehan.fitness.activity.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.cjsheehan.fitness.R;
import com.cjsheehan.fitness.adapter.GoalListAdapter;
import com.cjsheehan.fitness.model.Goal;
import com.cjsheehan.fitness.model.GoalData;
import com.cjsheehan.fitness.model.GoalState;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GoalsFragment extends BaseFragment {
    private static final String TAG = "BaseGoalsFragment";
    private final int LOADER_ID = new Random().nextInt(1000);
    private enum GoalDialog{EDIT, ADD};
    private TextView _editGoalTitle;
    private EditText _editGoalTarget;
    private Context context;
    private GoalData _goalData;
    private GoalListAdapter _goalListAdapter;
    private ListView _goalListView;
    private TextView txtMessage;
    private FloatingActionButton _fab;
    Context _context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_goals, container, false);
        init(view);
        setHasOptionsMenu(true);
        return view;
    }

    @Override
    protected void init(View view) {
        _context = view.getContext();

        // TODO : Need to get goals from repo
        //goalRepository = new GoalRepository(context);
        //txtMessage = (TextView) view.findViewById(R.id.goals_message);

        initGoalData(view);
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

    private void initGoalData(View view) {
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
                        setGoalActive(position);
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
                editGoalDialog(position);
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

    public void addGoalDialog(GoalDialog gd) {
        LinearLayout layout = new LinearLayout(_context);
        layout.setOrientation(LinearLayout.VERTICAL);

        // Goal Name
        final EditText editName = new EditText(_context);
        editName.setHint("Name");
        layout.addView(editName);

        // Goal Target
        final EditText editTarget = new EditText(_context);
        editTarget.setHint("Number of Steps");
        editTarget.setRawInputType(Configuration.KEYBOARD_QWERTY);
        layout.addView(editTarget);

        // Cache
        _editGoalTitle = editName;
        _editGoalTarget = editTarget;

        // Alert
        AlertDialog.Builder addGoalDialog = new AlertDialog.Builder(_context);
        addGoalDialog.setNegativeButton(android.R.string.cancel, null);

        // Icon
        addGoalDialog.setIcon(R.drawable.ic_trophie_green);

        // Setting Dialog Title
        addGoalDialog.setTitle("Add New Goal");

        addGoalDialog.setView(layout);
        addGoalDialog.create();

        addGoalDialog.setPositiveButton("Add",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        addNewGoal();
                    }
                });

        // Showing Alert Message
        addGoalDialog.show();

        // TODO : Resolve problems when trying to keep dialog open

    }

    public boolean isGoalValid(String title, String target) {
        boolean result = true;

        // Check for empty strings
        if (title.isEmpty() || target.isEmpty()) {
            result = false;
            Toast.makeText(_context, "All data must be entered", Toast.LENGTH_SHORT).show();
        }

        // Check numSteps is a number
        String stepsErrMsg = "Number of Steps must be a number greater than zero";
        int numSteps;
        try
        {
            numSteps = Integer.parseInt(target);
            if(numSteps < 1) {
                result = false;
                _editGoalTarget.setText("");
                Toast.makeText(_context, stepsErrMsg, Toast.LENGTH_SHORT).show();
            }
        }
        catch(NumberFormatException nfe)
        {
            result = false;
            Toast.makeText(_context, stepsErrMsg, Toast.LENGTH_SHORT).show();
        }

        return result;
    }

    public void addNewGoal()
    {
        String goalTitle = _editGoalTitle.getText().toString();
        String goalTarget = _editGoalTarget.getText().toString();

        if(isGoalValid(goalTitle, goalTarget)) {
            Goal goal = new Goal(goalTitle, 0, Integer.valueOf(goalTarget), GoalState.INACTIVE);
            _goalData.add(goal);
            _goalListAdapter.notifyDataSetChanged();
            if(_goalData.size() == 1)
            {
                // Make it active
                setGoalActive(0);
            }
            Toast.makeText(_context, "Added Goal", Toast.LENGTH_SHORT).show();

            //try {
            //    dbCreateGoal(new Goal(goalTitle, 0, Integer.parseInt(goalTarget), goalState));
            //}
            //catch(Exception e) {_goalData
            //    int a = 5;
            //    Toast.makeText(getApplicationContext(), "ERROR while attempting to write goal to db" + e.getMessage(), Toast.LENGTH_SHORT).show();
            //}

            return;
        }

        _editGoalTitle.setText("");
        _editGoalTarget.setText("");

        return;
    }

    public void editGoalDialog(final int position)
    {
        if(_goalData.get(position).getGoalState() == GoalState.ACTIVE) {
            Toast.makeText(_context, "Cannot edit active goal, please activate another goal first", Toast.LENGTH_SHORT).show();
            return;
        }

        Goal selectedGoal = _goalData.get(position);

        LinearLayout layout = new LinearLayout(_context);
        layout.setOrientation(LinearLayout.VERTICAL);

        // Goal Name
        final EditText editName = new EditText(_context);
        editName.setText(selectedGoal.getTitle());
        layout.addView(editName);

        // Goal Target
        final EditText editTarget = new EditText(_context);
        editTarget.setText("" + selectedGoal.getTarget());
        editTarget.setRawInputType(Configuration.KEYBOARD_QWERTY);
        layout.addView(editTarget);

        // Cache
        _editGoalTitle = editName;
        _editGoalTarget = editTarget;

        // Alert
        AlertDialog.Builder editGoalDialog = new AlertDialog.Builder(_context);
        editGoalDialog.setNegativeButton(android.R.string.cancel, null);
        editGoalDialog.setNeutralButton(getString(R.string.delete_label),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        removeGoal(position);
                    }
                });
        // Icon
        editGoalDialog.setIcon(R.drawable.ic_trophie_green);

        // Setting Dialog Title
        editGoalDialog.setTitle("Edit Goal");

        editGoalDialog.setView(layout);
        editGoalDialog.create();

        editGoalDialog.setPositiveButton("Edit",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        if (editGoal(position)) {
                            Toast.makeText(_context, "Editing Goal", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        // Showing Alert Message
        editGoalDialog.show();
    }

    public boolean editGoal(int position)
    {
        //TODO : Add to _db
        String goalTitle = _editGoalTitle.getText().toString();
        String goalTarget = _editGoalTarget.getText().toString();

        // Only edit inactive, valid goals
        if(_goalData.get(position).getGoalState() == GoalState.ACTIVE) {
            Toast.makeText(_context, "Cannot edit active goal, please activate another goal first", Toast.LENGTH_SHORT).show();
        }
        else if(isGoalValid(goalTitle, goalTarget)) {
            _goalData.get(position).setTitle(goalTitle);
            _goalData.get(position).setTarget(Integer.parseInt(goalTarget));
            _goalListAdapter.notifyDataSetChanged();
            return true;
        }
        return false;
    }

    public void removeGoal(int position)
    {
        //TODO : Add to _db
        if(_goalData.get(position).getGoalState() == GoalState.ACTIVE)
        {
            Toast.makeText(_context, "Cannot remove active goal, please activate another goal first", Toast.LENGTH_SHORT).show();
        }
        else
        {
            _goalData.remove(position);
            _goalListAdapter.notifyDataSetChanged();
            Toast.makeText(_context, "Goal removed", Toast.LENGTH_SHORT).show();
        }
    }

    public boolean setGoalActive(int position)
    {
        //TODO : Add to _db
        boolean isActive = false;
        if(_goalData.get(position).getGoalState() != GoalState.ACTIVE) {
            if(checkGoalStatesValid() <= 1) {
                Goal activeGoal = _goalData.get(position);
                Toast.makeText(_context, "Setting goal active : " + activeGoal.getTitle(), Toast.LENGTH_SHORT).show();
                _goalData.get(position).setGoalState(GoalState.ACTIVE);
                updateGoalActiveStates(position);
            }
            else {
                Toast.makeText(_context, "Warning: more than 1 goal is active", Toast.LENGTH_SHORT).show();
            }
            isActive = true;
        }
        else {
            Toast.makeText(_context, "Goal is already active", Toast.LENGTH_SHORT).show();
        }

        return isActive;
    }

    private void updateGoalActiveStates(int activeIdx)
    {
        //TODO : Add to _db
        for(int i = 0; i < _goalData.size(); i++)
        {
            // Only 1 goal can be active at a time
            if(i != activeIdx)
                _goalData.get(i).setGoalState(GoalState.INACTIVE);
        }

        if(checkGoalStatesValid() > 1)
            Toast.makeText(_context, "Warning: more than 1 goal is active", Toast.LENGTH_SHORT).show();
    }

    private int checkGoalStatesValid()
    {
        if(_goalData.size() > 0)
        {
            List<Integer> activeIdxs = new ArrayList<>();
            int numActive = 0;
            int activeIdx = 0;
            for(int i = 0; i < _goalData.size(); i++)  {
                if(_goalData.get(i).getGoalState() == GoalState.ACTIVE)
                    activeIdxs .add(i);
            }

            return activeIdxs.size();
        }

        return 0;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_add_goal, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_add_goal:
                addGoalDialog(GoalDialog.ADD);
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }

}
