package com.cjsheehan.fitness.activity.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Toast;

import com.cjsheehan.fitness.R;
import com.cjsheehan.fitness.adapter.GoalListAdapter;
import com.cjsheehan.fitness.event.date.DateListener;
import com.cjsheehan.fitness.model.ActiveState;
import com.cjsheehan.fitness.model.Goal;
import com.cjsheehan.fitness.model.GoalData;
import com.cjsheehan.fitness.model.conversion.HistoryData;
import com.cjsheehan.fitness.util.Util;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;


public class HistoryFragment extends BaseFragment implements DateListener {
    private static final String TAG = "HistoryFragment";
    private Context _context;
    RadioGroup _periodRadioGroup;
    SeekBar _periodSeeker;
    String _date;
    List<String> _dates;
    private static final int WEEK = 7;
    private static final int MONTH = 28;
    private static final int YEAR = 365;
    private View _view;

    private enum Period {WEEK, MONTH, OTHER}
    Period _period = Period.WEEK;

    // Goal handlers
    private GoalData _goalData;
    private GoalListAdapter _goalListAdapter;
    private ListView _goalListView;
    HistoryData _historyData;
    List<Goal> _historyRef;
    List<Goal> _historyDisplay;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);
        _view = view;
        onDateChanged(getArguments().getString(getString(R.string.date_bundle_key)));
        init(view);
        setHasOptionsMenu(true);
        return view;
    }

    @Override
    protected void init(View view) {
        _context = view.getContext();
        _date = Util.getDateToday();
        _periodSeeker = (SeekBar) view.findViewById(R.id.period_seeker);
        _periodRadioGroup = (RadioGroup) view.findViewById(R.id.rdb_period);
        RadioButton checkedRadioButton = (RadioButton) _periodRadioGroup.findViewById(_periodRadioGroup.getCheckedRadioButtonId());
        int checkedId = _periodRadioGroup.getCheckedRadioButtonId();
        _goalListView = (ListView) _view.findViewById(R.id.history_list);
        handlePeriodRadioButton(_periodRadioGroup, checkedId, true);

        _periodRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup rGroup, int checkedId) {
                handlePeriodRadioButton(rGroup, checkedId, false);
            }
        });

        _periodSeeker.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

                int days = getDaysFromSeeker();
                Toast.makeText(_context, "Range :" + days + " days", Toast.LENGTH_SHORT).show();
                _dates = Util.getDates(_date, days, Util.Order.REVERSE);
                initHistory(_dates);
            }
        });



    }

    private void handlePeriodRadioButton(RadioGroup rGroup, int checkedId, boolean isQuiet) {
        RadioButton checkedRadioButton = (RadioButton) rGroup.findViewById(_periodRadioGroup.getCheckedRadioButtonId());
        boolean isChecked = checkedRadioButton.isChecked();
        switch (checkedId) {
            case R.id.rdb_week:
                if (isChecked) {
                    _periodSeeker.setEnabled(false);
                    if(!isQuiet)
                        Toast.makeText(_context, "Range : " + WEEK + " days", Toast.LENGTH_SHORT).show();
                    _dates = Util.getDates(_date, WEEK, Util.Order.REVERSE);
                    initHistory(_dates);
                }
                break;
            case R.id.rdb_month:
                if (isChecked) {
                    _periodSeeker.setEnabled(false);
                    if(!isQuiet)
                    Toast.makeText(_context, "Range : " + MONTH + " days", Toast.LENGTH_SHORT).show();
                    _dates = Util.getDates(_date, MONTH, Util.Order.REVERSE);
                    initHistory(_dates);
                }
                break;
            case R.id.rdb_other:
                if (isChecked) {
                    _periodSeeker.setEnabled(true);
                    int days = getDaysFromSeeker();
                    _dates = Util.getDates(_date, days, Util.Order.REVERSE);
                    initHistory(_dates);
                }
                break;
        }
    }

    private int getDaysFromSeeker() {
        int max = _periodSeeker.getMax();
        int progress = _periodSeeker.getProgress();
        float days = ((float) YEAR / (float) max) * (float) progress;
        return (int) days;
    }


    @Override
    public void onDateChanged(String date) {
        //_date = date;
    }

    private void initHistory(List<String> dates) {
        try {
            _historyData = new HistoryData(_context, dates);
        } catch (IOException e) {
            e.printStackTrace();
        }
        _historyRef = _historyData.getAll();
        _historyDisplay = _historyData.getAll();
        for(Goal g : _historyDisplay) {
            g.setActiveState(ActiveState.INACTIVE);
        }
        _goalListAdapter = new GoalListAdapter(_historyDisplay, getContext());
        _goalListView.setAdapter(_goalListAdapter);
        _goalListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_change_unit, menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_change_unit:
                Toast.makeText(_context, "Change Unit", Toast.LENGTH_SHORT).show();
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    //private void updateView(List<String> dates) {
    //    try {
    //        _historyData = new HistoryData(_context, dates);
    //    } catch (IOException e) {
    //        e.printStackTrace();
    //    }
    //
    //
    //}

}
