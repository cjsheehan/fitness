package com.cjsheehan.fitness.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.preference.PreferenceManager;
import android.content.SharedPreferences;

import java.util.prefs.Preferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cjsheehan.fitness.R;
import com.cjsheehan.fitness.activity.fragment.ActiveGoalProgressFragment;
import com.cjsheehan.fitness.activity.fragment.BaseFragment;
import com.cjsheehan.fitness.activity.fragment.GoalsFragment;
import com.cjsheehan.fitness.activity.fragment.HistoryFragment;
import com.cjsheehan.fitness.activity.fragment.SettingsFragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import static com.cjsheehan.fitness.activity.MainActivity.FragmentId.ACTIVE_GOAL_PROGRESS;
import static com.cjsheehan.fitness.activity.MainActivity.FragmentId.getIdByInt;

public class MainActivity extends AppCompatActivity {
    private String TAG = "MainActivity";
    private SharedPreferences _sharedPreferences;
    private Calendar _calendar;
    private EditText _editDateText;
    private TextView _progressTextView;
    private Toolbar _toolbar;
    private FloatingActionButton _fab;
    private static final int PAGE_LIMIT = 2;
    private SharedPreferences.OnSharedPreferenceChangeListener _settingsListener;
    private String _dateFormat = "dd MMMM yyyy";
    private SimpleDateFormat _sdf = new SimpleDateFormat(_dateFormat, Locale.ENGLISH);
    private final static int DAY_IN_MS = 86400000;
    private Animation _simpleAnim;
    //private ProgressListAdapter _adapter;
    //private List<Progress> _progress;

    private ViewPagerAdapter _viewPagerAdapter;
    private ViewPager _viewPager;
    private int _selectedPagePosition;

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

    enum ProgressChangeDirection { INCREMENT, DECREMENT };

    private void init() {
        initUI();
        setupViewPager(_viewPager);
        setupSharedPreferences();
    }

    private void initUI() {

        //_toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        _calendar = Calendar.getInstance();
        //_editDateText = (EditText) findViewById(R.id.editDateText);
        //setSupportActionBar(_toolbar);
        //initDateText();
        //_simpleAnim = AnimationUtils.loadAnimation(this, R.animator.simple_animation);
    }

    private void setupSharedPreferences() {
        _sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        _settingsListener = new settingsChangedListener();
        _sharedPreferences.registerOnSharedPreferenceChangeListener(_settingsListener);
    }

    //private void initDateText() {
    //    _editDateText.setGravity(Gravity.CENTER);
    //    updateDateColour();
    //    updateLabel();
    //    final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
    //
    //        @Override
    //        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
    //            _calendar.set(Calendar.YEAR, year);
    //            _calendar.set(Calendar.MONTH, monthOfYear);
    //            _calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
    //            updateLabel();
    //        }
    //    };
    //
    //    _editDateText.setOnClickListener(new View.OnClickListener() {
    //
    //        @Override
    //        public void onClick(View v) {
    //            if (isTestModeEnabled()) {
    //                // TODO : Fix animation
    //                _editDateText.startAnimation(_simpleAnim);
    //                DatePickerDialog dpd = new DatePickerDialog(MainActivity.this, date, _calendar
    //                        .get(Calendar.YEAR), _calendar.get(Calendar.MONTH),
    //                        _calendar.get(Calendar.DAY_OF_MONTH));
    //                // TODO : BUG - setMaxDate won't allow to select today, hacked with +1 day
    //                dpd.getDatePicker().setMaxDate(System.currentTimeMillis() + DAY_IN_MS);
    //                dpd.show();
    //            }
    //        }
    //    });
//}

    //private void updateDateColour() {
//    if (isTestModeEnabled()) {
//        _editDateText.setTextColor(getResources().getColor(R.color.colorAccent));
//    } else {
//        _editDateText.setTextColor(getResources().getColor(R.color.colorPrimary));
//    }
//}
//
//private void updateLabel() {
//    _editDateText.setText(_sdf.format(_calendar.getTime()));
//}
//
//private boolean isTestModeEnabled() {
//    return _sharedPreferences.getBoolean(
//            getString(R.string.enableTestMode),
//            getResources().getBoolean(R.bool.testModeEnabled_Default));
//}
//
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(TAG, "onCreateOptionsMenu");
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
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

    private class settingsChangedListener implements SharedPreferences.OnSharedPreferenceChangeListener {

        @Override
        public void onSharedPreferenceChanged(SharedPreferences spref, String key) {
            //if (key.equals(getString(R.string.enableTestMode))) {
            //    updateDateColour();
            //    updateLabel();
            //}
        }
    }

    private void setupViewPager(ViewPager viewPager) {
        viewPager = (ViewPager) findViewById(R.id.view_pager);
        viewPager.setAdapter(_viewPagerAdapter);
        viewPager.setOffscreenPageLimit(PAGE_LIMIT);
        _viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        for(FragmentId fid : FragmentId.values()) {
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


    public static Fragment createFragment (FragmentId fragmentId) {
        BaseFragment fragment = null;
        switch (fragmentId) {
            case ACTIVE_GOAL_PROGRESS:
                fragment = new ActiveGoalProgressFragment();
                break;
            case GOALS:
                fragment = new GoalsFragment();
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
        ACTIVE_GOAL_PROGRESS(0, "Activity"),
        GOALS(1, "Goals"),
        HISTORY(2, "History"),
        SETTINGS(3, "Settings");
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
                    return ACTIVE_GOAL_PROGRESS;
                case 1 :
                    return GOALS;
                case 2 :
                    return HISTORY;
                case 4 :
                    return SETTINGS;
            }

            return null;
        }
    }

}