package com.cjsheehan.fitness.activity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.content.SharedPreferences;

import java.text.DateFormat;
import java.util.prefs.Preferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.cjsheehan.fitness.R;
import com.cjsheehan.fitness.activity.fragment.ActiveGoalProgressFragment;
import com.cjsheehan.fitness.activity.fragment.BaseFragment;
import com.cjsheehan.fitness.activity.fragment.GoalsFragment;
import com.cjsheehan.fitness.activity.fragment.HistoryFragment;
import com.cjsheehan.fitness.activity.fragment.SettingsFragment;
import com.cjsheehan.fitness.event.date.DateListener;
import com.cjsheehan.fitness.event.goal.GoalListener;
import com.cjsheehan.fitness.model.Goal;
import com.cjsheehan.fitness.model.UnitConverter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity
        implements GoalListener {
    private String TAG = "MainActivity";
    private SharedPreferences _sharedPreferences;

    private TextView _progressTextView;
    private Toolbar _toolbar;
    private FloatingActionButton _fab;
    private static final int PAGE_LIMIT = 2;
    private SharedPreferences.OnSharedPreferenceChangeListener _settingsListener;

    private Animation _simpleAnim;
    private MenuItem _calendarMenuItem;

    private GoalsFragment _goalsFragment;
    //private ProgressListAdapter _adapter;
    //private List<Progress> _progress;

    private ViewPagerAdapter _viewPagerAdapter;
    private ViewPager _viewPager;
    private int _selectedPagePosition;
    private boolean _isCounterRecording;



    // DATE
    private String _date;
    private Calendar _calendar;
    List<DateListener> _dateListeners;
    private String _dateFormat = "dd MMMM yyyy";
    DateFormat _dateFormatter = new SimpleDateFormat(_dateFormat, Locale.ENGLISH);
    private final static int DAY_IN_MS = 86400000;

    // GOALS
    List<GoalListener> _goalListeners;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //makeActionOverflowMenuShown();
        //PreferenceManager.setDefaultValues(this, R.xml.user_settings, false);
        init();
        //updateProgressView();
    }

    private void init() {
        setupSharedPreferences();
        _dateListeners = new ArrayList<>();
        _goalListeners = new ArrayList<>();
        setupDate();
        setupViewPager(_viewPager);
        _simpleAnim = AnimationUtils.loadAnimation(this, R.animator.simple_animation);
        _isCounterRecording = false;
        //setupCalendar();
        setupFloatActBtn();

        //updateDateListeners(_date);
    }

    private void initCalendarMenuItem(MenuItem calendarMenuItem) {
        calendarMenuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear,
                                          int dayOfMonth) {
                        _calendar.set(Calendar.YEAR, year);
                        _calendar.set(Calendar.MONTH, monthOfYear);
                        _calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        _date = _dateFormatter.format(_calendar.getTime());
                        updateDateListeners(_date);
                    }
                };

                new DatePickerDialog(MainActivity.this, date, _calendar
                        .get(Calendar.YEAR),
                        _calendar.get(Calendar.MONTH),
                        _calendar.get(Calendar.DAY_OF_MONTH)).show();
                return true;
            }
        });

        setCalendarMenuItemVisibilty();
        _goalsFragment.selectActiveGoal();
    }

    private void setupDate() {
        _calendar = Calendar.getInstance();
        _date = _dateFormatter.format(_calendar.getTime());
    }

    private void setupSharedPreferences() {
        _sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        _settingsListener = new SettingsChangedListener();
        _sharedPreferences.registerOnSharedPreferenceChangeListener(_settingsListener);
    }

    private class SettingsChangedListener implements SharedPreferences.OnSharedPreferenceChangeListener {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences spref, String key) {
            if (key.equals(getString(R.string.enable_test_mode_key))) {
                setCalendarMenuItemVisibilty();
            }


        }
    }




    private void updateDateListeners(String date) {
        for(DateListener dl : _dateListeners) {
            dl.onDateChanged(date);
        }
    }

    public boolean isTestModeEnabled() {
        return _sharedPreferences.getBoolean(
                getString(R.string.enable_test_mode_key),
                getResources().getBoolean(R.bool.testModeEnabled_Default));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(TAG, "onCreateOptionsMenu");
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        _calendarMenuItem = menu.findItem(R.id.menu_select_date);
        initCalendarMenuItem(_calendarMenuItem);
        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(TAG, "onOptionsItemSelected");
        switch (item.getItemId()) {
            case R.id.action_settings: {
                Intent intent = new Intent(this, Preferences.class);
                intent.putExtra(SettingsActivity.EXTRA_SHOW_FRAGMENT, SettingsFragment.class.getName());
                intent.putExtra(SettingsActivity.EXTRA_NO_HEADERS, true);
                intent.setClassName(this, "com.cjsheehan.fitness.activity.SettingsActivity");
                startActivity(intent);
                //getFragmentManager().beginTransaction()
                //        .replace(android.R.id.content, new SettingsFragment()).commit();

                return true;
            }
        }

        return super.onOptionsItemSelected(item);
    }



    private void setCalendarMenuItemVisibilty() {
        if(isTestModeEnabled())
            _calendarMenuItem.setVisible(true);
        else
            _calendarMenuItem.setVisible(false);
    }

    private void setupViewPager(ViewPager viewPager) {
        viewPager = (ViewPager) findViewById(R.id.view_pager);
        viewPager.setAdapter(_viewPagerAdapter);
        viewPager.setOffscreenPageLimit(PAGE_LIMIT);
        _viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());

        // Page order is a user setting
        FragmentId [] fidOrder = getFragmentOrder();
        for(FragmentId fid : fidOrder) {
            Fragment fragment = createFragment(fid);
            if(fragment != null)
                _viewPagerAdapter.addFragment(fragment, fid.getTitle());
            else
                Log.d(TAG, "ERROR : fragment is null, fragment id : " + fid.getTitle());
        }

        viewPager.setAdapter(_viewPagerAdapter);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    private FragmentId[] getFragmentOrder() {
        String[] defaultOrder = getResources().getStringArray(R.array.default_view_preference_values);
        String userOrderCat = _sharedPreferences.getString(getString(R.string.default_view_key), defaultOrder[0]);
        String[] userOrder = userOrderCat.split("\\|");
        FragmentId[] fids = new FragmentId[userOrder.length];
        for (int i = 0; i < userOrder.length; i++) {
            userOrder[i] = userOrder[i].trim();
            fids[i] = FragmentId.getIdByString(userOrder[i].trim());
        }
        return fids;
    }


    public Fragment createFragment (FragmentId fragmentId) {
        BaseFragment fragment = null;
        Bundle bundle= null;
        switch (fragmentId) {
            case ACTIVITY:
                bundle = new Bundle();
                bundle.putString(getString(R.string.date_bundle_key), _date);
                fragment = new ActiveGoalProgressFragment();
                fragment.setArguments(bundle);
                _dateListeners.add((DateListener) fragment);
                _goalListeners.add((GoalListener) fragment);
                break;
            case GOALS:
                bundle = new Bundle();
                bundle.putString(getString(R.string.date_bundle_key), _date);
                _goalsFragment = new GoalsFragment();
                fragment = _goalsFragment;
                fragment.setArguments(bundle);
                _dateListeners.add((DateListener) fragment);
                _goalListeners.add((GoalListener) fragment);
                break;
            case HISTORY:
                fragment = new HistoryFragment();
                break;
        }
        return fragment;
    }

    class ViewPagerAdapter extends FragmentPagerAdapter implements
            ViewPager.OnPageChangeListener {
        private final List<Fragment> _fragmentList = new ArrayList<>();
        private final List<String> _fragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return _fragmentList.get(position);
        }

        @Override
        public int getCount() {
            return _fragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            _fragmentList.add(fragment);
            _fragmentTitleList.add(title);
        }

        @Override
        public String getPageTitle(int position) {
            return _fragmentTitleList.get(position);
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            _selectedPagePosition = position;
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }

    public enum FragmentId {
        ACTIVITY(0, "Activity"),
        GOALS(1, "Goals"),
        HISTORY(2, "History"),
        STATS(2, "Stats");
        private int id;
        private String title;

        FragmentId(int id, String title) {
            this.id = id;
            this.title = title;
        }

        public int getId() {
            return id;
        }

        public String getTitle() {
            return title;
        }

        public static FragmentId getIdByInt(int id) {
            switch (id) {
                case 0 :
                    return ACTIVITY;
                case 1 :
                    return GOALS;
                case 2 :
                    return HISTORY;
                case 3 :
                    return STATS;
            }

            return null;
        }

        public static FragmentId getIdByString(String id) {
            switch (id) {
                case "ACTIVITY" :
                    return ACTIVITY;
                case "GOALS" :
                    return GOALS;
                case "HISTORY" :
                    return HISTORY;
                case "STATS" :
                    return STATS;
            }

            return null;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    private void setupFloatActBtn() {
        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            public void onClick(final View view) {
                view.startAnimation(_simpleAnim);
                if (_isCounterRecording) {
                    fab.setImageResource(R.drawable.ic_record_steps);
                    _isCounterRecording = false;
                } else {
                    fab.setImageResource(R.drawable.ic_stop_white);
                    _isCounterRecording = true;
                }
            }
        });
    }

    @Override
    public void onActiveGoalChanged(Goal goal) {
        if(goal != null) {
            for (GoalListener gl : _goalListeners) {
                if(gl != null)
                    gl.onActiveGoalChanged(goal);
            }
        }
    }

    @Override
    public void onGoalProgressChanged(double progress) {
        for (GoalListener gl : _goalListeners) {
            if (gl != null)
                gl.onGoalProgressChanged(progress);
        }
    }

}