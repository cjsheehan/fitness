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
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cjsheehan.fitness.R;
import com.cjsheehan.fitness.adapter.GoalListAdapter;
import com.cjsheehan.fitness.event.date.DateListener;
import com.cjsheehan.fitness.model.ActiveState;
import com.cjsheehan.fitness.model.Goal;
import com.cjsheehan.fitness.model.GoalData;
import com.cjsheehan.fitness.model.Unit;
import com.cjsheehan.fitness.model.UnitConverter;
import com.cjsheehan.fitness.model.conversion.HistoryData;
import com.cjsheehan.fitness.util.Util;
import com.cjsheehan.fitness.view.GoalView;

import java.io.IOException;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.List;


public class HistoryFragment extends BaseFragment implements DateListener {
    private static final String TAG = "HistoryFragment";
    private Context _context;
    private RadioGroup _periodRadioGroup;
    private SeekBar _periodSeeker;
    private TextView _periodView;
    private String _dateFrom;
    private String _fromLabel, _toLabel;
    private String _dateTo;
    List<String> _dates;
    private static final int WEEK = 7 + 1;
    private static final int MONTH = 28 + 1;
    private static final int YEAR = 365 + 1;
    private View _view;
    private GoalView _goalView;
    private static final Unit[] _unitArr = new Unit[]{Unit.STEP, Unit.YARD, Unit.METRE, Unit.KILOMETRE, Unit.MILE};
    private int _unitIndex = _unitArr.length + 1;  // This can go _unitArr.size + 1, in which case ot uses the recorded history unit
    private Unit _refUnit = null;

    private enum Period {WEEK, MONTH, OTHER}
    Period _period = Period.WEEK;

    // Goal handlers
    private GoalListAdapter _goalListAdapter;
    private ListView _goalListView;
    HistoryData _historyData;
    List<Goal> _historyRef;
    List<Goal> _historyDisplay;


    private void handleUnitChange() {
        if(_unitIndex >= _unitArr.length) {
            _unitIndex = 0;
        } else {
            _unitIndex++;
        }

        _refUnit = null;
        if(_unitIndex < _unitArr.length)
            _refUnit = _unitArr[_unitIndex];

        changeDisplayUnit();

    }

    private void changeDisplayUnit() {

        for (int i = 0; i < _historyDisplay.size(); i++) {
            if (_refUnit != null) {
                Unit unitFrom = _historyRef.get(i).getUnit();
                double targetFrom = _historyRef.get(i).getTarget();
                double progressFrom = _historyRef.get(i).getProgress();

                double targetTo = UnitConverter.convert(targetFrom, unitFrom, _refUnit);
                double progressTo = UnitConverter.convert(progressFrom, unitFrom, _refUnit);

                _historyDisplay.get(i).setUnit(_refUnit);
                _historyDisplay.get(i).setProgress(progressTo);
                _historyDisplay.get(i).setTarget(targetTo);
            } else {
                // Reset to original
                Unit unitTo = _historyRef.get(i).getUnit();
                double targetTo = _historyRef.get(i).getTarget();
                double progressTo = _historyRef.get(i).getProgress();

                _historyDisplay.get(i).setUnit(unitTo);
                _historyDisplay.get(i).setProgress(progressTo);
                _historyDisplay.get(i).setTarget(targetTo);
            }
        }
        _goalListAdapter.notifyDataSetChanged();
    }

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
        _dateFrom = Util.getDateToday();
        _periodSeeker = (SeekBar) view.findViewById(R.id.period_seeker);
        _periodRadioGroup = (RadioGroup) view.findViewById(R.id.rdb_period);
        RadioButton checkedRadioButton = (RadioButton) _periodRadioGroup.findViewById(_periodRadioGroup.getCheckedRadioButtonId());
        int checkedId = _periodRadioGroup.getCheckedRadioButtonId();
        _periodView = (TextView) view.findViewById(R.id.history_period_label);
        _goalListView = (ListView) _view.findViewById(R.id.history_list);
        _goalListView.setOnItemClickListener(null);
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
                _dates = Util.getDates(_dateFrom, days, Util.Order.REVERSE);
                initHistory(_dates);
            }
        });

        _goalView = new GoalView(_context);
        setupGoalView(_goalView, view);

    }


    private void setupGoalView(GoalView gv, View view) {
        gv.setProgress((TextView) view.findViewById(R.id.active_goal_current_progress));
        gv.setTarget((TextView) view.findViewById(R.id.active_goal_target));
        gv.setTargetProgress((ProgressBar) view.findViewById(R.id.active_goal_progress_bar));
        gv.setUnit((TextView) view.findViewById(R.id.active_goal_unit_text));
        gv.setTitle((TextView) view.findViewById(R.id.history_item_title));
        gv.setDate((TextView) view.findViewById(R.id.history_item_date));
    }

    private void handlePeriodRadioButton(RadioGroup rGroup, int checkedId, boolean isQuiet) {
        RadioButton checkedRadioButton = (RadioButton) rGroup.findViewById(_periodRadioGroup.getCheckedRadioButtonId());
        boolean isChecked = checkedRadioButton.isChecked();
        switch (checkedId) {
            case R.id.rdb_week:
                if (isChecked) {
                    _periodSeeker.setEnabled(false);
                    _dates = Util.getDates(_dateFrom, WEEK, Util.Order.REVERSE);
                    initHistory(_dates);
                }
                break;
            case R.id.rdb_month:
                if (isChecked) {
                    _periodSeeker.setEnabled(false);
                    _dates = Util.getDates(_dateFrom, MONTH, Util.Order.REVERSE);
                    initHistory(_dates);
                }
                break;
            case R.id.rdb_other:
                if (isChecked) {
                    _periodSeeker.setEnabled(true);
                    int days = getDaysFromSeeker();
                    _dates = Util.getDates(_dateFrom, days + 1, Util.Order.REVERSE);
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

    private void setDateLabels(List<String> dates) {
        _fromLabel = Util.getDateFrom(-1);

        if(dates.size() > 1) {
            _toLabel = _dates.get(_dates.size() - 1);
        } else if (dates.size() == 1) {
            _toLabel = _dateFrom;
        } else {
            _fromLabel = "";
            _toLabel = "";
        }
    }


    @Override
    public void onDateChanged(String date) {
        //_dateFrom = date;
    }

    private void initHistory(List<String> dates) {
        try {
            _historyData = new HistoryData(_context, dates);
        } catch (IOException e) {
            e.printStackTrace();
        }
        _historyRef = _historyData.getAll();
        _historyDisplay = new ArrayList<>();

        for(Goal g : _historyRef) {
            g.setActiveState(ActiveState.INACTIVE);
            Goal copy = Goal.copy(g);
            copy.setActiveState(ActiveState.INACTIVE);
            _historyDisplay.add(copy);
        }

        _goalListAdapter = new GoalListAdapter(_historyDisplay, getContext(), true);
        _goalListView.setAdapter(_goalListAdapter);
        _goalListAdapter.notifyDataSetChanged();
        setDateLabels(dates);
        _periodView.setText(_toLabel + " - " + _fromLabel);
    }



    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_change_unit, menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_change_unit:
                handleUnitChange();
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
