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


public class HistoryFragment extends BaseFragment implements DateListener {
    private static final String TAG = "HistoryFragment";
    private Context _context;
    RadioGroup _periodRadioGroup;
    SeekBar _periodSeeker;
    String _date;

    @Override
    public void onDateChanged(String date) {
        _date = date;
    }

    private enum Period { WEEK, MONTH, OTHER };
    Period _period = Period.WEEK;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);
        init(view);
        return view;
    }

    @Override
    protected void init(View view) {
        _context = view.getContext();
        _periodRadioGroup = (RadioGroup) view.findViewById(R.id.rdb_period);
        RadioButton checkedRadioButton = (RadioButton) _periodRadioGroup.findViewById(_periodRadioGroup.getCheckedRadioButtonId());
        _periodSeeker = (SeekBar) view.findViewById(R.id.period_seeker);

        _periodRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup rGroup, int checkedId) {
                RadioButton checkedRadioButton = (RadioButton) rGroup.findViewById(_periodRadioGroup.getCheckedRadioButtonId());
                boolean isChecked = checkedRadioButton.isChecked();
                switch (checkedId) {
                    case R.id.rdb_week:
                        if (isChecked) {
                            _periodSeeker.setEnabled(false);
                            Toast.makeText(_context, "Checked:" + checkedRadioButton.getText(), Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case R.id.rdb_month:
                        if (isChecked) {
                            _periodSeeker.setEnabled(false);
                            Toast.makeText(_context, "Checked:" + checkedRadioButton.getText(), Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case R.id.rdb_year:
                        if (isChecked) {
                            _periodSeeker.setEnabled(true);
                            Toast.makeText(_context, "Checked:" + checkedRadioButton.getText(), Toast.LENGTH_SHORT).show();
                        }
                        break;
                }
            }
        });

        _periodSeeker.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
        {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Toast.makeText(_context, "progress:" + seekBar.getProgress(), Toast.LENGTH_SHORT).show();
                ;
            }
        });
    }

    //
    //private class OnRadioButtonClicked {
    //    public void onRadioButtonClicked(View view) {
    //        // Is the button now checked?
    //        boolean checked = ((RadioButton) view).isChecked();
    //
    //        // Check which radio button was clicked
    //        switch (view.getId()) {
    //            case R.id.select_left:
    //                if (checked)
    //                    // Pirates are the best
    //                    break;
    //            case R.id.select_middle:
    //                if (checked)
    //                    // Pirates are the best
    //                    break;
    //            case R.id.select_right:
    //                if (checked)
    //                    // Pirates are the best
    //                    break;
    //        }
    //    }
    //}
}

