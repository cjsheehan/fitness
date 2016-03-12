package com.cjsheehan.fitness.activity.fragment;

import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;

import com.cjsheehan.fitness.broadcast.Action;
import com.cjsheehan.fitness.model.Goal;
import com.cjsheehan.fitness.model.IGoalAction;

/**
 * A simple {@link Fragment} subclass.
 */
public abstract class BaseFragment extends Fragment implements IGoalAction {
    private static final String TAG = "BaseFragment";

    protected void registerReceivers() {
        Log.d(TAG, "Entered : registerReceivers()");
        getActivity().registerReceiver(new FragmentReceiver(), new IntentFilter(Action.ADD_GOAL.name()));
        getActivity().registerReceiver(new FragmentReceiver(), new IntentFilter(Action.REMOVE_GOAL.name()));
        getActivity().registerReceiver(new FragmentReceiver(), new IntentFilter(Action.UPDATE_GOAL.name()));
        //getActivity().registerReceiver(new FragmentReceiver(), new IntentFilter(Action.UPDATE_GOAL_AMOUNT.title));
        //getActivity().registerReceiver(new FragmentReceiver(), new IntentFilter(Action.EDIT_MODE_SWITCH.title));
        //getActivity().registerReceiver(new FragmentReceiver(), new IntentFilter(Action.CALENDER_CHANGE.title));
    }

    protected abstract void init(View view);

    //protected abstract void addGoal(Goal goal);
    //
    //protected abstract void removeGoal(Goal goal);
    //
    //protected abstract void updateGoal(Goal goal);
    //
    //protected abstract void updateGoalAmountActionReceived(Goal goal);
    //
    //protected abstract void editModeSwitchActionReceived(boolean isEditMode, String date);
    //
    //protected abstract void calendarChangeActionReceived(String date);



    public class FragmentReceiver extends BroadcastReceiver {
        private static final String TAG = "FragmentReceiver";

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "Entered onReceive(Context context, Intent intent)");
            intent.getIntExtra("action", 0);
            Action action = Action.getById(intent.getIntExtra("action", 0));
            Goal goal = (Goal) intent.getSerializableExtra("goal");
            switch (action) {
                case ADD_GOAL: {
                    addGoal(goal);
                    break;
                }
                case REMOVE_GOAL: {
                    removeGoal(goal);
                }
                case UPDATE_GOAL: {
                    updateGoal(goal);
                    break;
                }

            }
        }
    }

}
