package com.cjsheehan.fitness.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.cjsheehan.fitness.R;
import com.cjsheehan.fitness.activity.fragment.BaseFragment;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private String TAG = "MainActivity";
    private SharedPreferences _sharedPreferences;
    private Calendar _calendar;
    private EditText _editDateText;
    private Toolbar _toolbar;
    private SharedPreferences.OnSharedPreferenceChangeListener _settingsListener;
    private String _dateFormat = "dd MMMM yyyy";
    private SimpleDateFormat _sdf = new SimpleDateFormat(_dateFormat, Locale.ENGLISH);
    private Animation _simpleAnim;
    private final static int DAY_IN_MS = 86400000;
    Button _btnSetGoals;
    //private ProgressListAdapter _adapter;
    //private List<Progress> _progress;
    private ListView _progListView;
    EditText _editProgressTarget;

    TextView _steps_today_tv;
    TextView _goals_today_tv;
    ProgressBar _steps_progress_bar;
    TextView _target_today_tv;

    int _stepsToday;
    int _targetToday;

    private SectionsPagerAdapter _sectionsPagerAdapter;
    private ViewPager _viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //PreferenceManager.setDefaultValues(this, R.xml.user_settings, false);
        init();
        //updateProgressView();
    }

    private void init() {
        Log.d(TAG, "Entered : init()");
        initUI();
        //_btnSetGoals = (Button) findViewById(R.id.buttonSetGoals);
        try {
            _targetToday = 10000;
            _stepsToday = 5000;
            _sectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
            _viewPager = (ViewPager) findViewById(R.id.view_pager);
            _viewPager.setAdapter(_sectionsPagerAdapter);
            _viewPager.setOffscreenPageLimit(2);

            TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
            tabLayout.setupWithViewPager(_viewPager);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.d(TAG, "Leaving : init()");
    }

    //public void onOpenGoals()
    //{
    //    openGoals(null);
    //}

    //public void openGoals(View view) {
    //    Intent intent = new Intent(this, GoalActivity.class);
    //    startActivity(intent);
    //}

    private void initUI() {
        _sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        _settingsListener = new settingsChangedListener();
        _sharedPreferences.registerOnSharedPreferenceChangeListener(_settingsListener);
        //_toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        _calendar = Calendar.getInstance();
        //_editDateText = (EditText) findViewById(R.id.editDateText);
        //setSupportActionBar(_toolbar);
        //initDateText();
        //_simpleAnim = AnimationUtils.loadAnimation(this, R.animator.simple_animation);
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
//@Override
//public boolean onCreateOptionsMenu(Menu menu) {
//    MenuInflater inflater = getMenuInflater();
//    inflater.inflate(R.menu.menu, menu);
//    return true;
//}
//
//@Override
//public boolean onOptionsItemSelected(MenuItem item) {
//    switch (item.getItemId()) {
//        case R.id.settings: {
//            Intent intent = new Intent(this, Preferences.class);
//            intent.putExtra(SettingsActivity.EXTRA_SHOW_FRAGMENT, SettingsFragment.class.getName());
//            intent.putExtra(SettingsActivity.EXTRA_NO_HEADERS, true);
//            intent.setClassName(this, "com.cjsheehan.fitness.settings.SettingsActivity");
//            startActivity(intent);
//            return true;
//        }
//    }
//
//    return super.onOptionsItemSelected(item);
//}
//
    private class settingsChangedListener implements SharedPreferences.OnSharedPreferenceChangeListener {

        @Override
        public void onSharedPreferenceChanged(SharedPreferences spref, String key) {
            //if (key.equals(getString(R.string.enableTestMode))) {
            //    updateDateColour();
            //    updateLabel();
            //}
        }
    }
//
//private void updateProgressView() {
//    try
//    {
//        //LinearLayout ll = (LinearLayout) findViewById(R.id.progress_linear);
//        //ll .setVisibility(View.VISIBLE);
//        ImageView progress_image = (ImageView) findViewById(R.id.progress_image);
//        _steps_today_tv = (TextView) findViewById(R.id.steps_today);
//        _steps_progress_bar = (ProgressBar) findViewById(R.id.steps_progress_bar);
//        _target_today_tv = (TextView) findViewById(R.id.target_today);
//
//        _goals_today_tv = (TextView ) findViewById(R.id.todays_challenge);
//
//        _steps_today_tv.setOnTouchListener(new View.OnTouchListener() {
//
//            @Override
//            public boolean onTouch(View view, MotionEvent arg1) {
//                editProgressDialog();
//                return true;
//            }
//        });
//
//        _steps_today_tv.setText("" + _stepsToday);
//        _target_today_tv.setText(_targetToday);
//
//    }
//    catch (Exception e)
//    {
//        int a = 5;
//    }
//}
//
//public void editProgressDialog() {
//
//    LinearLayout layout = new LinearLayout(MainActivity.this);
//    layout.setOrientation(LinearLayout.VERTICAL);
//
//    // Progress to Target
//    final EditText editTarget = new EditText(MainActivity.this);
//    editTarget.setText("");
//    editTarget.setRawInputType(Configuration.KEYBOARD_QWERTY);
//    layout.addView(editTarget);
//
//    // Cache
//    _editProgressTarget = editTarget;
//
//    // Alert
//    AlertDialog.Builder editGoalDialog = new AlertDialog.Builder(MainActivity.this);
//    editGoalDialog.setNegativeButton(android.R.string.cancel, null);
//
//    // Icon
//    editGoalDialog.setIcon(R.drawable.ic_icon_walk_green);
//
//    // Setting Dialog Title
//    editGoalDialog.setTitle("Add steps walked");
//
//    editGoalDialog.setView(layout);
//    editGoalDialog.create();
//
//    editGoalDialog.setPositiveButton("Add",
//            new DialogInterface.OnClickListener() {
//                public void onClick(DialogInterface dialog, int which) {
//
//                    //if (editGoal(position)) {
//                    //    Toast.makeText(getApplicationContext(), "Added steps to progress", Toast.LENGTH_SHORT).show();
//                    //}
//                }
//            });
//
//    // Showing Alert Message
//    editGoalDialog.show();
//}

    class SectionsPagerAdapter extends FragmentPagerAdapter implements
            ViewPager.OnPageChangeListener {

        final private static int PAGE_COUNT = 3;

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return BaseFragment.newInstance(BaseFragment.FragmentId.values()[position]);
        }

        @Override
        public int getCount() {
            return PAGE_COUNT;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return BaseFragment.FragmentId.values()[position].getTitle();
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {

        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }

}

