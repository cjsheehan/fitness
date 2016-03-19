package com.cjsheehan.fitness.activity.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;
import java.util.Random;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cjsheehan.fitness.R;
import com.cjsheehan.fitness.model.Goal;

import java.util.List;
import java.util.Random;


public class HistoryFragment extends BaseFragment {
    private static final String TAG = "HistoryFragment";
    private final int LOADER_ID = new Random().nextInt(1000);

    private Context context;
    //private GoalRepository repository;
    //private HistoryChartView historyChartView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "Entered onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)");
        View view = inflater.inflate(R.layout.fragment_history, container, false);
        init(view);
        return view;
    }

    @Override
    protected void init(View view) {
        context = view.getContext();
        //repository = new GoalRepository(context);
        //historyChartView = (HistoryChartView) view.findViewById(R.id.history_chart);

        registerReceivers();
        //initLoader();
    }

    //@Override
    //protected void addGoalActionReceived(Goal goal) {
    //    historyChartView.addGoal(goal);
    //}
    //
    //@Override
    //protected void deleteGoalActionReceived(Goal goal) {
    //}
    //
    //@Override
    //protected void updateGoalOfDayActionReceived(Goal goal) {
    //    historyChartView.editGoal(goal);
    //}
    //
    //@Override
    //protected void updateGoalAmountActionReceived(Goal goal) {
    //    historyChartView.editGoal(goal);
    //}
    //
    //@Override
    //protected void editModeSwitchActionReceived(boolean isEditMode, String date) {
    //
    //}
    //
    //@Override
    //protected void calendarChangeActionReceived(String date) {
    //
    //}

    //private void initLoader() {
    //    Log.d(TAG, "Entered initLoader()");
    //    if (getActivity().getSupportLoaderManager().getLoader(LOADER_ID) == null)
    //        Log.d(TAG, "# Initializing a new Loader");
    //    else
    //        Log.d(TAG, "# Reconnecting with existing Loader (id " + LOADER_ID + ")...");
    //    getLoaderManager().initLoader(LOADER_ID, null, this).forceLoad();
    //}
    //
    //@Override
    //public Loader<List<Goal>> onCreateLoader(int id, Bundle args) {
    //    Log.d(TAG, "entered : onCreateLoader(int id, Bundle args) " + id);
    //    // TODO : implement onCreateLoader
    //    return null;
    //}
    //
    //@Override
    //public void onLoadFinished(Loader<List<Goal>> loader, List<Goal> data) {
    //    Log.d(TAG, "entered : onLoadFinished(Loader<List<Goal>> loader, Bundle args)");
    //    // TODO : implement onLoadFinished
    //}
    //
    //@Override
    //public void onLoaderReset(Loader<List<Goal>> loader) {
    //    Log.d(TAG, "entered : onLoaderReset(Loader<List<Goal>> loader)");
    //    // TODO : implement onLoaderReset
    //}
    //
    //@Override
    //public Loader<List<Goal>> onCreateLoader(int id, Bundle args) {
    //    Log.d(TAG, "CREATING LOADER " + id);
    //    return new GoalDateDataLoader(context);
    //}
    //
    //@Override
    //public void onLoadFinished(Loader<List<Goal>> loader, List<Goal> data) {
    //    historyChartView.setGoalDates(data);
    //}
    //
    //@Override
    //public void onLoaderReset(Loader<List<Goal>> loader) {
    //    historyChartView.refresh();
    //}
}
