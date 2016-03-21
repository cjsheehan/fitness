package com.cjsheehan.fitness.activity.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Toast;

import com.cjsheehan.fitness.R;
import com.cjsheehan.fitness.event.date.DateListener;
import com.cjsheehan.fitness.util.Util;

import java.util.List;


public class HistoryFragment extends BaseFragment implements DateListener {
    private static final String TAG = "HistoryFragment";
    private Context _context;
    RadioGroup _periodRadioGroup;
    SeekBar _periodSeeker;
    String _date;
    private static final int WEEK = 7;
    private static final int MONTH = 28;
    private static final int YEAR = 365;
    private enum Period {WEEK, MONTH, OTHER}
    Period _period = Period.WEEK;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);
        onDateChanged(getArguments().getString(getString(R.string.date_bundle_key)));
        init(view);
        return view;
    }

    @Override
    protected void init(View view) {
        _context = view.getContext();
        _periodSeeker = (SeekBar) view.findViewById(R.id.period_seeker);
        _periodRadioGroup = (RadioGroup) view.findViewById(R.id.rdb_period);
        RadioButton checkedRadioButton = (RadioButton) _periodRadioGroup.findViewById(_periodRadioGroup.getCheckedRadioButtonId());
        int checkedId = _periodRadioGroup.getCheckedRadioButtonId();
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
                List<String> dates = Util.getDates(_date, days);
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
                        Toast.makeText(_context, "Range :" + WEEK + " days", Toast.LENGTH_SHORT).show();
                    List<String> dates = Util.getDates(_date, WEEK);
                }
                break;
            case R.id.rdb_month:
                if (isChecked) {
                    _periodSeeker.setEnabled(false);
                    if(!isQuiet)
                        Toast.makeText(_context, "Range :" + MONTH + " days", Toast.LENGTH_SHORT).show();
                    List<String> dates = Util.getDates(_date, MONTH);
                }
                break;
            case R.id.rdb_other:
                if (isChecked) {
                    _periodSeeker.setEnabled(true);
                    if(!isQuiet)
                        Toast.makeText(_context, "Checked:" + checkedRadioButton.getText(), Toast.LENGTH_SHORT).show();
                    int days = getDaysFromSeeker();
                    List<String> dates = Util.getDates(_date, days);
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
        _date = date;
    }

    private void updateView(String dateFrom, String dateTo) {

    }

}
