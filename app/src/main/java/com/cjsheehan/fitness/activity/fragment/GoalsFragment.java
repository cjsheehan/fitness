package com.cjsheehan.fitness.activity.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DialogTitle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.cjsheehan.fitness.R;
import com.cjsheehan.fitness.adapter.GoalListAdapter;
import com.cjsheehan.fitness.event.date.DateListener;
import com.cjsheehan.fitness.event.goal.GoalListener;
import com.cjsheehan.fitness.model.ActiveState;
import com.cjsheehan.fitness.model.Goal;
import com.cjsheehan.fitness.model.GoalData;
import com.cjsheehan.fitness.model.Unit;
import com.cjsheehan.fitness.model.UnitConversion;
import com.cjsheehan.fitness.util.GoalValidation;
import com.cjsheehan.fitness.util.GoalValidationCode;

public class GoalsFragment extends BaseFragment implements DateListener, GoalListener {


    private static final String TAG = "BaseGoalsFragment";
    // Vars for adding new goal
    private TextView _newGoalTitle;
    private EditText _newGoalTarget;
    private Unit _newGoalUnit = Unit.STEP;

    // Vars for editing existing goal
    private TextView _editExistingGoalTitle;
    private EditText _editExistingGoalTarget;
    private Unit _editExistingGoalUnit = Unit.STEP;
    private int _positionOfGoalToEdit = -1;


    private Context context;
    private GoalData _goalData;
    private GoalListAdapter _goalListAdapter;
    private ListView _goalListView;
    private TextView txtMessage;
    private FloatingActionButton _fab;
    Context _context;
    private SharedPreferences _sharedPreferences;
    private TextView _dateTextView;
    Activity _activity;


    GoalListener _cbkGoalListener;

    // Container Activity must implement this interface
    public interface OnActiveGoalChangedListener {
        public void onActiveGoalChanged(int position);
    }

    public void selectActiveGoal() {
        if(_goalData != null) {
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
        _dateTextView = (TextView) view.findViewById(R.id.date_display);
        String date = getArguments().getString(getString(R.string.date_bundle_key));
        onDateChanged(date);
        init(view);
        setHasOptionsMenu(true);
        return view;
    }

    @Override
    protected void init(View view) {
        _context = view.getContext();
        _sharedPreferences = PreferenceManager.getDefaultSharedPreferences(_context);
        _dateTextView = (TextView) view.findViewById(R.id.date_display);
        // TODO : Need to get goals from repo
        //goalRepository = new GoalRepository(context);
        //txtMessage = (TextView) view.findViewById(R.id.goals_message);

        initGoalData(view);
        //initLoader();
        //registerReceivers();
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
            int childCount = _goalListView.getChildCount();
            for (int i = 0; i < childCount; i++) {
                if (position == i) {
                    setGoalActive(position);
                    _goalListView.getChildAt(i).setBackgroundResource(R.color.colorAccentAlternate);
                } else {
                    _goalListView.getChildAt(i).setBackgroundResource(Color.TRANSPARENT);
                }
            }
            Goal goal = _goalListAdapter.getItem(position);
        }
        });

        _goalListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
                if (true == _sharedPreferences.getBoolean(getString(R.string.enable_edit_goal_key), false)) {
                    //editGoalDialog(position);
                    editGoalDialogWithUnit(position);
                } else {
                    Toast.makeText(_context, "To edit goals, please enable the feature in settings", Toast.LENGTH_SHORT).show();
                }
                return true;
            }
        });

        _goalListView.setAdapter(_goalListAdapter);
    }

    public void addGoalDialog() {
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
        _newGoalTitle = editName;
        _newGoalTarget = editTarget;

        AlertDialog.Builder addGoalDialog = new AlertDialog.Builder(_context);
        addGoalDialog.setNegativeButton(android.R.string.cancel, null);
        addGoalDialog.setIcon(R.drawable.ic_trophie_green);
        addGoalDialog.setTitle("Add New Goal");
        addGoalDialog.setView(layout);
        addGoalDialog.create();

        addGoalDialog.setPositiveButton("Add",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        addNewGoal();
                    }
                });

        addGoalDialog.show();
    }

    public void addGoalDialogWithUnit() {
        LayoutInflater li = LayoutInflater.from(_context);
        View promptsView = li.inflate(R.layout.goal_alert_dialog, null);
        AlertDialog.Builder addGoalDialog = new AlertDialog.Builder(_context);
        addGoalDialog.setView(promptsView);

        // Initialise the dialog widgets
        DialogTitle newGoalDialogTitle = (DialogTitle) promptsView.findViewById(R.id.goal_alert_title);
        newGoalDialogTitle.setText("Edit Goal");
        final EditText newGoalTitle = (EditText) promptsView.findViewById(R.id.goal_alert_name);
        final EditText newGoalTarget = (EditText) promptsView.findViewById(R.id.goal_alert_target);
        final Spinner unitSpinner= (Spinner) promptsView.findViewById(R.id.goal_unit_spinner);
        newGoalTarget.setRawInputType(Configuration.KEYBOARD_QWERTY);

        // Cache
        _newGoalTitle = newGoalTitle;
        _newGoalTarget = newGoalTarget;

        addGoalDialog.setNegativeButton(android.R.string.cancel, null);
        addGoalDialog.setPositiveButton("Add",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        addNewGoal();
                    }
                });

        final AlertDialog alertDialog = addGoalDialog.create();

        unitSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                ((TextView) parent.getChildAt(0)).setTextColor(ContextCompat.getColor(_context, R.color.colorPrimary));
                _newGoalUnit = UnitConversion.toUnit(parent.getItemAtPosition(position).toString());
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



    public void editGoalDialog(final int position) {
        if (_goalData.get(position).getActiveState() == ActiveState.ACTIVE) {
            Toast.makeText(_context, "Cannot edit active goal, please activate another goal first", Toast.LENGTH_SHORT).show();
            return;
        }

        Goal selectedGoal = _goalData.get(position);

        LinearLayout layout = new LinearLayout(_context);
        layout.setOrientation(LinearLayout.VERTICAL);

        // Goal Name
        final EditText goalTitle = new EditText(_context);
        goalTitle.setText(selectedGoal.getTitle());
        layout.addView(goalTitle);

        // Goal Target
        final EditText goalTarget = new EditText(_context);
        goalTarget.setText("" + selectedGoal.getTarget());
        goalTarget.setRawInputType(Configuration.KEYBOARD_QWERTY);
        layout.addView(goalTarget);

        // Cache
        _newGoalTitle = goalTitle;
        _newGoalTarget = goalTarget;

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
                            Toast.makeText(_context, "Edited Goal", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        // Showing Alert Message
        editGoalDialog.show();
    }

    public void editGoalDialogWithUnit(final int position) {
        if (_goalData.get(position).getActiveState() == ActiveState.ACTIVE) {
            Toast.makeText(_context, "Cannot edit active goal, please activate another goal first", Toast.LENGTH_SHORT).show();
            return;
        }

        Goal goalToEdit = _goalData.get(position);

        LayoutInflater li = LayoutInflater.from(_context);
        View promptsView = li.inflate(R.layout.goal_alert_dialog, null);
        AlertDialog.Builder editGoalDialog = new AlertDialog.Builder(_context);
        editGoalDialog.setView(promptsView);

        // Initialise the dialog widgets
        DialogTitle editGoalDialogTitle = (DialogTitle) promptsView.findViewById(R.id.goal_alert_title);
        editGoalDialogTitle.setText("Edit Goal");
        final EditText editName = (EditText) promptsView.findViewById(R.id.goal_alert_name);
        editName.setText("" + goalToEdit.getTitle());
        final EditText editTarget = (EditText) promptsView.findViewById(R.id.goal_alert_target);
        editTarget.setText("" + goalToEdit.getTarget());
        editTarget.setRawInputType(Configuration.KEYBOARD_QWERTY);
        final Spinner unitSpinner= (Spinner) promptsView.findViewById(R.id.goal_unit_spinner);
          goalToEdit.getUnit();
        int unitPosition = getPosition(goalToEdit.getUnit());
        unitSpinner.setSelection(unitPosition);

        // Cache
        _editExistingGoalTitle = editName;
        _editExistingGoalTarget = editTarget;

        editGoalDialog.setNegativeButton(android.R.string.cancel, null);
        editGoalDialog.setNeutralButton(getString(R.string.delete_label),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        removeGoal(position);
                    }
                });
        editGoalDialog.setPositiveButton("Edit",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        if (editGoal(position)) {
                            Toast.makeText(_context, "Edited Goal", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        final AlertDialog alertDialog = editGoalDialog.create();

        unitSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                ((TextView) parent.getChildAt(0)).setTextColor(ContextCompat.getColor(_context, R.color.colorPrimary));
                _editExistingGoalUnit = UnitConversion.toUnit(parent.getItemAtPosition(position).toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(_context, "No unit selected", Toast.LENGTH_SHORT).show();
            }

        });

        // Showing Alert Message
        editGoalDialog.show();
    }

    private int getPosition(Unit unit) {
        String[] unit_selectable = getResources().getStringArray(R.array.units_array_values);
        String strUni = UnitConversion.toString(unit);
        int position = -1;
        for (int i = 0; i < unit_selectable.length ; i++) {
           if(strUni.equals(unit_selectable[i])) {
               position = i;
               break;
           }
        }
        return position;
    }

    public boolean addGoal(int position) {
        String strTitle = _newGoalTitle.getText().toString();
        String strTarget = _newGoalTarget.getText().toString();
        // Only edit inactive, valid goals
        if (_goalData.get(position).getActiveState() == ActiveState.ACTIVE) {
            Toast.makeText(_context, "Cannot edit active goal, please activate another goal first", Toast.LENGTH_SHORT).show();
        } else if (isGoalValid(strTitle, strTarget) == GoalValidationCode.OK) {
            _goalData.get(position).setTitle(strTitle);
            _goalData.get(position).setTarget(Double.parseDouble(strTarget));
            _goalData.get(position).setUnit(_newGoalUnit);
            _goalListAdapter.notifyDataSetChanged();
            return true;
        }
        return false;
    }


    private boolean editGoal(int position) {
        String goalTitle = _editExistingGoalTitle.getText().toString();
        String goalTarget = _editExistingGoalTarget.getText().toString();

        // Only edit inactive, valid goals
        if (_goalData.get(position).getActiveState() == ActiveState.ACTIVE) {
            Toast.makeText(_context, "Cannot edit active goal, please activate another goal first", Toast.LENGTH_SHORT).show();
        } else if (isGoalValid(goalTitle, goalTarget) == GoalValidationCode.OK) {
            _positionOfGoalToEdit = position;
            _goalData.get(position).setTitle(goalTitle);
            _goalData.get(position).setTarget(Double.parseDouble(goalTarget));
            _goalData.get(position).setUnit(_editExistingGoalUnit);
            _goalListAdapter.notifyDataSetChanged();
            _positionOfGoalToEdit = -1;
            return true;
        }
        return false;
    }

    public void addNewGoal() {
        String goalTitle = _newGoalTitle.getText().toString();
        String goalTarget = _newGoalTarget.getText().toString();

        if (isGoalValid(goalTitle, goalTarget) == GoalValidationCode.OK) {
            Goal goal = new Goal(goalTitle, 0, 0, Double.parseDouble(goalTarget), _newGoalUnit, ActiveState.INACTIVE);
            _goalData.add(goal);
            _goalListAdapter.notifyDataSetChanged();
            if (_goalData.size() == 1) {
                setGoalActive(0);
            }
            Toast.makeText(_context, "Added " + goal.getTitle(), Toast.LENGTH_SHORT).show();
            return;
        }

        _newGoalTitle.setText("");
        _newGoalTarget.setText("");
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
            _goalData.setActive(position);
            _goalListAdapter.notifyDataSetChanged();
            raiseActiveGoalChanged(_goalData.getActive());
            Toast.makeText(_context, _goalData.getActive().getTitle() + " is now active", Toast.LENGTH_SHORT).show();
        } else {
            // Commented out as hack call to select ite at init is repeatedly called due to
            // MainACtivity onCreate being called between tab slides
            //Toast.makeText(_context, "Goal is already active", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_add_goal, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_add_goal:
                //addGoalDialog();
                addGoalDialogWithUnit();
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
        // Not needed as active goals are changed locally and emitted to others
    }

    @Override
    public void onGoalProgressChanged(double progress) {
        if(_goalData != null) {
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

}
